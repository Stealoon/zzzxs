package com.studyroom.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ReservationPackageDTO implements Serializable {

    //套餐ID（修改时必传）
    private Long id;

    //适用区域ID，0表示全场通用（兼容前端categoryId字段）
    @JsonProperty("categoryId")
    private String areaId;

    //套餐名称
    @JsonProperty("name")
    private String packageName;

    //套餐售价
    @JsonProperty("price")
    private BigDecimal packagePrice;

    //套餐使用规则说明
    @JsonProperty("description")
    private String packageDesc;

    //套餐封面图URL
    private String packageImage;

    //图片URL（前端使用）
    private String image;

    //套餐总有效时长（小时）
    private Integer durationHours;

    //套餐有效天数（天）
    private Integer validDays;

    // 兼容前端字段 - 区域ID类型
    @JsonProperty("idType")
    private Long idType;

    // 兼容前端字段 - 套餐码
    @JsonProperty("code")
    private String code;

    // 兼容前端字段 - 状态
    private Integer status;

}
