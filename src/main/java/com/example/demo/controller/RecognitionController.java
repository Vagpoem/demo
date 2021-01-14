package com.example.demo.controller;


import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.GlobalMap;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.bean.Producer01;
import com.example.demo.bean.entity.Job;
import com.example.demo.bean.entity.JobMessage;
import com.example.demo.bean.entity.Result;
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
        String classMessage = "7", status = "500", message = "识别失败！",
                bypass_result = "识别失败", client = " ", job_id = "-1", temp = "";
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
                // 如果发送出错
                message += "消息队列出错!";
                log.info("消息入队出错！！！");
            } else {
                // 如果发送成功
                log.info("消息入队成功！！！");
                int contrl1 = 0, control2 = 0;

                // 6.查询接受任务的客户端信息
                List<User> list = null;
                while (ObjectUtils.isEmpty(list)&&contrl1<globalVariable.getAvailuser_timeout()){
                    list = globalMap.getJobidReceiver(tempJobId);
                    //log.info(tempJobId+"   "+globalMap.jobReceiver.size());
                    //log.info("获取接收者中......");
                    contrl1++;
                    Thread.sleep(1000);
                }

                // 7.通过判断任务是否被接受来获取任务的结果
                if (ObjectUtils.isEmpty(list)){
                    message += "短期内没有空闲打码客户端";
                } else {
                    log.info("接收者的列表大小为：" + list.size());
                    log.info("任务已被接受！");

                    // 为任务对象设置接受时间
                    newJob.setReceive_time(new Timestamp(System.currentTimeMillis()));
                    // TODO:为任务设置接受的客户端
                    for (User user : list) {
                        client += user.getUser_name() + " ";
                    }
                    client = client.trim();

                    // 8.开始等待任务的结果，无论结果列表中是否含有结果都要等待60s
                    List<Result> tempRes = new ArrayList<>();
                    log.warn("中间结果列表的大小为："+tempRes.size());
                    log.warn("打码用户列表的大小为："+list.size());
                    while (tempRes.size()<list.size()&&control2<globalVariable.getTask_timeout()){
                        if (!ObjectUtils.isEmpty(getJobResService.getRes(tempJobId))){
                            tempRes = getJobResService.getRes(tempJobId);
                        }
                        log.info("获取打码结果中...");
                        control2++;
                        Thread.sleep(1000);
                    }

                    if (ObjectUtils.isEmpty(tempRes)){
                        message += "打码端超时！";
                    } else {
                        log.info("任务结果返回成功！");
                        // 9.结果投票获取最终的打码结果
                        bypass_result = voteService.voteResult(tempRes, classMessage);
                        newJob.setFinish_time(new Timestamp(System.currentTimeMillis()));
                        newJob.setCaptcha_result(bypass_result);

                        if (!Util.hasElement(globalVariable.getBypass_failed_result_list(), bypass_result)){
                            status = "200";
                            message = "识别成功";
                        }
                    }

                    // 10.将在全局中的任务缓存列表全部删除
                    globalMap.delJobidReceiver(tempJobId);
                    globalMap.delJobidResult(tempJobId);
                }
                // TODO:换人分发？
            }
        }

        // 11.对任务数据进行持久化
        try {
            newJob.setJob_id(null);
            jobMapper.addJob(newJob);
            job_id = newJob.getJob_id();
            log.info("job表插入成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.error("job表插入出错！");
        }
        // 12.对验证码类型进行中文转换
        for (int i = 0;i<globalVariable.getCaptcha_type_list().size();i++){
            if (classMessage.equals(globalVariable.getCaptcha_type_list().get(i))){
                classMessage = globalVariable.getCaptcha_type_desc_list().get(i);
            }
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
