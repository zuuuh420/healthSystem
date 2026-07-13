import { Bell, ChevronDown, HelpCircle, Search } from 'lucide-react'
import { useState } from 'react'

export function TopHeader() {
  const [query, setQuery] = useState('')
  return <header className="top-header"><div className="page-context"><span>7月13日 周一</span><strong>首页仪表盘</strong></div><div className="header-tools"><div className="search-field"><Search size={16} /><input value={query} onChange={event => setQuery(event.target.value)} placeholder="搜索功能或记录" /></div><button className="header-icon" title="消息通知"><Bell size={17} /></button><button className="header-icon" title="帮助中心"><HelpCircle size={17} /></button><button className="account-button"><span className="avatar">演</span><span><strong>演示用户</strong><small>个人账户</small></span><ChevronDown size={15} /></button></div></header>
}
