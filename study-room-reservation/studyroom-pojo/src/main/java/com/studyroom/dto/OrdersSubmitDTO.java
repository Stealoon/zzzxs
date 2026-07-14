package com.studyroom.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrdersSubmitDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    //预约座位ID
    private Long seatId;

    //所属区域ID
    private Long areaId;

    //预约开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    //预约结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    //预约套餐ID（按时计费则为null）
    private Long packageId;

    //支付方式
    private Integer payMethod;

}
