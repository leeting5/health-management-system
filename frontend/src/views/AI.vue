<template>
  <div class="ai-page">
    <aside class="conversation-sidebar">
      <div class="sidebar-header">
        <div>
          <h3>健康助手</h3>
          <p>AI 会话记录</p>
        </div>
        <el-button type="primary" :icon="Plus" circle @click="handleNewConversation" />
      </div>

      <div class="conversation-list" v-loading="conversationLoading">
        <div
          v-for="item in conversations"
          :key="item.id"
          :class="['conversation-item', { active: currentConversation?.id === item.id }]"
          @click="selectConversation(item)"
        >
          <div class="conversation-main">
            <el-icon><ChatDotRound /></el-icon>
            <div class="conversation-info">
              <div class="conversation-title">{{ item.title }}</div>
              <div class="conversation-time">{{ formatTime(item.updateTime) }}</div>
            </div>
          </div>
          <el-dropdown trigger="click" @command="(cmd) => handleConversationCommand(cmd, item)">
            <el-icon class="more-icon" @click.stop><MoreFilled /></el-icon>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="rename">
                  <el-icon><Edit /></el-icon>
                  重命名
                </el-dropdown-item>
                <el-dropdown-item command="delete" divided>
                  <el-icon><Delete /></el-icon>
                  删除会话
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <el-empty v-if="!conversationLoading && conversations.length === 0" description="暂无会话" />
      </div>
    </aside>

    <section class="chat-panel">
      <header class="chat-header">
        <div>
          <h3>{{ currentConversation?.title || '新对话' }}</h3>
          <p>基于 DeepSeek · 仅供健康参考</p>
        </div>
        <el-tag v-if="currentConversation" effect="plain" type="success">已选择会话</el-tag>
      </header>

      <div class="chat-messages" ref="messagesRef">
        <div v-if="messages.length === 0" class="welcome-message">
          <div class="welcome-icon">👋</div>
          <h3>你好！我是你的健康助手</h3>
          <p>有什么健康问题都可以问我，我会尽力为你解答。</p>
          <div class="quick-questions">
            <el-tag
              v-for="q in quickQuestions"
              :key="q"
              class="quick-tag"
              @click="sendQuickQuestion(q)"
              effect="plain"
              type="success"
            >
              {{ q }}
            </el-tag>
          </div>
          <div class="disclaimer">
            ⚠️ <strong>免责声明：</strong>AI 回复仅供健康参考，不能替代专业医疗诊断。如有严重不适，请及时就医。
          </div>
        </div>

        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message-item', msg.role === 'user' ? 'message-user' : 'message-ai']"
        >
          <div class="message-avatar">
            <el-avatar :size="36">{{ msg.role === 'user' ? '👤' : '🤖' }}</el-avatar>
          </div>
          <div class="message-content">
            <div class="message-bubble" v-loading="msg.loading">
              <div class="msg-role">{{ msg.role === 'user' ? '我' : '健康助手' }}</div>
              <p class="msg-text" v-html="formatMessage(msg.content)"></p>
            </div>
          </div>
        </div>
      </div>

      <footer class="chat-input">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="2"
          placeholder="输入你的健康问题..."
          @keydown.enter.ctrl="sendMessage"
          :disabled="isLoading"
          resize="none"
        />
        <div class="input-actions">
          <span class="tip-text">Ctrl + Enter 发送</span>
          <el-button
            type="primary"
            :icon="Position"
            :loading="isLoading"
            :disabled="!inputMessage.trim()"
            @click="sendMessage"
          >
            发送
          </el-button>
        </div>
      </footer>
    </section>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Position, ChatDotRound, MoreFilled, Edit, Delete } from '@element-plus/icons-vue'
import {
  getAiConversations,
  createAiConversation,
  renameAiConversation,
  deleteAiConversation,
  getAiMessages,
  aiChat
} from '@/api/ai'

const messagesRef = ref(null)
const conversations = ref([])
const currentConversation = ref(null)
const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)
const conversationLoading = ref(false)

const quickQuestions = [
  '最近总是失眠怎么办？',
  '血压偏高怎么调理？',
  'BMI 22 正常吗？',
  '每天运动多久比较好？'
]

function formatMessage(text) {
  if (!text) return ''
  return text
    .replace(/\n/g, '<br>')
    .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
}

function formatTime(value) {
  if (!value) return ''
  const date = new Date(value)
  const now = new Date()
  if (date.toDateString() === now.toDateString()) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
  }
  return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' })
}

async function fetchConversations() {
  conversationLoading.value = true
  try {
    const res = await getAiConversations()
    conversations.value = res.data || []
  } finally {
    conversationLoading.value = false
  }
}

async function handleNewConversation() {
  try {
    const res = await createAiConversation('新对话')
    conversations.value.unshift(res.data)
    await selectConversation(res.data)
  } catch (err) {
    console.error('创建会话失败:', err)
  }
}

async function selectConversation(conversation) {
  currentConversation.value = conversation
  messages.value = []
  try {
    const res = await getAiMessages(conversation.id)
    messages.value = (res.data || []).map((item) => ({
      role: item.role,
      content: item.content,
      loading: false
    }))
  } finally {
    scrollToBottom()
  }
}

async function handleConversationCommand(command, conversation) {
  if (command === 'rename') {
    try {
      const { value } = await ElMessageBox.prompt('请输入新的会话名称', '重命名会话', {
        confirmButtonText: '保存',
        cancelButtonText: '取消',
        inputValue: conversation.title,
        inputValidator: (val) => !!val.trim() || '名称不能为空'
      })
      await renameAiConversation(conversation.id, value.trim())
      conversation.title = value.trim()
      if (currentConversation.value?.id === conversation.id) {
        currentConversation.value.title = value.trim()
      }
      ElMessage.success('名称已修改')
    } catch (err) {
      if (err !== 'cancel' && err !== 'close') console.error('重命名失败:', err)
    }
  }

  if (command === 'delete') {
    try {
      await ElMessageBox.confirm('删除后会话和消息将无法恢复，确定删除吗？', '删除会话', {
        confirmButtonText: '删除',
        cancelButtonText: '取消',
        type: 'warning'
      })
      await deleteAiConversation(conversation.id)
      conversations.value = conversations.value.filter((item) => item.id !== conversation.id)
      if (currentConversation.value?.id === conversation.id) {
        currentConversation.value = null
        messages.value = []
      }
      ElMessage.success('会话已删除')
    } catch (err) {
      if (err !== 'cancel' && err !== 'close') console.error('删除失败:', err)
    }
  }
}

function sendQuickQuestion(question) {
  inputMessage.value = question
  sendMessage()
}

async function sendMessage() {
  const text = inputMessage.value.trim()
  if (!text || isLoading.value) return

  if (!currentConversation.value) {
    const res = await createAiConversation('新对话')
    conversations.value.unshift(res.data)
    currentConversation.value = res.data
  }

  messages.value.push({ role: 'user', content: text, loading: false })
  inputMessage.value = ''
  isLoading.value = true

  const aiMsgIndex = messages.value.length
  messages.value.push({ role: 'ai', content: '', loading: true })
  scrollToBottom()

  try {
    const res = await aiChat(currentConversation.value.id, text)
    messages.value[aiMsgIndex].content = res.data || '抱歉，AI 暂时无法回复。'
    messages.value[aiMsgIndex].loading = false
  } catch (err) {
    messages.value[aiMsgIndex].content = '网络异常，请检查网络连接。'
    messages.value[aiMsgIndex].loading = false
    console.error('AI chat error:', err)
  } finally {
    isLoading.value = false
    nextTick(() => scrollToBottom())
  }
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

onMounted(async () => {
  await fetchConversations()
  if (conversations.value.length > 0) {
    await selectConversation(conversations.value[0])
  }
})
</script>

<style scoped lang="scss">
.ai-page {
  display: flex;
  height: calc(100vh - 120px);
  overflow: hidden;
  background: #f5f7fa;
  border: 1px solid #eef0f4;
  border-radius: 16px;
}

.conversation-sidebar {
  display: flex;
  flex-direction: column;
  width: 280px;
  background: #fff;
  border-right: 1px solid #eef0f4;
}

.sidebar-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  border-bottom: 1px solid #f1f3f6;

  h3 {
    margin: 0 0 4px;
    font-size: 17px;
    color: #1f2937;
  }

  p {
    margin: 0;
    font-size: 12px;
    color: #909399;
  }
}

.conversation-list {
  flex: 1;
  padding: 12px;
  overflow-y: auto;
}

.conversation-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  margin-bottom: 8px;
  cursor: pointer;
  border: 1px solid transparent;
  border-radius: 12px;
  transition: background 0.2s ease, border-color 0.2s ease;

  &:hover {
    background: #f5f7fa;
  }

  &.active {
    background: #ecf5ff;
    border-color: #d9ecff;
  }

  .conversation-main {
    display: flex;
    align-items: center;
    gap: 10px;
    min-width: 0;
  }

  .conversation-info {
    min-width: 0;
  }

  .conversation-title {
    overflow: hidden;
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .conversation-time {
    margin-top: 3px;
    font-size: 12px;
    color: #909399;
  }

  .more-icon {
    color: #909399;
  }
}

.chat-panel {
  display: flex;
  flex: 1;
  flex-direction: column;
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 18px 24px;
  background: #fff;
  border-bottom: 1px solid #eef0f4;

  h3 {
    margin: 0 0 4px;
    font-size: 17px;
    color: #1f2937;
  }

  p {
    margin: 0;
    font-size: 12px;
    color: #909399;
  }
}

.chat-messages {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}

.welcome-message {
  max-width: 560px;
  padding: 40px 20px;
  margin: 0 auto;
  text-align: center;
  color: #606266;
}

.welcome-icon {
  margin-bottom: 12px;
  font-size: 48px;
}

.welcome-message h3 {
  margin-bottom: 8px;
  color: #303133;
}

.welcome-message p {
  margin-bottom: 20px;
  color: #909399;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 10px;
  margin-bottom: 20px;
}

.quick-tag {
  cursor: pointer;
  padding: 6px 12px;
  font-size: 13px;
}

.disclaimer {
  max-width: 500px;
  padding: 10px 16px;
  margin: 0 auto;
  font-size: 12px;
  line-height: 1.6;
  color: #e6a23c;
  text-align: left;
  background: #fdf6ec;
  border-radius: 8px;
}

.message-item {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.message-user {
  flex-direction: row-reverse;
}

.message-avatar {
  flex-shrink: 0;
}

.message-content {
  max-width: 72%;
}

.message-bubble {
  padding: 12px 16px;
  line-height: 1.6;
  word-wrap: break-word;
  border-radius: 12px;
}

.message-ai .message-bubble {
  background: #fff;
  border: 1px solid #ebeef5;
  border-top-left-radius: 4px;
}

.message-user .message-bubble {
  color: #fff;
  background: #409eff;
  border-top-right-radius: 4px;
}

.msg-role {
  margin-bottom: 4px;
  font-size: 11px;
  font-weight: bold;
  color: #67c23a;
}

.message-user .msg-role {
  color: rgba(255, 255, 255, 0.8);
}

.msg-text {
  margin: 0;
  font-size: 14px;
}

.message-user .msg-text {
  color: #fff;
}

.chat-input {
  padding: 16px 24px;
  background: #fff;
  border-top: 1px solid #eef0f4;
}

.input-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
}

.tip-text {
  font-size: 12px;
  color: #c0c4cc;
}

@media (max-width: 768px) {
  .ai-page {
    flex-direction: column;
    height: auto;
  }

  .conversation-sidebar {
    width: 100%;
    max-height: 220px;
    border-right: none;
    border-bottom: 1px solid #eef0f4;
  }
}
</style>
