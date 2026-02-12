<template>
  <div class="content-manage-container">
    <el-container>
      <el-header class="header">
        <h1>内容管理</h1>
        <div>
          <el-button @click="goToFrontend">返回前台</el-button>
          <el-button type="primary" @click="goToCategoryManage">分类管理</el-button>
        </div>
      </el-header>
      <el-main>
        <div class="toolbar">
          <el-button type="primary" @click="handleAdd">新增内容</el-button>
          <el-select 
            v-model="statusFilter" 
            placeholder="筛选状态" 
            clearable
            @change="handleFilterChange"
            style="width: 150px; margin-left: 10px;"
          >
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已发布" value="PUBLISHED" />
          </el-select>
        </div>
        
        <el-table :data="contents" border stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" />
          <el-table-column prop="categoryName" label="分类" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 'PUBLISHED' ? 'success' : 'info'">
                {{ row.status === 'PUBLISHED' ? '已发布' : '草稿' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="publishTime" label="发布时间" width="180">
            <template #default="{ row }">
              {{ row.publishTime ? formatTime(row.publishTime) : '-' }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="250">
            <template #default="{ row }">
              <el-button size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button 
                v-if="row.status === 'DRAFT'" 
                size="small" 
                type="success" 
                @click="handlePublish(row)"
              >
                发布
              </el-button>
              <el-button 
                v-else 
                size="small" 
                type="warning" 
                @click="handleUnpublish(row)"
              >
                取消发布
              </el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 新增/编辑对话框 -->
        <el-dialog 
          v-model="dialogVisible" 
          :title="dialogTitle"
          width="80%"
          top="5vh"
        >
          <el-form :model="form" label-width="100px">
            <el-form-item label="标题" required>
              <el-input v-model="form.title" placeholder="请输入标题" />
            </el-form-item>
            <el-form-item label="分类" required>
              <el-select v-model="form.categoryId" placeholder="请选择分类">
                <el-option 
                  v-for="cat in categories" 
                  :key="cat.id" 
                  :label="cat.name" 
                  :value="cat.id"
                />
              </el-select>
            </el-form-item>
            <el-form-item label="内容" required>
              <markdown-editor v-model="form.markdownContent" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit">保存</el-button>
          </template>
        </el-dialog>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { 
  getContents, 
  createContent, 
  updateContent, 
  deleteContent,
  publishContent,
  unpublishContent
} from '@/api/content'
import { getCategories } from '@/api/category'
import { ElMessage, ElMessageBox } from 'element-plus'
import MarkdownEditor from '@/components/MarkdownEditor.vue'

export default {
  name: 'ContentManage',
  components: { MarkdownEditor },
  setup() {
    const router = useRouter()
    const contents = ref([])
    const categories = ref([])
    const statusFilter = ref('')
    const dialogVisible = ref(false)
    const dialogTitle = ref('')
    const form = ref({
      id: null,
      title: '',
      categoryId: null,
      markdownContent: ''
    })

    // 加载内容列表
    const loadContents = async () => {
      try {
        contents.value = await getContents(statusFilter.value)
      } catch (error) {
        console.error('加载内容失败:', error)
      }
    }

    // 加载分类列表
    const loadCategories = async () => {
      try {
        categories.value = await getCategories()
      } catch (error) {
        console.error('加载分类失败:', error)
      }
    }

    // 状态筛选变化
    const handleFilterChange = () => {
      loadContents()
    }

    // 新增内容
    const handleAdd = () => {
      dialogTitle.value = '新增内容'
      form.value = {
        id: null,
        title: '',
        categoryId: null,
        markdownContent: ''
      }
      dialogVisible.value = true
    }

    // 编辑内容
    const handleEdit = (row) => {
      dialogTitle.value = '编辑内容'
      form.value = {
        id: row.id,
        title: row.title,
        categoryId: row.categoryId,
        markdownContent: row.markdownContent
      }
      dialogVisible.value = true
    }

    // 发布内容
    const handlePublish = async (row) => {
      try {
        await publishContent(row.id)
        ElMessage.success('发布成功')
        await loadContents()
      } catch (error) {
        ElMessage.error('发布失败')
      }
    }

    // 取消发布
    const handleUnpublish = async (row) => {
      try {
        await unpublishContent(row.id)
        ElMessage.success('取消发布成功')
        await loadContents()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    // 删除内容
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(
          '确定要删除该内容吗？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        await deleteContent(row.id)
        ElMessage.success('删除成功')
        await loadContents()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('删除失败')
        }
      }
    }

    // 提交表单
    const handleSubmit = async () => {
      if (!form.value.title || !form.value.title.trim()) {
        ElMessage.error('请输入标题')
        return
      }
      if (!form.value.categoryId) {
        ElMessage.error('请选择分类')
        return
      }
      if (!form.value.markdownContent || !form.value.markdownContent.trim()) {
        ElMessage.error('请输入内容')
        return
      }

      try {
        if (form.value.id) {
          await updateContent(form.value.id, form.value)
          ElMessage.success('更新成功')
        } else {
          await createContent(form.value)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        await loadContents()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    // 跳转到分类管理
    const goToCategoryManage = () => {
      router.push({ name: 'CategoryManage' })
    }

    // 返回前台
    const goToFrontend = () => {
      router.push({ name: 'ContentList' })
    }

    // 格式化时间
    const formatTime = (time) => {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleString('zh-CN')
    }

    onMounted(() => {
      loadContents()
      loadCategories()
    })

    return {
      contents,
      categories,
      statusFilter,
      dialogVisible,
      dialogTitle,
      form,
      handleFilterChange,
      handleAdd,
      handleEdit,
      handlePublish,
      handleUnpublish,
      handleDelete,
      handleSubmit,
      goToCategoryManage,
      goToFrontend,
      formatTime
    }
  }
}
</script>

<style scoped>
.content-manage-container {
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

.toolbar {
  margin-bottom: 20px;
}
</style>
