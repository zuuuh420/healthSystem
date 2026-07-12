import request from '@/utils/request'

// ============ 健康目标 ============

/**
 * 获取健康目标列表
 */
export function getHealthGoals(params) {
  return request({
    url: '/health-goals',
    method: 'get',
    params
  })
}

/**
 * 创建健康目标
 */
export function addHealthGoal(data) {
  return request({
    url: '/health-goals',
    method: 'post',
    data
  })
}

/**
 * 修改健康目标
 */
export function updateHealthGoal(id, data) {
  return request({
    url: `/health-goals/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除健康目标
 */
export function deleteHealthGoal(id) {
  return request({
    url: `/health-goals/${id}`,
    method: 'delete'
  })
}

/**
 * 更新目标进度
 */
export function updateGoalProgress(id, currentValue) {
  return request({
    url: `/health-goals/${id}/progress`,
    method: 'put',
    params: { currentValue }
  })
}

// ============ 每日打卡 ============

/**
 * 今日打卡
 */
export function checkin(sportRecordId, checkinType = 'sport') {
  return request({
    url: '/checkin',
    method: 'post',
    params: { sportRecordId, checkinType }
  })
}

/**
 * 查询今日是否已打卡
 */
export function getTodayCheckinStatus(checkinType = 'sport') {
  return request({
    url: '/checkin/today',
    method: 'get',
    params: { checkinType }
  })
}

/**
 * 查询连续打卡天数
 */
export function getCheckinStreak(checkinType = 'sport') {
  return request({
    url: '/checkin/streak',
    method: 'get',
    params: { checkinType }
  })
}

/**
 * 查询某月打卡日历
 */
export function getCheckinCalendar(month, checkinType = 'sport') {
  return request({
    url: '/checkin/calendar',
    method: 'get',
    params: { month, checkinType }
  })
}
