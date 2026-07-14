package com.studyroom.constant;

/**
 * 信息提示常量类
 */
public class MessageConstant {

    public static final String PASSWORD_ERROR = "密码错误";
    public static final String ACCOUNT_NOT_FOUND = "账号不存在";
    public static final String ACCOUNT_LOCKED = "账号被锁定";
    public static final String ACCOUNT_EXISTS = "账号已存在";
    public static final String UNKNOWN_ERROR = "未知错误";
    public static final String USER_NOT_LOGIN = "用户未登录";
    public static final String LOGIN_FAILED = "登录失败";
    public static final String UPLOAD_FAILED = "文件上传失败";
    public static final String PASSWORD_EDIT_FAILED = "密码修改失败";

    // 区域相关
    public static final String AREA_BE_RELATED_BY_SEAT = "当前区域关联了座位，不能删除";
    public static final String AREA_BE_RELATED_BY_PACKAGE = "当前区域关联了预约套餐，不能删除";
    public static final String AREA_NOT_FOUND = "区域不存在";

    // 座位相关
    public static final String SEAT_NOT_FOUND = "座位不存在";
    public static final String SEAT_NOT_AVAILABLE = "座位不存在或已停用";
    public static final String SEAT_ON_SALE = "启用中的座位不能删除";
    public static final String SEAT_TIME_CONFLICT = "该座位当前时段已被预约，请选择其他时段或座位";

    // 预约套餐相关
    public static final String PACKAGE_ENABLE_FAILED = "套餐关联的区域已停用，无法启售";
    public static final String PACKAGE_ON_SALE = "启用中的套餐不能删除";
    public static final String PACKAGE_NOT_FOUND = "预约套餐不存在";

    // 预约订单相关
    public static final String ORDER_STATUS_ERROR = "预约订单状态错误";
    public static final String ORDER_NOT_FOUND = "预约订单不存在";
    public static final String ORDER_NOT_CANCELLABLE = "当前预约状态不可取消";
    public static final String CHECKIN_TIME_INVALID = "当前不在签到时段内，请在预约开始前15分钟至开始后30分钟内签到";
    public static final String CHECKIN_CODE_ERROR = "签到码不正确";
    public static final String SEAT_DISABLED = "座位已停用";
}
