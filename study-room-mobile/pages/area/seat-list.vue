<template>
  <view class="seat-list-page">
    <!-- 区域信息条 -->
    <view class="area-info-bar">
      <view class="area-info-left">
        <text class="area-info-icon">📍</text>
        <text class="area-info-name">{{ areaName || '座位列表' }}</text>
      </view>
      <text class="area-info-count">共 {{ seatList.length }} 个座位</text>
    </view>

    <!-- 加载状态 -->
    <view v-if="loading" class="loading-wrap">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>

    <!-- 座位列表 -->
    <view v-else-if="seatList.length > 0" class="seat-list">
      <view
        v-for="item in seatList"
        :key="item.id"
        class="seat-card"
        @click="goSeatDetail(item)"
      >
        <!-- 左侧图片 -->
        <view class="seat-image-wrap">
          <image
            class="seat-image"
            :src="getSeatImage(item.seatImage)"
            mode="aspectFill"
          />
        </view>
        <!-- 右侧内容 -->
        <view class="seat-info">
          <view class="seat-header">
            <text class="seat-code">{{ item.seatCode }}</text>
            <text class="seat-price">{{ formatPrice(item.hourPrice) }}<text class="price-unit">/小时</text></text>
          </view>
          <view class="seat-tags">
            <view v-if="item.hasWindow === 1" class="tag tag-window">
              <text class="tag-text">靠窗</text>
            </view>
            <view v-if="item.hasSocket === 1" class="tag tag-socket">
              <text class="tag-text">带插座</text>
            </view>
          </view>
          <text class="seat-desc">{{ item.seatDescription || '暂无描述' }}</text>
          <view class="seat-action">
            <text class="action-text">查看详情</text>
            <text class="action-arrow">›</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else class="empty-state">
      <view class="empty-icon">🪑</view>
      <text class="empty-text">该区域暂无可用座位</text>
      <text class="empty-sub">请选择其他区域</text>
    </view>
  </view>
</template>

<script>
import seatApi from '@/api/seat'
import { formatImageurl, formatPrice } from '@/utils/format'

export default {
  data() {
    return {
      areaId: null,
      areaName: '',
      seatList: [],
      loading: false
    }
  },
  onLoad(options) {
    this.areaId = options.areaId
    this.areaName = options.areaName ? decodeURIComponent(options.areaName) : ''
    // 动态设置导航栏标题
    if (this.areaName) {
      uni.setNavigationBarTitle({ title: this.areaName })
    }
    this.loadSeatList()
  },
  methods: {
    formatPrice,
    async loadSeatList() {
      if (!this.areaId) {
        uni.showToast({ title: '区域参数缺失', icon: 'none' })
        return
      }
      this.loading = true
      try {
        const data = await seatApi.getList(this.areaId)
        // 只显示status=1（启用）的座位
        this.seatList = (data || []).filter((item) => item.status === 1)
      } catch (e) {
        console.error('加载座位列表失败:', e)
        this.seatList = []
      } finally {
        this.loading = false
      }
    },
    getSeatImage(path) {
      const url = formatImageurl(path)
      return url
    },
    goSeatDetail(item) {
      uni.navigateTo({
        url: `/pages/seat/seat-detail?seatId=${item.id}`
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.seat-list-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 40rpx;
}

/* 区域信息条 */
.area-info-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 32rpx;
  background-color: #ffffff;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.04);

  .area-info-left {
    display: flex;
    align-items: center;
    gap: 10rpx;

    .area-info-icon {
      font-size: 32rpx;
    }

    .area-info-name {
      font-size: 30rpx;
      font-weight: 600;
      color: #333333;
    }
  }

  .area-info-count {
    font-size: 26rpx;
    color: #999999;
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

/* 座位列表 */
.seat-list {
  padding: 20rpx 24rpx;
}

.seat-card {
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

  .seat-image-wrap {
    flex-shrink: 0;
    width: 220rpx;
    height: 220rpx;
    background-color: #f5f5f5;

    .seat-image {
      width: 100%;
      height: 100%;
    }
  }

  .seat-info {
    flex: 1;
    padding: 20rpx 24rpx;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    min-width: 0;
  }

  .seat-header {
    display: flex;
    align-items: baseline;
    justify-content: space-between;

    .seat-code {
      font-size: 32rpx;
      font-weight: bold;
      color: #333333;
    }

    .seat-price {
      font-size: 32rpx;
      font-weight: bold;
      color: #ff6600;

      .price-unit {
        font-size: 22rpx;
        color: #999999;
        font-weight: normal;
      }
    }
  }

  .seat-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 12rpx;
    margin-top: 12rpx;

    .tag {
      padding: 4rpx 16rpx;
      border-radius: 8rpx;

      .tag-text {
        font-size: 22rpx;
        line-height: 1.4;
      }
    }

    .tag-window {
      background-color: rgba(0, 153, 255, 0.12);

      .tag-text {
        color: #0099ff;
      }
    }

    .tag-socket {
      background-color: rgba(255, 153, 0, 0.12);

      .tag-text {
        color: #ff9900;
      }
    }
  }

  .seat-desc {
    font-size: 26rpx;
    color: #666666;
    line-height: 1.5;
    margin-top: 12rpx;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
  }

  .seat-action {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 4rpx;
    margin-top: 8rpx;

    .action-text {
      font-size: 26rpx;
      color: #4DBA87;
      font-weight: 600;
    }

    .action-arrow {
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
