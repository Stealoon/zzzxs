package com.studyroom.mapper;

import com.studyroom.annotation.AutoFill;
import com.studyroom.dto.ReservationPackagePageQueryDTO;
import com.studyroom.entity.ReservationPackage;
import com.studyroom.enumeration.OperationType;
import com.studyroom.vo.ReservationPackageVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface ReservationPackageMapper {

    // 插入预约套餐
    @AutoFill(OperationType.INSERT)
    void insert(ReservationPackage reservationPackage);

    // 根据ID查询套餐
    @Select("select * from reservation_package where id = #{id}")
    ReservationPackage getById(Long id);

    // 根据ID查询套餐VO（联表查区域名称）
    ReservationPackageVO getVOById(Long id);

    // 分页查询套餐（联表查区域名称）
    List<ReservationPackageVO> pageQuery(ReservationPackagePageQueryDTO queryDTO);

    // 分页查询计数
    Long count(ReservationPackagePageQueryDTO queryDTO);

    // 查询全量启用套餐（用户端）
    @Select("select * from reservation_package where status = 1 order by create_time desc")
    List<ReservationPackage> listEnabled();

    // 根据ID删除套餐
    @Delete("delete from reservation_package where id = #{id}")
    void deleteById(Long id);

    // 动态更新套餐
    @AutoFill(OperationType.UPDATE)
    void update(ReservationPackage reservationPackage);

    // 根据区域ID统计启用的套餐数量
    @Select("select count(*) from reservation_package where area_id = #{areaId} and status = 1")
    int countEnabledByAreaId(Long areaId);
}
