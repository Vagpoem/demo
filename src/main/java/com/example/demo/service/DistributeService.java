package com.example.demo.service;

import com.example.demo.bean.GlobalVariable;
import com.example.demo.bean.entity.BypassUser;
import com.example.demo.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DistributeService {

    @Autowired
    GlobalVariable globalVariable;

    public String distribute(String type){
        String res = "person";
        if (Util.hasElement(globalVariable.getAi_captcha_type_list(), type)){
            res = "ai";
        }
        System.out.println(res);
        return res;
    }
}
