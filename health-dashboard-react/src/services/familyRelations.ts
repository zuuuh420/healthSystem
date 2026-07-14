import { FamilyMember } from '../types'

type ApiResult<T> = { code: number; msg?: string; data?: T }
type RelationView = {
  id: number
  memberUserId: number
  memberNickname: string
  relationship: string
  status: 'ACTIVE' | 'REVOKED'
  createdAt?: string
  updatedAt?: string
}

function getAuthHeaders(): Record<string, string> {
  const token = window.localStorage.getItem('token') ?? window.localStorage.getItem('accessToken')
  return token ? { Authorization: `Bearer ${token}` } : {}
}

async function request<T>(path: string, init?: RequestInit) {
  const response = await fetch(`/api${path}`, {
    ...init,
    headers: { 'Content-Type': 'application/json', ...getAuthHeaders(), ...(init?.headers ?? {}) }
  })
  if (!response.ok) throw new Error(`family request failed: ${response.status}`)
  const result = await response.json() as ApiResult<T>
  if (result.code !== 200 || result.data === undefined) throw new Error(result.msg ?? '家人数据暂不可用')
  return result.data
}

function toMember(relation: RelationView): FamilyMember {
  const name = relation.memberNickname || '已关联家人'
  return {
    id: `family-user-${relation.memberUserId}`,
    name,
    relationship: relation.relationship,
    initials: name.slice(0, 1),
    linkedAt: relation.createdAt?.slice(0, 10) ?? '',
    deviceName: '知衡 Band 2',
    deviceOnline: false,
    wearing: false,
    vitals: null,
    history: [],
    lastSyncAt: '尚未同步'
  }
}

export async function loadFamilyRelations() {
  const token = window.localStorage.getItem('token') ?? window.localStorage.getItem('accessToken')
  if (!token) return null
  const [inviteCode, relations] = await Promise.all([
    request<{ inviteCode: string }>('/family/invite-code'),
    request<RelationView[]>('/family/relations')
  ])
  return { inviteCode: inviteCode.inviteCode, members: relations.filter(item => item.status === 'ACTIVE').map(toMember) }
}

export async function createFamilyRelation(inviteCode: string, relationship = '家人') {
  const relation = await request<RelationView>('/family/relations', {
    method: 'POST',
    body: JSON.stringify({ inviteCode, relationship })
  })
  return toMember(relation)
}
