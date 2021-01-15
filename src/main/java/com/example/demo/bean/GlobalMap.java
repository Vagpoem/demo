package com.example.demo.bean;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example.demo.bean.entity.Receive;
import com.example.demo.bean.entity.Result;
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

    private Log log = LogFactory.get(GlobalMap.class);

    // 用户名和会话之间的映射表
    public Map<String, HttpSession> username_Session = new HashMap<>();
    // 向映射表中添加用户名和会话的映射项
    public boolean setUsernameSession(String name, HttpSession session){
        boolean res = false;
        if (!name.equals("") && !ObjectUtils.isEmpty(session)) {
            username_Session.put(name, session);
            log.info("向username和session的映射表中加入："+"name为 "+name+"  session为 "+session);
            res = true;
        }
        return res;
    }
    // 从用户名和会话的映射表中获取会话
    public HttpSession getSessionFromUsername(String name){
        HttpSession session = null;
        if (!name.equals("")){
            session = username_Session.get(name);
        }
        return session;
    }
    // 从用户名和会话的映射表中删除元素
    public void delSessionFromUsername(String name){
        if (!name.equals("")){
            username_Session.remove(name);
        }
    }


    // 用户id和会话之间的映射表
    public Map<String, HttpSession> userid_Session = new HashMap<>();
    // 向用户id和会话的映射表中添加元素
    public boolean setUseridSession(String id, HttpSession session){
        boolean res = false;
        if (!id.equals("") && !ObjectUtils.isEmpty(session)) {
            userid_Session.put(id, session);
            log.info("向userid和session的映射表中加入："+"id为 "+id+"  session为 "+session);
            res = true;
        }
        return res;
    }
    // 从用户id和会话的映射表中获取元素
    public HttpSession getSessionFromUserid(String id){
        HttpSession session = null;
        if (!ObjectUtils.isEmpty(id)&&!id.equals("")){
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
    public Map<String, List<Result>> jobResult = new HashMap<>();
    // 向任务id和结果的映射表中添加元素
    public boolean setJobidResult(String job_id, Result result){
        boolean flag = false;
        if (jobResult.containsKey(job_id)){
            List<Result> list = jobResult.get(job_id);
            list.add(result);
            flag = true;
        } else {
            List<Result> list = new ArrayList<>();
            list.add(result);
            jobResult.put(job_id, list);
            flag = true;
        }
        return flag;
    }
    // 从任务id和结果的映射表中获取元素
    public List<Result> getJobidResult(String job_id){
        List<Result> list = null;
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
    public Map<String, List<User>> jobReceiver = new HashMap<>();
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
            jobReceiver.put(job_id, list);
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



    // 任务和接收者的完成情况映射
    public Map<String, List<Receive>> jobIsSuccess = new HashMap<>();
    public boolean addJobIsSuccess(String jobId, Receive receive){
        boolean flag = false;
        if (jobId.equals("")){
            return flag;
        }
        if (jobIsSuccess.containsKey(jobId)){
            List<Receive> list = jobIsSuccess.get(jobId);
            list.add(receive);
            flag = true;
        } else {
            List<Receive> list = new ArrayList<>();
            list.add(receive);
            jobIsSuccess.put(jobId, list);
            flag = true;
        }
        return flag;
    }
    public List<Receive> getJobIsSuccess(String jobId){
        List<Receive> res = null;
        if (!jobId.equals("")){
            if (jobIsSuccess.containsKey(jobId)){
                res = jobIsSuccess.get(jobId);
            }
        }
        return res;
    }
    public void delJobIsSuccess(String jobId){
        if (jobIsSuccess.containsKey(jobId)){
            jobIsSuccess.remove(jobId);
        }
    }

}
