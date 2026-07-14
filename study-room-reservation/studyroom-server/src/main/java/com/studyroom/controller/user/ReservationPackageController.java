package com.studyroom.controller.user;

import com.studyroom.entity.ReservationPackage;
import com.studyroom.result.Result;
import com.studyroom.service.ReservationPackageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userReservationPackageController")
@RequestMapping("/user/package")
@Api(tags = "用户端预约套餐接口")
@Slf4j
public class ReservationPackageController {

    @Autowired
    private ReservationPackageService reservationPackageService;

    @GetMapping("/list")
    @ApiOperation("查询预约套餐列表")
    public Result<List<ReservationPackage>> list() {
        log.info("查询预约套餐列表");
        List<ReservationPackage> list = reservationPackageService.listEnabled();
        return Result.success(list);
    }
}
