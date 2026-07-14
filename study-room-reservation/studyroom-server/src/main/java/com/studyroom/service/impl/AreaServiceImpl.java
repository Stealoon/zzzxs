package com.studyroom.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.studyroom.dto.AreaDTO;
import com.studyroom.dto.AreaPageQueryDTO;
import com.studyroom.entity.Area;
import com.studyroom.exception.DeletionNotAllowedException;
import com.studyroom.mapper.AreaMapper;
import com.studyroom.mapper.SeatMapper;
import com.studyroom.result.PageResult;
import com.studyroom.service.AreaService;
import com.studyroom.vo.AreaVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 区域管理Service实现类
 * 负责自习室区域的增删改查、启停及关联校验
 */
@Service
public class AreaServiceImpl implements AreaService {

    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private SeatMapper seatMapper;

    /**
     * 区域分页查询
     * 根据区域名称进行模糊检索，返回分页结果
     *
     * @param areaPageQueryDTO 分页查询参数（含页码、每页条数、区域名称）
     * @return 分页结果
     */
    @Override
    public PageResult pageQuery(AreaPageQueryDTO areaPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(areaPageQueryDTO.getPage(), areaPageQueryDTO.getPageSize());

        //mapper返回List，PageHelper会将其包装为Page
        List<AreaVO> list = areaMapper.pageQuery(areaPageQueryDTO.getName());
        Page<AreaVO> page = (Page<AreaVO>) list;

        long total = page.getTotal();
        List<AreaVO> records = page.getResult();

        return new PageResult(total, records);
    }

    /**
     * 新增区域
     * 将DTO属性复制到实体，设置默认启用状态后插入
     *
     * @param areaDTO 区域信息
     */
    @Override
    public void save(AreaDTO areaDTO) {
        Area area = new Area();

        //对象属性拷贝
        BeanUtils.copyProperties(areaDTO, area);

        //设置区域状态，1表示启用，0表示停用
        area.setStatus(1);

        areaMapper.insert(area);
    }

    /**
     * 编辑区域
     * 将DTO属性复制到实体后进行动态更新
     *
     * @param areaDTO 区域信息
     */
    @Override
    public void update(AreaDTO areaDTO) {
        Area area = new Area();

        //对象属性拷贝
        BeanUtils.copyProperties(areaDTO, area);

        areaMapper.update(area);
    }

    /**
     * 根据ID删除区域
     * 删除前校验该区域下是否关联了座位，若存在则不允许删除
     *
     * @param id 区域ID
     */
    @Override
    public void deleteById(Long id) {
        //校验该区域下是否有关联的座位
        int count = seatMapper.countByAreaId(id);
        if (count > 0) {
            //区域下存在座位，不允许删除
            throw new DeletionNotAllowedException("该区域下存在座位，不允许删除");
        }

        areaMapper.deleteById(id);
    }

    /**
     * 根据ID查询区域
     *
     * @param id 区域ID
     * @return 区域实体
     */
    @Override
    public Area getById(Long id) {
        return areaMapper.getById(id);
    }

    /**
     * 启用或停用区域
     * 通过仅更新id和status字段实现启停
     *
     * @param status 状态：0停用 1启用
     * @param id     区域ID
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        Area area = new Area();
        area.setStatus(status);
        area.setId(id);

        areaMapper.update(area);
    }

    /**
     * 查询全量启用区域
     * 供用户端展示可选区域列表使用
     *
     * @return 启用状态的区域列表
     */
    @Override
    public List<Area> listEnabled() {
        return areaMapper.listEnabled();
    }
}
