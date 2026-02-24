// rag_demo/frontend/src/api/chat.ts
import request from './request'

export interface ChatRequest {
  session_id: string
  message: string
}

export interface ChatResponse {
  reply: string
}

export interface Message {
  role: string
  content: string
}

export interface ChatHistoryResponse {
  messages: Message[]
}

export const chatApi = {
  chat: (data: ChatRequest): Promise<ChatResponse> =>
    request.post('/chat', data),

  getHistory: (sessionId: string): Promise<ChatHistoryResponse> =>
    request.get('/chat/history', { params: { session_id: sessionId } }),

  clearHistory: (sessionId: string): Promise<{ success: boolean }> =>
    request.delete('/chat/history', { params: { session_id: sessionId } }),
}
