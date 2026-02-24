<!-- rag_demo/frontend/src/views/ChatView.vue -->
<template>
  <div class="chat-page">
    <!-- Animated Background -->
    <div class="bg-decoration">
      <div class="glow-orb glow-orb-1"></div>
      <div class="glow-orb glow-orb-2"></div>
      <div class="glow-orb glow-orb-3"></div>
    </div>

    <div class="chat-container">
      <!-- Header -->
      <header class="chat-header">
        <div class="header-left">
          <div class="logo-container">
            <svg class="logo-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="header-title">
            <h1>RAG <span class="text-gradient">Knowledge</span></h1>
            <p class="header-subtitle">Intelligent Q&A System</p>
          </div>
        </div>
        <button class="btn-secondary admin-btn" @click="goToAdmin">
          <svg class="btn-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M12 6V4M12 20V18M18 12H20M4 12H6M15.5355 8.46436L16.9497 7.05014M7.05025 16.9497L8.46447 15.5355M15.5355 15.5356L16.9497 16.9498M7.05025 7.05029L8.46447 8.46451" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>Manage</span>
        </button>
      </header>

      <!-- Chat Messages -->
      <main class="chat-messages" ref="messagesContainer">
        <!-- Welcome Message -->
        <div v-if="chatStore.messages.length === 0" class="welcome-container">
          <div class="welcome-icon animate-float">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M21 15C21 15.5304 20.7893 16.0391 20.4142 16.4142C20.0391 16.7893 19.5304 17 19 17H7L3 21V5C3 4.46957 3.21071 3.96086 3.58579 3.58579C3.96086 3.21071 4.46957 3 5 3H19C19.5304 3 20.0391 3.21071 20.4142 3.58579C20.7893 3.96086 21 4.46957 21 5V15Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <h2 class="welcome-title">Welcome to <span class="text-gradient">RAG Knowledge</span></h2>
          <p class="welcome-text">Ask any question about your uploaded documents. I'll search through the knowledge base and provide accurate answers.</p>
          <div class="quick-actions">
            <button
              v-for="(action, index) in quickActions"
              :key="index"
              class="quick-action-btn glass-card"
              @click="sendQuickAction(action)"
            >
              <span class="quick-action-icon">{{ action.icon }}</span>
              <span>{{ action.text }}</span>
            </button>
          </div>
        </div>

        <!-- Messages List -->
        <div v-else class="messages-list">
          <TransitionGroup name="message">
            <div
              v-for="(msg, index) in chatStore.messages"
              :key="index"
              :class="['message', msg.role]"
            >
              <div class="message-avatar" :class="msg.role">
                <svg v-if="msg.role === 'user'" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M20 21V19C20 17.9391 19.5786 16.9217 18.8284 16.1716C18.0783 15.4214 17.0609 15 16 15H8C6.93913 15 5.92172 15.4214 5.17157 16.1716C4.42143 16.9217 4 17.9391 4 19V21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <circle cx="12" cy="7" r="4" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
                <svg v-else viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <div class="message-content glass-card-solid">
                <div class="message-text">{{ msg.content }}</div>
              </div>
            </div>
          </TransitionGroup>
        </div>

        <!-- Loading State -->
        <div v-if="chatStore.loading" class="message assistant loading-message">
          <div class="message-avatar assistant">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L2 7L12 12L22 7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 17L12 22L22 17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M2 12L12 17L22 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="message-content glass-card-solid">
            <div class="typing-indicator">
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="dot"></span>
              <span class="typing-text">Thinking...</span>
            </div>
          </div>
        </div>
      </main>

      <!-- Input Area -->
      <footer class="chat-input-container glass-card">
        <div class="input-wrapper">
          <el-input
            v-model="inputMessage"
            placeholder="Ask a question about your documents..."
            @keyup.enter="sendMessage"
            :disabled="chatStore.loading"
            class="chat-input"
          >
            <template #prefix>
              <svg class="input-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <circle cx="11" cy="11" r="8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M21 21L16.65 16.65" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </template>
          </el-input>
          <div class="input-actions">
            <button
              class="btn-send"
              @click="sendMessage"
              :disabled="!inputMessage.trim() || chatStore.loading"
              :class="{ active: inputMessage.trim() && !chatStore.loading }"
            >
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M22 2L11 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M22 2L15 22L11 13L2 9L22 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
            <button class="btn-clear" @click="clearChat" :disabled="chatStore.messages.length === 0">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M3 6H5H21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M8 6V4C8 3.46957 8.21071 2.96086 8.58579 2.58579C8.96086 2.21071 9.46957 2 10 2H14C14.5304 2 15.0391 2.21071 15.4142 2.58579C15.7893 2.96086 16 3.46957 16 4V6M19 6V20C19 20.5304 18.7893 21.0391 18.4142 21.4142C18.0391 21.7893 17.5304 22 17 22H7C6.46957 22 5.96086 21.7893 5.58579 21.4142C5.21071 21.0391 5 20.5304 5 20V6H19Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
        </div>
      </footer>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { useChatStore } from '@/stores/chat'

const router = useRouter()
const chatStore = useChatStore()
const inputMessage = ref('')
const messagesContainer = ref<HTMLElement | null>(null)

const quickActions = [
  { icon: '📄', text: 'What documents are available?' },
  { icon: '🔍', text: 'Summarize the key topics' },
  { icon: '💡', text: 'What can you help me with?' },
]

const sendMessage = async () => {
  if (!inputMessage.value.trim() || chatStore.loading) return

  const message = inputMessage.value.trim()
  inputMessage.value = ''

  await chatStore.sendMessage(message)
  scrollToBottom()
}

const sendQuickAction = (action: { icon: string; text: string }) => {
  inputMessage.value = action.text
  sendMessage()
}

const clearChat = () => {
  chatStore.clearHistory()
}

const goToAdmin = () => {
  router.push('/admin')
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
    }
  })
}
</script>

<style scoped>
/* ============================================
   Page Layout
   ============================================ */
.chat-page {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

/* ============================================
   Background Decoration
   ============================================ */
.bg-decoration {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.4;
}

.glow-orb-1 {
  width: 400px;
  height: 400px;
  background: var(--color-accent-primary);
  top: -100px;
  right: -100px;
  animation: float 8s ease-in-out infinite;
}

.glow-orb-2 {
  width: 300px;
  height: 300px;
  background: var(--color-accent-secondary);
  bottom: -50px;
  left: -50px;
  animation: float 10s ease-in-out infinite reverse;
}

.glow-orb-3 {
  width: 200px;
  height: 200px;
  background: var(--color-accent-tertiary);
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  animation: float 6s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-30px) scale(1.05); }
}

/* ============================================
   Chat Container
   ============================================ */
.chat-container {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  height: 100vh;
  max-width: 900px;
  margin: 0 auto;
  padding: var(--space-6);
}

/* ============================================
   Header
   ============================================ */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4) 0;
  margin-bottom: var(--space-6);
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.logo-container {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gradient-accent);
  border-radius: var(--radius-xl);
  box-shadow: var(--shadow-glow);
}

.logo-icon {
  width: 24px;
  height: 24px;
  color: white;
}

.header-title h1 {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  margin: 0;
  letter-spacing: var(--letter-spacing-tight);
}

.header-subtitle {
  font-size: var(--font-size-sm);
  color: var(--color-text-tertiary);
  margin: var(--space-1) 0 0;
}

.admin-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-5);
  cursor: pointer;
}

.btn-icon {
  width: 18px;
  height: 18px;
}

/* ============================================
   Messages Area
   ============================================ */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-4);
  margin-bottom: var(--space-6);
}

/* Welcome Section */
.welcome-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: var(--space-12) var(--space-6);
  animation: fade-in-up 0.6s ease-out;
}

.welcome-icon {
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gradient-accent);
  border-radius: var(--radius-2xl);
  margin-bottom: var(--space-6);
  box-shadow: var(--shadow-glow-intense);
}

.welcome-icon svg {
  width: 40px;
  height: 40px;
  color: white;
}

.welcome-title {
  font-size: var(--font-size-3xl);
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--space-4);
}

.welcome-text {
  max-width: 500px;
  color: var(--color-text-secondary);
  font-size: var(--font-size-lg);
  line-height: var(--line-height-relaxed);
  margin-bottom: var(--space-8);
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-3);
  justify-content: center;
}

.quick-action-btn {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-5);
  cursor: pointer;
  font-size: var(--font-size-sm);
  color: var(--color-text-primary);
}

.quick-action-btn:hover {
  border-color: var(--color-accent-primary);
}

.quick-action-icon {
  font-size: var(--font-size-lg);
}

/* Messages List */
.messages-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-6);
}

.message {
  display: flex;
  gap: var(--space-4);
  animation: fade-in-up 0.3s ease-out;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.message-avatar.user {
  background: var(--gradient-accent);
  box-shadow: var(--shadow-glow);
}

.message-avatar.assistant {
  background: var(--color-bg-elevated);
  border: 1px solid var(--color-glass-border);
}

.message-avatar svg {
  width: 20px;
  height: 20px;
  color: white;
}

.message-avatar.assistant svg {
  color: var(--color-accent-primary);
}

.message-content {
  max-width: 75%;
  padding: var(--space-4) var(--space-5);
}

.message-text {
  color: var(--color-text-primary);
  line-height: var(--line-height-relaxed);
  white-space: pre-wrap;
  word-wrap: break-word;
}

/* Loading Animation */
.typing-indicator {
  display: flex;
  align-items: center;
  gap: var(--space-2);
}

.dot {
  width: 8px;
  height: 8px;
  background: var(--color-accent-primary);
  border-radius: 50%;
  animation: bounce 1.4s ease-in-out infinite;
}

.dot:nth-child(1) { animation-delay: 0s; }
.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounce {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-6px); }
}

.typing-text {
  color: var(--color-text-tertiary);
  font-size: var(--font-size-sm);
  margin-left: var(--space-2);
}

/* Message Transitions */
.message-enter-active {
  transition: all 0.3s ease-out;
}

.message-leave-active {
  transition: all 0.2s ease-in;
}

.message-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.message-leave-to {
  opacity: 0;
  transform: translateX(-20px);
}

/* ============================================
   Input Area
   ============================================ */
.chat-input-container {
  padding: var(--space-4);
  background: var(--gradient-glass);
  backdrop-filter: blur(20px);
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.chat-input {
  flex: 1;
}

.chat-input :deep(.el-input__wrapper) {
  padding: var(--space-3) var(--space-4);
  background: var(--color-glass-light) !important;
}

.input-icon {
  width: 18px;
  height: 18px;
  color: var(--color-text-tertiary);
}

.input-actions {
  display: flex;
  gap: var(--space-2);
}

.btn-send,
.btn-clear {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-glass-border);
  background: var(--color-glass-light);
  color: var(--color-text-tertiary);
  cursor: pointer;
  transition: all var(--transition-normal);
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-send:hover,
.btn-clear:hover {
  background: var(--color-glass-medium);
  border-color: var(--color-glass-border-hover);
  color: var(--color-text-primary);
}

.btn-send.active {
  background: var(--gradient-accent);
  border: none;
  color: white;
  box-shadow: var(--shadow-glow);
}

.btn-send svg,
.btn-clear svg {
  width: 18px;
  height: 18px;
}

.btn-send:disabled,
.btn-clear:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ============================================
   Responsive Design
   ============================================ */
@media (max-width: 768px) {
  .chat-container {
    padding: var(--space-4);
  }

  .header-title h1 {
    font-size: var(--font-size-xl);
  }

  .admin-btn span {
    display: none;
  }

  .welcome-title {
    font-size: var(--font-size-2xl);
  }

  .welcome-text {
    font-size: var(--font-size-base);
  }

  .message-content {
    max-width: 85%;
  }

  .glow-orb-1,
  .glow-orb-2 {
    width: 200px;
    height: 200px;
  }
}

@media (max-width: 480px) {
  .quick-actions {
    flex-direction: column;
  }

  .quick-action-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>
