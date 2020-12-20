package com.example.demo.controller.listener;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.entity.User;
import com.example.demo.service.AvaiUserListService;
import com.example.demo.service.JobResRedisService;
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

    private static UserRedisService userRedisService;
    private static AvaiUserListService avaiUserListService;
    private static JobResRedisService jobResRedisService;

    @Autowired
    public void setPrivateVar(UserRedisService userRedisService, AvaiUserListService avaiUserListService,
                              JobResRedisService jobResRedisService){
        WebSocketServer.userRedisService = userRedisService;
        WebSocketServer.avaiUserListService = avaiUserListService;
        WebSocketServer.jobResRedisService = jobResRedisService;
    }

    /**静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。*/
    private static int onlineCount = 0;
    /**concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。*/
    private static ConcurrentHashMap<String,WebSocketServer> webSocketMap = new ConcurrentHashMap<>();
    /**与某个客户端的连接会话，需要通过它来给客户端发送数据*/
    private Session session;
    /**接收userId*/
    private String userId="";

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId){

        // 1.将websocket加入到集合中
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

        // 2.将用户加入到空闲用户列表中

        // 3.登录状态会话状态维护


        avaiUserListService.addUserToRedisList(userRedisService.getUserFromRedis(userId));

        // 会话状态维护
        boolean flag = MySessionMap.sessionMaintain(userId);

        log.info("用户连接:"+userId+",当前在线人数为:" + getOnlineCount());
    }

    @OnClose
    public void onClose(){

        // 1.将websocket从集合中删除

        // 2.将用户从空闲用户列表中删除

        // 3.登录状态会话维护

        if (webSocketMap.containsKey(userId)){
            webSocketMap.remove(userId);

            User user = null;

            if (ObjectUtils.isEmpty(userRedisService)){
                log.warn("userRedisService对象获取失败！");
            } else {
                log.warn("userRedisService对象获取成功！");
                user = userRedisService.getUserFromRedis(userId);
                log.info("获取对象为："+user);
            }

            // 认定为放弃任务将信息放入任务结果缓存区中，从用户可用列表中删除并从set中删除
            if (ObjectUtils.isEmpty(avaiUserListService)){
                log.warn("avaiUserListService对象获取失败！");
            } else {
                log.warn("avaiUserListService对象获取成功！");
                if (!ObjectUtils.isEmpty(user)){
                    avaiUserListService.deleteUserFromRedisList(user);
                    log.warn("删除成功！！！！");
                }
            }

            // 在线人数减一
            subOnlineCount();

        }

        // 会话状态维护
        boolean flag = MySessionMap.sessionMaintain(userId);

    }

    @OnMessage
    public void onMessage(String message, Session session){

        // 1.接受任务的结果

        // 2.将结果保存

        // 3.将用户加入到空闲用户列表中

        // 4.登录状态会话维护

        // 先通过用户id和任务id的映射获取任务id，再将收到的消息放入到任务结果返回区中
        log.info("接收到来自id为 "+userId+" 的用户消息："+message);
        String jobId = jobResRedisService.getJobId(userId);
        try {
            jobResRedisService.setRes(jobId, message);
            log.info("已将用户返回的结果存储至缓存区中！");
        } catch (Exception e) {
            log.error("结果缓存出错");
            jobResRedisService.setRes(jobId, "系统出错");
        }
        avaiUserListService.addUserToRedisList(userRedisService.getUserFromRedis(userId));

        // 会话状态维护
        boolean flag = MySessionMap.sessionMaintain(userId);

    }

    @OnError
    public void onError(Session session, Throwable error){
        log.error("用户错误:"+this.userId+",原因:"+error.getMessage());
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    public static boolean sendInfo(JSONObject message, @PathParam("userId") String userId) throws IOException{

        // 将任务发送给相应的客户端
        log.info("发送消息到:"+userId+"，报文:"+message);
        if (!userId.isEmpty() && webSocketMap.containsKey(userId)){
            webSocketMap.get(userId).sendMessage(message.toString());
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
}
