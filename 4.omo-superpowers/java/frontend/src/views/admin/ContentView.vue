<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { MdEditor } from 'md-editor-v3'
import 'md-editor-v3/lib/style.css'
import { useContentStore } from '@/stores/content'
import { useCategoryStore } from '@/stores/category'
import { storeToRefs } from 'pinia'
import { imageApi } from '@/api'
import type { CreateContentRequest, UpdateContentRequest } from '@/types'

const contentStore = useContentStore()
const categoryStore = useCategoryStore()
const { contents, loading } = storeToRefs(contentStore)
const { categories, flatCategories } = storeToRefs(categoryStore)

const showModal = ref(false)
const editingId = ref<string | null>(null)
const formData = ref<CreateContentRequest>({ title: '', markdownContent: '', categoryId: '' })
const fileInputRef = ref<HTMLInputElement | null>(null)

onMounted(() => {
  contentStore.fetchContents()
  categoryStore.fetchCategories()
})

const sortedContents = computed(() => {
  return [...contents.value].sort((a, b) => 
    new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()
  )
})

function openCreateModal() {
  editingId.value = null
  formData.value = { title: '', markdownContent: '', categoryId: categories.value[0]?.id || '' }
  showModal.value = true
}

function openEditModal(content: { id: string; title: string; markdownContent: string; categoryId: string }) {
  editingId.value = content.id
  formData.value = { 
    title: content.title, 
    markdownContent: content.markdownContent, 
    categoryId: content.categoryId 
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingId.value = null
  formData.value = { title: '', markdownContent: '', categoryId: '' }
}

function triggerFileUpload() {
  fileInputRef.value?.click()
}

function handleFileUpload(event: Event) {
  const target = event.target as HTMLInputElement
  const file = target.files?.[0]
  if (!file) return
  
  if (!file.name.endsWith('.md')) {
    alert('请选择 .md 格式的文件')
    return
  }
  
  const reader = new FileReader()
  reader.onload = (e) => {
    const content = e.target?.result as string
    formData.value.markdownContent = content
    if (!formData.value.title) {
      formData.value.title = file.name.replace(/\.md$/, '')
    }
  }
  reader.readAsText(file, 'UTF-8')
  target.value = ''
}

async function handleSubmit() {
  try {
    if (editingId.value) {
      await contentStore.updateContent(editingId.value, formData.value as UpdateContentRequest)
    } else {
      await contentStore.createContent(formData.value)
    }
    closeModal()
  } catch (e) {
    console.error(e)
  }
}

async function handlePublish(id: string) {
  await contentStore.publishContent(id)
}

async function handleUnpublish(id: string) {
  await contentStore.unpublishContent(id)
}

async function handleDelete(id: string) {
  if (confirm('确定要删除这篇内容吗？')) {
    await contentStore.deleteContent(id)
  }
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const onUploadImg = async (files: File[], callback: (urls: string[]) => void) => {
  try {
    const urls = await Promise.all(
      files.map(async (file) => {
        const response = await imageApi.upload(file)
        return response.data.url
      })
    )
    callback(urls)
  } catch (e) {
    console.error('Image upload failed:', e)
    alert('图片上传失败')
  }
}
</script>

<template>
  <div class="content-view">
    <div class="page-header flex justify-between items-center mb-6">
      <h1>内容管理</h1>
      <button class="btn btn-primary" @click="openCreateModal" :disabled="categories.length === 0">
        <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="12" y1="5" x2="12" y2="19"/>
          <line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        新建内容
      </button>
    </div>

    <div v-if="categories.length === 0" class="warning-box">
      <p>请先创建一个分类，然后再添加内容。</p>
      <router-link to="/admin/categories" class="btn btn-secondary mt-4">前往分类管理</router-link>
    </div>

    <div v-else-if="loading" class="loading">
      <p class="text-muted">加载中...</p>
    </div>

    <div v-else-if="sortedContents.length === 0" class="empty-state">
      <p class="text-muted">暂无内容，创建第一篇文章吧！</p>
    </div>

    <div v-else class="content-list">
      <div v-for="content in sortedContents" :key="content.id" class="content-item card">
        <div class="item-header flex justify-between items-center">
          <div class="item-meta">
            <span :class="['badge', content.status === 'PUBLISHED' ? 'badge-published' : 'badge-draft']">
              {{ content.status === 'PUBLISHED' ? '已发布' : '草稿' }}
            </span>
            <span v-if="content.categoryName" class="category-name text-muted">
              {{ content.categoryName }}
            </span>
          </div>
          <div class="item-actions flex gap-2">
            <button 
              v-if="content.status === 'DRAFT'" 
              class="btn btn-secondary btn-sm" 
              @click="handlePublish(content.id)"
            >
              发布
            </button>
            <button 
              v-else 
              class="btn btn-secondary btn-sm" 
              @click="handleUnpublish(content.id)"
            >
              取消发布
            </button>
            <button class="action-btn" @click="openEditModal(content)" title="Edit">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
              </svg>
            </button>
            <button class="action-btn danger" @click="handleDelete(content.id)" title="Delete">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="3 6 5 6 21 6"/>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
              </svg>
            </button>
          </div>
        </div>
        <h3 class="item-title">{{ content.title }}</h3>
        <p class="item-date text-muted">更新于: {{ formatDate(content.updatedAt) }}</p>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal modal-lg">
        <h2>{{ editingId ? '编辑内容' : '新建内容' }}</h2>
        <form @submit.prevent="handleSubmit">
          <div class="form-row flex gap-4">
            <div class="form-group flex-1">
              <label for="title">标题</label>
              <input id="title" v-model="formData.title" type="text" required placeholder="内容标题" />
            </div>
            <div class="form-group" style="width: 200px;">
              <label for="categoryId">分类</label>
              <select id="categoryId" v-model="formData.categoryId" required>
                <option 
                  v-for="cat in flatCategories" 
                  :key="cat.id" 
                  :value="cat.id"
                >
                  {{ '\u3000'.repeat(cat.level) }}{{ cat.name }}
                </option>
              </select>
            </div>
          </div>
          <div class="form-group">
            <div class="label-row flex justify-between items-center">
              <label>内容 (Markdown)</label>
              <button type="button" class="btn btn-secondary btn-sm" @click="triggerFileUpload">
                <svg class="btn-icon-sm" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
                  <polyline points="17 8 12 3 7 8"/>
                  <line x1="12" y1="3" x2="12" y2="15"/>
                </svg>
                上传 MD 文件
              </button>
              <input 
                ref="fileInputRef"
                type="file" 
                accept=".md" 
                style="display: none;" 
                @change="handleFileUpload"
              />
            </div>
            <MdEditor 
              v-model="formData.markdownContent" 
              theme="dark" 
              language="zh-CN"
              :preview="true"
              style="height: 400px;"
              @onUploadImg="onUploadImg"
            />
          </div>
          <div class="modal-actions flex gap-4 justify-between">
            <button type="button" class="btn btn-secondary" @click="closeModal">取消</button>
            <button type="submit" class="btn btn-primary">{{ editingId ? '更新' : '创建' }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.btn-icon {
  width: 1.25rem;
  height: 1.25rem;
}

.btn-sm {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
}

.warning-box {
  background-color: rgba(202, 138, 4, 0.1);
  border: 1px solid var(--color-accent);
  border-radius: 0.75rem;
  padding: 2rem;
  text-align: center;
}

.content-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.content-item {
  cursor: default;
}

.content-item:hover {
  border-color: var(--color-border);
}

.item-header {
  margin-bottom: 0.75rem;
}

.item-meta {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.category-name {
  font-size: 0.875rem;
}

.item-actions {
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.content-item:hover .item-actions {
  opacity: 1;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border: none;
  background: transparent;
  color: var(--color-text-muted);
  border-radius: 0.375rem;
  cursor: pointer;
}

.action-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: var(--color-text);
}

.action-btn.danger:hover {
  color: var(--color-error);
}

.action-btn svg {
  width: 1rem;
  height: 1rem;
}

.item-title {
  margin: 0 0 0.5rem;
}

.item-date {
  font-size: 0.75rem;
}

.empty-state, .loading {
  text-align: center;
  padding: 4rem 0;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 2rem;
}

.modal {
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 0.75rem;
  padding: 2rem;
  width: 100%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-lg {
  max-width: 900px;
}

.modal h2 {
  margin-bottom: 1.5rem;
}

.modal-actions {
  margin-top: 2rem;
}

.form-row {
  margin-bottom: 1rem;
}

.flex-1 {
  flex: 1;
}

.label-row {
  margin-bottom: 0.5rem;
}

.btn-icon-sm {
  width: 1rem;
  height: 1rem;
}
</style>
