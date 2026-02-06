<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="public-layout">
    <header class="header">
      <div class="container flex items-center justify-between">
        <RouterLink to="/" class="logo">
          <span class="logo-text">内容</span>
          <span class="logo-accent">管理系统</span>
        </RouterLink>
        <nav class="nav">
          <RouterLink to="/" class="nav-link">首页</RouterLink>
          <RouterLink to="/admin" class="nav-link admin-link">管理后台</RouterLink>
        </nav>
      </div>
    </header>
    <main class="main">
      <RouterView />
    </main>
    <footer class="footer">
      <div class="container">
        <p class="text-muted">内容管理系统</p>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.public-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.header {
  padding: 1rem 0;
  border-bottom: 1px solid var(--color-border);
  background-color: rgba(10, 10, 10, 0.95);
  backdrop-filter: blur(10px);
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 1.5rem;
  font-weight: 700;
  text-decoration: none;
}

.logo-text {
  color: var(--color-text);
}

.logo-accent {
  color: var(--color-accent);
}

.nav {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.nav-link {
  color: var(--color-text-muted);
  font-weight: 500;
  transition: color var(--transition-fast);
}

.nav-link:hover {
  color: var(--color-text);
}

.admin-link {
  padding: 0.5rem 1rem;
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 0.5rem;
}

.admin-link:hover {
  border-color: var(--color-accent);
  color: var(--color-accent);
}

.main {
  flex: 1;
  padding: 3rem 0;
}

.footer {
  padding: 2rem 0;
  border-top: 1px solid var(--color-border);
  text-align: center;
}
</style>
