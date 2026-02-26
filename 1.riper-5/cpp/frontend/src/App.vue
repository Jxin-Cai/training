<template>
  <div class="app">
    <header v-if="$route.meta.showHeader !== false" class="header">
      <router-link to="/" class="logo">CMS</router-link>
      <nav>
        <router-link to="/">首页</router-link>
        <router-link to="/posts">内容列表</router-link>
        <router-link to="/admin/categories">后台 · 分类</router-link>
        <router-link to="/admin/contents">后台 · 内容</router-link>
      </nav>
    </header>
    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
</script>

<style>
* { box-sizing: border-box; }
body { margin: 0; font-family: system-ui, sans-serif; }
.app { min-height: 100vh; display: flex; flex-direction: column; }
.header {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  padding: 0.75rem 1.5rem;
  background: #1a1a2e;
  color: #eee;
}
.header a { color: #eee; text-decoration: none; }
.header a.router-link-active { font-weight: bold; }
.logo { font-weight: bold; margin-right: 1rem; }
nav { display: flex; gap: 1rem; }
.main { flex: 1; padding: 1.5rem; max-width: 900px; margin: 0 auto; width: 100%; }
</style>
