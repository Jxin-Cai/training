<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useCategoryStore } from '@/stores/category'
import { storeToRefs } from 'pinia'
import type { CreateCategoryRequest, UpdateCategoryRequest } from '@/types'

const categoryStore = useCategoryStore()
const { categories, loading } = storeToRefs(categoryStore)

const showModal = ref(false)
const editingId = ref<string | null>(null)
const formData = ref<CreateCategoryRequest>({ name: '', description: '' })

onMounted(() => {
  categoryStore.fetchCategories()
})

function openCreateModal() {
  editingId.value = null
  formData.value = { name: '', description: '' }
  showModal.value = true
}

function openEditModal(category: { id: string; name: string; description: string }) {
  editingId.value = category.id
  formData.value = { name: category.name, description: category.description }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingId.value = null
  formData.value = { name: '', description: '' }
}

async function handleSubmit() {
  try {
    if (editingId.value) {
      await categoryStore.updateCategory(editingId.value, formData.value as UpdateCategoryRequest)
    } else {
      await categoryStore.createCategory(formData.value)
    }
    closeModal()
  } catch (e) {
    console.error(e)
  }
}

async function handleDelete(id: string) {
  if (confirm('确定要删除这个分类吗？')) {
    await categoryStore.deleteCategory(id)
  }
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}
</script>

<template>
  <div class="categories-view">
    <div class="page-header flex justify-between items-center mb-6">
      <h1>分类管理</h1>
      <button class="btn btn-primary" @click="openCreateModal">
        <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="12" y1="5" x2="12" y2="19"/>
          <line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        新建分类
      </button>
    </div>

    <div v-if="loading" class="loading">
      <p class="text-muted">加载中...</p>
    </div>

    <div v-else-if="categories.length === 0" class="empty-state">
      <p class="text-muted">暂无分类，创建第一个分类吧！</p>
    </div>

    <div v-else class="categories-grid">
      <div v-for="category in categories" :key="category.id" class="category-card card">
        <div class="card-header flex justify-between items-center">
          <h3>{{ category.name }}</h3>
          <div class="card-actions flex gap-2">
            <button class="action-btn" @click="openEditModal(category)" title="Edit">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
              </svg>
            </button>
            <button class="action-btn danger" @click="handleDelete(category.id)" title="Delete">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="3 6 5 6 21 6"/>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
              </svg>
            </button>
          </div>
        </div>
        <p class="card-description text-muted">{{ category.description || '暂无描述' }}</p>
        <p class="card-date text-muted">创建于: {{ formatDate(category.createdAt) }}</p>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <h2>{{ editingId ? '编辑分类' : '新建分类' }}</h2>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="name">名称</label>
            <input id="name" v-model="formData.name" type="text" required placeholder="分类名称" />
          </div>
          <div class="form-group">
            <label for="description">描述</label>
            <textarea id="description" v-model="formData.description" rows="3" placeholder="分类描述"></textarea>
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

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.category-card {
  cursor: default;
}

.category-card:hover {
  border-color: var(--color-border);
}

.card-header h3 {
  margin: 0;
}

.card-actions {
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.category-card:hover .card-actions {
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

.card-description {
  margin: 1rem 0 0.5rem;
  font-size: 0.9rem;
}

.card-date {
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
}

.modal {
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 0.75rem;
  padding: 2rem;
  width: 100%;
  max-width: 500px;
}

.modal h2 {
  margin-bottom: 1.5rem;
}

.modal-actions {
  margin-top: 2rem;
}
</style>
