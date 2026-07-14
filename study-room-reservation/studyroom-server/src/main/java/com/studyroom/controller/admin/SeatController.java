package com.studyroom.controller.admin;

import com.studyroom.dto.SeatDTO;
import com.studyroom.dto.SeatPageQueryDTO;
import com.studyroom.entity.Seat;
import com.studyroom.result.PageResult;
import com.studyroom.result.Result;
import com.studyroom.service.SeatService;
import com.studyroom.vo.SeatVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/seat")
@Api(tags = "座位管理接口")
@Slf4j
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping("/page")
    @ApiOperation("座位分页查询")
    public Result<PageResult> page(SeatPageQueryDTO seatPageQueryDTO) {
        log.info("座位分页查询：{}", seatPageQueryDTO);
        PageResult pageResult = seatService.pageQuery(seatPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping
    @ApiOperation("新增座位")
    public Result save(@RequestBody SeatDTO seatDTO) {
        log.info("新增座位：{}", seatDTO);
        seatService.save(seatDTO);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改座位")
    public Result update(@RequestBody SeatDTO seatDTO) {
        log.info("修改座位：{}", seatDTO);
        seatService.update(seatDTO);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除座位")
    public Result delete(@RequestParam("ids") Long id) {
        log.info("删除座位：{}", id);
        seatService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据id查询座位")
    public Result<SeatVO> getById(@PathVariable Long id) {
        log.info("根据id查询座位：{}", id);
        SeatVO seatVO = seatService.getVOById(id);
        return Result.success(seatVO);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用禁用座位")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("启用禁用座位：{},{}", status, id);
        seatService.startOrStop(status, id);
        return Result.success();
    }

    @GetMapping("/list")
    @ApiOperation("查询启用座位列表")
    public Result<List<SeatVO>> list() {
        log.info("查询启用座位列表");
        List<SeatVO> list = seatService.listEnabledVO();
        return Result.success(list);
    }
}
