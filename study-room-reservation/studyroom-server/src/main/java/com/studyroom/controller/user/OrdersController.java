package com.studyroom.controller.user;

import com.studyroom.context.BaseContext;
import com.studyroom.dto.OrdersCancelDTO;
import com.studyroom.dto.OrdersPageQueryDTO;
import com.studyroom.dto.OrdersPaymentDTO;
import com.studyroom.dto.OrdersSubmitDTO;
import com.studyroom.exception.OrderBusinessException;
import com.studyroom.result.PageResult;
import com.studyroom.result.Result;
import com.studyroom.service.OrdersService;
import com.studyroom.vo.OrderPaymentVO;
import com.studyroom.vo.OrderSubmitVO;
import com.studyroom.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("userOrdersController")
@RequestMapping("/user/order")
@Api(tags = "用户端预约订单接口")
@Slf4j
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @PostMapping("/submit")
    @ApiOperation("提交预约")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("提交预约：{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = ordersService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    @PostMapping("/pay")
    @ApiOperation("预约支付")
    public Result<OrderPaymentVO> pay(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("预约支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = ordersService.pay(ordersPaymentDTO);
        return Result.success(orderPaymentVO);
    }

    @PostMapping("/checkin")
    @ApiOperation("扫码签到")
    public Result checkin(Long orderId, String checkinCode) {
        log.info("扫码签到，订单ID：{}，签到码：{}", orderId, checkinCode);
        ordersService.checkin(orderId, checkinCode);
        return Result.success();
    }

    @PutMapping("/cancel")
    @ApiOperation("取消预约")
    public Result cancel(@RequestBody OrdersCancelDTO cancelDTO) throws Exception {
        log.info("取消预约：{}", cancelDTO);
        // 归属校验：确保当前用户只能取消自己的订单
        OrderVO orderVO = ordersService.getOrderDetail(cancelDTO.getId());
        if (orderVO == null || !orderVO.getUserId().equals(BaseContext.getCurrentId())) {
            throw new OrderBusinessException("无权操作此订单");
        }
        ordersService.cancel(cancelDTO);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("我的预约列表")
    public Result<PageResult> list(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("我的预约列表：{}", ordersPageQueryDTO);
        // 强制设置当前用户ID，防止越权查询他人订单
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        PageResult pageResult = ordersService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/detail/{id}")
    @ApiOperation("预约详情")
    public Result<OrderVO> detail(@PathVariable Long id) {
        log.info("预约详情：{}", id);
        OrderVO orderVO = ordersService.getOrderDetail(id);
        // 归属校验：确保当前用户只能查看自己的订单
        if (orderVO == null || !orderVO.getUserId().equals(BaseContext.getCurrentId())) {
            throw new OrderBusinessException("无权查看此订单");
        }
        return Result.success(orderVO);
    }
}
