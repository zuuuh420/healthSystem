import { FamilyMember, FamilyRelationRequest } from '../types'

type ApiResult<T> = { code: number; msg?: string; data?: T }
type RelationView = {
  id: number
  memberUserId: number
  memberNickname: string
  memberOriginalNickname?: string
  memberInviteCode?: string
  relationship: string
  status: 'PENDING' | 'ACTIVE' | 'REJECTED' | 'REVOKED'
  createdAt?: string
  updatedAt?: string
}
type SnapshotView = {
  relationId: number
  memberUserId: number
  memberNickname: string
  memberOriginalNickname?: string
  memberInviteCode?: string
  relationship: string
  deviceName: string
  deviceOnline: boolean
  wearing: boolean
  heartRate: number | null
  oxygen: number | null
  temperature: number | null
  sleepMinutes: number | null
  steps: number | null
  measuredAt?: string
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
    relationId: relation.id,
    name,
    originalName: relation.memberOriginalNickname || name,
    identityCode: relation.memberInviteCode,
    relationship: relation.relationship || '',
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

function minutesToSleep(minutes: number) {
  return `${Math.floor(minutes / 60)}小时${minutes % 60}分`
}

function toSnapshotMember(snapshot: SnapshotView): FamilyMember {
  const name = snapshot.memberNickname || '已关联家人'
  const available = snapshot.deviceOnline && snapshot.wearing
    && snapshot.heartRate !== null && snapshot.oxygen !== null
    && snapshot.temperature !== null && snapshot.sleepMinutes !== null
    && snapshot.steps !== null
  return {
    id: `family-user-${snapshot.memberUserId}`,
    relationId: snapshot.relationId,
    name,
    originalName: snapshot.memberOriginalNickname || name,
    identityCode: snapshot.memberInviteCode,
    relationship: snapshot.relationship || '',
    initials: name.slice(0, 1),
    linkedAt: '',
    deviceName: snapshot.deviceName,
    deviceOnline: snapshot.deviceOnline,
    wearing: snapshot.wearing,
    vitals: available ? {
      heartRate: snapshot.heartRate!,
      oxygen: snapshot.oxygen!,
      temperature: snapshot.temperature!,
      sleep: minutesToSleep(snapshot.sleepMinutes!),
      steps: snapshot.steps!
    } : null,
    history: [],
    lastSyncAt: snapshot.measuredAt ? '刚刚' : '尚未同步'
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

export async function createFamilyRelation(inviteCode: string, relationship = '') {
  return request<RelationView>('/family/relations', {
    method: 'POST',
    body: JSON.stringify({ inviteCode, relationship })
  })
}

export async function loadFamilyHealthSnapshots() {
  const token = window.localStorage.getItem('token') ?? window.localStorage.getItem('accessToken')
  if (!token) return null
  const snapshots = await request<SnapshotView[]>('/family/health-snapshots')
  return snapshots.map(toSnapshotMember)
}

export async function loadFamilyRequests() {
  const token = window.localStorage.getItem('token') ?? window.localStorage.getItem('accessToken')
  if (!token) return null
  return request<FamilyRelationRequest[]>('/family/requests')
}

export async function decideFamilyRequest(id: number, decision: 'ACCEPT' | 'REJECT', relationship = '') {
  return request<null>(`/family/requests/${id}`, {
    method: 'PATCH',
    body: JSON.stringify({ decision, relationship })
  })
}

export async function updateFamilyRelation(relationId: number, displayName: string | undefined, relationship?: string) {
  return request<null>(`/family/relations/${relationId}`, {
    method: 'PATCH',
    body: JSON.stringify({ ...(displayName !== undefined ? { displayName } : {}), ...(relationship !== undefined ? { relationship } : {}) })
  })
}

export async function removeFamilyRelation(relationId: number) {
  return request<null>(`/family/relations/${relationId}`, { method: 'DELETE' })
}
