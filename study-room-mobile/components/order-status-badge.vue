<template>
  <view
    class="order-status-badge"
    :style="{ backgroundColor: statusInfo.color + '20', color: statusInfo.color, borderColor: statusInfo.color + '40' }"
  >
    {{ statusInfo.text }}
  </view>
</template>

<script>
import { ORDER_STATUS } from '@/utils/constants'

export default {
  name: 'OrderStatusBadge',
  props: {
    /**
     * 订单状态值
     * 1=待支付 2=待签到 3=使用中 4=已完成 5=已取消 6=已过期
     */
    status: {
      type: Number,
      default: 0
    }
  },
  computed: {
    statusInfo() {
      const statusMap = {
        1: ORDER_STATUS.PENDING_PAYMENT,
        2: ORDER_STATUS.RESERVED,
        3: ORDER_STATUS.IN_USE,
        4: ORDER_STATUS.COMPLETED,
        5: ORDER_STATUS.CANCELLED,
        6: ORDER_STATUS.EXPIRED
      }
      const info = statusMap[this.status]
      if (info) {
        return { text: info.text, color: info.color }
      }
      return { text: '未知', color: '#999999' }
    }
  }
}
</script>

<style lang="scss" scoped>
.order-status-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 4rpx 16rpx;
  border-radius: 8rpx;
  font-size: 24rpx;
  line-height: 1.4;
  border: 1rpx solid transparent;
  white-space: nowrap;
}
</style>
