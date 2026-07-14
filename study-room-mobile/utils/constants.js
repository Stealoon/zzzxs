/**
 * 全局常量定义
 */

// 订单状态：1待支付 2待签到(已预约) 3使用中 4已完成 5已取消 6已过期
export const ORDER_STATUS = {
  PENDING_PAYMENT: { value: 1, text: '待支付', color: '#ff9900' },
  RESERVED: { value: 2, text: '待签到', color: '#4DBA87' },
  IN_USE: { value: 3, text: '使用中', color: '#0099ff' },
  COMPLETED: { value: 4, text: '已完成', color: '#999999' },
  CANCELLED: { value: 5, text: '已取消', color: '#cccccc' },
  EXPIRED: { value: 6, text: '已过期', color: '#ff6600' }
}

// 支付方式
export const PAY_METHOD = {
  WECHAT: 1,
  ALIPAY: 2
}

// 支付状态
export const PAY_STATUS = {
  UN_PAID: 0,
  PAID: 1
}

// 座位状态
export const SEAT_STATUS = {
  DISABLED: 0,
  ENABLED: 1
}

// 场馆状态
export const SHOP_STATUS = {
  CLOSED: 0,
  OPEN: 1
}

// API基础路径
export const BASE_URL = 'http://localhost:8080'

// 不需要认证的路径（请求拦截器排除列表）
export const NO_AUTH_PATHS = [
  '/user/user/login',
  '/user/user/loginByPassword',
  '/user/user/register',
  '/user/shop/status'
]

// 本地存储键名
export const STORAGE_KEYS = {
  TOKEN: 'token',
  USER_ID: 'userId',
  USER_INFO: 'userInfo'
}

// 分页默认参数
export const PAGE_DEFAULTS = {
  PAGE: 1,
  PAGE_SIZE: 10
}
