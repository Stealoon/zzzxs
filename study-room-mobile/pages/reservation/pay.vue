<template>
  <view class="pay-page">
    <!-- 金额展示 -->
    <view class="amount-section">
      <text class="amount-label">应付金额</text>
      <text class="amount-display">{{ formatPrice(orderAmount) }}</text>
    </view>

    <!-- 订单信息 -->
    <view class="card">
      <view class="section-title">订单信息</view>
      <view class="info-row">
        <text class="label">订单编号</text>
        <text class="value">{{ orderNumber }}</text>
      </view>
    </view>

    <!-- 支付方式 -->
    <view class="card">
      <view class="section-title">支付方式</view>
      <view class="pay-method" @tap="selectPayMethod(PAY_METHOD.WECHAT)">
        <view class="method-left">
          <view class="method-icon wechat-icon">
            <text class="icon-text">微</text>
          </view>
          <view class="method-info">
            <text class="method-name">微信支付</text>
            <text class="method-desc">推荐使用</text>
          </view>
        </view>
        <view class="radio" :class="{ checked: payMethod === PAY_METHOD.WECHAT }">
          <view v-if="payMethod === PAY_METHOD.WECHAT" class="radio-dot"></view>
        </view>
      </view>
    </view>

    <!-- 安全提示 -->
    <view class="security-tip">
      <text class="tip-text">🔒 支付环境安全加密，请放心支付</text>
    </view>

    <!-- 底部栏 -->
    <view class="bottom-bar">
      <view class="bottom-amount">
        <text class="bottom-label">需付</text>
        <text class="bottom-price">{{ formatPrice(orderAmount) }}</text>
      </view>
      <button
        class="pay-btn"
        :loading="paying"
        :disabled="paying"
        @tap="handlePay"
      >
        {{ paying ? '支付中' : '确认支付' }}
      </button>
    </view>
  </view>
</template>

<script>
import orderApi from '@/api/order'
import { formatPrice } from '@/utils/format'
import { PAY_METHOD } from '@/utils/constants'

export default {
  data() {
    return {
      orderNumber: '',
      orderAmount: 0,
      orderId: null,
      payMethod: PAY_METHOD.WECHAT,
      paying: false
    }
  },
  onLoad(options) {
    this.orderNumber = decodeURIComponent(options.orderNumber || '')
    this.orderAmount = parseFloat(options.orderAmount) || 0
    this.orderId = Number(options.orderId)
  },
  methods: {
    formatPrice,
    PAY_METHOD,
    selectPayMethod(method) {
      this.payMethod = method
    },
    async handlePay() {
      if (this.paying) return
      this.paying = true
      try {
        const res = await orderApi.pay({
          orderNumber: this.orderNumber,
          payMethod: this.payMethod
        })
        uni.redirectTo({
          url: `/pages/reservation/success?orderId=${this.orderId}&checkinCode=${encodeURIComponent(res.checkinCode)}&orderNumber=${encodeURIComponent(this.orderNumber)}`
        })
      } catch (e) {
        console.error('支付失败', e)
      } finally {
        this.paying = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.pay-page {
  padding: 20rpx;
  padding-bottom: 140rpx;
  min-height: 100vh;
}

/* 金额展示 */
.amount-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60rpx 0 50rpx;
}

.amount-label {
  font-size: 28rpx;
  color: #999;
  margin-bottom: 16rpx;
}

.amount-display {
  font-size: 72rpx;
  font-weight: bold;
  color: #333;
}

/* 卡片通用 */
.section-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 20rpx;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
}

.label {
  font-size: 28rpx;
  color: #999;
}

.value {
  font-size: 28rpx;
  color: #333;
  word-break: break-all;
  text-align: right;
  max-width: 60%;
}

/* 支付方式 */
.pay-method {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24rpx 0;
}

.method-left {
  display: flex;
  align-items: center;
}

.method-icon {
  width: 72rpx;
  height: 72rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20rpx;
}

.wechat-icon {
  background: #07c160;
}

.icon-text {
  font-size: 36rpx;
  color: #fff;
  font-weight: bold;
}

.method-info {
  display: flex;
  flex-direction: column;
}

.method-name {
  font-size: 30rpx;
  color: #333;
  font-weight: 500;
}

.method-desc {
  font-size: 22rpx;
  color: #999;
  margin-top: 4rpx;
}

.radio {
  width: 40rpx;
  height: 40rpx;
  border: 2rpx solid #ddd;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;

  &.checked {
    border-color: #4dba87;
    background: #4dba87;
  }
}

.radio-dot {
  width: 20rpx;
  height: 20rpx;
  border-radius: 50%;
  background: #fff;
}

/* 安全提示 */
.security-tip {
  text-align: center;
  padding: 30rpx 0;
}

.tip-text {
  font-size: 24rpx;
  color: #bbb;
}

/* 底部栏 */
.bottom-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background: #fff;
  box-shadow: 0 -2rpx 12rpx rgba(0, 0, 0, 0.06);
  z-index: 100;
}

.bottom-amount {
  display: flex;
  flex-direction: column;
}

.bottom-label {
  font-size: 24rpx;
  color: #999;
}

.bottom-price {
  font-size: 40rpx;
  font-weight: bold;
  color: #4dba87;
}

.pay-btn {
  flex: 1;
  max-width: 60%;
  height: 88rpx;
  line-height: 88rpx;
  background: #4dba87;
  color: #fff;
  font-size: 32rpx;
  font-weight: bold;
  border-radius: 44rpx;
  border: none;

  &::after {
    border: none;
  }

  &[disabled] {
    background: #cccccc;
    color: #fff;
  }
}
</style>
