import { Camera, Check, Copy, Mail, Phone, ShieldCheck, UserRound } from 'lucide-react'
import { ChangeEvent, FormEvent, useState } from 'react'
import { UserProfile, UserProfileUpdate } from '../types'

function initials(profile: UserProfile) {
  return (profile.nickname || profile.username || '知').slice(0, 1)
}

export function ProfilePage({ profile, message, onSave, onAvatar }: { profile: UserProfile; message: string; onSave: (update: UserProfileUpdate) => Promise<boolean>; onAvatar: (file: File) => Promise<boolean> }) {
  const [form, setForm] = useState<UserProfileUpdate>({ nickname: profile.nickname, email: profile.email, phone: profile.phone })
  const [saving, setSaving] = useState(false)
  const submit = async (event: FormEvent) => {
    event.preventDefault()
    setSaving(true)
    await onSave(form)
    setSaving(false)
  }
  const upload = async (event: ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (!file) return
    if (!['image/png', 'image/jpeg', 'image/webp'].includes(file.type) || file.size > 2 * 1024 * 1024) return
    await onAvatar(file)
    event.target.value = ''
  }
  const copyCode = async () => { await navigator.clipboard?.writeText(profile.inviteCode) }
  return <section className="profile-page">
    <div className="profile-heading"><div><span className="eyebrow">PERSONAL PROFILE</span><h1>个人资料</h1><p>管理你的账户信息和家人关联身份。</p></div><span className="profile-safe-state"><ShieldCheck size={15} />资料仅对本人可见</span></div>
    <div className="profile-layout">
      <article className="profile-identity-card"><div className="profile-avatar-wrap">{profile.avatar ? <img src={profile.avatar} alt="个人头像" /> : <span>{initials(profile)}</span>}<label className="profile-avatar-edit" title="上传头像"><Camera size={15} /><input type="file" accept="image/png,image/jpeg,image/webp" onChange={upload} /></label></div><h2>{profile.nickname || profile.username}</h2><p>@{profile.username}</p><div className="profile-id-row"><span>知衡 ID</span><strong>ZH{String(profile.id).padStart(10, '0')}</strong></div></article>
      <form className="profile-form-card" onSubmit={submit}><div className="profile-section-heading"><div><span className="eyebrow">ACCOUNT DETAILS</span><h2>账户信息</h2></div><UserRound size={20} /></div><label>昵称<input value={form.nickname} onChange={event => setForm(current => ({ ...current, nickname: event.target.value }))} maxLength={32} /></label><label>用户名<input value={profile.username} readOnly /></label><label><span><Mail size={14} />邮箱</span><input value={form.email} onChange={event => setForm(current => ({ ...current, email: event.target.value }))} /></label><label><span><Phone size={14} />手机号</span><input value={form.phone} onChange={event => setForm(current => ({ ...current, phone: event.target.value }))} /></label><button className="primary-button profile-save-button" type="submit" disabled={saving}><Check size={16} />{saving ? '保存中…' : '保存资料'}</button>{message && <p className="profile-form-message">{message}</p>}</form>
    </div>
    <article className="profile-code-card"><div><span className="eyebrow">FAMILY INVITE CODE</span><h2>我的家人关联码</h2><p>家人使用它向你发起关联申请，接受后双方才会共享健康状态。</p></div><button onClick={() => void copyCode()} title="复制关联码"><strong>{profile.inviteCode}</strong><Copy size={15} /></button></article>
  </section>
}
