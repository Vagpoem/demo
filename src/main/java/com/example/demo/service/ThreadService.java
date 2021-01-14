package com.example.demo.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example.demo.bean.GlobalMap;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.bean.entity.AI;
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

    private Log log = LogFactory.get(ThreadService.class);

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
        System.out.println("schedule"+AiOrPerson);

        log.info("确定了任务分发的类型为："+ AiOrPerson);

        // 2.再确定分配的人员
        User avaiUser = null;
        int control = 0;
        while (ObjectUtils.isEmpty(avaiUser)&&control<globalVariable.getAvailuser_timeout()){
            if (AiOrPerson.equals("person")){
                avaiUser = avaiUserListService.getGreatUser();
                log.info("分配给人工："+avaiUser);
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
                log.info("分配给AI："+avaiUser);
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

        // 3.将任务和接收者做映射
        for (User user : list){

            globalMap.setJobidReceiver(jobMessage.getJob_id(), user);
            log.info(jobMessage.getJob_id()+"globalMap的大小为："+globalMap.jobReceiver.size());
        }

        log.info("开始推送服务！");

        // 4.推送任务
        try {
            if (!pushService.pushImgToClient(jobMessage, list)){
                globalMap.setJobidResult(jobMessage.getJob_id(), "系统出错");
                log.info("推送出错！");
            }
        } catch (Exception e) {
            globalMap.setJobidResult(jobMessage.getJob_id(), "系统出错");
        }

        log.info("推送成功！");
    }
}