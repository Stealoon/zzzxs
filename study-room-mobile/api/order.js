/**
 * 订单API
 */

import request from '@/utils/request'

export default {
  /**
   * 提交预约
   * @param {Object} data - 预约数据 {seatId, startTime, endTime, packageId, payMethod}
   * @returns {Promise<Object>} 提交结果 {id, orderNumber, orderAmount, orderTime}
   */
  submit(data) {
    return request.post('/user/order/submit', data)
  },

  /**
   * 预约支付
   * @param {Object} data - 支付数据 {orderNumber, payMethod}
   * @returns {Promise<Object>} 支付结果
   */
  pay(data) {
    return request.post('/user/order/pay', data)
  },

  /**
   * 扫码签到
   * @param {number} orderId - 订单ID
   * @param {string} checkinCode - 签到核销码
   * @returns {Promise}
   */
  checkin(orderId, checkinCode) {
    return request.post(
      `/user/order/checkin?orderId=${orderId}&checkinCode=${checkinCode}`
    )
  },

  /**
   * 取消预约
   * @param {Object} data - 取消数据 {id, cancelReason}
   * @returns {Promise}
   */
  cancel(data) {
    return request.put('/user/order/cancel', data)
  },

  /**
   * 查询我的预约列表（分页）
   * @param {Object} params - 查询参数 {page, pageSize, status}
   * @returns {Promise<Object>} 分页结果 {total, records, pages}
   */
  getList(params) {
    return request.get('/user/order/list', params)
  },

  /**
   * 查询预约详情
   * @param {number} id - 订单ID
   * @returns {Promise<Object>} 订单详情（含座位编号、区域名称等）
   */
  getDetail(id) {
    return request.get(`/user/order/detail/${id}`)
  }
}
