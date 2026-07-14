package com.studyroom.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * C端用户手机号+密码登录
 */
@Data
public class UserLoginByPasswordDTO implements Serializable {

    private String phone;

    private String password;

}
