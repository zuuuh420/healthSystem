import request from '@/utils/request'

export function getAdminStats() {
  return request({ url: '/admin/stats', method: 'get' })
}

export function getUserList(params) {
  return request({ url: '/admin/users', method: 'get', params })
}

export function updateUserRole(id, role) {
  return request({ url: `/admin/users/${id}/role`, method: 'put', params: { role } })
}

export function deleteUser(id) {
  return request({ url: `/admin/users/${id}`, method: 'delete' })
}

export function resetUserPassword(id) {
  return request({ url: `/admin/users/${id}/reset-password`, method: 'put' })
}
