<template>
  <div class="content-detail-container">
    <el-container>
      <el-header class="header">
        <h1>CMS内容管理系统</h1>
        <el-button @click="goBack">返回列表</el-button>
      </el-header>
      <el-main>
        <div v-if="loading" class="loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          加载中...
        </div>
        <div v-else-if="content" class="content-detail">
          <h1 class="title">{{ content.title }}</h1>
          <div class="meta">
            <el-tag>{{ content.categoryName }}</el-tag>
            <span class="time">发布于 {{ formatTime(content.publishTime) }}</span>
          </div>
          <el-divider />
          <div class="content-body" v-html="content.renderedHtml"></div>
        </div>
        <div v-else class="empty">
          内容不存在或未发布
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getPublishedContent } from '@/api/content'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'

export default {
  name: 'ContentDetail',
  components: { Loading },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const content = ref(null)
    const loading = ref(false)

    // 加载内容详情
    const loadContent = async () => {
      loading.value = true
      try {
        const id = route.params.id
        content.value = await getPublishedContent(id)
      } catch (error) {
        console.error('加载内容失败:', error)
        ElMessage.error('内容不存在或未发布')
      } finally {
        loading.value = false
      }
    }

    // 返回列表
    const goBack = () => {
      router.push({ name: 'ContentList' })
    }

    // 格式化时间
    const formatTime = (time) => {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleString('zh-CN')
    }

    onMounted(() => {
      loadContent()
    })

    return {
      content,
      loading,
      goBack,
      formatTime
    }
  }
}
</script>

<style scoped>
.content-detail-container {
  min-height: 100vh;
  background: #f5f7fa;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 0 40px;
}

.header h1 {
  margin: 0;
  font-size: 24px;
  color: #303133;
}

.loading,
.empty {
  text-align: center;
  padding: 100px 20px;
  color: #909399;
  font-size: 16px;
}

.content-detail {
  max-width: 900px;
  margin: 20px auto;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.title {
  margin: 0 0 20px 0;
  font-size: 32px;
  color: #303133;
}

.meta {
  display: flex;
  align-items: center;
  gap: 15px;
  color: #909399;
  font-size: 14px;
}

.content-body {
  line-height: 1.8;
  color: #606266;
}

.content-body :deep(h1),
.content-body :deep(h2),
.content-body :deep(h3) {
  margin-top: 30px;
  margin-bottom: 15px;
}

.content-body :deep(p) {
  margin-bottom: 15px;
}

.content-body :deep(code) {
  padding: 2px 6px;
  background: #f5f7fa;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
}

.content-body :deep(pre) {
  padding: 15px;
  background: #f5f7fa;
  border-radius: 6px;
  overflow-x: auto;
}

.content-body :deep(blockquote) {
  margin: 20px 0;
  padding: 10px 20px;
  border-left: 4px solid #409eff;
  background: #f5f7fa;
}
</style>
