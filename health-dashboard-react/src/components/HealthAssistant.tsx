import { Bot, CheckCircle2, Send, ShieldCheck, UserRound } from 'lucide-react'
import { FormEvent, useMemo, useState } from 'react'
import { FamilyMember } from '../types'
import { SimulatedVitals } from '../hooks/useSimulatedVitals'
import { remoteAssistantEnabled, requestHealthAssistant } from '../services/healthAssistant'

type Message = { id: number; role: 'assistant' | 'user'; text: string }

function answerQuestion(question: string, members: FamilyMember[], vitals: SimulatedVitals) {
  const text = question.trim().toLowerCase()
  const matched = members.find(member => text.includes(member.name) || text.includes(member.relationship))
  if (matched) {
    if (text.includes('睡眠')) return matched.vitals ? `${matched.name}昨夜睡眠约 ${matched.vitals.sleep}。这是设备记录的结果，仅供日常参考。` : `${matched.name}当前未佩戴手环，暂时没有可用的睡眠数据。`
    if (text.includes('心率') || text.includes('心跳')) return matched.vitals ? `${matched.name}当前心率约 ${matched.vitals.heartRate} bpm，处于本地模拟的正常范围。` : `${matched.name}当前未佩戴手环，暂时无法读取心率。`
    if (text.includes('血氧')) return matched.vitals ? `${matched.name}当前血氧约 ${matched.vitals.oxygen}%，设备状态正常。` : `${matched.name}当前未佩戴手环，暂时无法读取血氧。`
    if (text.includes('体温')) return matched.vitals ? `${matched.name}当前体温约 ${matched.vitals.temperature.toFixed(1)}°C，数据来自设备模拟快照。` : `${matched.name}当前未佩戴手环，暂时无法读取体温。`
    if (text.includes('步数') || text.includes('走了多少')) return matched.vitals ? `${matched.name}今日约 ${matched.vitals.steps.toLocaleString()} 步，数据来自设备模拟快照。` : `${matched.name}当前未佩戴手环，暂时无法读取步数。`
    if (text.includes('在线') || text.includes('设备')) return `${matched.name}当前${matched.deviceOnline ? '设备在线' : '设备离线'}，${matched.wearing ? '手环已佩戴。' : '手环未佩戴。'}`
    return `${matched.name}当前${matched.wearing ? '已佩戴手环，设备在线' : '未佩戴手环'}，上次同步时间为${matched.lastSyncAt}。`
  }
  if (text.includes('我') || text.includes('本人')) {
    if (text.includes('心率')) return `你当前心率约 ${vitals.heartRate} bpm。`
    if (text.includes('血氧')) return `你当前血氧为 ${vitals.oxygen}%。`
    if (text.includes('体温')) return `你当前体温为 ${vitals.temperature.toFixed(1)}°C。`
    if (text.includes('睡眠') || text.includes('睡得')) return `你昨夜睡眠约 ${vitals.sleep}，这是设备记录的结果，仅供日常参考。`
    if (text.includes('步数') || text.includes('走了多少')) return `你今天约走了 ${vitals.steps.toLocaleString()} 步。`
  }
  if (text.includes('家人') || text.includes('谁')) {
    return members.map(member => `${member.name}${member.wearing ? '已佩戴' : '未佩戴'}`).join('，') + '。你可以继续问某位家人的心率、睡眠或设备状态。'
  }
  return '我目前可以帮你查看本人或已关联家人的佩戴状态、设备在线情况、心率、血氧和睡眠。'
}

export function HealthAssistant({ family, userVitals }: { family: FamilyMember[]; userVitals: SimulatedVitals }) {
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)
  const [messages, setMessages] = useState<Message[]>([{ id: 1, role: 'assistant', text: '你好，我是知衡健康助手。我只读取当前用户和已授权家人的数据，可以帮你查看佩戴状态、设备在线情况和健康指标。' }])
  const suggestions = useMemo(() => ['爸爸今天有没有佩戴？', '爸爸今天走了多少步？', '我昨夜睡得怎么样？', '我今天走了多少步？'], [])

  const send = async (value = input) => {
    const question = value.trim()
    if (!question || loading) return
    const nextId = messages.length + 1
    setMessages(current => [...current, { id: nextId, role: 'user', text: question }])
    setInput('')
    setLoading(true)
    try {
      const remoteAnswer = await requestHealthAssistant({ question, family, userVitals })
      setMessages(current => [...current, { id: nextId + 1, role: 'assistant', text: remoteAnswer ?? answerQuestion(question, family, userVitals) }])
    } catch {
      setMessages(current => [...current, { id: nextId + 1, role: 'assistant', text: `${answerQuestion(question, family, userVitals)}（远程助手暂不可用，已使用本地数据回答。）` }])
    } finally {
      setLoading(false)
    }
  }

  const submit = (event: FormEvent) => { event.preventDefault(); void send() }

  return <section className="assistant-page">
    <div className="assistant-heading"><div><span className="eyebrow">HEALTH ASSISTANT</span><h1>健康助手</h1><p>先用授权数据为你做可靠的日常查询，后续可接入 AI 对话能力。</p></div><div className="assistant-mode"><CheckCircle2 size={15} />{remoteAssistantEnabled ? '远程 AI 模式' : '本地数据分析模式'}</div></div>
    <div className="assistant-privacy"><ShieldCheck size={17} /><span>助手只回答当前用户和已授权家人的数据，不会替你做诊断或编造周边信息。</span></div>
    <div className="assistant-shell">
      <div className="assistant-messages">{messages.map(message => <div className={`assistant-message assistant-message--${message.role}`} key={message.id}><span className="assistant-message-icon">{message.role === 'assistant' ? <Bot size={16} /> : <UserRound size={16} />}</span><div><span className="assistant-message-role">{message.role === 'assistant' ? '知衡助手' : '我'}</span><p>{message.text}</p></div></div>)}{loading && <div className="assistant-message assistant-message--assistant"><span className="assistant-message-icon"><Bot size={16} /></span><div><span className="assistant-message-role">知衡助手</span><p className="assistant-loading">正在读取授权数据…</p></div></div>}</div>
      <div className="assistant-suggestions"><span>你可以这样问</span>{suggestions.map(suggestion => <button key={suggestion} onClick={() => void send(suggestion)} disabled={loading}>{suggestion}</button>)}</div>
      <form className="assistant-composer" onSubmit={submit}><input value={input} onChange={event => setInput(event.target.value)} placeholder="问问你的健康数据…" aria-label="输入健康问题" disabled={loading} /><button type="submit" aria-label="发送问题" disabled={loading}><Send size={17} /></button></form>
    </div>
  </section>
}
