package com.example.demo.bean;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example.demo.bean.entity.JobMessage;
import com.example.demo.service.AppointPushService;
import com.example.demo.service.ThreadService;
import com.example.demo.util.Util;
import com.google.gson.Gson;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息队列的消费者；
 * 会寻找空闲用户，
 * 并将相应的打码任务推送给相应的客户端。
 */
@Component
public class Consumer01 {

    private static Log log = LogFactory.get(Consumer01.class);

    @Autowired
    private ThreadService threadService;
    @Autowired
    GlobalMap globalMap;
    @Autowired
    GlobalVariable globalVariable;
    @Autowired
    AppointPushService appointPushService;

    /**
     * 消费者消费消息的方法
     * @param message 从队列中接收到的消息对象
     */
    @RabbitHandler
    @RabbitListener(queues = "jobMessage_queue_01")
    public void jobMessageProcess01(Message message){

        // 1.将获取的message对象转化为byte数组，再将byte数组转化为字符串
        byte[] msgByte = message.getBody();
        String strMsg = new String(msgByte);

        // 2.将序列化的字符串转化为所需要的对象
        JobMessage jobMessage = new Gson().fromJson(strMsg, JobMessage.class);
        log.info("消息"+jobMessage.getJob_id()+"出队");
        System.out.println("xiaoxidetypewei:"+jobMessage.getJobType());

        // 3.利用多线程的服务进行任务调度
        try {
            // 判断任务是自动分配还是指定分配类型
            if (Util.hasElement(globalVariable.getAuto_src_type_list(), jobMessage.getType())) {
                // 自动分配类型
                threadService.schedule(jobMessage);
                log.info("任务自动分配成功。。。");
            } else if (Util.hasElement(globalVariable.getAppoint_src_type_list(), jobMessage.getType())) {
                // 指定分配类型
                appointPushService.appointPush(jobMessage);
                log.info("任务指定分配成功。。。");
            }
        } catch (Exception e) {
            // 如果任务调度出错，在识别接口处就不会获取到接受人物的客户端信息
            e.printStackTrace();
            log.error("任务调度出错！");
        }

    }
}
