package com.studyroom.task;

import com.studyroom.mapper.OrdersMapper;
import com.studyroom.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单状态定时任务
 * 替代原 RabbitMQ 延时消息，每分钟轮询处理超时/过期/完成的订单
 */
@Component
@Slf4j
public class OrderScheduledTask {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrdersMapper ordersMapper;

    /**
     * 超时未支付自动取消（每60秒执行一次）
     */
    @Scheduled(fixedRate = 60000)
    public void autoCancelTask() {
        List<Long> ids = ordersMapper.selectIdsForAutoCancel(LocalDateTime.now().minusMinutes(15));
        if (ids != null && !ids.isEmpty()) {
            for (Long id : ids) {
                try {
                    ordersService.autoCancel(id);
                    log.info("自动取消订单：{}", id);
                } catch (Exception e) {
                    log.error("自动取消订单失败：{}", id, e);
                }
            }
        }
    }

    /**
     * 预约开始未签到自动过期（每60秒执行一次）
     */
    @Scheduled(fixedRate = 60000)
    public void autoExpireTask() {
        List<Long> ids = ordersMapper.selectIdsForAutoExpire(LocalDateTime.now());
        if (ids != null && !ids.isEmpty()) {
            for (Long id : ids) {
                try {
                    ordersService.autoExpire(id);
                    log.info("自动过期订单：{}", id);
                } catch (Exception e) {
                    log.error("自动过期订单失败：{}", id, e);
                }
            }
        }
    }

    /**
     * 预约结束自动完成（每60秒执行一次）
     */
    @Scheduled(fixedRate = 60000)
    public void autoCompleteTask() {
        List<Long> ids = ordersMapper.selectIdsForAutoComplete(LocalDateTime.now());
        if (ids != null && !ids.isEmpty()) {
            for (Long id : ids) {
                try {
                    ordersService.autoComplete(id);
                    log.info("自动完成订单：{}", id);
                } catch (Exception e) {
                    log.error("自动完成订单失败：{}", id, e);
                }
            }
        }
    }
}
