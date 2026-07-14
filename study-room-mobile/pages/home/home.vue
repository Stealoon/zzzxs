<template>
  <view class="home-page">
    <!-- 顶部场馆信息 -->
    <view class="shop-header">
      <view class="shop-info">
        <view class="shop-name-wrap">
          <text class="shop-name">共享自习室</text>
          <view class="shop-subtitle">专注 · 高效 · 共享学习空间</view>
        </view>
        <view class="shop-status" :class="shopStatus === 1 ? 'status-open' : 'status-closed'">
          <view class="status-dot" :class="shopStatus === 1 ? 'dot-open' : 'dot-closed'"></view>
          <text>{{ shopStatus === 1 ? '营业中' : '已闭馆' }}</text>
        </view>
      </view>
    </view>

    <!-- 快捷操作区 -->
    <view class="quick-actions">
      <view class="action-item" @tap="goReserve">
        <view class="action-icon icon-reserve">📅</view>
        <text class="action-text">立即预约</text>
        <text class="action-desc">选座预约</text>
      </view>
      <view class="action-item" @tap="goMyOrders">
        <view class="action-icon icon-orders">📋</view>
        <text class="action-text">我的预约</text>
        <text class="action-desc">查看订单</text>
      </view>
    </view>

    <!-- 区域概览 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">区域概览</text>
        <text class="section-more" @tap="goReserve">查看全部 ></text>
      </view>

      <view v-if="areaList.length === 0 && !loading" class="empty-tip">
        <text class="empty-text">暂无区域信息</text>
      </view>

      <view class="area-card" v-for="item in areaList" :key="item.id" @tap="goSeatList(item)">
        <view class="area-card-left">
          <text class="area-name">{{ item.name }}</text>
          <text class="area-desc">{{ item.description || '暂无描述' }}</text>
          <view class="area-meta">
            <view class="meta-tag">
              <text class="meta-icon">💺</text>
              <text class="meta-text">{{ item.seatCount || 0 }} 个座位</text>
            </view>
            <view class="meta-tag" v-if="item.status !== undefined">
              <text class="meta-icon">✅</text>
              <text class="meta-text">{{ item.status === 1 ? '可用' : '不可用' }}</text>
            </view>
          </view>
        </view>
        <view class="area-card-arrow">
          <text class="arrow-text">选座</text>
          <text class="arrow-icon">›</text>
        </view>
      </view>
    </view>

    <!-- 推荐套餐 -->
    <view class="section">
      <view class="section-header">
        <text class="section-title">推荐套餐</text>
        <text class="section-more" @tap="goPackageList">更多 ></text>
      </view>

      <view v-if="packageList.length === 0 && !loading" class="empty-tip">
        <text class="empty-text">暂无套餐</text>
      </view>

      <scroll-view scroll-x class="package-scroll" show-scrollbar="false" v-if="packageList.length > 0">
        <view class="package-card" v-for="item in packageList" :key="item.id" @tap="goPackageList">
          <image
            v-if="item.packageImage"
            class="package-image"
            :src="formatImageurl(item.packageImage)"
            mode="aspectFill"
          ></image>
          <view v-else class="package-image package-image-placeholder">
            <text class="placeholder-text">暂无图片</text>
          </view>
          <view class="package-info">
            <text class="package-name">{{ item.packageName }}</text>
            <text class="package-desc">{{ item.packageDesc || '超值套餐' }}</text>
            <view class="package-bottom">
              <text class="package-price">{{ formatPrice(item.packagePrice) }}</text>
              <view class="package-duration">
                <text class="duration-text">{{ item.durationHours || 0 }}小时 / {{ item.validDays || 0 }}天</text>
              </view>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- 底部留白 -->
    <view style="height: 30rpx;"></view>
  </view>
</template>

<script>
import shopApi from '@/api/shop'
import areaApi from '@/api/area'
import packageApi from '@/api/package'
import { formatImageurl, formatPrice } from '@/utils/format'

export default {
  data() {
    return {
      shopStatus: 0,
      areaList: [],
      packageList: [],
      loading: false
    }
  },
  onLoad() {
    this.loadAllData()
  },
  onShow() {
    // 每次显示刷新场馆状态
    this.loadShopStatus()
  },
  onPullDownRefresh() {
    this.loadAllData(() => {
      uni.stopPullDownRefresh()
    })
  },
  methods: {
    formatPrice,
    formatImageurl,

    /** 加载所有数据 */
    async loadAllData(callback) {
      this.loading = true
      await Promise.all([
        this.loadShopStatus(),
        this.loadAreaList(),
        this.loadPackageList()
      ]).catch(() => {})
      this.loading = false
      if (callback) callback()
    },

    /** 加载场馆状态 */
    async loadShopStatus() {
      try {
        const data = await shopApi.getStatus()
        this.shopStatus = data === 1 ? 1 : 0
      } catch (e) {
        // 请求失败默认闭馆
      }
    },

    /** 加载区域列表 */
    async loadAreaList() {
      try {
        const data = await areaApi.getList()
        this.areaList = data || []
      } catch (e) {
        // request.js 已自动 toast
      }
    },

    /** 加载套餐列表 */
    async loadPackageList() {
      try {
        const data = await packageApi.getList()
        this.packageList = data || []
      } catch (e) {
        // request.js 已自动 toast
      }
    },

    /** 跳转区域页（立即预约） */
    goReserve() {
      uni.switchTab({ url: '/pages/area/area' })
    },

    /** 跳转我的预约 */
    goMyOrders() {
      uni.navigateTo({ url: '/pages/order/order-list' })
    },

    /** 跳转座位列表 */
    goSeatList(area) {
      uni.navigateTo({
        url: `/pages/area/seat-list?areaId=${area.id}&areaName=${encodeURIComponent(area.name)}`
      })
    },

    /** 跳转套餐列表 */
    goPackageList() {
      uni.navigateTo({ url: '/pages/package/package-list' })
    }
  }
}
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 20rpx;
}

/* 顶部场馆信息 */
.shop-header {
  background: linear-gradient(135deg, #4DBA87 0%, #3DA975 100%);
  padding: 30rpx 30rpx 40rpx;
}

.shop-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.shop-name-wrap {
  display: flex;
  flex-direction: column;
}

.shop-name {
  font-size: 44rpx;
  font-weight: bold;
  color: #ffffff;
}

.shop-subtitle {
  font-size: 22rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 8rpx;
}

.shop-status {
  display: flex;
  align-items: center;
  padding: 10rpx 24rpx;
  border-radius: 30rpx;
  font-size: 24rpx;
  flex-shrink: 0;

  &.status-open {
    background: rgba(255, 255, 255, 0.25);
    color: #ffffff;
  }

  &.status-closed {
    background: rgba(0, 0, 0, 0.2);
    color: rgba(255, 255, 255, 0.9);
  }
}

.status-dot {
  width: 14rpx;
  height: 14rpx;
  border-radius: 50%;
  margin-right: 10rpx;

  &.dot-open {
    background: #ffffff;
    box-shadow: 0 0 8rpx rgba(255, 255, 255, 0.8);
  animation: pulse 1.5s infinite;
  }

  &.dot-closed {
    background: #cccccc;
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* 快捷操作区 */
.quick-actions {
  display: flex;
  margin: -20rpx 30rpx 0;
  background: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx 0;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.06);
  position: relative;
  z-index: 2;
}

.action-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.action-icon {
  width: 88rpx;
  height: 88rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40rpx;
  margin-bottom: 14rpx;

  &.icon-reserve {
    background: linear-gradient(135deg, #e8f7ef, #d4f0e0);
  }

  &.icon-orders {
    background: linear-gradient(135deg, #fff4e6, #ffe8cc);
  }
}

.action-text {
  font-size: 30rpx;
  font-weight: 600;
  color: #333333;
}

.action-desc {
  font-size: 22rpx;
  color: #999999;
  margin-top: 4rpx;
}

/* 通用Section */
.section {
  margin: 30rpx 30rpx 0;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20rpx;
}

.section-title {
  font-size: 34rpx;
  font-weight: bold;
  color: #333333;
}

.section-more {
  font-size: 24rpx;
  color: #999999;
}

/* 空状态 */
.empty-tip {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 60rpx 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-text {
  font-size: 28rpx;
  color: #cccccc;
}

/* 区域卡片 */
.area-card {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
  transition: transform 0.15s;

  &:active {
    transform: scale(0.98);
  }
}

.area-card-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.area-name {
  font-size: 32rpx;
  font-weight: 600;
  color: #333333;
  margin-bottom: 10rpx;
}

.area-desc {
  font-size: 24rpx;
  color: #999999;
  margin-bottom: 16rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.area-meta {
  display: flex;
  align-items: center;
}

.meta-tag {
  display: flex;
  align-items: center;
  margin-right: 30rpx;
}

.meta-icon {
  font-size: 24rpx;
  margin-right: 6rpx;
}

.meta-text {
  font-size: 24rpx;
  color: #666666;
}

.area-card-arrow {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-left: 20rpx;
  flex-shrink: 0;
}

.arrow-text {
  font-size: 22rpx;
  color: #4DBA87;
  margin-bottom: 4rpx;
}

.arrow-icon {
  font-size: 40rpx;
  color: #4DBA87;
  line-height: 1;
}

/* 套餐横向滚动 */
.package-scroll {
  white-space: nowrap;
  width: 100%;
}

.package-card {
  display: inline-flex;
  flex-direction: column;
  width: 280rpx;
  background: #ffffff;
  border-radius: 16rpx;
  overflow: hidden;
  margin-right: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.06);
  transition: transform 0.15s;

  &:active {
    transform: scale(0.97);
  }

  &:first-child {
    margin-left: 0;
  }
}

.package-image {
  width: 280rpx;
  height: 180rpx;
  background: #f0f0f0;
}

.package-image-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
}

.placeholder-text {
  font-size: 22rpx;
  color: #bbbbbb;
}

.package-info {
  padding: 20rpx;
  display: flex;
  flex-direction: column;
  white-space: normal;
}

.package-name {
  font-size: 28rpx;
  font-weight: 600;
  color: #333333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.package-desc {
  font-size: 22rpx;
  color: #999999;
  margin-top: 8rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.package-bottom {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-top: 14rpx;
}

.package-price {
  font-size: 34rpx;
  font-weight: bold;
  color: #ff6600;
}

.package-duration {
  background: #f0f7f3;
  border-radius: 8rpx;
  padding: 4rpx 12rpx;
}

.duration-text {
  font-size: 20rpx;
  color: #4DBA87;
}
</style>
