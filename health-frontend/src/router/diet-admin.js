/**
 * 饮食管理 & 管理端路由
 * 负责人：刘子豪
 */
const dietAdminRoutes = [
  {
    path: '/foods',
    name: 'FoodLibrary',
    component: () => import('@/views/diet/FoodLibrary.vue'),
    meta: { title: '食物库' }
  },
  {
    path: '/diet',
    name: 'DietRecord',
    component: () => import('@/views/diet/DietRecord.vue'),
    meta: { title: '饮食记录' }
  },
  {
    path: '/diet-stats',
    name: 'CalorieStats',
    component: () => import('@/views/diet/CalorieStats.vue'),
    meta: { title: '热量统计' }
  },
  {
    path: '/health-data',
    name: 'HealthData',
    component: () => import('@/views/health/HealthData.vue'),
    meta: { title: '健康数据' }
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('@/views/admin/AdminDashboard.vue'),
    meta: { title: '系统概览', requireAdmin: true }
  },
  {
    path: '/admin/users',
    name: 'UserManagement',
    component: () => import('@/views/admin/UserManagement.vue'),
    meta: { title: '用户管理', requireAdmin: true }
  }
]

export default dietAdminRoutes
