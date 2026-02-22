<template>
  <a-layout class="admin-layout">
    <a-layout-sider v-model:collapsed="collapsed" collapsible class="admin-sidebar">
      <div class="logo">
        <h2 v-if="!collapsed">Java CMS</h2>
        <span v-else>CMS</span>
      </div>
      <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="inline"
      >
        <a-menu-item key="dashboard">
          <router-link to="/admin">
            <HomeOutlined />
            <span>仪表盘</span>
          </router-link>
        </a-menu-item>
        <a-menu-item key="articles">
          <router-link to="/admin/articles">
            <FileTextOutlined />
            <span>文章管理</span>
          </router-link>
        </a-menu-item>
        <a-menu-item key="categories">
          <router-link to="/admin/categories">
            <FolderOutlined />
            <span>分类管理</span>
          </router-link>
        </a-menu-item>
        <a-menu-item key="users">
          <router-link to="/admin/users">
            <UserOutlined />
            <span>用户管理</span>
          </router-link>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    
    <a-layout>
      <a-layout-header class="admin-header">
        <div class="header-left">
          <a-button type="link" @click="router.push('/')">
            <HomeOutlined />
            返回前台
          </a-button>
        </div>
        <div class="header-right">
          <a-dropdown>
            <a-button type="text">
              <UserOutlined />
              {{ userStore.user?.username || '管理员' }}
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item @click="handleLogout">
                  <LogoutOutlined />
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>
      
      <a-layout-content class="admin-content">
        <router-view />
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { 
  HomeOutlined, 
  FileTextOutlined, 
  FolderOutlined, 
  UserOutlined,
  LogoutOutlined 
} from '@ant-design/icons-vue'
import { useUserStore } from '@/store'

const router = useRouter()
const userStore = useUserStore()

const collapsed = ref(false)
const selectedKeys = ref(['dashboard'])

const handleLogout = () => {
  userStore.logout()
  router.push('/admin/login')
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.logo h2 {
  color: #fff;
  margin: 0;
}

.admin-header {
  background: #fff;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
}

.header-right {
  display: flex;
  align-items: center;
}

.admin-content {
  margin: 24px;
  padding: 24px;
  background: #fff;
  border-radius: 8px;
}
</style>
