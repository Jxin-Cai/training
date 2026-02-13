<template>
  <div class="content-page">
    <header class="header">
      <router-link to="/">返回首页</router-link>
      <router-link to="/admin" class="admin-button">管理后台</router-link>
    </header>

    <div v-if="loading" class="loading">加载中...</div>
    <article v-else class="article">
      <h1>{{ content?.title }}</h1>
      <div class="meta">
        <span>分类: {{ content?.category?.name }}</span>
        <span>发布时间: {{ formatDate(content?.publishedAt) }}</span>
      </div>
      <div class="body" v-html="content?.body"></div>
    </article>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import contentService from '@/services/contentService'

const route = useRoute()
const content = ref(null)
const loading = ref(true)

onMounted(async () => {
  try {
    content.value = await contentService.getById(route.params.id)
  } catch (error) {
    console.error('Failed to load content:', error)
  } finally {
    loading.value = false
  }
})

function formatDate(dateString) {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.content-page {
  min-height: 100vh;
  padding: 2rem;
  max-width: 800px;
  margin: 0 auto;
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

.article {
  background: white;
  padding: 2rem;
  border-radius: 4px;
}

.meta {
  color: #999;
  margin-bottom: 2rem;
  display: flex;
  gap: 1rem;
}

.body {
  line-height: 1.8;
}

.loading {
  text-align: center;
  padding: 2rem;
}
</style>
