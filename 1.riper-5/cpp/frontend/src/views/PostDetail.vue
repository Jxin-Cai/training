<template>
  <div class="page">
    <p v-if="error" class="error">{{ error }}</p>
    <p v-if="loading">加载中…</p>
    <template v-else-if="content">
      <h1>{{ content.title }}</h1>
      <p class="meta">分类ID {{ content.categoryId }} · {{ content.publishedAt || '-' }}</p>
      <div class="body" v-html="content.htmlBody"></div>
    </template>
    <p v-else>未找到该内容</p>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { api } from '../api/client'

const route = useRoute()
const content = ref(null)
const loading = ref(true)
const error = ref('')

async function load() {
  if (!route.params.id) return
  loading.value = true
  error.value = ''
  content.value = null
  try {
    const res = await api.public.get(route.params.id)
    content.value = res.data
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

onMounted(load)
watch(() => route.params.id, load)
</script>

<style scoped>
.meta { color: #666; font-size: 0.9em; margin-bottom: 1rem; }
.body { line-height: 1.6; }
.body :deep(p) { margin: 0.5rem 0; }
.body :deep(br) { display: block; content: ''; margin: 0.25rem 0; }
.error { color: #c00; }
</style>
