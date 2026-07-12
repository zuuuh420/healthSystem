import request from '@/utils/request'

export function getFoodList(params) {
  return request({ url: '/foods', method: 'get', params })
}

export function getAllFoods() {
  return request({ url: '/foods/all', method: 'get' })
}

export function getFoodCategories() {
  return request({ url: '/foods/categories', method: 'get' })
}

export function getFoodById(id) {
  return request({ url: `/foods/${id}`, method: 'get' })
}

export function createFood(data) {
  return request({ url: '/foods', method: 'post', data })
}

export function updateFood(id, data) {
  return request({ url: `/foods/${id}`, method: 'put', data })
}

export function deleteFood(id) {
  return request({ url: `/foods/${id}`, method: 'delete' })
}

export function batchImportFoods(data) {
  return request({ url: '/foods/batch', method: 'post', data })
}
