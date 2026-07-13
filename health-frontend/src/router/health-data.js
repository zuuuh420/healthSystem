/**
 * 健康数据管理模块路由配置
 * 负责人：李滔
 */
export default [
  {
    path: '/health/weight',
    name: 'HealthWeight',
    component: () => import('@/views/health/HealthWeightList.vue'),
    meta: { title: '体重管理', icon: 'weight' }
  },
  {
    path: '/health/blood-pressure',
    name: 'HealthBloodPressure',
    component: () => import('@/views/health/HealthBloodPressureList.vue'),
    meta: { title: '血压管理', icon: 'bp' }
  },
  {
    path: '/health/blood-sugar',
    name: 'HealthBloodSugar',
    component: () => import('@/views/health/HealthBloodSugarList.vue'),
    meta: { title: '血糖管理', icon: 'sugar' }
  },
  {
    path: '/health/heart-rate',
    name: 'HealthHeartRate',
    component: () => import('@/views/health/HealthHeartRateList.vue'),
    meta: { title: '心率管理', icon: 'heart' }
  }
]
