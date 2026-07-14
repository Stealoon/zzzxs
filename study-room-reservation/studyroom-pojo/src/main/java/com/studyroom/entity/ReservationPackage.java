package com.studyroom.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预约套餐实体
 * 对应数据库reservation_package表，描述自习室预约套餐的价格、时长、有效期等信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationPackage implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键ID
    private Long id;

    //适用区域ID，0表示全场通用
    private Long areaId;

    //套餐名称
    private String packageName;

    //套餐售价
    private BigDecimal packagePrice;

    //状态：0停用 1启用
    private Integer status;

    //套餐使用规则说明
    private String packageDesc;

    //套餐封面图URL
    private String packageImage;

    //套餐总有效时长（小时）
    private Integer durationHours;

    //套餐有效天数（天）
    private Integer validDays;

    //创建时间
    private LocalDateTime createTime;

    //更新时间
    private LocalDateTime updateTime;

    //创建人ID
    private Long createUser;

    //修改人ID
    private Long updateUser;

}
