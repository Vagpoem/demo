package com.example.demo.bean.entity;

import java.util.HashMap;
import java.util.Map;

public class UserInfo {

    private String id;
    // 用户的准确率
    private int accuracy;
    // 用户的平均速度
    private int speed;
    // 用户的
    private int accu_score;
    private int exception_rate;

    public UserInfo(String id, int accuracy, int speed, int accu_score, int exception_rate) {
        this.id = id;
        this.accuracy = accuracy;
        this.speed = speed;
        this.accu_score = accu_score;
        this.exception_rate = exception_rate;
    }

    public Map<String, Object> remap(){
        Map<String, Object> map = new HashMap<>();
        map.put("id", this.id);
        map.put("accuracy", this.accuracy);
        map.put("speed", this.speed);
        map.put("accu_score", this.accu_score);
        map.put("exception_rate", this.exception_rate);
        return map;
    }

    public String getUserid() {
        return id;
    }

    public void setUserid(String userid) {
        this.id = userid;
    }

    public int getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getAccu_score() {
        return accu_score;
    }

    public void setAccu_score(int accu_score) {
        this.accu_score = accu_score;
    }

    public int getException_rate() {
        return exception_rate;
    }

    public void setException_rate(int exception_rate) {
        this.exception_rate = exception_rate;
    }
}
