package com.studyroom.service;

import com.studyroom.dto.AreaDTO;
import com.studyroom.dto.AreaPageQueryDTO;
import com.studyroom.entity.Area;
import com.studyroom.result.PageResult;
import com.studyroom.vo.AreaVO;
import java.util.List;

public interface AreaService {
    PageResult pageQuery(AreaPageQueryDTO areaPageQueryDTO);
    void save(AreaDTO areaDTO);
    void update(AreaDTO areaDTO);
    void deleteById(Long id);
    Area getById(Long id);
    void startOrStop(Integer status, Long id);
    List<Area> listEnabled();
}
