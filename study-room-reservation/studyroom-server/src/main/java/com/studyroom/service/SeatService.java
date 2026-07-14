package com.studyroom.service;

import com.studyroom.dto.SeatDTO;
import com.studyroom.dto.SeatPageQueryDTO;
import com.studyroom.entity.Seat;
import com.studyroom.result.PageResult;
import com.studyroom.vo.SeatQueryVO;
import com.studyroom.vo.SeatVO;
import java.util.List;

public interface SeatService {
    PageResult pageQuery(SeatPageQueryDTO seatPageQueryDTO);
    void save(SeatDTO seatDTO);
    void update(SeatDTO seatDTO);
    void deleteById(Long id);
    SeatVO getVOById(Long id);
    SeatQueryVO getQueryVOById(Long id);
    void startOrStop(Integer status, Long id);
    List<Seat> listByAreaId(Long areaId);
    List<Seat> listEnabled();
    List<SeatVO> listEnabledVO();
}
