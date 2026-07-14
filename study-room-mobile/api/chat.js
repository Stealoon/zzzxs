/**
 * 智能客服API
 */

import request from '@/utils/request'

export default {
  /**
   * 发送消息给智能客服
   * @param {string} message - 用户消息
   * @returns {Promise<Object>} 客服回复
   */
  send(message) {
    return request.post(
      `/user/chat/send?message=${encodeURIComponent(message)}`
    )
  }
}
