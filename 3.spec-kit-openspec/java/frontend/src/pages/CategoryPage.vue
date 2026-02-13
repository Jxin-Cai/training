<template>
  <div class="category-page">
    <header class="header">
      <router-link to="/">返回首页</router-link>
      <h1>{{ category?.name || '分类内容' }}</h1>
      <router-link to="/admin" class="admin-button">管理后台</router-link>
    </header>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else class="content-list">
      <div v-if="content.length === 0" class="empty">该分类下暂无内容</div>
      <div v-for="item in content" :key="item.id" class="content-item" @click="viewContent(item.id)">
        <h3>{{ item.title }}</h3>
        <p>{{ item.summary }}</p>
        <div class="meta">{{ formatDate(item.publishedAt) }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import categoryService from '@/services/categoryService'
import contentService from '@/services/contentService'

const route = useRoute()
const router = useRouter()
const category = ref(null)
const content = ref([])
const loading = ref(true)

onMounted(async () => {
  try {
    const [categoryData, contentData] = await Promise.all([
      categoryService.getById(route.params.id),
      contentService.getAll({ categoryId: route.params.id, status: 'PUBLISHED' })
    ])
    category.value = categoryData
    content.value = contentData
  } catch (error) {
    console.error('Failed to load data:', error)
  } finally {
    loading.value = false
  }
})

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

.admin-button {
  background: #409eff;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  text-decoration: none;
}

.content-item {
  background: white;
  padding: 1rem;
  margin-bottom: 1rem;
  border-radius: 4px;
  cursor: pointer;
}

.loading, .empty {
  text-align: center;
  padding: 2rem;
}
</style>
