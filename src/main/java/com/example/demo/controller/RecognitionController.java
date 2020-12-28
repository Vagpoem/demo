package com.example.demo.controller;


import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.GlobalMap;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.bean.Producer01;
import com.example.demo.bean.entity.Job;
import com.example.demo.bean.entity.JobMessage;
import com.example.demo.bean.entity.User;
import com.example.demo.mapper.JobMapper;
import com.example.demo.service.*;
import com.example.demo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class RecognitionController {

    private Log log = LogFactory.get(RecognitionController.class);

    @Autowired
    GlobalVariable globalVariable;
    @Autowired
    VerificationService verificationService;
    @Autowired
    ImgSaveService imgSaveService;
    @Autowired
    ClassifyService classifyService;
    @Autowired
    Producer01 producer01;
    @Autowired
    GetJobResService getJobResService;
    @Autowired
    VoteService voteService;
    @Autowired
    JobMapper jobMapper;
    @Autowired
    GlobalMap globalMap;

    @PostMapping("/recognition")
    public JSONObject recognition(@RequestBody JSONObject params, @RequestHeader("token") String token) throws InterruptedException{

        JSONObject res = new JSONObject();
        String classMessage = "未知", status = "500", message = "识别失败！",
                bypass_result = "识别失败", client = "-1", job_id = "-1", temp = "";
        Job newJob = null;

        // 1.进行身份和数据合法性的验证
        if (!verificationService.verify(params, token)){
            message += "非法请求者或非法请求数据！";
        } else {
            // 2.生成局部Job数据对象（并先为其设置请求者id、请求时间以及任务状态 “0”代表还未开始）
            newJob = new Job();
            String tempJobId = UUID.randomUUID().toString();
            newJob.setRequester_id(params.getString("user_id"));
            newJob.setReceive_time(new Timestamp(System.currentTimeMillis()));
            newJob.setJob_status("0");
            newJob.setJob_name(" ");

            log.info("图片保存路径为："+globalVariable.getPhotoSave_path());

            // 3.将传入的验证码数据保存为图片
            // TODO:数据保存出错不需要处理？继续打码任务？
            if (!imgSaveService.save(params, globalVariable.getPhotoSave_path() + tempJobId + ".png")){
                newJob.setCaptcha_src("null");
                log.error("图片保存失败！");
            } else {
                log.info("图片已成功保存！");
                // 4.将保存的图片进行分类
                newJob.setCaptcha_src(globalVariable.getPhotoSave_path() + tempJobId + ".png");
                classMessage = classifyService.classify(params.getString("src_type"),
                        globalVariable.getPhotoSave_path() + tempJobId + ".png");
                log.info("图片分类成功！类别为："+classMessage);
                newJob.setSubtype_id(classMessage);
            }

            log.info("验证码的分类结果为："+classMessage);

            // 5.发送到消息队列中
            JobMessage jobMessage = new JobMessage(tempJobId, params.getString("src_type"), params.getString("data"), classMessage);
            jobMessage.setPath(globalVariable.getPhotoSave_path()+tempJobId+".png");
            if (!producer01.produce(jobMessage)){
                message += "消息队列出错!";
                log.info("消息入队出错！！！");
            } else {
                log.info("消息入队成功！！！");
                int contrl1 = 0, control2 = 0;
                // 6.是否接受任务
                List<User> list = null;
                while (ObjectUtils.isEmpty(list)&&contrl1<globalVariable.getAvailuser_timeout()){
                    list = globalMap.getJobidReceiver(tempJobId);
                    log.info(tempJobId+"   "+globalMap.jobReceiver.size());
                    log.info("获取接收者中......");
                    contrl1++;
                    Thread.sleep(1000);
                }

                if (ObjectUtils.isEmpty(list)){
                    message += "短期内没有空闲打码客户端";
                } else {
                    log.info("接收者的列表大小为：" + list.size());
                    log.info("任务已被接受！");
                    client = "";
                    newJob.setReceive_time(new Timestamp(System.currentTimeMillis()));
                    for (User user : list){
                        newJob.setRequester_id(user.getUser_id()+"");
                        client += user.getUser_name() + " ";
                    }
                    // 7.开始等待任务的结果
                    List<String> tempRes = new ArrayList<>();

                    log.info("jieguoduixiangshifouweikong:"+ObjectUtils.isEmpty(tempRes)+" "+tempRes.size());

                    while (ObjectUtils.isEmpty(tempRes)&&control2<globalVariable.getTask_timeout()){
                        tempRes = getJobResService.getRes(tempJobId);

                        log.info("获取打码结果中...");
                        control2++;
                        Thread.sleep(1000);
                    }

                    if (ObjectUtils.isEmpty(tempRes)){
                        message += "打码端超时！";
                    } else {
                        log.info("任务结果返回成功！");
                        // 8.结果投票
                        bypass_result = voteService.voteResult(tempRes);
                        newJob.setFinish_time(new Timestamp(System.currentTimeMillis()));
                        newJob.setCaptcha_result(bypass_result);

                        if (!Util.hasElement(globalVariable.getBypass_failed_result_list(), bypass_result)){

                            status = "200";
                            message = "识别成功";
                        }
                    }

                    globalMap.delJobidReceiver(tempJobId);
                    globalMap.delJobidResult(tempJobId);
                }
                // TODO:9.换人分发？
            }
        }

        try {
            newJob.setJob_id(null);
            jobMapper.addJob(newJob);
            job_id = newJob.getJob_id();
            log.info("job表插入成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("job表插入出错！");
        }


        res.put("status", status);
        res.put("message", message);
        res.put("class", classMessage);
        res.put("bypass_result", bypass_result);
        res.put("client", client);
        res.put("job_id", job_id);
        log.info("fanhuidejieguowei:"+res.toString());
        return res;
    }
}
