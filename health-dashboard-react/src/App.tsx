import { BodyStatusCard } from './components/BodyStatusCard'
import { DeviceCard } from './components/DeviceCard'
import { Sidebar } from './components/Sidebar'
import { SuggestedActions } from './components/SuggestedActions'
import { TopHeader } from './components/TopHeader'
import { TrendChart } from './components/TrendChart'
import { VitalMetric } from './types'

const metrics: VitalMetric[] = [
  { key: 'heartRate', label: '心率', value: '72', unit: 'bpm', icon: 'heart', tone: 'rose' },
  { key: 'oxygen', label: '血氧饱和度', value: '98', unit: '% SpO₂', icon: 'drop', tone: 'mint' },
  { key: 'temperature', label: '体温', value: '36.8', unit: '°C', icon: 'temp', tone: 'amber' },
  { key: 'sleep', label: '睡眠时长', value: '7小时42分', unit: '昨夜', icon: 'moon', tone: 'lilac' }
]

export default function App() { return <div className="app-shell"><Sidebar /><main className="main-content"><TopHeader /><div className="dashboard-content"><div className="hero-heading"><div><span className="eyebrow">7月13日 星期一</span><h1>下午好，演示用户</h1><p>关注身体的信号，做出更好的健康决策。</p></div><div className="hero-actions"><button className="ghost-button">消息通知</button><button className="primary-button">同步设备</button></div></div><div className="top-grid"><BodyStatusCard metrics={metrics} /><DeviceCard /></div><div className="bottom-grid"><TrendChart /><SuggestedActions /></div><p className="disclaimer">* 数据来源于设备监测，仅供参考，不作为临床诊断依据。</p></div></main></div> }
