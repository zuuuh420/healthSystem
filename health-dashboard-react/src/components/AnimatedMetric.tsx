import { ArrowRight, Droplets, HeartPulse, Moon, Thermometer } from 'lucide-react'
import { VitalMetric } from '../types'
import { LiveSparkline } from './LiveSparkline'

const icons = { heart: HeartPulse, oxygen: Droplets, temperature: Thermometer, sleep: Moon }
export function AnimatedMetric({ metric, onOpen }: { metric: VitalMetric; onOpen?: () => void }) {
  const Icon = icons[metric.key === 'oxygen' ? 'oxygen' : metric.key === 'temperature' ? 'temperature' : metric.key === 'sleep' ? 'sleep' : 'heart']
  return <button className="metric-row" type="button" onClick={onOpen} aria-label={`查看${metric.label}参考范围`}><span className={`metric-icon metric-icon--${metric.tone}`}><Icon size={21} /></span><span className="metric-name"><span>{metric.label}</span><strong key={metric.value} className="metric-number">{metric.value}<small>{metric.unit}</small></strong></span><LiveSparkline tone={metric.tone} /><ArrowRight className="metric-arrow" size={16} /></button>
}
