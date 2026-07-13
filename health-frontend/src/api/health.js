import request from '@/utils/request'

// ============ 体重管理 ============
export function getWeightList(params) {
  return request({ url: '/health-weight', method: 'get', params })
}
export function getWeightTrend(limit = 30) {
  return request({ url: '/health-weight/trend', method: 'get', params: { limit } })
}
export function getWeightById(id) {
  return request({ url: `/health-weight/${id}`, method: 'get' })
}
export function addWeight(data) {
  return request({ url: '/health-weight', method: 'post', data })
}
export function updateWeight(id, data) {
  return request({ url: `/health-weight/${id}`, method: 'put', data })
}
export function deleteWeight(id) {
  return request({ url: `/health-weight/${id}`, method: 'delete' })
}

// ============ 血压管理 ============
export function getBloodPressureList(params) {
  return request({ url: '/health-blood-pressure', method: 'get', params })
}
export function getBloodPressureTrend(limit = 30) {
  return request({ url: '/health-blood-pressure/trend', method: 'get', params: { limit } })
}
export function getBloodPressureById(id) {
  return request({ url: `/health-blood-pressure/${id}`, method: 'get' })
}
export function addBloodPressure(data) {
  return request({ url: '/health-blood-pressure', method: 'post', data })
}
export function updateBloodPressure(id, data) {
  return request({ url: `/health-blood-pressure/${id}`, method: 'put', data })
}
export function deleteBloodPressure(id) {
  return request({ url: `/health-blood-pressure/${id}`, method: 'delete' })
}

// ============ 血糖管理 ============
export function getBloodSugarList(params) {
  return request({ url: '/health-blood-sugar', method: 'get', params })
}
export function getBloodSugarTrend(limit = 30) {
  return request({ url: '/health-blood-sugar/trend', method: 'get', params: { limit } })
}
export function getBloodSugarById(id) {
  return request({ url: `/health-blood-sugar/${id}`, method: 'get' })
}
export function addBloodSugar(data) {
  return request({ url: '/health-blood-sugar', method: 'post', data })
}
export function updateBloodSugar(id, data) {
  return request({ url: `/health-blood-sugar/${id}`, method: 'put', data })
}
export function deleteBloodSugar(id) {
  return request({ url: `/health-blood-sugar/${id}`, method: 'delete' })
}

// ============ 心率管理 ============
export function getHeartRateList(params) {
  return request({ url: '/health-heart-rate', method: 'get', params })
}
export function getHeartRateTrend(limit = 30) {
  return request({ url: '/health-heart-rate/trend', method: 'get', params: { limit } })
}
export function getHeartRateById(id) {
  return request({ url: `/health-heart-rate/${id}`, method: 'get' })
}
export function addHeartRate(data) {
  return request({ url: '/health-heart-rate', method: 'post', data })
}
export function updateHeartRate(id, data) {
  return request({ url: `/health-heart-rate/${id}`, method: 'put', data })
}
export function deleteHeartRate(id) {
  return request({ url: `/health-heart-rate/${id}`, method: 'delete' })
}
