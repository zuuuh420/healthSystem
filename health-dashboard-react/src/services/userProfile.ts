import { UserProfile, UserProfileUpdate } from '../types'

type ApiResult<T> = { code: number; msg?: string; data?: T }

const demoProfile: UserProfile = {
  id: 0,
  username: 'demo_user',
  nickname: '演示用户',
  email: 'demo@zhiheng.local',
  phone: '未绑定',
  avatar: null,
  role: 'user',
  inviteCode: 'ZH-DEMO-USER'
}

function token() {
  return window.localStorage.getItem('token') ?? window.localStorage.getItem('accessToken')
}

async function request<T>(path: string, init?: RequestInit) {
  const response = await fetch(`/api${path}`, {
    ...init,
    headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token()}`, ...(init?.headers ?? {}) }
  })
  if (!response.ok) throw new Error(`profile request failed: ${response.status}`)
  const result = await response.json() as ApiResult<T>
  if (result.code !== 200 || result.data === undefined) throw new Error(result.msg ?? '资料暂不可用')
  return result.data
}

export async function loadUserProfile() {
  if (!token()) return { profile: demoProfile, remote: false }
  return { profile: await request<UserProfile>('/auth/me'), remote: true }
}

export async function updateUserProfile(update: UserProfileUpdate) {
  return request<UserProfile>('/auth/profile', { method: 'PUT', body: JSON.stringify(update) })
}

export async function uploadUserAvatar(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  const response = await fetch('/api/auth/avatar', { method: 'POST', headers: { Authorization: `Bearer ${token()}` }, body: formData })
  const result = await response.json() as ApiResult<{ avatar: string }>
  if (!response.ok || result.code !== 200 || !result.data) throw new Error(result.msg ?? '头像上传失败')
  return result.data.avatar
}

export async function changeUserPassword(oldPassword: string, newPassword: string) {
  return request<null>('/auth/password', { method: 'PUT', body: JSON.stringify({ oldPassword, newPassword }) })
}
