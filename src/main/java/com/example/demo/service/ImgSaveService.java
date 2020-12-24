package com.example.demo.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.bean.GlobalVariable;
import com.example.demo.bean.HttpRequest;
import com.example.demo.bean.entity.Base64Json;
import com.example.demo.bean.entity.LinkJson;
import com.example.demo.bean.entity.UrlJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class ImgSaveService {

    private Log log = LogFactory.get(ImgSaveService.class);

    @Autowired
    GlobalVariable globalVariable;
    @Autowired
    HttpRequest httpRequest;

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

        log.info("base64编码为："+base64);

        boolean flag = false;

        // 1.先将base64编码转换为合适的格式
        String tempbase64 = base64;
        int end = base64.indexOf(",");
        if (end>0){
            tempbase64 = base64.substring(end + 1);
        }

        // 2.构造base64数据保存对象
        Base64Json base64Json = new Base64Json(tempbase64, path);
        JSONObject object = (JSONObject) JSONObject.toJSON(base64Json);

        // 3.调用base64数据保存接口
        JSONObject isSave = httpRequest.getRes(globalVariable.getPhotoSave_base64_url(), object);

        // 4.判断存储状态
        if (!ObjectUtils.isEmpty(isSave)){
            if (isSave.getString("status").equals("200")){
                flag = true;
            }
        }

        return flag;
    }

    // 用于保存url数据的方法
    private boolean urlSave(String url, String path){
        boolean flag = false;

        // 1.构造url数据保存对象
        UrlJson urlJson = new UrlJson(url, path);

        // 2.调用保存接口
        JSONObject object = (JSONObject) JSONObject.toJSON(urlJson);
        JSONObject isSave = httpRequest.getRes(globalVariable.getPhotoSave_url_url(), object);

        // 3.判断存储状态
        if (!ObjectUtils.isEmpty(isSave)){
            if (isSave.getString("status").equals("200")){
                flag = true;
            }
        }

        return flag;
    }

    // 用于保存链接数据的方法
    private boolean linkSave(String link, String path){
        boolean flag = true;

        // 1.构造link数据保存对象
        // LinkJson linkJson = new LinkJson(link, path);

        return flag;
    }
}
