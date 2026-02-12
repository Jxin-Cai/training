<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useContentStore } from '@/stores/content'
import { useCategoryStore } from '@/stores/category'
import { storeToRefs } from 'pinia'
import { contentApi } from '@/api'
import type { Content } from '@/types'

const route = useRoute()
const contentStore = useContentStore()
const categoryStore = useCategoryStore()
const { loading } = storeToRefs(contentStore)
const { flatCategories } = storeToRefs(categoryStore)

const filteredContents = ref<Content[]>([])

const currentCategoryId = computed(() => route.query.category as string | undefined)

const breadcrumb = computed(() => {
  if (!currentCategoryId.value) return []
  return categoryStore.getCategoryPath(currentCategoryId.value)
})

const currentCategoryName = computed(() => {
  if (!currentCategoryId.value) return '全部内容'
  const cat = flatCategories.value.find(c => c.id === currentCategoryId.value)
  return cat?.name || '全部内容'
})

async function loadContents() {
  if (currentCategoryId.value) {
    const response = await contentApi.getByCategory(currentCategoryId.value)
    filteredContents.value = response.data.filter(c => c.status === 'PUBLISHED')
  } else {
    const response = await contentApi.getPublished()
    filteredContents.value = response.data
  }
}

onMounted(async () => {
  await categoryStore.fetchCategories()
  await loadContents()
})

watch(currentCategoryId, () => {
  loadContents()
})

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script>

<template>
  <div class="home-view">
    <div v-if="breadcrumb.length > 0" class="breadcrumb">
      <router-link to="/">首页</router-link>
      <span v-for="(name, index) in breadcrumb" :key="index">
        <span class="separator">/</span>
        <span :class="{ current: index === breadcrumb.length - 1 }">{{ name }}</span>
      </span>
    </div>

    <h1 class="page-title">{{ currentCategoryName }}</h1>

    <div v-if="loading" class="loading">
      <p class="text-muted">加载中...</p>
    </div>

    <div v-else-if="filteredContents.length === 0" class="empty-state">
      <p class="text-muted">暂无内容</p>
    </div>

    <div v-else class="content-list">
      <router-link 
        v-for="content in filteredContents" 
        :key="content.id"
        :to="`/content/${content.id}`"
        class="content-card"
      >
        <h2 class="content-title">{{ content.title }}</h2>
        <div class="content-meta">
          <span v-if="content.categoryName" class="category-tag">
            {{ content.categoryName }}
          </span>
          <span class="publish-date">{{ formatDate(content.publishedAt || content.createdAt) }}</span>
        </div>
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.home-view {
  max-width: 800px;
  margin: 0 auto;
}

.breadcrumb {
  margin-bottom: 1rem;
  font-size: 0.875rem;
  color: var(--color-text-muted);
}

.breadcrumb a {
  color: var(--color-text-muted);
  text-decoration: none;
}

.breadcrumb a:hover {
  color: var(--color-text);
}

.breadcrumb .separator {
  margin: 0 0.5rem;
}

.breadcrumb .current {
  color: var(--color-text);
}

.page-title {
  margin-bottom: 2rem;
}

.content-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.content-card {
  display: block;
  padding: 1.5rem;
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 0.75rem;
  text-decoration: none;
  transition: all var(--transition-fast);
}

.content-card:hover {
  border-color: var(--color-text-muted);
  transform: translateY(-2px);
}

.content-title {
  margin: 0 0 0.75rem;
  color: var(--color-text);
  font-size: 1.25rem;
}

.content-meta {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.875rem;
  color: var(--color-text-muted);
}

.category-tag {
  background-color: rgba(255, 255, 255, 0.1);
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
}

.loading, .empty-state {
  text-align: center;
  padding: 4rem 0;
}
</style>
