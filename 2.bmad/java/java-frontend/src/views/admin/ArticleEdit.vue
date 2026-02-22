<template>
  <div class="article-edit">
    <div class="page-header">
      <h1>{{ isEdit ? '编辑文章' : '新建文章' }}</h1>
    </div>
    
    <a-card>
      <a-form :model="form" :label-col="{ span: 2 }" :wrapper-col="{ span: 22 }">
        <a-form-item label="标题" required>
          <a-input v-model:value="form.title" placeholder="请输入文章标题" />
        </a-form-item>
        
        <a-form-item label="分类" required>
          <a-select v-model:value="form.categoryId" placeholder="请选择分类">
            <a-select-option v-for="cat in categories" :key="cat.id" :value="cat.id">
              {{ cat.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="内容" required>
          <MarkdownEditor v-model:content="form.content" />
        </a-form-item>
        
        <a-form-item :wrapper-col="{ offset: 2 }">
          <a-space>
            <a-button @click="handleSaveDraft" :loading="saving">保存草稿</a-button>
            <a-button type="primary" @click="handlePublish" :loading="publishing">发布</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import api from '@/api'
import MarkdownEditor from '@/components/MarkdownEditor.vue'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)

const form = ref({
  title: '',
  categoryId: null,
  content: '',
  status: 'DRAFT'
})

const categories = ref([])
const saving = ref(false)
const publishing = ref(false)

onMounted(async () => {
  await loadCategories()
  if (isEdit.value) {
    await loadArticle()
  }
})

const loadCategories = async () => {
  const data = await api.getCategories()
  categories.value = data || []
}

const loadArticle = async () => {
  const data = await api.getArticle(route.params.id)
  form.value = {
    title: data.title,
    categoryId: data.categoryId,
    content: data.content,
    status: data.status
  }
}

const handleSaveDraft = async () => {
  saving.value = true
  try {
    form.value.status = 'DRAFT'
    if (isEdit.value) {
      await api.updateArticle(route.params.id, form.value)
    } else {
      await api.createArticle(form.value)
    }
    message.success('保存成功')
    router.push('/admin/articles')
  } finally {
    saving.value = false
  }
}

const handlePublish = async () => {
  publishing.value = true
  try {
    let articleId = route.params.id
    
    // 先保存文章内容
    if (isEdit.value) {
      form.value.status = 'DRAFT'  // 先保存为草稿
      await api.updateArticle(articleId, form.value)
    } else {
      const result = await api.createArticle(form.value)
      articleId = result.id
    }
    
    // 再调用发布接口
    await api.publishArticle(articleId)
    
    message.success('发布成功')
    router.push('/admin/articles')
  } catch (error) {
    message.error('发布失败：' + (error.message || '未知错误'))
  } finally {
    publishing.value = false
  }
}
</script>

<style scoped>
.page-header {
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0;
}
</style>
