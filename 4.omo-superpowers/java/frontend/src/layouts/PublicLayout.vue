<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCategoryStore } from '@/stores/category'
import { storeToRefs } from 'pinia'
import type { CategoryTreeNode } from '@/types'

const route = useRoute()
const router = useRouter()
const categoryStore = useCategoryStore()
const { categoryTree } = storeToRefs(categoryStore)

const hoveredCategory = ref<string | null>(null)

onMounted(() => {
  categoryStore.fetchCategories()
})

const currentCategoryId = computed(() => {
  return route.query.category as string | undefined
})

function navigateToCategory(categoryId: string | null) {
  if (categoryId) {
    router.push({ path: '/', query: { category: categoryId } })
  } else {
    router.push({ path: '/' })
  }
}

function hasDescendants(node: CategoryTreeNode): boolean {
  return node.children && node.children.length > 0
}
</script>

<template>
  <div class="public-layout">
    <header class="public-header">
      <div class="header-content">
        <router-link to="/" class="logo">CMS</router-link>
        
        <nav class="category-nav">
          <ul class="nav-list">
            <li class="nav-item">
              <a 
                href="#" 
                @click.prevent="navigateToCategory(null)"
                :class="{ active: !currentCategoryId }"
              >
                全部
              </a>
            </li>
            <li 
              v-for="category in categoryTree" 
              :key="category.id"
              class="nav-item"
              @mouseenter="hoveredCategory = category.id"
              @mouseleave="hoveredCategory = null"
            >
              <a 
                href="#"
                @click.prevent="navigateToCategory(category.id)"
                :class="{ active: currentCategoryId === category.id }"
              >
                {{ category.name }}
                <svg v-if="hasDescendants(category)" class="dropdown-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="6 9 12 15 18 9"/>
                </svg>
              </a>
              
              <ul v-if="hasDescendants(category) && hoveredCategory === category.id" class="dropdown-menu">
                <li v-for="child in category.children" :key="child.id" class="dropdown-item">
                  <a 
                    href="#"
                    @click.prevent="navigateToCategory(child.id)"
                    :class="{ active: currentCategoryId === child.id }"
                  >
                    {{ child.name }}
                  </a>
                  
                  <ul v-if="hasDescendants(child)" class="sub-dropdown">
                    <li v-for="grandchild in child.children" :key="grandchild.id">
                      <a 
                        href="#"
                        @click.prevent="navigateToCategory(grandchild.id)"
                        :class="{ active: currentCategoryId === grandchild.id }"
                      >
                        {{ grandchild.name }}
                      </a>
                    </li>
                  </ul>
                </li>
              </ul>
            </li>
          </ul>
        </nav>
        
        <router-link to="/admin" class="admin-link">管理后台</router-link>
      </div>
    </header>
    
    <main class="public-main">
      <router-view />
    </main>
    
    <footer class="public-footer">
      <p>&copy; 2026 CMS. All rights reserved.</p>
    </footer>
  </div>
</template>

<style scoped>
.public-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.public-header {
  background-color: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
  padding: 0 2rem;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

.logo {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--color-text);
  text-decoration: none;
}

.category-nav {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-list {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: 0.5rem;
}

.nav-item {
  position: relative;
}

.nav-item > a {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.5rem 1rem;
  color: var(--color-text-muted);
  text-decoration: none;
  border-radius: 0.375rem;
  transition: all var(--transition-fast);
}

.nav-item > a:hover,
.nav-item > a.active {
  color: var(--color-text);
  background-color: rgba(255, 255, 255, 0.1);
}

.dropdown-icon {
  width: 1rem;
  height: 1rem;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  min-width: 160px;
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 0.5rem;
  padding: 0.5rem 0;
  list-style: none;
  margin: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.dropdown-item {
  position: relative;
}

.dropdown-item > a {
  display: block;
  padding: 0.5rem 1rem;
  color: var(--color-text-muted);
  text-decoration: none;
  transition: all var(--transition-fast);
}

.dropdown-item > a:hover,
.dropdown-item > a.active {
  color: var(--color-text);
  background-color: rgba(255, 255, 255, 0.1);
}

.sub-dropdown {
  display: none;
  position: absolute;
  left: 100%;
  top: 0;
  min-width: 140px;
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 0.5rem;
  padding: 0.5rem 0;
  list-style: none;
  margin: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.dropdown-item:hover .sub-dropdown {
  display: block;
}

.sub-dropdown a {
  display: block;
  padding: 0.5rem 1rem;
  color: var(--color-text-muted);
  text-decoration: none;
  transition: all var(--transition-fast);
}

.sub-dropdown a:hover,
.sub-dropdown a.active {
  color: var(--color-text);
  background-color: rgba(255, 255, 255, 0.1);
}

.admin-link {
  padding: 0.5rem 1rem;
  color: var(--color-text-muted);
  text-decoration: none;
  border: 1px solid var(--color-border);
  border-radius: 0.375rem;
  transition: all var(--transition-fast);
}

.admin-link:hover {
  color: var(--color-text);
  border-color: var(--color-text-muted);
}

.public-main {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  width: 100%;
}

.public-footer {
  background-color: var(--color-surface);
  border-top: 1px solid var(--color-border);
  padding: 1.5rem;
  text-align: center;
}

.public-footer p {
  color: var(--color-text-muted);
  margin: 0;
  font-size: 0.875rem;
}
</style>
