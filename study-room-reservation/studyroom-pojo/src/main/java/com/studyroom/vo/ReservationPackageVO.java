package com.studyroom.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预约套餐VO（管理端返回）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPackageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键ID
    private Long id;

    //适用区域ID
    private Long areaId;

    //适用区域名称（联表查询填充）
    private String areaName;

    //套餐名称 - 同时输出name字段兼容前端
    @JsonProperty("name")
    private String packageName;

    //套餐售价
    private BigDecimal price;

    //状态
    private Integer status;

    //套餐使用规则说明
    private String packageDesc;

    //套餐封面图URL
    private String packageImage;

    //图片URL（前端使用）
    private String image;

    //套餐总有效时长
    private Integer durationHours;

    //套餐有效天数
    private Integer validDays;

    //创建时间
    private LocalDateTime createTime;

    //更新时间
    private LocalDateTime updateTime;

    // 兼容前端 - 分类ID
    @JsonProperty("categoryId")
    public Long getCategoryId() {
        return areaId;
    }

    // 兼容前端 - 分类名称
    @JsonProperty("categoryName")
    public String getCategoryName() {
        return areaName;
    }

}
