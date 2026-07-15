import { ArrowLeft, ArrowRight, Check, LockKeyhole, Mail, UserRound } from 'lucide-react'
import { FormEvent, useState } from 'react'
import revealHero from '../assets/health-hero-reveal.webp'
import { register } from '../services/auth'

type RegisterPageProps = { onBackToLogin: () => void; onRegistered: () => void }

export function RegisterPage({ onBackToLogin, onRegistered }: RegisterPageProps) {
  const [form, setForm] = useState({ username: '', nickname: '', email: '', password: '', confirmPassword: '' })
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const update = (key: keyof typeof form, value: string) => setForm(current => ({ ...current, [key]: value }))
  const submit = async (event: FormEvent) => {
    event.preventDefault()
    const username = form.username.trim()
    const nickname = form.nickname.trim()
    const email = form.email.trim()
    if (username.length < 3 || username.length > 20) return setError('用户名需要是3到20个字符。')
    if (!nickname) return setError('请输入昵称。')
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) return setError('请输入正确的邮箱地址。')
    if (form.password.length < 6) return setError('密码不能少于6位。')
    if (form.password !== form.confirmPassword) return setError('两次输入的密码不一致。')
    setLoading(true)
    setError('')
    try { await register({ username, nickname, email, password: form.password }); onRegistered() }
    catch (requestError) { setError(requestError instanceof Error ? requestError.message : '注册失败，请稍后重试。') }
    finally { setLoading(false) }
  }
  return <main className="auth-register">
    <section className="auth-register-visual" style={{ backgroundImage: `linear-gradient(180deg, rgba(0,0,0,.16), rgba(0,0,0,.72)), url(${revealHero})` }}>
      <button className="auth-register-brand" onClick={onBackToLogin}><span>✦</span><strong>知衡</strong></button>
      <div className="auth-register-copy"><span>BEGIN YOUR TRACE</span><h1>让今天的记录，<br />成为明天的依据。</h1><p>建立个人账户，持续整理身体指标、运动与饮食变化。</p></div>
    </section>
    <section className="auth-register-form-side">
      <button className="auth-register-back" onClick={onBackToLogin}><ArrowLeft size={15} /> 返回登录</button>
      <div className="auth-register-form-wrap"><span className="auth-register-eyebrow">CREATE ACCOUNT</span><h2>创建你的健康档案</h2><p className="auth-register-intro">只需要基础信息，稍后可以继续完善个人资料。</p>
        <form className="auth-register-form" onSubmit={submit}>
          <div className="auth-register-grid"><label>用户名<div className="auth-register-input"><UserRound size={15} /><input value={form.username} onChange={event => update('username', event.target.value)} placeholder="3-20个字符" autoComplete="username" /></div></label><label>昵称<div className="auth-register-input"><UserRound size={15} /><input value={form.nickname} onChange={event => update('nickname', event.target.value)} placeholder="怎么称呼你" /></div></label></div>
          <label>邮箱<div className="auth-register-input"><Mail size={15} /><input value={form.email} onChange={event => update('email', event.target.value)} placeholder="name@example.com" type="email" autoComplete="email" /></div></label>
          <div className="auth-register-grid"><label>密码<div className="auth-register-input"><LockKeyhole size={15} /><input value={form.password} onChange={event => update('password', event.target.value)} placeholder="至少6位" type="password" autoComplete="new-password" /></div></label><label>确认密码<div className="auth-register-input"><LockKeyhole size={15} /><input value={form.confirmPassword} onChange={event => update('confirmPassword', event.target.value)} placeholder="再次输入" type="password" autoComplete="new-password" /></div></label></div>
          <button className="auth-register-submit" type="submit" disabled={loading}>{loading ? '正在创建…' : '创建账户'}{loading ? <LockKeyhole size={16} /> : <ArrowRight size={16} />}</button>
          {error && <p className="auth-register-error">{error}</p>}
        </form><p className="auth-register-login-tip">已有账户？<button onClick={onBackToLogin}>返回登录</button></p><p className="auth-register-note"><Check size={14} />账户创建成功后，请使用新账号登录健康工作台</p>
      </div>
    </section>
  </main>
}
