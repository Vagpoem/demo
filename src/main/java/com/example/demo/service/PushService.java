package com.example.demo.service;

import com.example.demo.bean.entity.BypassUser;
import com.example.demo.bean.entity.JobMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PushService {

    public boolean pushImgToClient(JobMessage jobMessage, List<BypassUser> list){
        boolean res = false;

        // 推送给每一个用户
        for (BypassUser bypassUser : list){
            bypassUser.recieveJob(jobMessage);
        }

        return res;
    }
}
