/**
 * 运动与目标管理模块路由配置
 */
export default [
  // 运动类型管理
  {
    path: '/sport/types',
    name: 'SportTypes',
    component: () => import('@/views/sport/SportTypeList.vue'),
    meta: { title: '运动库', icon: 'sport' }
  },
  // 运动记录管理
  {
    path: '/sport/records',
    name: 'SportRecords',
    component: () => import('@/views/sport/SportRecordList.vue'),
    meta: { title: '运动记录', icon: 'record' }
  },
  {
    path: '/sport/records/add',
    name: 'SportRecordAdd',
    component: () => import('@/views/sport/SportRecordForm.vue'),
    meta: { title: '新增运动记录', hidden: true }
  },
  {
    path: '/sport/records/edit/:id',
    name: 'SportRecordEdit',
    component: () => import('@/views/sport/SportRecordForm.vue'),
    meta: { title: '编辑运动记录', hidden: true }
  },
  // 运动计划管理
  {
    path: '/sport/plans',
    name: 'SportPlans',
    component: () => import('@/views/sport/SportPlanList.vue'),
    meta: { title: '运动计划', icon: 'plan' }
  },
  // 运动统计
  {
    path: '/sport/stats',
    name: 'SportStats',
    component: () => import('@/views/sport/SportStats.vue'),
    meta: { title: '运动统计', icon: 'stats' }
  },
  // 健康目标管理
  {
    path: '/goal/list',
    name: 'HealthGoals',
    component: () => import('@/views/goal/HealthGoalList.vue'),
    meta: { title: '健康目标', icon: 'goal' }
  },
  {
    path: '/goal/add',
    name: 'HealthGoalAdd',
    component: () => import('@/views/goal/HealthGoalForm.vue'),
    meta: { title: '新增健康目标', hidden: true }
  },
  {
    path: '/goal/edit/:id',
    name: 'HealthGoalEdit',
    component: () => import('@/views/goal/HealthGoalForm.vue'),
    meta: { title: '编辑健康目标', hidden: true }
  },
  // 每日打卡
  {
    path: '/goal/checkin',
    name: 'DailyCheckin',
    component: () => import('@/views/goal/DailyCheckin.vue'),
    meta: { title: '每日打卡', icon: 'checkin' }
  }
]
