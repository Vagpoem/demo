package com.example.demo.controller;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.entity.User;
import com.example.demo.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class LoginController {

    private Log log = LogFactory.get(LoginController.class);

    @Autowired
    private LoginService loginService;

    @CrossOrigin
    @PostMapping("/login")
    public JSONObject login(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject params){

        // 1.将输入的信息生成一个登录的用户数据
        User loginUser = new User();
        loginUser.setUser_name(params.getString("username"));
        loginUser.setPassword(params.getString("password"));

        // 2.调用登录服务进行登录操作
        JSONObject res = loginService.login(request, response, loginUser);

        Cookie cookie = new Cookie("what","wfsdfds");
        response.addCookie(cookie);

        return res;
    }
}
