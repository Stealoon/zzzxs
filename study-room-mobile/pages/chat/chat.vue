<template>
  <view class="chat-container">
    <!-- 消息列表区域 -->
    <scroll-view
      class="chat-messages"
      scroll-y
      :scroll-top="scrollTop"
      :scroll-with-animation="true"
      :scroll-into-view="scrollIntoView"
    >
      <view class="message-list-inner">
        <view
          v-for="(msg, index) in messages"
          :key="index"
          class="message-item"
          :class="msg.role"
        >
          <!-- 头像 -->
          <view class="avatar" :class="msg.role">
            <text class="avatar-text">{{ msg.role === 'user' ? '我' : 'AI' }}</text>
          </view>
          <!-- 气泡 -->
          <view class="bubble-wrap">
            <text v-if="msg.time" class="msg-time">{{ formatMsgTime(msg.time) }}</text>
            <view class="bubble">{{ msg.content }}</view>
          </view>
        </view>

        <!-- 思考中加载状态 -->
        <view v-if="loading" class="message-item bot">
          <view class="avatar bot">
            <text class="avatar-text">AI</text>
          </view>
          <view class="bubble-wrap">
            <view class="bubble typing">
              <view class="typing-dot"></view>
              <view class="typing-dot"></view>
              <view class="typing-dot"></view>
            </view>
          </view>
        </view>

        <!-- 底部锚点 -->
        <view id="msg-bottom" class="scroll-anchor"></view>
      </view>
    </scroll-view>

    <!-- 底部输入区域 -->
    <view class="chat-input-area">
      <view class="input-wrap">
        <input
          v-model="inputText"
          class="chat-input"
          type="text"
          placeholder="请输入您的问题"
          placeholder-class="input-placeholder"
          :adjust-position="true"
          cursor-spacing="20"
          confirm-type="send"
          @confirm="handleSend"
        />
      </view>
      <button
        class="send-btn"
        :disabled="!inputText.trim() || loading"
        @click="handleSend"
      >
        发送
      </button>
    </view>
  </view>
</template>

<script>
import chatApi from '@/api/chat'

export default {
  data() {
    return {
      messages: [
        { role: 'bot', content: '您好，我是智能客服，有什么可以帮您的？', time: Date.now() }
      ],
      inputText: '',
      loading: false,
      scrollTop: 0,
      scrollIntoView: ''
    }
  },
  onLoad() {
    this.scrollToBottom()
  },
  onReady() {
    this.scrollToBottom()
  },
  onShow() {
    // 监听键盘弹起时滚动到底部
    uni.onKeyboardHeightChange(() => {
      this.scrollToBottom()
    })
  },
  onHide() {
    uni.offKeyboardHeightChange()
  },
  methods: {
    /** 格式化消息时间 */
    formatMsgTime(timestamp) {
      if (!timestamp) return ''
      const date = new Date(timestamp)
      const h = date.getHours() < 10 ? '0' + date.getHours() : '' + date.getHours()
      const m = date.getMinutes() < 10 ? '0' + date.getMinutes() : '' + date.getMinutes()
      return `${h}:${m}`
    },

    /** 发送消息 */
    async handleSend() {
      const text = this.inputText.trim()
      if (!text || this.loading) return

      // 添加用户消息
      this.messages.push({ role: 'user', content: text, time: Date.now() })
      this.inputText = ''
      this.loading = true
      this.scrollToBottom()

      try {
        const res = await chatApi.send(text)
        this.messages.push({ role: 'bot', content: res.reply, time: Date.now() })
      } catch (e) {
        this.messages.push({
          role: 'bot',
          content: '客服功能暂未开启或出现异常，请稍后再试',
          time: Date.now()
        })
      }

      this.loading = false
      this.scrollToBottom()
    },

    /** 滚动到底部 */
    scrollToBottom() {
      this.$nextTick(() => {
        // 使用 scroll-into-view 锚点方式，确保可靠滚动
        this.scrollIntoView = ''
        setTimeout(() => {
          this.scrollIntoView = 'msg-bottom'
          // 同时用 scroll-top 作为备用
          this.scrollTop = this.scrollTop === 99999 ? 99998 : 99999
        }, 50)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #ededed;
}

/* 消息列表 */
.chat-messages {
  flex: 1;
  overflow: hidden;
}

.message-list-inner {
  padding: 24rpx 24rpx 40rpx;
}

/* 单条消息 */
.message-item {
  display: flex;
  margin-bottom: 32rpx;

  &.bot {
    flex-direction: row;
  }

  &.user {
    flex-direction: row-reverse;
  }
}

/* 头像 */
.avatar {
  width: 76rpx;
  height: 76rpx;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;

  &.bot {
    background: linear-gradient(135deg, #4DBA87 0%, #6FCFA8 100%);
  }

  &.user {
    background: linear-gradient(135deg, #5b8def 0%, #7aa5f3 100%);
  }

  .avatar-text {
    font-size: 28rpx;
    font-weight: bold;
    color: #ffffff;
  }
}

/* 气泡区域 */
.bubble-wrap {
  max-width: 480rpx;
  display: flex;
  flex-direction: column;
  margin: 0 20rpx;
}

.user .bubble-wrap {
  align-items: flex-end;
}

.bot .bubble-wrap {
  align-items: flex-start;
}

/* 时间戳 */
.msg-time {
  font-size: 22rpx;
  color: #999999;
  margin-bottom: 8rpx;
  padding: 0 8rpx;
}

/* 气泡 */
.bubble {
  padding: 20rpx 28rpx;
  border-radius: 16rpx;
  font-size: 30rpx;
  line-height: 1.6;
  word-break: break-all;
}

.user .bubble {
  background-color: #4DBA87;
  color: #ffffff;
  border-top-right-radius: 4rpx;
}

.bot .bubble {
  background-color: #ffffff;
  color: #333333;
  border-top-left-radius: 4rpx;
}

/* 思考中动画 */
.bubble.typing {
  display: flex;
  align-items: center;
  gap: 12rpx;
  padding: 24rpx 28rpx;
}

.typing-dot {
  width: 14rpx;
  height: 14rpx;
  border-radius: 50%;
  background-color: #cccccc;
  animation: typingBounce 1.4s infinite ease-in-out;

  &:nth-child(2) {
    animation-delay: 0.2s;
  }

  &:nth-child(3) {
    animation-delay: 0.4s;
  }
}

@keyframes typingBounce {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  30% {
    transform: translateY(-10rpx);
    opacity: 1;
  }
}

/* 锚点 */
.scroll-anchor {
  height: 1rpx;
}

/* 底部输入区域 */
.chat-input-area {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  padding: 16rpx 24rpx;
  padding-bottom: calc(16rpx + env(safe-area-inset-bottom));
  background-color: #f7f7f7;
  border-top: 2rpx solid #dcdcdc;
  transition: padding-bottom 0.2s ease;
}

.input-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  background-color: #ffffff;
  border-radius: 12rpx;
  border: 2rpx solid #e0e0e0;
  padding: 0 24rpx;
  margin-right: 20rpx;
  height: 80rpx;
  transition: border-color 0.2s;

  &:focus-within {
    border-color: #4DBA87;
  }
}

.chat-input {
  flex: 1;
  height: 80rpx;
  font-size: 30rpx;
  color: #333333;
}

.input-placeholder {
  color: #bbbbbb;
  font-size: 30rpx;
}

/* 发送按钮 */
.send-btn {
  flex-shrink: 0;
  width: 140rpx;
  height: 80rpx;
  line-height: 80rpx;
  background-color: #4DBA87;
  color: #ffffff;
  font-size: 30rpx;
  font-weight: 600;
  border-radius: 12rpx;
  border: none;
  padding: 0;
  margin: 0;

  &::after {
    border: none;
  }

  &[disabled] {
    background-color: #a0d8c0;
    color: rgba(255, 255, 255, 0.8);
  }
}
</style>
