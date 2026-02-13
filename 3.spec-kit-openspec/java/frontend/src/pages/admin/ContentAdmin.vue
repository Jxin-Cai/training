<template>
  <div class="content-admin">
    <h2>内容管理</h2>
    <div class="toolbar">
      <el-button type="primary" @click="createContent">新建内容</el-button>
    </div>

    <el-table :data="content" style="width: 100%">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="categoryName" label="分类" width="120" />
      <el-table-column prop="status" label="状态" width="100" />
      <el-table-column label="操作" width="250">
        <template #default="{ row }">
          <el-button size="small" @click="editContent(row)">编辑</el-button>
          <el-button size="small" v-if="row.status === 'DRAFT'" type="success" @click="publishContent(row)">发布</el-button>
          <el-button size="small" v-else @click="unpublishContent(row)">取消发布</el-button>
          <el-button size="small" type="danger" @click="deleteContent(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import contentService from '@/services/contentService'

const router = useRouter()
const content = ref([])

onMounted(async () => {
  await loadContent()
})

async function loadContent() {
  try {
    content.value = await contentService.getAll()
  } catch (error) {
    ElMessage.error('加载内容失败')
  }
}

function createContent() {
  router.push('/admin/content/new')
}

function editContent(row) {
  router.push(`/admin/content/${row.id}`)
}

async function publishContent(row) {
  try {
    await contentService.publish(row.id)
    ElMessage.success('内容已发布')
    await loadContent()
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

async function unpublishContent(row) {
  try {
    await contentService.unpublish(row.id)
    ElMessage.success('已取消发布')
    await loadContent()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

async function deleteContent(row) {
  try {
    await ElMessageBox.confirm('确定要删除这个内容吗？', '确认删除')
    await contentService.delete(row.id)
    ElMessage.success('内容已删除')
    await loadContent()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}
</script>

<style scoped>
.content-admin {
  background: white;
  padding: 2rem;
  border-radius: 4px;
}

.toolbar {
  margin-bottom: 1rem;
}
</style>
