package com.example.demo.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.entity.User;
import com.example.demo.mapper.UserMapper;
import com.example.demo.util.GlobalMap;
import com.example.demo.util.GlobalVariable;
import com.example.demo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class RegisterService {

    private static Log log = LogFactory.get(RegisterService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GlobalVariable globalVariable;

    @Autowired
    GlobalMap globalMap;

    /**
     * 注册方法
     * @param user
     * @return
     */
    public JSONObject register(HttpServletRequest request, HttpServletResponse response, User user){

        // 请求接口反馈给请求端的信息
        JSONObject res = new JSONObject();
        String message = "注册失败！";
        int status = 500;

        // 1.查看用户名和手机号是否已被占用
        User exitUser = userMapper.selectUserByName(user.getUser_name());
        User phoneUser = userMapper.selectUserByPhone(user.getPhone());

        if (!ObjectUtils.isEmpty(phoneUser) || !ObjectUtils.isEmpty(exitUser)){
            // :如果用户名或手机号已被注册
            message += "手机号或用户名已被注册！";
            user.setUser_id(-1);
        } else {
            // :如果没有被注册
            // 2.将新用户插入数据库
            try {
                userMapper.addUser(user);
                log.info("用户："+user.getUser_name()+" 向数据库中插入成功！其id为："+user.getUser_id()+" 。");
                status = 200;
                message = "注册成功！";
            } catch (Exception e) {
                // :如果插入失败则则直接返回错误信息
                user.setUser_id(-1);
                log.info("用户："+user.getUser_name()+" 注册失败！向数据库插入数据失败！");
                message += "系统出错，请重新注册！";
                res.put("data", user);
                res.put("status", status);
                res.put("message", message);
                return res;
            }

            // 3.进行登录状态的维护：
            //  3.1将用户的信息作为一个session对象存在session中；
            //  3.2将用户的sessionid以及user_name传递给浏览器端的cookie；
            //  3.3将用户名和session做一个映射。
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(globalVariable.getSession_age());
            String sessionid = session.getId();
            session.setAttribute("user", user);
            Util.addCookie("sessionid", sessionid, response, globalVariable.getCookie_age());
            Util.addCookie("user_name", user.getUser_name(), response, globalVariable.getCookie_age());
            globalMap.setUsernameSession(user.getUser_name(), session);

            // TODO:4.将用户的信息放入到redis中以备下次登录使用？
        }
        // 返回信息
        res.put("data", user);
        res.put("status", status);
        res.put("message", message);

        return res;
    }
}