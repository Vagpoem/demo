package com.example.demo.service;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.example.demo.bean.entity.Receive;
import com.example.demo.bean.entity.Result;
import com.example.demo.bean.entity.User;
import com.example.demo.mapper.ReceiveMapper;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class MarkService {

    private Log log = LogFactory.get(MarkService.class);

    @Autowired
    ReceiveMapper receiveMapper;
    @Autowired
    UserMapper userMapper;

    /**
     * 更新数据库中的用户得分以及任务接收的表的情况
     * @param list 结果列表
     * @param jobId 任务的id
     * @return 返回是否更新成功
     */
    public void updateMark(List<Result> list, String jobId, Timestamp timestamp) throws Exception{
        Receive temp = null;
        String state = "";

        for (Result result : list){
            // 1.更新receive表和user表
            temp = new Receive(jobId, result.getUserId(), result.getFlag()+"");
            log.info("更新的receive对象为："+temp);
            int loss = (int) ((result.getOverTime().getTime() - timestamp.getTime())/1000);
            temp.setTimeLoss(loss);
            receiveMapper.addReceive(temp);

            // 2.更新user表
            if (result.getFlag()==-1){
                userMapper.markSubtractOne(result.getUserId());
            }
            if (result.getFlag()==0){
                userMapper.markZeroOne(result.getUserId());
            }
//            if (result.getFlag()==1){
//                userMapper.markAddOne(result.getUserId());
//            }
        }
    }

    /**
     * 获取某一个用户的得分
     * @param userId
     * @return
     */
    public int getMark(String userId){
        int res = 0;

        User user = userMapper.selectUser(Integer.parseInt(userId));
        long temp = (long)user.getMark();
        res = (int)user.getMark();

        return res;
    }

}
