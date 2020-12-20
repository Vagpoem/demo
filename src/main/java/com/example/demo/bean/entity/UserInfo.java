package com.example.demo.bean.entity;

public class UserInfo {

    private String userid;
    // 用户的准确率
    private int accuracy;
    // 用户的平均速度
    private int speed;
    // 用户的
    private int accu_score;
    private int exception_rate;
    private int fatigue;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

    public int getFatigue() {
        return fatigue;
    }

    public void setFatigue(int fatigue) {
        this.fatigue = fatigue;
    }
}
