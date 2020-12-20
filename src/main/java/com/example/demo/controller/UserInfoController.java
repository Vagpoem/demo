package com.example.demo.controller;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example.demo.bean.entity.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public class UserInfoController {

    private Log log = LogFactory.get(UserInfoController.class);

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/users/{id}")
    public User getUserInfo(@PathVariable Integer id){

        // 会话状态维护
        // boolean flag = MySessionMap.sessionMaintain(id+"");

        return userMapper.selectUser(id);
    }

    @GetMapping("/usertoken/{id}")
    public String getToken(@PathVariable Integer id){

        // 获取用户的token信息
        return userMapper.selectToken(id+"");

    }

}
