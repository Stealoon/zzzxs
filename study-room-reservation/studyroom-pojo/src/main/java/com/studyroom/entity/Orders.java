package com.studyroom.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预约订单
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Orders implements Serializable {

    /**
     * 预约订单状态 1待支付 2已预约 3使用中 4已完成 5已取消 6已过期
     */
    public static final Integer PENDING_PAYMENT = 1; // 待支付
    public static final Integer RESERVED = 2;        // 已预约（待签到）
    public static final Integer IN_USE = 3;          // 使用中（已签到）
    public static final Integer COMPLETED = 4;        // 已完成
    public static final Integer CANCELLED = 5;        // 已取消
    public static final Integer EXPIRED = 6;          // 已过期

    /**
     * 支付状态 0未支付 1已支付
     */
    public static final Integer UN_PAID = 0; // 未支付
    public static final Integer PAID = 1;    // 已支付

    private static final long serialVersionUID = 1L;

    // 主键ID
    private Long id;

    // 预约订单号
    private String number;

    // 预约订单状态 1待支付 2已预约 3使用中 4已完成 5已取消 6已过期
    private Integer status;

    // 下单用户ID
    private Long userId;

    // 预约座位ID
    private Long seatId;

    // 所属区域ID
    private Long areaId;

    // 预约开始时间
    private LocalDateTime startTime;

    // 预约结束时间
    private LocalDateTime endTime;

    // 实际签到时间
    private LocalDateTime checkinTime;

    // 6位数字签到核销码
    private String checkinCode;

    // 使用的预约套餐ID
    private Long packageId;

    // 预约总金额
    private BigDecimal amount;

    // 支付方式 1微信，2支付宝
    private Integer payMethod;

    // 支付状态 0未支付 1已支付
    private Integer payStatus;

    // 提交预约时间
    private LocalDateTime orderTime;

    // 支付完成时间
    private LocalDateTime checkoutTime;

    // 取消时间
    private LocalDateTime cancelTime;

    // 取消原因
    private String cancelReason;
}
