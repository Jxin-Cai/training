<script setup lang="ts">
import { onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { useContentStore } from '@/stores/content'
import { storeToRefs } from 'pinia'

const contentStore = useContentStore()
const { publishedContents, loading } = storeToRefs(contentStore)

onMounted(() => {
  contentStore.fetchPublishedContents()
})

function formatDate(dateStr: string | null): string {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

function getExcerpt(html: string): string {
  const text = html.replace(/<[^>]+>/g, '')
  return text.length > 200 ? text.substring(0, 200) + '...' : text
}
</script>

<template>
  <div class="home-view">
    <div class="container">
      <section class="hero">
        <h1 class="hero-title">欢迎访问<span class="text-accent">内容管理系统</span></h1>
        <p class="hero-subtitle text-muted">探索我们的最新内容</p>
      </section>

      <section class="content-list" v-if="!loading">
        <div v-if="publishedContents.length === 0" class="empty-state">
          <p class="text-muted">暂无已发布的内容</p>
        </div>
        <div v-else class="content-grid">
          <RouterLink 
            v-for="content in publishedContents" 
            :key="content.id"
            :to="`/content/${content.id}`"
            class="content-card card"
          >
            <div class="card-header">
              <span v-if="content.categoryName" class="category-badge">
                {{ content.categoryName }}
              </span>
              <span class="publish-date text-muted">
                {{ formatDate(content.publishedAt) }}
              </span>
            </div>
            <h3 class="card-title">{{ content.title }}</h3>
            <p class="card-excerpt text-muted">{{ getExcerpt(content.htmlContent) }}</p>
            <span class="read-more">阅读更多</span>
          </RouterLink>
        </div>
      </section>

      <div v-else class="loading">
        <p class="text-muted">加载中...</p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.hero {
  text-align: center;
  margin-bottom: 4rem;
}

.hero-title {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.hero-subtitle {
  font-size: 1.25rem;
}

.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
}

.content-card {
  display: flex;
  flex-direction: column;
  text-decoration: none;
  color: inherit;
  transition: transform var(--transition-fast), border-color var(--transition-fast);
}

.content-card:hover {
  transform: translateY(-4px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.card-title {
  margin-bottom: 0.75rem;
  color: var(--color-text);
}

.card-excerpt {
  flex: 1;
  font-size: 0.9rem;
  line-height: 1.6;
  margin-bottom: 1rem;
}

.read-more {
  color: var(--color-accent);
  font-weight: 500;
  font-size: 0.875rem;
}

.empty-state, .loading {
  text-align: center;
  padding: 4rem 0;
}
</style>
