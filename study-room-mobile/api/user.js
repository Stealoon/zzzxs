/**
 * 用户相关API
 */

import request from '@/utils/request'
import { BASE_URL } from '@/utils/constants'

export default {
  /**
   * 微信登录
   * @param {string} code - 微信登录凭证
   * @returns {Promise} 登录结果 {id, openid, token}
   */
  login(code) {
    return request.post('/user/user/login', { code })
  },

  /**
   * 手机号密码登录
   * @param {string} phone - 手机号
   * @param {string} password - 密码
   * @returns {Promise} 登录结果
   */
  loginByPassword(phone, password) {
    return request.post('/user/user/loginByPassword', { phone, password })
  },

  /**
   * 用户注册
   * @param {Object} data - 注册信息 { name?, phone, password }
   * @returns {Promise} 注册结果 { id, openid, token, name }
   */
  register(data) {
    return request.post('/user/user/register', data)
  },

  /**
   * 更新用户信息
   * @param {Object} data - 用户信息
   * @returns {Promise}
   */
  update(data) {
    return request.put('/user/user/update', data)
  },

  /**
   * 获取当前登录用户信息
   * @returns {Promise} 用户信息 {id, name, phone, avatar, ...}
   */
  getProfile() {
    return request.get('/user/user/profile')
  },

  /**
   * 上传文件（头像等）
   * 使用 uni.uploadFile 单独封装，不走 request 拦截器
   * @param {string} filePath - 本地文件路径
   * @returns {Promise<string>} 上传后的文件URL
   */
  upload(filePath) {
    return new Promise((resolve, reject) => {
      uni.uploadFile({
        url: BASE_URL + '/user/common/upload',
        filePath: filePath,
        name: 'file',
        header: {
          authentication: uni.getStorageSync('token') || ''
        },
        success: (res) => {
          const data = JSON.parse(res.data)
          if (data.code === 1) {
            resolve(data.data || data.url)
          } else {
            reject(new Error(data.msg || '上传失败'))
          }
        },
        fail: (err) => {
          reject(err)
        }
      })
    })
  }
}
