package com.studyroom.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * C端用户注册
 */
@Data
public class UserRegisterDTO implements Serializable {

    //昵称（可选，默认"新用户"）
    private String name;

    //手机号
    @NotBlank(message = "手机号不能为空")
    private String phone;

    //密码（明文，后端MD5加密）
    @NotBlank(message = "密码不能为空")
    private String password;

}
