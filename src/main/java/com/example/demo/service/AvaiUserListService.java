package com.example.demo.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.bean.entity.User;
import com.example.demo.bean.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AvaiUserListService {

    private Log log = LogFactory.get(AvaiUserListService.class);

    @Autowired
    GlobalVariable globalVariable;

    // 空闲用户列表
    private Set<User> availUserList = new HashSet<>();
    // 空闲用户列表中的传入topsis算法中的信息
    private Set<UserInfo> availUserInfoList = new HashSet<>();
    // 向空闲用户列表中加入空闲用户
    public synchronized boolean addUser(User user, UserInfo userInfo){
        boolean flag = false;
        if (!availUserList.contains(user)){
            availUserList.add(user);
            log.info("已向空闲用户列表中加入用户："+user);
        }
        return flag;
    }
    // 从空闲用户列表中删除用户
    public synchronized boolean delUser(User user, UserInfo userInfo){
        boolean flag = false;
        if (availUserList.contains(user)){
            flag = availUserList.remove(user);
            log.info("已从空闲用户列表中删除用户："+user);
        }
        return flag;
    }
    // 从空闲用户列表中获取一个分数最高的用户
    public synchronized List<User> getGreatUser(){
        List<User> users = new ArrayList<>();
        User user = null;
        double max = 0.0;
        for (User user1 : availUserList){
            if (user1.getMark()>max){
                user = user1;
                log.info("已从空闲用户列表中获取一个可用用户："+user);
            }
        }
        availUserList.remove(user);
        users.add(user);
        return users;
    }
    // 从空闲用户列表中获取多个用户
    public synchronized List<User> getGreatUsers(){
        List<User> users = new ArrayList<>();
        if (availUserList.size()>=globalVariable.getCaptcha_multi_push_number()){
            int tempcontrol1 = 0;
            for (User user : availUserList){
                users.add(user);
                tempcontrol1++;
                if (tempcontrol1>=globalVariable.getCaptcha_multi_push_number()){
                    break;
                }
            }
            for (User user : users){
                availUserList.remove(user);
            }
        } else {
            User tempUser = null;
            double max = 0.0;
            for (User user1 : availUserList){
                if (user1.getMark()>max){
                    tempUser = user1;
                    log.info("已从空闲用户列表中获取一个可用用户："+tempUser);
                }
            }
            availUserList.remove(tempUser);
            users.add(tempUser);
        }
        return users;
    }
}
