import request from '@/utils/request'

// 获取会话列表
export function getAiConversations() {
  return request({
    url: '/ai/conversations',
    method: 'get'
  })
}

// 创建会话
export function createAiConversation(title) {
  return request({
    url: '/ai/conversations',
    method: 'post',
    data: { title }
  })
}

// 重命名会话
export function renameAiConversation(id, title) {
  return request({
    url: `/ai/conversations/${id}`,
    method: 'put',
    data: { title }
  })
}

// 删除会话
export function deleteAiConversation(id) {
  return request({
    url: `/ai/conversations/${id}`,
    method: 'delete'
  })
}

// 获取会话消息
export function getAiMessages(id) {
  return request({
    url: `/ai/conversations/${id}/messages`,
    method: 'get'
  })
}

// 在指定会话中发送消息
export function aiChat(conversationId, message) {
  return request({
    url: `/ai/conversations/${conversationId}/chat`,
    method: 'post',
    data: { message }
  })
}

// 获取健康建议
export function aiHealthAdvice(healthData) {
  return request({
    url: '/ai/health-advice',
    method: 'post',
    data: { healthData }
  })
}
