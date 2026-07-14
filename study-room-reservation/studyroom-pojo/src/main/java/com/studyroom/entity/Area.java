package com.studyroom.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 区域实体
 * 对应数据库area表，描述自习室区域划分，如静音区、沉浸区、靠窗区等
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Area implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键ID
    private Long id;

    //区域名称（如静音区、沉浸区、靠窗区）
    private String name;

    //排序字段
    private Integer sort;

    //状态：0停用 1启用
    private Integer status;

    //区域描述（区域特点、使用规则）
    private String description;

    //区域座位总数
    private Integer seatCount;

    //创建时间
    private LocalDateTime createTime;

    //更新时间
    private LocalDateTime updateTime;

    //创建人ID
    private Long createUser;

    //修改人ID
    private Long updateUser;

}
