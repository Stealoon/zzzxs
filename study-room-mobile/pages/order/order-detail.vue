<template>
  <view class="detail-page">
    <!-- 加载中 -->
    <view v-if="loading" class="state-tip">
      <text>加载中...</text>
    </view>

    <view v-else-if="order" class="detail-content">
      <!-- 顶部状态展示 -->
      <view class="status-header" :style="{ background: statusInfo.color }">
        <text class="status-text">{{ statusInfo.text }}</text>
        <text class="status-sub">{{ statusSubText }}</text>
      </view>

      <!-- 签到码卡片（待签到状态且存在签到码） -->
      <view v-if="order.checkinCode && order.status === 2" class="checkin-code-card">
        <text class="code-label">签到码</text>
        <view class="code-display">
          <text v-for="(c, i) in checkinCodeArr" :key="i" class="code-char">{{ c }}</text>
        </view>
        <text class="code-tip">请在座位旁扫码或向工作人员出示此签到码</text>
      </view>

      <!-- 座位信息卡片 -->
      <view class="info-card">
        <view class="card-title-row">
          <text class="card-icon">🪑</text>
          <text class="card-title">座位信息</text>
        </view>
        <view class="info-row">
          <text class="info-label">座位编号</text>
          <text class="info-value">{{ order.seatCode || '—' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">所在区域</text>
          <text class="info-value">{{ order.areaName || '—' }}</text>
        </view>
      </view>

      <!-- 预约时段卡片 -->
      <view class="info-card">
        <view class="card-title-row">
          <text class="card-icon">🕐</text>
          <text class="card-title">预约时段</text>
        </view>
        <view class="info-row">
          <text class="info-label">开始时间</text>
          <text class="info-value">{{ formatDateTime(order.startTime) }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">结束时间</text>
          <text class="info-value">{{ formatDateTime(order.endTime) }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">时长</text>
          <text class="info-value">{{ formatDuration(order.startTime, order.endTime) }}</text>
        </view>
      </view>

      <!-- 订单信息卡片 -->
      <view class="info-card">
        <view class="card-title-row">
          <text class="card-icon">📄</text>
          <text class="card-title">订单信息</text>
        </view>
        <view class="info-row">
          <text class="info-label">订单编号</text>
          <text class="info-value">{{ order.number || '—' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">下单时间</text>
          <text class="info-value">{{ formatDateTime(order.orderTime) || '—' }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">订单金额</text>
          <text class="info-value price">{{ formatPrice(order.amount) }}</text>
        </view>
        <view class="info-row">
          <text class="info-label">支付方式</text>
          <text class="info-value">{{ payMethodText }}</text>
        </view>
        <view v-if="order.checkinTime" class="info-row">
          <text class="info-label">签到时间</text>
          <text class="info-value">{{ formatDateTime(order.checkinTime) }}</text>
        </view>
        <view v-if="order.checkoutTime" class="info-row">
          <text class="info-label">完成时间</text>
          <text class="info-value">{{ formatDateTime(order.checkoutTime) }}</text>
        </view>
      </view>

      <!-- 取消信息卡片（已取消/已过期且有取消信息） -->
      <view v-if="order.cancelTime || order.cancelReason" class="info-card">
        <view class="card-title-row">
          <text class="card-icon">⚠️</text>
          <text class="card-title">取消信息</text>
        </view>
        <view v-if="order.cancelTime" class="info-row">
          <text class="info-label">取消时间</text>
          <text class="info-value">{{ formatDateTime(order.cancelTime) }}</text>
        </view>
        <view v-if="order.cancelReason" class="info-row">
          <text class="info-label">取消原因</text>
          <text class="info-value">{{ order.cancelReason }}</text>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else class="state-tip">
      <text>订单信息不存在</text>
    </view>

    <!-- 底部操作栏 -->
    <view v-if="order" class="bottom-bar">
      <!-- 待支付：去支付 -->
      <button v-if="order.status === 1" class="bar-btn primary" @tap="goPay">去支付</button>

      <!-- 待签到：去签到 + 取消预约 -->
      <template v-else-if="order.status === 2">
        <button class="bar-btn ghost" @tap="handleCancel">取消预约</button>
        <button class="bar-btn primary" @tap="goCheckin">去签到</button>
      </template>
    </view>
  </view>
</template>

<script>
import orderApi from '@/api/order'
import { ORDER_STATUS, PAY_METHOD } from '@/utils/constants'
import { formatPrice, formatDateTime, formatDuration, getOrderStatusInfo } from '@/utils/format'

export default {
  name: 'OrderDetail',
  data() {
    return {
      orderId: null,
      order: null,
      loading: true,
      isLoaded: false // 标记是否已完成首次加载
    }
  },
  computed: {
    statusInfo() {
      if (!this.order) return { text: '', color: '#999999' }
      return getOrderStatusInfo(this.order.status)
    },
    statusSubText() {
      if (!this.order) return ''
      const map = {
        1: '请尽快完成支付',
        2: '请在规定时间内完成签到',
        3: '使用中，祝您学习愉快',
        4: '本次预约已完成',
        5: '预约已取消',
        6: '预约已过期'
      }
      return map[this.order.status] || ''
    },
    payMethodText() {
      if (!this.order) return '—'
      if (this.order.payMethod === PAY_METHOD.WECHAT) return '微信支付'
      if (this.order.payMethod === PAY_METHOD.ALIPAY) return '支付宝'
      return '—'
    },
    checkinCodeArr() {
      const code = (this.order && this.order.checkinCode) ? String(this.order.checkinCode) : ''
      return code.split('')
    }
  },
  onLoad(options) {
    this.orderId = options.id
    this.loadDetail()
  },
  onShow() {
    // 从签到/支付返回时刷新（跳过首次进入时的重复加载）
    if (this.orderId && this.isLoaded) {
      this.loadDetail()
    }
  },
  methods: {
    formatPrice,
    formatDateTime,
    formatDuration,

    /** 加载详情 */
    async loadDetail() {
      this.loading = true
      try {
        const res = await orderApi.getDetail(this.orderId)
        this.order = res
      } catch (e) {
        // request.js 已自动 toast 错误
      } finally {
        this.loading = false
        this.isLoaded = true
      }
    },

    /** 去支付 */
    goPay() {
      uni.navigateTo({
        url: `/pages/reservation/pay?orderNumber=${this.order.number}&orderAmount=${this.order.amount}&orderId=${this.order.id}`
      })
    },

    /** 去签到 */
    goCheckin() {
      uni.navigateTo({ url: `/pages/checkin/checkin?orderId=${this.order.id}` })
    },

    /** 取消预约（二次确认） */
    handleCancel() {
      uni.showModal({
        title: '取消预约',
        content: '确定要取消该预约吗？',
        editable: true,
        placeholderText: '请输入取消原因（可选）',
        success: async (res) => {
          if (res.confirm) {
            try {
              await orderApi.cancel({ id: this.order.id, cancelReason: res.content || '' })
              uni.showToast({ title: '取消成功', icon: 'success' })
              this.loadDetail()
            } catch (e) {
              // request.js 已自动 toast 错误
            }
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.detail-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 160rpx;
}

.state-tip {
  padding: 200rpx 0;
  text-align: center;
  color: #999999;
  font-size: 28rpx;
}

.detail-content {
  padding: 24rpx;
}

/* 顶部状态区 */
.status-header {
  width: 100%;
  border-radius: 20rpx;
  padding: 48rpx 32rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 24rpx;

  .status-text {
    font-size: 44rpx;
    font-weight: bold;
    color: #ffffff;
  }

  .status-sub {
    font-size: 26rpx;
    color: rgba(255, 255, 255, 0.9);
    margin-top: 12rpx;
  }
}

/* 签到码卡片 */
.checkin-code-card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 36rpx 28rpx;
  margin-bottom: 24rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);

  .code-label {
    font-size: 28rpx;
    color: #999999;
    margin-bottom: 24rpx;
  }

  .code-display {
    display: flex;
    justify-content: center;
    gap: 20rpx;
    margin-bottom: 20rpx;

    .code-char {
      width: 72rpx;
      height: 88rpx;
      line-height: 88rpx;
      text-align: center;
      font-size: 40rpx;
      font-weight: bold;
      color: #4DBA87;
      background: #f0faf6;
      border: 2rpx solid #4DBA87;
      border-radius: 12rpx;
    }
  }

  .code-tip {
    font-size: 24rpx;
    color: #bbbbbb;
  }
}

/* 信息卡片 */
.info-card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.card-title-row {
  display: flex;
  align-items: center;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f2f2f2;
  margin-bottom: 8rpx;

  .card-icon {
    font-size: 32rpx;
    margin-right: 12rpx;
  }

  .card-title {
    font-size: 30rpx;
    font-weight: 600;
    color: #333333;
  }
}

.info-row {
  display: flex;
  align-items: flex-start;
  padding: 16rpx 0;

  .info-label {
    width: 160rpx;
    font-size: 26rpx;
    color: #999999;
    flex-shrink: 0;
  }

  .info-value {
    flex: 1;
    font-size: 28rpx;
    color: #333333;
    word-break: break-all;

    &.price {
      color: #ff6600;
      font-weight: 600;
    }
  }
}

/* 底部操作栏 */
.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 24rpx;
  padding: 20rpx 32rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background: #ffffff;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.06);
}

.bar-btn {
  min-width: 220rpx;
  height: 80rpx;
  line-height: 80rpx;
  font-size: 30rpx;
  border-radius: 40rpx;
  margin: 0;
  border: none;

  &::after {
    border: none;
  }

  &.primary {
    background: #4DBA87;
    color: #ffffff;
  }

  &.ghost {
    background: #ffffff;
    color: #666666;
    border: 2rpx solid #dddddd;
  }
}
</style>
