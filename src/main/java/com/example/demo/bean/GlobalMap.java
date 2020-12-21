package com.example.demo.bean;

import com.example.demo.bean.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GlobalMap {
    // 用户名和会话之间的映射表
    private Map<String, HttpSession> username_Session = new HashMap<>();
    // 向映射表中添加用户名和会话的映射项
    public boolean setUsernameSession(String name, HttpSession session){
        boolean res = false;
        if (name!=null && !ObjectUtils.isEmpty(session)) {
            username_Session.put(name, session);
            res = true;
        }
        return res;
    }
    // 从用户名和会话的映射表中获取会话
    public HttpSession getSessionFromUsername(String name){
        HttpSession session = null;
        if (name!=null){
            session = username_Session.get(name);
        }
        return session;
    }
    // 从用户名和会话的映射表中删除元素
    public void delSessionFromUsername(String name){
        if (name!=null){
            username_Session.remove(name);
        }
    }


    // 用户id和会话之间的映射表
    private Map<String, HttpSession> userid_Session = new HashMap<>();
    // 向用户id和会话的映射表中添加元素
    public boolean setUseridSession(String id, HttpSession session){
        boolean res = false;
        if (id!=null && !ObjectUtils.isEmpty(session)) {
            userid_Session.put(id, session);
            res = true;
        }
        return res;
    }
    // 从用户id和会话的映射表中获取元素
    public HttpSession getSessionFromUserid(String id){
        HttpSession session = null;
        if (id!=null){
            session = userid_Session.get(id);
        }
        return session;
    }
    // 从用户id和会话的映射表中删除元素
    public void delSessionFromUserid(String id){
        if (id!=null){
            userid_Session.remove(id);
        }
    }


    // 任务id和任务结果的映射表
    private Map<String, List<String>> jobResult = new HashMap<>();
    // 向任务id和结果的映射表中添加元素
    public boolean setJobidResult(String job_id, String result){
        boolean flag = false;
        if (jobResult.containsKey(job_id)){
            List<String> list = jobResult.get(job_id);
            list.add(result);
            flag = true;
        } else {
            List<String> list = new ArrayList<>();
            list.add(result);
            flag = true;
        }
        return flag;
    }
    // 从任务id和结果的映射表中获取元素
    public List<String> getJobidResult(String job_id){
        List<String > list = null;
        if (jobResult.containsKey(job_id)){
            list = jobResult.get(job_id);
        }
        return list;
    }
    // 从任务id和结果的映射表中删除元素
    public void delJobidResult(String job_id){
        if (jobResult.containsKey(job_id)){
            jobResult.remove(job_id);
        }
    }


    // 任务id和任务接受者的映射表
    private Map<String, List<User>> jobReceiver = new HashMap<>();
    // 向任务id和接受者的映射表中添加元素
    public boolean setJobidReceiver(String job_id, User user){
        boolean flag = false;
        if (jobReceiver.containsKey(job_id)){
            List<User> list = jobReceiver.get(job_id);
            list.add(user);
            flag = true;
        } else {
            List<User> list = new ArrayList<>();
            list.add(user);
            flag = true;
        }
        return flag;
    }
    // 从任务id和接收者的映射表中获取元素
    public List<User> getJobidReceiver(String job_id){
        List<User> list = null;
        if (jobReceiver.containsKey(job_id)){
            list = jobReceiver.get(job_id);
        }
        return list;
    }
    // 从任务id和接受者的映射表中删除元素
    public void delJobidReceiver(String job_id){
        if (jobReceiver.containsKey(job_id)){
            jobReceiver.remove(job_id);
        }
    }
}
