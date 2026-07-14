package com.studyroom.service.impl;

import com.studyroom.constant.MessageConstant;
import com.studyroom.context.BaseContext;
import com.studyroom.dto.OrdersCancelDTO;
import com.studyroom.dto.OrdersPageQueryDTO;
import com.studyroom.dto.OrdersPaymentDTO;
import com.studyroom.dto.OrdersSubmitDTO;
import com.studyroom.entity.Area;
import com.studyroom.entity.Orders;
import com.studyroom.entity.Seat;
import com.studyroom.entity.ReservationPackage;
import com.studyroom.entity.User;
import com.studyroom.exception.OrderBusinessException;
import com.studyroom.mapper.AreaMapper;
import com.studyroom.mapper.OrdersMapper;
import com.studyroom.mapper.SeatMapper;
import com.studyroom.mapper.ReservationPackageMapper;
import com.studyroom.mapper.UserMapper;
import com.studyroom.result.PageResult;
import com.studyroom.service.OrdersService;
import com.studyroom.vo.OrderPaymentVO;
import com.studyroom.vo.OrderSubmitVO;
import com.studyroom.vo.OrderVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class OrdersServiceImpl implements OrdersService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private SeatMapper seatMapper;
    @Autowired
    private ReservationPackageMapper reservationPackageMapper;
    @Autowired
    private AreaMapper areaMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 提交预约
     */
    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        // 1. 校验座位状态
        Seat seat = seatMapper.getById(ordersSubmitDTO.getSeatId());
        if (seat == null || seat.getStatus() != 1) {
            throw new OrderBusinessException("座位不存在或已停用");
        }

        // 2. 时段冲突校验（数据库级兜底）
        int conflictCount = ordersMapper.countConflict(
                ordersSubmitDTO.getSeatId(),
                ordersSubmitDTO.getStartTime(),
                ordersSubmitDTO.getEndTime());
        if (conflictCount > 0) {
            throw new OrderBusinessException("该座位当前时段已被预约，请选择其他时段或座位");
        }

        // 3. 计算预约总金额
        BigDecimal amount = calculateAmount(ordersSubmitDTO, seat);

        // 4. 生成预约订单号
        String orderNumber = generateOrderNumber();

        // 5. 构建订单对象
        Orders orders = Orders.builder()
                .number(orderNumber)
                .status(Orders.PENDING_PAYMENT)
                .userId(BaseContext.getCurrentId())
                .seatId(ordersSubmitDTO.getSeatId())
                .areaId(seat.getAreaId())
                .startTime(ordersSubmitDTO.getStartTime())
                .endTime(ordersSubmitDTO.getEndTime())
                .packageId(ordersSubmitDTO.getPackageId())
                .amount(amount)
                .payMethod(ordersSubmitDTO.getPayMethod())
                .payStatus(Orders.UN_PAID)
                .orderTime(LocalDateTime.now())
                .build();

        // 6. 插入订单
        ordersMapper.insert(orders);

        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
    }

    /**
     * 预约支付
     */
    @Override
    @Transactional
    public OrderPaymentVO pay(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 1. 查询订单
        Orders orders = ordersMapper.getByNumber(ordersPaymentDTO.getOrderNumber());
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 2. 模拟支付成功（实际项目中对接微信支付）
        // 支付前再次校验时段冲突（防止并发下单导致重复预约）
        int conflictCount = ordersMapper.countConflict(orders.getSeatId(), orders.getStartTime(), orders.getEndTime());
        if (conflictCount > 0) {
            throw new OrderBusinessException("该座位当前时段已被预约");
        }
        // 更新订单状态为已预约
        orders.setStatus(Orders.RESERVED);
        orders.setPayStatus(Orders.PAID);
        orders.setCheckoutTime(LocalDateTime.now());

        // 3. 生成6位签到码
        String checkinCode = generateCheckinCode();
        orders.setCheckinCode(checkinCode);

        ordersMapper.update(orders);

        return OrderPaymentVO.builder()
                .orderNumber(orders.getNumber())
                .checkinCode(checkinCode)
                .build();
    }

    /**
     * 扫码签到
     */
    @Override
    @Transactional
    public void checkin(Long orderId, String checkinCode) {
        // 1. 校验订单和签到码
        Orders orders = ordersMapper.getById(orderId);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 2. 订单状态校验
        if (!orders.getStatus().equals(Orders.RESERVED)) {
            throw new OrderBusinessException("订单状态不可签到");
        }

        // 3. 签到码校验
        if (!checkinCode.equals(orders.getCheckinCode())) {
            throw new OrderBusinessException("签到码不正确");
        }

        // 4. 时段校验（预约开始前15分钟至预约开始后30分钟）
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earliestCheckin = orders.getStartTime().minusMinutes(15);
        LocalDateTime latestCheckin = orders.getStartTime().plusMinutes(30);
        if (now.isBefore(earliestCheckin) || now.isAfter(latestCheckin)) {
            throw new OrderBusinessException("当前不在签到时段内，请在预约开始前15分钟至开始后30分钟内签到");
        }

        // 5. 签到成功
        orders.setStatus(Orders.IN_USE);
        orders.setCheckinTime(now);
        ordersMapper.update(orders);
    }

    /**
     * 管理端确认预约（签到）
     */
    @Override
    @Transactional
    public void confirm(Long id) {
        // 1. 查询订单
        Orders orders = ordersMapper.getById(id);
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        // 2. 订单状态校验：仅已预约(2)的订单可确认签到
        if (!orders.getStatus().equals(Orders.RESERVED)) {
            throw new OrderBusinessException("订单状态不可确认");
        }

        // 3. 确认签到：状态更新为使用中，记录签到时间
        orders.setStatus(Orders.IN_USE);
        orders.setCheckinTime(LocalDateTime.now());
        ordersMapper.update(orders);
    }

    /**
     * 取消预约
     */
    @Override
    @Transactional
    public void cancel(OrdersCancelDTO ordersCancelDTO) throws Exception {
        Orders orders = ordersMapper.getById(ordersCancelDTO.getId());
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        Integer status = orders.getStatus();

        // 已取消或已过期的订单无需重复取消，直接返回成功
        if (status.equals(Orders.CANCELLED) || status.equals(Orders.EXPIRED)) {
            return;
        }

        // 允许取消的状态：待付款(1)、已预约(2)
        if (!status.equals(Orders.PENDING_PAYMENT) && !status.equals(Orders.RESERVED)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        ordersMapper.update(orders);
    }

    /**
     * 预约订单分页查询
     */
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        List<OrderVO> list = ordersMapper.pageQuery(ordersPageQueryDTO);
        Page<OrderVO> page = (Page<OrderVO>) list;
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 预约详情
     */
    @Override
    public OrderVO getOrderDetail(Long id) {
        Orders orders = ordersMapper.getById(id);
        if (orders == null) {
            return null;
        }
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        // 填充座位编号
        Seat seat = seatMapper.getById(orders.getSeatId());
        if (seat != null) {
            orderVO.setSeatCode(seat.getSeatCode());
        }
        // 填充区域名称
        Area area = areaMapper.getById(orders.getAreaId());
        if (area != null) {
            orderVO.setAreaName(area.getName());
        }
        // 填充用户姓名
        User user = userMapper.getById(orders.getUserId());
        if (user != null) {
            orderVO.setUserName(user.getName());
        }
        return orderVO;
    }

    /**
     * 超时未支付自动取消
     */
    @Override
    @Transactional
    public void autoCancel(Long orderId) {
        Orders orders = ordersMapper.getById(orderId);
        if (orders != null && orders.getStatus().equals(Orders.PENDING_PAYMENT)) {
            orders.setStatus(Orders.CANCELLED);
            orders.setCancelReason("超时未支付自动取消");
            orders.setCancelTime(LocalDateTime.now());
            ordersMapper.update(orders);
        }
    }

    /**
     * 预约开始未签到自动过期
     */
    @Override
    @Transactional
    public void autoExpire(Long orderId) {
        Orders orders = ordersMapper.getById(orderId);
        if (orders != null && orders.getStatus().equals(Orders.RESERVED)) {
            orders.setStatus(Orders.EXPIRED);
            ordersMapper.update(orders);
        }
    }

    /**
     * 预约结束自动完成
     */
    @Override
    @Transactional
    public void autoComplete(Long orderId) {
        Orders orders = ordersMapper.getById(orderId);
        if (orders != null && (orders.getStatus().equals(Orders.IN_USE) || orders.getStatus().equals(Orders.RESERVED))) {
            orders.setStatus(Orders.COMPLETED);
            ordersMapper.update(orders);
        }
    }

    /**
     * 统计所有订单数量
     */
    @Override
    public Integer countAll() {
        return ordersMapper.countAll();
    }

    /**
     * 根据状态统计订单数量
     */
    @Override
    public Integer countByStatus(Integer status) {
        return ordersMapper.countByStatus(status);
    }

    // =================== 私有方法 ===================

    /**
     * 计算预约总金额
     */
    private BigDecimal calculateAmount(OrdersSubmitDTO dto, Seat seat) {
        // 如果使用套餐，返回套餐价格
        if (dto.getPackageId() != null) {
            ReservationPackage pkg = reservationPackageMapper.getById(dto.getPackageId());
            if (pkg != null) {
                return pkg.getPackagePrice();
            }
        }
        // 按时长计费：小时单价 x 小时数
        long hours = Duration.between(dto.getStartTime(), dto.getEndTime()).toHours();
        if (hours < 1) hours = 1; // 不足1小时按1小时计费
        return seat.getHourPrice().multiply(BigDecimal.valueOf(hours));
    }

    /**
     * 生成预约订单号
     */
    private String generateOrderNumber() {
        return String.valueOf(System.currentTimeMillis()) + ThreadLocalRandom.current().nextInt(1000, 9999);
    }

    /**
     * 生成6位签到码
     */
    private String generateCheckinCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
    }

}
