<template>
  <div class="category-admin">
    <h2>分类管理</h2>

    <div class="toolbar">
      <el-button type="primary" @click="showCreateDialog">新建分类</el-button>
    </div>

    <el-tree
      :data="categoryTree"
      :props="treeProps"
      node-key="id"
      default-expand-all>
      <template #default="{ data }">
        <div class="tree-node">
          <span>{{ data.name }}</span>
          <div class="actions">
            <el-button size="small" @click="editCategory(data)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteCategory(data)">删除</el-button>
          </div>
        </div>
      </template>
    </el-tree>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新建分类'">
      <el-form :model="form" label-width="80px">
        <el-form-item label="名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="父分类">
          <el-cascader
            v-model="form.parentIds"
            :options="categoryTree"
            :props="{ checkStrictly: true, value: 'id', label: 'name', children: 'children' }"
            clearable
            placeholder="选择父分类（可选）"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.displayOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCategory">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import categoryService from '@/services/categoryService'

const categoryTree = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  id: null,
  name: '',
  description: '',
  parentIds: [],
  displayOrder: 0
})

const treeProps = {
  children: 'children',
  label: 'name'
}

onMounted(async () => {
  await loadCategories()
})

async function loadCategories() {
  try {
    categoryTree.value = await categoryService.getTree()
  } catch (error) {
    ElMessage.error('加载分类失败')
  }
}

function showCreateDialog() {
  isEdit.value = false
  form.value = { id: null, name: '', description: '', parentIds: [], displayOrder: 0 }
  dialogVisible.value = true
}

function editCategory(data) {
  isEdit.value = true
  form.value = {
    id: data.id,
    name: data.name,
    description: data.description,
    parentIds: data.parentId ? [data.parentId] : [],
    displayOrder: data.displayOrder
  }
  dialogVisible.value = true
}

async function saveCategory() {
  try {
    const parentId = form.value.parentIds.length > 0
      ? form.value.parentIds[form.value.parentIds.length - 1]
      : null

    const data = {
      name: form.value.name,
      description: form.value.description,
      parentId: parentId,
      displayOrder: form.value.displayOrder
    }

    if (isEdit.value) {
      await categoryService.update(form.value.id, data)
      ElMessage.success('分类已更新')
    } else {
      await categoryService.create(data)
      ElMessage.success('分类已创建')
    }

    dialogVisible.value = false
    await loadCategories()
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  }
}

async function deleteCategory(data) {
  try {
    await ElMessageBox.confirm('确定要删除这个分类吗？', '确认删除', {
      type: 'warning'
    })
    await categoryService.delete(data.id, 'REASSIGN_TO_PARENT', 'REASSIGN_TO_PARENT')
    ElMessage.success('分类已删除')
    await loadCategories()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.response?.data?.message || '删除失败')
    }
  }
}
</script>

<style scoped>
.category-admin {
  background: white;
  padding: 2rem;
  border-radius: 4px;
}

.toolbar {
  margin-bottom: 1rem;
}

.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding-right: 1rem;
}

.actions {
  display: flex;
  gap: 0.5rem;
}
</style>
