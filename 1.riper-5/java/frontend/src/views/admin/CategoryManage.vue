<template>
  <div class="category-manage-container">
    <el-container>
      <el-header class="header">
        <h1>分类管理</h1>
        <div>
          <el-button @click="goToFrontend">返回前台</el-button>
          <el-button type="primary" @click="goToContentManage">内容管理</el-button>
        </div>
      </el-header>
      <el-main>
        <div class="toolbar">
          <el-button type="primary" @click="handleAdd">新增分类</el-button>
        </div>
        
        <el-table :data="categories" border stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="分类名称" />
          <el-table-column prop="description" label="描述" />
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="{ row }">
              {{ formatTime(row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="{ row }">
              <el-button size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 新增/编辑对话框 -->
        <el-dialog 
          v-model="dialogVisible" 
          :title="dialogTitle"
          width="500px"
        >
          <el-form :model="form" label-width="100px">
            <el-form-item label="分类名称" required>
              <el-input v-model="form.name" placeholder="请输入分类名称" />
            </el-form-item>
            <el-form-item label="描述">
              <el-input 
                v-model="form.description" 
                type="textarea"
                :rows="4"
                placeholder="请输入分类描述"
              />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit">确定</el-button>
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
  getCategories, 
  createCategory, 
  updateCategory, 
  deleteCategory 
} from '@/api/category'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  name: 'CategoryManage',
  setup() {
    const router = useRouter()
    const categories = ref([])
    const dialogVisible = ref(false)
    const dialogTitle = ref('')
    const form = ref({
      id: null,
      name: '',
      description: ''
    })

    // 加载分类列表
    const loadCategories = async () => {
      try {
        categories.value = await getCategories()
      } catch (error) {
        console.error('加载分类失败:', error)
      }
    }

    // 新增分类
    const handleAdd = () => {
      dialogTitle.value = '新增分类'
      form.value = {
        id: null,
        name: '',
        description: ''
      }
      dialogVisible.value = true
    }

    // 编辑分类
    const handleEdit = (row) => {
      dialogTitle.value = '编辑分类'
      form.value = {
        id: row.id,
        name: row.name,
        description: row.description
      }
      dialogVisible.value = true
    }

    // 删除分类
    const handleDelete = async (row) => {
      try {
        await ElMessageBox.confirm(
          '确定要删除该分类吗？',
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )
        await deleteCategory(row.id)
        ElMessage.success('删除成功')
        await loadCategories()
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error(error.response?.status === 409 ? '该分类下存在内容，无法删除' : '删除失败')
        }
      }
    }

    // 提交表单
    const handleSubmit = async () => {
      if (!form.value.name || !form.value.name.trim()) {
        ElMessage.error('请输入分类名称')
        return
      }

      try {
        if (form.value.id) {
          await updateCategory(form.value.id, form.value)
          ElMessage.success('更新成功')
        } else {
          await createCategory(form.value)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        await loadCategories()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    // 跳转到内容管理
    const goToContentManage = () => {
      router.push({ name: 'ContentManage' })
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
      loadCategories()
    })

    return {
      categories,
      dialogVisible,
      dialogTitle,
      form,
      handleAdd,
      handleEdit,
      handleDelete,
      handleSubmit,
      goToContentManage,
      goToFrontend,
      formatTime
    }
  }
}
</script>

<style scoped>
.category-manage-container {
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
