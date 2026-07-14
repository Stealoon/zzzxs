package com.studyroom.controller.admin;

import com.studyroom.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 统计报表接口（管理端）
 * 提供营业额、用户、订单的区间统计以及座位预约 Top10。
 * 前端期望所有列表字段为逗号分隔的字符串（如 "364.00,217.00,..."），
 * 而非 JSON 数组，因为前端 JS 会调用 .split(",") 进行解析。
 */
@RestController
@RequestMapping("/admin/report")
@Api(tags = "统计报表接口")
@Slf4j
public class ReportController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 营业额统计
     * 按日期区间统计每日营业额：status=4(已完成) 且 DATE(checkout_time)=当日 的订单金额之和。
     * 返回：dateList(逗号分隔字符串)、turnoverList(逗号分隔字符串)、totalTurnover
     */
    @GetMapping("/turnoverStatistics")
    @ApiOperation("营业额统计")
    public Result<Map<String, Object>> turnoverStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("营业额统计：begin={}, end={}", begin, end);

        List<String> dateList = new ArrayList<>();
        List<String> turnoverList = new ArrayList<>();
        BigDecimal totalTurnover = BigDecimal.ZERO;

        for (LocalDate date : dateRange(begin, end)) {
            String dateStr = date.format(DATE_FORMATTER);
            dateList.add(dateStr);
            BigDecimal turnover = queryBigDecimal(
                    "select coalesce(sum(amount), 0) from orders " +
                            "where status = 4 and date(checkout_time) = ?", dateStr);
            turnoverList.add(turnover.toPlainString());
            totalTurnover = totalTurnover.add(turnover);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dateList", String.join(",", dateList));
        result.put("turnoverList", String.join(",", turnoverList));
        result.put("totalTurnover", totalTurnover);
        return Result.success(result);
    }

    /**
     * 用户统计
     * 按日期区间统计每日新增用户数与累计用户数。
     * 返回：dateList、newUserList、totalUserList(均为逗号分隔字符串)、totalUser、totalNewUser
     */
    @GetMapping("/userStatistics")
    @ApiOperation("用户统计")
    public Result<Map<String, Object>> userStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户统计：begin={}, end={}", begin, end);

        List<String> dateList = new ArrayList<>();
        List<String> newUserList = new ArrayList<>();
        List<String> totalUserList = new ArrayList<>();
        int totalNewUser = 0;

        for (LocalDate date : dateRange(begin, end)) {
            String dateStr = date.format(DATE_FORMATTER);
            dateList.add(dateStr);

            Integer newUser = queryInteger(
                    "select count(*) from user where date(create_time) = ?", dateStr);
            newUserList.add(String.valueOf(newUser));
            totalNewUser += newUser;

            Integer totalUser = queryInteger(
                    "select count(*) from user where date(create_time) <= ?", dateStr);
            totalUserList.add(String.valueOf(totalUser));
        }

        // 截至结束日期的累计用户数
        Integer totalUser = queryInteger(
                "select count(*) from user where date(create_time) <= ?", end.format(DATE_FORMATTER));

        Map<String, Object> result = new HashMap<>();
        result.put("dateList", String.join(",", dateList));
        result.put("newUserList", String.join(",", newUserList));
        result.put("totalUserList", String.join(",", totalUserList));
        result.put("totalUser", totalUser == null ? 0 : totalUser);
        result.put("totalNewUser", totalNewUser);
        return Result.success(result);
    }

    /**
     * 订单统计
     * 按日期区间统计每日订单总数与有效订单数(status NOT IN (5,6))。
     * 返回：dateList、orderCountList、validOrderCountList(逗号分隔字符串)，
     *       totalOrderCount、validOrderCount、orderCompletionRate(数值)
     */
    @GetMapping("/ordersStatistics")
    @ApiOperation("订单统计")
    public Result<Map<String, Object>> ordersStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("订单统计：begin={}, end={}", begin, end);

        List<String> dateList = new ArrayList<>();
        List<String> orderCountList = new ArrayList<>();
        List<String> validOrderCountList = new ArrayList<>();
        int totalOrderCount = 0;
        int validOrderCount = 0;

        for (LocalDate date : dateRange(begin, end)) {
            String dateStr = date.format(DATE_FORMATTER);
            dateList.add(dateStr);

            Integer orderCount = queryInteger(
                    "select count(*) from orders where date(order_time) = ?", dateStr);
            Integer validCount = queryInteger(
                    "select count(*) from orders " +
                            "where status is not null and status not in (5,6) and date(order_time) = ?", dateStr);
            orderCountList.add(String.valueOf(orderCount));
            validOrderCountList.add(String.valueOf(validCount));
            totalOrderCount += orderCount;
            validOrderCount += validCount;
        }

        double orderCompletionRate = totalOrderCount > 0
                ? (validOrderCount * 1.0d / totalOrderCount) : 0.0d;

        Map<String, Object> result = new HashMap<>();
        result.put("dateList", String.join(",", dateList));
        result.put("orderCountList", String.join(",", orderCountList));
        result.put("validOrderCountList", String.join(",", validOrderCountList));
        result.put("totalOrderCount", totalOrderCount);
        result.put("validOrderCount", validOrderCount);
        result.put("orderCompletionRate", Math.round(orderCompletionRate * 100) / 100.0d);
        return Result.success(result);
    }

    /**
     * 座位预约 Top10
     * 按日期区间统计各座位的预约次数，取前10。
     * 返回：nameList(逗号分隔的座位编号)、numberList(逗号分隔的预约次数)
     * 注意：前端会调用 .reverse() 反转顺序
     */
    @GetMapping("/top10")
    @ApiOperation("座位预约Top10")
    public Result<Map<String, Object>> top10(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("座位预约Top10：begin={}, end={}", begin, end);

        String beginStr = begin.format(DATE_FORMATTER);
        String endStr = end.format(DATE_FORMATTER);

        String sql = "select s.seat_code, count(o.id) as cnt " +
                "from orders o left join seat s on o.seat_id = s.id " +
                "where date(o.order_time) between ? and ? " +
                "group by o.seat_id, s.seat_code " +
                "order by cnt desc limit 10";

        List<Map<String, Object>> rows = jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> item = new HashMap<>();
            String seatCode = rs.getString("seat_code");
            item.put("name", seatCode == null ? "未知座位" : seatCode);
            item.put("value", rs.getInt("cnt"));
            return item;
        }, beginStr, endStr);

        // 前端期望 nameList 和 numberList 为逗号分隔的字符串
        List<String> names = rows.stream().map(r -> String.valueOf(r.get("name"))).collect(Collectors.toList());
        List<String> numbers = rows.stream().map(r -> String.valueOf(r.get("value"))).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("nameList", String.join(",", names));
        result.put("numberList", String.join(",", numbers));
        return Result.success(result);
    }

    // ======================== 内部辅助方法 ========================

    /**
     * 构造日期区间列表 [begin, end]（含两端）。begin 晚于 end 时返回空列表。
     */
    private List<LocalDate> dateRange(LocalDate begin, LocalDate end) {
        List<LocalDate> dates = new ArrayList<>();
        if (begin == null || end == null || begin.isAfter(end)) {
            return dates;
        }
        for (LocalDate d = begin; !d.isAfter(end); d = d.plusDays(1)) {
            dates.add(d);
        }
        return dates;
    }

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
