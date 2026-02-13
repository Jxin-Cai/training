<template>
  <div class="admin-layout">
    <header class="header">
      <h1>管理后台</h1>
      <div class="actions">
        <router-link to="/" class="view-site-btn">查看网站</router-link>
      </div>
    </header>

    <div class="content">
      <nav class="sidebar">
        <router-link to="/admin/categories">分类管理</router-link>
        <router-link to="/admin/content">内容管理</router-link>
      </nav>

      <main class="main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
}

.header {
  background: #2c3e50;
  color: white;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.view-site-btn {
  background: #67c23a;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  text-decoration: none;
}

.content {
  display: flex;
  height: calc(100vh - 60px);
}

.sidebar {
  width: 200px;
  background: #34495e;
  padding: 1rem;
}

.sidebar a {
  display: block;
  color: white;
  padding: 0.75rem;
  text-decoration: none;
  border-radius: 4px;
  margin-bottom: 0.5rem;
}

.sidebar a.router-link-active {
  background: #409eff;
}

.main {
  flex: 1;
  padding: 2rem;
  background: #f5f5f5;
  overflow-y: auto;
}
</style>
