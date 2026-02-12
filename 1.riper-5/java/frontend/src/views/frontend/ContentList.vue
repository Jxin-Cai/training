<template>
  <div class="content-list-container">
    <el-container>
      <el-header class="header">
        <h1>CMS内容管理系统</h1>
        <el-button type="primary" @click="goToAdmin">进入后台管理</el-button>
      </el-header>
      <el-main>
        <div v-if="loading" class="loading">
          <el-icon class="is-loading"><Loading /></el-icon>
          加载中...
        </div>
        <div v-else-if="contentList.length === 0" class="empty">
          暂无已发布内容
        </div>
        <div v-else class="content-grid">
          <el-card 
            v-for="content in contentList" 
            :key="content.id"
            class="content-card"
            shadow="hover"
            @click="goToDetail(content.id)"
          >
            <h2>{{ content.title }}</h2>
            <div class="meta">
              <el-tag size="small">{{ content.categoryName }}</el-tag>
              <span class="time">{{ formatTime(content.publishTime) }}</span>
            </div>
          </el-card>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getPublishedContents } from '@/api/content'
import { ElMessage } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'

export default {
  name: 'ContentList',
  components: { Loading },
  setup() {
    const router = useRouter()
    const contentList = ref([])
    const loading = ref(false)

    // 加载内容列表
    const loadContents = async () => {
      loading.value = true
      try {
        contentList.value = await getPublishedContents()
      } catch (error) {
        console.error('加载内容失败:', error)
        ElMessage.error('加载内容失败')
      } finally {
        loading.value = false
      }
    }

    // 跳转到详情页
    const goToDetail = (id) => {
      router.push({ name: 'ContentDetail', params: { id } })
    }

    // 跳转到后台管理
    const goToAdmin = () => {
      router.push({ name: 'CategoryManage' })
    }

    // 格式化时间
    const formatTime = (time) => {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleDateString('zh-CN')
    }

    onMounted(() => {
      loadContents()
    })

    return {
      contentList,
      loading,
      goToDetail,
      goToAdmin,
      formatTime
    }
  }
}
</script>

<style scoped>
.content-list-container {
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

.content-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding: 20px;
}

.content-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.content-card:hover {
  transform: translateY(-5px);
}

.content-card h2 {
  margin: 0 0 15px 0;
  font-size: 18px;
  color: #303133;
}

.meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #909399;
  font-size: 14px;
}

.time {
  margin-left: 10px;
}
</style>
