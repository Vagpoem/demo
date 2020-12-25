package com.example.demo.controller.listener;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.GlobalMap;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.bean.entity.User;
import com.example.demo.service.AvaiUserListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/socket/{userId}")
@Component
public class WebSocketServer {

    private static Log log = LogFactory.get(WebSocketServer.class);

    private static AvaiUserListService avaiUserListService;
    private static GlobalMap globalMap;
    private static GlobalVariable globalVariable;

    @Autowired
    public void setPrivateVar(AvaiUserListService avaiUserListService, GlobalMap globalMap, GlobalVariable globalVariable){
        WebSocketServer.avaiUserListService = avaiUserListService;
        WebSocketServer.globalMap = globalMap;
        WebSocketServer.globalVariable = globalVariable;
    }

    /**静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。*/
    private static int onlineCount = 0;
    /**concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。*/
    private static ConcurrentHashMap<String,WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**与某个客户端的连接会话，需要通过它来给客户端发送数据*/
    private Session session;
    /**接收userId*/
    private String userId="";
    /**接收jobId*/
    private String jobId="";

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId){

        // 1.先查看是否为登录状态再进行连接
        if (ObjectUtils.isEmpty(globalMap.getSessionFromUserid(userId))){
            return;
        }

        // 2.将websocket加入到集合中
        this.session = session;
        this.userId = userId;
        if (webSocketMap.containsKey(userId)){
            webSocketMap.remove(userId);
            webSocketMap.put(userId, this);
            //加入set中
        } else {
            webSocketMap.put(userId, this);
            addOnlineCount();
        }

        // 3.将用户加入到空闲用户列表中
        avaiUserListService.addUser((User) globalMap.getSessionFromUserid(userId).getAttribute("user"));

        // 4.登录状态会话状态维护
        globalMap.getSessionFromUserid(userId).setMaxInactiveInterval(globalVariable.getSession_age());

        log.info("用户连接:"+userId+",当前在线人数为:" + getOnlineCount());
    }

    @OnClose
    public void onClose(){

        if (webSocketMap.containsKey(userId)){
            // 1.将websocket从集合中删除
            webSocketMap.remove(userId);

            // 2.将用户从空闲用户列表中删除
            avaiUserListService.delUser((User)globalMap.getSessionFromUserid(userId).getAttribute("user"));

            // 3.登录状态会话状态维护
            globalMap.getSessionFromUserid(userId).setMaxInactiveInterval(globalVariable.getSession_age());

            // 4.在线人数减一
            subOnlineCount();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session){

        // 1.将结果保存
        globalMap.setJobidResult(jobId, message.trim());

        // 2.将用户加入到空闲用户列表中
        avaiUserListService.addUser((User)globalMap.getSessionFromUserid(userId).getAttribute("user"));

        // 3.将本对象的jobId置为空
        setJobId("");

        // 4.登录状态会话维护
        globalMap.getSessionFromUserid(userId).setMaxInactiveInterval(globalVariable.getSession_age());

    }

    @OnError
    public void onError(Session session, Throwable error){
        log.error("用户错误:"+this.userId+",原因:"+error.getMessage());
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static boolean sendInfo(JSONObject message, @PathParam("userId") String userId,
                                   @PathParam("jobId") String jobId) throws IOException{

        // 将任务发送给相应的客户端
        log.info("发送消息到:"+userId+"，报文:"+message);
        if (!userId.isEmpty() && webSocketMap.containsKey(userId)){
            webSocketMap.get(userId).sendMessage(message.toString());
            webSocketMap.get(userId).setJobId(jobId);
            log.info("返回请求客户端消息 " + message + "给用户 " + userId);
            return true;
        } else {
            log.info("用户"+userId+",不在线！");
            return false;
        }
    }

    public static WebSocketServer getWebSocket(String id){
        return webSocketMap.get(id);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }
}
