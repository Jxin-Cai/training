<template>
  <div class="category-manage">
    <div class="page-header">
      <h1>分类管理</h1>
      <a-button type="primary" @click="showModal()">新建分类</a-button>
    </div>
    
    <a-card>
      <a-table :columns="columns" :data-source="categories" :loading="loading" row-key="id">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <a-button type="link" @click="showModal(record)">编辑</a-button>
            <a-popconfirm title="确定删除？" @confirm="handleDelete(record.id)">
              <a-button type="link" danger>删除</a-button>
            </a-popconfirm>
          </template>
        </template>
      </a-table>
    </a-card>
    
    <a-modal
      v-model:open="modalVisible"
      :title="isEdit ? '编辑分类' : '新建分类'"
      @ok="handleSubmit"
      :confirm-loading="submitting"
    >
      <a-form :model="form" :label-col="{ span: 4 }">
        <a-form-item label="名称" required>
          <a-input v-model:value="form.name" placeholder="请输入分类名称" />
        </a-form-item>
        <a-form-item label="描述">
          <a-textarea v-model:value="form.description" placeholder="请输入分类描述" :rows="3" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import api from '@/api'

const columns = [
  { title: '名称', dataIndex: 'name', key: 'name' },
  { title: '描述', dataIndex: 'description', key: 'description' },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt' },
  { title: '操作', key: 'action', width: 150 }
]

const loading = ref(false)
const categories = ref([])
const modalVisible = ref(false)
const submitting = ref(false)
const isEdit = ref(false)
const editId = ref(null)

const form = ref({
  name: '',
  description: ''
})

onMounted(async () => {
  await loadCategories()
})

const loadCategories = async () => {
  loading.value = true
  try {
    const data = await api.getCategories()
    categories.value = data || []
  } finally {
    loading.value = false
  }
}

const showModal = (record = null) => {
  isEdit.value = !!record
  editId.value = record?.id || null
  form.value = record ? {
    name: record.name,
    description: record.description
  } : {
    name: '',
    description: ''
  }
  modalVisible.value = true
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    if (isEdit.value) {
      await api.updateCategory(editId.value, form.value)
      message.success('更新成功')
    } else {
      await api.createCategory(form.value)
      message.success('创建成功')
    }
    modalVisible.value = false
    await loadCategories()
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id) => {
  await api.deleteCategory(id)
  message.success('删除成功')
  await loadCategories()
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
