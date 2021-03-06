package com.example.demo.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.GlobalMap;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.bean.HttpRequest;
import com.example.demo.bean.entity.JobMessage;
import com.example.demo.bean.entity.Result;
import com.example.demo.bean.entity.User;
import com.example.demo.controller.listener.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

@Service
public class PushService {

    private Log log = LogFactory.get(PushService.class);

    @Autowired
    HttpRequest httpRequest;
    @Autowired
    GlobalMap globalMap;
    @Autowired
    GlobalVariable globalVariable;

    public boolean pushImgToClient(JobMessage jobMessage, List<User> list) throws IOException {
        boolean res = false;

        // 推送给每一个用户
        for (User user : list){
            if (user.getRole().equals("人工打码客户端")){
                // 1.构造向客户端推送的对象
                JSONObject msg = new JSONObject();
                msg.put("src_type", jobMessage.getType());
                msg.put("show_data", jobMessage.getData());
                msg.put("cate_code", jobMessage.getJobType());

                // 2.通过websocket推送个人工打码客户端
                WebSocketServer.sendInfo(msg, user.getUser_id()+"", jobMessage.getJob_id());

                log.info("推送给人工打码客户端！");

                res = true;
            }
            if (user.getRole().equals("第三方打码平台")){
                // 1.构造数据对象
                // 2.制造post请求
                res = false;
            }
            if (user.getRole().equals("AI打码客户端")){

                log.info("推送给AI打码客户端！");

                // 1.构造数据对象
                JSONObject object = new JSONObject();
                object.put("path", jobMessage.getPath());
                System.out.println("PushService:"+jobMessage.getPath());

                // 2.发送post对象
                JSONObject isAI = httpRequest.getRes(globalVariable.getCharactor_recognition_url(), object);

                // 3.返回结果
                if (!ObjectUtils.isEmpty(isAI)){
                    if (isAI.getString("status").equals("200")){
                        Result tempResult = new Result(user.getUser_id()+"", isAI.getString("value"));
                        tempResult.setFlag(1);
                        tempResult.setOverTime(new Timestamp(System.currentTimeMillis()+4000));
                        globalMap.setJobidResult(jobMessage.getJob_id(), tempResult);
                        log.info(jobMessage.getJob_id()+"   AI识别的结果为："+isAI.getString("value"));
                        log.info("任务结果和id映射完成："+globalMap.jobResult.size());
                    }else {
                        Result re1 = new Result(user.getUser_id()+"", "系统出错");
                        re1.setOverTime(new Timestamp(System.currentTimeMillis()));
                        globalMap.setJobidResult(jobMessage.getJob_id(), re1);
                    }
                }else {
                    Result re2 = new Result(user.getUser_id()+"", "系统出错");
                    re2.setOverTime(new Timestamp(System.currentTimeMillis()));
                    globalMap.setJobidResult(jobMessage.getJob_id(), re2);
                }

                res = true;
            }
        }

        return res;
    }
}
