import { useEffect, useState } from 'react'
import { FamilyMember } from '../types'
import { createFamilyRelation, loadFamilyHealthSnapshots, loadFamilyRelations } from '../services/familyRelations'

const DEMO_INVITE_CODE = 'FAMILY-2026'

const seedMembers: FamilyMember[] = [
  {
    id: 'family-dad', name: '爸爸', relationship: '父亲', initials: '爸', linkedAt: '2026-07-12',
    deviceName: '知衡 Band 2', deviceOnline: true, wearing: true,
    vitals: { heartRate: 72, oxygen: 98, temperature: 36.6, sleep: '7小时18分', steps: 6842 }, history: [
      { date: '7/08', heartRate: 70, oxygen: 98, sleep: 7.1 }, { date: '7/09', heartRate: 73, oxygen: 98, sleep: 6.8 },
      { date: '7/10', heartRate: 71, oxygen: 99, sleep: 7.4 }, { date: '7/11', heartRate: 72, oxygen: 98, sleep: 7.0 },
      { date: '7/12', heartRate: 74, oxygen: 98, sleep: 7.6 }, { date: '7/13', heartRate: 71, oxygen: 98, sleep: 7.2 },
      { date: '7/14', heartRate: 72, oxygen: 98, sleep: 7.3 }
    ], lastSyncAt: '刚刚'
  },
  {
    id: 'family-mom', name: '妈妈', relationship: '母亲', initials: '妈', linkedAt: '2026-07-10',
    deviceName: '知衡 Band 2', deviceOnline: true, wearing: false,
    vitals: null, history: [], lastSyncAt: '12分钟前'
  }
]

const vary = (value: number, min: number, max: number) => Math.max(min, Math.min(max, value + (Math.random() > .5 ? 1 : -1)))

export function useFamilyMembers() {
  const [members, setMembers] = useState<FamilyMember[]>(seedMembers)
  const [message, setMessage] = useState('')
  const [inviteCode, setInviteCode] = useState<string | null>(null)

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
    const refreshRemoteSnapshots = () => {
      if (!active || !remoteMode || document.visibilityState === 'hidden') return
      void loadFamilyHealthSnapshots().then(remoteMembers => {
        if (remoteMembers) setMembers(remoteMembers)
      }).catch(() => setMessage('家人设备数据暂时无法更新。'))
    }
    const resume = () => {
      clearTimer()
      clearSnapshotTimer()
      if (document.visibilityState !== 'visible') return
      if (remoteMode) snapshotTimer = window.setInterval(refreshRemoteSnapshots, 5000)
      else timer = window.setInterval(update, 5000)
    }
    resume()
    void loadFamilyRelations().then(snapshot => {
      if (!snapshot) return
      remoteMode = true
      setInviteCode(snapshot.inviteCode)
      return loadFamilyHealthSnapshots().then(remoteMembers => {
        setMembers(remoteMembers ?? snapshot.members)
        resume()
      })
    }).catch(() => {
      remoteMode = false
      resume()
      setMessage('后端暂不可用，当前显示本地演示数据。')
    })
    const handleVisibilityChange = () => {
      resume()
      if (document.visibilityState === 'visible') refreshRemoteSnapshots()
    }
    document.addEventListener('visibilitychange', handleVisibilityChange)
    return () => { active = false; clearTimer(); clearSnapshotTimer(); document.removeEventListener('visibilitychange', handleVisibilityChange) }
  }, [])

  const addByInviteCode = async (code: string) => {
    if (inviteCode) {
      try {
        const member = await createFamilyRelation(code)
        setMembers(current => current.some(item => item.id === member.id) ? current : [...current, member])
        setMessage(`关联成功，已添加${member.name}。`)
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
      id: 'family-grandma', name: '奶奶', relationship: '祖辈', initials: '奶', linkedAt: '2026-07-14',
      deviceName: '知衡 Band 2', deviceOnline: false, wearing: false, vitals: null, history: [], lastSyncAt: '尚未同步'
    }])
    setMessage('关联成功，已添加奶奶。')
    return true
  }

  return { members, message, addByInviteCode, inviteCode, demoInviteCode: DEMO_INVITE_CODE }
}
