/**
 * 核心请求封装
 * 基于 uni.request 封装，支持请求/响应拦截器
 */

import { BASE_URL, NO_AUTH_PATHS } from './constants'
import { getToken, removeToken } from './auth'

/**
 * 发起请求的核心函数
 * @param {Object} options - 请求配置
 * @param {string} options.url - 接口路径（不含baseUrl）
 * @param {string} options.method - 请求方法 GET/POST/PUT/DELETE
 * @param {Object} options.data - 请求体数据（POST/PUT）
 * @param {Object} options.params - URL query参数（GET/DELETE）
 * @returns {Promise} resolve(responseData) / reject(Error)
 */
function request(options) {
  const { url, method = 'GET', data, params } = options
  let fullUrl = BASE_URL + url

  // 处理query参数
  if (params && Object.keys(params).length > 0) {
    const queryStr = Object.entries(params)
      .filter(([_, v]) => v !== undefined && v !== null && v !== '')
      .map(([k, v]) => `${k}=${encodeURIComponent(v)}`)
      .join('&')
    if (queryStr) {
      fullUrl += (fullUrl.includes('?') ? '&' : '?') + queryStr
    }
  }

  // 判断是否需要注入认证头
  const needAuth = !NO_AUTH_PATHS.some((path) => url.includes(path))
  const header = { 'Content-Type': 'application/json' }
  if (needAuth) {
    const token = getToken()
    if (token) {
      header['authentication'] = token
    }
  }

  return new Promise((resolve, reject) => {
    uni.request({
      url: fullUrl,
      method: method.toUpperCase(),
      data,
      header,
      success: (res) => {
        if (res.statusCode === 200) {
          const body = res.data
          // 后端约定：code===1 为成功，返回 data 字段
          if (body.code === 1) {
            resolve(body.data)
          } else {
            uni.showToast({
              title: body.msg || '操作失败',
              icon: 'none'
            })
            reject(new Error(body.msg || '操作失败'))
          }
        } else if (res.statusCode === 401) {
          // 登录过期，清除token并跳转登录页
          removeToken()
          uni.showToast({
            title: '登录已过期，请重新登录',
            icon: 'none'
          })
          setTimeout(() => {
            uni.navigateTo({ url: '/pages/login/login' })
          }, 500)
          reject(new Error('登录已过期'))
        } else {
          uni.showToast({
            title: '网络错误',
            icon: 'none'
          })
          reject(new Error('网络错误'))
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络连接失败',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

/**
 * 导出带便捷方法的request对象
 */
export default {
  /**
   * GET请求
   * @param {string} url - 接口路径
   * @param {Object} params - query参数
   * @returns {Promise}
   */
  get(url, params) {
    return request({ url, method: 'GET', params })
  },

  /**
   * POST请求
   * @param {string} url - 接口路径
   * @param {Object} data - 请求体
   * @returns {Promise}
   */
  post(url, data) {
    return request({ url, method: 'POST', data })
  },

  /**
   * PUT请求
   * @param {string} url - 接口路径
   * @param {Object} data - 请求体
   * @returns {Promise}
   */
  put(url, data) {
    return request({ url, method: 'PUT', data })
  },

  /**
   * DELETE请求
   * @param {string} url - 接口路径
   * @param {Object} params - query参数
   * @returns {Promise}
   */
  delete(url, params) {
    return request({ url, method: 'DELETE', params })
  }
}
