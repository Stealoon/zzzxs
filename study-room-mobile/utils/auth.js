/**
 * 认证管理模块
 * 负责token和userId的存取、登录状态判断、登出操作
 */

import * as storage from './storage'
import { STORAGE_KEYS } from './constants'

/**
 * 获取token
 * @returns {string|null}
 */
export function getToken() {
  return storage.get(STORAGE_KEYS.TOKEN)
}

/**
 * 保存token
 * @param {string} token - JWT令牌
 */
export function setToken(token) {
  storage.set(STORAGE_KEYS.TOKEN, token)
}

/**
 * 清除token
 */
export function removeToken() {
  storage.remove(STORAGE_KEYS.TOKEN)
}

/**
 * 获取userId
 * @returns {string|number|null}
 */
export function getUserId() {
  return storage.get(STORAGE_KEYS.USER_ID)
}

/**
 * 保存userId
 * @param {string|number} id - 用户ID
 */
export function setUserId(id) {
  storage.set(STORAGE_KEYS.USER_ID, id)
}

/**
 * 获取用户信息
 * @returns {object|null}
 */
export function getUserInfo() {
  return storage.get(STORAGE_KEYS.USER_INFO)
}

/**
 * 保存用户信息
 * @param {object} info - 用户信息对象
 */
export function setUserInfo(info) {
  storage.set(STORAGE_KEYS.USER_INFO, info)
}

/**
 * 判断是否已登录（token存在）
 * @returns {boolean}
 */
export function isLoggedIn() {
  const token = getToken()
  return !!token
}

/**
 * 清除所有认证信息（登出）
 */
export function logout() {
  removeToken()
  storage.remove(STORAGE_KEYS.USER_ID)
  storage.remove(STORAGE_KEYS.USER_INFO)
}

export default {
  getToken,
  setToken,
  removeToken,
  getUserId,
  setUserId,
  getUserInfo,
  setUserInfo,
  isLoggedIn,
  logout
}
