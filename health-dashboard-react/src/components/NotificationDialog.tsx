import { AlertTriangle, Bell, Check, CircleCheck, UserRound, X } from 'lucide-react'
import { FamilyHealthAlert, FamilyRelationRequest } from '../types'
import { useState } from 'react'

const relationshipOptions = ['父亲', '母亲', '子女', '配偶', '祖父母', '兄弟姐妹']

function formatRequestTime(value?: string) {
  if (!value) return '刚刚'
  return value.replace('T', ' ').slice(0, 16)
}

export function NotificationDialog({ open, onClose, requests, healthAlerts = [], onDismissHealthAlert, onDecision, processingId }: { open: boolean; onClose: () => void; requests: FamilyRelationRequest[]; healthAlerts?: FamilyHealthAlert[]; onDismissHealthAlert?: (id: string) => void; onDecision: (id: number, decision: 'ACCEPT' | 'REJECT', relationship?: string) => Promise<boolean>; processingId?: number | null }) {
  const [relationshipValues, setRelationshipValues] = useState<Record<number, string>>({})
  if (!open) return null
  return <div className="notification-backdrop" role="presentation" onMouseDown={event => { if (event.target === event.currentTarget) onClose() }}>
    <section className="notification-dialog" role="dialog" aria-modal="true" aria-labelledby="notification-title">
      <header className="notification-dialog-heading"><div><span className="eyebrow">INBOX</span><h2 id="notification-title">消息通知</h2></div><button className="dialog-close" onClick={onClose} aria-label="关闭消息通知"><X size={18} /></button></header>
      <div className="notification-tabs"><button className="is-active">全部 {requests.length + healthAlerts.length > 0 ? `· ${requests.length + healthAlerts.length}` : ''}</button><button>关联申请</button><button>健康提醒</button></div>
      <div className="notification-list">
        {requests.length === 0 && healthAlerts.length === 0 ? <article className="notification-empty"><span className="notification-empty-icon"><Bell size={20} /></span><strong>暂时没有新的消息</strong><p>当家人申请关联或健康状态持续异常时，会在这里提醒你。</p></article> : <div className="notification-request-list">{requests.map(request => { const selected = relationshipValues[request.id] ?? ''; const isCustom = selected.startsWith('__custom__:'); const customValue = selected.slice('__custom__:'.length); return <article className="notification-request" key={request.id}><div className="notification-request-icon"><UserRound size={18} /></div><div className="notification-request-content"><div className="notification-request-meta"><strong>{request.requesterNickname}</strong><time>{formatRequestTime(request.createdAt)}</time></div><p>想与你建立家人健康关联{request.relationship ? `，对方称呼你为“${request.relationship}”` : ''}。</p><label className="notification-relationship-field">我如何称呼对方（可选）<select value={isCustom ? '__custom__' : selected} onChange={event => setRelationshipValues(current => ({ ...current, [request.id]: event.target.value === '__custom__' ? '__custom__:' : event.target.value }))}><option value="">未定义</option>{relationshipOptions.map(option => <option key={option} value={option}>{option}</option>)}<option value="__custom__">自定义</option></select></label>{isCustom && <input className="notification-custom-relationship" value={customValue} placeholder="输入关系称谓" maxLength={32} onChange={event => setRelationshipValues(current => ({ ...current, [request.id]: `__custom__:${event.target.value}` }))} />}<div className="notification-request-actions"><button className="notification-accept" onClick={() => void onDecision(request.id, 'ACCEPT', isCustom ? customValue : selected)} disabled={processingId === request.id}><CircleCheck size={14} />接受</button><button className="notification-reject" onClick={() => void onDecision(request.id, 'REJECT')} disabled={processingId === request.id}><X size={14} />拒绝</button></div></div></article> })}{healthAlerts.map(alert => <article className="notification-request notification-health-alert" key={alert.id}><div className="notification-request-icon"><AlertTriangle size={18} /></div><div className="notification-request-content"><div className="notification-request-meta"><strong>{alert.memberName} · {alert.label}</strong><time>{formatRequestTime(alert.createdAt)}</time></div><p>{alert.message} 当前值：{alert.value}</p><div className="notification-request-actions"><button className="notification-accept" onClick={() => onDismissHealthAlert?.(alert.id)}><Check size={14} />知道了</button></div></div></article>)}</div>}
      </div>
      <footer className="notification-dialog-footer"><span><Check size={14} /> 只展示与当前账户有关的通知</span></footer>
    </section>
  </div>
}
