package com.example.demo.controller.listener;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example.demo.bean.GlobalMap;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.bean.SpringJobBeanFactory;
import com.example.demo.bean.entity.User;
import com.example.demo.service.AvaiUserListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.*;
import java.util.HashSet;

@WebListener
public class SessionListener implements HttpSessionListener, HttpSessionAttributeListener {
    private Log log = LogFactory.get(SessionListener.class);

    @Override
    public void attributeAdded(HttpSessionBindingEvent httpSessionBindingEvent){
        System.out.println("-----attributeAdded-----");
    }

    @Override
    public void attributeRemoved(HttpSessionBindingEvent httpSessionBindingEvent) {
        System.out.println("-----attributeRemoved-----");
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent httpSessionBindingEvent) {
        System.out.println("-----attributeReplaced-----");
    }

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext application = session.getServletContext();

        // 在application范围由一个HashSet集保存所有的session
        @SuppressWarnings("unchecked")
        HashSet<HttpSession> sessions = (HashSet<HttpSession>) application.getAttribute("sessions");
        if (sessions == null) {
            sessions = new HashSet<HttpSession>();
            application.setAttribute("sessions", sessions);
        }
        // 新创建的session均添加到HashSet集中
        sessions.add(session);
        // 可以在别处从application范围中取出sessions集合
        // 然后使用sessions.size()获取当前活动的session数，即为“在线人数”
        log.info("-----sessionCreated-----");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) throws ClassCastException {

        // 1.获取断开会话的会话对象
        log.warn("-----sessionDestroyed-----");
        HttpSession session = event.getSession();

        // 2.消除session和用户信息之间的映射
        User user = (User) session.getAttribute("user");
        session.removeAttribute("user");
        log.info("被删除session的用户信息为："+user);
        GlobalMap globalMap = SpringJobBeanFactory.getBean("globalMap");
        log.info("globalMap的信息为："+globalMap);
        globalMap.delSessionFromUserid(user.getUser_id()+"");
        globalMap.delSessionFromUsername(user.getUser_name());

        // 3.销毁会话对象映射
        ServletContext application = session.getServletContext();
        HashSet<?> sessions = (HashSet<?>) application.getAttribute("sessions");
        // 4.销毁的session均从HashSet集中移除
        sessions.remove(session);

        log.info("session注销完成！！！");
    }
}
