import { BatteryMedium, Link2, RefreshCw, Settings2, Wifi } from 'lucide-react'
import { useEffect, useRef, useState } from 'react'
import { DeviceSyncEffect } from './DeviceSyncEffect'

const weekdays = ['周日', '周一', '周二', '周三', '周四', '周五', '周六']

function useCurrentTime() {
  const [now, setNow] = useState<Date | null>(null)

  useEffect(() => {
    let timer: number | undefined
    let active = true
    const clearTimer = () => {
      if (timer !== undefined) window.clearTimeout(timer)
      timer = undefined
    }
    const scheduleNextMinute = () => {
      clearTimer()
      if (!active || document.visibilityState === 'hidden') return
      const current = new Date()
      const delay = 60_000 - (current.getSeconds() * 1_000 + current.getMilliseconds())
      timer = window.setTimeout(() => {
        if (!active || document.visibilityState === 'hidden') return
        setNow(new Date())
        scheduleNextMinute()
      }, delay)
    }
    const refreshOnVisibility = () => {
      clearTimer()
      if (document.visibilityState === 'visible') {
        setNow(new Date())
        scheduleNextMinute()
      }
    }
    setNow(new Date())
    scheduleNextMinute()
    document.addEventListener('visibilitychange', refreshOnVisibility)
    return () => {
      active = false
      clearTimer()
      document.removeEventListener('visibilitychange', refreshOnVisibility)
    }
  }, [])

  if (!now) return { hours: '--', minutes: '--', date: '--/--', weekday: '--', ariaLabel: '健康手环，正在读取当前时间，设备已连接' }
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const date = `${String(now.getMonth() + 1).padStart(2, '0')}/${String(now.getDate()).padStart(2, '0')}`
  const weekday = weekdays[now.getDay()]
  return { hours, minutes, date, weekday, ariaLabel: `健康手环，当前时间${hours}点${minutes}分，设备已连接` }
}

export function DeviceCard({ heartRate = 77, onSyncSuccess }: { heartRate?: number; onSyncSuccess?: () => void }) {
  const [syncing, setSyncing] = useState(false)
  const [status, setStatus] = useState('已连接')
  const timers = useRef<number[]>([])
  const currentTime = useCurrentTime()

  const clearTimers = () => {
    timers.current.forEach(timer => window.clearTimeout(timer))
    timers.current = []
  }

  useEffect(() => clearTimers, [])

  const sync = () => {
    if (syncing) return
    clearTimers()
    setSyncing(true)
    setStatus('正在同步')
    timers.current.push(window.setTimeout(() => {
      onSyncSuccess?.()
      setStatus('同步成功')
      timers.current.push(window.setTimeout(() => {
        setSyncing(false)
        setStatus('已连接')
      }, 2000))
    }, 1700))
  }

  return <section className="device-card"><div className="card-heading"><div><span className="eyebrow">DEVICE CONNECTION</span><h2>设备连接</h2></div><Link2 size={19} color="#6d8871" /></div><div className="band-stage"><DeviceSyncEffect active={syncing} /><svg className="band-svg" viewBox="0 0 280 260" role="img" aria-label={currentTime.ariaLabel}><path d="M103 38 C82 48 74 82 78 116 L90 207 C94 234 119 246 143 239 L177 229 C199 222 208 201 204 176 L190 79 C186 45 165 27 140 29 Z" fill="#344447" stroke="#172d2d" strokeWidth="4" /><rect x="91" y="59" width="82" height="129" rx="19" fill="#182728" stroke="#b7d1be" strokeWidth="5" transform="rotate(-7 132 123)" /><g className="watch-display" transform="rotate(-7 132 123)" aria-hidden="true"><text className="watch-time watch-hour" x="111" y="113">{currentTime.hours}</text><text className="watch-time watch-minute" x="111" y="142">{currentTime.minutes}</text><text className="watch-date" x="111" y="158">{currentTime.date} {currentTime.weekday}</text><text className="watch-heart" x="111" y="174">♥ {heartRate}</text></g><path d="M84 32 L60 9 M184 31 L207 8 M94 210 L76 248 M186 204 L206 241" stroke="#819d88" strokeWidth="19" strokeLinecap="round" /><circle cx="153" cy="164" r="4" fill="#a8c66c" /></svg><span className="device-status"><i />设备状态：{status}</span></div><div className="signal-strength"><div><span>连接信号</span><strong>强</strong></div><div className="signal-bars">{Array.from({ length: 20 }, (_, i) => <i key={i} style={{ height: `${5 + i * 1.5}px` }} />)}</div></div><div className="device-score"><div><span>综合健康分</span><strong>88%</strong></div><span>88 / 100</span></div><div className="device-progress"><span style={{ width: '88%' }} /></div><button className="primary-button" onClick={sync} disabled={syncing}>{syncing ? <RefreshCw size={16} className="spin" /> : <Settings2 size={16} />}{syncing ? '正在同步…' : status === '同步成功' ? '同步成功' : '设备管理'}</button><div className="device-foot"><span><BatteryMedium size={15} />86%</span><span><Wifi size={15} />蓝牙连接</span></div></section>
}
