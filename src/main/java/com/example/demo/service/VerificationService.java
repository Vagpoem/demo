package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.entity.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class VerificationService {

    @Autowired
    UserMapper userMapper;

    public boolean verify(JSONObject param, String token){
        boolean flag = false;

        // 1.先检查用户是否存在
        User exitUser = userMapper.selectUser(Integer.parseInt(param.getString("user_id")));
        if (!ObjectUtils.isEmpty(exitUser)){
            // 2.再检查token是正确
            if (exitUser.getToken().equals(token)){
                // 3.最后检查数据是否合法
                if (!param.getString("data").equals("")){
                    flag = true;
                }
            }
        }

        return flag;
    }
}
