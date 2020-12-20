package com.example.demo.service;

import com.example.demo.bean.GlobalMap;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.bean.entity.JobMessage;
import com.example.demo.bean.entity.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ThreadService {

    @Autowired
    DistributeService distributeService;
    @Autowired
    AvaiUserListService avaiUserListService;
    @Autowired
    GlobalVariable globalVariable;
    @Autowired
    UserMapper userMapper;
    @Autowired
    PushService pushService;
    @Autowired
    GlobalMap globalMap;

    public void schedule(JobMessage jobMessage){

        // 1.先确定分发的模式
        String AiOrPerson = distributeService.distribute(jobMessage.getJobType());

        // 2.再确定分配的人员
        User avaiUser = null;
        int control = 0;
        while (ObjectUtils.isEmpty(avaiUser)&&control<globalVariable.getAvailuser_timeout()){
            if (AiOrPerson.equals("person")){
                avaiUser = avaiUserListService.getGreatUser();
            } else if (AiOrPerson.equals("ai")){
                String id = "-1";
                int index = -1;
                for (String type : globalVariable.getAi_captcha_type_list()){
                    if (jobMessage.getJobType().equals(type)){
                        index = globalVariable.getAi_captcha_type_list().indexOf(type);
                    }
                }
                id = globalVariable.getAi_captcha_id_list().get(index);
                if (id.equals("-1")){
                    avaiUser = avaiUserListService.getGreatUser();
                } else {
                    avaiUser = userMapper.selectUser(Integer.parseInt(id));
                }
            }
            control++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        List<User> list = new ArrayList<>();
        list.add(avaiUser);

        // 3.推送任务
        try {
            if (!pushService.pushImgToClient(jobMessage, list)){
                globalMap.setJobidResult(jobMessage.getJob_id(), "系统出错");
            }
        } catch (Exception e) {
            globalMap.setJobidResult(jobMessage.getJob_id(), "系统出错");
        }
    }
}