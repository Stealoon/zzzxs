package com.studyroom.mapper;

import com.studyroom.annotation.AutoFill;
import com.studyroom.entity.Area;
import com.studyroom.enumeration.OperationType;
import com.studyroom.vo.AreaVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface AreaMapper {

    // 插入区域
    @AutoFill(OperationType.INSERT)
    void insert(Area area);

    // 根据ID查询区域
    @Select("select * from area where id = #{id}")
    Area getById(Long id);

    // 查询全量启用区域
    @Select("select * from area where status = 1 order by sort")
    List<Area> listEnabled();

    // 分页查询区域
    List<AreaVO> pageQuery(String name);

    // 根据ID删除区域
    @Delete("delete from area where id = #{id}")
    void deleteById(Long id);

    // 动态更新区域
    @AutoFill(OperationType.UPDATE)
    void update(Area area);

    // 根据区域名称查询（用于唯一性校验）
    @Select("select count(*) from area where name = #{name}")
    int countByName(String name);
}
