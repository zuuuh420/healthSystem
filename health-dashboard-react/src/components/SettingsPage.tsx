import { Bell, KeyRound, LockKeyhole, LogOut, MonitorCog, ShieldCheck, UsersRound } from 'lucide-react'
import { FormEvent, useEffect, useState } from 'react'
import { changeUserPassword } from '../services/userProfile'

type Preferences = { relationRequests: boolean; offlineAlerts: boolean; assistantAlerts: boolean; reducedMotion: boolean }
const defaultPreferences: Preferences = { relationRequests: true, offlineAlerts: true, assistantAlerts: true, reducedMotion: false }

function loadPreferences(): Preferences {
  try { return { ...defaultPreferences, ...JSON.parse(window.localStorage.getItem('zhiheng-preferences') ?? '{}') } } catch { return defaultPreferences }
}

export function SettingsPage({ onNavigate, onLogout }: { onNavigate: (view: string) => void; onLogout: () => void }) {
  const [preferences, setPreferences] = useState<Preferences>(loadPreferences)
  const [password, setPassword] = useState({ oldPassword: '', newPassword: '', confirm: '' })
  const [passwordMessage, setPasswordMessage] = useState('')
  useEffect(() => { window.localStorage.setItem('zhiheng-preferences', JSON.stringify(preferences)); document.documentElement.classList.toggle('reduce-motion', preferences.reducedMotion) }, [preferences])
  const update = (key: keyof Preferences) => setPreferences(current => ({ ...current, [key]: !current[key] }))
  const submitPassword = async (event: FormEvent) => {
    event.preventDefault()
    if (password.newPassword.length < 6 || password.newPassword !== password.confirm) { setPasswordMessage('请确认新密码至少6位且两次输入一致。'); return }
    try { await changeUserPassword(password.oldPassword, password.newPassword); setPassword({ oldPassword: '', newPassword: '', confirm: '' }); setPasswordMessage('密码已更新。') } catch (error) { setPasswordMessage(error instanceof Error ? error.message : '密码更新失败。') }
  }
  return <section className="settings-page"><div className="settings-heading"><div><span className="eyebrow">CONTROL CENTER</span><h1>设置中心</h1><p>管理账户安全、通知偏好和设备体验。</p></div></div><div className="settings-grid"><article className="settings-section"><div className="settings-section-heading"><KeyRound size={18} /><div><h2>账户安全</h2><p>保护你的知衡账户</p></div></div><form className="password-form" onSubmit={submitPassword}><input type="password" value={password.oldPassword} onChange={event => setPassword(current => ({ ...current, oldPassword: event.target.value }))} placeholder="当前密码" aria-label="当前密码" /><input type="password" value={password.newPassword} onChange={event => setPassword(current => ({ ...current, newPassword: event.target.value }))} placeholder="新密码（至少6位）" aria-label="新密码" /><input type="password" value={password.confirm} onChange={event => setPassword(current => ({ ...current, confirm: event.target.value }))} placeholder="确认新密码" aria-label="确认新密码" /><button className="ghost-button" type="submit">修改密码</button>{passwordMessage && <span className="settings-message">{passwordMessage}</span>}</form><button className="settings-danger-button" onClick={onLogout}><LogOut size={15} />退出登录</button></article><article className="settings-section"><div className="settings-section-heading"><Bell size={18} /><div><h2>通知设置</h2><p>选择你想接收的提醒</p></div></div><SettingToggle label="家人关联申请" checked={preferences.relationRequests} onChange={() => update('relationRequests')} /><SettingToggle label="设备离线提醒" checked={preferences.offlineAlerts} onChange={() => update('offlineAlerts')} /><SettingToggle label="健康助手提醒" checked={preferences.assistantAlerts} onChange={() => update('assistantAlerts')} /></article><article className="settings-section"><div className="settings-section-heading"><ShieldCheck size={18} /><div><h2>隐私与授权</h2><p>管理家人数据的查看权限</p></div></div><button className="settings-link-row" onClick={() => onNavigate('家人健康')}><UsersRound size={16} /><span>家人健康授权</span><strong>管理关联关系</strong></button><div className="settings-info"><LockKeyhole size={15} />只有接受关联申请后，双方才可以查看彼此的设备状态。</div></article><article className="settings-section"><div className="settings-section-heading"><MonitorCog size={18} /><div><h2>设备与显示</h2><p>当前工作台体验</p></div></div><div className="settings-static-row"><span>设备数据</span><strong>模拟设备，仅供参考</strong></div><div className="settings-static-row"><span>同步频率</span><strong>每 5 秒</strong></div><SettingToggle label="减少动效" checked={preferences.reducedMotion} onChange={() => update('reducedMotion')} /></article></div></section>
}

function SettingToggle({ label, checked, onChange }: { label: string; checked: boolean; onChange: () => void }) {
  return <button className="settings-toggle-row" onClick={onChange}><span>{label}</span><i className={checked ? 'is-on' : ''}><b /></i></button>
}
