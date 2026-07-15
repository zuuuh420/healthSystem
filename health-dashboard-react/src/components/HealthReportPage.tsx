import { Activity, ArrowRight, CheckCircle2, FileText, Moon, TrendingUp } from 'lucide-react'
import { VitalMetric } from '../types'

export function HealthReportPage({ metrics, steps }: { metrics: VitalMetric[]; steps: number }) {
  const heartRate = metrics.find(metric => metric.key === 'heartRate')?.value ?? '--'
  const oxygen = metrics.find(metric => metric.key === 'oxygen')?.value ?? '--'
  const temperature = metrics.find(metric => metric.key === 'temperature')?.value ?? '--'
  const sleep = metrics.find(metric => metric.key === 'sleep')?.value ?? '--'
  return <section className="report-page">
    <header className="report-heading"><div><span className="eyebrow">WEEKLY HEALTH REPORT</span><h1>健康报告</h1><p>把这一周的记录整理成一份清晰的健康摘要。</p></div><button className="report-period"><FileText size={15} />7 月 9 日 — 7 月 15 日</button></header>
    <article className="report-hero"><div className="report-hero-icon"><CheckCircle2 size={25} /></div><div><span>本周总体状态</span><h2>状态稳定，继续保持当前节奏</h2><p>当前生命体征处于日常参考范围内，设备记录连续性良好。</p></div><strong>88<small>/100</small></strong></article>
    <div className="report-metrics"><article><span className="report-metric-label"><Activity size={15} />生命体征</span><strong>{heartRate}<small> bpm</small></strong><p>平均心率记录稳定</p></article><article><span className="report-metric-label"><TrendingUp size={15} />血氧水平</span><strong>{oxygen}<small>%</small></strong><p>当前设备记录值</p></article><article><span className="report-metric-label"><Moon size={15} />睡眠记录</span><strong>{sleep}</strong><p>昨夜设备记录</p></article><article><span className="report-metric-label"><Activity size={15} />今日活动</span><strong>{steps.toLocaleString()}<small> 步</small></strong><p>距离目标还需保持活动</p></article></div>
    <div className="report-content-grid"><article className="report-section-card"><div className="report-card-heading"><div><span className="eyebrow">THIS WEEK</span><h2>本周观察</h2></div><TrendingUp size={18} /></div><div className="report-observation-list"><div><strong>活动规律</strong><p>周中活动量较高，周末略有回落。建议把轻量活动分散到每天，减少连续久坐。</p></div><div><strong>睡眠节奏</strong><p>保持固定起床时间，比单纯追求某一晚的睡眠时长更有助于观察长期变化。</p></div><div><strong>设备记录</strong><p>当前设备在线且有连续数据，未发现需要确认的异常状态。</p></div></div></article><article className="report-section-card report-action-card"><div className="report-card-heading"><div><span className="eyebrow">NEXT WEEK</span><h2>下周建议</h2></div><ArrowRight size={18} /></div><button><span>保持每日活动</span><small>把步数目标拆成几段完成</small><ArrowRight size={15} /></button><button><span>优先规律作息</span><small>尽量保持稳定的起床时间</small><ArrowRight size={15} /></button><button><span>继续记录变化</span><small>连续数据比单次读数更有参考价值</small><ArrowRight size={15} /></button></article></div>
    <p className="report-disclaimer">报告基于当前账户的设备模拟数据生成，仅供日常健康管理参考，不作为临床诊断依据。</p>
  </section>
}
