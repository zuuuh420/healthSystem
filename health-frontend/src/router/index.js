import Vue from 'vue'
import VueRouter from 'vue-router'
import sportGoalRoutes from './sport-goal'

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
  }
]

// 合并所有路由
const routes = [
  ...commonRoutes,
  ...sportGoalRoutes
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
    // 未登录且访问受保护页面，跳转到登录页
    next('/login')
  } else if (token && isPublicPage) {
    // 已登录且访问登录/注册页，跳转到首页
    next('/dashboard')
  } else {
    next()
  }
})

export default router
