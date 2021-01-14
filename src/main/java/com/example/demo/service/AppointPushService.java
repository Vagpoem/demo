package com.example.demo.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.GlobalMap;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.bean.entity.JobMessage;
import com.example.demo.bean.entity.User;
import com.example.demo.bean.entity.UserInfo;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class AppointPushService {

    private Log log = LogFactory.get(AppointPushService.class);

    @Autowired
    UserMapper userMapper;
    @Autowired
    GlobalMap globalMap;
    @Autowired
    AvaiUserListService avaiUserListService;
    @Autowired
    GlobalVariable globalVariable;
    @Autowired
    PushService pushService;

    public boolean appointPush(JobMessage jobMessage){
        boolean res = false;

        List<User> availUserList = userMapper.selectHasPhoneUser();
        String id = availUserList.get(0).getUser_id()+"";
        int control = 0;
        boolean flag = false;
        while (!flag && control<globalVariable.getAvailuser_timeout()){
            flag = avaiUserListService.delUser((User)globalMap.getSessionFromUserid(id).getAttribute("user"), new UserInfo());
            control ++ ;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (flag){
            try {
                for (User user : availUserList){
                    globalMap.setJobidReceiver(jobMessage.getJob_id(), user);
                    log.info(jobMessage.getJob_id()+"globalMap的大小为："+globalMap.jobReceiver.size());
                }
                pushService.pushImgToClient(jobMessage, availUserList);
                res = true;
            } catch (IOException e) {
                e.printStackTrace();
//                globalMap.setJobidResult(jobMessage.getJob_id(), "系统出错");
            }
        }

        return res;
    }
}
