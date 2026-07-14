package com.studyroom.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.studyroom.dto.SeatDTO;
import com.studyroom.dto.SeatPageQueryDTO;
import com.studyroom.entity.Orders;
import com.studyroom.entity.Seat;
import com.studyroom.mapper.SeatMapper;
import com.studyroom.result.PageResult;
import com.studyroom.service.SeatService;
import com.studyroom.vo.SeatQueryVO;
import com.studyroom.vo.SeatVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 座位管理Service实现类
 * 负责自习室座位的增删改查、启停及按区域查询
 */
@Service
public class SeatServiceImpl implements SeatService {

    @Autowired
    private SeatMapper seatMapper;

    /**
     * 座位分页查询
     * 支持按座位编号模糊检索、按区域和状态筛选，返回分页结果
     *
     * @param seatPageQueryDTO 分页查询参数（含页码、每页条数、座位编号、区域ID、状态）
     * @return 分页结果
     */
    @Override
    public PageResult pageQuery(SeatPageQueryDTO seatPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(seatPageQueryDTO.getPage(), seatPageQueryDTO.getPageSize());

        //mapper返回List，PageHelper会将其包装为Page
        List<SeatVO> list = seatMapper.pageQuery(seatPageQueryDTO);
        Page<SeatVO> page = (Page<SeatVO>) list;

        long total = page.getTotal();
        List<SeatVO> records = page.getResult();

        return new PageResult(total, records);
    }

    /**
     * 新增座位
     * 将DTO属性复制到实体，设置默认启用状态后插入
     *
     * @param seatDTO 座位信息
     */
    @Override
    public void save(SeatDTO seatDTO) {
        Seat seat = new Seat();

        //对象属性拷贝
        BeanUtils.copyProperties(seatDTO, seat);
        
        //映射前端image字段到seatImage
        if (seatDTO.getImage() != null) {
            seat.setSeatImage(seatDTO.getImage());
        }

        //设置区域ID（前端传递的是字符串类型）
        if (seatDTO.getAreaId() != null && !seatDTO.getAreaId().isEmpty()) {
            seat.setAreaId(Long.parseLong(seatDTO.getAreaId()));
        }

        //设置座位状态，1表示启用，0表示停用
        seat.setStatus(1);

        //设置默认值
        if (seat.getHasSocket() == null) {
            seat.setHasSocket(0);
        }
        if (seat.getHasWindow() == null) {
            seat.setHasWindow(0);
        }

        seatMapper.insert(seat);
    }

    /**
     * 编辑座位
     * 将DTO属性复制到实体后进行动态更新
     *
     * @param seatDTO 座位信息
     */
    @Override
    public void update(SeatDTO seatDTO) {
        Seat seat = new Seat();

        //对象属性拷贝
        BeanUtils.copyProperties(seatDTO, seat);
        
        //映射前端image字段到seatImage
        if (seatDTO.getImage() != null) {
            seat.setSeatImage(seatDTO.getImage());
        }

        //设置区域ID（前端传递的是字符串类型）
        if (seatDTO.getAreaId() != null && !seatDTO.getAreaId().isEmpty()) {
            seat.setAreaId(Long.parseLong(seatDTO.getAreaId()));
        }

        //设置默认值，防止数据库非空约束报错
        if (seat.getHasSocket() == null) {
            seat.setHasSocket(0);
        }
        if (seat.getHasWindow() == null) {
            seat.setHasWindow(0);
        }

        seatMapper.update(seat);
    }

    /**
     * 根据ID删除座位
     *
     * @param id 座位ID
     */
    @Override
    public void deleteById(Long id) {
        seatMapper.deleteById(id);
    }

    /**
     * 根据ID查询座位（联表查询区域名称）
     * 返回包含区域名称的VO，供管理端详情展示使用
     *
     * @param id 座位ID
     * @return 座位VO
     */
    @Override
    public SeatVO getVOById(Long id) {
        return seatMapper.getVOById(id);
    }

    /**
     * 根据ID查询座位详情（含当日时段占用状态）
     * 生成当天07:00-22:00的2小时时段列表，并检查每个时段是否被有效订单占用
     *
     * @param id 座位ID
     * @return 含时段占用状态的座位查询VO
     */
    @Override
    public SeatQueryVO getQueryVOById(Long id) {
        //1. 查询座位基本信息（联表查区域名称）
        SeatVO seatVO = seatMapper.getVOById(id);

        //2. 构建当天的时间范围（07:00 ~ 22:00）
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime dayStart = today.with(LocalTime.of(7, 0));
        LocalDateTime dayEnd = today.with(LocalTime.of(22, 0));

        //3. 查询该座位当天的有效订单（状态为待支付、已预约、使用中）
        List<Orders> activeOrders = seatMapper.getActiveOrdersBySeatId(id, dayStart, dayEnd);
        if (activeOrders == null) {
            activeOrders = new ArrayList<>();
        }

        //4. 生成当天从07:00到22:00的2小时为一个时段的时段列表
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<SeatQueryVO.TimeSlotVO> timeSlots = new ArrayList<>();

        LocalDateTime slotStart = dayStart;
        while (slotStart.isBefore(dayEnd)) {
            LocalDateTime slotEnd = slotStart.plusHours(2);
            if (slotEnd.isAfter(dayEnd)) {
                slotEnd = dayEnd;
            }

            //对每个时段，检查是否有订单与之时间重叠
            //两个时间段重叠的条件：slotStart < orderEndTime && orderStartTime < slotEnd
            boolean available = true;
            for (Orders order : activeOrders) {
                if (slotStart.isBefore(order.getEndTime()) && order.getStartTime().isBefore(slotEnd)) {
                    available = false;
                    break;
                }
            }

            SeatQueryVO.TimeSlotVO timeSlot = SeatQueryVO.TimeSlotVO.builder()
                    .startTime(slotStart.format(formatter))
                    .endTime(slotEnd.format(formatter))
                    .available(available)
                    .build();
            timeSlots.add(timeSlot);

            slotStart = slotEnd;
        }

        //5. 组装 SeatQueryVO 并返回
        SeatQueryVO seatQueryVO = new SeatQueryVO();
        BeanUtils.copyProperties(seatVO, seatQueryVO);
        //SeatVO中图片字段为image，SeatQueryVO中为seatImage，需手动映射
        seatQueryVO.setSeatImage(seatVO.getImage());
        seatQueryVO.setTimeSlots(timeSlots);

        return seatQueryVO;
    }

    /**
     * 启用或停用座位
     * 通过仅更新id和status字段实现启停
     *
     * @param status 状态：0停用 1启用
     * @param id     座位ID
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Seat seat = new Seat();
        seat.setStatus(status);
        seat.setId(id);

        seatMapper.update(seat);
    }

    /**
     * 根据区域ID查询启用座位列表
     * 供用户端选择某区域下可用座位使用
     *
     * @param areaId 区域ID
     * @return 该区域下启用状态的座位列表
     */
    @Override
    public List<Seat> listByAreaId(Long areaId) {
        return seatMapper.listByAreaId(areaId);
    }

    /**
     * 查询全量启用座位
     * 供用户端展示所有可选座位使用
     *
     * @return 启用状态的座位列表
     */
    @Override
    public List<Seat> listEnabled() {
        return seatMapper.listEnabled();
    }

    @Override
    public List<SeatVO> listEnabledVO() {
        return seatMapper.listEnabledVO();
    }
}
