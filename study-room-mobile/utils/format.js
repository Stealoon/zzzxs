/**
 * 格式化工具函数
 */

import { BASE_URL, ORDER_STATUS } from './constants'

/**
 * 拼接图片完整URL
 * 如果path已包含http前缀则直接返回，否则拼接BASE_URL
 * @param {string} path - 图片路径
 * @returns {string} 完整的图片URL
 */
export function formatImageurl(path) {
  if (!path) return ''
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path
  }
  // 确保路径以 / 开头，避免拼接时缺少分隔符
  if (!path.startsWith('/')) {
    path = '/' + path
  }
  return BASE_URL + path
}

/**
 * 格式化金额
 * @param {number|string} amount - 金额数值
 * @returns {string} 格式化后的金额，如 "¥99.00"
 */
export function formatPrice(amount) {
  if (amount === null || amount === undefined || amount === '') {
    return '¥0.00'
  }
  const num = parseFloat(amount)
  if (isNaN(num)) {
    return '¥0.00'
  }
  return '¥' + num.toFixed(2)
}

/**
 * 补零函数
 * @param {number} n - 数字
 * @returns {string} 两位补零字符串
 */
function padZero(n) {
  return n < 10 ? '0' + n : '' + n
}

/**
 * 将日期对象或字符串转换为Date对象
 * 支持 Date对象、"yyyy-MM-dd HH:mm:ss"、"yyyy-MM-dd HH:mm" 等格式
 * @param {Date|string} dt - 日期
 * @returns {Date} Date对象
 */
function toDate(dt) {
  if (!dt) return null
  if (dt instanceof Date) return dt
  // 处理字符串格式
  const str = String(dt).replace(/-/g, '/').replace('T', ' ')
  const date = new Date(str)
  return isNaN(date.getTime()) ? null : date
}

/**
 * 格式化日期时间 "yyyy-MM-dd HH:mm"
 * @param {Date|string} dt - 日期
 * @returns {string} 格式化后的日期时间
 */
export function formatDateTime(dt) {
  const date = toDate(dt)
  if (!date) return ''
  const y = date.getFullYear()
  const m = padZero(date.getMonth() + 1)
  const d = padZero(date.getDate())
  const h = padZero(date.getHours())
  const min = padZero(date.getMinutes())
  return `${y}-${m}-${d} ${h}:${min}`
}

/**
 * 格式化日期 "yyyy-MM-dd"
 * @param {Date|string} dt - 日期
 * @returns {string} 格式化后的日期
 */
export function formatDate(dt) {
  const date = toDate(dt)
  if (!date) return ''
  const y = date.getFullYear()
  const m = padZero(date.getMonth() + 1)
  const d = padZero(date.getDate())
  return `${y}-${m}-${d}`
}

/**
 * 格式化时间 "HH:mm"
 * @param {Date|string} dt - 日期
 * @returns {string} 格式化后的时间
 */
export function formatTime(dt) {
  const date = toDate(dt)
  if (!date) return ''
  const h = padZero(date.getHours())
  const min = padZero(date.getMinutes())
  return `${h}:${min}`
}

/**
 * 根据订单状态值获取状态信息
 * @param {number} status - 订单状态码
 * @returns {{text: string, color: string}} 状态文字和颜色
 */
export function getOrderStatusInfo(status) {
  const statusMap = {
    1: ORDER_STATUS.PENDING_PAYMENT,
    2: ORDER_STATUS.RESERVED,
    3: ORDER_STATUS.IN_USE,
    4: ORDER_STATUS.COMPLETED,
    5: ORDER_STATUS.CANCELLED,
    6: ORDER_STATUS.EXPIRED
  }
  const info = statusMap[status]
  if (info) {
    return { text: info.text, color: info.color }
  }
  return { text: '未知状态', color: '#999999' }
}

/**
 * 计算时长描述
 * @param {Date|string} startTime - 开始时间
 * @param {Date|string} endTime - 结束时间
 * @returns {string} 时长描述，如 "2小时" 或 "1小时30分钟"
 */
export function formatDuration(startTime, endTime) {
  const start = toDate(startTime)
  const end = toDate(endTime)
  if (!start || !end) return ''
  const diffMs = end.getTime() - start.getTime()
  if (diffMs <= 0) return '0小时'
  const totalMinutes = Math.floor(diffMs / (1000 * 60))
  const hours = Math.floor(totalMinutes / 60)
  const minutes = totalMinutes % 60
  if (hours === 0) {
    return `${minutes}分钟`
  }
  if (minutes === 0) {
    return `${hours}小时`
  }
  return `${hours}小时${minutes}分钟`
}

export default {
  formatImageurl,
  formatPrice,
  formatDateTime,
  formatDate,
  formatTime,
  getOrderStatusInfo,
  formatDuration
}
