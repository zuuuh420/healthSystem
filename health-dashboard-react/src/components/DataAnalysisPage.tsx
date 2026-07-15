import { BarChart3, CalendarDays, Target, TrendingUp } from 'lucide-react'
import { VitalMetric } from '../types'
import { TrendChart } from './TrendChart'

export function DataAnalysisPage({ metrics, steps }: { metrics: VitalMetric[]; steps: number }) {
  const sleep = metrics.find(metric => metric.key === 'sleep')?.value ?? '--'
  const heartRate = metrics.find(metric => metric.key === 'heartRate')?.value ?? '--'
  return <section className="analysis-page">
    <header className="analysis-heading"><div><span className="eyebrow">HEALTH ANALYTICS</span><h1>数据分析</h1><p>从连续记录中发现变化，帮助你调整下一步。</p></div><button className="analysis-period-button"><CalendarDays size={15} />近 7 天</button></header>
    <div className="analysis-kpis"><article><span className="analysis-kpi-icon analysis-kpi-icon--green"><TrendingUp size={18} /></span><div><small>平均目标完成率</small><strong>78%</strong><em>较前周期 +6%</em></div></article><article><span className="analysis-kpi-icon analysis-kpi-icon--blue"><BarChart3 size={18} /></span><div><small>平均每日步数</small><strong>6,286</strong><em>当前记录 {steps.toLocaleString()} 步</em></div></article><article><span className="analysis-kpi-icon analysis-kpi-icon--orange"><Target size={18} /></span><div><small>连续记录</small><strong>7 天</strong><em>保持当前记录节奏</em></div></article></div>
    <div className="analysis-main-grid"><TrendChart /><article className="analysis-insight-card"><div className="analysis-card-heading"><div><span className="eyebrow">INSIGHTS</span><h2>趋势解读</h2></div><BarChart3 size={18} /></div><div className="analysis-insight-list"><div><strong>活动表现</strong><p>本周期步数在周中达到高点，周末略有回落，可以提前安排轻量活动。</p></div><div><strong>睡眠记录</strong><p>昨夜记录为 {sleep}，建议继续保持固定起床时间，观察连续 7 天变化。</p></div><div><strong>心率观察</strong><p>当前心率 {heartRate} bpm，单次读数正常，建议结合运动和休息状态一起看。</p></div></div><p className="analysis-note">分析基于当前设备记录，不代表临床结论。</p></article></div>
  </section>
}
