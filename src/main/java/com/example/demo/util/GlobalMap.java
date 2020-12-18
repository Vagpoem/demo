package com.example.demo.util;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Component
public class GlobalMap {

    private Map<String, HttpSession> username_Session = new HashMap<>();

    public boolean setUsernameSession(String name, HttpSession session){
        boolean res = false;
        if (name!=null && !ObjectUtils.isEmpty(session)) {
            username_Session.put(name, session);
            res = true;
        }
        return res;
    }

    public HttpSession getSession(String name){
        HttpSession session = null;
        if (name!=null){
            session = username_Session.get(name);
        }
        return session;
    }

}
