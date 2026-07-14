<template>
  <view class="success-page">
    <!-- 成功图标 -->
    <view class="success-header">
      <view class="icon-circle">
        <text class="check-mark">✓</text>
      </view>
      <text class="success-title">预约成功</text>
      <text class="success-subtitle">请妥善保管签到码，按时到店签到</text>
    </view>

    <!-- 签到码展示 -->
    <view class="checkin-code-box">
      <text class="code-label">签到码</text>
      <view class="code-display">{{ checkinCode }}</view>
      <text class="code-hint">请到自习室后输入此签到码完成签到</text>
      <view class="copy-btn" @tap="copyCode">
        <text class="copy-text">复制签到码</text>
      </view>
    </view>

    <!-- 订单信息 -->
    <view class="card order-info-card">
      <view class="info-row">
        <text class="label">订单编号</text>
        <text class="value">{{ orderNumber }}</text>
      </view>
    </view>

    <!-- 操作按钮 -->
    <view class="action-buttons">
      <button class="btn-primary" @tap="goCheckin">去签到</button>
      <button class="btn-outline" @tap="goOrderList">查看我的预约</button>
      <button class="btn-text" @tap="goHome">返回首页</button>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      orderId: null,
      checkinCode: '',
      orderNumber: ''
    }
  },
  onLoad(options) {
    this.orderId = Number(options.orderId)
    this.checkinCode = decodeURIComponent(options.checkinCode || '')
    this.orderNumber = decodeURIComponent(options.orderNumber || '')
  },
  methods: {
    copyCode() {
      uni.setClipboardData({
        data: String(this.checkinCode),
        success: () => {
          uni.showToast({
            title: '签到码已复制',
            icon: 'success'
          })
        }
      })
    },
    goCheckin() {
      uni.navigateTo({
        url: `/pages/checkin/checkin?orderId=${this.orderId}`
      })
    },
    goOrderList() {
      uni.navigateTo({
        url: '/pages/order/order-list'
      })
    },
    goHome() {
      uni.switchTab({
        url: '/pages/home/home'
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.success-page {
  padding: 20rpx;
  min-height: 100vh;
  background: linear-gradient(180deg, #4dba87 0%, #4dba87 320rpx, #f5f5f5 320rpx, #f5f5f5 100%);
}

/* 成功头部 */
.success-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 50rpx 0 40rpx;
}

.icon-circle {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.15);
  margin-bottom: 30rpx;
}

.check-mark {
  font-size: 72rpx;
  color: #4dba87;
  font-weight: bold;
}

.success-title {
  font-size: 40rpx;
  font-weight: bold;
  color: #fff;
  margin-bottom: 12rpx;
}

.success-subtitle {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.85);
}

/* 签到码展示 */
.checkin-code-box {
  background: #fff;
  border-radius: 24rpx;
  padding: 50rpx 30rpx;
  margin: 0 0 24rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 4rpx 24rpx rgba(0, 0, 0, 0.08);
  border: 2rpx solid #4dba87;
}

.code-label {
  font-size: 28rpx;
  color: #999;
  margin-bottom: 20rpx;
}

.code-display {
  font-size: 96rpx;
  font-weight: bold;
  color: #4dba87;
  letter-spacing: 16rpx;
  padding: 20rpx 40rpx;
  background: rgba(77, 186, 135, 0.08);
  border-radius: 16rpx;
  margin-bottom: 20rpx;
  text-align: center;
  min-width: 400rpx;
}

.code-hint {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 24rpx;
  text-align: center;
}

.copy-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 12rpx 36rpx;
  background: rgba(77, 186, 135, 0.1);
  border-radius: 30rpx;
}

.copy-text {
  font-size: 26rpx;
  color: #4dba87;
}

/* 订单信息卡片 */
.order-info-card {
  margin-bottom: 40rpx;
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

/* 操作按钮 */
.action-buttons {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20rpx;
  padding: 0 30rpx;
}

.btn-primary,
.btn-outline,
.btn-text {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  border-radius: 44rpx;
  font-size: 32rpx;
  font-weight: bold;
  border: none;
  text-align: center;

  &::after {
    border: none;
  }
}

.btn-primary {
  background: #4dba87;
  color: #fff;
}

.btn-outline {
  background: #fff;
  color: #4dba87;
  border: 2rpx solid #4dba87;
}

.btn-text {
  background: transparent;
  color: #999;
  font-weight: normal;
  font-size: 28rpx;
}
</style>
