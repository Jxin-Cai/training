<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useCategoryStore } from '@/stores/category'
import { storeToRefs } from 'pinia'
import type { CreateCategoryRequest, UpdateCategoryRequest, CategoryTreeNode } from '@/types'

const categoryStore = useCategoryStore()
const { categoryTree, flatCategories, loading } = storeToRefs(categoryStore)

const showModal = ref(false)
const editingId = ref<string | null>(null)
const formData = ref<CreateCategoryRequest>({ name: '', description: '', parentId: null })

onMounted(() => {
  categoryStore.fetchCategories()
})

interface FlatCategoryItem {
  id: string
  name: string
  description: string
  level: number
  parentId: string | null
  hasChildren: boolean
}

const flattenedTree = computed((): FlatCategoryItem[] => {
  const result: FlatCategoryItem[] = []
  
  function traverse(nodes: CategoryTreeNode[]) {
    for (const node of nodes) {
      result.push({
        id: node.id,
        name: node.name,
        description: node.description,
        level: node.level,
        parentId: node.parentId,
        hasChildren: node.children.length > 0
      })
      if (node.children.length > 0) {
        traverse(node.children)
      }
    }
  }
  
  traverse(categoryTree.value)
  return result
})

function openCreateModal(parentId: string | null = null) {
  editingId.value = null
  formData.value = { name: '', description: '', parentId }
  showModal.value = true
}

function openEditModal(category: FlatCategoryItem) {
  editingId.value = category.id
  formData.value = { 
    name: category.name, 
    description: category.description,
    parentId: category.parentId
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingId.value = null
  formData.value = { name: '', description: '', parentId: null }
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
    try {
      await categoryStore.deleteCategory(id)
    } catch (e) {
      // Error handled in store
    }
  }
}

const availableParents = computed(() => {
  if (!editingId.value) {
    return flatCategories.value
  }
  
  const editing = flatCategories.value.find(c => c.id === editingId.value)
  if (!editing) return flatCategories.value
  
  return flatCategories.value.filter(c => 
    c.id !== editingId.value && !c.path.startsWith(editing.path + '/')
  )
})
</script>

<template>
  <div class="categories-view">
    <div class="page-header flex justify-between items-center mb-6">
      <h1>分类管理</h1>
      <button class="btn btn-primary" @click="openCreateModal(null)">
        <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="12" y1="5" x2="12" y2="19"/>
          <line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        新建顶级分类
      </button>
    </div>

    <div v-if="loading" class="loading">
      <p class="text-muted">加载中...</p>
    </div>

    <div v-else-if="flattenedTree.length === 0" class="empty-state">
      <p class="text-muted">暂无分类，创建第一个分类吧！</p>
    </div>

    <div v-else class="category-tree">
      <div 
        v-for="category in flattenedTree" 
        :key="category.id" 
        class="category-row"
        :style="{ paddingLeft: `${category.level * 24 + 16}px` }"
      >
        <div class="category-content flex justify-between items-center">
          <div class="category-info">
            <span v-if="category.level > 0" class="tree-indent">└─</span>
            <span class="category-name">{{ category.name }}</span>
            <span v-if="category.description" class="category-desc text-muted">
              - {{ category.description }}
            </span>
          </div>
          <div class="category-actions flex gap-2">
            <button 
              class="btn btn-secondary btn-sm" 
              @click="openCreateModal(category.id)"
              title="添加子分类"
            >
              + 子分类
            </button>
            <button class="action-btn" @click="openEditModal(category)" title="编辑">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
              </svg>
            </button>
            <button 
              class="action-btn danger" 
              @click="handleDelete(category.id)" 
              title="删除"
              :disabled="category.hasChildren"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="3 6 5 6 21 6"/>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <h2>{{ editingId ? '编辑分类' : '新建分类' }}</h2>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="parentId">父分类</label>
            <select id="parentId" v-model="formData.parentId">
              <option :value="null">-- 顶级分类 --</option>
              <option 
                v-for="cat in availableParents" 
                :key="cat.id" 
                :value="cat.id"
              >
                {{ '\u3000'.repeat(cat.level) }}{{ cat.name }}
              </option>
            </select>
          </div>
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

.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
}

.category-tree {
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 0.75rem;
  overflow: hidden;
}

.category-row {
  border-bottom: 1px solid var(--color-border);
  padding: 0.75rem 1rem;
  transition: background-color var(--transition-fast);
}

.category-row:last-child {
  border-bottom: none;
}

.category-row:hover {
  background-color: rgba(255, 255, 255, 0.05);
}

.category-content {
  min-height: 2rem;
}

.category-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.tree-indent {
  color: var(--color-text-muted);
  font-family: monospace;
}

.category-name {
  font-weight: 500;
}

.category-desc {
  font-size: 0.875rem;
}

.category-actions {
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.category-row:hover .category-actions {
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

.action-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.action-btn svg {
  width: 1rem;
  height: 1rem;
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
