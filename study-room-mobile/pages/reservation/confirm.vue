<template>
  <view class="confirm-page">
    <!-- 座位信息 -->
    <view class="card">
      <view class="section-title">座位信息</view>
      <view v-if="seatLoading" class="loading-text">加载中...</view>
      <view v-else>
        <view class="info-row">
          <text class="label">区域</text>
          <text class="value">{{ seatDetail.areaName || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="label">座位编号</text>
          <text class="value">{{ seatDetail.seatCode || '-' }}</text>
        </view>
        <view class="info-row">
          <text class="label">小时单价</text>
          <text class="value text-primary">{{ formatPrice(hourPrice) }}</text>
        </view>
      </view>
    </view>

    <!-- 预约时段 -->
    <view class="card">
      <view class="section-title">预约时段</view>
      <view class="info-row">
        <text class="label">开始时间</text>
        <text class="value">{{ formatDateTime(startTime) }}</text>
      </view>
      <view class="info-row">
        <text class="label">结束时间</text>
        <text class="value">{{ formatDateTime(endTime) }}</text>
      </view>
      <view class="info-row">
        <text class="label">时长</text>
        <text class="value">{{ formatDuration(startTime, endTime) }}</text>
      </view>
    </view>

    <!-- 计费方式 -->
    <view class="card">
      <view class="section-title">计费方式</view>
      <!-- Tab切换 -->
      <view class="billing-tabs">
        <view
          class="tab-item"
          :class="{ active: billingType === 'hour' }"
          @tap="switchTab('hour')"
        >
          按时计费
        </view>
        <view
          class="tab-item"
          :class="{ active: billingType === 'package' }"
          @tap="switchTab('package')"
        >
          使用套餐
        </view>
      </view>

      <!-- 按时计费明细 -->
      <view v-if="billingType === 'hour'" class="billing-detail">
        <view class="detail-row">
          <text class="detail-label">小时单价</text>
          <text class="detail-value">{{ formatPrice(hourPrice) }}</text>
        </view>
        <view class="detail-row">
          <text class="detail-label">计费时长</text>
          <text class="detail-value">{{ hours }}小时</text>
        </view>
        <view class="detail-row total-row">
          <text class="detail-label">小计</text>
          <text class="detail-value text-primary">{{ formatPrice(hourlyAmount) }}</text>
        </view>
      </view>

      <!-- 套餐选择 -->
      <view v-else class="billing-detail">
        <view v-if="packageLoading" class="loading-text">套餐加载中...</view>
        <view v-else-if="packageList.length === 0" class="empty-text">
          暂无可用套餐
        </view>
        <view v-else class="package-list">
          <view
            v-for="pkg in packageList"
            :key="pkg.id"
            class="package-card"
            :class="{ selected: selectedPackageId === pkg.id }"
            @tap="selectPackage(pkg)"
          >
            <view class="package-left">
              <view class="package-name">{{ pkg.packageName }}</view>
              <view class="package-desc">{{ pkg.packageDesc || '暂无描述' }}</view>
              <view class="package-meta">
                <text class="meta-item">时长 {{ pkg.durationHours }}h</text>
                <text class="meta-divider">|</text>
                <text class="meta-item">有效期 {{ pkg.validDays }}天</text>
              </view>
            </view>
            <view class="package-right">
              <text class="package-price">{{ formatPrice(pkg.packagePrice) }}</text>
              <view v-if="selectedPackageId === pkg.id" class="selected-mark">✓</view>
            </view>
          </view>
        </view>
        <view v-if="selectedPackage" class="detail-row total-row">
          <text class="detail-label">小计</text>
          <text class="detail-value text-primary">
            {{ formatPrice(selectedPackage.packagePrice) }}
          </text>
        </view>
      </view>
    </view>

    <!-- 底部栏 -->
    <view class="bottom-bar">
      <view class="bottom-amount">
        <text class="bottom-label">合计</text>
        <text class="bottom-price">{{ formatPrice(totalAmount) }}</text>
      </view>
      <button
        class="submit-btn"
        :loading="submitting"
        :disabled="submitting || !canSubmit"
        @tap="handleSubmit"
      >
        {{ submitting ? '提交中' : '确认预约' }}
      </button>
    </view>
  </view>
</template>

<script>
import seatApi from '@/api/seat'
import packageApi from '@/api/package'
import orderApi from '@/api/order'
import { formatPrice, formatDateTime, formatDuration } from '@/utils/format'
import { PAY_METHOD } from '@/utils/constants'

export default {
  data() {
    return {
      seatId: null,
      areaId: null,
      startTime: '',
      endTime: '',
      hourPrice: 0,
      seatDetail: {},
      seatLoading: false,
      billingType: 'hour',
      packageList: [],
      packageLoading: false,
      selectedPackageId: null,
      submitting: false
    }
  },
  computed: {
    hours() {
      return this.calculateHours(this.startTime, this.endTime)
    },
    hourlyAmount() {
      return this.hourPrice * this.hours
    },
    selectedPackage() {
      return this.packageList.find((p) => p.id === this.selectedPackageId)
    },
    totalAmount() {
      if (this.billingType === 'hour') {
        return this.hourlyAmount
      }
      if (this.selectedPackage) {
        return this.selectedPackage.packagePrice
      }
      return 0
    },
    canSubmit() {
      if (this.billingType === 'hour') {
        return this.hours > 0
      }
      return this.selectedPackageId !== null
    }
  },
  onLoad(options) {
    this.seatId = Number(options.seatId)
    this.areaId = Number(options.areaId)
    this.startTime = decodeURIComponent(options.startTime || '')
    this.endTime = decodeURIComponent(options.endTime || '')
    this.hourPrice = parseFloat(options.hourPrice) || 0
    this.loadSeatDetail()
  },
  methods: {
    formatPrice,
    formatDateTime,
    formatDuration,
    async loadSeatDetail() {
      this.seatLoading = true
      try {
        this.seatDetail = await seatApi.getDetail(this.seatId)
      } catch (e) {
        console.error('获取座位详情失败', e)
      } finally {
        this.seatLoading = false
      }
    },
    calculateHours(startTime, endTime) {
      if (!startTime || !endTime) return 0
      const start = new Date(startTime.replace(/-/g, '/'))
      const end = new Date(endTime.replace(/-/g, '/'))
      const diffMs = end - start
      if (diffMs <= 0) return 0
      const hours = diffMs / (1000 * 60 * 60)
      return Math.ceil(hours)
    },
    switchTab(type) {
      this.billingType = type
      if (type === 'package' && this.packageList.length === 0) {
        this.loadPackageList()
      }
    },
    async loadPackageList() {
      this.packageLoading = true
      try {
        this.packageList = await packageApi.getList()
      } catch (e) {
        console.error('获取套餐列表失败', e)
      } finally {
        this.packageLoading = false
      }
    },
    selectPackage(pkg) {
      this.selectedPackageId = pkg.id
    },
    ensureSeconds(dateStr) {
      if (!dateStr) return dateStr
      if (/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/.test(dateStr)) return dateStr
      if (/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}$/.test(dateStr)) return dateStr + ':00'
      return dateStr
    },
    async handleSubmit() {
      if (this.submitting || !this.canSubmit) return
      this.submitting = true
      try {
        const data = {
          seatId: this.seatId,
          areaId: this.areaId,
          startTime: this.ensureSeconds(this.startTime),
          endTime: this.ensureSeconds(this.endTime),
          payMethod: PAY_METHOD.WECHAT
        }
        if (this.billingType === 'package' && this.selectedPackageId) {
          data.packageId = this.selectedPackageId
        }
        const res = await orderApi.submit(data)
        uni.navigateTo({
          url: `/pages/reservation/pay?orderNumber=${res.orderNumber}&orderAmount=${res.orderAmount}&orderId=${res.id}`
        })
      } catch (e) {
        console.error('提交预约失败', e)
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.confirm-page {
  padding: 20rpx;
  padding-bottom: 140rpx;
  min-height: 100vh;
}

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
  border-bottom: 1rpx solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
}

.label {
  font-size: 28rpx;
  color: #999;
}

.value {
  font-size: 28rpx;
  color: #333;
}

.loading-text,
.empty-text {
  text-align: center;
  color: #999;
  font-size: 28rpx;
  padding: 30rpx 0;
}

/* 计费方式 Tab */
.billing-tabs {
  display: flex;
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 6rpx;
  margin-bottom: 24rpx;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 18rpx 0;
  font-size: 28rpx;
  color: #666;
  border-radius: 8rpx;
  transition: all 0.2s;

  &.active {
    background: #ffffff;
    color: #4dba87;
    font-weight: bold;
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.06);
  }
}

.billing-detail {
  padding: 10rpx 0;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16rpx 0;
  border-bottom: 1rpx solid #f0f0f0;

  &:last-child {
    border-bottom: none;
  }
}

.detail-label {
  font-size: 28rpx;
  color: #666;
}

.detail-value {
  font-size: 28rpx;
  color: #333;
}

.total-row {
  padding-top: 20rpx;
  border-top: 2rpx solid #f0f0f0;
  border-bottom: none;
}

/* 套餐列表 */
.package-list {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.package-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx;
  border: 2rpx solid #e8e8e8;
  border-radius: 16rpx;
  transition: all 0.2s;

  &.selected {
    border-color: #4dba87;
    background: rgba(77, 186, 135, 0.05);
  box-shadow: 0 2rpx 12rpx rgba(77, 186, 135, 0.15);
  }
}

.package-left {
  flex: 1;
  min-width: 0;
}

.package-name {
  font-size: 30rpx;
  font-weight: bold;
  color: #333;
  margin-bottom: 8rpx;
}

.package-desc {
  font-size: 24rpx;
  color: #999;
  margin-bottom: 10rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.package-meta {
  display: flex;
  align-items: center;
  gap: 12rpx;
}

.meta-item {
  font-size: 22rpx;
  color: #999;
}

.meta-divider {
  font-size: 22rpx;
  color: #ddd;
}

.package-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10rpx;
  margin-left: 20rpx;
}

.package-price {
  font-size: 36rpx;
  font-weight: bold;
  color: #4dba87;
}

.selected-mark {
  width: 40rpx;
  height: 40rpx;
  border-radius: 50%;
  background: #4dba87;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28rpx;
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

.submit-btn {
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
