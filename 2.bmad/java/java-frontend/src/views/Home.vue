<template>
  <div class="frontend-layout">
    <header class="site-header">
      <h1>Java CMS</h1>
      <nav>
        <router-link to="/">首页</router-link>
        <router-link to="/admin/login">管理后台</router-link>
      </nav>
    </header>
    
    <main class="site-main">
      <a-spin v-if="loading" />
      <div v-else class="article-list">
        <ArticleCard
          v-for="article in articles"
          :key="article.id"
          :article="article"
        />
      </div>
    </main>
    
    <footer class="site-footer">
      <p>&copy; 2026 Java CMS - 内部知识分享平台</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ArticleCard from '@/components/ArticleCard.vue'
import api from '@/api'

const loading = ref(false)
const articles = ref([])

onMounted(async () => {
  loading.value = true
  try {
    const data = await api.getArticles({ page: 1, size: 10 })
    articles.value = Array.isArray(data) ? data : (data.list || [])
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.site-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e8e8e8;
}

.site-header h1 {
  font-size: 24px;
  font-weight: 600;
}

.site-header nav a {
  margin-left: 24px;
  color: #595959;
}

.site-header nav a:hover {
  color: #1890ff;
}

.site-footer {
  margin-top: 48px;
  padding-top: 16px;
  border-top: 1px solid #e8e8e8;
  text-align: center;
  color: #8c8c8c;
  font-size: 14px;
}
</style>
