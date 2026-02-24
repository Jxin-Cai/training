// rag_demo/frontend/src/stores/chat.ts
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { chatApi } from '@/api/chat'

export interface Message {
  role: 'user' | 'assistant'
  content: string
}

export const useChatStore = defineStore('chat', () => {
  const messages = ref<Message[]>([])
  const sessionId = ref<string>(generateSessionId())
  const loading = ref(false)

  function generateSessionId(): string {
    return 'session_' + Date.now() + '_' + Math.random().toString(36).substr(2, 9)
  }

  async function sendMessage(content: string) {
    // 添加用户消息
    messages.value.push({ role: 'user', content })
    loading.value = true

    try {
      const response = await chatApi.chat({
        session_id: sessionId.value,
        message: content,
      })
      // 添加 AI 回复
      messages.value.push({ role: 'assistant', content: response.reply })
    } catch (error) {
      messages.value.push({
        role: 'assistant',
        content: '抱歉，发生错误，请稍后重试。',
      })
    } finally {
      loading.value = false
    }
  }

  function clearMessages() {
    messages.value = []
  }

  async function clearHistory() {
    await chatApi.clearHistory(sessionId.value)
    clearMessages()
  }

  return {
    messages,
    sessionId,
    loading,
    sendMessage,
    clearMessages,
    clearHistory,
  }
})
