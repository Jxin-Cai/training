<template>
  <div class="page">
    <h1>分类管理</h1>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-if="loading">加载中…</p>
    <template v-else>
      <div class="toolbar">
        <button @click="showForm = true; editing = null; form = { name: '', description: '' }">新增分类</button>
      </div>
      <table class="table" v-if="list.length">
        <thead>
          <tr><th>ID</th><th>名称</th><th>描述</th><th>操作</th></tr>
        </thead>
        <tbody>
          <tr v-for="c in list" :key="c.id">
            <td>{{ c.id }}</td>
            <td>{{ c.name }}</td>
            <td>{{ c.description }}</td>
            <td>
              <button @click="openEdit(c)">编辑</button>
              <button @click="doDelete(c)" :disabled="deleting === c.id">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
      <p v-else>暂无分类</p>
    </template>
    <div v-if="showForm" class="modal">
      <div class="modal-inner">
        <h3>{{ editing ? '编辑分类' : '新增分类' }}</h3>
        <div class="form">
          <label>名称 <input v-model="form.name" /></label>
          <label>描述 <input v-model="form.description" /></label>
        </div>
        <div class="actions">
          <button @click="submitForm">保存</button>
          <button @click="showForm = false">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { api } from '../../api/client'

const list = ref([])
const loading = ref(true)
const error = ref('')
const showForm = ref(false)
const editing = ref(null)
const form = ref({ name: '', description: '' })
const deleting = ref(null)

async function load() {
  loading.value = true
  error.value = ''
  try {
    const res = await api.categories.list()
    list.value = res.data || []
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

function openEdit(c) {
  editing.value = c
  form.value = { name: c.name, description: c.description || '' }
  showForm.value = true
}

async function submitForm() {
  if (!form.value.name?.trim()) return
  try {
    if (editing.value) {
      await api.categories.update(editing.value.id, form.value.name, form.value.description)
    } else {
      await api.categories.create(form.value.name, form.value.description)
    }
    showForm.value = false
    load()
  } catch (e) {
    error.value = e.message || '保存失败'
  }
}

async function doDelete(c) {
  if (!confirm('确定删除该分类？')) return
  deleting.value = c.id
  try {
    await api.categories.delete(c.id)
    load()
  } catch (e) {
    error.value = e.message || '删除失败'
  } finally {
    deleting.value = null
  }
}

onMounted(load)
</script>

<style scoped>
.page { max-width: 700px; }
.toolbar { margin-bottom: 1rem; }
.table { width: 100%; border-collapse: collapse; }
.table th, .table td { border: 1px solid #ccc; padding: 0.5rem; text-align: left; }
.error { color: #c00; }
.modal { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; }
.modal-inner { background: #fff; padding: 1.5rem; border-radius: 8px; min-width: 320px; }
.form label { display: block; margin-bottom: 0.5rem; }
.form input { width: 100%; padding: 0.25rem; }
.actions { margin-top: 1rem; display: flex; gap: 0.5rem; }
</style>
