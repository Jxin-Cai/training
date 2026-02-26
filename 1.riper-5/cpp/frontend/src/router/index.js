import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/posts' },
  { path: '/posts', component: () => import('../views/PostList.vue'), meta: { showHeader: true } },
  { path: '/posts/:id', component: () => import('../views/PostDetail.vue'), meta: { showHeader: true } },
  { path: '/admin/categories', component: () => import('../views/admin/CategoryList.vue') },
  { path: '/admin/contents', component: () => import('../views/admin/ContentList.vue') },
  { path: '/admin/contents/new', component: () => import('../views/admin/ContentForm.vue') },
  { path: '/admin/contents/edit/:id', component: () => import('../views/admin/ContentForm.vue') },
]

export default createRouter({
  history: createWebHistory(),
  routes,
})
