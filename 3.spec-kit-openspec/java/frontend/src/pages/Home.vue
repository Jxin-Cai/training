<template>
  <div class="home-page">
    <header class="header">
      <h1>CMS 内容管理系统</h1>
      <router-link to="/admin" class="admin-button">进入管理后台</router-link>
    </header>

    <div class="content">
      <aside class="sidebar">
        <h3>分类导航</h3>
        <el-tree
          :data="categoryTree"
          :props="treeProps"
          node-key="id"
          :default-expanded-keys="[]"
          @node-click="handleCategorySelect">
          <template #default="{ data }">
            <span>{{ data.name }} ({{ data.contentCount }})</span>
          </template>
        </el-tree>
      </aside>

      <main class="main">
        <h2>最新内容</h2>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="content.length === 0" class="empty">暂无内容</div>
        <div v-else class="content-list">
          <div v-for="item in content" :key="item.id" class="content-item" @click="viewContent(item.id)">
            <h3>{{ item.title }}</h3>
            <p>{{ item.summary }}</p>
            <div class="meta">
              <span>{{ item.categoryName }}</span>
              <span>{{ formatDate(item.publishedAt) }}</span>
            </div>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import categoryService from '@/services/categoryService'
import contentService from '@/services/contentService'

const router = useRouter()
const categoryTree = ref([])
const content = ref([])
const loading = ref(true)

const treeProps = {
  children: 'children',
  label: 'name'
}

onMounted(async () => {
  try {
    const [tree, contentData] = await Promise.all([
      categoryService.getTree(),
      contentService.getAll({ status: 'PUBLISHED' })
    ])
    categoryTree.value = tree
    content.value = contentData
  } catch (error) {
    console.error('Failed to load data:', error)
  } finally {
    loading.value = false
  }
})

function handleCategorySelect(data) {
  router.push(`/category/${data.id}`)
}

function viewContent(id) {
  router.push(`/content/${id}`)
}

function formatDate(dateString) {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.home-page {
  min-height: 100vh;
}

.header {
  background: white;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.admin-button {
  background: #409eff;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  text-decoration: none;
}

.admin-button:hover {
  background: #66b1ff;
}

.content {
  display: flex;
  padding: 2rem;
  gap: 2rem;
}

.sidebar {
  width: 250px;
  background: white;
  padding: 1rem;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.main {
  flex: 1;
  background: white;
  padding: 1rem;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.content-item {
  padding: 1rem;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}

.content-item:hover {
  background: #f5f5f5;
}

.content-item h3 {
  margin: 0 0 0.5rem 0;
}

.meta {
  display: flex;
  gap: 1rem;
  color: #999;
  font-size: 0.875rem;
  margin-top: 0.5rem;
}

.loading, .empty {
  text-align: center;
  padding: 2rem;
  color: #999;
}
</style>
