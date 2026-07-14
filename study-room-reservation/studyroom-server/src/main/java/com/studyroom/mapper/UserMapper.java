package com.studyroom.mapper;

import com.studyroom.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    /**
     * 根据openid查询用户
     * @param openid
     * @return
     */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);

    /**
     * 根据ID查询用户
     * @param id
     * @return
     */
    @Select("select * from user where id = #{id}")
    User getById(Long id);

    /**
     * 根据手机号查询用户
     * @param phone
     * @return
     */
    @Select("select * from user where phone = #{phone}")
    User getByPhone(String phone);

    /**
     * 插入数据
     * @param user
     */
    void insert(User user);

    /**
     * 动态更新用户信息
     * @param user
     */
    void update(User user);
}
