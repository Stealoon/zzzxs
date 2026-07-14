package com.studyroom.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 座位实体
 * 对应数据库seat表，描述自习室区域内具体座位的编号、价格、设施等信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Seat implements Serializable {

    private static final long serialVersionUID = 1L;

    //主键ID
    private Long id;

    //座位编号（如A01、B05，区域内唯一）
    private String seatCode;

    //所属区域ID
    private Long areaId;

    //小时单价（单位：元）
    private BigDecimal hourPrice;

    //座位实景图URL
    private String seatImage;

    //座位描述
    private String seatDescription;

    //状态：0停用 1启用
    private Integer status;

    //是否带插座：0否 1是
    private Integer hasSocket;

    //是否靠窗：0否 1是
    private Integer hasWindow;

    //创建时间
    private LocalDateTime createTime;

    //更新时间
    private LocalDateTime updateTime;

    //创建人ID
    private Long createUser;

    //修改人ID
    private Long updateUser;

}
