import request from '@/utils/request'

export function createDietRecord(data) {
  return request({ url: '/diet-records', method: 'post', data })
}

export function updateDietRecord(id, data) {
  return request({ url: `/diet-records/${id}`, method: 'put', data })
}

export function deleteDietRecord(id) {
  return request({ url: `/diet-records/${id}`, method: 'delete' })
}

export function getDietRecords(params) {
  return request({ url: '/diet-records', method: 'get', params })
}

export function getDietRecordsByRange(start, end) {
  return request({ url: '/diet-records/range', method: 'get', params: { start, end } })
}

export function getDietStats() {
  return request({ url: '/diet-records/stats', method: 'get' })
}
