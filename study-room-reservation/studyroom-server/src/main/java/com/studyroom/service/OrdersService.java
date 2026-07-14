package com.studyroom.service;

import com.studyroom.dto.OrdersCancelDTO;
import com.studyroom.dto.OrdersPageQueryDTO;
import com.studyroom.dto.OrdersPaymentDTO;
import com.studyroom.dto.OrdersSubmitDTO;
import com.studyroom.result.PageResult;
import com.studyroom.vo.OrderSubmitVO;
import com.studyroom.vo.OrderVO;
import com.studyroom.vo.OrderPaymentVO;

public interface OrdersService {

    // 提交预约
    OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);

    // 预约支付
    OrderPaymentVO pay(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    // 扫码签到
    void checkin(Long orderId, String checkinCode);

    // 管理端确认预约（签到）
    void confirm(Long id);

    // 取消预约（管理端和用户端共用）
    void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception;

    // 预约订单分页查询
    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    // 预约详情
    OrderVO getOrderDetail(Long id);

    // 超时未支付自动取消
    void autoCancel(Long orderId);

    // 预约开始未签到自动过期
    void autoExpire(Long orderId);

    // 预约结束自动完成
    void autoComplete(Long orderId);

    // 统计所有订单数量
    Integer countAll();

    // 根据状态统计订单数量
    Integer countByStatus(Integer status);
}
