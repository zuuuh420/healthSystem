import { Activity, CheckCircle2, Clock3, HeartPulse, Info, Thermometer, Waves } from 'lucide-react'
import { VitalMetric } from '../types'
import { getReferenceRange, statusLabel } from '../services/healthStatus'

const trend = [74, 76, 75, 78, 77, 76, 77]

function TrendLine() {
  const points = trend.map((value, index) => `${18 + index * 49},${120 - (value - 70) * 7}`).join(' ')
  return <svg className="monitoring-trend-svg" viewBox="0 0 360 140" role="img" aria-label="近7天心率趋势"><path d="M18 120H342" /><polyline points={points} /><g>{trend.map((value, index) => <circle key={`${value}-${index}`} cx={18 + index * 49} cy={120 - (value - 70) * 7} r="4" />)}</g></svg>
}

function MetricPanel({ metric }: { metric: VitalMetric }) {
  const range = getReferenceRange(metric.key)
  const Icon = metric.key === 'heartRate' ? HeartPulse : metric.key === 'oxygen' ? Waves : metric.key === 'temperature' ? Thermometer : Clock3
  return <article className="monitoring-metric-card"><div className={`monitoring-metric-icon monitoring-metric-icon--${metric.tone}`}><Icon size={19} /></div><div className="monitoring-metric-copy"><span>{metric.label}</span><strong>{metric.value}<small>{metric.unit}</small></strong><em><CheckCircle2 size={13} />{statusLabel('normal')}</em></div><div className="monitoring-range"><span>参考范围</span><strong>{range.range}</strong></div></article>
}

export function HealthMonitoringPage({ metrics, steps }: { metrics: VitalMetric[]; steps: number }) {
  return <section className="monitoring-page">
    <header className="monitoring-heading"><div><span className="eyebrow">HEALTH MONITORING</span><h1>健康监测</h1><p>集中查看当前生命体征和近期变化。</p></div><span className="monitoring-live-state"><i />设备在线 · 刚刚更新</span></header>
    <div className="monitoring-summary"><Activity size={19} /><div><strong>当前状态稳定</strong><span>数据来自已连接设备，仅用于日常趋势观察。</span></div><span className="monitoring-summary-time">更新频率 · 5 秒</span></div>
    <div className="monitoring-metrics">{metrics.map(metric => <MetricPanel key={metric.key} metric={metric} />)}</div>
    <div className="monitoring-lower-grid"><article className="monitoring-trend-card"><div className="monitoring-card-heading"><div><span className="eyebrow">RECENT TREND</span><h2>近 7 天心率变化</h2></div><span className="monitoring-card-note">单位：bpm</span></div><div className="monitoring-chart"><div className="monitoring-chart-scale"><span>85</span><span>80</span><span>75</span><span>70</span></div><TrendLine /></div><div className="monitoring-chart-days">{['7/09','7/10','7/11','7/12','7/13','7/14','今天'].map(day => <span key={day}>{day}</span>)}</div></article><article className="monitoring-side-card"><div className="monitoring-card-heading"><div><span className="eyebrow">TODAY</span><h2>今日摘要</h2></div><Info size={18} /></div><div className="monitoring-summary-list"><div><span>今日步数</span><strong>{steps.toLocaleString()}<small> 步</small></strong></div><div><span>昨夜睡眠</span><strong>{metrics.find(metric => metric.key === 'sleep')?.value ?? '--'}</strong></div><div><span>状态记录</span><strong className="monitoring-good">无异常确认</strong></div></div><p className="monitoring-disclaimer">单次读数不能代表完整健康状况。若身体出现明显不适，请优先寻求专业医疗帮助。</p></article></div>
  </section>
}
