import { ArrowLeft, ArrowRight, Bell, Check, Filter, HeartPulse, Link2, Moon, MoreHorizontal, Pencil, Plus, RotateCcw, Search, ShieldCheck, Thermometer, UserRound, UsersRound, Wifi, WifiOff, X } from 'lucide-react'
import { FormEvent, useEffect, useMemo, useRef, useState } from 'react'
import { useFamilyMembers } from '../hooks/useFamilyMembers'
import { FamilyMember, FamilyTrendPoint } from '../types'
import { statusLabel } from '../services/healthStatus'

function Vital({ label, value, unit }: { label: string; value: string | number; unit: string }) {
  return <div className="family-vital"><span>{label}</span><strong>{value}<small>{unit}</small></strong></div>
}

function IdentityLine({ member }: { member: FamilyMember }) {
  const accountName = member.originalName || member.name
  const readableAccountName = accountName && !/^\?+$/.test(accountName)
  return <small className="family-identity-line">{readableAccountName && accountName !== member.name && <>账户名：{accountName} · </>}知衡 ID：{member.identityCode || '演示用户'}</small>
}

function MemberCard({ member, onOpenHealth, onOpenProfile, onRename, onRemove }: { member: FamilyMember; onOpenHealth: () => void; onOpenProfile: () => void; onRename: (name: string) => Promise<boolean>; onRemove: () => Promise<boolean> }) {
  const [editing, setEditing] = useState(false)
  const [value, setValue] = useState(member.name)
  const [saving, setSaving] = useState(false)
  const [menuOpen, setMenuOpen] = useState(false)
  const save = async () => {
    setSaving(true)
    const saved = await onRename(value)
    setSaving(false)
    if (saved) setEditing(false)
  }
  return <article className="family-member-card">
    <div className="family-member-heading">
      <div className="family-avatar">{member.initials}</div>
      <div className="family-member-name">{editing ? <div className="family-name-editor"><input value={value} onChange={event => setValue(event.target.value)} maxLength={64} autoFocus aria-label={`${member.name}备注名`} /><button onClick={() => void save()} disabled={saving} aria-label="保存备注名"><Check size={14} /></button><button onClick={() => setEditing(false)} disabled={saving} aria-label="取消编辑"><X size={14} /></button></div> : <><h3>{member.name}<button className="family-name-edit" onClick={() => { setValue(member.name); setEditing(true) }} aria-label={`编辑${member.name}备注名`}><Pencil size={13} /></button></h3><span>设备：{member.deviceName}</span><IdentityLine member={member} /></>}</div>
      <div className="family-member-heading-actions"><span className={`family-device-state ${member.wearing ? 'is-wearing' : 'is-away'}`}><i />{member.wearing ? '已佩戴' : '未佩戴'}</span><div className="family-member-menu"><button className="family-more-button" onClick={() => setMenuOpen(value => !value)} aria-label="更多家人操作" aria-expanded={menuOpen}><MoreHorizontal size={17} /></button>{menuOpen && <div className="family-more-menu"><button onClick={() => { setMenuOpen(false); if (window.confirm(`确定解除与${member.name}的关联吗？解除后双方将无法继续查看彼此的家人数据。`)) void onRemove() }}>解除关联</button></div>}</div></div>
    </div>
    <div className="family-status-line">
      {member.deviceOnline ? <><Wifi size={14} className="family-online-icon" /><span>设备在线</span></> : <><WifiOff size={14} /><span>设备离线</span></>}
      <span>上次同步：{member.lastSyncAt}</span>
    </div>
    {member.healthStatus && !['normal', 'offline', 'not_wearing'].includes(member.healthStatus) && <div className={`family-health-status family-health-status--${member.healthStatus}`}><Bell size={13} /><span>{statusLabel(member.healthStatus)}{member.healthAlert ? `：${member.healthAlert.label} ${member.healthAlert.value}` : ''}</span></div>}
    <div className="family-health-heading"><HeartPulse size={14} /><span>健康情况</span></div>
    {member.wearing && member.vitals ? <div className="family-vitals-grid">
      <Vital label="心率" value={member.vitals.heartRate} unit=" bpm" />
      <Vital label="血氧" value={member.vitals.oxygen} unit="%" />
      <Vital label="体温" value={member.vitals.temperature.toFixed(1)} unit="°C" />
      <Vital label="睡眠" value={member.vitals.sleep} unit="" />
      <Vital label="步数" value={member.vitals.steps.toLocaleString()} unit="步" />
    </div> : <div className="family-no-data"><ShieldCheck size={18} /><div><strong>暂未获取实时指标</strong><span>佩戴手环后，这里会显示最新健康数据。</span></div></div>}
    {!editing && member.originalName && !/^\?+$/.test(member.originalName) && member.name !== member.originalName && <button className="family-original-name" onClick={() => void onRename(member.originalName!)}><RotateCcw size={13} />使用原名“{member.originalName}”</button>}
    <div className="family-card-actions"><button className="family-health-action" onClick={onOpenHealth}><HeartPulse size={15} />查看健康情况</button><button className="family-profile-action" onClick={onOpenProfile}>个人详情<ArrowRight size={16} /></button></div>
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

function deviceTimeline(member: FamilyMember) {
  return [
    { id: 'linked', title: '建立家人关联', detail: member.linkedAt || '关联关系已建立', tone: 'neutral' as const },
    { id: 'device', title: member.deviceOnline ? '设备在线' : '设备离线', detail: member.deviceOnline ? '设备可以继续上传数据' : '暂时没有新的设备数据', tone: member.deviceOnline ? 'success' as const : 'warning' as const },
    { id: 'wearing', title: member.wearing ? '检测到佩戴' : '当前未佩戴', detail: member.wearing ? '实时指标可用' : '未佩戴期间不判断健康异常', tone: member.wearing ? 'success' as const : 'neutral' as const },
    { id: 'sync', title: '最近一次同步', detail: member.lastSyncAt, tone: 'neutral' as const }
  ]
}

function FamilyDetail({ member, onBack, onRemove }: { member: FamilyMember; onBack: () => void; onRemove: () => Promise<boolean> }) {
  const heartPath = trendPath(member.history, 'heartRate')
  const sleepPath = trendPath(member.history, 'sleep')
  const alert = !member.deviceOnline
    ? { tone: 'warning', title: '设备已离线', text: '暂时无法获取新的实时数据，请确认设备电量和网络连接。' }
    : !member.wearing
      ? { tone: 'info', title: '当前未佩戴手环', text: '佩戴后才会继续采集心率、血氧和体温数据。' }
      : member.healthStatus === 'confirmed' && member.healthAlert
        ? { tone: 'warning', title: '有一项状态需要关注', text: `${member.healthAlert.label} ${member.healthAlert.value}，系统已连续观察到异常，请先确认佩戴和测量状态。` }
        : member.healthStatus === 'observing' && member.healthAlert
          ? { tone: 'info', title: '正在观察一项指标', text: `${member.healthAlert.label}暂时偏离参考范围，连续异常后才会产生提醒。` }
      : { tone: 'success', title: '当前状态稳定', text: '最近一次同步的指标处于模拟正常范围内。' }

  return <section className="family-detail-view">
    <button className="family-back-button" onClick={onBack}><ArrowLeft size={16} />返回家人列表</button>
    <div className="family-detail-heading"><div className="family-avatar">{member.initials}</div><div><span className="eyebrow">FAMILY HEALTH</span><h1>{member.name}的健康情况</h1><p>{member.relationship} · {member.deviceName}</p><IdentityLine member={member} /></div><span className={`family-device-state ${member.wearing ? 'is-wearing' : 'is-away'}`}><i />{member.wearing ? '已佩戴' : '未佩戴'}</span></div>
    <div className={`family-alert family-alert--${alert.tone}`}><Bell size={17} /><div><strong>{alert.title}</strong><span>{alert.text}</span></div></div>
    {member.wearing && member.vitals ? <div className="family-detail-vitals"><div><HeartPulse size={17} /><span>当前心率</span><strong>{member.vitals.heartRate}<small>bpm</small></strong></div><div><ShieldCheck size={17} /><span>血氧水平</span><strong>{member.vitals.oxygen}<small>%</small></strong></div><div><Thermometer size={17} /><span>核心体温</span><strong>{member.vitals.temperature.toFixed(1)}<small>°C</small></strong></div><div><Moon size={17} /><span>昨夜睡眠</span><strong>{member.vitals.sleep}</strong></div><div><HeartPulse size={17} /><span>今日步数</span><strong>{member.vitals.steps.toLocaleString()}<small>步</small></strong></div></div> : <div className="family-detail-empty"><ShieldCheck size={20} /><strong>暂无可用的实时指标</strong><span>佩戴手环后，详情页会自动显示最新数据。</span></div>}
    {member.history.length > 0 && <section className="family-history-card"><div className="family-section-heading"><div><span className="eyebrow">HEALTH TREND</span><h2>近 7 天变化</h2></div><span>系统模拟记录</span></div><div className="family-chart-legend"><span><i className="family-legend-line family-legend-line--heart" />心率</span><span><i className="family-legend-line family-legend-line--sleep" />睡眠时长</span></div><svg className="family-trend-chart" viewBox="0 0 720 220" role="img" aria-label={`${member.name}近7天心率和睡眠趋势`}><path d="M34 184H686 M34 116H686 M34 48H686" stroke="#e9f0e7" /><path d={heartPath} fill="none" stroke="#b7686f" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" /><path d={sleepPath} fill="none" stroke="#3f8058" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round" />{member.history.map((point, index) => <g key={point.date}><text x={34 + (index * 652) / 6} y="207" textAnchor="middle" fill="#849188" fontSize="11">{point.date}</text><circle cx={34 + (index * 652) / 6} cy={184 - ((point.heartRate - 65) / 15) * 136} r="4" fill="#fff" stroke="#b7686f" strokeWidth="2" /></g>)}</svg></section>}
    <section className="family-timeline-card"><div className="family-section-heading"><div><span className="eyebrow">DEVICE TIMELINE</span><h2>设备时间线</h2></div><span>{member.deviceName}</span></div><ol className="family-device-timeline">{deviceTimeline(member).map(event => <li key={event.id} className={`family-device-event family-device-event--${event.tone}`}><i /><div><strong>{event.title}</strong><span>{event.detail}</span></div></li>)}</ol></section>
    <section className="family-alert-history-card"><div className="family-section-heading"><div><span className="eyebrow">STATUS HISTORY</span><h2>状态记录</h2></div><span>仅显示已确认状态</span></div>{member.healthAlertHistory && member.healthAlertHistory.length > 0 ? <ul className="family-alert-history">{member.healthAlertHistory.map((alert, index) => <li key={`${alert.metric}-${alert.confirmedAt ?? index}`}><div><strong>{alert.label} {alert.value}</strong><span>{alert.message}</span></div><time>{alert.confirmedAt?.replace('T', ' ').slice(0, 16) ?? '刚刚'}</time></li>)}</ul> : <div className="family-alert-history-empty"><Check size={16} />最近没有需要关注的状态</div>}</section>
    <button className="family-remove-detail" onClick={() => { if (window.confirm(`确定解除与${member.name}的关联吗？`)) void onRemove() }}>解除关联</button>
  </section>
}

const relationshipOptions = ['父亲', '母亲', '子女', '配偶', '祖父母', '兄弟姐妹']

function FamilyProfileDetail({ member, onBack, onRename, onUpdateRelationship, onRemove }: { member: FamilyMember; onBack: () => void; onRename: (name: string) => Promise<boolean>; onUpdateRelationship: (relationship: string) => Promise<boolean>; onRemove: () => Promise<boolean> }) {
  const [editing, setEditing] = useState(false)
  const [value, setValue] = useState(member.name)
  const [saving, setSaving] = useState(false)
  const [relationshipEditing, setRelationshipEditing] = useState(false)
  const [relationship, setRelationship] = useState(member.relationship || '')
  const [relationshipSaving, setRelationshipSaving] = useState(false)
  const save = async () => {
    setSaving(true)
    const saved = await onRename(value)
    setSaving(false)
    if (saved) setEditing(false)
  }
  const saveRelationship = async () => {
    setRelationshipSaving(true)
    const saved = await onUpdateRelationship(relationship)
    setRelationshipSaving(false)
    if (saved) setRelationshipEditing(false)
  }
  const accountName = member.originalName && !/^\?+$/.test(member.originalName) ? member.originalName : member.name
  return <section className="family-detail-view family-profile-view">
    <button className="family-back-button" onClick={onBack}><ArrowLeft size={16} />返回家人列表</button>
    <div className="family-detail-heading"><div className="family-avatar">{member.initials}</div><div><span className="eyebrow">FAMILY PROFILE</span><h1>{member.name}</h1><p>设备：{member.deviceName}</p></div><span className={`family-device-state ${member.deviceOnline ? 'is-wearing' : 'is-away'}`}><i />{member.deviceOnline ? '设备在线' : '设备离线'}</span></div>
    <section className="family-profile-card"><div className="family-section-heading"><div><span className="eyebrow">IDENTITY</span><h2>个人信息</h2></div><UserRound size={19} /></div><dl className="family-profile-fields"><div><dt>当前称呼</dt><dd>{editing ? <div className="family-name-editor"><input value={value} onChange={event => setValue(event.target.value)} maxLength={64} autoFocus aria-label="家人备注名" /><button onClick={() => void save()} disabled={saving} aria-label="保存备注名"><Check size={14} /></button><button onClick={() => setEditing(false)} disabled={saving} aria-label="取消编辑"><X size={14} /></button></div> : <>{member.name}<button className="family-inline-edit" onClick={() => { setValue(member.name); setEditing(true) }} aria-label="编辑我的备注名"><Pencil size={14} />编辑我的备注名</button></>}</dd></div>{accountName !== member.name && <div><dt>账户昵称</dt><dd>{accountName}</dd></div>}<div><dt>知衡 ID</dt><dd>{member.identityCode || '演示用户'}</dd></div><div><dt>关系称谓</dt><dd>{relationshipEditing ? <div className="family-relationship-editor"><select value={relationship} onChange={event => setRelationship(event.target.value)} aria-label="选择关系称谓"><option value="">未定义</option>{relationshipOptions.map(option => <option key={option} value={option}>{option}</option>)}</select><input value={relationshipOptions.includes(relationship) || !relationship ? '' : relationship} onChange={event => setRelationship(event.target.value)} maxLength={32} placeholder="也可自定义" aria-label="自定义关系称谓" /><button onClick={() => void saveRelationship()} disabled={relationshipSaving} aria-label="保存关系称谓"><Check size={14} /></button><button onClick={() => setRelationshipEditing(false)} disabled={relationshipSaving} aria-label="取消关系称谓编辑"><X size={14} /></button></div> : <>{relationship || '未定义'}<button className="family-inline-edit" onClick={() => { setRelationship(member.relationship || ''); setRelationshipEditing(true) }} aria-label={relationship ? '修改关系称谓' : '设置关系称谓'}><Pencil size={14} />{relationship ? '修改关系称谓' : '设置关系称谓'}</button></>}</dd></div><div><dt>关联时间</dt><dd>{member.linkedAt}</dd></div></dl></section>
    <section className="family-profile-card"><div className="family-section-heading"><div><span className="eyebrow">DEVICE</span><h2>设备状态</h2></div><Wifi size={18} /></div><div className="family-profile-device"><div><span>设备名称</span><strong>{member.deviceName}</strong></div><div><span>连接状态</span><strong>{member.deviceOnline ? '在线' : '离线'}</strong></div><div><span>佩戴状态</span><strong>{member.wearing ? '已佩戴' : '未佩戴'}</strong></div><div><span>最近同步</span><strong>{member.lastSyncAt}</strong></div></div><p className="family-profile-note">健康数据仍需在“健康情况”中查看，本页面仅展示身份与设备关系信息。</p></section>
    <div className="family-profile-actions"><button className="family-remove-detail" onClick={() => { if (window.confirm(`确定解除与${member.name}的关联吗？`)) void onRemove() }}>解除关联</button></div>
  </section>
}

type MemberFilter = 'all' | 'wearing' | 'away' | 'offline'

function FamilyMemberListItem({ member, selected, onSelect }: { member: FamilyMember; selected: boolean; onSelect: () => void }) {
  const hasVitals = member.wearing && member.deviceOnline && member.vitals
  return <button className={`family-member-list-item ${selected ? 'is-selected' : ''}`} onClick={onSelect} aria-current={selected ? 'true' : undefined}>
    <span className="family-list-avatar">{member.initials}</span>
    <span className="family-list-main"><strong>{member.name}</strong><small>设备：{member.deviceName}</small><span className="family-list-status"><i className={member.wearing ? 'is-wearing' : 'is-away'} />{member.wearing ? '已佩戴' : '未佩戴'}<em>{member.deviceOnline ? '在线' : '离线'}</em></span></span>
    <span className="family-list-meta">{hasVitals ? <><b>{member.vitals!.heartRate}</b><small>bpm</small><b>{member.vitals!.oxygen}%</b></> : <small>暂无实时数据</small>}<ArrowRight size={15} /></span>
  </button>
}

function AddFamilyDrawer({ open, onClose, onSubmit, inviteCode, demoInviteCode, message }: { open: boolean; onClose: () => void; onSubmit: (code: string) => Promise<boolean>; inviteCode: string | null; demoInviteCode: string; message: string }) {
  const [step, setStep] = useState<1 | 2>(1)
  const [code, setCode] = useState('')
  const inputRef = useRef<HTMLInputElement>(null)
  useEffect(() => {
    if (!open) return
    setStep(1)
    setCode('')
    const previousOverflow = document.body.style.overflow
    document.body.style.overflow = 'hidden'
    window.setTimeout(() => inputRef.current?.focus(), 0)
    const closeOnEscape = (event: KeyboardEvent) => { if (event.key === 'Escape') onClose() }
    document.addEventListener('keydown', closeOnEscape)
    return () => { document.body.style.overflow = previousOverflow; document.removeEventListener('keydown', closeOnEscape) }
  }, [open, onClose])
  if (!open) return null
  const submit = async (event: FormEvent) => {
    event.preventDefault()
    if (!code.trim()) return
    if (step === 1) { setStep(2); return }
    const success = await onSubmit(code)
    if (success) onClose()
  }
  return <div className="family-drawer-backdrop" role="presentation" onMouseDown={event => { if (event.target === event.currentTarget && (!code.trim() || window.confirm('当前输入还未提交，确定关闭吗？'))) onClose() }}>
    <aside className="family-drawer" role="dialog" aria-modal="true" aria-labelledby="family-drawer-title">
      <header className="family-drawer-header"><div><span className="eyebrow">INVITE FAMILY</span><h2 id="family-drawer-title">添加家人</h2></div><button className="dialog-close" onClick={() => { if (!code.trim() || window.confirm('当前输入还未提交，确定关闭吗？')) onClose() }} aria-label="关闭添加家人"><X size={18} /></button></header>
      <div className="family-drawer-progress"><span className={step === 1 ? 'is-active' : 'is-done'}>1<span>输入关联码</span></span><i /><span className={step === 2 ? 'is-active' : ''}>2<span>确认关联</span></span></div>
      {step === 1 ? <form className="family-drawer-form" onSubmit={submit}><label htmlFor="family-drawer-code">家人的独立关联码</label><input ref={inputRef} id="family-drawer-code" value={code} onChange={event => setCode(event.target.value)} placeholder="例如 ZHXXXXXXXXXX" autoComplete="off" /><p>关联码用于确认对方账户，不会直接公开健康数据。</p>{!inviteCode && <small>演示关联码：{demoInviteCode}</small>}<button className="primary-button" type="submit"><Search size={16} />验证关联码</button></form> : <form className="family-drawer-form" onSubmit={submit}><div className="family-drawer-confirm"><span className="family-list-avatar">家</span><div><strong>待关联家人</strong><p>确认后，对方需要在消息通知中接受申请。</p></div></div><div className="family-drawer-scope"><strong>授权范围</strong><span>仅查看对方已授权的设备状态和健康摘要。</span><span>双方可以分别设置备注名和关系称谓。</span></div><button className="primary-button" type="submit"><Link2 size={16} />发送关联申请</button><button className="family-drawer-back" type="button" onClick={() => setStep(1)}>返回修改关联码</button></form>}
      {message && <p className="family-drawer-message">{message}</p>}
    </aside>
  </div>
}

export type FamilyDashboardProps = { family: ReturnType<typeof useFamilyMembers> }

export function FamilyDashboard({ family }: FamilyDashboardProps) {
  const { members, message, addByInviteCode, inviteCode, demoInviteCode, renameMember, updateRelationship, removeMember } = family
  const [selectedId, setSelectedId] = useState<string | null>(members[0]?.id ?? null)
  const [detailMode, setDetailMode] = useState<'health' | 'profile' | null>(null)
  const [search, setSearch] = useState('')
  const [filter, setFilter] = useState<MemberFilter>('all')
  const [page, setPage] = useState(1)
  const [drawerOpen, setDrawerOpen] = useState(false)
  const wearingCount = members.filter(member => member.wearing).length

  useEffect(() => {
    if (!selectedId && members[0]) setSelectedId(members[0].id)
    if (selectedId && !members.some(member => member.id === selectedId)) setSelectedId(members[0]?.id ?? null)
  }, [members, selectedId])
  useEffect(() => { setPage(1) }, [search, filter])

  const filteredMembers = useMemo(() => {
    const query = search.trim().toLowerCase()
    return members.filter(member => {
      const matchesQuery = !query || [member.name, member.originalName, member.relationship, member.identityCode].filter(Boolean).some(value => value!.toLowerCase().includes(query))
      const matchesFilter = filter === 'all' || (filter === 'wearing' && member.wearing) || (filter === 'away' && !member.wearing) || (filter === 'offline' && !member.deviceOnline)
      return matchesQuery && matchesFilter
    })
  }, [filter, members, search])
  const pageSize = 20
  const totalPages = Math.max(1, Math.ceil(filteredMembers.length / pageSize))
  const visibleMembers = filteredMembers.slice((page - 1) * pageSize, page * pageSize)

  const selectedMember = members.find(member => member.id === selectedId)
  const clearDetail = () => { setSelectedId(null); setDetailMode(null) }
  if (selectedMember && detailMode === 'profile') return <section className="family-dashboard"><FamilyProfileDetail member={selectedMember} onBack={clearDetail} onRename={name => renameMember(selectedMember.id, name)} onUpdateRelationship={name => updateRelationship(selectedMember.id, name)} onRemove={async () => { const removed = await removeMember(selectedMember.id); if (removed) clearDetail(); return removed }} /></section>
  if (selectedMember && detailMode === 'health') return <section className="family-dashboard"><FamilyDetail member={selectedMember} onBack={clearDetail} onRemove={async () => { const removed = await removeMember(selectedMember.id); if (removed) clearDetail(); return removed }} /></section>

  return <section className="family-dashboard">
    <header className="family-page-heading family-page-header">
      <div className="family-page-header__copy"><span className="eyebrow">FAMILY HEALTH</span><h1>家人健康</h1><p>只查看已授权关联家人的设备状态与健康数据。</p></div>
      <div className="family-page-header__actions"><div className="family-summary"><strong>{members.length}</strong><span>位已关联</span><em>{wearingCount} 位正在佩戴</em></div><button className="family-add-button" onClick={() => setDrawerOpen(true)}><Plus size={15} />添加家人</button></div>
    </header>
    <div className="family-privacy-note"><ShieldCheck size={17} /><span>家人数据仅对已关联成员可见，当前设备数据为演示模拟，仅供参考；未佩戴时不会展示虚构的实时指标。</span></div>
    <div className="family-workspace">
      <aside className="family-member-list-panel"><div className="family-list-heading"><div><span className="eyebrow">CONNECTED MEMBERS</span><h2>已关联家人</h2></div><span>{filteredMembers.length} 人</span></div><label className="family-member-search"><Search size={15} /><input value={search} onChange={event => setSearch(event.target.value)} placeholder="搜索姓名、关系或知衡 ID" aria-label="搜索家人" /></label><div className="family-filter-row"><button className={filter === 'all' ? 'is-active' : ''} onClick={() => setFilter('all')}><Filter size={13} />全部</button><button className={filter === 'wearing' ? 'is-active' : ''} onClick={() => setFilter('wearing')}>已佩戴</button><button className={filter === 'away' ? 'is-active' : ''} onClick={() => setFilter('away')}>未佩戴</button><button className={filter === 'offline' ? 'is-active' : ''} onClick={() => setFilter('offline')}>离线</button></div><div className="family-member-list-scroll">{visibleMembers.length > 0 ? visibleMembers.map(member => <FamilyMemberListItem key={member.id} member={member} selected={member.id === selectedId} onSelect={() => { setSelectedId(member.id); setDetailMode(null) }} />) : <div className="family-list-empty"><Search size={20} /><strong>没有匹配的家人</strong><span>试试其他姓名、关系或筛选条件。</span></div>}</div>{totalPages > 1 && <div className="family-pagination"><button onClick={() => setPage(value => Math.max(1, value - 1))} disabled={page === 1}>上一页</button><span>{page} / {totalPages}</span><button onClick={() => setPage(value => Math.min(totalPages, value + 1))} disabled={page === totalPages}>下一页</button></div>}</aside>
      <section className="family-member-detail-panel">{selectedMember ? <MemberCard member={selectedMember} onOpenHealth={() => setDetailMode('health')} onOpenProfile={() => setDetailMode('profile')} onRename={name => renameMember(selectedMember.id, name)} onRemove={() => removeMember(selectedMember.id)} /> : <div className="family-detail-empty family-detail-empty--panel"><UsersRound size={20} /><strong>请选择一位家人</strong><span>从左侧列表选择成员查看设备和健康摘要。</span></div>}</section>
    </div>
    <AddFamilyDrawer open={drawerOpen} onClose={() => setDrawerOpen(false)} onSubmit={invite => addByInviteCode(invite)} inviteCode={inviteCode} demoInviteCode={demoInviteCode} message={message} />
  </section>
}
