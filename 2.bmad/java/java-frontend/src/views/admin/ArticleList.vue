<template>
  <div class="article-list">
    <div class="page-header">
      <h1>文章管理</h1>
      <a-button type="primary" @click="$router.push('/admin/articles/new')">
        新建文章
      </a-button>
    </div>
    
    <a-card>
      <a-table :columns="columns" :data-source="articles" :loading="loading" row-key="id">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 'PUBLISHED' ? 'green' : 'orange'">
              {{ record.status === 'PUBLISHED' ? '已发布' : '草稿' }}
            </a-tag>
          </template>
          <template v-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="$router.push(`/admin/articles/${record.id}/edit`)">编辑</a-button>
              <a-button 
                v-if="record.status === 'DRAFT'" 
                type="link" 
                size="small" 
                @click="handlePublish(record.id)"
              >发布</a-button>
              <a-button 
                v-else 
                type="link" 
                size="small" 
                @click="handleUnpublish(record.id)"
              >取消发布</a-button>
              <a-popconfirm title="确定删除？" @confirm="handleDelete(record.id)">
                <a-button type="link" size="small" danger>删除</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import api from '@/api'

const columns = [
  { title: '标题', dataIndex: 'title', key: 'title' },
  { title: '分类', dataIndex: 'categoryName', key: 'category' },
  { title: '状态', key: 'status' },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt' },
  { title: '操作', key: 'action', width: 220 }
]

const loading = ref(false)
const articles = ref([])

onMounted(async () => {
  await loadArticles()
})

const loadArticles = async () => {
  loading.value = true
  try {
    // 后台管理需要获取所有文章（包括草稿）
    const data = await api.getArticles({ all: true })
    articles.value = Array.isArray(data) ? data : (data.list || [])
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id) => {
  await api.deleteArticle(id)
  message.success('删除成功')
  await loadArticles()
}

const handlePublish = async (id) => {
  try {
    await api.publishArticle(id)
    message.success('发布成功')
    await loadArticles()
  } catch (error) {
    message.error('发布失败')
  }
}

const handleUnpublish = async (id) => {
  try {
    await api.unpublishArticle(id)
    message.success('已取消发布')
    await loadArticles()
  } catch (error) {
    message.error('操作失败')
  }
}
</script>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0;
}
</style>
