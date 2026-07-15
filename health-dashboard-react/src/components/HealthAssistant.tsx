import { Bot, CheckCircle2, Send, ShieldCheck, UserRound } from 'lucide-react'
import { FormEvent, useMemo, useState } from 'react'
import { FamilyMember } from '../types'
import { SimulatedVitals } from '../hooks/useSimulatedVitals'
import { remoteAssistantEnabled, requestHealthAssistant } from '../services/healthAssistant'
import { answerLocalHealthQuestion, LocalAssistantAnswer } from '../services/healthAssistantLocal'

type Message = { id: number; role: 'assistant' | 'user'; text: string; answer?: Pick<LocalAssistantAnswer, 'category' | 'severity' | 'source'> }

const sourceLabel: Record<LocalAssistantAnswer['source'], string> = { device: '设备数据', 'health-guide': '健康建议', 'safety-guide': '安全提醒' }

export function HealthAssistant({ family, userVitals }: { family: FamilyMember[]; userVitals: SimulatedVitals }) {
  const [input, setInput] = useState('')
  const [loading, setLoading] = useState(false)
  const [messages, setMessages] = useState<Message[]>([{ id: 1, role: 'assistant', text: '你好，我是知衡健康助手。我只读取当前用户和已授权家人的数据，可以帮你查看佩戴状态、设备在线情况和健康指标。' }])
  const suggestions = useMemo(() => ['我昨夜睡得怎么样？', '每天运动多久合适？', '饭后多久适合运动？', '爸爸今天有没有佩戴？'], [])

  const send = async (value = input) => {
    const question = value.trim()
    if (!question || loading) return
    const nextId = messages.length + 1
    setMessages(current => [...current, { id: nextId, role: 'user', text: question }])
    setInput('')
    setLoading(true)
    try {
      const localAnswer = answerLocalHealthQuestion(question, { family, userVitals })
      const remoteAnswer = await requestHealthAssistant({ question, family, userVitals })
      const finalAnswer = remoteAnswer ? { text: remoteAnswer, category: 'lifestyle' as const, severity: 'normal' as const, source: 'health-guide' as const } : localAnswer
      setMessages(current => [...current, { id: nextId + 1, role: 'assistant', text: finalAnswer.text, answer: finalAnswer }])
    } catch {
      const localAnswer = answerLocalHealthQuestion(question, { family, userVitals })
      setMessages(current => [...current, { id: nextId + 1, role: 'assistant', text: `${localAnswer.text}${remoteAssistantEnabled ? '（远程助手暂不可用，已使用本地健康知识回答。）' : ''}`, answer: localAnswer }])
    } finally {
      setLoading(false)
    }
  }

  const submit = (event: FormEvent) => { event.preventDefault(); void send() }

  return <section className="assistant-page">
    <div className="assistant-heading"><div><span className="eyebrow">HEALTH ASSISTANT</span><h1>健康助手</h1><p>先用授权数据为你做可靠的日常查询，后续可接入 AI 对话能力。</p></div><div className="assistant-mode"><CheckCircle2 size={15} />{remoteAssistantEnabled ? '远程 AI 模式' : '本地数据分析模式'}</div></div>
    <div className="assistant-privacy"><ShieldCheck size={17} /><span>助手只回答当前用户和已授权家人的数据，不会替你做诊断或编造周边信息。</span></div>
    <div className="assistant-shell">
      <div className="assistant-messages">{messages.map(message => <div className={`assistant-message assistant-message--${message.role}${message.answer?.severity === 'urgent' ? ' assistant-message--urgent' : ''}`} key={message.id}><span className="assistant-message-icon">{message.role === 'assistant' ? <Bot size={16} /> : <UserRound size={16} />}</span><div><div className="assistant-message-meta"><span className="assistant-message-role">{message.role === 'assistant' ? '知衡助手' : '我'}</span>{message.answer && <span className={`assistant-answer-source assistant-answer-source--${message.answer.severity}`}>{sourceLabel[message.answer.source]}</span>}</div><p>{message.text}</p></div></div>)}{loading && <div className="assistant-message assistant-message--assistant"><span className="assistant-message-icon"><Bot size={16} /></span><div><span className="assistant-message-role">知衡助手</span><p className="assistant-loading">正在整理健康信息…</p></div></div>}</div>
      <div className="assistant-suggestions"><span>你可以这样问</span>{suggestions.map(suggestion => <button key={suggestion} onClick={() => void send(suggestion)} disabled={loading}>{suggestion}</button>)}</div>
      <form className="assistant-composer" onSubmit={submit}><input value={input} onChange={event => setInput(event.target.value)} placeholder="问问健康数据或日常健康问题…" aria-label="输入健康问题" disabled={loading} /><button type="submit" aria-label="发送问题" disabled={loading}><Send size={17} /></button></form>
    </div>
  </section>
}
