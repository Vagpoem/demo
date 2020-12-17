package com.example.demo.bean.entity;

public interface BypassUser {

    // 是所有的打码端都继承该接口
    public String recieveJob(JobMessage jobMessage);

}
