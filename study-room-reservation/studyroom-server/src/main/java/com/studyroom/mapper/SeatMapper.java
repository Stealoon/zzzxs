package com.studyroom.mapper;

import com.github.pagehelper.Page;
import com.studyroom.annotation.AutoFill;
import com.studyroom.dto.SeatPageQueryDTO;
import com.studyroom.entity.Orders;
import com.studyroom.entity.Seat;
import com.studyroom.enumeration.OperationType;
import com.studyroom.vo.SeatVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface SeatMapper {

    // 插入座位
    @AutoFill(OperationType.INSERT)
    void insert(Seat seat);

    // 根据ID查询座位
    @Select("select * from seat where id = #{id}")
    Seat getById(Long id);

    // 根据ID查询座位VO（联表查区域名称）
    SeatVO getVOById(Long id);

    // 分页查询座位（联表查区域名称）
    List<SeatVO> pageQuery(SeatPageQueryDTO seatPageQueryDTO);

    // 分页查询计数
    Long count(SeatPageQueryDTO seatPageQueryDTO);

    // 根据区域ID查询启用的座位列表
    @Select("select * from seat where area_id = #{areaId} and status = 1 order by seat_code")
    List<Seat> listByAreaId(Long areaId);

    // 查询全量启用座位（用户端）
    @Select("select * from seat where status = 1 order by area_id, seat_code")
    List<Seat> listEnabled();

    // 查询全量启用座位VO（含区域名称）
    List<SeatVO> listEnabledVO();

    // 根据ID删除座位
    @Delete("delete from seat where id = #{id}")
    void deleteById(Long id);

    // 动态更新座位
    @AutoFill(OperationType.UPDATE)
    void update(Seat seat);

    // 根据区域ID统计座位数量
    @Select("select count(*) from seat where area_id = #{areaId}")
    int countByAreaId(Long areaId);

    // 查询启用的座位数量（用于判断是否可启停）
    @Select("select count(*) from seat where area_id = #{areaId} and status = 1")
    int countEnabledByAreaId(Long areaId);

    // 查询指定座位在指定时间段内的有效订单（状态为待支付、已预约、使用中）
    List<Orders> getActiveOrdersBySeatId(@Param("seatId") Long seatId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
