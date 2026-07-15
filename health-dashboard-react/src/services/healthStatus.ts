import { FamilyVital, HealthAlert, HealthStatus } from '../types'

export type ReferenceRange = {
  key: 'heartRate' | 'oxygen' | 'temperature' | 'sleep'
  label: string
  unit: string
  range: string
  min: number
  max: number
  warningMin: number
  warningMax: number
  alertMin: number
  alertMax: number
}

export const referenceRanges: ReferenceRange[] = [
  { key: 'heartRate', label: '心率', unit: 'bpm', range: '60–100 bpm', min: 60, max: 100, warningMin: 50, warningMax: 119, alertMin: 45, alertMax: 120 },
  { key: 'oxygen', label: '血氧饱和度', unit: '%', range: '95–100%', min: 95, max: 100, warningMin: 92, warningMax: 100, alertMin: 0, alertMax: 91 },
  { key: 'temperature', label: '体温', unit: '°C', range: '36.0–37.3°C', min: 36, max: 37.3, warningMin: 35.5, warningMax: 37.9, alertMin: 0, alertMax: 35.4 },
  { key: 'sleep', label: '睡眠时长', unit: '小时', range: '7–9 小时', min: 7, max: 9, warningMin: 6, warningMax: 9, alertMin: 0, alertMax: 5.99 }
]

export type HealthTracker = { metric?: HealthAlert['metric']; abnormalCount: number; normalCount: number; confirmed: boolean; confirmedAt?: string; notifiedAt?: number; alert?: HealthAlert; alertHistory: HealthAlert[] }
export type HealthSampleResult = { status: HealthStatus; alert: HealthAlert | null; tracker: HealthTracker; shouldNotify: boolean }

function numericSleep(value: string) {
  const match = value.match(/(\d+)小时(\d+)分/)
  return match ? Number(match[1]) + Number(match[2]) / 60 : 0
}

function metricValues(vitals: FamilyVital) {
  return { heartRate: vitals.heartRate, oxygen: vitals.oxygen, temperature: vitals.temperature, sleep: numericSleep(vitals.sleep) }
}

export function getReferenceRange(key: ReferenceRange['key']) {
  return referenceRanges.find(item => item.key === key)!
}

export function findOutOfRange(vitals: FamilyVital): HealthAlert | null {
  const values = metricValues(vitals)
  for (const reference of referenceRanges) {
    const value = values[reference.key]
    const isAlert = reference.key === 'heartRate'
      ? value < 45 || value > 120
      : reference.key === 'oxygen'
        ? value <= 91
        : reference.key === 'temperature'
          ? value < 35.5 || value >= 38
          : value < 6
    const isWarning = value < reference.min || value > reference.max
    if (isAlert || isWarning) return { metric: reference.key, label: reference.label, value: `${value}${reference.key === 'temperature' ? '°C' : reference.key === 'oxygen' ? '%' : reference.key === 'heartRate' ? ' bpm' : ' 小时'}`, message: `${reference.label}暂时偏离参考范围，系统正在继续观察。` }
  }
  return null
}

export function advanceHealthStatus(deviceOnline: boolean, wearing: boolean, vitals: FamilyVital | null, previous: HealthTracker = { abnormalCount: 0, normalCount: 0, confirmed: false, alertHistory: [] }, now = Date.now()): HealthSampleResult {
  const tracker: HealthTracker = { ...previous, alertHistory: [...(previous.alertHistory ?? [])] }
  if (!deviceOnline) { tracker.abnormalCount = 0; tracker.normalCount = 0; tracker.confirmed = false; return { status: 'offline', alert: null, tracker, shouldNotify: false } }
  if (!wearing || !vitals) { tracker.abnormalCount = 0; tracker.normalCount = 0; tracker.confirmed = false; return { status: 'not_wearing', alert: null, tracker, shouldNotify: false } }
  const outOfRange = findOutOfRange(vitals)
  if (!outOfRange) {
    tracker.abnormalCount = 0
    if (tracker.confirmed) {
      tracker.normalCount += 1
      if (tracker.normalCount < 2) return { status: 'confirmed', alert: tracker.alert ?? null, tracker, shouldNotify: false }
    }
    tracker.normalCount = 0
    tracker.confirmed = false
    tracker.metric = undefined
    tracker.alert = undefined
    return { status: 'normal', alert: null, tracker, shouldNotify: false }
  }
  tracker.normalCount = 0
  const sameMetric = tracker.metric === outOfRange.metric
  tracker.metric = outOfRange.metric
  tracker.abnormalCount = sameMetric ? tracker.abnormalCount + 1 : 1
  const threshold = outOfRange.metric === 'oxygen' || outOfRange.metric === 'temperature' ? 2 : 3
  let shouldNotify = false
  if (tracker.abnormalCount >= threshold && !tracker.confirmed) {
    tracker.confirmed = true
    tracker.confirmedAt = new Date(now).toISOString()
    tracker.alert = { ...outOfRange, confirmedAt: tracker.confirmedAt }
    tracker.alertHistory = [tracker.alert, ...tracker.alertHistory].slice(0, 10)
    if (!tracker.notifiedAt || now - tracker.notifiedAt >= 30 * 60 * 1000) { tracker.notifiedAt = now; shouldNotify = true }
  }
  return { status: tracker.confirmed ? 'confirmed' : 'observing', alert: tracker.confirmed ? tracker.alert ?? null : outOfRange, tracker, shouldNotify }
}

export function statusLabel(status: HealthStatus) {
  return ({ normal: '状态稳定', observing: '观察中', confirmed: '需要关注', offline: '设备离线', not_wearing: '未佩戴' } as Record<HealthStatus, string>)[status]
}
