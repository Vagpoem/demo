package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoteService {

    public String voteResult(List<String> list){
        String res = "";

        res = list.get(0);

        return res;
    }
}
