package com.example.demo.bean.entity;

import java.sql.Timestamp;

public class Result {
    private String userId;
    private String result;
    // -1表示意外或是出错，0表示结果未被采纳，1表示结果被采纳
    private int flag;
    private Timestamp overTime;

    public Result(String userId, String result) {
        this.userId = userId;
        this.result = result;
        this.flag = 0;
    }

    public Timestamp getOverTime() {
        return overTime;
    }

    public void setOverTime(Timestamp overTime) {
        this.overTime = overTime;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
