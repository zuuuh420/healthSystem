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
