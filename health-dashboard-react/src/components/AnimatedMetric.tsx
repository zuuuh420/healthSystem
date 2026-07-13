import { ArrowRight, Droplets, HeartPulse, Moon, Thermometer } from 'lucide-react'
import { VitalMetric } from '../types'
import { LiveSparkline } from './LiveSparkline'

const icons = { heart: HeartPulse, oxygen: Droplets, temperature: Thermometer, sleep: Moon }
export function AnimatedMetric({ metric }: { metric: VitalMetric }) {
  const Icon = icons[metric.key === 'oxygen' ? 'oxygen' : metric.key === 'temperature' ? 'temperature' : metric.key === 'sleep' ? 'sleep' : 'heart']
  return <article className="metric-row"><span className={`metric-icon metric-icon--${metric.tone}`}><Icon size={21} /></span><div className="metric-name"><span>{metric.label}</span><strong className="metric-number">{metric.value}<small>{metric.unit}</small></strong></div><LiveSparkline tone={metric.tone} /><ArrowRight className="metric-arrow" size={16} /></article>
}
