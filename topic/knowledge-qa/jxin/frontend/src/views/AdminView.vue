<!-- rag_demo/frontend/src/views/AdminView.vue -->
<template>
  <div class="admin-page">
    <!-- Animated Background -->
    <div class="bg-decoration">
      <div class="glow-orb glow-orb-1"></div>
      <div class="glow-orb glow-orb-2"></div>
    </div>

    <div class="admin-container">
      <!-- Header -->
      <header class="admin-header">
        <div class="header-left">
          <button class="back-btn glass-card" @click="goToChat">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M19 12H5M5 12L12 19M5 12L12 5" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
          <div class="header-title">
            <h1>Document <span class="text-gradient">Management</span></h1>
            <p class="header-subtitle">Upload and manage your knowledge base</p>
          </div>
        </div>
        <div class="header-stats">
          <div class="stat-item glass-card-solid">
            <span class="stat-value">{{ documents.length }}</span>
            <span class="stat-label">Documents</span>
          </div>
          <div class="stat-item glass-card-solid">
            <span class="stat-value">{{ totalChunks }}</span>
            <span class="stat-label">Chunks</span>
          </div>
        </div>
      </header>

      <!-- Upload Section -->
      <section class="upload-section glass-card">
        <div class="upload-header">
          <div class="upload-icon">
            <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M21 15V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M17 8L12 3L7 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M12 3V15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
          <div class="upload-info">
            <h3>Upload Document</h3>
            <p>Supports PDF, TXT, and Markdown files</p>
          </div>
        </div>

        <div class="upload-area" :class="{ 'has-file': selectedFile }">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :on-change="handleFileChange"
            :show-file-list="false"
            accept=".pdf,.txt,.md"
            drag
          >
            <div class="upload-content">
              <svg class="upload-drag-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M14 2H6C5.46957 2 4.96086 2.21071 4.58579 2.58579C4.21071 2.96086 4 3.46957 4 4V20C4 20.5304 4.21071 21.0391 4.58579 21.4142C4.96086 21.7893 5.46957 22 6 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V8L14 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M14 2V8H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M12 18V12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M9 15L12 12L15 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <p class="upload-text">
                <span class="upload-link">Click to upload</span> or drag and drop
              </p>
              <p class="upload-hint">PDF, TXT, or MD (MAX. 10MB)</p>
            </div>
          </el-upload>
        </div>

        <div v-if="selectedFile" class="selected-file glass-card-solid">
          <div class="file-info">
            <div class="file-icon">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M14 2H6C5.46957 2 4.96086 2.21071 4.58579 2.58579C4.21071 2.96086 4 3.46957 4 4V20C4 20.5304 4.21071 21.0391 4.58579 21.4142C4.96086 21.7893 5.46957 22 6 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V8L14 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M14 2V8H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <div class="file-details">
              <span class="file-name">{{ selectedFile.name }}</span>
              <span class="file-size">{{ formatFileSize(selectedFile.size) }}</span>
            </div>
          </div>
          <div class="file-actions">
            <button class="btn-upload" @click="uploadFile" :disabled="uploading">
              <svg v-if="!uploading" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M21 15V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M17 8L12 3L7 8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M12 3V15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <svg v-else class="animate-spin" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2V6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M12 18V22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M4.93 4.93L7.76 7.76" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M16.24 16.24L19.07 19.07" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M2 12H6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M18 12H22" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M4.93 19.07L7.76 16.24" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                <path d="M16.24 7.76L19.07 4.93" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              <span>{{ uploading ? 'Processing...' : 'Upload & Process' }}</span>
            </button>
            <button class="btn-cancel" @click="selectedFile = null" :disabled="uploading">
              <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M18 6L6 18M6 6L18 18" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </button>
          </div>
        </div>
      </section>

      <!-- Documents List -->
      <section class="documents-section">
        <div class="section-header">
          <h2>Uploaded Documents</h2>
          <button class="btn-refresh glass-card" @click="fetchDocuments" :disabled="loading">
            <svg :class="{ 'animate-spin': loading }" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M23 4V10H17" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M1 20V14H7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <path d="M3.51 9.00001C4.01717 7.56679 4.87913 6.28542 6.01547 5.27543C7.1518 4.26545 8.52547 3.55977 10.0083 3.22427C11.4911 2.88877 13.0348 2.93436 14.4952 3.35679C15.9556 3.77922 17.2853 4.56473 18.36 5.64001L23 10M1 14L5.64 18.36C6.71475 19.4353 8.04437 20.2208 9.50481 20.6432C10.9652 21.0657 12.5089 21.1113 13.9917 20.7758C15.4745 20.4403 16.8482 19.7346 17.9845 18.7246C19.1209 17.7146 19.9828 16.4332 20.49 15" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>

        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>Loading documents...</p>
        </div>

        <div v-else-if="documents.length === 0" class="empty-state glass-card">
          <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M14 2H6C5.46957 2 4.96086 2.21071 4.58579 2.58579C4.21071 2.96086 4 3.46957 4 4V20C4 20.5304 4.21071 21.0391 4.58579 21.4142C4.96086 21.7893 5.46957 22 6 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V8L14 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M14 2V8H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <h3>No documents yet</h3>
          <p>Upload your first document to get started</p>
        </div>

        <div v-else class="documents-grid">
          <TransitionGroup name="document">
            <div
              v-for="doc in documents"
              :key="doc.id"
              class="document-card glass-card"
            >
              <div class="doc-icon">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M14 2H6C5.46957 2 4.96086 2.21071 4.58579 2.58579C4.21071 2.96086 4 3.46957 4 4V20C4 20.5304 4.21071 21.0391 4.58579 21.4142C4.96086 21.7893 5.46957 22 6 22H18C18.5304 22 19.0391 21.7893 19.4142 21.4142C19.7893 21.0391 20 20.5304 20 20V8L14 2Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M14 2V8H20" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M16 13H8M16 17H8M10 9H8" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </div>
              <div class="doc-info">
                <h4 class="doc-name">{{ doc.filename }}</h4>
                <div class="doc-meta">
                  <span class="doc-chunks">
                    <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                      <rect x="3" y="3" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <rect x="14" y="3" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <rect x="14" y="14" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      <rect x="3" y="14" width="7" height="7" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    {{ doc.chunk_count }} chunks
                  </span>
                  <span class="doc-date">{{ formatDate(doc.created_at) }}</span>
                </div>
              </div>
              <button class="btn-delete" @click="deleteDocument(doc.id)" title="Delete document">
                <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M3 6H5H21" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                  <path d="M8 6V4C8 3.46957 8.21071 2.96086 8.58579 2.58579C8.96086 2.21071 9.46957 2 10 2H14C14.5304 2 15.0391 2.21071 15.4142 2.58579C15.7893 2.96086 16 3.46957 16 4V6M19 6V20C19 20.5304 18.7893 21.0391 18.4142 21.4142C18.0391 21.7893 17.5304 22 17 22H7C6.46957 22 5.96086 21.7893 5.58579 21.4142C5.21071 21.0391 5 20.5304 5 20V6H19Z" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>
          </TransitionGroup>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { documentApi, type Document } from '@/api/document'

const router = useRouter()
const documents = ref<Document[]>([])
const loading = ref(false)
const uploading = ref(false)
const selectedFile = ref<File | null>(null)

const totalChunks = computed(() => {
  return documents.value.reduce((sum, doc) => sum + doc.chunk_count, 0)
})

const fetchDocuments = async () => {
  loading.value = true
  try {
    const response = await documentApi.list()
    documents.value = response.documents
  } catch (error) {
    ElMessage.error('Failed to fetch documents')
  } finally {
    loading.value = false
  }
}

const handleFileChange = (file: any) => {
  selectedFile.value = file.raw
}

const uploadFile = async () => {
  if (!selectedFile.value) return

  uploading.value = true
  try {
    const response = await documentApi.upload(selectedFile.value)
    ElMessage.success(`Upload successful! Document split into ${response.chunks} chunks`)
    selectedFile.value = null
    await fetchDocuments()
  } catch (error) {
    ElMessage.error('Upload failed')
  } finally {
    uploading.value = false
  }
}

const deleteDocument = async (docId: string) => {
  try {
    await ElMessageBox.confirm('Are you sure you want to delete this document?', 'Confirm Delete', {
      type: 'warning',
    })
    await documentApi.delete(docId)
    ElMessage.success('Document deleted')
    await fetchDocuments()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('Delete failed')
    }
  }
}

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const formatFileSize = (bytes: number) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

const goToChat = () => {
  router.push('/')
}

onMounted(() => {
  fetchDocuments()
})
</script>

<style scoped>
/* ============================================
   Page Layout
   ============================================ */
.admin-page {
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
  filter: blur(100px);
  opacity: 0.3;
}

.glow-orb-1 {
  width: 500px;
  height: 500px;
  background: var(--color-accent-secondary);
  top: -150px;
  left: -150px;
  animation: float 12s ease-in-out infinite;
}

.glow-orb-2 {
  width: 400px;
  height: 400px;
  background: var(--color-accent-tertiary);
  bottom: -100px;
  right: -100px;
  animation: float 10s ease-in-out infinite reverse;
}

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-30px) scale(1.05); }
}

/* ============================================
   Admin Container
   ============================================ */
.admin-container {
  position: relative;
  z-index: 1;
  max-width: 1000px;
  margin: 0 auto;
  padding: var(--space-6);
}

/* ============================================
   Header
   ============================================ */
.admin-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-8);
  flex-wrap: wrap;
  gap: var(--space-4);
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--space-4);
}

.back-btn {
  width: 44px;
  height: 44px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  padding: 0;
  border: none;
}

.back-btn svg {
  width: 20px;
  height: 20px;
  color: var(--color-text-primary);
}

.back-btn:hover {
  border-color: var(--color-accent-primary);
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

.header-stats {
  display: flex;
  gap: var(--space-3);
}

.stat-item {
  padding: var(--space-3) var(--space-5);
  text-align: center;
  min-width: 100px;
}

.stat-value {
  display: block;
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  background: var(--gradient-accent);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: var(--font-size-xs);
  color: var(--color-text-tertiary);
  text-transform: uppercase;
  letter-spacing: var(--letter-spacing-wide);
}

/* ============================================
   Upload Section
   ============================================ */
.upload-section {
  padding: var(--space-6);
  margin-bottom: var(--space-8);
}

.upload-header {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  margin-bottom: var(--space-6);
}

.upload-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gradient-accent);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-glow);
}

.upload-icon svg {
  width: 24px;
  height: 24px;
  color: white;
}

.upload-info h3 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  margin: 0;
}

.upload-info p {
  font-size: var(--font-size-sm);
  color: var(--color-text-tertiary);
  margin: var(--space-1) 0 0;
}

.upload-area {
  border: 2px dashed var(--color-glass-border);
  border-radius: var(--radius-xl);
  transition: all var(--transition-normal);
  overflow: hidden;
}

.upload-area:hover {
  border-color: var(--color-accent-primary);
  background: var(--color-glass-light);
}

.upload-area.has-file {
  border-color: var(--color-success);
  background: var(--color-success-bg);
}

.upload-content {
  padding: var(--space-8);
  text-align: center;
}

.upload-drag-icon {
  width: 48px;
  height: 48px;
  margin: 0 auto var(--space-4);
  color: var(--color-text-tertiary);
}

.upload-text {
  font-size: var(--font-size-base);
  color: var(--color-text-secondary);
  margin: 0 0 var(--space-2);
}

.upload-link {
  color: var(--color-accent-primary);
  font-weight: var(--font-weight-medium);
  cursor: pointer;
}

.upload-hint {
  font-size: var(--font-size-sm);
  color: var(--color-text-tertiary);
  margin: 0;
}

/* Selected File */
.selected-file {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-4);
  margin-top: var(--space-4);
  animation: fade-in-up 0.3s ease-out;
}

.file-info {
  display: flex;
  align-items: center;
  gap: var(--space-3);
}

.file-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--gradient-accent);
  border-radius: var(--radius-md);
}

.file-icon svg {
  width: 20px;
  height: 20px;
  color: white;
}

.file-details {
  display: flex;
  flex-direction: column;
}

.file-name {
  font-weight: var(--font-weight-medium);
  color: var(--color-text-primary);
}

.file-size {
  font-size: var(--font-size-sm);
  color: var(--color-text-tertiary);
}

.file-actions {
  display: flex;
  gap: var(--space-2);
}

.btn-upload {
  display: flex;
  align-items: center;
  gap: var(--space-2);
  padding: var(--space-3) var(--space-5);
  background: var(--gradient-accent);
  border: none;
  border-radius: var(--radius-lg);
  color: white;
  font-weight: var(--font-weight-medium);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.btn-upload:hover:not(:disabled) {
  box-shadow: var(--shadow-glow);
  transform: translateY(-1px);
}

.btn-upload:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.btn-upload svg {
  width: 18px;
  height: 18px;
}

.btn-cancel {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-glass-medium);
  border: 1px solid var(--color-glass-border);
  border-radius: var(--radius-lg);
  color: var(--color-text-tertiary);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.btn-cancel:hover:not(:disabled) {
  background: var(--color-error-bg);
  border-color: var(--color-error);
  color: var(--color-error);
}

.btn-cancel:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-cancel svg {
  width: 18px;
  height: 18px;
}

/* ============================================
   Documents Section
   ============================================ */
.documents-section {
  margin-bottom: var(--space-8);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-6);
}

.section-header h2 {
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  margin: 0;
}

.btn-refresh {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  padding: 0;
  border: none;
}

.btn-refresh svg {
  width: 18px;
  height: 18px;
  color: var(--color-text-secondary);
}

.btn-refresh:hover:not(:disabled) {
  border-color: var(--color-accent-primary);
}

.btn-refresh:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* Loading State */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-16);
  color: var(--color-text-tertiary);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--color-glass-border);
  border-top-color: var(--color-accent-primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: var(--space-4);
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* Empty State */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-16);
  text-align: center;
}

.empty-state svg {
  width: 64px;
  height: 64px;
  color: var(--color-text-muted);
  margin-bottom: var(--space-4);
}

.empty-state h3 {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  margin: 0 0 var(--space-2);
  color: var(--color-text-secondary);
}

.empty-state p {
  color: var(--color-text-tertiary);
  margin: 0;
}

/* Documents Grid */
.documents-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--space-4);
}

.document-card {
  display: flex;
  align-items: center;
  gap: var(--space-4);
  padding: var(--space-4);
  cursor: default;
}

.document-card:hover {
  border-color: var(--color-glass-border-hover);
}

.doc-icon {
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--color-bg-elevated);
  border-radius: var(--radius-lg);
  flex-shrink: 0;
}

.doc-icon svg {
  width: 22px;
  height: 22px;
  color: var(--color-accent-primary);
}

.doc-info {
  flex: 1;
  min-width: 0;
}

.doc-name {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  margin: 0 0 var(--space-1);
  color: var(--color-text-primary);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.doc-meta {
  display: flex;
  align-items: center;
  gap: var(--space-3);
  font-size: var(--font-size-sm);
  color: var(--color-text-tertiary);
}

.doc-chunks {
  display: flex;
  align-items: center;
  gap: var(--space-1);
}

.doc-chunks svg {
  width: 14px;
  height: 14px;
}

.doc-date {
  font-size: var(--font-size-xs);
}

.btn-delete {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: 1px solid transparent;
  border-radius: var(--radius-md);
  color: var(--color-text-tertiary);
  cursor: pointer;
  transition: all var(--transition-normal);
  flex-shrink: 0;
}

.btn-delete:hover {
  background: var(--color-error-bg);
  border-color: var(--color-error);
  color: var(--color-error);
}

.btn-delete svg {
  width: 16px;
  height: 16px;
}

/* Document Transitions */
.document-enter-active {
  transition: all 0.3s ease-out;
}

.document-leave-active {
  transition: all 0.2s ease-in;
}

.document-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.document-leave-to {
  opacity: 0;
  transform: scale(0.9);
}

/* Utility */
.animate-spin {
  animation: spin 1s linear infinite;
}

/* ============================================
   Responsive Design
   ============================================ */
@media (max-width: 768px) {
  .admin-container {
    padding: var(--space-4);
  }

  .admin-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-stats {
    width: 100%;
    justify-content: flex-start;
  }

  .upload-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .selected-file {
    flex-direction: column;
    gap: var(--space-4);
  }

  .file-actions {
    width: 100%;
  }

  .btn-upload {
    flex: 1;
    justify-content: center;
  }

  .documents-grid {
    grid-template-columns: 1fr;
  }

  .glow-orb-1,
  .glow-orb-2 {
    width: 200px;
    height: 200px;
  }
}
</style>
