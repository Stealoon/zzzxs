<template>
  <view class="register-page">
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

    <!-- 注册卡片 -->
    <view class="register-card">
      <text class="card-title">注册账号</text>

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
            placeholder="请输入密码（至少6位）"
            placeholder-class="placeholder"
          />
          <text class="pwd-toggle" @tap="showPassword = !showPassword">{{ showPassword ? '隐藏' : '显示' }}</text>
        </view>
      </view>

      <view class="form-item">
        <view class="input-wrap">
          <text class="input-icon">🔐</text>
          <input
            v-model="confirmPassword"
            class="form-input"
            :password="!showConfirmPassword"
            placeholder="请确认密码"
            placeholder-class="placeholder"
          />
          <text class="pwd-toggle" @tap="showConfirmPassword = !showConfirmPassword">{{ showConfirmPassword ? '隐藏' : '显示' }}</text>
        </view>
      </view>

      <view class="form-item">
        <view class="input-wrap">
          <text class="input-icon">👤</text>
          <input
            v-model="name"
            class="form-input"
            type="nickname"
            maxlength="20"
            placeholder="昵称（选填，默认'新用户'）"
            placeholder-class="placeholder"
          />
        </view>
      </view>

      <button class="register-btn" :loading="loading" :disabled="loading" @tap="handleRegister">
        {{ loading ? '注册中...' : '注 册' }}
      </button>

      <view class="login-link" @tap="goLogin">
        <text class="link-text">已有账号？去登录</text>
      </view>
    </view>

    <!-- 底部协议 -->
    <view class="footer">
      <text class="footer-text">注册即代表同意《用户协议》和《隐私政策》</text>
    </view>
  </view>
</template>

<script>
import userApi from '@/api/user'
import { setToken, setUserId, setUserInfo } from '@/utils/auth'

export default {
  data() {
    return {
      phone: '',
      password: '',
      confirmPassword: '',
      name: '',
      showPassword: false,
      showConfirmPassword: false,
      loading: false,
      statusBarHeight: 20
    }
  },
  onLoad() {
    const sysInfo = uni.getSystemInfoSync()
    this.statusBarHeight = sysInfo.statusBarHeight || 20
  },
  methods: {
    /** 表单校验 */
    validate() {
      if (!this.phone) {
        uni.showToast({ title: '请输入手机号', icon: 'none' })
        return false
      }
      if (!/^1[3-9]\d{9}$/.test(this.phone)) {
        uni.showToast({ title: '请输入正确的手机号', icon: 'none' })
        return false
      }
      if (!this.password) {
        uni.showToast({ title: '请输入密码', icon: 'none' })
        return false
      }
      if (this.password.length < 6) {
        uni.showToast({ title: '密码至少6位', icon: 'none' })
        return false
      }
      if (!this.confirmPassword) {
        uni.showToast({ title: '请确认密码', icon: 'none' })
        return false
      }
      if (this.password !== this.confirmPassword) {
        uni.showToast({ title: '两次密码不一致', icon: 'none' })
        return false
      }
      return true
    },

    /** 注册处理 */
    async handleRegister() {
      if (this.loading) return
      if (!this.validate()) return

      this.loading = true
      try {
        const registerData = {
          phone: this.phone,
          password: this.password,
          name: this.name.trim() || '新用户'
        }
        const res = await userApi.register(registerData)
        // res: { id, openid, token, name }
        setToken(res.token)
        setUserId(res.id)
        if (res.name) {
          setUserInfo({ name: res.name })
        } else {
          setUserInfo({ name: registerData.name })
        }

        uni.showToast({ title: '注册成功', icon: 'success' })
        setTimeout(() => {
          uni.switchTab({ url: '/pages/home/home' })
        }, 500)
      } catch (e) {
        // request.js 已自动 toast 错误
      } finally {
        this.loading = false
      }
    },

    /** 跳转登录页 */
    goLogin() {
      const pages = getCurrentPages()
      if (pages.length > 1) {
        uni.navigateBack()
      } else {
        uni.navigateTo({ url: '/pages/login/login' })
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.register-page {
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

/* 注册卡片 */
.register-card {
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

/* 注册按钮 */
.register-btn {
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

/* 登录链接 */
.login-link {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 40rpx;
  padding: 16rpx 0;

  .link-text {
    font-size: 28rpx;
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
