package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class HeartBeatController {

    // 查看系统运行的状态
    @CrossOrigin
    @GetMapping("/heartbeat")
    public JSONObject heartbeat(HttpServletResponse response){
        JSONObject res = new JSONObject();
        res.put("code", 200);
        res.put("message", "系统运行正常！");
        res.put("data", "");

        return res;
    }

}
