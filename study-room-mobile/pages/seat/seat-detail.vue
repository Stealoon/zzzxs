<template>
  <view class="seat-detail-page">
    <!-- 加载状态 -->
    <view v-if="loading" class="loading-wrap">
      <view class="loading-spinner"></view>
      <text class="loading-text">加载中...</text>
    </view>

    <view v-else-if="seatData" class="detail-content">
      <!-- 座位大图 -->
      <view class="seat-image-section">
        <image
          class="seat-big-image"
          :src="getSeatImage(seatData)"
          mode="aspectFill"
        />
        <view class="image-overlay">
          <view class="seat-code-badge">
            <text class="seat-code-text">{{ seatData.seatCode }}</text>
          </view>
        </view>
      </view>

      <!-- 基本信息 -->
      <view class="info-card">
        <view class="info-header">
          <text class="info-title">{{ seatData.seatCode }}</text>
          <view v-if="seatData.areaName" class="area-tag">
            <text class="area-tag-text">📍 {{ seatData.areaName }}</text>
          </view>
        </view>

        <view class="price-row">
          <view class="price-wrap">
            <text class="price-value">{{ formatPrice(seatData.hourPrice) }}</text>
            <text class="price-unit">/小时</text>
          </view>
          <view class="feature-tags">
            <view v-if="seatData.hasWindow === 1" class="tag tag-window">
              <text class="tag-icon">🪟</text>
              <text class="tag-text">靠窗</text>
            </view>
            <view v-if="seatData.hasSocket === 1" class="tag tag-socket">
              <text class="tag-icon">🔌</text>
              <text class="tag-text">带插座</text>
            </view>
          </view>
        </view>

        <view v-if="seatData.seatDescription" class="desc-row">
          <text class="desc-label">座位描述</text>
          <text class="desc-content">{{ seatData.seatDescription }}</text>
        </view>
      </view>

      <!-- 时段轴 -->
      <view class="time-slots-section">
        <view class="section-header">
          <view class="section-bar"></view>
          <text class="section-title">可选时段</text>
        </view>

        <view v-if="timeSlots.length > 0" class="time-slots">
          <view
            v-for="(slot, index) in timeSlots"
            :key="index"
            class="time-slot"
            :class="{
              'available': slot.available,
              'selected': selectedIndex === index,
              'occupied': !slot.available
            }"
            @click="slot.available && selectSlot(index)"
          >
            <view class="slot-time-row">
              <text class="time-start">{{ formatTime(slot.startTime) }}</text>
              <text class="time-dash">—</text>
              <text class="time-end">{{ formatTime(slot.endTime) }}</text>
            </view>
            <view class="slot-status-wrap">
              <text class="slot-status" v-if="slot.available">
                {{ selectedIndex === index ? '已选中' : '可用' }}
              </text>
              <text class="slot-status" v-else>已占用</text>
            </view>
          </view>
        </view>

        <view v-else class="no-slots">
          <text class="no-slots-text">今日暂无可选时段</text>
        </view>
      </view>

      <!-- 底部预约按钮 -->
      <view v-if="selectedSlot" class="bottom-bar">
        <view class="selected-info">
          <text class="selected-label">已选时段</text>
          <text class="selected-time">{{ formatTime(selectedSlot.startTime) }} - {{ formatTime(selectedSlot.endTime) }}</text>
        </view>
        <view class="reserve-btn" @click="goReserve">
          <text class="reserve-btn-text">去预约</text>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else class="empty-state">
      <view class="empty-icon">😢</view>
      <text class="empty-text">座位信息加载失败</text>
      <view class="retry-btn" @click="loadSeatDetail">
        <text class="retry-text">重试</text>
      </view>
    </view>
  </view>
</template>

<script>
import seatApi from '@/api/seat'
import { formatImageurl, formatPrice } from '@/utils/format'

export default {
  data() {
    return {
      seatId: null,
      seatData: null,
      timeSlots: [],
      selectedIndex: -1,
      loading: false
    }
  },
  computed: {
    selectedSlot() {
      if (this.selectedIndex >= 0 && this.timeSlots.length > 0) {
        return this.timeSlots[this.selectedIndex]
      }
      return null
    }
  },
  onLoad(options) {
    this.seatId = options.seatId
    this.loadSeatDetail()
  },
  methods: {
    formatPrice,
    async loadSeatDetail() {
      if (!this.seatId) {
        uni.showToast({ title: '座位参数缺失', icon: 'none' })
        return
      }
      this.loading = true
      this.selectedIndex = -1
      try {
        const data = await seatApi.getDetail(this.seatId)
        this.seatData = data || null
        this.timeSlots = (data && data.timeSlots) ? data.timeSlots : []
      } catch (e) {
        console.error('加载座位详情失败:', e)
        this.seatData = null
        this.timeSlots = []
      } finally {
        this.loading = false
      }
    },
    /**
     * 获取座位图片URL
     * 兼容 image 和 seatImage 两种字段名
     */
    getSeatImage(data) {
      if (!data) return ''
      const path = data.image || data.seatImage || ''
      return formatImageurl(path)
    },
    /**
     * 格式化时间：从 "2026-07-13 09:00:00" 或 "2026-07-13 09:00" 中提取 HH:mm
     */
    formatTime(dt) {
      if (!dt) return ''
      const parts = String(dt).split(' ')
      if (parts.length > 1) {
        return parts[1].substring(0, 5)
      }
      return dt
    },
    selectSlot(index) {
      if (this.selectedIndex === index) {
        // 再次点击取消选择
        this.selectedIndex = -1
      } else {
        this.selectedIndex = index
      }
    },
    goReserve() {
      if (!this.selectedSlot || !this.seatData) return
      const params = [
        `seatId=${this.seatId}`,
        `areaId=${this.seatData.areaId}`,
        `startTime=${encodeURIComponent(this.selectedSlot.startTime)}`,
        `endTime=${encodeURIComponent(this.selectedSlot.endTime)}`,
        `hourPrice=${this.seatData.hourPrice}`
      ].join('&')
      uni.navigateTo({
        url: `/pages/reservation/confirm?${params}`
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.seat-detail-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 140rpx;
}

/* 加载状态 */
.loading-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 160rpx 0;

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

/* 座位大图 */
.seat-image-section {
  position: relative;
  width: 100%;
  height: 400rpx;
  background-color: #f5f5f5;

  .seat-big-image {
    width: 100%;
    height: 100%;
  }

  .image-overlay {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 120rpx;
    background: linear-gradient(to top, rgba(0, 0, 0, 0.5) 0%, transparent 100%);
    display: flex;
    align-items: flex-end;
    padding: 0 30rpx 20rpx;

    .seat-code-badge {
      background-color: rgba(77, 186, 135, 0.9);
      padding: 8rpx 24rpx;
      border-radius: 8rpx;

      .seat-code-text {
        font-size: 28rpx;
        font-weight: bold;
        color: #ffffff;
      }
    }
  }
}

/* 信息卡片 */
.info-card {
  background-color: #ffffff;
  border-radius: 16rpx;
  margin: 20rpx 24rpx 0;
  padding: 30rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);

  .info-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 24rpx;

    .info-title {
      font-size: 38rpx;
      font-weight: bold;
      color: #333333;
    }

    .area-tag {
      background-color: rgba(77, 186, 135, 0.12);
      padding: 6rpx 18rpx;
      border-radius: 20rpx;

      .area-tag-text {
        font-size: 24rpx;
        color: #4DBA87;
        font-weight: 600;
      }
    }
  }

  .price-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-bottom: 24rpx;
    border-bottom: 1rpx solid #f0f0f0;

    .price-wrap {
      display: flex;
      align-items: baseline;

      .price-value {
        font-size: 44rpx;
        font-weight: bold;
        color: #ff6600;
      }

      .price-unit {
        font-size: 24rpx;
        color: #999999;
        margin-left: 6rpx;
      }
    }

    .feature-tags {
      display: flex;
      gap: 16rpx;

      .tag {
        display: flex;
        align-items: center;
        gap: 6rpx;
        padding: 8rpx 18rpx;
        border-radius: 10rpx;

        .tag-icon {
          font-size: 28rpx;
        }

        .tag-text {
          font-size: 24rpx;
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
  }

  .desc-row {
    padding-top: 24rpx;

    .desc-label {
      display: block;
      font-size: 26rpx;
      color: #999999;
      margin-bottom: 12rpx;
    }

    .desc-content {
      font-size: 28rpx;
      color: #333333;
      line-height: 1.6;
    }
  }
}

/* 时段轴 */
.time-slots-section {
  background-color: #ffffff;
  border-radius: 16rpx;
  margin: 20rpx 24rpx 0;
  padding: 30rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.08);

  .section-header {
    display: flex;
    align-items: center;
    gap: 12rpx;
    margin-bottom: 28rpx;

    .section-bar {
      width: 8rpx;
      height: 32rpx;
      background-color: #4DBA87;
      border-radius: 4rpx;
    }

    .section-title {
      font-size: 32rpx;
      font-weight: bold;
      color: #333333;
    }
  }

  .time-slots {
    display: flex;
    flex-wrap: wrap;
    gap: 20rpx;
  }

  .time-slot {
    width: calc(50% - 10rpx);
    border: 2rpx solid #e8e8e8;
    border-radius: 12rpx;
    padding: 20rpx;
    background-color: #fafafa;
    transition: all 0.2s ease;

    .slot-time-row {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 12rpx;
      margin-bottom: 10rpx;

      .time-start,
      .time-end {
        font-size: 30rpx;
        font-weight: 600;
        color: #333333;
      }

      .time-dash {
        font-size: 26rpx;
        color: #999999;
      }
    }

    .slot-status-wrap {
      display: flex;
      justify-content: center;

      .slot-status {
        font-size: 24rpx;
        color: #999999;
      }
    }

    &.available {
      border-color: #4DBA87;
      background-color: rgba(77, 186, 135, 0.06);

      .slot-status {
        color: #4DBA87;
      }

      &:active {
        transform: scale(0.97);
      }
    }

    &.selected {
      border-color: #4DBA87;
      background-color: #4DBA87;
      box-shadow: 0 4rpx 12rpx rgba(77, 186, 135, 0.4);

      .time-start,
      .time-end {
        color: #ffffff;
      }

      .time-dash {
        color: rgba(255, 255, 255, 0.7);
      }

      .slot-status {
        color: #ffffff;
        font-weight: 600;
      }
    }

    &.occupied {
      border-color: #e8e8e8;
      background-color: #f5f5f5;

      .time-start,
      .time-end {
        color: #cccccc;
      }

      .time-dash {
        color: #cccccc;
      }

      .slot-status {
        color: #cccccc;
      }
    }
  }

  .no-slots {
    display: flex;
    justify-content: center;
    padding: 60rpx 0;

    .no-slots-text {
      font-size: 28rpx;
      color: #999999;
    }
  }
}

/* 底部预约栏 */
.bottom-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20rpx 30rpx;
  padding-bottom: calc(20rpx + env(safe-area-inset-bottom));
  background-color: #ffffff;
  box-shadow: 0 -4rpx 16rpx rgba(0, 0, 0, 0.08);
  z-index: 100;

  .selected-info {
    display: flex;
    flex-direction: column;
    gap: 6rpx;

    .selected-label {
      font-size: 24rpx;
      color: #999999;
    }

    .selected-time {
      font-size: 30rpx;
      font-weight: 600;
      color: #333333;
    }
  }

  .reserve-btn {
    background: linear-gradient(135deg, #4DBA87 0%, #6FCFA8 100%);
    padding: 24rpx 60rpx;
    border-radius: 40rpx;
    box-shadow: 0 4rpx 12rpx rgba(77, 186, 135, 0.4);

    &:active {
      opacity: 0.85;
    }

    .reserve-btn-text {
      font-size: 30rpx;
      font-weight: bold;
      color: #ffffff;
    }
  }
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 200rpx 0;

  .empty-icon {
    font-size: 100rpx;
    margin-bottom: 30rpx;
  }

  .empty-text {
    font-size: 32rpx;
    color: #666666;
    margin-bottom: 40rpx;
  }

  .retry-btn {
    background-color: #4DBA87;
    padding: 20rpx 60rpx;
    border-radius: 40rpx;

    &:active {
      opacity: 0.85;
    }

    .retry-text {
      font-size: 28rpx;
      color: #ffffff;
      font-weight: 600;
    }
  }
}
</style>
