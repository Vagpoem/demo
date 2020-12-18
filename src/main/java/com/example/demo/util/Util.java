package com.example.demo.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class Util {

    public static void addCookie(String name, String value, HttpServletResponse response, int time){
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(time);
        response.addCookie(cookie);
    }

}
