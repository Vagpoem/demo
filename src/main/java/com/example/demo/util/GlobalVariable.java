package com.example.demo.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GlobalVariable {

    // 任务类型列表
    @Value("#{'${request.srctype}'.split(',')}")
    private List<String> src_type_list;

    // 自动分配的任务类型列表
    @Value("#{'${request.auto.srctype}'.split(',')}")
    private List<String> auto_src_type_list;

    // 指定分配的任务类型列表
    @Value("#{'${request.appoint.srctype}'.split(',')}")
    private List<String> appoint_src_type_list;

    // 验证码类型的列表
    @Value("#{'${captcha.type}'.split(',')}")
    private List<String> captcha_type_list;

    // 验证码类型的描述列表
    @Value("#{'${captcha.type.desc}'.split(',')}")
    private List<String> captcha_type_desc_list;

    // 分配给AI的验证码类型列表
    @Value("#{'${captcha.ai.type}'.split(',')}")
    private List<String> ai_captcha_type_list;

    // 分配给人的验证码类型列表
    @Value("#{'${captcha.person.type}'.split(',')}")
    private List<String> person_captcha_type_list;

    // 单人推送的验证码类型列表
    @Value("#{'${captcha.single.push}'.split(',')}")
    private List<String> captcha_single_push_list;

    // 多人推送的验证码类型列表
    @Value("#{'${captcha.single.push}'.split(',')}")
    private List<String> captcha_multi_push_list;

    // 任务超时时间设置
    @Value("${task.timeout}")
    private int task_timeout;

    // 空闲用户获取时间设置
    @Value("${availuser.timeout}")
    private int availuser_timeout;
}
