package com.studyroom.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.studyroom.dto.ReservationPackageDTO;
import com.studyroom.dto.ReservationPackagePageQueryDTO;
import com.studyroom.entity.ReservationPackage;
import com.studyroom.mapper.ReservationPackageMapper;
import com.studyroom.result.PageResult;
import com.studyroom.service.ReservationPackageService;
import com.studyroom.vo.ReservationPackageVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 预约套餐管理Service实现类
 * 负责自习室预约套餐的增删改查、启停及列表查询
 */
@Service
public class ReservationPackageServiceImpl implements ReservationPackageService {

    @Autowired
    private ReservationPackageMapper reservationPackageMapper;

    /**
     * 套餐分页查询
     * 支持按套餐名称模糊检索、按区域和状态筛选，返回分页结果
     *
     * @param reservationPackagePageQueryDTO 分页查询参数（含页码、每页条数、套餐名称、区域ID、状态）
     * @return 分页结果
     */
    @Override
    public PageResult pageQuery(ReservationPackagePageQueryDTO reservationPackagePageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(reservationPackagePageQueryDTO.getPage(), reservationPackagePageQueryDTO.getPageSize());

        //mapper返回List，PageHelper会将其包装为Page
        List<ReservationPackageVO> list = reservationPackageMapper.pageQuery(reservationPackagePageQueryDTO);
        Page<ReservationPackageVO> page = (Page<ReservationPackageVO>) list;

        long total = page.getTotal();
        List<ReservationPackageVO> records = page.getResult();

        return new PageResult(total, records);
    }

    /**
     * 新增套餐
     * 将DTO属性复制到实体，设置默认启用状态后插入
     *
     * @param reservationPackageDTO 套餐信息
     */
    @Override
    public void save(ReservationPackageDTO reservationPackageDTO) {
        ReservationPackage reservationPackage = new ReservationPackage();

        //对象属性拷贝
        BeanUtils.copyProperties(reservationPackageDTO, reservationPackage);

        //映射前端image字段到packageImage
        if (reservationPackageDTO.getImage() != null) {
            reservationPackage.setPackageImage(reservationPackageDTO.getImage());
        }

        //兼容前端categoryId字段（字符串类型）
        if (reservationPackageDTO.getAreaId() != null && !reservationPackageDTO.getAreaId().isEmpty()) {
            reservationPackage.setAreaId(Long.parseLong(reservationPackageDTO.getAreaId()));
        }

        //设置默认值，防止数据库非空约束报错（0表示全场通用）
        if (reservationPackage.getAreaId() == null) {
            reservationPackage.setAreaId(0L);
        }

        //设置套餐状态，1表示启用，0表示停用
        reservationPackage.setStatus(1);

        reservationPackageMapper.insert(reservationPackage);
    }

    /**
     * 编辑套餐
     * 将DTO属性复制到实体后进行动态更新
     *
     * @param reservationPackageDTO 套餐信息
     */
    @Override
    public void update(ReservationPackageDTO reservationPackageDTO) {
        ReservationPackage reservationPackage = new ReservationPackage();

        //对象属性拷贝
        BeanUtils.copyProperties(reservationPackageDTO, reservationPackage);

        //映射前端image字段到packageImage
        if (reservationPackageDTO.getImage() != null) {
            reservationPackage.setPackageImage(reservationPackageDTO.getImage());
        }

        //兼容前端categoryId字段（字符串类型）
        if (reservationPackageDTO.getAreaId() != null && !reservationPackageDTO.getAreaId().isEmpty()) {
            reservationPackage.setAreaId(Long.parseLong(reservationPackageDTO.getAreaId()));
        }

        //设置默认值，防止数据库非空约束报错（0表示全场通用）
        if (reservationPackage.getAreaId() == null) {
            reservationPackage.setAreaId(0L);
        }

        reservationPackageMapper.update(reservationPackage);
    }

    /**
     * 根据ID删除套餐
     *
     * @param id 套餐ID
     */
    @Override
    public void deleteById(Long id) {
        reservationPackageMapper.deleteById(id);
    }

    /**
     * 根据ID查询套餐（联表查询区域名称）
     * 返回包含区域名称的VO，供管理端详情展示使用
     *
     * @param id 套餐ID
     * @return 套餐VO
     */
    @Override
    public ReservationPackageVO getVOById(Long id) {
        return reservationPackageMapper.getVOById(id);
    }

    /**
     * 启用或停用套餐
     * 通过仅更新id和status字段实现启停
     *
     * @param status 状态：0停用 1启用
     * @param id     套餐ID
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        ReservationPackage reservationPackage = new ReservationPackage();
        reservationPackage.setStatus(status);
        reservationPackage.setId(id);

        reservationPackageMapper.update(reservationPackage);
    }

    /**
     * 查询全量启用套餐
     * 供用户端展示可选预约套餐列表使用
     *
     * @return 启用状态的套餐列表
     */
    @Override
    public List<ReservationPackage> listEnabled() {
        return reservationPackageMapper.listEnabled();
    }
}
