/**
 * 本地存储封装
 * 统一封装 uni.setStorageSync / getStorageSync / removeStorageSync
 */

/**
 * 获取本地存储数据
 * @param {string} key - 存储键名
 * @returns {any} 存储的值，不存在时返回 null
 */
export function get(key) {
  try {
    const value = uni.getStorageSync(key)
    if (value === '' || value === undefined || value === null) {
      return null
    }
    return value
  } catch (e) {
    console.error('读取本地存储失败:', key, e)
    return null
  }
}

/**
 * 设置本地存储数据
 * @param {string} key - 存储键名
 * @param {any} value - 存储的值
 */
export function set(key, value) {
  try {
    uni.setStorageSync(key, value)
  } catch (e) {
    console.error('写入本地存储失败:', key, e)
  }
}

/**
 * 移除本地存储数据
 * @param {string} key - 存储键名
 */
export function remove(key) {
  try {
    uni.removeStorageSync(key)
  } catch (e) {
    console.error('移除本地存储失败:', key, e)
  }
}

/**
 * 清除所有本地存储
 */
export function clear() {
  try {
    uni.clearStorageSync()
  } catch (e) {
    console.error('清除本地存储失败:', e)
  }
}

export default {
  get,
  set,
  remove,
  clear
}
