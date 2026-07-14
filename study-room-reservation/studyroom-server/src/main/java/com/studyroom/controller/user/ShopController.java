package com.studyroom.controller.user;

import com.studyroom.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("userShopController")
@RequestMapping("/user/shop")
@Slf4j
@Api(tags = "场馆相关接口")
public class ShopController {

    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取场馆开放状态
     * @return
     */
    @GetMapping("/status")
    @ApiOperation("获取场馆开放状态")
    public Result<Integer> getStatus() {
        // 从Redis获取场馆状态
        Integer status = (Integer) redisTemplate.opsForValue().get("SHOP_STATUS");

        // 关键：为空时给默认值 1（开放中）
        if (status == null) {
            status = 1;
        }

        return Result.success(status);
    }

}
