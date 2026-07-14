package com.studyroom.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrdersPageQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    //当前页码
    private int page;

    //每页条数
    private int pageSize;

    //预约订单号（模糊查询）
    private String number;

    //预约订单状态筛选
    private Integer status;

    //座位ID筛选
    private Long seatId;

    //用户ID筛选
    private Long userId;

    //查询开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    //查询结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

}
