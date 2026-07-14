package com.studyroom.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReservationPackagePageQueryDTO implements Serializable {

    //当前页码
    private int page;

    //每页显示记录数
    private int pageSize;

    //套餐名称（模糊查询）
    @JsonProperty("name")
    private String packageName;

    //适用区域ID筛选
    @JsonProperty("categoryId")
    private Long areaId;

    //状态筛选
    private Integer status;

}
