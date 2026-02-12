import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'ContentList',
    component: () => import('@/views/frontend/ContentList.vue'),
    meta: { title: '内容列表' }
  },
  {
    path: '/content/:id',
    name: 'ContentDetail',
    component: () => import('@/views/frontend/ContentDetail.vue'),
    meta: { title: '内容详情' }
  },
  {
    path: '/admin/categories',
    name: 'CategoryManage',
    component: () => import('@/views/admin/CategoryManage.vue'),
    meta: { title: '分类管理' }
  },
  {
    path: '/admin/contents',
    name: 'ContentManage',
    component: () => import('@/views/admin/ContentManage.vue'),
    meta: { title: '内容管理' }
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
