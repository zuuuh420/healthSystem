import axios from 'axios'
import { Message, MessageBox } from 'element-ui'
import store from '@/store'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API || '/api',
  timeout: 15000
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data

    // 如果返回的code不是200，认为是错误
    if (res.code !== 200) {
      Message({
        message: res.msg || '请求失败',
        type: 'error',
        duration: 3000
      })

      // 401: Token过期或未登录
      if (res.code === 401) {
        MessageBox.confirm(
          '登录状态已过期，请重新登录',
          '提示',
          {
            confirmButtonText: '重新登录',
            cancelButtonText: '取消',
            type: 'warning'
          }
        ).then(() => {
          // 清除token并跳转到登录页
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          router.push('/login')
        })
      }

      return Promise.reject(new Error(res.msg || '请求失败'))
    }

    return res
  },
  error => {
    console.error('响应错误:', error)

    let message = '网络错误，请稍后重试'
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = '请求参数错误'
          break
        case 401:
          message = '未登录或登录已过期'
          // 清除token并跳转到登录页
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          router.push('/login')
          break
        case 403:
          message = '没有权限访问'
          break
        case 404:
          message = '请求的资源不存在'
          break
        case 500:
          message = '服务器内部错误'
          break
        default:
          message = `请求失败: ${error.response.status}`
      }
    } else if (error.message.includes('timeout')) {
      message = '请求超时，请稍后重试'
    } else if (error.message.includes('Network Error')) {
      message = '网络连接失败，请检查网络'
    }

    Message({
      message: message,
      type: 'error',
      duration: 3000
    })

    return Promise.reject(error)
  }
)

export default service
