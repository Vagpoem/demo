package com.example.demo.util;

import com.example.demo.bean.entity.Result;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

public class Util {

    /**
     * 用于向响应数据中加入cookie的静态方法
     * @param name cookie的name值
     * @param value cookie的value值
     * @param response 响应数据对象
     * @param time cookie的存活时间
     */
    public static void addCookie(String name, String value, HttpServletResponse response, int time){
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(time);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 用于判断是否含有某条指定的cookie
     * @param name 指定cookie的name值
     * @param cookies cookie数组
     * @return 返回数组中是否含有该条cookie
     */
    public static boolean hasCookie(String name, Cookie[] cookies){
        boolean res = false;
        // 遍历cookie数组
        if (!ObjectUtils.isEmpty(cookies)){
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)){
                    res = true;
                    break;
                }
            }
        }
        return res;
    }
    public static String getCookieValue(String name, Cookie[] cookies){
        String res = null;
        // 遍历cookie数组
        if (!ObjectUtils.isEmpty(cookies)){
            for (Cookie cookie : cookies){
                if (cookie.getName().equals(name)){
                    res = cookie.getValue();
                }
            }
        }
        return res;
    }

    /**
     * 查看一个列表中是否含有某个元素值
     * @param list
     * @param key
     * @return
     */
    public static boolean hasElement(List<String> list, String key){
        boolean flag = false;
        for (String ele : list){
            if (ele.equals(key))
                return true;
        }
        return flag;
    }

    public static void main(String[] args) {
        List<Result> list = new ArrayList<>();
        list.add(new Result("22", "abc"));
        System.out.println(list.get(0).getFlag());
        List<Result> temp = new ArrayList<>();
        temp.add(list.get(0));
        temp.get(0).setFlag(2);
        System.out.println(list.get(0).getFlag());
    }

}
