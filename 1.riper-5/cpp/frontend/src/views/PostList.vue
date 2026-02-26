<template>
  <div class="page">
    <h1>内容列表</h1>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-if="loading">加载中…</p>
    <ul v-else-if="list.length" class="list">
      <li v-for="c in list" :key="c.id" class="item">
        <router-link :to="`/posts/${c.id}`">{{ c.title }}</router-link>
        <span class="meta">分类ID {{ c.categoryId }} · {{ c.publishedAt || '-' }}</span>
      </li>
    </ul>
    <p v-else>暂无已发布内容</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from '../api/client'

const list = ref([])
const loading = ref(true)
const error = ref('')

async function load() {
  loading.value = true
  error.value = ''
  try {
    const res = await api.public.list()
    list.value = res.data || []
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<style scoped>
.list { list-style: none; padding: 0; margin: 0; }
.item { padding: 0.75rem 0; border-bottom: 1px solid #eee; }
.item a { font-weight: 500; text-decoration: none; color: #1a1a2e; }
.item a:hover { text-decoration: underline; }
.meta { color: #666; font-size: 0.9em; margin-left: 0.5rem; }
.error { color: #c00; }
</style>
