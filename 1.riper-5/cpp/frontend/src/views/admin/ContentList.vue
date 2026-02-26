<template>
  <div class="page">
    <h1>内容管理</h1>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-if="loading">加载中…</p>
    <template v-else>
      <div class="toolbar">
        <router-link to="/admin/contents/new">新建内容</router-link>
      </div>
      <table class="table" v-if="list.length">
        <thead>
          <tr><th>ID</th><th>标题</th><th>分类ID</th><th>状态</th><th>发布时间</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-for="c in list" :key="c.id">
            <td>{{ c.id }}</td>
            <td>{{ c.title }}</td>
            <td>{{ c.categoryId }}</td>
            <td>{{ c.status }}</td>
            <td>{{ c.publishedAt || '-' }}</td>
            <td>
              <router-link :to="`/admin/contents/edit/${c.id}`">编辑</router-link>
              <button v-if="c.status === 'draft'" @click="publish(c.id)">发布</button>
              <button v-else @click="unpublish(c.id)">取消发布</button>
              <button @click="doDelete(c.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else>暂无内容</p>
    </template>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from '../../api/client'

const list = ref([])
const loading = ref(true)
const error = ref('')

async function load() {
  loading.value = true
  error.value = ''
  try {
    const res = await api.contents.list()
    list.value = res.data || []
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

async function publish(id) {
  try {
    await api.contents.publish(id)
    load()
  } catch (e) {
    error.value = e.message || '发布失败'
  }
}

async function unpublish(id) {
  try {
    await api.contents.unpublish(id)
    load()
  } catch (e) {
    error.value = e.message || '取消发布失败'
  }
}

async function doDelete(id) {
  if (!confirm('确定删除该内容？')) return
  try {
    await api.contents.delete(id)
    load()
  } catch (e) {
    error.value = e.message || '删除失败'
  }
}

onMounted(load)
</script>

<style scoped>
.page { max-width: 960px; }
.toolbar { margin-bottom: 1rem; }
.toolbar a { margin-right: 0.5rem; }
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { border: 1px solid #ccc; padding: 0.5rem; text-align: left; }
.error { color: #c00; }
</style>
