import { useCallback, useEffect, useState } from 'react'
import { UserProfile, UserProfileUpdate } from '../types'
import { loadUserProfile, updateUserProfile, uploadUserAvatar } from '../services/userProfile'

const fallbackProfile: UserProfile = { id: 0, username: 'demo_user', nickname: '演示用户', email: 'demo@zhiheng.local', phone: '未绑定', avatar: null, role: 'user', inviteCode: 'ZH-DEMO-USER' }

export function useUserProfile() {
  const [profile, setProfile] = useState<UserProfile | null>(null)
  const [message, setMessage] = useState('')

  const refreshProfile = useCallback(async () => {
    try {
      const result = await loadUserProfile()
      setProfile(result.profile)
      return result.profile
    } catch {
      setMessage('资料暂不可用，当前显示本地演示资料。')
      setProfile(fallbackProfile)
      return fallbackProfile
    }
  }, [])

  useEffect(() => { void refreshProfile() }, [refreshProfile])

  const saveProfile = async (update: UserProfileUpdate) => {
    if (!profile) return false
    try {
      const next = window.localStorage.getItem('token') || window.localStorage.getItem('accessToken')
        ? await updateUserProfile(update)
        : { ...profile, ...update }
      setProfile(next)
      setMessage('资料已更新。')
      return true
    } catch (error) {
      setMessage(error instanceof Error ? error.message : '资料保存失败。')
      return false
    }
  }

  const saveAvatar = async (file: File) => {
    if (!profile) return false
    try {
      const avatar = window.localStorage.getItem('token') || window.localStorage.getItem('accessToken')
        ? await uploadUserAvatar(file)
        : URL.createObjectURL(file)
      setProfile(current => current ? { ...current, avatar } : current)
      setMessage('头像已更新。')
      return true
    } catch (error) {
      setMessage(error instanceof Error ? error.message : '头像上传失败。')
      return false
    }
  }

  return { profile, message, refreshProfile, saveProfile, saveAvatar }
}
