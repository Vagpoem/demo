package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.entity.Base64Json;
import com.example.demo.bean.entity.LinkJson;
import com.example.demo.bean.entity.UrlJson;
import org.springframework.stereotype.Service;

@Service
public class ImgSaveService {

    // 用于保存数据的方法
    public boolean save(JSONObject params, String path){
        String src_type = params.getString("src_type");

        switch (src_type) {
            case "1":
                return base64Save(params.getString("data"), path);
            case "2":
                return urlSave(params.getString("data"), path);
            case "3":
                return linkSave(params.getString("data"), path);
            case "4":
                return true;
        }

        return false;
    }

    // 用于保存base64数据的方法
    private boolean base64Save(String base64, String path){
        boolean flag = false;

        // 1.构造base64数据保存对象
        Base64Json base64Json = new Base64Json(base64, path);

        return flag;
    }

    // 用于保存url数据的方法
    private boolean urlSave(String url, String path){
        boolean flag = false;

        // 1.构造url数据保存对象
        UrlJson urlJson = new UrlJson(url, path);

        return flag;
    }

    // 用于保存链接数据的方法
    private boolean linkSave(String link, String path){
        boolean flag = false;

        // 1.构造link数据保存对象
        LinkJson linkJson = new LinkJson(link, path);

        return flag;
    }
}
