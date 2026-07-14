package com.studyroom.controller.user;

import com.studyroom.entity.Seat;
import com.studyroom.result.Result;
import com.studyroom.service.SeatService;
import com.studyroom.vo.SeatQueryVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userSeatController")
@RequestMapping("/user/seat")
@Api(tags = "用户端座位接口")
@Slf4j
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping("/list")
    @ApiOperation("查询座位列表")
    public Result<List<Seat>> list(Long areaId) {
        log.info("查询座位列表，区域ID：{}", areaId);
        List<Seat> list = seatService.listByAreaId(areaId);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    @ApiOperation("查询座位详情")
    public Result<SeatQueryVO> getById(@PathVariable Long id) {
        log.info("查询座位详情：{}", id);
        SeatQueryVO seatQueryVO = seatService.getQueryVOById(id);
        return Result.success(seatQueryVO);
    }
}
