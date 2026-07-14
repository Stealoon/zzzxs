package com.studyroom.controller.admin;

import com.studyroom.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作台 / 仪表盘数据接口（管理端）
 * 提供今日运营数据、订单状态概览、座位与套餐启用状态概览。
 * 由于没有专门的 Mapper 方法，这里使用 JdbcTemplate 直接执行 SQL 查询。
 */
@RestController
@RequestMapping("/admin/workspace")
@Api(tags = "工作台/仪表盘接口")
@Slf4j
public class WorkspaceController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // 订单状态名称：1待付款 2已预约 3使用中 4已完成 5已取消 6已过期
    private static final String[] ORDER_STATUS_NAMES = {"待付款", "已预约", "使用中", "已完成", "已取消", "已过期"};

    /**
     * 今日运营数据概览
     * turnover           今日营业额（status=4 已完成 且 pay_status=1 已支付的订单金额之和）
     * validOrderCount    今日有效订单数（status NOT IN (5,6)，排除已取消/已过期）
     * orderCompletionRate 今日订单完成率（已完成订单数 / 今日订单总数，分母为0时返回0）
     * unitPrice          今日客单价（营业额 / 有效订单数，分母为0时返回0）
     * newUsers           今日新增用户数
     */
    @GetMapping("/businessData")
    @ApiOperation("今日运营数据概览")
    public Result<Map<String, Object>> businessData() {
        log.info("查询今日运营数据概览");
        String today = LocalDate.now().format(DATE_FORMATTER);
        Map<String, Object> data = new HashMap<>();

        // 今日营业额：已完成且已支付（按支付完成时间 checkout_time 归属当日）
        BigDecimal turnover = queryBigDecimal(
                "select coalesce(sum(amount), 0) from orders " +
                        "where status = 4 and pay_status = 1 and date(checkout_time) = ?", today);

        // 今日有效订单数：状态不在已取消(5)、已过期(6)中（按下单时间 order_time 归属当日）
        Integer validOrderCount = queryInteger(
                "select count(*) from orders " +
                        "where status is not null and status not in (5,6) and date(order_time) = ?", today);

        // 今日订单总数
        Integer totalOrderCount = queryInteger(
                "select count(*) from orders where date(order_time) = ?", today);

        // 今日已完成订单数
        Integer completedOrderCount = queryInteger(
                "select count(*) from orders where status = 4 and date(order_time) = ?", today);

        // 订单完成率 = 已完成订单数 / 今日订单总数
        double orderCompletionRate = (totalOrderCount != null && totalOrderCount > 0)
                ? (completedOrderCount * 1.0d / totalOrderCount) : 0.0d;

        // 客单价 = 营业额 / 有效订单数
        BigDecimal unitPrice = (validOrderCount != null && validOrderCount > 0)
                ? turnover.divide(BigDecimal.valueOf(validOrderCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // 今日新增用户数
        Integer newUsers = queryInteger(
                "select count(*) from user where date(create_time) = ?", today);

        data.put("turnover", turnover.doubleValue());
        data.put("validOrderCount", validOrderCount == null ? 0 : validOrderCount);
        data.put("orderCompletionRate", Math.round(orderCompletionRate * 100) / 100.0d);
        data.put("unitPrice", unitPrice.doubleValue());
        data.put("newUsers", newUsers == null ? 0 : newUsers);

        return Result.success(data);
    }

    /**
     * 今日订单状态概览
     * 返回前端期望的对象格式，包含各状态订单数量
     */
    @GetMapping("/overviewOrders")
    @ApiOperation("今日订单状态概览")
    public Result<Map<String, Object>> overviewOrders() {
        log.info("查询今日订单状态概览");
        String today = LocalDate.now().format(DATE_FORMATTER);
        Map<String, Object> data = new HashMap<>();

        // waitingOrders: 待确认(已预约, status=2)
        Integer waitingOrders = queryInteger(
                "select count(*) from orders where status = 2 and date(order_time) = ?", today);
        // deliveredOrders: 待使用(使用中, status=3)
        Integer deliveredOrders = queryInteger(
                "select count(*) from orders where status = 3 and date(order_time) = ?", today);
        // completedOrders: 已完成(status=4)
        Integer completedOrders = queryInteger(
                "select count(*) from orders where status = 4 and date(order_time) = ?", today);
        // cancelledOrders: 已取消(status=5)
        Integer cancelledOrders = queryInteger(
                "select count(*) from orders where status = 5 and date(order_time) = ?", today);
        // allOrders: 今日订单总数
        Integer allOrders = queryInteger(
                "select count(*) from orders where date(order_time) = ?", today);

        data.put("waitingOrders", waitingOrders == null ? 0 : waitingOrders);
        data.put("deliveredOrders", deliveredOrders == null ? 0 : deliveredOrders);
        data.put("completedOrders", completedOrders == null ? 0 : completedOrders);
        data.put("cancelledOrders", cancelledOrders == null ? 0 : cancelledOrders);
        data.put("allOrders", allOrders == null ? 0 : allOrders);
        return Result.success(data);
    }

    /**
     * 座位启用状态概览
     * 返回前端期望的对象格式
     */
    @GetMapping("/overviewSeats")
    @ApiOperation("座位启用状态概览")
    public Result<Map<String, Object>> overviewSeats() {
        log.info("查询座位启用状态概览");
        Map<String, Object> data = new HashMap<>();

        Integer enabled = queryInteger("select count(*) from seat where status = 1");
        Integer disabled = queryInteger("select count(*) from seat where status = 0");

        data.put("sold", enabled == null ? 0 : enabled);
        data.put("discontinued", disabled == null ? 0 : disabled);
        return Result.success(data);
    }

    /**
     * 预约套餐启用状态概览
     * 返回前端期望的对象格式
     */
    @GetMapping("/overviewPackages")
    @ApiOperation("预约套餐启用状态概览")
    public Result<Map<String, Object>> overviewPackages() {
        log.info("查询预约套餐启用状态概览");
        Map<String, Object> data = new HashMap<>();

        Integer enabled = queryInteger("select count(*) from reservation_package where status = 1");
        Integer disabled = queryInteger("select count(*) from reservation_package where status = 0");

        data.put("sold", enabled == null ? 0 : enabled);
        data.put("discontinued", disabled == null ? 0 : disabled);
        return Result.success(data);
    }

    // ======================== 内部辅助方法 ========================

    /**
     * 查询单值 BigDecimal，异常或空结果返回 0。
     */
    private BigDecimal queryBigDecimal(String sql, Object... args) {
        try {
            BigDecimal result = jdbcTemplate.queryForObject(sql, BigDecimal.class, args);
            return result == null ? BigDecimal.ZERO : result;
        } catch (Exception e) {
            log.warn("查询BigDecimal失败：sql={}, msg={}", sql, e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * 查询单值 Integer，异常或空结果返回 0。
     */
    private Integer queryInteger(String sql, Object... args) {
        try {
            Integer result = jdbcTemplate.queryForObject(sql, Integer.class, args);
            return result == null ? 0 : result;
        } catch (Exception e) {
            log.warn("查询Integer失败：sql={}, msg={}", sql, e.getMessage());
            return 0;
        }
    }
}
