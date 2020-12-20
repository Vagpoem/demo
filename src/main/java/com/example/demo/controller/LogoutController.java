package com.example.demo.controller;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.service.LogoutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LogoutController {

    private Log log = LogFactory.get(LoginController.class);

    @Autowired
    private UserRedisService userRedisService;

    @Autowired
    private LogoutService logoutService;

    @GetMapping("/logout")
    public JSONObject logout(HttpServletRequest request, HttpServletResponse response, @RequestParam Integer user_id){

        // 调用登出服务进行用户状态的注销
        JSONObject res = logoutService.logout(request, response, user_id+"");

        return res;
    }
}
