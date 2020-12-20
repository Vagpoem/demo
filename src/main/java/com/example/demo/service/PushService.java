package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.GlobalMap;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.bean.HttpRequest;
import com.example.demo.bean.entity.BypassUser;
import com.example.demo.bean.entity.JobMessage;
import com.example.demo.bean.entity.User;
import com.example.demo.controller.listener.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.List;

@Service
public class PushService {

    @Autowired
    HttpRequest httpRequest;
    @Autowired
    GlobalMap globalMap;

    public boolean pushImgToClient(JobMessage jobMessage, List<User> list) throws IOException {
        boolean res = false;

        // 推送给每一个用户
        for (User user : list){
            if (user.getRole().equals("人工打码客户端")){
                // 1.构造向客户端推送的对象
                JSONObject msg = new JSONObject();
                msg.put("src_type", jobMessage.getType());
                msg.put("show_data", jobMessage.getData());
                msg.put("cate_code", jobMessage.getType());

                // 2.通过websocket推送个人工打码客户端
                WebSocketServer.sendInfo(msg, user.getUser_id()+"", jobMessage.getJob_id());

                res = true;
            }
            if (user.getRole().equals("第三方打码平台")){
                // 1.构造数据对象
                // 2.制造post请求
                res = false;
            }
            if (user.getRole().equals("AI打码客户端")){
                // 1.构造数据对象
                JSONObject object = new JSONObject();
                object.put("path", jobMessage.getPath());

                // 2.发送post对象
                JSONObject isAI = httpRequest.getRes(user.getMail(), object);

                // 3.返回结果
                if (!ObjectUtils.isEmpty(isAI)){
                    if (isAI.getString("status").equals("200")){
                        globalMap.setJobidResult(jobMessage.getJob_id(), isAI.getString("value"));
                    }else {
                        globalMap.setJobidResult(jobMessage.getJob_id(), "系统出错");
                    }
                }else {
                    globalMap.setJobidResult(jobMessage.getJob_id(), "系统出错");
                }

                res = true;
            }
        }

        return res;
    }
}
