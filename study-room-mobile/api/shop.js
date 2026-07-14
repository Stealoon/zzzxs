/**
 * 场馆状态API
 */

import request from '@/utils/request'

export default {
  /**
   * 获取场馆开放状态
   * @returns {Promise<number>} 状态值：1=开放中，0=休息中
   */
  getStatus() {
    return request.get('/user/shop/status')
  }
}
