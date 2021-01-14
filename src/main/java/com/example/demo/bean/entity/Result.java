package com.example.demo.bean.entity;

public class Result {
    private String userId;
    private String result;

    public Result(String userId, String result) {
        this.userId = userId;
        this.result = result;
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
