package com.studyroom.service;

import com.studyroom.dto.UserLoginByPasswordDTO;
import com.studyroom.dto.UserLoginDTO;
import com.studyroom.dto.UserRegisterDTO;
import com.studyroom.dto.UserUpdateDTO;
import com.studyroom.entity.User;
import com.studyroom.vo.UserLoginVO;

public interface UserService {

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);

    /**
     * 手机号+密码登录
     * @param userLoginByPasswordDTO
     * @return
     */
    User loginByPassword(UserLoginByPasswordDTO userLoginByPasswordDTO);

    /**
     * 更新用户信息
     * @param userUpdateDTO
     */
    void update(UserUpdateDTO userUpdateDTO);

    /**
     * 根据ID查询用户
     * @param id
     * @return
     */
    User getById(Long id);

    /**
     * 用户注册
     * @param userRegisterDTO
     * @return
     */
    UserLoginVO register(UserRegisterDTO userRegisterDTO);
}
