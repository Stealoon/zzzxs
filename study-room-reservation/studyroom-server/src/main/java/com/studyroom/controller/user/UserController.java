package com.studyroom.controller.user;


import com.studyroom.constant.JwtClaimsConstant;
import com.studyroom.context.BaseContext;
import com.studyroom.dto.UserLoginByPasswordDTO;
import com.studyroom.dto.UserLoginDTO;
import com.studyroom.dto.UserRegisterDTO;
import com.studyroom.dto.UserUpdateDTO;
import com.studyroom.entity.User;
import com.studyroom.properties.JwtProperties;
import com.studyroom.result.Result;
import com.studyroom.service.UserService;
import com.studyroom.utils.JwtUtil;
import com.studyroom.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Api(tags = "C端用户相关接口")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    @ApiOperation("微信登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("微信用户登录:{}",userLoginDTO.getCode());

        //微信登录
        User user = userService.wxLogin(userLoginDTO);

        //为微信用户生成jwt令牌
        Map<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(),claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .name(user.getName())
                .token(token)
                .build();

        return Result.success(userLoginVO);

    }

    /**
     * 用户注册
     * @param userRegisterDTO
     * @return
     */
    @PostMapping("/register")
    @ApiOperation("用户注册")
    public Result<UserLoginVO> register(@RequestBody @Valid UserRegisterDTO userRegisterDTO){
        log.info("用户注册:{}", userRegisterDTO.getPhone());

        UserLoginVO userLoginVO = userService.register(userRegisterDTO);

        return Result.success(userLoginVO);
    }

    /**
     * 手机号+密码登录
     * @param userLoginByPasswordDTO
     * @return
     */
    @PostMapping("/loginByPassword")
    @ApiOperation("手机号密码登录")
    public Result<UserLoginVO> loginByPassword(@RequestBody UserLoginByPasswordDTO userLoginByPasswordDTO){
        log.info("手机号密码登录:{}", userLoginByPasswordDTO.getPhone());

        //手机号密码登录
        User user = userService.loginByPassword(userLoginByPasswordDTO);

        //为用户生成jwt令牌
        Map<String,Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID,user.getId());
        String token = JwtUtil.createJWT(jwtProperties.getUserSecretKey(),jwtProperties.getUserTtl(),claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .name(user.getName())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

    /**
     * 获取当前登录用户信息
     * @return
     */
    @GetMapping("/profile")
    @ApiOperation("获取当前用户信息")
    public Result<User> getProfile() {
        Long userId = BaseContext.getCurrentId();
        User user = userService.getById(userId);
        // 清除敏感信息
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    /**
     * 更新用户信息
     * @param userUpdateDTO
     * @return
     */
    @PutMapping("/update")
    @ApiOperation("更新用户信息")
    public Result update(@RequestBody UserUpdateDTO userUpdateDTO){
        log.info("更新用户信息:{}", userUpdateDTO);

        //从JWT拦截器设置的BaseContext中获取当前用户ID，不允许前端传入任意userId
        userUpdateDTO.setId(BaseContext.getCurrentId());

        userService.update(userUpdateDTO);

        return Result.success();
    }
}
