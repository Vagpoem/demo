package com.example.demo.util;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Component
public class GlobalMap {

    private Map<String, HttpSession> username_Session = new HashMap<>();
    private Map<String, HttpSession> userid_Session = new HashMap<>();

    public boolean setUsernameSession(String name, HttpSession session){
        boolean res = false;
        if (name!=null && !ObjectUtils.isEmpty(session)) {
            username_Session.put(name, session);
            res = true;
        }
        return res;
    }
    public HttpSession getSessionFromUsername(String name){
        HttpSession session = null;
        if (name!=null){
            session = username_Session.get(name);
        }
        return session;
    }
    public void delSessionFromUsername(String name){
        if (name!=null){
            username_Session.remove(name);
        }
    }

    public boolean setUseridSession(String id, HttpSession session){
        boolean res = false;
        if (id!=null && !ObjectUtils.isEmpty(session)) {
            userid_Session.put(id, session);
            res = true;
        }
        return res;
    }
    public HttpSession getSessionFromUserid(String id){
        HttpSession session = null;
        if (id!=null){
            session = userid_Session.get(id);
        }
        return session;
    }
    public void delSessionFromUserid(String id){
        if (id!=null){
            userid_Session.remove(id);
        }
    }

}
