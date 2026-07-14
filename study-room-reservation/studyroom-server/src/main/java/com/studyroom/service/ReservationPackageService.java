package com.studyroom.service;

import com.studyroom.dto.ReservationPackageDTO;
import com.studyroom.dto.ReservationPackagePageQueryDTO;
import com.studyroom.entity.ReservationPackage;
import com.studyroom.result.PageResult;
import com.studyroom.vo.ReservationPackageVO;
import java.util.List;

public interface ReservationPackageService {
    PageResult pageQuery(ReservationPackagePageQueryDTO reservationPackagePageQueryDTO);
    void save(ReservationPackageDTO reservationPackageDTO);
    void update(ReservationPackageDTO reservationPackageDTO);
    void deleteById(Long id);
    ReservationPackageVO getVOById(Long id);
    void startOrStop(Integer status, Long id);
    List<ReservationPackage> listEnabled();
}
