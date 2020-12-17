package com.example.demo.bean.entity;

public class UrlJson {
    private String url;
    private String path;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public UrlJson(String url, String path) {
        this.url = url;
        this.path = path;
    }
}
