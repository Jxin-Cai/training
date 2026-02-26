<template>
  <div class="page">
    <h1>{{ isEdit ? '编辑内容' : '新建内容' }}</h1>
    <p v-if="error" class="error">{{ error }}</p>
    <p v-if="loading && isEdit">加载中…</p>
    <form v-else @submit.prevent="submit" class="form">
      <label>标题 <input v-model="form.title" required /></label>
      <label>分类
        <select v-model="form.categoryId" required>
          <option value="">请选择</option>
          <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
      </label>
      <label>
        Markdown 正文
        <span class="hint">或上传 .md 文件：</span>
        <input type="file" accept=".md" @change="onFileSelect" />
      </label>
      <textarea v-model="form.markdownBody" rows="16" placeholder="Markdown 正文"></textarea>
      <div class="actions">
        <button type="submit" :disabled="saving">保存</button>
        <router-link to="/admin/contents">返回列表</router-link>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '../../api/client'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const form = ref({ title: '', categoryId: '', markdownBody: '' })
const categories = ref([])
const loading = ref(false)
const saving = ref(false)
const error = ref('')

async function loadCategories() {
  try {
    const res = await api.categories.list()
    categories.value = res.data || []
    if (categories.value.length && !form.value.categoryId) form.value.categoryId = categories.value[0].id
  } catch (e) {
    error.value = e.message || '加载分类失败'
  }
}

async function loadContent() {
  if (!route.params.id) return
  loading.value = true
  try {
    const res = await api.contents.get(route.params.id)
    const d = res.data
    form.value = {
      title: d.title,
      categoryId: d.categoryId,
      markdownBody: d.markdownBody || '',
    }
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}

function onFileSelect(e) {
  const file = e.target.files?.[0]
  if (!file) return
  const r = new FileReader()
  r.onload = () => {
    form.value.markdownBody = r.result || ''
    if (!form.value.title) form.value.title = file.name.replace(/\.md$/i, '')
  }
  r.readAsText(file)
  e.target.value = ''
}

async function submit() {
  if (!form.value.title?.trim() || !form.value.categoryId) return
  saving.value = true
  error.value = ''
  try {
    const payload = {
      title: form.value.title,
      categoryId: Number(form.value.categoryId),
      markdownBody: form.value.markdownBody || '',
    }
    if (isEdit.value) {
      await api.contents.update(route.params.id, payload)
    } else {
      await api.contents.create(payload)
    }
    router.push('/admin/contents')
  } catch (e) {
    error.value = e.message || '保存失败'
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadCategories().then(loadContent)
})
</script>

<style scoped>
.page { max-width: 800px; }
.form label { display: block; margin-bottom: 0.5rem; }
.form input[type="text"], .form select { width: 100%; padding: 0.35rem; }
.form textarea { width: 100%; font-family: inherit; padding: 0.5rem; }
.form .hint { margin-left: 0.5rem; font-size: 0.9em; color: #666; }
.form input[type="file"] { margin-left: 0.5rem; }
.actions { margin-top: 1rem; display: flex; gap: 0.5rem; align-items: center; }
.error { color: #c00; }
</style>
