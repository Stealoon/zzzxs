package com.studyroom.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用户端座位查询VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatQueryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    //座位ID
    private Long id;

    //座位编号
    private String seatCode;

    //所属区域ID
    private Long areaId;

    //所属区域名称
    private String areaName;

    //小时单价
    private BigDecimal hourPrice;

    //座位实景图URL
    private String seatImage;

    //座位描述
    private String seatDescription;

    //状态
    private Integer status;

    //是否带插座
    private Integer hasSocket;

    //是否靠窗
    private Integer hasWindow;

    //当日时段占用状态列表
    private List<TimeSlotVO> timeSlots;

    /**
     * 时段占用状态VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlotVO implements Serializable {

        private static final long serialVersionUID = 1L;

        //时段开始时间
        private String startTime;

        //时段结束时间
        private String endTime;

        //是否可预约
        private Boolean available;

    }

}
