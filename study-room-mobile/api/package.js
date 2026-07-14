/**
 * 套餐API
 */

import request from '@/utils/request'

export default {
  /**
   * 查询启用状态的套餐列表
   * @returns {Promise<Array>} 套餐列表
   */
  getList() {
    return request.get('/user/package/list')
  }
}
