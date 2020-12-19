package com.example.demo.mapper;

import com.example.demo.bean.entity.Job;
import org.apache.ibatis.annotations.*;

@Mapper
public interface JobMapper {

    /**
     * 添加任务记录
     * @param job 任务数据对象
     */
    @Insert("insert into job values(#{job_id},#{job_name},#{requester_id},#{receiver_id}," +
            "#{job_status},#{request_time},#{receive_time},#{finish_time},#{captcha_src},#{captcha_result}," +
            "#{response_code},#{subtype_id})")
    @Options(useGeneratedKeys = true, keyProperty = "job_id")
    public int addJob(Job job);

    /**
     * 通过任务id选择人物的数据
     * @param job_id 任务id
     * @return 任务数据对象
     */
    @Select("select * from job where job_id=#{job_id}")
    public Job findById(@Param("job_id") String job_id);

    /**
     * 选择一个用户所有成功打码任务的数量
     * @param receiver_id 接收者的id
     * @return 成功打码任务的数量
     */
    @Select("select count(*) from job where receiver_id=#{receiver_id} and response_code='1'")
    public Integer selectSuccessJobsFromReceiver(@Param("receiver_id") String receiver_id);

    /**
     * 选择一个打码客户端的所有任务的数量
     * @param receiver_id 打码客户端的id
     * @return 所有任务的数量
     */
    @Select("select count(*) from job where receiver_id=#{receiver_id}")
    public Integer selectAllJobsFromReceiver(@Param("receiver_id") String receiver_id);

    /**
     * 选择一个打码客户端的所有无反馈任务的数量
     * @param receiver_id 打码客户端的id
     * @return 所有任务的数量
     */
    @Select("select count(*) from job where receiver_id=#{receiver_id} and response_code is null")
    public Integer selectNullResJobsFromReceiver(@Param("receiver_id") String receiver_id);

    /**
     * 任务反馈的方法
     * @param response_code 反馈码 “1”代表成功，“0”代表失败
     * @param job_id 任务的id
     */
    @Update("update job set response_code=#{response_code} where job_id=#{job_id}")
    public void response(@Param("response_code") String response_code, @Param("job_id") String job_id);
}
