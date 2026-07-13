import { Activity, BarChart3, BedDouble, ClipboardList, Gauge, HeartPulse, Link2, Menu, Settings, ShieldCheck, Sparkles, Utensils, Wind } from 'lucide-react'
import { useState } from 'react'

const items = [
  ['概览', Gauge], ['健康监测', HeartPulse], ['数据分析', BarChart3], ['健康报告', ClipboardList], ['设备连接', Link2], ['运动管理', Activity], ['饮食管理', Utensils], ['睡眠管理', BedDouble], ['压力管理', Wind], ['健康计划', ShieldCheck], ['设置中心', Settings]
] as const

export function Sidebar() {
  const [collapsed, setCollapsed] = useState(false)
  const [active, setActive] = useState('概览')
  return <aside className={`sidebar ${collapsed ? 'sidebar--collapsed' : ''}`}>
    <div className="brand"><span className="brand-mark"><Sparkles size={18} /></span><span className="brand-copy"><strong>知衡健康</strong><small>个人健康工作台</small></span></div>
    <nav>{items.map(([label, Icon]) => <button key={label} className={active === label ? 'nav-item nav-item--active' : 'nav-item'} onClick={() => setActive(label)} title={label}><Icon size={18} /><span>{label}</span></button>)}</nav>
    <div className="sidebar-bottom"><button className="nav-item"><Sparkles size={18} /><span>帮助与反馈</span></button><button className="collapse-button" onClick={() => setCollapsed(value => !value)}><Menu size={18} /><span>{collapsed ? '展开' : '收起导航'}</span></button></div>
  </aside>
}
