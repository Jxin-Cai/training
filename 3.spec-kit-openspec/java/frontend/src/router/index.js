import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/pages/Home.vue')
  },
  {
    path: '/category/:id',
    name: 'CategoryPage',
    component: () => import('@/pages/CategoryPage.vue')
  },
  {
    path: '/content/:id',
    name: 'ContentPage',
    component: () => import('@/pages/ContentPage.vue')
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/pages/admin/AdminLayout.vue'),
    children: [
      {
        path: '',
        redirect: '/admin/categories'
      },
      {
        path: 'categories',
        name: 'CategoryAdmin',
        component: () => import('@/pages/admin/CategoryAdmin.vue')
      },
      {
        path: 'content',
        name: 'ContentAdmin',
        component: () => import('@/pages/admin/ContentAdmin.vue')
      },
      {
        path: 'content/new',
        name: 'ContentCreate',
        component: () => import('@/pages/admin/ContentEditor.vue')
      },
      {
        path: 'content/:id',
        name: 'ContentEdit',
        component: () => import('@/pages/admin/ContentEditor.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
