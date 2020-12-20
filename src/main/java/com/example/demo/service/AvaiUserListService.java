package com.example.demo.service;

import com.example.demo.bean.entity.User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AvaiUserListService {

    // 空闲用户列表
    private Set<User> availUserList = new HashSet<>();
    // 向空闲用户列表中加入空闲用户
    public synchronized boolean addUser(User user){
        boolean flag = false;
        if (!availUserList.contains(user)){
            availUserList.add(user);
        }
        return flag;
    }
    // 从空闲用户列表中删除用户
    public synchronized boolean delUser(User user){
        boolean flag = false;
        if (availUserList.contains(user)){
            flag = availUserList.remove(user);
        }
        return flag;
    }
    // 从空闲用户列表中获取一个分数最高的用户
    public synchronized User getGreatUser(){
        User user = null;
        double max = 0.0;
        for (User user1 : availUserList){
            if (user1.getMark()>max){
                user = user1;
            }
        }
        availUserList.remove(user);
        return user;
    }

}
