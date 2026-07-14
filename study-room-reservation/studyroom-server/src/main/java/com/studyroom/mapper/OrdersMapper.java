package com.studyroom.mapper;

import com.studyroom.dto.OrdersPageQueryDTO;
import com.studyroom.entity.Orders;
import com.studyroom.vo.OrderVO;
import com.studyroom.annotation.AutoFill;
import com.studyroom.enumeration.OperationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrdersMapper {

    // 分页查询预约订单（联表查座位编号和区域名称）
    List<OrderVO> pageQuery(OrdersPageQueryDTO queryDTO);

    // 分页查询计数
    Long count(OrdersPageQueryDTO queryDTO);

    // 根据ID查询预约订单
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    // 根据订单号查询
    @Select("select * from orders where number = #{number}")
    Orders getByNumber(String number);

    // 根据状态查询订单
    @Select("select * from orders where status = #{status}")
    List<Orders> getByStatus(Integer status);

    // 查询指定时间段内的订单
    @Select("select * from orders where order_time >= #{begin} and order_time <= #{end}")
    List<Orders> getOrdersBetweenTime(LocalDateTime begin, LocalDateTime end);

    // 动态更新订单
    void update(Orders orders);

    // 插入预约订单
    void insert(Orders orders);

    // 时段冲突校验：查询指定座位在目标时段内是否存在有效订单（状态为已预约或使用中）
    @Select("SELECT COUNT(*) FROM orders WHERE seat_id = #{seatId} AND status IN (2, 3) AND start_time < #{endTime} AND end_time > #{startTime}")
    int countConflict(Long seatId, LocalDateTime startTime, LocalDateTime endTime);

    // 签到核销：更新订单状态为使用中，记录签到时间
    @Select("select * from orders where id = #{id} and checkin_code = #{checkinCode} and status = 2")
    Orders getByIdAndCheckinCode(Long id, String checkinCode);

    // 统计所有订单数量
    @Select("SELECT COUNT(*) FROM orders")
    Integer countAll();

    // 根据状态统计订单数量
    @Select("SELECT COUNT(*) FROM orders WHERE status = #{status}")
    Integer countByStatus(Integer status);

    // 查询超时未支付的订单ID（状态=1 且下单时间早于指定时间）
    @Select("SELECT id FROM orders WHERE status = 1 AND order_time < #{time}")
    List<Long> selectIdsForAutoCancel(LocalDateTime time);

    // 查询已预约但未签到且已过开始时间的订单ID（状态=2 且开始时间早于指定时间）
    @Select("SELECT id FROM orders WHERE status = 2 AND start_time < #{time}")
    List<Long> selectIdsForAutoExpire(LocalDateTime time);

    // 查询使用中且已过结束时间的订单ID（状态=3 且结束时间早于指定时间）
    @Select("SELECT id FROM orders WHERE status = 3 AND end_time < #{time}")
    List<Long> selectIdsForAutoComplete(LocalDateTime time);
}
