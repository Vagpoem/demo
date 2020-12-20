package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.bean.HttpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class ClassifyService {

    @Autowired
    HttpRequest httpRequest;
    @Autowired
    GlobalVariable globalVariable;

    public String classify(String path){
        String type = "7";

        // 1.构造分类api接口的数据对象
        JSONObject object = new JSONObject();
        object.put("path", path);

        // 2.调用分类接口
        JSONObject isClass = httpRequest.getRes(globalVariable.getPhotoClassify_api_url(), object);

        // 3.对分类结果进行解析
        if (!ObjectUtils.isEmpty(isClass)){
            if (isClass.getString("code").equals("200")){
                type = isClass.getString("type");
            }
        }

        return type;
    }
}
