<template>
  <view class="package-page">
    <!-- 顶部Banner -->
    <view class="banner">
      <view class="banner-bg"></view>
      <view class="banner-content">
        <view class="banner-title">套餐中心</view>
        <view class="banner-desc">多种套餐，灵活选择</view>
      </view>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-wrap">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 套餐列表 -->
    <view v-else-if="packageList.length > 0" class="package-list">
      <view
        v-for="item in packageList"
        :key="item.id"
        class="package-card"
        @click="goReservation"
      >
        <!-- 套餐封面图 -->
        <view class="card-image">
          <image
            v-if="item.packageImage"
            class="cover-img"
            :src="formatImageurl(item.packageImage)"
            mode="aspectFill"
            lazy-load
          />
          <view v-else class="cover-img cover-img-placeholder">
            <text class="placeholder-text">暂无图片</text>
          </view>
          <view class="image-overlay">
            <view v-if="item.validDays" class="type-badge day-badge">天卡</view>
            <view v-else-if="item.durationHours" class="type-badge hour-badge">小时卡</view>
          </view>
        </view>

        <!-- 套餐信息区域 -->
        <view class="card-info">
          <view class="info-header">
            <text class="package-name">{{ item.packageName }}</text>
            <text class="package-price">{{ formatPrice(item.packagePrice) }}</text>
          </view>

          <text v-if="item.packageDesc" class="package-desc">{{ item.packageDesc }}</text>

          <view class="tag-row">
            <!-- 时长标签 -->
            <view v-if="item.validDays" class="tag tag-day">
              <text class="tag-text">有效期{{ item.validDays }}天</text>
            </view>
            <view v-else-if="item.durationHours" class="tag tag-hour">
              <text class="tag-text">{{ item.durationHours }}小时</text>
            </view>
            <!-- 适用范围标签 -->
            <view v-if="item.areaId === 0" class="tag tag-global">
              <text class="tag-text">全场通用</text>
            </view>
            <view v-else class="tag tag-area">
              <text class="tag-text">限区域</text>
            </view>
          </view>

          <view class="card-footer">
            <text class="footer-tip">选择套餐开始预约</text>
            <view class="enter-btn">
              <text class="enter-text">去预约</text>
              <text class="enter-arrow">›</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else class="empty-state">
      <view class="empty-icon">📦</view>
      <text class="empty-text">暂无可用套餐</text>
      <text class="empty-sub">请稍后再来查看</text>
    </view>
  </view>
</template>

<script>
import packageApi from '@/api/package'
import { formatImageurl, formatPrice } from '@/utils/format'

export default {
  data() {
    return {
      packageList: [],
      loading: false
    }
  },
  onLoad() {
    this.loadPackageList()
  },
  onPullDownRefresh() {
    this.loadPackageList().finally(() => {
      uni.stopPullDownRefresh()
    })
  },
  methods: {
    formatImageurl,
    formatPrice,

    /** 加载套餐列表 */
    async loadPackageList() {
      this.loading = true
      try {
        const data = await packageApi.getList()
        this.packageList = (data || []).filter((item) => item.status === 1)
      } catch (e) {
        console.error('加载套餐列表失败:', e)
        this.packageList = []
      } finally {
        this.loading = false
      }
    },

    /** 跳转到预约流程（区域选择页） */
    goReservation() {
      uni.switchTab({
        url: '/pages/area/area'
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.package-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 40rpx;
}

/* Banner */
.banner {
  position: relative;
  height: 200rpx;
  overflow: hidden;

  .banner-bg {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 100%;
    background: linear-gradient(135deg, #4DBA87 0%, #6FCFA8 100%);
  }

  .banner-content {
    position: relative;
    z-index: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
    height: 100%;
    padding: 0 40rpx;
  }

  .banner-title {
    font-size: 40rpx;
    font-weight: bold;
    color: #ffffff;
    margin-bottom: 12rpx;
  }

  .banner-desc {
    font-size: 26rpx;
    color: rgba(255, 255, 255, 0.85);
  }
}

/* 加载状态 */
.loading-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120rpx 0;

  .loading-spinner {
    width: 60rpx;
    height: 60rpx;
    border: 6rpx solid #e8e8e8;
    border-top-color: #4DBA87;
    border-radius: 50%;
    animation: spin 0.8s linear infinite;
    margin-bottom: 20rpx;
  }

  .loading-text {
    font-size: 26rpx;
    color: #999999;
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 套餐列表 */
.package-list {
  padding: 20rpx 24rpx;
}

.package-card {
  background-color: #ffffff;
  border-radius: 20rpx;
  margin-bottom: 28rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease;

  &:active {
    transform: scale(0.98);
    box-shadow: 0 2rpx 10rpx rgba(0, 0, 0, 0.1);
  }
}

/* 封面图 */
.card-image {
  position: relative;
  width: 100%;
  height: 320rpx;
  overflow: hidden;

  .cover-img {
    width: 100%;
    height: 100%;
  }

  .cover-img-placeholder {
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f0f0f0;
  }

  .placeholder-text {
    font-size: 26rpx;
    color: #bbbbbb;
  }

  .image-overlay {
    position: absolute;
    top: 20rpx;
    left: 20rpx;
    display: flex;
    gap: 12rpx;
  }

  .type-badge {
    padding: 8rpx 24rpx;
    border-radius: 24rpx;
    font-size: 24rpx;
    font-weight: 600;
    color: #ffffff;
    backdrop-filter: blur(4px);
  }

  .day-badge {
    background: rgba(255, 153, 0, 0.85);
  }

  .hour-badge {
    background: rgba(0, 153, 255, 0.85);
  }
}

/* 信息区域 */
.card-info {
  padding: 24rpx 28rpx 28rpx;
}

.info-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 16rpx;

  .package-name {
    font-size: 34rpx;
    font-weight: bold;
    color: #333333;
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    margin-right: 16rpx;
  }

  .package-price {
    font-size: 44rpx;
    font-weight: bold;
    color: #ff4d4f;
    flex-shrink: 0;
  }
}

.package-desc {
  font-size: 26rpx;
  color: #666666;
  line-height: 1.5;
  margin-bottom: 20rpx;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

/* 标签行 */
.tag-row {
  display: flex;
  flex-wrap: wrap;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.tag {
  padding: 8rpx 20rpx;
  border-radius: 8rpx;
  font-size: 24rpx;

  .tag-text {
    font-size: 24rpx;
    font-weight: 500;
  }
}

.tag-day {
  background-color: rgba(255, 153, 0, 0.12);

  .tag-text {
    color: #ff9900;
  }
}

.tag-hour {
  background-color: rgba(0, 153, 255, 0.12);

  .tag-text {
    color: #0099ff;
  }
}

.tag-global {
  background-color: rgba(77, 186, 135, 0.12);

  .tag-text {
    color: #4DBA87;
  }
}

.tag-area {
  background-color: rgba(153, 153, 153, 0.12);

  .tag-text {
    color: #999999;
  }
}

/* 底部 */
.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 20rpx;
  border-top: 2rpx solid #f0f0f0;

  .footer-tip {
    font-size: 24rpx;
    color: #999999;
  }

  .enter-btn {
    display: flex;
    align-items: center;
    gap: 4rpx;

    .enter-text {
      font-size: 28rpx;
      color: #4DBA87;
      font-weight: 600;
    }

    .enter-arrow {
      font-size: 36rpx;
      color: #4DBA87;
      line-height: 1;
    }
  }
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 160rpx 0;

  .empty-icon {
    font-size: 100rpx;
    margin-bottom: 30rpx;
  }

  .empty-text {
    font-size: 32rpx;
    color: #666666;
    margin-bottom: 12rpx;
  }

  .empty-sub {
    font-size: 26rpx;
    color: #999999;
  }
}
</style>
