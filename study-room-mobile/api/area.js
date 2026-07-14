/**
 * 区域API
 */

import request from '@/utils/request'

export default {
  /**
   * 查询区域列表
   * @returns {Promise<Array>} 启用状态的区域列表
   */
  getList() {
    return request.get('/user/area/list')
  }
}
