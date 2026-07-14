package com.studyroom.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AreaDTO implements Serializable {

    //区域ID（修改时必传）
    private Long id;

    //区域名称
    private String name;

    //排序
    private Integer sort;

    //区域描述
    private String description;

    // 兼容前端字段 - 类型（1区域，2套餐）
    private Integer type;

    // 兼容前端字段 - 图片
    private String image;

}
