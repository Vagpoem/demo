package com.example.demo.service;

import com.example.demo.bean.GlobalMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetJobResService {

    @Autowired
    GlobalMap globalMap;

    public List<String> getRes(String id){
        List<String> res = null;

        if (!ObjectUtils.isEmpty(id))
        {
            res = globalMap.getJobidResult(id);
        }

        return res;
    }
}
