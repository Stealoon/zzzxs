package com.studyroom.controller.user;

import com.studyroom.entity.Area;
import com.studyroom.result.Result;
import com.studyroom.service.AreaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userAreaController")
@RequestMapping("/user/area")
@Api(tags = "用户端区域接口")
@Slf4j
public class AreaController {

    @Autowired
    private AreaService areaService;

    @GetMapping("/list")
    @ApiOperation("查询区域列表")
    public Result<List<Area>> list() {
        log.info("查询区域列表");
        List<Area> list = areaService.listEnabled();
        return Result.success(list);
    }
}
