<template>
  <div class="category-page">
    <header class="header">
      <router-link to="/">返回首页</router-link>
      <h1>{{ category?.name || '分类内容' }}</h1>
      <router-link to="/admin" class="admin-button">管理后台</router-link>
    </header>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else>
      <div v-if="subcategories.length > 0" class="subcategories-section">
        <h2>子分类</h2>
        <div class="subcategory-grid">
          <div 
            v-for="sub in subcategories" 
            :key="sub.id" 
            class="subcategory-card"
            @click="navigateToCategory(sub.id)">
            <h3>{{ sub.name }}</h3>
            <span class="content-count">{{ sub.contentCount }} 篇内容</span>
            <div v-if="sub.children && sub.children.length > 0" class="has-children">
              {{ sub.children.length }} 个子分类
            </div>
          </div>
        </div>
      </div>

      <div class="content-section">
        <h2>{{ subcategories.length > 0 ? '当前分类内容' : '内容列表' }}</h2>
        <div v-if="content.length === 0" class="empty">该分类下暂无内容</div>
        <div v-else class="content-list">
          <div v-for="item in content" :key="item.id" class="content-item" @click="viewContent(item.id)">
            <h3>{{ item.title }}</h3>
            <p>{{ item.summary }}</p>
            <div class="meta">{{ formatDate(item.publishedAt) }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import categoryService from '@/services/categoryService'
import contentService from '@/services/contentService'

const route = useRoute()
const router = useRouter()
const category = ref(null)
const content = ref([])
const subcategories = ref([])
const loading = ref(true)

async function loadData() {
  loading.value = true
  try {
    const [categoryData, contentData, treeData] = await Promise.all([
      categoryService.getById(route.params.id),
      contentService.getAll({ categoryId: route.params.id, status: 'PUBLISHED' }),
      categoryService.getTree()
    ])
    category.value = categoryData
    content.value = contentData
    
    subcategories.value = findSubcategories(treeData, route.params.id)
  } catch (error) {
    console.error('Failed to load data:', error)
  } finally {
    loading.value = false
  }
}

function findSubcategories(tree, categoryId) {
  for (const node of tree) {
    if (node.id === parseInt(categoryId)) {
      return node.children || []
    }
    if (node.children && node.children.length > 0) {
      const found = findSubcategories(node.children, categoryId)
      if (found) return found
    }
  }
  return []
}

onMounted(loadData)

watch(() => route.params.id, loadData)

function navigateToCategory(id) {
  router.push(`/category/${id}`)
}

function viewContent(id) {
  router.push(`/content/${id}`)
}

function formatDate(dateString) {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.category-page {
  min-height: 100vh;
  padding: 2rem;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.header h1 {
  margin: 0;
}

.admin-button {
  background: #409eff;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  text-decoration: none;
}

.subcategories-section {
  margin-bottom: 2rem;
}

.subcategories-section h2 {
  font-size: 1.2rem;
  margin-bottom: 1rem;
  color: #333;
}

.subcategory-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 1rem;
}

.subcategory-card {
  background: white;
  padding: 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.subcategory-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.subcategory-card h3 {
  margin: 0 0 0.5rem 0;
  font-size: 1rem;
  color: #333;
}

.content-count {
  font-size: 0.875rem;
  color: #666;
}

.has-children {
  margin-top: 0.5rem;
  font-size: 0.75rem;
  color: #999;
}

.content-section h2 {
  font-size: 1.2rem;
  margin-bottom: 1rem;
  color: #333;
}

.content-item {
  background: white;
  padding: 1rem;
  margin-bottom: 1rem;
  border-radius: 4px;
  cursor: pointer;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.content-item:hover {
  background: #f9f9f9;
}

.loading, .empty {
  text-align: center;
  padding: 2rem;
  color: #999;
}
</style>
