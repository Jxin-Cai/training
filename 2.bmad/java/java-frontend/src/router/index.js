import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  // 前台路由
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/article/:id',
    name: 'Article',
    component: () => import('@/views/Article.vue'),
    meta: { title: '文章详情' }
  },
  {
    path: '/category/:id',
    name: 'Category',
    component: () => import('@/views/Category.vue'),
    meta: { title: '分类' }
  },
  
  // 后台路由
  {
    path: '/admin/login',
    name: 'Login',
    component: () => import('@/views/admin/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/views/admin/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '仪表盘' }
      },
      {
        path: 'articles',
        name: 'ArticleList',
        component: () => import('@/views/admin/ArticleList.vue'),
        meta: { title: '文章管理' }
      },
      {
        path: 'articles/new',
        name: 'ArticleCreate',
        component: () => import('@/views/admin/ArticleEdit.vue'),
        meta: { title: '新建文章' }
      },
      {
        path: 'articles/:id/edit',
        name: 'ArticleEdit',
        component: () => import('@/views/admin/ArticleEdit.vue'),
        meta: { title: '编辑文章' }
      },
      {
        path: 'categories',
        name: 'CategoryManage',
        component: () => import('@/views/admin/CategoryManage.vue'),
        meta: { title: '分类管理' }
      },
      {
        path: 'users',
        name: 'UserManage',
        component: () => import('@/views/admin/UserManage.vue'),
        meta: { title: '用户管理' }
      }
    ]
  },
  
  // 404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/NotFound.vue'),
    meta: { title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - Java CMS` : 'Java CMS'
  
  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) {
      next('/admin/login')
      return
    }
  }
  
  next()
})

export default router
