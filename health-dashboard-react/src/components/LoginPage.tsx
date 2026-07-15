import { ArrowRight, LockKeyhole, UserRound, X } from 'lucide-react'
import { FormEvent, useEffect, useRef, useState } from 'react'
import baseHero from '../assets/health-hero-base.webp'
import revealHero from '../assets/health-hero-reveal.webp'
import { login } from '../services/auth'

const SPOTLIGHT_RADIUS = 260

export function LoginPage({ onLogin, onOpenRegister, notice }: { onLogin: (token: string) => void; onOpenRegister: () => void; notice?: string }) {
  const [loginOpen, setLoginOpen] = useState(false)
  const [intendedFeature, setIntendedFeature] = useState('')
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [pointer, setPointer] = useState({ x: window.innerWidth * .52, y: window.innerHeight * .56 })
  const inputRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
    if (!loginOpen) return
    const closeOnEscape = (event: KeyboardEvent) => { if (event.key === 'Escape') setLoginOpen(false) }
    document.addEventListener('keydown', closeOnEscape)
    const previousOverflow = document.body.style.overflow
    document.body.style.overflow = 'hidden'
    window.setTimeout(() => inputRef.current?.focus(), 0)
    return () => { document.removeEventListener('keydown', closeOnEscape); document.body.style.overflow = previousOverflow }
  }, [loginOpen])

  const openLogin = (feature = '') => { setIntendedFeature(feature); setError(''); setLoginOpen(true) }
  const submit = async (event: FormEvent) => {
    event.preventDefault()
    if (!username.trim() || password.length < 6) { setError('请输入用户名和至少6位密码。'); return }
    setLoading(true)
    setError('')
    try {
      const result = await login(username.trim(), password)
      onLogin(result.token)
    } catch (requestError) {
      setError(requestError instanceof Error ? requestError.message : '登录失败，请稍后重试。')
    } finally { setLoading(false) }
  }

  const mask = `radial-gradient(circle ${SPOTLIGHT_RADIUS}px at ${pointer.x}px ${pointer.y}px, #fff 0%, #fff 38%, rgba(255,255,255,.76) 58%, rgba(255,255,255,.35) 74%, transparent 100%)`
  return <main className="auth-hero" onMouseMove={event => setPointer({ x: event.clientX, y: event.clientY })} onTouchMove={event => { const touch = event.touches[0]; if (touch) setPointer({ x: touch.clientX, y: touch.clientY }) }}>
    <div className="auth-hero-image auth-hero-image--base" style={{ backgroundImage: `url(${baseHero})` }} />
    <div className="auth-hero-image auth-hero-image--reveal" style={{ backgroundImage: `url(${revealHero})`, WebkitMaskImage: mask, maskImage: mask }} />
    <div className="auth-hero-shade" />
    {notice && <div className="auth-global-notice" role="status">{notice}</div>}
    <nav className="auth-hero-nav"><button className="auth-brand" onClick={() => setPointer({ x: window.innerWidth * .52, y: window.innerHeight * .56 })}><span>✦</span><strong>知衡</strong></button><div className="auth-nav-pill"><button className="is-active">健康首页</button><button onClick={() => openLogin('记录数据')}>数据记录</button><button onClick={() => openLogin('运动计划')}>运动计划</button><button onClick={() => openLogin('健康周报')}>健康周报</button></div><div className="auth-nav-actions"><button className="auth-login-link" onClick={() => openLogin()}>登录</button><button className="auth-signup-button" onClick={onOpenRegister}>创建账户</button></div></nav>
    <section className="auth-hero-heading"><span>PERSONAL HEALTH, MADE VISIBLE</span><h1><b>每一次记录</b><b>都让身体更清晰</b></h1></section>
    <div className="auth-bottom-copy"><span>01 / HEALTH TRACE</span><p>体重、血压、心率与每一次运动，看似零散的数据，会在时间里汇聚成属于你的健康轨迹。</p></div><div className="auth-bottom-action"><p>移动光标，看看持续记录如何让状态从模糊走向清晰。你的改变，不必等到很久以后才被看见。</p><button onClick={() => openLogin()}>开始记录 <ArrowRight size={15} /></button></div>
    {loginOpen && <><div className="auth-panel-backdrop" onClick={() => setLoginOpen(false)} /><aside className="auth-login-panel" role="dialog" aria-modal="true" aria-labelledby="auth-login-title"><button className="auth-close-button" onClick={() => setLoginOpen(false)} aria-label="关闭登录"><X size={18} /></button><div className="auth-panel-heading"><span>WELCOME BACK</span><h2 id="auth-login-title">继续你的健康记录</h2><p>{intendedFeature ? `登录后即可进入${intendedFeature}` : '数据会持续保留，并只对你的账户可见。'}</p></div>{notice && <p className="auth-notice">{notice}</p>}<form className="auth-login-form" onSubmit={submit}><label>用户名<div className="auth-input-wrap"><UserRound size={15} /><input ref={inputRef} value={username} onChange={event => setUsername(event.target.value)} placeholder="请输入用户名" autoComplete="username" /></div></label><label>密码<div className="auth-input-wrap"><LockKeyhole size={15} /><input value={password} onChange={event => setPassword(event.target.value)} placeholder="请输入密码" type="password" autoComplete="current-password" /></div></label><button className="auth-submit-button" type="submit" disabled={loading}>{loading ? '正在进入…' : '进入健康工作台'}<ArrowRight size={16} /></button>{error && <p className="auth-error">{error}</p>}</form><div className="auth-panel-footer"><span>还没有账户？</span><button onClick={onOpenRegister}>免费创建账户</button></div><div className="auth-privacy-note"><LockKeyhole size={14} />健康数据采用账户隔离，仅用于个人趋势分析</div></aside></>}
  </main>
}
