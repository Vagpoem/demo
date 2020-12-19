package com.example.demo.controller;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.entity.User;
import com.example.demo.service.RegisterService;
import com.example.demo.service.UserRedisService;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.bean.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class RegisterController {

    private Log log = LogFactory.get(RegisterController.class);

    @Autowired
    private RegisterService registerService;

    @Autowired
    private UserRedisService userRedisService;

    @Autowired
    private GlobalVariable globalVariable;

    // 注册接口
    @PostMapping("/register")
    public JSONObject register(HttpServletRequest request, HttpServletResponse response, @RequestBody JSONObject formData){

        // 1.利用传入的表单数据生成新的用户对象
        String user_name = formData.getString("user_name");
        User newUser = new User(user_name, formData.getString("password"), formData.getString("mail"),
                "正常", TokenUtil.sign(user_name), 0.0);
        newUser.setPhone(formData.getString("phone"));
        switch (Integer.parseInt(formData.getString("role_id"))){
            case 0:
                newUser.setRole("管理员");
                break;
            case 1:
                newUser.setRole("打码请求者");
                break;
            case 2:
                newUser.setRole("人工打码客户端");
                break;
            default: newUser.setRole("error in role setting");
        }

        // 2.调用注册服务的注册方法返回相应的信息
        JSONObject res = registerService.register(request, response, newUser);

        return res;
    }
}
