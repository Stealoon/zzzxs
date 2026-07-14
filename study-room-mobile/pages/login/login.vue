<template>
  <view class="login-page">
    <!-- 顶部背景区域 -->
    <view class="header" :style="{ paddingTop: statusBarHeight + 'px' }">
      <view class="logo-wrap">
        <view class="logo-icon">
          <text class="iconfont">📚</text>
        </view>
        <text class="app-title">共享自习室</text>
        <text class="app-subtitle">专注 · 高效 · 共享学习空间</text>
      </view>
    </view>

    <!-- 登录卡片 -->
    <view class="login-card">
      <text class="card-title">账号登录</text>

      <view class="form-item">
        <view class="input-wrap">
          <text class="input-icon">📱</text>
          <input
            v-model="phone"
            class="form-input"
            type="number"
            maxlength="11"
            placeholder="请输入手机号"
            placeholder-class="placeholder"
          />
        </view>
      </view>

      <view class="form-item">
        <view class="input-wrap">
          <text class="input-icon">🔒</text>
          <input
            v-model="password"
            class="form-input"
            :password="!showPassword"
            placeholder="请输入密码"
            placeholder-class="placeholder"
          />
          <text class="pwd-toggle" @tap="showPassword = !showPassword">{{ showPassword ? '隐藏' : '显示' }}</text>
        </view>
      </view>

      <button class="login-btn" :loading="loading" :disabled="loading" @tap="handleLogin">
        {{ loading ? '登录中...' : '登 录' }}
      </button>

      <!-- 测试账号提示 -->
      <view class="test-account">
        <text class="test-label">测试账号：</text>
        <text class="test-value" @longpress="fillTestAccount">13800138000 / 123456</text>
      </view>

      <!-- 注册入口 -->
      <view class="register-link" @tap="goRegister">
        <text class="link-text">还没有账号？立即注册</text>
      </view>
    </view>

    <!-- 底部协议 -->
    <view class="footer">
      <text class="footer-text">登录即代表同意《用户协议》和《隐私政策》</text>
    </view>
  </view>
</template>

<script>
import userApi from '@/api/user'
import { setToken, setUserId, setUserInfo, isLoggedIn } from '@/utils/auth'

export default {
  data() {
    return {
      phone: '',
      password: '',
      showPassword: false,
      loading: false,
      statusBarHeight: 20
    }
  },
  onLoad() {
    // 获取状态栏高度（custom导航栏需要适配）
    const sysInfo = uni.getSystemInfoSync()
    this.statusBarHeight = sysInfo.statusBarHeight || 20

    // 已有有效token直接跳转首页
    if (isLoggedIn()) {
      uni.switchTab({ url: '/pages/home/home' })
    }
  },
  methods: {
    /** 跳转注册页 */
    goRegister() {
      uni.navigateTo({ url: '/pages/login/register' })
    },

    /** 填充测试账号（长按触发） */
    fillTestAccount() {
      this.phone = '13800138000'
      this.password = '123456'
      uni.showToast({ title: '已填充测试账号', icon: 'none' })
    },

    /** 登录处理 */
    async handleLogin() {
      if (!this.phone || !this.password) {
        uni.showToast({ title: '请输入手机号和密码', icon: 'none' })
        return
      }
      if (!/^1[3-9]\d{9}$/.test(this.phone)) {
        uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
        return
      }

      this.loading = true
      try {
        const res = await userApi.loginByPassword(this.phone, this.password)
        // res: { id, openid, name, token }
        setToken(res.token)
        setUserId(res.id)
        uni.setStorageSync('openid', res.openid)

        // 先用登录返回的 name 做快速显示
        if (res.name) {
          setUserInfo({ name: res.name })
        }

        // 异步获取完整用户信息（头像等）
        try {
          const profile = await userApi.getProfile()
          setUserInfo(profile)
        } catch (e) {
          // 获取完整信息失败不影响登录流程
        }

        uni.showToast({ title: '登录成功', icon: 'success' })
        setTimeout(() => {
          uni.switchTab({ url: '/pages/home/home' })
        }, 500)
      } catch (e) {
        // request.js 已自动 toast 错误
      } finally {
        this.loading = false
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #4DBA87 0%, #4DBA87 38%, #f5f5f5 38%, #f5f5f5 100%);
}

/* 顶部区域 */
.header {
  width: 100%;
  padding-bottom: 80rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-icon {
  width: 120rpx;
  height: 120rpx;
  border-radius: 30rpx;
  background: rgba(255, 255, 255, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20rpx;

  .iconfont {
    font-size: 56rpx;
  }
}

.app-title {
  font-size: 48rpx;
  font-weight: bold;
  color: #ffffff;
  letter-spacing: 4rpx;
}

.app-subtitle {
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.85);
  margin-top: 12rpx;
}

/* 登录卡片 */
.login-card {
  margin: 0 40rpx;
  padding: 50rpx 40rpx;
  background: #ffffff;
  border-radius: 24rpx;
  box-shadow: 0 8rpx 30rpx rgba(0, 0, 0, 0.1);
}

.card-title {
  font-size: 36rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 40rpx;
  display: block;
}

.form-item {
  margin-bottom: 30rpx;
}

.input-wrap {
  display: flex;
  align-items: center;
  border: 2rpx solid #e8e8e8;
  border-radius: 16rpx;
  padding: 0 24rpx;
  height: 96rpx;
  background: #fafafa;
  transition: border-color 0.2s;

  &:focus-within {
    border-color: #4DBA87;
    background: #ffffff;
  }
}

.input-icon {
  font-size: 36rpx;
  margin-right: 16rpx;
  flex-shrink: 0;
}

.form-input {
  flex: 1;
  height: 96rpx;
  font-size: 30rpx;
  color: #333333;
}

.placeholder {
  color: #bbbbbb;
  font-size: 30rpx;
}

.pwd-toggle {
  font-size: 24rpx;
  color: #4DBA87;
  flex-shrink: 0;
  padding-left: 16rpx;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 96rpx;
  line-height: 96rpx;
  background: #4DBA87;
  color: #ffffff;
  font-size: 32rpx;
  font-weight: bold;
  border-radius: 16rpx;
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

/* 注册入口 */
.register-link {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 30rpx;
  padding: 16rpx 0;

  .link-text {
    font-size: 28rpx;
    color: #4DBA87;
    font-weight: 500;
  }
}

/* 测试账号 */
.test-account {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 40rpx;
  padding: 16rpx 0;

  .test-label {
    font-size: 24rpx;
    color: #999999;
  }

  .test-value {
    font-size: 24rpx;
    color: #4DBA87;
    font-weight: 500;
  }
}

/* 底部 */
.footer {
  flex: 1;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  padding-bottom: 40rpx;
  margin-top: 20rpx;
}

.footer-text {
  font-size: 22rpx;
  color: #cccccc;
}
</style>
