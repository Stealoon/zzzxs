package com.studyroom.controller.admin;

import com.studyroom.dto.AreaDTO;
import com.studyroom.dto.AreaPageQueryDTO;
import com.studyroom.entity.Area;
import com.studyroom.result.PageResult;
import com.studyroom.result.Result;
import com.studyroom.service.AreaService;
import com.studyroom.vo.AreaVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/area")
@Api(tags = "区域管理接口")
@Slf4j
public class AreaController {

    @Autowired
    private AreaService areaService;

    @GetMapping("/page")
    @ApiOperation("区域分页查询")
    public Result<PageResult> page(AreaPageQueryDTO areaPageQueryDTO) {
        log.info("区域分页查询：{}", areaPageQueryDTO);
        PageResult pageResult = areaService.pageQuery(areaPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @ApiOperation("新增区域")
    public Result save(@RequestBody AreaDTO areaDTO) {
        log.info("新增区域：{}", areaDTO);
        areaService.save(areaDTO);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改区域")
    public Result update(@RequestBody AreaDTO areaDTO) {
        log.info("修改区域：{}", areaDTO);
        areaService.update(areaDTO);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除区域")
    public Result delete(Long id) {
        log.info("删除区域：{}", id);
        areaService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询区域")
    public Result<Area> getById(@PathVariable Long id) {
        log.info("根据id查询区域：{}", id);
        Area area = areaService.getById(id);
        return Result.success(area);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用区域")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("启用禁用区域：{},{}", status, id);
        areaService.startOrStop(status, id);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查询启用区域列表")
    public Result<List<Area>> list() {
        log.info("查询启用区域列表");
        List<Area> list = areaService.listEnabled();
        return Result.success(list);
    }
}
