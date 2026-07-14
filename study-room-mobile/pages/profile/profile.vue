<template>
  <view class="profile-page">
    <!-- 顶部用户信息区 -->
    <view class="user-header">
      <view class="user-info" @tap="handleHeaderTap">
        <view class="avatar-wrap">
          <image class="avatar" :src="avatarUrl" mode="aspectFill"></image>
          <view class="avatar-border"></view>
        </view>
        <view class="user-detail">
          <text class="user-name" v-if="loggedIn">{{ userName }}</text>
          <text class="user-name" v-else>未登录</text>
          <text class="user-desc" v-if="loggedIn">ID: {{ userId }}</text>
          <text class="user-desc" v-else>点击登录以使用更多功能</text>
        </view>
        <view class="login-btn-wrap" v-if="!loggedIn">
          <text class="go-login-btn">去登录</text>
        </view>
      </view>
    </view>

    <!-- 功能列表 -->
    <view class="menu-section">
      <view class="menu-item" @tap="goPage('/pages/order/order-list')">
        <view class="menu-icon icon-bg-green">📋</view>
        <text class="menu-text">我的预约</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goPage('/pages/package/package-list')">
        <view class="menu-icon icon-bg-orange">🎁</view>
        <text class="menu-text">套餐中心</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <view class="menu-section">
      <view class="menu-item" @tap="goPage('/pages/chat/chat')">
        <view class="menu-icon icon-bg-blue">💬</view>
        <text class="menu-text">智能客服</text>
        <text class="menu-arrow">›</text>
      </view>
      <view class="menu-item" @tap="goPage('/pages/checkin/checkin')">
        <view class="menu-icon icon-bg-purple">📍</view>
        <text class="menu-text">签到</text>
        <text class="menu-arrow">›</text>
      </view>
    </view>

    <!-- 退出登录 -->
    <view class="menu-section" v-if="loggedIn">
      <view class="menu-item logout-item" @tap="handleLogout">
        <text class="logout-text">退出登录</text>
      </view>
    </view>

    <!-- 底部版本号 -->
    <view class="version-footer">
      <text class="version-text">共享自习室 v1.0.0</text>
    </view>
  </view>
</template>

<script>
import { isLoggedIn, logout, getUserId, getUserInfo, setUserInfo } from '@/utils/auth'
import { formatImageurl } from '@/utils/format'
import userApi from '@/api/user'

export default {
  data() {
    return {
      loggedIn: false,
      userId: null,
      userName: '自习室用户',
      avatarUrl: ''
    }
  },
  onShow() {
    this.checkLoginStatus()
  },
  methods: {
    /** 检查登录状态 */
    async checkLoginStatus() {
      this.loggedIn = isLoggedIn()
      this.userId = getUserId()
      if (this.loggedIn) {
        // 先从本地缓存恢复，再异步刷新
        this.loadLocalUserInfo()
        await this.fetchProfile()
      } else {
        this.userName = '自习室用户'
        this.avatarUrl = ''
      }
    },

    /** 从本地缓存加载用户信息（快速显示） */
    loadLocalUserInfo() {
      const userInfo = getUserInfo()
      if (userInfo) {
        this.userName = userInfo.name || userInfo.nickName || '自习室用户'
        this.avatarUrl = userInfo.avatar ? formatImageurl(userInfo.avatar) : ''
      }
    },

    /** 从后端获取最新用户信息 */
    async fetchProfile() {
      try {
        const res = await userApi.getProfile()
        // 存入本地缓存
        setUserInfo(res)
        // 更新页面显示
        this.userName = res.name || '自习室用户'
        this.avatarUrl = res.avatar ? formatImageurl(res.avatar) : ''
      } catch (e) {
        // 获取失败时保持本地缓存数据
      }
    },

    /** 点击头部区域 */
    handleHeaderTap() {
      if (!this.loggedIn) {
        uni.navigateTo({ url: '/pages/login/login' })
      }
    },

    /** 跳转页面 */
    goPage(url) {
      if (!this.loggedIn) {
        uni.showModal({
          title: '提示',
          content: '请先登录',
          confirmText: '去登录',
          success: (res) => {
            if (res.confirm) {
              uni.navigateTo({ url: '/pages/login/login' })
            }
          }
        })
        return
      }
      uni.navigateTo({ url })
    },

    /** 退出登录 */
    handleLogout() {
      uni.showModal({
        title: '提示',
        content: '确定退出登录吗？',
        success: (res) => {
          if (res.confirm) {
            logout()
            this.checkLoginStatus()
            uni.showToast({ title: '已退出登录', icon: 'none' })
            setTimeout(() => {
              uni.navigateTo({ url: '/pages/login/login' })
            }, 500)
          }
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.profile-page {
  min-height: 100vh;
  background: #f5f5f5;
  padding-bottom: 40rpx;
}

/* 顶部用户信息区 */
.user-header {
  background: linear-gradient(135deg, #4DBA87 0%, #3DA975 100%);
  padding: 50rpx 30rpx 60rpx;
}

.user-info {
  display: flex;
  align-items: center;
}

.avatar-wrap {
  position: relative;
  width: 128rpx;
  height: 128rpx;
  flex-shrink: 0;
}

.avatar {
  width: 128rpx;
  height: 128rpx;
  border-radius: 50%;
  background: #e8e8e8;
  border: 4rpx solid rgba(255, 255, 255, 0.6);
}

.avatar-border {
  position: absolute;
  top: -6rpx;
  left: -6rpx;
  right: -6rpx;
  bottom: -6rpx;
  border: 2rpx solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
}

.user-detail {
  flex: 1;
  margin-left: 30rpx;
  display: flex;
  flex-direction: column;
}

.user-name {
  font-size: 36rpx;
  font-weight: bold;
  color: #ffffff;
}

.user-desc {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.8);
  margin-top: 10rpx;
}

.login-btn-wrap {
  flex-shrink: 0;
}

.go-login-btn {
  display: inline-block;
  padding: 12rpx 36rpx;
  font-size: 26rpx;
  color: #4DBA87;
  background: #ffffff;
  border-radius: 30rpx;
  font-weight: 600;
}

/* 功能菜单 */
.menu-section {
  margin: 20rpx 30rpx 0;
  background: #ffffff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.04);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 30rpx;
  position: relative;
  transition: background 0.15s;

  &:active {
    background: #f7f7f7;
  }

  &:not(:last-child)::after {
    content: '';
    position: absolute;
    left: 100rpx;
    right: 0;
    bottom: 0;
    height: 1rpx;
    background: #f0f0f0;
  }
}

.menu-icon {
  width: 64rpx;
  height: 64rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32rpx;
  margin-right: 24rpx;
  flex-shrink: 0;

  &.icon-bg-green {
    background: #e8f7ef;
  }

  &.icon-bg-orange {
    background: #fff4e6;
  }

  &.icon-bg-blue {
    background: #e6f4ff;
  }

  &.icon-bg-purple {
    background: #f3e8ff;
  }
}

.menu-text {
  flex: 1;
  font-size: 30rpx;
  color: #333333;
}

.menu-arrow {
  font-size: 40rpx;
  color: #cccccc;
  line-height: 1;
}

/* 退出登录 */
.logout-item {
  justify-content: center;
  padding: 36rpx 30rpx;

  &:not(:last-child)::after {
    display: none;
  }
}

.logout-text {
  font-size: 30rpx;
  color: #ff4d4f;
  font-weight: 500;
}

/* 底部版本号 */
.version-footer {
  display: flex;
  justify-content: center;
  padding: 60rpx 0 20rpx;
}

.version-text {
  font-size: 22rpx;
  color: #cccccc;
}
</style>
