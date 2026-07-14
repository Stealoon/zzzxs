<template>
  <view class="area-page">
    <!-- 顶部Banner -->
    <view class="banner">
      <view class="banner-bg"></view>
      <view class="banner-content">
        <view class="banner-title">选择自习区域</view>
        <view class="banner-desc">安静专注，高效学习</view>
      </view>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-wrap">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 区域列表 -->
    <view v-else-if="areaList.length > 0" class="area-list">
      <view
        v-for="item in areaList"
        :key="item.id"
        class="area-card"
        @click="goSeatList(item)"
      >
        <!-- 左侧色块 -->
        <view class="card-left">
          <view class="color-block">
            <text class="area-icon">📚</text>
          </view>
        </view>
        <!-- 右侧内容 -->
        <view class="card-right">
          <view class="card-header">
            <text class="area-name">{{ item.name }}</text>
            <view class="seat-count-tag">
              <text class="seat-count-text">{{ item.seatCount || 0 }} 座位</text>
            </view>
          </view>
          <text class="area-desc">{{ item.description || '暂无描述' }}</text>
          <view class="card-footer">
            <view class="sort-tag">
              <text class="sort-label">排序</text>
              <text class="sort-value">{{ item.sort }}</text>
            </view>
            <view class="enter-btn">
              <text class="enter-text">查看座位</text>
              <text class="enter-arrow">›</text>
            </view>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else class="empty-state">
      <view class="empty-icon">📋</view>
      <text class="empty-text">暂无可选区域</text>
      <text class="empty-sub">请稍后再来查看</text>
    </view>
  </view>
</template>

<script>
import areaApi from '@/api/area'

export default {
  data() {
    return {
      areaList: [],
      loading: false
    }
  },
  onLoad() {
    this.loadAreaList()
  },
  onPullDownRefresh() {
    this.loadAreaList().finally(() => {
      uni.stopPullDownRefresh()
    })
  },
  methods: {
    async loadAreaList() {
      this.loading = true
      try {
        const data = await areaApi.getList()
        // 只显示status=1（启用）的区域
        this.areaList = (data || []).filter((item) => item.status === 1)
      } catch (e) {
        console.error('加载区域列表失败:', e)
        this.areaList = []
      } finally {
        this.loading = false
      }
    },
    goSeatList(item) {
      const areaName = encodeURIComponent(item.name || '')
      uni.navigateTo({
        url: `/pages/area/seat-list?areaId=${item.id}&areaName=${areaName}`
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.area-page {
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

/* 区域列表 */
.area-list {
  padding: 20rpx 24rpx;
}

.area-card {
  display: flex;
  background-color: #ffffff;
  border-radius: 16rpx;
  margin-bottom: 24rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);
  transition: transform 0.2s ease, box-shadow 0.2s ease;

  &:active {
    transform: scale(0.98);
    box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
  }

  .card-left {
    flex-shrink: 0;
    width: 140rpx;

    .color-block {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 100%;
      min-height: 200rpx;
      background: linear-gradient(135deg, #4DBA87 0%, #6FCFA8 100%);

      .area-icon {
        font-size: 56rpx;
      }
    }
  }

  .card-right {
    flex: 1;
    padding: 24rpx 28rpx;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  .card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 12rpx;

    .area-name {
      font-size: 34rpx;
      font-weight: bold;
      color: #333333;
    }

    .seat-count-tag {
      background-color: rgba(77, 186, 135, 0.12);
      padding: 6rpx 18rpx;
      border-radius: 20rpx;

      .seat-count-text {
        font-size: 24rpx;
        color: #4DBA87;
        font-weight: 600;
      }
    }
  }

  .area-desc {
    font-size: 26rpx;
    color: #666666;
    line-height: 1.5;
    margin-bottom: 16rpx;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
  }

  .card-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .sort-tag {
      display: flex;
      align-items: center;
      gap: 8rpx;

      .sort-label {
        font-size: 24rpx;
        color: #999999;
      }

      .sort-value {
        font-size: 24rpx;
        color: #666666;
        font-weight: 600;
      }
    }

    .enter-btn {
      display: flex;
      align-items: center;
      gap: 4rpx;

      .enter-text {
        font-size: 26rpx;
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
