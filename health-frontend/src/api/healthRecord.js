import request from '@/utils/request'

export function createHealthRecord(data) {
  return request({ url: '/health-records', method: 'post', data })
}

export function updateHealthRecord(id, data) {
  return request({ url: `/health-records/${id}`, method: 'put', data })
}

export function deleteHealthRecord(id) {
  return request({ url: `/health-records/${id}`, method: 'delete' })
}

export function getHealthRecords() {
  return request({ url: '/health-records', method: 'get' })
}

export function getHealthRecordsByRange(start, end) {
  return request({ url: '/health-records/range', method: 'get', params: { start, end } })
}
