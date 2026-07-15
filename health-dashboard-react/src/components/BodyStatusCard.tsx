import { CheckCircle2, X } from 'lucide-react'
import { useState } from 'react'
import { VitalMetric } from '../types'
import { AnimatedMetric } from './AnimatedMetric'
import { BodyScanVisualization } from './BodyScanVisualization'
import { getReferenceRange, referenceRanges } from '../services/healthStatus'

export function BodyStatusCard({ metrics, steps }: { metrics: VitalMetric[]; steps: number }) {
  const [selectedKey, setSelectedKey] = useState<'heartRate' | 'oxygen' | 'temperature' | 'sleep' | null>(null)
  const vitals = metrics.filter(metric => metric.key !== 'sleep')
  const sleep = metrics.find(metric => metric.key === 'sleep')
  const heartRate = Number(metrics.find(metric => metric.key === 'heartRate')?.value ?? 77)
  const selectedMetric = selectedKey ? metrics.find(metric => metric.key === selectedKey) : undefined
  const selectedRange = selectedKey ? getReferenceRange(selectedKey) : undefined
  return <section className="body-card"><div className="card-heading"><div><span className="eyebrow">BODY SCAN</span><h2>身体状态</h2></div><span className="wearing-state"><CheckCircle2 size={15} />佩戴状态：已佩戴</span></div><div className="body-card-grid body-status-content"><div className="body-scan-column"><BodyScanVisualization heartRate={heartRate} /></div><div className="vitals-panel vitals-column"><div className="vitals-box"><h3>生命体征</h3>{vitals.map(metric => <AnimatedMetric key={metric.key} metric={metric} onOpen={() => setSelectedKey(metric.key as 'heartRate' | 'oxygen' | 'temperature')} />)}</div><div className="activity-strip"><div className="activity-heading"><h3>活动数据</h3>{sleep && <button type="button" onClick={() => setSelectedKey('sleep')}>昨夜睡眠 <b>{sleep.value}</b></button>}</div><div className="activity-main"><strong>5.2 <small>km</small></strong><span>今日距离</span></div><div className="activity-sub"><span><b>382</b> kcal</span><span><b>45</b> min</span><span><b>{steps.toLocaleString()}</b> 步数</span></div></div></div></div>{selectedMetric && selectedRange && <div className="metric-range-backdrop" role="presentation" onMouseDown={event => { if (event.target === event.currentTarget) setSelectedKey(null) }}><section className="metric-range-popover" role="dialog" aria-modal="true" aria-label={`${selectedMetric.label}参考范围`}><button className="metric-range-close" onClick={() => setSelectedKey(null)} aria-label="关闭参考范围"><X size={16} /></button><span className="eyebrow">REFERENCE RANGE</span><h3>{selectedMetric.label}</h3><div className="metric-range-value"><strong>{selectedMetric.value}</strong><span>{selectedMetric.unit}</span></div><div className="metric-range-track"><i /><b /></div><div className="metric-range-scale"><span>偏低</span><strong>参考范围 {selectedRange.range}</strong><span>偏高</span></div><p>当前数据处于参考范围内。参考范围仅用于日常健康趋势观察，不作为临床诊断依据。</p></section></div>}</section>
}
