package com.example.demo.bean.entity;

public class Base64Json {

    private String str;
    private String path;

    public String getStr() {
        return str;
    }

    public void setStr(String str) {
        this.str = str;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Base64Json(String str, String path) {
        this.str = str;
        this.path = path;
    }
}
