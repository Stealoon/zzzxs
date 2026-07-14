package com.studyroom.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 座位VO（管理端返回）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键ID
    private Long id;

    //座位编号
    private String seatCode;

    //所属区域ID
    private Long areaId;

    //所属区域名称（联表查询填充）
    private String areaName;

    //小时单价
    private BigDecimal hourPrice;

    //座位实景图URL
    private String seatImage;
    
    //图片URL（前端使用）
    private String image;

    //座位描述
    private String seatDescription;

    //状态
    private Integer status;

    //是否带插座
    private Integer hasSocket;

    //是否靠窗
    private Integer hasWindow;

    //创建时间
    private LocalDateTime createTime;

    //更新时间
    private LocalDateTime updateTime;

}
