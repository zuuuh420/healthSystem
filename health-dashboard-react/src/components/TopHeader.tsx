import { Bell, ChevronDown, HelpCircle, LogOut, Search, Settings, UserRound } from 'lucide-react'
import { useEffect, useRef, useState } from 'react'
import { UserProfile } from '../types'

export function TopHeader({ onOpenNotifications, onOpenProfile, onOpenSettings, onLogout, notificationCount = 0, profile }: { onOpenNotifications: () => void; onOpenProfile: () => void; onOpenSettings: () => void; onLogout: () => void; notificationCount?: number; profile?: UserProfile | null }) {
  const [query, setQuery] = useState('')
  const [menuOpen, setMenuOpen] = useState(false)
  const menuRef = useRef<HTMLDivElement>(null)
  const nickname = profile?.nickname || '演示用户'
  useEffect(() => {
    if (!menuOpen) return
    const close = (event: MouseEvent) => { if (!menuRef.current?.contains(event.target as Node)) setMenuOpen(false) }
    const escape = (event: KeyboardEvent) => { if (event.key === 'Escape') setMenuOpen(false) }
    document.addEventListener('mousedown', close)
    document.addEventListener('keydown', escape)
    return () => { document.removeEventListener('mousedown', close); document.removeEventListener('keydown', escape) }
  }, [menuOpen])
  const go = (action: () => void) => { setMenuOpen(false); action() }
  return <header className="top-header"><div className="header-tools"><div className="search-field"><Search size={16} /><input value={query} onChange={event => setQuery(event.target.value)} placeholder="搜索功能或记录" /></div><button className="header-action notification-trigger" title="消息通知" onClick={onOpenNotifications}><Bell size={17} /><span>消息通知</span>{notificationCount > 0 && <i aria-label={`${notificationCount}条未读消息`}>{notificationCount}</i>}</button><button className="header-action" title="帮助中心"><HelpCircle size={17} /><span>帮助中心</span></button><div className="account-menu-wrap" ref={menuRef}><button className="account-button" onClick={() => setMenuOpen(value => !value)} title="打开账户菜单" aria-expanded={menuOpen}>{profile?.avatar ? <img className="avatar avatar-image" src={profile.avatar} alt="个人头像" /> : <span className="avatar">{nickname.slice(0, 1)}</span>}<span><strong>{nickname}</strong><small>个人账户</small></span><ChevronDown className={menuOpen ? 'is-open' : ''} size={15} /></button>{menuOpen && <div className="account-menu" role="menu"><div className="account-menu-heading"><span className="avatar avatar--small">{nickname.slice(0, 1)}</span><div><strong>{nickname}</strong><small>{profile?.username || 'demo_user'}</small></div></div><button role="menuitem" onClick={() => go(onOpenProfile)}><UserRound size={16} /><span>个人资料</span></button><button role="menuitem" onClick={() => go(onOpenSettings)}><Settings size={16} /><span>设置中心</span></button><div className="account-menu-divider" /><button className="account-menu-danger" role="menuitem" onClick={() => go(onLogout)}><LogOut size={16} /><span>退出登录</span></button></div>}</div></div></header>
}
