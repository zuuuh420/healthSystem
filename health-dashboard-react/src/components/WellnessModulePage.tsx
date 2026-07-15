import { Activity, Check, CheckCircle2, CircleHelp, Clock3, Coffee, Droplets, Flame, Headphones, Leaf, Mail, Moon, Play, Send, ShieldCheck, Sparkles, Target, Utensils, Wind } from 'lucide-react'
import { FormEvent, useState } from 'react'
import { SimulatedVitals } from '../hooks/useSimulatedVitals'

export type WellnessKind = '运动管理' | '饮食管理' | '睡眠管理' | '压力管理' | '健康计划' | '帮助与反馈'

const copy: Record<WellnessKind, { eyebrow: string; title: string; description: string }> = {
  运动管理: { eyebrow: 'MOVEMENT', title: '运动管理', description: '把活动拆成今天能完成的小步骤。' },
  饮食管理: { eyebrow: 'NUTRITION', title: '饮食管理', description: '记录饮水和饮食习惯，建立更稳定的日常节奏。' },
  睡眠管理: { eyebrow: 'SLEEP', title: '睡眠管理', description: '从固定作息和睡前习惯开始改善恢复质量。' },
  压力管理: { eyebrow: 'RECOVERY', title: '压力管理', description: '用短时练习帮助身体从紧绷状态慢慢恢复。' },
  健康计划: { eyebrow: 'HEALTH PLAN', title: '健康计划', description: '把长期目标拆成可以持续执行的日常任务。' },
  帮助与反馈: { eyebrow: 'SUPPORT', title: '帮助与反馈', description: '查看使用说明，或把遇到的问题告诉我们。' }
}

function ModuleHeader({ kind }: { kind: WellnessKind }) {
  const item = copy[kind]
  return <header className="wellness-heading"><div><span className="eyebrow">{item.eyebrow}</span><h1>{item.title}</h1><p>{item.description}</p></div></header>
}

function ExerciseModule({ vitals }: { vitals: SimulatedVitals }) {
  return <><div className="wellness-hero wellness-hero--green"><div className="wellness-icon"><Activity size={23} /></div><div><span>今日活动</span><h2>继续保持活动节奏</h2><p>你今天已完成 {vitals.steps.toLocaleString()} 步，建议再安排一段轻松走动。</p></div><strong>68<small>% 目标</small></strong></div><div className="wellness-grid"><article className="wellness-card"><div className="wellness-card-heading"><h2>今日计划</h2><span>1 / 3</span></div><div className="wellness-task is-done"><CheckCircle2 size={17} /><div><strong>完成 10 分钟热身</strong><small>已根据活动记录完成</small></div><Check size={16} /></div><div className="wellness-task"><CircleHelp size={17} /><div><strong>晚饭后轻松走 20 分钟</strong><small>建议保持可以说话的强度</small></div><button><Play size={14} />开始</button></div></article><article className="wellness-card"><div className="wellness-card-heading"><h2>活动提示</h2><Flame size={18} /></div><p className="wellness-card-copy">连续坐着超过一小时，可以起身活动几分钟。今天的目标不需要一次完成，分段完成更容易坚持。</p><div className="wellness-tip"><Target size={16} /><span>本周建议：累计 150 分钟中等强度活动</span></div></article></div></>
}

function NutritionModule() {
  return <><div className="wellness-hero wellness-hero--blue"><div className="wellness-icon"><Droplets size={23} /></div><div><span>今日饮水</span><h2>记录你的第一杯水</h2><p>当前没有接入饮食设备，先用轻量记录建立习惯。</p></div><strong>0<small>/ 6 杯</small></strong></div><div className="wellness-grid"><article className="wellness-card"><div className="wellness-card-heading"><h2>饮水记录</h2><span>今日</span></div><div className="water-cups">{Array.from({ length: 6 }, (_, index) => <button key={index} aria-label={`记录第 ${index + 1} 杯水`}><Droplets size={17} /></button>)}</div><button className="wellness-primary-button"><Droplets size={15} />记录一杯水</button></article><article className="wellness-card"><div className="wellness-card-heading"><h2>饮食建议</h2><Utensils size={18} /></div><p className="wellness-card-copy">优先保证蔬菜、蛋白质和主食的均衡，减少含糖饮料。若有特殊疾病或饮食限制，请以医生或营养师建议为准。</p><div className="wellness-tip wellness-tip--soft"><Leaf size={16} /><span>今天可以从一顿均衡早餐开始</span></div></article></div></>
}

function SleepModule({ vitals }: { vitals: SimulatedVitals }) {
  return <><div className="wellness-hero wellness-hero--purple"><div className="wellness-icon"><Moon size={23} /></div><div><span>昨夜睡眠</span><h2>{vitals.sleep}，保持稳定作息</h2><p>固定起床时间，比偶尔补觉更有助于观察长期变化。</p></div><strong>7<small>小时目标</small></strong></div><div className="wellness-grid"><article className="wellness-card"><div className="wellness-card-heading"><h2>睡前计划</h2><span>建议</span></div><div className="wellness-task is-done"><CheckCircle2 size={17} /><div><strong>固定起床时间</strong><small>让身体形成稳定节奏</small></div></div><div className="wellness-task"><Moon size={17} /><div><strong>睡前一小时减少屏幕</strong><small>给大脑留出放松时间</small></div><button><Check size={14} />完成</button></div><div className="wellness-task"><Coffee size={17} /><div><strong>下午后减少咖啡因</strong><small>避免影响入睡</small></div><button><Check size={14} />完成</button></div></article><article className="wellness-card"><div className="wellness-card-heading"><h2>睡眠记录说明</h2><Clock3 size={18} /></div><p className="wellness-card-copy">当前睡眠时长来自设备模拟记录，只能作为趋势参考。持续数周睡眠困难或白天功能明显受影响时，建议咨询医生。</p></article></div></>
}

function StressModule() {
  const [running, setRunning] = useState(false)
  return <><div className="wellness-hero wellness-hero--warm"><div className="wellness-icon"><Wind size={23} /></div><div><span>恢复练习</span><h2>{running ? '跟随节奏，慢慢呼吸' : '给自己两分钟放松时间'}</h2><p>{running ? '吸气 4 秒，呼气 6 秒，保持舒适，不要勉强。' : '短暂离开屏幕，做几轮缓慢的深呼吸。'}</p></div><button className="wellness-primary-button" onClick={() => setRunning(value => !value)}>{running ? '结束练习' : '开始练习'}</button></div><div className="wellness-grid"><article className="wellness-card"><div className="wellness-card-heading"><h2>今日恢复建议</h2><Headphones size={18} /></div><div className="wellness-focus"><Sparkles size={18} /><div><strong>2 分钟呼吸练习</strong><small>适合在工作间隙或睡前进行</small></div></div><p className="wellness-card-copy">压力管理页面不读取或诊断心理状态，仅提供低风险的日常放松练习。</p></article><article className="wellness-card"><div className="wellness-card-heading"><h2>需要注意</h2><ShieldCheck size={18} /></div><p className="wellness-card-copy">如果持续感到无法放松、情绪低落或影响日常生活，建议尽早和专业人士沟通。</p></article></div></>
}

function PlanModule({ vitals }: { vitals: SimulatedVitals }) {
  return <><div className="wellness-hero wellness-hero--green"><div className="wellness-icon"><Target size={23} /></div><div><span>本周计划进度</span><h2>保持 3 个小习惯</h2><p>从活动、作息和饮水中选择最容易坚持的一项开始。</p></div><strong>2<small>/ 3 完成</small></strong></div><div className="wellness-card wellness-plan-card"><div className="wellness-card-heading"><h2>我的健康计划</h2><span>本周</span></div><div className="wellness-plan-row is-done"><CheckCircle2 size={18} /><div><strong>每日步数达到 6,000 步</strong><small>当前记录 {vitals.steps.toLocaleString()} 步</small></div><b>完成</b></div><div className="wellness-plan-row is-done"><CheckCircle2 size={18} /><div><strong>保持 7 小时以上睡眠</strong><small>昨夜记录 {vitals.sleep}</small></div><b>完成</b></div><div className="wellness-plan-row"><CircleHelp size={18} /><div><strong>每天记录 6 杯水</strong><small>饮食设备尚未接入，可手动记录</small></div><button>开始记录</button></div></div></>
}

function SupportModule() {
  const [sent, setSent] = useState(false)
  const submit = (event: FormEvent<HTMLFormElement>) => { event.preventDefault(); setSent(true) }
  return <div className="support-grid">
    <article className="wellness-card">
      <div className="wellness-card-heading"><div><span className="eyebrow">QUICK HELP</span><h2>常见问题</h2></div><CircleHelp size={18} /></div>
      <div className="support-list"><button>如何关联家人？<ArrowIcon /></button><button>设备数据多久更新一次？<ArrowIcon /></button><button>如何修改个人备注？<ArrowIcon /></button></div>
    </article>
    <form className="wellness-card support-form" onSubmit={submit}>
      <div className="wellness-card-heading"><div><span className="eyebrow">FEEDBACK</span><h2>意见与反馈</h2></div><Mail size={18} /></div>
      <label>告诉我们你遇到的问题<textarea placeholder="请输入反馈内容" required /></label>
      <button className="wellness-primary-button" type="submit"><Send size={15} />{sent ? '已收到反馈' : '提交反馈'}</button>
      {sent && <p className="support-success">感谢你的反馈，我们会持续改进。</p>}
    </form>
  </div>
}

function ArrowIcon() { return <span aria-hidden="true">→</span> }

export function WellnessModulePage({ kind, vitals }: { kind: WellnessKind; vitals: SimulatedVitals }) {
  return <section className="wellness-page"><ModuleHeader kind={kind} />{kind === '运动管理' && <ExerciseModule vitals={vitals} />}{kind === '饮食管理' && <NutritionModule />}{kind === '睡眠管理' && <SleepModule vitals={vitals} />}{kind === '压力管理' && <StressModule />}{kind === '健康计划' && <PlanModule vitals={vitals} />}{kind === '帮助与反馈' && <SupportModule />}</section>
}
