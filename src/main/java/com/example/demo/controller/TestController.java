package com.example.demo.controller;

import com.example.demo.util.GlobalVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

//    @Autowired
//    GlobalVariable globalVariable;

    @GetMapping("/getlist1")
    public List<String> test(){
//        return globalVariable.list1;
        return GlobalVariable.list1;
    }
}
