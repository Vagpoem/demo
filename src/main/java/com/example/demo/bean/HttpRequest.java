package com.example.demo.bean;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpRequest {

    @Autowired
    RestTemplate restTemplate;

    public JSONObject getRes(String url, JSONObject params){
        JSONObject res = new JSONObject();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity<String> entity = new HttpEntity(params, headers);

            ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            String responseBody = responseEntity.getBody();
            res = JSONObject.parseObject(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            res = null;
        }

        System.out.println(res);

        return res;
    }
}
