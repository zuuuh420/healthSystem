import { BodyStatusCard } from './components/BodyStatusCard'
import { DeviceCard } from './components/DeviceCard'
import { FamilyDashboard } from './components/FamilyDashboard'
import { HealthAssistant } from './components/HealthAssistant'
import { HealthMonitoringPage } from './components/HealthMonitoringPage'
import { DataAnalysisPage } from './components/DataAnalysisPage'
import { HealthReportPage } from './components/HealthReportPage'
import { DeviceConnectionPage } from './components/DeviceConnectionPage'
import { WellnessModulePage, WellnessKind } from './components/WellnessModulePage'
import { LoginPage } from './components/LoginPage'
import { RegisterPage } from './components/RegisterPage'
import { FloatingAssistant } from './components/FloatingAssistant'
import { NotificationDialog } from './components/NotificationDialog'
import { ProfilePage } from './components/ProfilePage'
import { SettingsPage } from './components/SettingsPage'
import { Sidebar } from './components/Sidebar'
import { SuggestedActions } from './components/SuggestedActions'
import { TopHeader } from './components/TopHeader'
import { TrendChart } from './components/TrendChart'
import { useMemo, useState } from 'react'
import { useSimulatedVitals } from './hooks/useSimulatedVitals'
import { useFamilyMembers } from './hooks/useFamilyMembers'
import { useUserProfile } from './hooks/useUserProfile'
import { VitalMetric } from './types'

export default function App() {
  const [authenticated] = useState(() => Boolean(window.localStorage.getItem('token') || window.localStorage.getItem('accessToken')))
  const [authMode, setAuthMode] = useState<'login' | 'register'>('login')
  const [authNotice, setAuthNotice] = useState('')
  const [activeView, setActiveView] = useState('概览')
  const [assistantOpen, setAssistantOpen] = useState(false)
  const [notificationsOpen, setNotificationsOpen] = useState(false)
  const [processingRequestId, setProcessingRequestId] = useState<number | null>(null)
  const { vitals, applySyncUpdate } = useSimulatedVitals()
  const family = useFamilyMembers()
  const userProfile = useUserProfile()
  const openNotifications = () => setNotificationsOpen(true)
  const handleRequestDecision = async (id: number | string, decision?: 'ACCEPT' | 'REJECT', relationship = '') => {
    if (typeof id === 'string' || !decision) {
      family.dismissHealthAlert(String(id))
      return true
    }
    setProcessingRequestId(id)
    try {
      const succeeded = await family.decideRequest(id, decision, relationship)
      if (succeeded) setNotificationsOpen(false)
      return succeeded
    } finally { setProcessingRequestId(null) }
  }
  const logout = () => { window.localStorage.removeItem('token'); window.localStorage.removeItem('accessToken'); window.location.assign('/') }
  const metrics = useMemo<VitalMetric[]>(() => [
    { key: 'heartRate', label: '心率', value: String(vitals.heartRate), unit: 'bpm', icon: 'heart', tone: 'rose' },
    { key: 'oxygen', label: '血氧饱和度', value: String(vitals.oxygen), unit: '% SpO₂', icon: 'drop', tone: 'mint' },
    { key: 'temperature', label: '体温', value: vitals.temperature.toFixed(1), unit: '°C', icon: 'temp', tone: 'amber' },
    { key: 'sleep', label: '睡眠时长', value: vitals.sleep, unit: '昨夜', icon: 'moon', tone: 'lilac' }
  ], [vitals])
  if (!authenticated) return authMode === 'register'
    ? <RegisterPage onBackToLogin={() => { setAuthNotice(''); setAuthMode('login') }} onRegistered={() => { setAuthNotice('账户创建成功，请登录。'); setAuthMode('login') }} />
    : <LoginPage notice={authNotice} onOpenRegister={() => { setAuthNotice(''); setAuthMode('register') }} onLogin={token => { window.localStorage.setItem('token', token); window.location.reload() }} />
  return <div className="app-shell"><Sidebar active={activeView} onNavigate={setActiveView} /><main className="main-content"><TopHeader onOpenNotifications={openNotifications} onOpenProfile={() => setActiveView('个人资料')} onOpenSettings={() => setActiveView('设置中心')} onLogout={logout} notificationCount={family.pendingRequests.length + family.healthAlerts.length} profile={userProfile.profile} />{activeView === '家人健康' ? <div className="dashboard-content dashboard-content--family"><FamilyDashboard family={family} /></div> : activeView === '个人资料' ? <div className="dashboard-content">{userProfile.profile ? <ProfilePage profile={userProfile.profile} message={userProfile.message} onSave={userProfile.saveProfile} onAvatar={userProfile.saveAvatar} /> : <p>正在加载个人资料…</p>}</div> : activeView === '设置中心' ? <div className="dashboard-content"><SettingsPage onNavigate={setActiveView} onLogout={logout} /></div> : activeView === '健康监测' ? <div className="dashboard-content"><HealthMonitoringPage metrics={metrics} steps={vitals.steps} /></div> : activeView === '数据分析' ? <div className="dashboard-content"><DataAnalysisPage metrics={metrics} steps={vitals.steps} /></div> : activeView === '健康报告' ? <div className="dashboard-content"><HealthReportPage metrics={metrics} steps={vitals.steps} /></div> : activeView === '设备连接' ? <div className="dashboard-content"><DeviceConnectionPage vitals={vitals} onSyncSuccess={applySyncUpdate} /></div> : ['运动管理', '饮食管理', '睡眠管理', '压力管理', '健康计划', '帮助与反馈'].includes(activeView) ? <div className="dashboard-content"><WellnessModulePage kind={activeView as WellnessKind} vitals={vitals} /></div> : <div className="dashboard-content"><div className="hero-heading"><div><span className="eyebrow">7月13日 星期一</span><h1>下午好，{userProfile.profile?.nickname || '演示用户'}</h1><p>关注身体的信号，做出更好的健康决策。</p></div></div><div className="dashboard-main"><div className="dashboard-top-row"><BodyStatusCard metrics={metrics} steps={vitals.steps} /><DeviceCard heartRate={vitals.heartRate} onSyncSuccess={applySyncUpdate} /></div><div className="dashboard-bottom-row"><TrendChart /><SuggestedActions /></div><p className="disclaimer">* 数据来源于设备监测，仅供参考，不作为临床诊断依据。</p></div></div>}</main><FloatingAssistant open={assistantOpen} onOpen={() => setAssistantOpen(true)} onClose={() => setAssistantOpen(false)} /><NotificationDialog open={notificationsOpen} onClose={() => setNotificationsOpen(false)} requests={family.pendingRequests} healthAlerts={family.healthAlerts} onDismissHealthAlert={handleRequestDecision} onDecision={handleRequestDecision} processingId={processingRequestId} />{assistantOpen && <div className="assistant-backdrop" role="presentation" onMouseDown={event => { if (event.target === event.currentTarget) setAssistantOpen(false) }}><section className="assistant-dialog" role="dialog" aria-modal="true" aria-labelledby="assistant-dialog-title"><header className="assistant-dialog-heading"><div><span className="eyebrow">HEALTH ASSISTANT</span><h2 id="assistant-dialog-title">健康助手</h2></div><button className="dialog-close" onClick={() => setAssistantOpen(false)} aria-label="关闭健康助手">×</button></header><HealthAssistant family={family.members} userVitals={vitals} /></section></div>}</div>
}
