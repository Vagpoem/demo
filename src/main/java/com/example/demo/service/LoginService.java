package com.example.demo.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.entity.User;
import com.example.demo.bean.entity.UserInfo;
import com.example.demo.controller.listener.WebSocketServer;
import com.example.demo.mapper.UserMapper;
import com.example.demo.bean.GlobalMap;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Service
public class LoginService {

    private Log log = LogFactory.get(LoginService.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    GlobalVariable globalVariable;

    @Autowired
    GlobalMap globalMap;

    @Autowired
    LogoutService logoutService;

    public JSONObject login(HttpServletRequest request, HttpServletResponse response, User user){

        log.info("用户："+user.getUser_name()+" 正在尝试登录......");

        // 声明登录接口要返回的数据
        JSONObject res = new JSONObject();
        int status = 500;
        String message = "登录失败！";

        // TODO:redis缓存暂时登录的某些用户以便近期登录时快速登录
        // 1.从数据库中查找登录用户的信息存放在一个User类型的对象中
        User judgeUser = userMapper.selectUserByName(user.getUser_name());

        // 2.判断浏览器是已经有账号登录
        Cookie[] cookies = request.getCookies();

        if (Util.hasCookie("user_name", cookies)&&
                !ObjectUtils.isEmpty(globalMap.getSessionFromUsername(Util.getCookieValue("user_name", cookies)))){
            message += "该浏览器已有用户登录，请换一个浏览器登录";
        } else {
            if (ObjectUtils.isEmpty(judgeUser)) {
                // 3.如果数据库中没有该条信息则返回错误信息
                message += "无用户信息！";
            } else {
                // 4.查看该用户名对应的用户是否已经登录
                if (!ObjectUtils.isEmpty(globalMap.getSessionFromUsername(user.getUser_name()))){
                    HttpSession oldSession = globalMap.getSessionFromUsername(user.getUser_name());
                    WebSocketServer webSocketServer = WebSocketServer.getWebSocket(judgeUser.getUser_id()+"");
                    if (ObjectUtils.isEmpty(webSocketServer) || webSocketServer.getJobId().equals("")){
                        if (judgeUser.getPassword().equals(user.getPassword())){
                            logoutService.logout(request, response, judgeUser.getUser_id()+"");
                            // 4.如果密码比对成功则进行登录状态维护
                            // 将用户的准确率、速度、得分、异常率、疲劳度的信息存入到session的属性信息中
                            log.info("开始登录......");
                            HttpSession session = request.getSession();
                            session.setMaxInactiveInterval(globalVariable.getSession_age());
                            String sessionid = session.getId();
                            session.setAttribute("user", judgeUser);
                            UserInfo userInfo = new UserInfo();
                            session.setAttribute("userinfo", userInfo);
                            log.info("session信息设置完毕......");
                            Util.addCookie("sessionid", sessionid, response, globalVariable.getCookie_age());
                            Util.addCookie("user_name", user.getUser_name(), response, globalVariable.getCookie_age());
                            Util.addCookie("user_id", user.getUser_id()+"", response, globalVariable.getCookie_age());
                            Util.addCookie("token", user.getToken(), response, globalVariable.getCookie_age());
                            log.info("cookie信息设置完毕......");
                            globalMap.setUsernameSession(user.getUser_name(), session);
                            globalMap.setUseridSession(judgeUser.getUser_id()+"", session);
                            log.info("全局映射设置完毕......");
                            log.info("usernameSession的大小为："+globalMap.username_Session.size()+
                                    "  useridSession的大小为："+globalMap.userid_Session.size());

                            // 修改返回信息中的状态码和状态信息
                            res.put("data", judgeUser);
                            status = 200;
                            message = "登录成功！";
                        } else {
                            message += "密码错误！";
                        }
                    } else {
                        message += "用户已登录且在执行任务，请勿重复登录！";
                    }
                } else {
                    // 5.如果有该条用户的信息则比对密码信息
                    if (judgeUser.getPassword().equals(user.getPassword())){
                        // 4.如果密码比对成功则进行登录状态维护
                        // TODO:将用户的准确率、速度、得分、异常率、疲劳度的信息存入到session的属性信息中
                        log.info("开始登录......");
                        HttpSession session = request.getSession();
                        session.setMaxInactiveInterval(globalVariable.getSession_age());
                        String sessionid = session.getId();
                        session.setAttribute("user", judgeUser);
                        UserInfo userInfo = new UserInfo();
                        session.setAttribute("userinfo", userInfo);
                        log.info("session信息设置完毕......");
                        Util.addCookie("sessionid", sessionid, response, globalVariable.getCookie_age());
                        Util.addCookie("user_name", user.getUser_name(), response, globalVariable.getCookie_age());
                        Util.addCookie("user_id", user.getUser_id()+"", response, globalVariable.getCookie_age());
                        Util.addCookie("token", user.getToken(), response, globalVariable.getCookie_age());
                        log.info("cookie信息设置完毕......");
                        globalMap.setUsernameSession(user.getUser_name(), session);
                        globalMap.setUseridSession(judgeUser.getUser_id()+"", session);
                        log.info("全局映射设置完毕......");
                        log.info("usernameSession的大小为："+globalMap.username_Session.size()+
                                "  useridSession的大小为："+globalMap.userid_Session.size());

                        // 修改返回信息中的状态码和状态信息
                        res.put("data", judgeUser);
                        status = 200;
                        message = "登录成功！";
                    } else {
                        message += "密码错误！";
                    }
                }
            }
        }
        res.put("status", status);
        res.put("message", message);
        return res;
    }
}
