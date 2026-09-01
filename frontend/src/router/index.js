import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

// 路由配置
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '健康概览', icon: 'DataAnalysis' }
      },
      {
        path: 'records',
        name: 'Records',
        component: () => import('@/views/Records.vue'),
        meta: { title: '健康记录', icon: 'Document' }
      },
      {
        path: 'add-record',
        name: 'AddRecord',
        component: () => import('@/views/AddRecord.vue'),
        meta: { title: '录入数据', icon: 'Edit' }
      },
      {
        path: 'charts',
        name: 'Charts',
        component: () => import('@/views/Charts.vue'),
        meta: { title: '数据可视化', icon: 'TrendCharts' }
      },
      {
        path: 'ai',
        name: 'AI',
        component: () => import('@/views/AI.vue'),
        meta: { title: 'AI健康助手', icon: 'MagicStick' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心', icon: 'User' }
      },
      {
        path: 'admin',
        name: 'Admin',
        component: () => import('@/views/Admin.vue'),
        meta: { title: '管理后台', icon: 'Setting', requiresAdmin: true }
      }
    ]
  },
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

// 路由守卫 - 检查登录状态
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const requiresAdmin = to.matched.some(record => record.meta.requiresAdmin)

  // 设置页面标题
  if (to.meta.title) {
    document.title = `${to.meta.title} - 个人健康管理系统`
  }

  if (requiresAuth && !userStore.isLogin) {
    // 需要登录但未登录，跳转登录页
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else if (requiresAdmin && !userStore.isAdmin) {
    // 需要管理员权限但当前不是管理员，跳转首页
    next('/dashboard')
  } else if ((to.path === '/login' || to.path === '/register') && userStore.isLogin) {
    // 已登录状态下访问登录/注册页，跳转首页
    next('/')
  } else {
    next()
  }
})

export default router
