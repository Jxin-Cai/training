<template>
  <div class="user-manage">
    <div class="page-header">
      <h1>用户管理</h1>
      <a-button type="primary" @click="showModal()">新建用户</a-button>
    </div>
    
    <a-card>
      <a-table :columns="columns" :data-source="users" :loading="loading" row-key="id">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'role'">
            <a-tag :color="getRoleColor(record.role)">
              {{ getRoleName(record.role) }}
            </a-tag>
          </template>
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
      :title="isEdit ? '编辑用户' : '新建用户'"
      @ok="handleSubmit"
      :confirm-loading="submitting"
    >
      <a-form :model="form" :label-col="{ span: 4 }">
        <a-form-item label="用户名" required>
          <a-input v-model:value="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </a-form-item>
        <a-form-item label="密码" :required="!isEdit">
          <a-input-password v-model:value="form.password" :placeholder="isEdit ? '留空则不修改' : '请输入密码'" />
        </a-form-item>
        <a-form-item label="角色" required>
          <a-select v-model:value="form.role" placeholder="请选择角色">
            <a-select-option value="ADMIN">管理员</a-select-option>
            <a-select-option value="AUTHOR">作者</a-select-option>
            <a-select-option value="READER">读者</a-select-option>
          </a-select>
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
  { title: '用户名', dataIndex: 'username', key: 'username' },
  { title: '角色', key: 'role' },
  { title: '创建时间', dataIndex: 'createdAt', key: 'createdAt' },
  { title: '操作', key: 'action', width: 150 }
]

const loading = ref(false)
const users = ref([])
const modalVisible = ref(false)
const submitting = ref(false)
const isEdit = ref(false)
const editId = ref(null)

const form = ref({
  username: '',
  password: '',
  role: 'READER'
})

onMounted(async () => {
  await loadUsers()
})

const loadUsers = async () => {
  loading.value = true
  try {
    const data = await api.getUsers({ page: 1, size: 100 })
    users.value = Array.isArray(data) ? data : (data.list || [])
  } finally {
    loading.value = false
  }
}

const getRoleName = (role) => {
  const roleMap = {
    'ADMIN': '管理员',
    'AUTHOR': '作者',
    'READER': '读者'
  }
  return roleMap[role] || role
}

const getRoleColor = (role) => {
  const colorMap = {
    'ADMIN': 'red',
    'AUTHOR': 'blue',
    'READER': 'green'
  }
  return colorMap[role] || 'default'
}

const showModal = (record = null) => {
  isEdit.value = !!record
  editId.value = record?.id || null
  form.value = record ? {
    username: record.username,
    password: '',
    role: record.role
  } : {
    username: '',
    password: '',
    role: 'READER'
  }
  modalVisible.value = true
}

const handleSubmit = async () => {
  submitting.value = true
  try {
    if (isEdit.value) {
      await api.updateUser(editId.value, form.value)
      message.success('更新成功')
    } else {
      await api.createUser(form.value)
      message.success('创建成功')
    }
    modalVisible.value = false
    await loadUsers()
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id) => {
  await api.deleteUser(id)
  message.success('删除成功')
  await loadUsers()
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
