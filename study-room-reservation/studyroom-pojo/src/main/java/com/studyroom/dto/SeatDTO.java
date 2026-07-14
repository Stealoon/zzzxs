package com.studyroom.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SeatDTO implements Serializable {

    //座位ID（修改时必传）
    private Long id;

    //座位编号（兼容前端name字段）
    @JsonProperty("name")
    private String seatCode;

    //所属区域ID
    private String areaId;
    
    //兼容前端categoryId字段
    @JsonProperty("categoryId")
    public void setCategoryId(String categoryId) {
        this.areaId = categoryId;
    }
    
    //兼容前端area_id字段
    @JsonProperty("area_id")
    public void setAreaIdUnderscore(String areaIdUnderscore) {
        this.areaId = areaIdUnderscore;
    }

    //小时单价（兼容前端price字段）
    @JsonProperty("price")
    private BigDecimal hourPrice;

    //座位实景图URL
    private String seatImage;
    
    //图片URL（前端使用）
    private String image;

    //座位描述（兼容前端description字段）
    @JsonProperty("description")
    private String seatDescription;

    //状态
    private Integer status;

    //是否带插座
    private Integer hasSocket;

    //是否靠窗
    private Integer hasWindow;

}
