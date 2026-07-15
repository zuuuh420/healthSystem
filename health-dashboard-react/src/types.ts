export type MetricKey = 'heartRate' | 'oxygen' | 'temperature' | 'sleep'

export type VitalMetric = {
  key: MetricKey
  label: string
  value: string
  unit: string
  icon: string
  tone: 'rose' | 'mint' | 'amber' | 'lilac'
}

export type TrendPoint = { date: string; steps: number; calories: number; goal: number }

export type FamilyVital = { heartRate: number; oxygen: number; temperature: number; sleep: string; steps: number }
export type FamilyTrendPoint = { date: string; heartRate: number; oxygen: number; sleep: number }
export type HealthStatus = 'normal' | 'observing' | 'confirmed' | 'offline' | 'not_wearing'
export type HealthAlert = { metric: 'heartRate' | 'oxygen' | 'temperature' | 'sleep'; label: string; value: string; message: string; confirmedAt?: string }
export type FamilyHealthAlert = HealthAlert & { id: string; memberId: string; memberName: string; createdAt: string }
export type FamilyDeviceEvent = { id: string; title: string; detail: string; tone: 'neutral' | 'success' | 'warning' }

export type FamilyMember = {
  id: string
  relationId?: number
  name: string
  originalName?: string
  identityCode?: string
  relationship: string
  initials: string
  linkedAt: string
  deviceName: string
  deviceOnline: boolean
  wearing: boolean
  vitals: FamilyVital | null
  history: FamilyTrendPoint[]
  lastSyncAt: string
  healthStatus?: HealthStatus
  healthAlert?: HealthAlert | null
  healthAlertHistory?: HealthAlert[]
}

export type FamilyRelationRequest = {
  id: number
  requesterUserId: number
  requesterNickname: string
  relationship: string
  status: 'PENDING'
  createdAt?: string
}

export type UserProfile = {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  avatar: string | null
  role: string
  inviteCode: string
  createTime?: string
}

export type UserProfileUpdate = Pick<UserProfile, 'nickname' | 'email' | 'phone'>
