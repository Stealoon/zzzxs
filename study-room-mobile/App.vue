<script>
import { getToken, isLoggedIn } from '@/utils/auth'

export default {
  onLaunch() {
    console.log('App Launch - 共享自习室')
    // 检查token是否存在
    const token = getToken()
    if (!token) {
      console.log('未检测到登录token，请先登录')
    }
  },
  onShow() {
    console.log('App Show')
    // 应用从后台进入前台时的处理
    if (!isLoggedIn()) {
      // 未登录时重定向到登录页（仅非tabBar页面场景）
      console.log('当前未登录')
    }
  },
  onHide() {
    console.log('App Hide')
  }
}
</script>

<style lang="scss">
/* 全局样式 */
page {
  background-color: #f5f5f5;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC',
    'Hiragino Sans GB', 'Microsoft YaHei', 'Helvetica Neue', Helvetica, Arial,
    sans-serif;
  font-size: 28rpx;
  color: #333333;
  line-height: 1.6;
}

/* 全局通用样式类 */
.container {
  padding: 20rpx;
}

.card {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(0, 0, 0, 0.05);
}

.text-primary {
  color: #4DBA87;
}

.text-muted {
  color: #999999;
}

.text-warning {
  color: #ff9900;
}

.flex {
  display: flex;
}

.flex-center {
  display: flex;
  align-items: center;
  justify-content: center;
}

.flex-between {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.mt-20 {
  margin-top: 20rpx;
}

.mt-40 {
  margin-top: 40rpx;
}

.mb-20 {
  margin-bottom: 20rpx;
}

.p-30 {
  padding: 30rpx;
}
</style>
