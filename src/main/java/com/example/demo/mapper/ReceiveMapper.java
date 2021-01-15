package com.example.demo.mapper;

import com.example.demo.bean.entity.Receive;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ReceiveMapper {

    /**
     * 向任务接受表中添加数据
     * @param receive
     * @return
     */
    @Insert("insert into receive values(#{jobId},#{receiverId},#{isSuccess},#{timeLoss})")
    public int addReceive(Receive receive);

    /**
     * 选择被采纳的数量
     * @param receiverId
     * @return
     */
    @Select("select count(*) from receive where receiverId=#{receiverId} and isSuccess='1'")
    public Integer selectSuccess(@Param("receiverId") String receiverId);

    /**
     * 选择异常数量
     * @param receiverId
     * @return
     */
    @Select("select count(*) from receive where receiverId=#{receiverId} and isSuccess='-1'")
    public Integer selectError(@Param("receiverId") String receiverId);

    /**
     * 选择没有被采纳的数量
     * @param receiverId
     * @return
     */
    @Select("select count(*) from receive where receiverId=#{receiverId} and isSuccess='0'")
    public Integer selectUnSuccess(@Param("receiverId") String receiverId);

    /**
     * 选择总的任务数量
     * @param receiverId
     * @return
     */
    @Select("select count(*) from receive where receiverId=#{receiverId}")
    public Integer selectAll(@Param("receiverId") String receiverId);

    /**
     * 查询平均时间消耗
     * @param receiverId
     * @return
     */
    @Select("select AVG(timeLoss) from receive where receiverId=#{receiverId} and isSuccess='1'")
    public Integer selectAverageTimeLoss(@Param("receiverId") String receiverId);

}
