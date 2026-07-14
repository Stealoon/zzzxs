package com.studyroom.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.studyroom.constant.JwtClaimsConstant;
import com.studyroom.constant.MessageConstant;
import com.studyroom.dto.UserLoginByPasswordDTO;
import com.studyroom.dto.UserLoginDTO;
import com.studyroom.dto.UserRegisterDTO;
import com.studyroom.dto.UserUpdateDTO;
import com.studyroom.entity.User;
import com.studyroom.exception.LoginFailedException;
import com.studyroom.mapper.UserMapper;
import com.studyroom.properties.JwtProperties;
import com.studyroom.properties.WeChatProperties;
import com.studyroom.service.UserService;
import com.studyroom.utils.HttpClientUtil;
import com.studyroom.utils.JwtUtil;
import com.studyroom.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class UserServiceImpl implements UserService {
    //微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProrerties;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtProperties jwtProperties;
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    public User wxLogin(UserLoginDTO userLoginDTO) {

        String openid = getOpinid(userLoginDTO.getCode());

        //判断openid是否为空 空则登录失败 抛出异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断是否为新用户 若不是则自动注册
        User user = userMapper.getByOpenid(openid);
        if(user == null){
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        //返回用户对象
        return user;
    }

    /**
     * 手机号+密码登录
     * @param userLoginByPasswordDTO
     * @return
     */
    public User loginByPassword(UserLoginByPasswordDTO userLoginByPasswordDTO) {
        String phone = userLoginByPasswordDTO.getPhone();
        String password = userLoginByPasswordDTO.getPassword();

        //根据手机号查询用户
        User user = userMapper.getByPhone(phone);

        //判断用户是否存在
        if (user == null) {
            throw new LoginFailedException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码MD5加密后比对
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!md5Password.equals(user.getPassword())) {
            throw new LoginFailedException(MessageConstant.PASSWORD_ERROR);
        }

        return user;
    }

    /**
     * 更新用户信息
     * @param userUpdateDTO
     */
    public void update(UserUpdateDTO userUpdateDTO) {
        User user = new User();
        BeanUtils.copyProperties(userUpdateDTO, user);
        userMapper.update(user);
    }

    /**
     * 根据ID查询用户
     * @param id
     * @return
     */
    public User getById(Long id) {
        return userMapper.getById(id);
    }

    /**
     * 用户注册
     * @param userRegisterDTO
     * @return
     */
    public UserLoginVO register(UserRegisterDTO userRegisterDTO) {
        String phone = userRegisterDTO.getPhone();
        String password = userRegisterDTO.getPassword();

        //检查手机号是否已注册
        User existingUser = userMapper.getByPhone(phone);
        if (existingUser != null) {
            throw new LoginFailedException(MessageConstant.ACCOUNT_EXISTS);
        }

        //密码MD5加密
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

        //昵称：有则用传入的，无则默认"新用户"
        String name = (userRegisterDTO.getName() != null && !userRegisterDTO.getName().trim().isEmpty())
                ? userRegisterDTO.getName() : "新用户";
        //openid：用 phone_ + 手机号 生成
        String openid = "phone_" + phone;

        User user = User.builder()
                .phone(phone)
                .password(md5Password)
                .name(name)
                .openid(openid)
                .createTime(LocalDateTime.now())
                .build();

        userMapper.insert(user);

        //生成JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims
        );

        return UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .name(user.getName())
                .token(token)
                .build();
    }

    private String getOpinid(String code) {
        //调用微信接口服务 获得当前微信用户的openid
        Map<String, String> map = new HashMap<>();
        map.put("appid",weChatProrerties.getAppid());
        map.put("secret",weChatProrerties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN,map);

        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
