<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { useContentStore } from '@/stores/content'
import type { Content } from '@/types'

const route = useRoute()
const contentStore = useContentStore()
const content = ref<Content | null>(null)
const loading = ref(true)

onMounted(async () => {
  const id = route.params.id as string
  content.value = await contentStore.fetchContentById(id)
  loading.value = false
})

function formatDate(dateStr: string | null): string {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script>

<template>
  <div class="content-detail-view">
    <div class="container">
      <RouterLink to="/" class="back-link">
        <svg class="back-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="19" y1="12" x2="5" y2="12"/>
          <polyline points="12,19 5,12 12,5"/>
        </svg>
        返回首页
      </RouterLink>

      <div v-if="loading" class="loading">
        <p class="text-muted">加载中...</p>
      </div>

      <article v-else-if="content" class="article">
        <header class="article-header">
          <div class="article-meta">
            <span v-if="content.categoryName" class="category-badge">
              {{ content.categoryName }}
            </span>
            <span class="publish-date text-muted">
              {{ formatDate(content.publishedAt) }}
            </span>
          </div>
          <h1 class="article-title">{{ content.title }}</h1>
        </header>
        
        <div class="article-content content-html" v-html="content.htmlContent"></div>
      </article>

      <div v-else class="not-found">
        <h2>内容未找到</h2>
        <p class="text-muted">请求的内容不存在或已被删除。</p>
        <RouterLink to="/" class="btn btn-primary mt-4">返回首页</RouterLink>
      </div>
    </div>
  </div>
</template>

<style scoped>
.back-link {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 2rem;
  color: var(--color-text-muted);
  font-weight: 500;
}

.back-link:hover {
  color: var(--color-accent);
}

.back-icon {
  width: 1.25rem;
  height: 1.25rem;
}

.article {
  max-width: 800px;
  margin: 0 auto;
}

.article-header {
  margin-bottom: 3rem;
}

.article-meta {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.category-badge {
  background-color: rgba(202, 138, 4, 0.2);
  color: var(--color-accent);
  padding: 0.25rem 0.75rem;
  border-radius: 9999px;
  font-size: 0.75rem;
  font-weight: 500;
}

.publish-date {
  font-size: 0.875rem;
}

.article-title {
  font-size: 2.5rem;
  line-height: 1.2;
}

.article-content {
  font-size: 1.125rem;
  line-height: 1.8;
}

.loading, .not-found {
  text-align: center;
  padding: 4rem 0;
}
</style>
