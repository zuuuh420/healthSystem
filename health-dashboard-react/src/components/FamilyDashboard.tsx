import { ArrowLeft, Bell, HeartPulse, Link2, Moon, Plus, ShieldCheck, Thermometer, Wifi, WifiOff } from 'lucide-react'
import { FormEvent, useState } from 'react'
import { useFamilyMembers } from '../hooks/useFamilyMembers'
import { FamilyMember, FamilyTrendPoint } from '../types'

function Vital({ label, value, unit }: { label: string; value: string | number; unit: string }) {
  return <div className="family-vital"><span>{label}</span><strong>{value}<small>{unit}</small></strong></div>
}

function MemberCard({ member, onOpenDetails }: { member: FamilyMember; onOpenDetails: () => void }) {
  return <article className="family-member-card">
    <div className="family-member-heading">
      <div className="family-avatar">{member.initials}</div>
      <div><h3>{member.name}</h3><span>{member.relationship} · {member.deviceName}</span></div>
      <span className={`family-device-state ${member.wearing ? 'is-wearing' : 'is-away'}`}><i />{member.wearing ? '已佩戴' : '未佩戴'}</span>
    </div>
    <div className="family-status-line">
      {member.deviceOnline ? <><Wifi size={14} className="family-online-icon" /><span>设备在线</span></> : <><WifiOff size={14} /><span>设备离线</span></>}
      <span>上次同步：{member.lastSyncAt}</span>
    </div>
    {member.wearing && member.vitals ? <div className="family-vitals-grid">
      <Vital label="心率" value={member.vitals.heartRate} unit=" bpm" />
      <Vital label="血氧" value={member.vitals.oxygen} unit="%" />
      <Vital label="体温" value={member.vitals.temperature.toFixed(1)} unit="°C" />
      <Vital label="睡眠" value={member.vitals.sleep} unit="" />
    </div> : <div className="family-no-data"><ShieldCheck size={18} /><div><strong>暂未获取实时指标</strong><span>佩戴手环后，这里会显示最新健康数据。</span></div></div>}
    <button className="family-detail-button" onClick={onOpenDetails}>查看健康详情 <ArrowLeft size={14} className="family-detail-arrow" /></button>
  </article>
}

function trendPath(history: FamilyTrendPoint[], key: 'heartRate' | 'sleep') {
  if (!history.length) return ''
  const values = history.map(point => point[key])
  const min = key === 'heartRate' ? 65 : 5
  const max = key === 'heartRate' ? 80 : 9
  return values.map((value, index) => {
    const x = 34 + (index * 652) / Math.max(1, values.length - 1)
    const y = 184 - ((value - min) / (max - min)) * 136
    return `${index === 0 ? 'M' : 'L'} ${x.toFixed(1)} ${y.toFixed(1)}`
  }).join(' ')
}

function FamilyDetail({ member, onBack }: { member: FamilyMember; onBack: () => void }) {
  const heartPath = trendPath(member.history, 'heartRate')
  const sleepPath = trendPath(member.history, 'sleep')
  const alert = !member.deviceOnline
    ? { tone: 'warning', title: '设备已离线', text: '暂时无法获取新的实时数据，请确认设备电量和网络连接。' }
    : !member.wearing
      ? { tone: 'info', title: '当前未佩戴手环', text: '佩戴后才会继续采集心率、血氧和体温数据。' }
      : { tone: 'success', title: '当前状态稳定', text: '最近一次同步的指标处于模拟正常范围内。' }

  return <section className="family-detail-view">
    <button className="family-back-button" onClick={onBack}><ArrowLeft size={16} />返回家人列表</button>
    <div className="family-detail-heading"><div className="family-avatar">{member.initials}</div><div><span className="eyebrow">FAMILY MEMBER</span><h1>{member.name}的健康详情</h1><p>{member.relationship} · {member.deviceName}</p></div><span className={`family-device-state ${member.wearing ? 'is-wearing' : 'is-away'}`}><i />{member.wearing ? '已佩戴' : '未佩戴'}</span></div>
    <div className={`family-alert family-alert--${alert.tone}`}><Bell size={17} /><div><strong>{alert.title}</strong><span>{alert.text}</span></div></div>
    {member.wearing && member.vitals ? <div className="family-detail-vitals"><div><HeartPulse size={17} /><span>当前心率</span><strong>{member.vitals.heartRate}<small>bpm</small></strong></div><div><ShieldCheck size={17} /><span>血氧水平</span><strong>{member.vitals.oxygen}<small>%</small></strong></div><div><Thermometer size={17} /><span>核心体温</span><strong>{member.vitals.temperature.toFixed(1)}<small>°C</small></strong></div><div><Moon size={17} /><span>昨夜睡眠</span><strong>{member.vitals.sleep}</strong></div></div> : <div className="family-detail-empty"><ShieldCheck size={20} /><strong>暂无可用的实时指标</strong><span>佩戴手环后，详情页会自动显示最新数据。</span></div>}
    {member.history.length > 0 && <section className="family-history-card"><div className="family-section-heading"><div><span className="eyebrow">HEALTH TREND</span><h2>近 7 天变化</h2></div><span>系统模拟记录</span></div><div className="family-chart-legend"><span><i className="family-legend-line family-legend-line--heart" />心率</span><span><i className="family-legend-line family-legend-line--sleep" />睡眠时长</span></div><svg className="family-trend-chart" viewBox="0 0 720 220" role="img" aria-label={`${member.name}近7天心率和睡眠趋势`}><path d="M34 184H686 M34 116H686 M34 48H686" stroke="#e9f0e7" /><path d={heartPath} fill="none" stroke="#b7686f" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" /><path d={sleepPath} fill="none" stroke="#3f8058" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" />{member.history.map((point, index) => <g key={point.date}><text x={34 + (index * 652) / 6} y="207" textAnchor="middle" fill="#849188" fontSize="11">{point.date}</text><circle cx={34 + (index * 652) / 6} cy={184 - ((point.heartRate - 65) / 15) * 136} r="4" fill="#fff" stroke="#b7686f" strokeWidth="2" /></g>)}</svg></section>}
  </section>
}

export type FamilyDashboardProps = { family: ReturnType<typeof useFamilyMembers> }

export function FamilyDashboard({ family }: FamilyDashboardProps) {
  const { members, message, addByInviteCode, inviteCode, demoInviteCode } = family
  const [code, setCode] = useState('')
  const [selectedId, setSelectedId] = useState<string | null>(null)
  const wearingCount = members.filter(member => member.wearing).length

  const submit = async (event: FormEvent) => {
    event.preventDefault()
    if (await addByInviteCode(code)) setCode('')
  }

  const selectedMember = members.find(member => member.id === selectedId)
  if (selectedMember) return <section className="family-dashboard"><FamilyDetail member={selectedMember} onBack={() => setSelectedId(null)} /></section>

  return <section className="family-dashboard">
    <div className="family-page-heading">
      <div><span className="eyebrow">FAMILY HEALTH</span><h1>家人健康</h1><p>只查看已授权关联家人的设备状态与健康数据。</p></div>
      <div className="family-summary"><strong>{members.length}</strong><span>位已关联家人</span><em>{wearingCount} 位正在佩戴</em></div>
    </div>
    <div className="family-privacy-note"><ShieldCheck size={17} /><span>家人数据仅对已关联成员可见，未佩戴时不会展示虚构的实时指标。</span></div>
    <div className="family-layout">
      <div className="family-member-list"><div className="family-section-heading"><div><span className="eyebrow">CONNECTED MEMBERS</span><h2>已关联家人</h2></div><span>{members.length} 人</span></div>{members.map(member => <MemberCard key={member.id} member={member} onOpenDetails={() => setSelectedId(member.id)} />)}</div>
      <aside className="family-link-card"><div className="family-link-icon"><Link2 size={19} /></div><span className="eyebrow">INVITE FAMILY</span><h2>添加家人</h2><p>输入家人设备端生成的关联码，建立授权查看关系。</p>{inviteCode && <div className="family-own-code"><span>我的关联码</span><strong>{inviteCode}</strong></div>}<form onSubmit={submit}><label htmlFor="family-code">关联码</label><input id="family-code" value={code} onChange={event => setCode(event.target.value)} placeholder="输入家人的独立关联码" /><button className="primary-button" type="submit"><Plus size={16} />关联家人</button></form>{!inviteCode && <small>演示关联码：{demoInviteCode}</small>}{message && <div className={`family-form-message ${message.includes('成功') ? 'is-success' : ''}`}>{message}</div>}</aside>
    </div>
  </section>
}
