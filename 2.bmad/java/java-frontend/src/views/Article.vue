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
      <article v-else-if="article" class="article-detail">
        <h1>{{ article.title }}</h1>
        <div class="article-meta">
          <span class="category">{{ article.categoryName }}</span>
          <span class="author">{{ article.authorName }}</span>
          <span class="date">{{ formatDate(article.createdAt) }}</span>
        </div>
        <div class="article-content" v-html="renderedContent"></div>
      </article>
      <a-result v-else status="404" title="文章不存在" />
    </main>
    
    <footer class="site-footer">
      <p>&copy; 2026 Java CMS - 内部知识分享平台</p>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import api from '@/api'

const md = new MarkdownIt({
  html: true,
  linkify: true,
  highlight: (str, lang) => {
    if (lang && hljs.getLanguage(lang)) {
      return `<pre class="hljs"><code>${hljs.highlight(str, { language: lang }).value}</code></pre>`
    }
    return `<pre class="hljs"><code>${md.utils.escapeHtml(str)}</code></pre>`
  }
})

const route = useRoute()
const loading = ref(true)
const article = ref(null)

const renderedContent = computed(() => {
  return article.value ? md.render(article.value.content || '') : ''
})

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return dateStr.split('T')[0]
}

onMounted(async () => {
  const id = route.params.id
  try {
    const data = await api.getArticle(id)
    article.value = data
  } catch (error) {
    console.error('加载文章失败:', error)
    article.value = null
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

.article-detail {
  background: #fff;
  padding: 32px;
  border-radius: 8px;
}

.article-detail h1 {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #262626;
}

.article-meta {
  color: #8c8c8c;
  font-size: 14px;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.article-meta span {
  margin-right: 24px;
}

.article-meta .category {
  color: #1890ff;
}

.article-content {
  font-size: 16px;
  line-height: 1.8;
  color: #262626;
}

.article-content :deep(h1),
.article-content :deep(h2),
.article-content :deep(h3) {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
}

.article-content :deep(p) {
  margin-bottom: 16px;
}

.article-content :deep(pre) {
  background: #f6f8fa;
  padding: 16px;
  border-radius: 6px;
  overflow-x: auto;
  margin: 16px 0;
}

.article-content :deep(code) {
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 14px;
}

.article-content :deep(blockquote) {
  border-left: 4px solid #dfe2e5;
  padding-left: 16px;
  margin: 16px 0;
  color: #6a737d;
}

.article-content :deep(ul),
.article-content :deep(ol) {
  padding-left: 24px;
  margin-bottom: 16px;
}

.article-content :deep(li) {
  margin-bottom: 8px;
}

.article-content :deep(a) {
  color: #1890ff;
}

.article-content :deep(img) {
  max-width: 100%;
  border-radius: 4px;
}
</style>
