import request from '@/utils/request'

// ============ 运动类型 ============

/**
 * 获取运动类型列表
 */
export function getSportTypes(params) {
  return request({
    url: '/sport-types',
    method: 'get',
    params
  })
}

/**
 * 获取运动类型详情
 */
export function getSportTypeById(id) {
  return request({
    url: `/sport-types/${id}`,
    method: 'get'
  })
}

/**
 * 新增运动类型
 */
export function addSportType(data) {
  return request({
    url: '/sport-types',
    method: 'post',
    data
  })
}

/**
 * 修改运动类型
 */
export function updateSportType(id, data) {
  return request({
    url: `/sport-types/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除运动类型
 */
export function deleteSportType(id) {
  return request({
    url: `/sport-types/${id}`,
    method: 'delete'
  })
}

// ============ 运动记录 ============

/**
 * 获取运动记录列表
 */
export function getSportRecords(params) {
  return request({
    url: '/sport-records',
    method: 'get',
    params
  })
}

/**
 * 获取运动记录详情
 */
export function getSportRecordById(id) {
  return request({
    url: `/sport-records/${id}`,
    method: 'get'
  })
}

/**
 * 新增运动记录
 */
export function addSportRecord(data) {
  return request({
    url: '/sport-records',
    method: 'post',
    data
  })
}

/**
 * 修改运动记录
 */
export function updateSportRecord(id, data) {
  return request({
    url: `/sport-records/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除运动记录
 */
export function deleteSportRecord(id) {
  return request({
    url: `/sport-records/${id}`,
    method: 'delete'
  })
}

/**
 * 获取今日运动记录
 */
export function getTodayRecords() {
  return request({
    url: '/sport-records/today',
    method: 'get'
  })
}

// ============ 运动计划 ============

/**
 * 获取运动计划列表
 */
export function getSportPlans(params) {
  return request({
    url: '/sport-plans',
    method: 'get',
    params
  })
}

/**
 * 创建运动计划
 */
export function addSportPlan(data) {
  return request({
    url: '/sport-plans',
    method: 'post',
    data
  })
}

/**
 * 修改运动计划
 */
export function updateSportPlan(id, data) {
  return request({
    url: `/sport-plans/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除运动计划
 */
export function deleteSportPlan(id) {
  return request({
    url: `/sport-plans/${id}`,
    method: 'delete'
  })
}

// ============ 运动统计 ============

/**
 * 获取本周运动统计
 */
export function getWeeklyStats() {
  return request({
    url: '/sport-stats/weekly',
    method: 'get'
  })
}

/**
 * 获取本月运动统计
 */
export function getMonthlyStats() {
  return request({
    url: '/sport-stats/monthly',
    method: 'get'
  })
}

/**
 * 获取运动趋势数据
 */
export function getSportTrend(days = 30) {
  return request({
    url: '/sport-stats/trend',
    method: 'get',
    params: { days }
  })
}
