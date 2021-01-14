package com.example.demo.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example.demo.bean.GlobalMap;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.bean.entity.AI;
import com.example.demo.bean.entity.JobMessage;
import com.example.demo.bean.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.util.Util;
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

    public void schedule(JobMessage jobMessage) throws Exception{

        // 1.先确定分发的模式
        String AiOrPerson = distributeService.distribute(jobMessage.getJobType());
        System.out.println("schedule"+AiOrPerson);

        log.info("确定了任务分发的类型为："+ AiOrPerson);

        // 2.再确定分配的人员
        List<User> availUserList = null;
        int control = 0;

        // 3.循环的获取空闲的客户端并向其推送任务
        while (ObjectUtils.isEmpty(availUserList)&&control<globalVariable.getAvailuser_timeout()){

            // 分发给人工打码客户端
            if (AiOrPerson.equals("person")){
                // 进行多人和单人的分发判断
                if (Util.hasElement(globalVariable.getCaptcha_multi_push_list(),jobMessage.getJobType())){
                    availUserList = avaiUserListService.getGreatUsers();
                    log.info("ThreadService获取的多人列表为："+availUserList);
                } else {
                    availUserList = avaiUserListService.getGreatUser();
                    log.info("ThreadService获取的单人列表为："+availUserList);
                }
            }

            // 分发给AI打码端
            if (AiOrPerson.equals("ai")){
                String id = "-1";
                int index = -1;

                // 获取AI打码的类型索引用于获取AI打码的id
                for (String type : globalVariable.getAi_captcha_type_list()){
                    if (jobMessage.getJobType().equals(type)){
                        index = globalVariable.getAi_captcha_type_list().indexOf(type);
                    }
                }

                // 获取AI打码的信息
                id = globalVariable.getAi_captcha_id_list().get(index);
                if (id.equals("-1")){
                    // 如果获取失败则直接分配给人工
                    availUserList = avaiUserListService.getGreatUser();
                } else {
                    // 如果没有出错则将AI加入到列表中
                    User tempAIUser = userMapper.selectUser(Integer.parseInt(id));
                    availUserList = new ArrayList<>();
                    availUserList.add(tempAIUser);
                }
            }

            // 控制循环的次数
            control++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 3.将任务和接收者做映射
        for (User user : availUserList){
            globalMap.setJobidReceiver(jobMessage.getJob_id(), user);
            log.info(jobMessage.getJob_id()+"globalMap的大小为："+globalMap.jobReceiver.size());
        }

        log.info("开始推送服务！");

        // 4.推送任务
        try {
            pushService.pushImgToClient(jobMessage, availUserList);
        } catch (Exception e) {
            // 如果推送出错识别接口就不会获取到相应的结果直接得到系统出错的信号
            log.error("推送任务出错。。。");
        }

        log.info("推送成功！");
    }
}