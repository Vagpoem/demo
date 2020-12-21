package com.example.demo.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.entity.User;
import com.example.demo.bean.GlobalMap;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class LogoutService {

    private Log log = LogFactory.get(LogoutService.class);

    @Autowired
    GlobalVariable globalVariable;

    @Autowired
    GlobalMap globalMap;

    public JSONObject logout(HttpServletRequest request, HttpServletResponse response, String user_id){
        JSONObject res = new JSONObject();
        String status = "500";
        String message = "退出失败";

        // 1.先获取会话信息
        HttpSession session = globalMap.getSessionFromUserid(user_id);
        log.info("全局的session映射为："+globalMap.userid_Session.size()+"  session信息为："+session);

        if (session==null){
            log.info("已退出！");
            status = "200";
            message = "用户已退出，请勿重复退出！";
        } else {
            // 2.再通过会话的属性获取用户信息，同时获取会话的sessionid信息
            String sessionid = session.getId();
            User user = (User) session.getAttribute("user");
            log.info("退出的用户为："+user);

            if (ObjectUtils.isEmpty(user)){
                log.info("已退出！");
                status = "200";
                message = "用户已退出，请勿重复退出！";
            } else {
                // 3.删除全局映射中的映射信息
                globalMap.delSessionFromUserid(user_id);
                globalMap.delSessionFromUsername(user.getUser_name());
                log.info("注销用户的登录信息！");

                // 4.将cookie信息全部置0
                Util.addCookie("sessionid", sessionid, response, 0);
                Util.addCookie("user_name", user.getUser_name(), response, 0);
                Util.addCookie("user_id", user.getUser_id()+"", response, 0);
                log.info("消除cookie的信息！");

                // 5.将session进行销毁
                session.invalidate();
                status = "200";
                message = "退出成功！";
            }
        }
        res.put("status", status);
        res.put("message", message);
        return res;
    }
}
