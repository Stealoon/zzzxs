package com.studyroom.controller.admin;

import com.studyroom.dto.ReservationPackageDTO;
import com.studyroom.dto.ReservationPackagePageQueryDTO;
import com.studyroom.result.PageResult;
import com.studyroom.result.Result;
import com.studyroom.service.ReservationPackageService;
import com.studyroom.vo.ReservationPackageVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/package")
@Api(tags = "预约套餐管理接口")
@Slf4j
public class ReservationPackageController {

    @Autowired
    private ReservationPackageService reservationPackageService;

    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(ReservationPackagePageQueryDTO reservationPackagePageQueryDTO) {
        log.info("套餐分页查询：{}", reservationPackagePageQueryDTO);
        PageResult pageResult = reservationPackageService.pageQuery(reservationPackagePageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @ApiOperation("新增套餐")
    public Result save(@RequestBody ReservationPackageDTO reservationPackageDTO) {
        log.info("新增套餐：{}", reservationPackageDTO);
        reservationPackageService.save(reservationPackageDTO);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody ReservationPackageDTO reservationPackageDTO) {
        log.info("修改套餐：{}", reservationPackageDTO);
        reservationPackageService.update(reservationPackageDTO);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除套餐")
    public Result delete(@RequestParam("ids") Long id) {
        log.info("删除套餐：{}", id);
        reservationPackageService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<ReservationPackageVO> getById(@PathVariable Long id) {
        log.info("根据id查询套餐：{}", id);
        ReservationPackageVO reservationPackageVO = reservationPackageService.getVOById(id);
        return Result.success(reservationPackageVO);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用套餐")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("启用禁用套餐：{},{}", status, id);
        reservationPackageService.startOrStop(status, id);
        return Result.success();
    }
}
