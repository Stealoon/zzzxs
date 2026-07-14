package com.studyroom.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * C端用户信息更新DTO
 */
@Data
public class UserUpdateDTO implements Serializable {

    private Long id;

    private String name;

    private String phone;

    private String sex;

    private String avatar;

}
