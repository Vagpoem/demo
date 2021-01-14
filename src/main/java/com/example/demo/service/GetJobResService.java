package com.example.demo.service;

import com.example.demo.bean.GlobalMap;
import com.example.demo.bean.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetJobResService {

    @Autowired
    GlobalMap globalMap;

    public List<Result> getRes(String id){
        List<Result> res = null;

        if (!ObjectUtils.isEmpty(id))
        {
            res = globalMap.getJobidResult(id);
        }

        return res;
    }
}
