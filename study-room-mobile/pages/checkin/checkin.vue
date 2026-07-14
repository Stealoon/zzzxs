<template>
  <view class="checkin-page">
    <!-- 顶部说明 -->
    <view class="tip-banner">
      <text class="tip-icon">⏰</text>
      <view class="tip-text-wrap">
        <text class="tip-title">签到时间提示</text>
        <text class="tip-desc">请在预约开始前15分钟至开始后30分钟内签到</text>
      </view>
    </view>

    <!-- 当前订单信息（通过orderId进入时拉取） -->
    <view v-if="orderInfo" class="order-info-card">
      <view class="order-info-row">
        <text class="info-label">座位编号</text>
        <text class="info-value">{{ orderInfo.seatCode || '—' }}</text>
      </view>
      <view class="order-info-row">
        <text class="info-label">所在区域</text>
        <text class="info-value">{{ orderInfo.areaName || '—' }}</text>
      </view>
      <view class="order-info-row">
        <text class="info-label">预约时段</text>
        <text class="info-value">{{ formatDateTime(orderInfo.startTime) }} - {{ formatTime(orderInfo.endTime) }}</text>
      </view>
      <view v-if="orderInfo.checkinCode" class="order-info-row highlight">
        <text class="info-label">签到码</text>
        <text class="info-value code">{{ orderInfo.checkinCode }}</text>
      </view>
    </view>

    <!-- 扫码签到入口 -->
    <view v-if="!orderId" class="scan-card">
      <text class="scan-title">方式一：扫码签到</text>
      <text class="scan-desc">扫描座位上的二维码，自动识别订单</text>
      <button class="scan-btn" @tap="handleScan">
        <text class="scan-icon">📷</text>
        <text class="scan-btn-text">扫码签到</text>
      </button>
    </view>

    <!-- 签到码输入区 -->
    <view class="code-input-card">
      <text v-if="orderId" class="code-input-title">请输入签到码</text>
      <text v-else class="code-input-title">扫码后请输入签到码</text>

      <!-- 6位签到码小方框展示 -->
      <view class="code-boxes" @tap="focusInput">
        <view
          v-for="(item, index) in 6"
          :key="index"
          class="code-box"
          :class="{ active: index === codeArr.length, filled: index < codeArr.length }"
        >
          <text class="code-char">{{ codeArr[index] || '' }}</text>
        </view>
      </view>

      <!-- 隐藏input接收输入 -->
      <input
        ref="codeInput"
        class="hidden-input"
        type="number"
        :value="codeInput"
        maxlength="6"
        :focus="inputFocus"
        @input="onCodeInput"
        @blur="inputFocus = false"
      />

      <text class="input-hint">请输入6位数字签到码</text>
    </view>

    <!-- 订单ID提示（扫码获得） -->
    <view v-if="orderId && !orderInfo" class="order-id-tip">
      <text>当前订单ID：{{ orderId }}</text>
    </view>

    <!-- 确认签到按钮 -->
    <button
      class="confirm-btn"
      :loading="submitting"
      :disabled="submitting || !orderId || codeArr.length < 6"
      @tap="handleCheckin"
    >
      {{ submitting ? '签到中...' : '确认签到' }}
    </button>
  </view>
</template>

<script>
import orderApi from '@/api/order'
import { formatDateTime, formatTime } from '@/utils/format'

export default {
  name: 'Checkin',
  data() {
    return {
      orderId: null,
      orderInfo: null,
      codeInput: '',
      submitting: false,
      inputFocus: false
    }
  },
  computed: {
    codeArr() {
      return this.codeInput.split('')
    }
  },
  onLoad(options) {
    if (options.orderId) {
      this.orderId = options.orderId
      this.loadOrderInfo()
      // 已知订单时自动聚焦输入框
      this.$nextTick(() => {
        this.inputFocus = true
      })
    }
  },
  methods: {
    formatDateTime,
    formatTime,

    /** 拉取订单详情，用于展示信息及签到码提示 */
    async loadOrderInfo() {
      try {
        const res = await orderApi.getDetail(this.orderId)
        this.orderInfo = res
      } catch (e) {
        // request.js 已自动 toast 错误
      }
    },

    /** 扫码签到 */
    handleScan() {
      uni.scanCode({
        scanType: ['qrCode'],
        success: (res) => {
          // 尝试解析二维码内容
          let orderId = null
          try {
            const parsed = JSON.parse(res.result)
            orderId = parsed.orderId || parsed.id
          } catch (e) {
            orderId = res.result // 纯数字orderId
          }
          if (orderId) {
            this.orderId = orderId
            this.codeInput = ''
            this.loadOrderInfo()
            uni.showToast({ title: '扫码成功，请输入签到码', icon: 'none' })
            this.$nextTick(() => {
              this.inputFocus = true
            })
          } else {
            uni.showToast({ title: '无法识别二维码内容', icon: 'none' })
          }
        },
        fail: () => {
          uni.showToast({ title: '扫码取消或失败', icon: 'none' })
        }
      })
    },

    /** 签到码输入处理，限制6位数字 */
    onCodeInput(e) {
      let val = (e.detail.value || '').replace(/\D/g, '').slice(0, 6)
      this.codeInput = val
    },

    /** 聚焦隐藏输入框 */
    focusInput() {
      this.inputFocus = true
    },

    /** 确认签到 */
    async handleCheckin() {
      if (!this.orderId) {
        uni.showToast({ title: '请先扫码或选择订单', icon: 'none' })
        return
      }
      const code = this.codeInput
      if (code.length < 6) {
        uni.showToast({ title: '请输入6位签到码', icon: 'none' })
        return
      }
      this.submitting = true
      try {
        await orderApi.checkin(this.orderId, code)
        uni.showToast({ title: '签到成功', icon: 'success' })
        setTimeout(() => {
          uni.redirectTo({ url: `/pages/order/order-detail?id=${this.orderId}` })
        }, 800)
      } catch (e) {
        // request.js 已自动 toast 错误（如"签到码错误"、"不在签到时间窗口内"等）
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.checkin-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding: 24rpx;
}

/* 时间提示 */
.tip-banner {
  display: flex;
  align-items: center;
  background: #fff8e6;
  border-radius: 16rpx;
  padding: 24rpx 28rpx;
  margin-bottom: 24rpx;

  .tip-icon {
    font-size: 40rpx;
    margin-right: 20rpx;
    flex-shrink: 0;
  }

  .tip-text-wrap {
    display: flex;
    flex-direction: column;
    flex: 1;
  }

  .tip-title {
    font-size: 28rpx;
    font-weight: 600;
    color: #ff9900;
    margin-bottom: 8rpx;
  }

  .tip-desc {
    font-size: 24rpx;
    color: #b88500;
    line-height: 1.5;
  }
}

/* 订单信息卡片 */
.order-info-card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 28rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.order-info-row {
  display: flex;
  align-items: center;
  padding: 12rpx 0;

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

    &.code {
      color: #4DBA87;
      font-weight: bold;
      font-size: 36rpx;
      letter-spacing: 8rpx;
    }
  }

  &.highlight {
    padding: 16rpx 20rpx;
    margin-top: 8rpx;
    background: #f0faf6;
    border-radius: 12rpx;
  }
}

/* 扫码卡片 */
.scan-card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 40rpx 28rpx;
  margin-bottom: 24rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);

  .scan-title {
    font-size: 30rpx;
    font-weight: 600;
    color: #333333;
    margin-bottom: 12rpx;
  }

  .scan-desc {
    font-size: 24rpx;
    color: #999999;
    margin-bottom: 32rpx;
  }
}

.scan-btn {
  width: 80%;
  height: 88rpx;
  line-height: 88rpx;
  background: #4DBA87;
  color: #ffffff;
  font-size: 30rpx;
  border-radius: 44rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;

  &::after {
    border: none;
  }

  .scan-icon {
    font-size: 36rpx;
    margin-right: 12rpx;
  }
}

/* 签到码输入卡片 */
.code-input-card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 40rpx 28rpx;
  margin-bottom: 24rpx;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);

  .code-input-title {
    font-size: 30rpx;
    font-weight: 600;
    color: #333333;
    margin-bottom: 36rpx;
  }

  .input-hint {
    font-size: 24rpx;
    color: #bbbbbb;
    margin-top: 24rpx;
  }
}

/* 6位签到码方框 */
.code-boxes {
  display: flex;
  justify-content: center;
  gap: 20rpx;
}

.code-box {
  width: 80rpx;
  height: 96rpx;
  border: 2rpx solid #e0e0e0;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  transition: all 0.2s;

  &.active {
    border-color: #4DBA87;
    background: #f0faf6;
  }

  &.filled {
    border-color: #4DBA87;
    background: #ffffff;
  }

  .code-char {
    font-size: 40rpx;
    font-weight: bold;
    color: #333333;
  }
}

.hidden-input {
  position: absolute;
  left: -9999rpx;
  width: 1rpx;
  height: 1rpx;
  opacity: 0;
}

/* 订单ID提示 */
.order-id-tip {
  text-align: center;
  font-size: 24rpx;
  color: #999999;
  margin-bottom: 24rpx;
}

/* 确认签到按钮 */
.confirm-btn {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  background: #4DBA87;
  color: #ffffff;
  font-size: 32rpx;
  font-weight: bold;
  border-radius: 48rpx;
  margin-top: 20rpx;
  border: none;

  &::after {
    border: none;
  }

  &[disabled] {
    background: #a0d8c0;
    color: rgba(255, 255, 255, 0.8);
  }
}
</style>
