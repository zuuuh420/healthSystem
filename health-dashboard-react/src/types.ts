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

export type FamilyMember = {
  id: string
  name: string
  relationship: string
  initials: string
  linkedAt: string
  deviceName: string
  deviceOnline: boolean
  wearing: boolean
  vitals: FamilyVital | null
  history: FamilyTrendPoint[]
  lastSyncAt: string
}
