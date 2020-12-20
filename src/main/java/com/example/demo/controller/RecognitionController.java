package com.example.demo.controller;


import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.bean.Producer01;
import com.example.demo.bean.entity.Job;
import com.example.demo.bean.entity.JobMessage;
import com.example.demo.mapper.JobMapper;
import com.example.demo.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
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

    @PostMapping("/recognition")
    public JSONObject recognition(@RequestBody JSONObject params, @RequestHeader("token") String token) throws InterruptedException{

        JSONObject res = new JSONObject();
        String classMessage = "未知", status = "500", message = "识别失败！",
                bypass_result = "识别失败", client = "-1", job_id = "-1";

        // 1.进行身份和数据合法性的验证
        if (!verificationService.verify(params, token)){
            message += "非法请求者或非法请求数据！";
        } else {
            // 2.生成局部Job数据对象（并先为其设置请求者id、请求时间以及任务状态 “0”代表还未开始）
            Job newJob = new Job();
            String tempJobId = UUID.randomUUID().toString();
            newJob.setRequester_id(params.getString("user_id"));
            newJob.setReceive_time(new Timestamp(System.currentTimeMillis()));
            newJob.setJob_status("0");

            // 3.将传入的验证码数据保存为图片
            // TODO:数据保存出错不需要处理？继续打码任务？
            if (!imgSaveService.save(params, globalVariable.getPhotoSave_path())){
                message += "数据保存出错!";
            }

            // 4.将保存的图片进行分类
            classMessage = classifyService.classify(globalVariable.getPhotoSave_path());
            newJob.setSubtype_id(classMessage);

            // 5.发送到消息队列中
            JobMessage jobMessage = new JobMessage(tempJobId, params.getString("src_type"), params.getString("data"), classMessage);
            if (!producer01.produce(jobMessage)){
                message += "消息队列出错!";
            } else {
                int contrl1 = 0, control2 = 0;
                // 6.是否接受任务

                // 7.开始等待任务的结果
                List<String> tempRes = getJobResService.getRes(tempJobId);

                // 8.结果投票
                bypass_result = voteService.voteResult(tempRes);

                // TODO:9.换人分发？

            }
        }
        res.put("status", status);
        res.put("message", message);
        res.put("class", classMessage);
        res.put("bypass_result", bypass_result);
        res.put("client", client);
        res.put("job_id", job_id);
        return new JSONObject();
    }
}
