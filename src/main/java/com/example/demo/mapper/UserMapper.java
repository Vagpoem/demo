package com.example.demo.mapper;

import com.example.demo.bean.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    /**
     * 向user表中插入数据
     * @param user 新用户的的user对象数据
     * @return 插入是否成功
     */
    @Insert("insert into user values(#{user_id},#{user_name},#{password},#{mail},#{role},#{status},#{token},#{mark},#{phone},#{hasPhone})")
    @Options(useGeneratedKeys = true, keyProperty = "user_id")
    public int addUser(User user);

    /**
     * 根据用户的id选择用户
     * @param user_id
     * @return 返回一个用户对象
     */
    @Select("select * from user where user_id = #{user_id}")
    public User selectUser(@Param("user_id") int user_id);

    /**
     * 根据用户的用户名选择用户
     * @param user_name 被选择用户的用户名
     * @return 返回一个用户对象
     */
    @Select("select * from user where user_name = #{user_name}")
    public User selectUserByName(@Param("user_name") String user_name);

    /**
     * 更新用户的得分情况
     * @param mark 被更新用户的的分数
     * @param user_id 被更新用户的用户id
     */
    @Update("update user set mark=#{mark} where user_id=#{user_id}")
    public void mark(@Param("mark") double mark, @Param("user_id") String user_id);

    /**
     * 通过手机号码选择用户
     * @param phone 被选择用户的手机号码
     * @return 返回一个用户对象
     */
    @Select("select * from user where phone=#{phone}")
    public User selectUserByPhone(@Param("phone") String phone);

    /**
     * 选择
     * @return
     */
    @Select("select * from user where hasPhone='1' order by mark")
    public List<User> selectHasPhoneUser();

    /**
     * 获取用户的token信息
     * @param user_id 用户的id
     * @return 用户的token
     */
    @Select("select token from user where user_id=#{user_id}")
    public String selectToken(@Param("user_id") String user_id);

}
