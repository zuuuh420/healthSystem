import { useCallback, useEffect, useRef, useState } from 'react'
import { FamilyHealthAlert, FamilyMember, FamilyRelationRequest } from '../types'
import { createFamilyRelation, decideFamilyRequest, loadFamilyHealthSnapshots, loadFamilyRelations, loadFamilyRequests, removeFamilyRelation, updateFamilyRelation } from '../services/familyRelations'
import { advanceHealthStatus, HealthTracker } from '../services/healthStatus'

const DEMO_INVITE_CODE = 'FAMILY-2026'

const seedMembers: FamilyMember[] = [
  {
    id: 'family-dad', identityCode: 'ZH-DEMO-DAD', name: '爸爸', relationship: '父亲', initials: '爸', linkedAt: '2026-07-12',
    deviceName: '知衡 Band 2', deviceOnline: true, wearing: true,
    vitals: { heartRate: 72, oxygen: 98, temperature: 36.6, sleep: '7小时18分', steps: 6842 }, history: [
      { date: '7/08', heartRate: 70, oxygen: 98, sleep: 7.1 }, { date: '7/09', heartRate: 73, oxygen: 98, sleep: 6.8 },
      { date: '7/10', heartRate: 71, oxygen: 99, sleep: 7.4 }, { date: '7/11', heartRate: 72, oxygen: 98, sleep: 7.0 },
      { date: '7/12', heartRate: 74, oxygen: 98, sleep: 7.6 }, { date: '7/13', heartRate: 71, oxygen: 98, sleep: 7.2 },
      { date: '7/14', heartRate: 72, oxygen: 98, sleep: 7.3 }
    ], lastSyncAt: '刚刚'
  },
  {
    id: 'family-mom', identityCode: 'ZH-DEMO-MOM', name: '妈妈', relationship: '母亲', initials: '妈', linkedAt: '2026-07-10',
    deviceName: '知衡 Band 2', deviceOnline: true, wearing: false,
    vitals: null, history: [], lastSyncAt: '12分钟前'
  }
]

const vary = (value: number, min: number, max: number) => Math.max(min, Math.min(max, value + (Math.random() > .5 ? 1 : -1)))

export function useFamilyMembers() {
  const [members, setMembers] = useState<FamilyMember[]>(seedMembers)
  const [message, setMessage] = useState('')
  const [inviteCode, setInviteCode] = useState<string | null>(null)
  const [pendingRequests, setPendingRequests] = useState<FamilyRelationRequest[]>([])
  const [healthAlerts, setHealthAlerts] = useState<FamilyHealthAlert[]>([])
  const trackers = useRef<Record<string, HealthTracker>>({})

  const applyHealthStatus = useCallback((nextMembers: FamilyMember[]) => nextMembers.map(member => {
    const result = advanceHealthStatus(member.deviceOnline, member.wearing, member.vitals, trackers.current[member.id])
    trackers.current[member.id] = result.tracker
    const alert = result.alert
    if (result.shouldNotify && alert) setHealthAlerts(existing => [...existing, { ...alert, id: `${member.id}-${alert.metric}-${result.tracker.notifiedAt}`, memberId: member.id, memberName: member.name, createdAt: alert.confirmedAt ?? new Date().toISOString() }])
    return { ...member, healthStatus: result.status, healthAlert: result.alert, healthAlertHistory: result.tracker.alertHistory }
  }), [])

  const refreshFamilyData = useCallback(async () => {
    const snapshot = await loadFamilyRelations()
    if (!snapshot) return null
    const [remoteMembers, requests] = await Promise.all([loadFamilyHealthSnapshots(), loadFamilyRequests()])
    setInviteCode(snapshot.inviteCode)
    setMembers(applyHealthStatus(remoteMembers ?? snapshot.members))
    setPendingRequests(requests ?? [])
    return true
  }, [])

  useEffect(() => {
    let timer: number | undefined
    let snapshotTimer: number | undefined
    let active = true
    let remoteMode = false
    const clearTimer = () => { if (timer !== undefined) window.clearInterval(timer); timer = undefined }
    const clearSnapshotTimer = () => { if (snapshotTimer !== undefined) window.clearInterval(snapshotTimer); snapshotTimer = undefined }
    const update = () => {
      if (!active || remoteMode || document.visibilityState === 'hidden') return
      setMembers(current => current.map(member => {
        if (!member.wearing || !member.vitals) return member
        return {
          ...member,
          vitals: {
            ...member.vitals,
            heartRate: vary(member.vitals.heartRate, 68, 78),
            oxygen: member.vitals.oxygen === 98 && Math.random() > .8 ? 97 : 98,
            temperature: Number((member.vitals.temperature === 36.6 ? 36.7 : 36.6).toFixed(1)),
            steps: member.vitals.steps + Math.floor(Math.random() * 5)
          },
          lastSyncAt: '刚刚'
        }
      }))
    }
    const refreshRemoteData = () => {
      if (!active || !remoteMode || document.visibilityState === 'hidden') return
      void Promise.all([loadFamilyHealthSnapshots(), loadFamilyRequests()]).then(([remoteMembers, requests]) => {
        if (remoteMembers) setMembers(applyHealthStatus(remoteMembers))
        if (requests) setPendingRequests(requests)
      }).catch(() => setMessage('家人设备数据暂时无法更新。'))
    }
    const resume = () => {
      clearTimer()
      clearSnapshotTimer()
      if (document.visibilityState !== 'visible') return
      if (remoteMode) snapshotTimer = window.setInterval(refreshRemoteData, 5000)
      else timer = window.setInterval(update, 5000)
    }
    resume()
    void refreshFamilyData().then(remote => {
      if (!remote) return
      remoteMode = true
      resume()
    }).catch(() => {
      remoteMode = false
      resume()
      setMessage('后端暂不可用，当前显示本地演示数据。')
    })
    const handleVisibilityChange = () => {
      resume()
      if (document.visibilityState === 'visible') refreshRemoteData()
    }
    document.addEventListener('visibilitychange', handleVisibilityChange)
    return () => { active = false; clearTimer(); clearSnapshotTimer(); document.removeEventListener('visibilitychange', handleVisibilityChange) }
  }, [])

  const addByInviteCode = async (code: string) => {
    if (inviteCode) {
      try {
        await createFamilyRelation(code)
        setMessage('申请已发送，等待对方确认。')
        return true
      } catch (error) {
        setMessage(error instanceof Error ? error.message : '关联失败，请稍后重试。')
        return false
      }
    }
    if (code.trim().toUpperCase() !== DEMO_INVITE_CODE) {
      setMessage('邀请码无效，请确认家人提供的关联码。')
      return false
    }
    if (members.some(member => member.id === 'family-grandma')) {
      setMessage('这个家人已经关联。')
      return false
    }
    setMembers(current => [...current, {
      id: 'family-grandma', identityCode: 'ZH-DEMO-GMA', name: '奶奶', relationship: '祖辈', initials: '奶', linkedAt: '2026-07-14',
      deviceName: '知衡 Band 2', deviceOnline: false, wearing: false, vitals: null, history: [], lastSyncAt: '尚未同步'
    }])
    setMessage('关联成功，已添加奶奶。')
    return true
  }

  const decideRequest = async (id: number, decision: 'ACCEPT' | 'REJECT', relationship = '') => {
    try {
      await decideFamilyRequest(id, decision, relationship)
      setPendingRequests(current => current.filter(request => request.id !== id))
      if (decision === 'ACCEPT') await refreshFamilyData()
      setMessage(decision === 'ACCEPT' ? '关联申请已接受。' : '关联申请已拒绝。')
      return true
    } catch (error) {
      setMessage(error instanceof Error ? error.message : '申请处理失败，请稍后重试。')
      return false
    }
  }

  const updateRelationship = async (memberId: string, relationship: string) => {
    const member = members.find(item => item.id === memberId)
    if (!member?.relationId) return false
    try {
      await updateFamilyRelation(member.relationId, undefined, relationship.trim())
      setMembers(current => current.map(item => item.id === memberId ? { ...item, relationship: relationship.trim() } : item))
      return true
    } catch (error) {
      setMessage(error instanceof Error ? error.message : '关系称谓保存失败，请稍后重试。')
      return false
    }
  }

  const renameMember = async (memberId: string, displayName: string) => {
    const member = members.find(item => item.id === memberId)
    if (!member) return false
    const nextName = displayName.trim()
    if (member.relationId) {
      try {
        await updateFamilyRelation(member.relationId, nextName)
      } catch (error) {
        setMessage(error instanceof Error ? error.message : '备注名保存失败，请稍后重试。')
        return false
      }
    }
    setMembers(current => current.map(item => item.id === memberId ? { ...item, name: nextName || item.originalName || item.name, initials: (nextName || item.originalName || item.name).slice(0, 1) } : item))
    return true
  }

  const removeMember = async (memberId: string) => {
    const member = members.find(item => item.id === memberId)
    if (!member) return false
    if (member.relationId) {
      try {
        await removeFamilyRelation(member.relationId)
      } catch (error) {
        setMessage(error instanceof Error ? error.message : '解除关联失败，请稍后重试。')
        return false
      }
    }
    setMembers(current => current.filter(item => item.id !== memberId))
    setMessage(`已解除与${member.name}的关联。`)
    return true
  }

  const dismissHealthAlert = (id: string) => setHealthAlerts(current => current.filter(alert => alert.id !== id))

  return { members, message, addByInviteCode, inviteCode, demoInviteCode: DEMO_INVITE_CODE, pendingRequests, healthAlerts, dismissHealthAlert, decideRequest, refreshFamilyData, renameMember, updateRelationship, removeMember }
}
