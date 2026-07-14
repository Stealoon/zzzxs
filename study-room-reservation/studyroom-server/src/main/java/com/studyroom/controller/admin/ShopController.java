package com.studyroom.controller.admin;

import com.studyroom.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("adminShopController")
@RequestMapping("/admin/shop")
@Slf4j
@Api(tags = "场馆相关接口")
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 设置场馆开放状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    @ApiOperation("设置场馆开放状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置场馆开放状态为：{}",status == 1 ? "开放中" : "已闭馆");
        redisTemplate.opsForValue().set(KEY,status);
        return Result.success();
    }
    /**
     * 获取场馆开放状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取场馆开放状态")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(KEY);
        if (status == null) {
            status = 1;
        }
        log.info("获取场馆开放状态为：{}",status == 1 ? "开放中" : "已闭馆");
        return Result.success(status);
    }

}
