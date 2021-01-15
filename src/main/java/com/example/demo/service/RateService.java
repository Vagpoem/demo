package com.example.demo.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example.demo.mapper.ReceiveMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RateService {

    private Log log = LogFactory.get(RateService.class);

    @Autowired
    ReceiveMapper receiveMapper;

    /**
     * 准确率计算
     * @param userId
     * @return
     */
    public int accuracyCalculate(String userId){
        int res = 0;

        int success = receiveMapper.selectSuccess(userId);
        int all = receiveMapper.selectAll(userId);

        if (all == 0){
            return res;
        }

        double successTemp = success;
        double allTemp = all;
        double temp = ((double)successTemp/allTemp) * 100;

        log.info("用户"+userId+"的正确率为："+temp);
        res = (int)temp;

        return res;
    }

    /**
     * 异常率计算
     * @param userId
     * @return
     */
    public int exceptionRateCalculate(String userId){
        int res = 0;

        int exception = receiveMapper.selectError(userId);
        int all = receiveMapper.selectAll(userId);

        if (all == 0){
            return res;
        }

        double exceptionTemp = exception;
        double allTemp = all;
        double temp = ((double)exceptionTemp/allTemp) * 100;
        log.info("用户"+userId+"的异常率为："+temp);

        res = (int)temp ;

        return res;
    }

    public Integer averageTime(String receiverId){
        return receiveMapper.selectAverageTimeLoss(receiverId);
    }
}
