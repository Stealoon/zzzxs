<template>
  <view class="order-list-page">
    <!-- 状态筛选Tab（横向滚动） -->
    <scroll-view class="status-tabs" scroll-x :scroll-into-view="tabScrollInto" scroll-with-animation>
      <view class="tabs-inner">
        <view
          v-for="tab in statusTabs"
          :key="tab.key"
          :id="'tab-' + tab.key"
          class="tab-item"
          :class="{ active: activeKey === tab.key }"
          @tap="switchTab(tab)"
        >
          <text class="tab-text">{{ tab.label }}</text>
        </view>
      </view>
    </scroll-view>

    <!-- 订单列表 -->
    <view class="list-wrap">
      <!-- 加载中（首次） -->
      <view v-if="loading && list.length === 0" class="state-tip">
        <text>加载中...</text>
      </view>

      <!-- 空状态 -->
      <view v-else-if="list.length === 0" class="empty-state">
        <text class="empty-icon">📭</text>
        <text class="empty-text">暂无预约记录</text>
      </view>

      <!-- 列表内容 -->
      <view v-else class="list-content">
        <view
          v-for="item in list"
          :key="item.id"
          class="order-card"
          @tap="goDetail(item)"
        >
          <!-- 卡片头部 -->
          <view class="card-header">
            <text class="order-number">订单号：{{ item.number }}</text>
            <order-status-badge :status="item.status" />
          </view>

          <!-- 卡片内容 -->
          <view class="card-body">
            <view class="info-row">
              <text class="info-label">座位</text>
              <text class="info-value">{{ item.seatCode || '—' }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">区域</text>
              <text class="info-value">{{ item.areaName || '—' }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">时段</text>
              <text class="info-value">{{ formatDateTime(item.startTime) }} - {{ formatTime(item.endTime) }}</text>
            </view>
            <view class="info-row">
              <text class="info-label">金额</text>
              <text class="info-value price">{{ formatPrice(item.amount) }}</text>
            </view>
          </view>

          <!-- 卡片操作区 -->
          <view class="card-footer" @tap.stop>
            <!-- 待支付：去支付 -->
            <button
              v-if="item.status === 1"
              class="action-btn primary"
              @tap="goPay(item)"
            >去支付</button>

            <!-- 待签到：签到 + 取消 -->
            <template v-else-if="item.status === 2">
              <button class="action-btn ghost" @tap="goCheckin(item)">签到</button>
              <button class="action-btn danger-ghost" @tap="handleCancel(item)">取消</button>
            </template>

            <!-- 其他状态：仅查看 -->
            <text v-else class="view-text">点击查看详情</text>
          </view>
        </view>

        <!-- 加载更多/到底提示 -->
        <view class="load-tip">
          <text v-if="loading">加载中...</text>
          <text v-else-if="!hasMore">— 已经到底了 —</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
import orderApi from '@/api/order'
import orderStatusBadge from '@/components/order-status-badge.vue'
import { ORDER_STATUS } from '@/utils/constants'
import { formatPrice, formatDateTime, formatTime } from '@/utils/format'

export default {
  name: 'OrderList',
  components: { orderStatusBadge },
  data() {
    return {
      // 状态Tab配置，key 用字符串保证 'all' 唯一
      statusTabs: [
        { key: 'all', label: '全部', status: null },
        { key: 'pending', label: '待支付', status: ORDER_STATUS.PENDING_PAYMENT.value },
        { key: 'reserved', label: '待签到', status: ORDER_STATUS.RESERVED.value },
        { key: 'inuse', label: '使用中', status: ORDER_STATUS.IN_USE.value },
        { key: 'completed', label: '已完成', status: ORDER_STATUS.COMPLETED.value },
        { key: 'cancelled', label: '已取消', status: ORDER_STATUS.CANCELLED.value },
        { key: 'expired', label: '已过期', status: ORDER_STATUS.EXPIRED.value }
      ],
      activeKey: 'all',
      activeStatus: null, // null=全部
      tabScrollInto: '',
      // 列表数据
      list: [],
      page: 1,
      pageSize: 10,
      total: 0,
      loading: false,
      hasMore: true,
      isLoaded: false // 标记是否已完成首次加载
    }
  },
  onLoad() {
    this.loadData()
  },
  onShow() {
    // 从详情/支付/签到页返回时刷新当前列表（跳过首次进入时的重复加载）
    if (this.isLoaded) {
      this.loadData(true)
    }
  },
  onReachBottom() {
    this.loadData()
  },
  onPullDownRefresh() {
    this.loadData(true).finally(() => {
      uni.stopPullDownRefresh()
    })
  },
  methods: {
    formatPrice,
    formatDateTime,
    formatTime,

    /** 切换状态Tab */
    switchTab(tab) {
      if (this.activeKey === tab.key) return
      this.activeKey = tab.key
      this.activeStatus = tab.status
      this.tabScrollInto = 'tab-' + tab.key
      this.loadData(true)
    },

    /** 加载列表数据 */
    async loadData(reset = false) {
      if (reset) {
        this.page = 1
        this.list = []
        this.hasMore = true
      }
      if (this.loading || !this.hasMore) return
      this.loading = true
      try {
        const params = { page: this.page, pageSize: this.pageSize }
        if (this.activeStatus !== null) params.status = this.activeStatus
        const res = await orderApi.getList(params)
        const records = res.records || []
        this.list.push(...records)
        this.total = res.total || 0
        this.hasMore = this.list.length < this.total
        this.page++
      } catch (e) {
        // request.js 已自动 toast 错误
      } finally {
        this.loading = false
        this.isLoaded = true
      }
    },

    /** 跳转详情页 */
    goDetail(item) {
      uni.navigateTo({ url: `/pages/order/order-detail?id=${item.id}` })
    },

    /** 去支付 */
    goPay(item) {
      uni.navigateTo({
        url: `/pages/reservation/pay?orderNumber=${item.number}&orderAmount=${item.amount}&orderId=${item.id}`
      })
    },

    /** 去签到 */
    goCheckin(item) {
      uni.navigateTo({ url: `/pages/checkin/checkin?orderId=${item.id}` })
    },

    /** 取消预约（二次确认） */
    handleCancel(order) {
      uni.showModal({
        title: '取消预约',
        content: '确定要取消该预约吗？',
        editable: true,
        placeholderText: '请输入取消原因（可选）',
        success: async (res) => {
          if (res.confirm) {
            try {
              await orderApi.cancel({ id: order.id, cancelReason: res.content || '' })
              uni.showToast({ title: '取消成功', icon: 'success' })
              this.loadData(true)
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
.order-list-page {
  min-height: 100vh;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
}

/* 状态Tab */
.status-tabs {
  width: 100%;
  height: 88rpx;
  background: #ffffff;
  white-space: nowrap;
  border-bottom: 1rpx solid #f0f0f0;
  flex-shrink: 0;
}

.tabs-inner {
  display: inline-flex;
  height: 88rpx;
  align-items: center;
  padding: 0 12rpx;
}

.tab-item {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  height: 64rpx;
  padding: 0 28rpx;
  margin: 0 6rpx;
  border-radius: 32rpx;
  background: #f5f5f5;
  transition: all 0.25s;

  .tab-text {
    font-size: 26rpx;
    color: #666666;
    white-space: nowrap;
  }

  &.active {
    background: #4DBA87;

    .tab-text {
      color: #ffffff;
      font-weight: 600;
    }
  }
}

/* 列表区域 */
.list-wrap {
  flex: 1;
  padding: 20rpx 24rpx 40rpx;
}

.state-tip {
  padding: 120rpx 0;
  text-align: center;
  color: #999999;
  font-size: 28rpx;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 160rpx 0;

  .empty-icon {
    font-size: 96rpx;
    margin-bottom: 24rpx;
  }

  .empty-text {
    font-size: 28rpx;
    color: #999999;
  }
}

/* 订单卡片 */
.order-card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 28rpx 28rpx 20rpx;
  margin-bottom: 24rpx;
  box-shadow: 0 4rpx 16rpx rgba(0, 0, 0, 0.05);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #f2f2f2;

  .order-number {
    font-size: 26rpx;
    color: #888888;
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    margin-right: 16rpx;
  }
}

.card-body {
  padding: 20rpx 0;
}

.info-row {
  display: flex;
  align-items: center;
  padding: 10rpx 0;

  .info-label {
    width: 96rpx;
    font-size: 26rpx;
    color: #999999;
    flex-shrink: 0;
  }

  .info-value {
    flex: 1;
    font-size: 28rpx;
    color: #333333;

    &.price {
      color: #ff6600;
      font-weight: 600;
    }
  }
}

/* 操作区 */
.card-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 20rpx;
  padding-top: 20rpx;
  border-top: 1rpx solid #f2f2f2;
}

.view-text {
  font-size: 24rpx;
  color: #bbbbbb;
}

.action-btn {
  height: 64rpx;
  line-height: 64rpx;
  padding: 0 32rpx;
  font-size: 26rpx;
  border-radius: 32rpx;
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
    color: #4DBA87;
    border: 2rpx solid #4DBA87;
  }

  &.danger-ghost {
    background: #ffffff;
    color: #ff6600;
    border: 2rpx solid #ff6600;
  }
}

/* 加载提示 */
.load-tip {
  text-align: center;
  padding: 24rpx 0;
  color: #cccccc;
  font-size: 24rpx;
}
</style>
