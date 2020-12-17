package com.example.demo.bean.entity;

public class AI implements BypassUser {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public String recieveJob(JobMessage jobMessage) {
        return null;
    }
}
