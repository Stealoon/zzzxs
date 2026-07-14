package com.studyroom.mapper;

import com.github.pagehelper.Page;
import com.studyroom.annotation.AutoFill;
import com.studyroom.dto.EmployeePageQueryDTO;
import com.studyroom.entity.Employee;
import com.studyroom.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);

    /**
     * 插入员工数据（适配id自增，删除id字段插入）
     * 修复点：
     * 1. 移除字段列表中的id（自增无需手动插入）
     * 2. 保证字段数（11个）和值数（11个）完全匹配
     * 3. 格式化SQL，提升可读性
     */
    @Insert("INSERT INTO employee " +
            "(name, username, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
            "VALUES " +
            "(#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    @AutoFill(OperationType.INSERT)
    void insert(Employee employee);

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    /**
     * 根据主键动态修改属性
     * @param employee
     */
    @AutoFill(OperationType.UPDATE)
    void update(Employee employee);

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @Select("select * from employee where id = #{id}")
    Employee getById(long id);
}