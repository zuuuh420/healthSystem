import { CheckCircle2 } from 'lucide-react'
import { VitalMetric } from '../types'
import { AnimatedMetric } from './AnimatedMetric'
import { BodyScanVisualization } from './BodyScanVisualization'

export function BodyStatusCard({ metrics }: { metrics: VitalMetric[] }) { return <section className="body-card"><div className="card-heading"><div><span className="eyebrow">BODY SCAN</span><h2>身体状态</h2></div><span className="wearing-state"><CheckCircle2 size={15} />佩戴状态：已佩戴</span></div><div className="body-card-grid"><BodyScanVisualization /><div className="vitals-panel"><h3>生命体征</h3>{metrics.map(metric => <AnimatedMetric key={metric.key} metric={metric} />)}<div className="activity-strip"><h3>活动数据</h3><div className="activity-main"><strong>5.2 <small>km</small></strong><span>今日距离</span></div><div className="activity-sub"><span><b>382</b> kcal</span><span><b>45</b> min</span><span><b>6,842</b> 步数</span></div></div></div></div></section> }
