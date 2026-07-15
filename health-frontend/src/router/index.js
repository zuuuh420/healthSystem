import Vue from 'vue'
import VueRouter from 'vue-router'
import sportGoalRoutes from './sport-goal'
import dietAdminRoutes from './diet-admin'

Vue.use(VueRouter)

// 公共路由
const commonRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册', hidden: true }
  },
  {
    path: '/',
    redirect: '/dashboard'
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '首页仪表盘' }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue'),
    meta: { title: '个人中心' }
  },
  {
    path: '/analysis',
    name: 'HealthAnalysis',
    component: () => import('@/views/HealthAnalysis.vue'),
    meta: { title: '健康分析' }
  },
  {
    path: '/report',
    name: 'HealthReport',
    component: () => import('@/views/HealthReport.vue'),
    meta: { title: '健康周报' }
  }
]

// 合并所有路由
const routes = [
  ...commonRoutes,
  ...sportGoalRoutes,
  ...dietAdminRoutes
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 不需要登录的页面
  const publicPages = ['/login', '/register']
  const isPublicPage = publicPages.includes(to.path)

  if (!token && !isPublicPage) {
    next('/login')
  } else if (token && isPublicPage) {
    next('/dashboard')
  } else if (to.meta && to.meta.requireAdmin) {
    // 管理员权限检查
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (userInfo.role !== 'admin') {
      next('/dashboard')
    } else {
      next()
    }
  } else {
    next()
  }
})

export default router
