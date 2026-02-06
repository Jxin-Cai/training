import { createRouter, createWebHistory } from 'vue-router'
import PublicLayout from '@/layouts/PublicLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      component: PublicLayout,
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/public/HomeView.vue')
        },
        {
          path: 'content/:id',
          name: 'content-detail',
          component: () => import('@/views/public/ContentDetailView.vue')
        }
      ]
    },
    {
      path: '/admin',
      component: AdminLayout,
      redirect: '/admin/content',
      children: [
        {
          path: 'categories',
          name: 'admin-categories',
          component: () => import('@/views/admin/CategoriesView.vue')
        },
        {
          path: 'content',
          name: 'admin-content',
          component: () => import('@/views/admin/ContentView.vue')
        }
      ]
    }
  ]
})

export default router