package com.studyroom.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AreaPageQueryDTO implements Serializable {

    //当前页码
    private int page;

    //每页显示记录数
    private int pageSize;

    //区域名称（模糊查询）
    private String name;

    // 兼容前端字段 - 类型（1区域，2套餐）
    private Integer type;

}
