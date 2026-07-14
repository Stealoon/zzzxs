package com.studyroom.controller.admin;

import com.studyroom.entity.Orders;
import com.studyroom.dto.OrdersCancelDTO;
import com.studyroom.dto.OrdersPageQueryDTO;
import com.studyroom.result.PageResult;
import com.studyroom.result.Result;
import com.studyroom.service.OrdersService;
import com.studyroom.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 预约订单管理（管理端）
 */
@RestController("adminOrdersController")
@RequestMapping("/admin/order")
@Api(tags = "预约订单管理接口")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @GetMapping("/page")
    @ApiOperation("预约订单分页查询")
    public Result<PageResult> page(OrdersPageQueryDTO queryDTO) {
        log.info("预约订单分页查询：{}", queryDTO);
        PageResult pageResult = ordersService.pageQuery(queryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("查询预约订单详情")
    public Result<OrderVO> detail(@PathVariable Long id) {
        log.info("查询预约订单详情：{}", id);
        OrderVO orderVO = ordersService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    @GetMapping("/details/{id}")
    @ApiOperation("查询预约订单详情（兼容前端）")
    public Result<OrderVO> details(@PathVariable Long id) {
        log.info("查询预约订单详情（兼容前端）：{}", id);
        OrderVO orderVO = ordersService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    @GetMapping("/conditionSearch")
    @ApiOperation("条件搜索订单")
    public Result<PageResult> conditionSearch(OrdersPageQueryDTO queryDTO) {
        log.info("条件搜索订单：{}", queryDTO);
        PageResult pageResult = ordersService.pageQuery(queryDTO);
        return Result.success(pageResult);
    }

    @PutMapping("/cancel")
    @ApiOperation("取消预约")
    public Result cancel(@RequestBody OrdersCancelDTO cancelDTO) throws Exception {
        log.info("取消预约：{}", cancelDTO);
        ordersService.cancel(cancelDTO);
        return Result.success();
    }

    @PostMapping("/checkin")
    @ApiOperation("管理端签到核销")
    public Result checkin(Long orderId, String checkinCode) {
        log.info("管理端签到核销，订单ID：{}，签到码：{}", orderId, checkinCode);
        ordersService.checkin(orderId, checkinCode);
        return Result.success();
    }

    @PutMapping("/confirm")
    @ApiOperation("确认预约")
    public Result confirm(@RequestBody java.util.Map<String, Object> params) throws Exception {
        log.info("确认预约：{}", params.get("id"));
        Long id = Long.valueOf(params.get("id").toString());
        ordersService.confirm(id);
        return Result.success();
    }

    @PutMapping("/rejection")
    @ApiOperation("拒绝预约")
    public Result rejection(@RequestBody java.util.Map<String, Object> params) throws Exception {
        log.info("拒绝预约：{}", params.get("id"));
        OrdersCancelDTO cancelDTO = new OrdersCancelDTO();
        cancelDTO.setId(Long.valueOf(params.get("id").toString()));
        cancelDTO.setCancelReason(params.get("rejectionReason") != null ? params.get("rejectionReason").toString() : "管理员拒绝");
        ordersService.cancel(cancelDTO);
        return Result.success();
    }

    @PutMapping("/complete/{id}")
    @ApiOperation("完成预约")
    public Result complete(@PathVariable Long id) {
        log.info("完成预约：{}", id);
        ordersService.autoComplete(id);
        return Result.success();
    }

    @GetMapping("/statistics")
    @ApiOperation("订单统计")
    public Result statistics() {
        log.info("订单统计");
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("total", ordersService.countAll());
        stats.put("completed", ordersService.countByStatus(Orders.COMPLETED));     // 已完成(status=4)
        stats.put("cancelled", ordersService.countByStatus(Orders.CANCELLED));   // 已取消(status=5)
        stats.put("expired", ordersService.countByStatus(Orders.EXPIRED));      // 已过期(status=6)
        stats.put("toBeConfirmed", ordersService.countByStatus(Orders.RESERVED)); // 待确认(已预约,status=2)
        stats.put("confirmed", ordersService.countByStatus(Orders.IN_USE));      // 待使用(使用中,status=3)
        return Result.success(stats);
    }
}
