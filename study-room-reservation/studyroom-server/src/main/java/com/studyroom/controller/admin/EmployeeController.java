package com.studyroom.controller.admin;

import com.studyroom.constant.JwtClaimsConstant;
import com.studyroom.dto.EmployeeDTO;
import com.studyroom.dto.EmployeeLoginDTO;
import com.studyroom.dto.EmployeePageQueryDTO;
import com.studyroom.entity.Employee;
import com.studyroom.properties.JwtProperties;
import com.studyroom.result.PageResult;
import com.studyroom.result.Result;
import com.studyroom.service.EmployeeService;
import com.studyroom.utils.JwtUtil;
import com.studyroom.vo.EmployeeLoginVO;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理员管理
 */
@RestController
@RequestMapping("/admin/employee")
public class EmployeeController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("管理员登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("管理员退出")
    public Result<String> logout() {
        return Result.success();
    }

    /**
     * 新增管理员
     * @param employeeDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增管理员")
    public Result save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增管理员 {}",employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("管理员分页查询")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("管理员分页查询 参数为：{}", employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 启用禁用管理员账号
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用管理员账号")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("启用禁用管理员账号：{},{}",status,id);
        employeeService.startOrStop(status,id);
        return Result.success();
    }

    /**
     * 根据id查询管理员信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询管理员信息")
    public Result<Employee> getById(@PathVariable  long id){

        Employee employee = employeeService.getById(id);

        return Result.success(employee);
    }

    /**
     * 编辑管理员信息
     * @param employeeDTO
     * @return
     */
    @PutMapping
    @ApiOperation("编辑管理员信息")
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("编辑管理员信息：{}",employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }



}
