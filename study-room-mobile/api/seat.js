/**
 * 座位API
 */

import request from '@/utils/request'

export default {
  /**
   * 查询指定区域下的座位列表
   * @param {number} areaId - 区域ID
   * @returns {Promise<Array>} 座位列表
   */
  getList(areaId) {
    return request.get('/user/seat/list', { areaId })
  },

  /**
   * 查询座位详情
   * @param {number} id - 座位ID
   * @returns {Promise<Object>} 座位详情（含区域名称）
   */
  getDetail(id) {
    return request.get(`/user/seat/${id}`)
  }
}
