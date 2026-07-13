<template>
  <section class="dashboard" v-loading="loading">
    <header class="welcome">
      <div>
        <span class="date-note">{{ fullDate }}</span>
        <h1>{{ greeting }}，{{ displayName }}</h1>
        <p>手环正在把今天的身体信号，整理成可以行动的建议。</p>
      </div>
      <div class="welcome-actions">
        <el-select v-model="activeProfileId" size="small" class="profile-switch" popper-class="profile-popper">
          <el-option v-for="profile in profiles" :key="profile.id" :label="`${profile.name} · ${profile.worn ? '已佩戴' : '未佩戴'}`" :value="profile.id" />
        </el-select>
        <el-button class="sync-button" icon="el-icon-refresh" :loading="syncing" @click="syncDevice">同步设备</el-button>
      </div>
    </header>

    <section class="device-hero" :class="{ 'is-off': !device.worn }">
      <div class="device-copy">
        <div class="eyebrow-row"><span class="eyebrow">CONNECTED DEVICE</span><span class="connection-pill"><i :class="device.worn ? 'el-icon-success' : 'el-icon-warning-outline'" />{{ device.worn ? '佩戴中' : '暂未佩戴' }}</span></div>
        <h2>{{ device.device }}</h2>
        <p v-if="device.worn">正在采集心率、血氧、睡眠、体温和运动状态。最新数据已同步到今天的健康轨迹。</p>
        <p v-else>手环暂时没有检测到佩戴状态。重新戴上手环后，数据会自动回到这条健康轨迹里。</p>
        <div class="device-meta"><span><i class="el-icon-time" />{{ device.lastSync }}</span><span><i class="el-icon-battery" />电量 {{ device.battery }}%</span><button type="button" @click="showDeviceInfo">设备详情 <i class="el-icon-right" /></button></div>
      </div>
      <div class="device-visual"><div class="visual-glow" /><img src="@/assets/wristband-product.png" alt="知衡健康智能手环" /></div>
    </section>

    <section class="signal-section">
      <header class="section-heading"><div><span class="eyebrow">LIVE SIGNALS</span><h2>今日身体信号</h2></div><span class="section-hint">{{ device.worn ? '来自手环的自动采集' : '佩戴后自动出现' }}</span></header>
      <div class="signal-grid">
        <article v-for="signal in signals" :key="signal.key" class="signal-card" :class="{ muted: !device.worn }">
          <div class="signal-top"><span class="signal-icon" :class="signal.tone"><i :class="signal.icon" /></span><span>{{ signal.label }}</span></div>
          <strong>{{ signal.value }}</strong><small>{{ signal.unit }}</small><div v-if="device.worn" class="signal-spark" :class="signal.tone"><i /><i /><i /><i /><i /></div>
        </article>
      </div>
    </section>

    <div class="dashboard-grid">
      <section class="activity-section"><header class="section-heading"><div><span class="eyebrow">AUTO DETECTED</span><h2>自动活动轨迹</h2></div><router-link to="/sport/records">全部活动 <i class="el-icon-right" /></router-link></header><div v-if="device.worn && device.activities.length" class="activity-list"><div v-for="activity in device.activities" :key="activity.time + activity.title" class="activity-row"><time>{{ activity.time }}</time><span class="activity-icon"><i :class="activity.icon" /></span><div><strong>{{ activity.title }}</strong><p>{{ activity.detail }}</p></div><i class="el-icon-check activity-check" /></div></div><div v-else class="empty-strip"><i class="el-icon-watch-1" /><div><strong>等待手环产生轨迹</strong><p>佩戴后会自动识别步行、跑步、骑行与久坐状态。</p></div></div></section>

      <section class="recovery-section"><header class="section-heading"><div><span class="eyebrow">RECOVERY</span><h2>恢复状态</h2></div><router-link to="/report">看周报 <i class="el-icon-right" /></router-link></header><div class="recovery-score"><strong>{{ device.worn ? recoveryScore : '--' }}</strong><span>{{ device.worn ? '恢复指数' : '等待数据' }}</span></div><p>{{ recoveryText }}</p><div class="recovery-bar"><i :style="{ width: device.worn ? recoveryScore + '%' : '8%' }" /></div><div class="recovery-tags"><span><i class="el-icon-moon" />睡眠 {{ device.metrics.sleep }}</span><span><i class="el-icon-data-analysis" />压力 {{ device.metrics.pressure }}</span></div></section>

      <section class="nutrition-section"><header class="section-heading"><div><span class="eyebrow">DAILY FUEL</span><h2>今日补给建议</h2></div><router-link to="/diet">记录饮食 <i class="el-icon-right" /></router-link></header><div class="nutrition-main"><span class="nutrition-orb"><i class="el-icon-food" /></span><div><strong>{{ nutritionTitle }}</strong><p>{{ nutritionAdvice }}</p></div></div><div class="nutrition-foot"><span>依据 {{ device.worn ? '步数、睡眠与心率' : '最近一次记录' }}</span><button type="button" @click="$router.push('/diet')">查看详情 <i class="el-icon-right" /></button></div></section>

      <section class="weekly-section"><header class="section-heading"><div><span class="eyebrow">WEEKLY RHYTHM</span><h2>本周节奏</h2></div><router-link to="/analysis">完整分析 <i class="el-icon-right" /></router-link></header><div class="weekly-stats"><div><strong>{{ overview ? overview.weeklyMinutes : 0 }}</strong><span>运动分钟</span></div><div><strong>{{ device.metrics.steps || '--' }}</strong><span>今日步数</span></div><div><strong>{{ overview ? overview.currentStreak : 0 }}</strong><span>连续打卡</span></div></div><div class="weekly-progress"><i :style="{ width: progressRate + '%' }" /></div><p>{{ weeklyText }}</p></section>
    </div>
  </section>
</template>

<script>
import { getDashboardOverview } from '@/api/dashboard'
import { getSportRecords } from '@/api/sport'
import { wearableProfiles, findWearableProfile } from '@/mock/wearable'

export default {
  name: 'Dashboard',
  data: () => ({ loading: false, syncing: false, overview: null, recentRecords: [], profiles: wearableProfiles, activeProfileId: 'demo2026' }),
  computed: {
    activeUser() { return this.$store.state.userInfo || {} },
    displayName() { return this.device.name || this.activeUser.nickname || this.activeUser.username || '朋友' },
    device() { return findWearableProfile(this.activeProfileId) },
    greeting() { const hour = new Date().getHours(); return hour < 11 ? '早上好' : hour < 18 ? '下午好' : '晚上好' },
    fullDate() { return new Intl.DateTimeFormat('zh-CN', { month: 'long', day: 'numeric', weekday: 'long' }).format(new Date()) },
    signals() {
      const m = this.device.metrics
      return [
        { key: 'heart', label: '心率', value: m.heartRate || '--', unit: m.heartRate ? 'bpm' : '未检测', icon: 'el-icon-heart', tone: 'rose' },
        { key: 'oxygen', label: '血氧饱和度', value: m.oxygen ? `${m.oxygen}%` : '--', unit: m.oxygen ? 'SpO₂' : '未检测', icon: 'el-icon-odometer', tone: 'mint' },
        { key: 'temp', label: '体温趋势', value: m.temperature || '--', unit: m.temperature ? '°C' : '未检测', icon: 'el-icon-sunny', tone: 'amber' },
        { key: 'sleep', label: '睡眠时长', value: m.sleep, unit: m.sleep !== '--' ? '昨夜' : '未检测', icon: 'el-icon-moon', tone: 'lilac' },
        { key: 'pressure', label: '血压趋势', value: m.pressure, unit: m.pressure !== '--' ? 'mmHg' : '未检测', icon: 'el-icon-data-analysis', tone: 'blue' },
        { key: 'bodyFat', label: '体脂率', value: m.bodyFat ? `${m.bodyFat}%` : '--', unit: m.bodyFat ? 'BF' : '未检测', icon: 'el-icon-user', tone: 'leaf' }
      ]
    },
    progressRate() { return Math.min(100, Math.round((((this.overview && this.overview.weeklyMinutes) || 0) / 150) * 100)) },
    recoveryScore() { return Math.min(98, Math.round((this.device.metrics.heartRate ? 82 : 0) + (this.device.metrics.sleep === '8小时06分' ? 12 : 5))) },
    recoveryText() { if (!this.device.worn) return '手环重新佩戴后，这里会根据睡眠和心率变化更新。'; return this.recoveryScore > 85 ? '昨夜恢复充分，今天适合保持轻到中等强度活动。' : '今天建议降低强度，优先补足睡眠和水分。' },
    nutritionTitle() { if (!this.device.worn) return '先让数据回来'; return this.device.metrics.steps > 9000 ? '今天需要补充能量' : '保持轻盈补给' },
    nutritionAdvice() { if (!this.device.worn) return '佩戴手环后，会结合活动量与睡眠给出更贴近今天的建议。'; return this.device.metrics.steps > 9000 ? '运动量偏高，晚餐可优先选择蛋白质、粗粮和足量饮水。' : '当前活动量平稳，优先保证早餐蛋白质与全天蔬菜摄入。' },
    weeklyText() { const minutes = (this.overview && this.overview.weeklyMinutes) || 0; return minutes >= 150 ? '本周基础运动量已完成，留出恢复时间。' : `距离本周建议运动量还差${150 - minutes}分钟，手环会自动记录下一次活动。` }
  },
  created() { const user = this.$store.state.userInfo || {}; if (user.username && this.profiles.some(item => item.id === user.username)) this.activeProfileId = user.username; this.loadData() },
  methods: {
    async loadData() { this.loading = true; try { const overviewRes = await getDashboardOverview(); this.overview = overviewRes.data; try { const recordsRes = await getSportRecords({ pageNum: 1, pageSize: 4 }); this.recentRecords = (recordsRes.data && recordsRes.data.records) || [] } catch (error) { this.recentRecords = [] } } finally { this.loading = false } },
    async syncDevice() { this.syncing = true; await new Promise(resolve => setTimeout(resolve, 650)); this.syncing = false; this.$message.success(this.device.worn ? '设备数据已同步' : '未检测到佩戴，请重新佩戴手环') },
    showDeviceInfo() { this.$alert('这是演示设备状态。真实接入时，可在这里绑定蓝牙手环、查看固件和同步记录。', '设备详情', { confirmButtonText: '知道了' }) }
  }
}
</script>

<style scoped>
.dashboard{max-width:1280px;margin:0 auto;color:#25342b}.welcome{display:flex;align-items:flex-end;justify-content:space-between;padding:8px 2px 28px}.date-note,.eyebrow{display:block;color:#6d8871;font-size:11px;font-weight:700;letter-spacing:1.6px}.welcome h1{margin:8px 0 7px;font-size:32px;font-weight:600}.welcome p{color:#68756c;font-size:14px}.welcome-actions{display:flex;align-items:center;gap:10px}.profile-switch{width:170px}.sync-button{height:38px;border:0;border-radius:20px;background:#3f5f4b;color:#fff}.device-hero{display:grid;grid-template-columns:1.2fr .8fr;min-height:270px;overflow:hidden;position:relative;border-radius:28px;background:linear-gradient(115deg,#0b3429,#3f5f4b);box-shadow:0 18px 46px rgba(63,95,75,.17);color:#fff}.device-hero.is-off{background:linear-gradient(115deg,#40524a,#718178)}.device-copy{position:relative;z-index:2;padding:38px 46px}.eyebrow-row{display:flex;align-items:center;gap:12px}.device-copy .eyebrow{color:#c8e1c3}.connection-pill{padding:6px 10px;border-radius:15px;background:rgba(220,235,221,.16);color:#e3f2df;font-size:12px}.connection-pill i{margin-right:5px;color:#a8c66c}.device-copy h2{margin:18px 0 10px;font-size:28px;font-weight:600}.device-copy>p{max-width:530px;color:#d5e6d5;line-height:1.7;font-size:14px}.device-meta{display:flex;gap:24px;align-items:center;margin-top:28px;color:#c7ddc6;font-size:12px}.device-meta i{margin-right:5px}.device-meta button{border:0;background:transparent;color:#e8f5e6;cursor:pointer}.device-visual{position:relative;display:flex;align-items:center;justify-content:center}.device-visual .visual-glow{position:absolute;width:230px;height:230px;border-radius:50%;background:rgba(168,198,108,.18);filter:blur(4px)}.device-visual img{position:relative;width:min(290px,82%);transform:rotate(-9deg);border-radius:24px;mix-blend-mode:screen;opacity:.98}.signal-section{margin-top:34px}.section-heading{display:flex;align-items:flex-end;justify-content:space-between;margin-bottom:16px}.section-heading h2{margin-top:6px;font-size:21px;font-weight:600;color:#344c3d}.section-heading a{color:#6d8871;font-size:13px;text-decoration:none}.section-hint{color:#94a098;font-size:12px}.signal-grid{display:grid;grid-template-columns:repeat(6,1fr);gap:12px}.signal-card{min-height:146px;padding:17px 16px;border-radius:20px;background:#fcfbf7;box-shadow:0 10px 28px rgba(63,95,75,.06);transition:transform .2s,box-shadow .2s}.signal-card:hover{transform:translateY(-3px);box-shadow:0 14px 32px rgba(63,95,75,.12)}.signal-card.muted{background:#f0f3ef;box-shadow:none}.signal-top{display:flex;align-items:center;gap:8px;color:#68756c;font-size:12px}.signal-icon{width:30px;height:30px;display:grid;place-items:center;border-radius:11px}.signal-icon.rose,.signal-spark.rose i{color:#b7686f;background:#f6e4e4}.signal-icon.mint,.signal-spark.mint i{color:#4b8b73;background:#def0e6}.signal-icon.amber,.signal-spark.amber i{color:#a37a4c;background:#f5ead4}.signal-icon.lilac,.signal-spark.lilac i{color:#766f99;background:#eae7f4}.signal-icon.blue,.signal-spark.blue i{color:#5d7fa0;background:#e4edf5}.signal-icon.leaf,.signal-spark.leaf i{color:#557a46;background:#e0eedc}.signal-card strong{display:inline-block;margin-top:18px;color:#344c3d;font-size:26px;font-weight:500}.signal-card small{margin-left:4px;color:#94a098;font-size:11px}.signal-spark{display:flex;align-items:flex-end;gap:3px;height:24px;margin-top:9px}.signal-spark i{display:block;width:7px;border-radius:5px}.signal-spark i:nth-child(1){height:9px}.signal-spark i:nth-child(2){height:15px}.signal-spark i:nth-child(3){height:11px}.signal-spark i:nth-child(4){height:19px}.signal-spark i:nth-child(5){height:14px}.dashboard-grid{display:grid;grid-template-columns:1.2fr .8fr;gap:28px 34px;margin-top:38px}.activity-section,.recovery-section,.nutrition-section,.weekly-section{min-width:0}.activity-list{border-top:1px solid #dfe7df}.activity-row{display:grid;grid-template-columns:48px 34px 1fr 20px;align-items:center;gap:12px;padding:16px 0;border-bottom:1px solid #dfe7df}.activity-row time{color:#94a098;font-size:12px}.activity-icon{width:32px;height:32px;display:grid;place-items:center;border-radius:50%;background:#dcebdd;color:#557a46}.activity-row strong{color:#344c3d;font-size:14px}.activity-row p{margin-top:4px;color:#7e8b82;font-size:12px}.activity-check{color:#6d8871}.empty-strip{display:flex;align-items:center;gap:14px;padding:28px 0;border-top:1px solid #dfe7df;border-bottom:1px solid #dfe7df}.empty-strip>i{font-size:27px;color:#6d8871}.empty-strip strong{color:#344c3d}.empty-strip p{margin-top:5px;color:#7e8b82;font-size:12px}.recovery-section,.nutrition-section,.weekly-section{padding:26px 28px;border-radius:24px;background:#ecf4ea}.recovery-section .section-heading,.nutrition-section .section-heading,.weekly-section .section-heading{margin-bottom:10px}.recovery-score{display:flex;align-items:baseline;gap:8px}.recovery-score strong{font-size:55px;font-weight:500;color:#3f5f4b}.recovery-score span{color:#7e8b82;font-size:12px}.recovery-section>p{min-height:38px;color:#68756c;font-size:13px;line-height:1.6}.recovery-bar,.weekly-progress{height:7px;margin:15px 0;border-radius:6px;background:#d1e2d0}.recovery-bar i,.weekly-progress i{display:block;height:100%;border-radius:6px;background:#6d8871;transition:width .5s}.recovery-tags{display:flex;justify-content:space-between;color:#7e8b82;font-size:12px}.recovery-tags i{margin-right:4px;color:#557a46}.nutrition-section{background:#fff9f0}.nutrition-main{display:flex;align-items:center;gap:16px;min-height:92px}.nutrition-orb{width:52px;height:52px;display:grid;place-items:center;border-radius:18px;background:#f4e1c9;color:#a66e45;font-size:22px}.nutrition-main strong{color:#694b37;font-size:16px}.nutrition-main p{margin-top:7px;color:#856f61;font-size:12px;line-height:1.55}.nutrition-foot{display:flex;justify-content:space-between;align-items:center;padding-top:15px;border-top:1px solid #f0dfcb;color:#a0836c;font-size:11px}.nutrition-foot button{border:0;background:transparent;color:#a66e45;font-size:12px;cursor:pointer}.weekly-section{background:#f7faf5}.weekly-stats{display:grid;grid-template-columns:repeat(3,1fr);gap:14px}.weekly-stats div+div{border-left:1px solid #dce7d9;padding-left:14px}.weekly-stats strong,.weekly-stats span{display:block}.weekly-stats strong{color:#3f5f4b;font-size:26px;font-weight:500}.weekly-stats span{margin-top:6px;color:#7e8b82;font-size:11px}.weekly-section p{color:#7e8b82;font-size:12px}.profile-popper .el-select-dropdown__item{font-size:12px}@media(max-width:1050px){.signal-grid{grid-template-columns:repeat(3,1fr)}.device-hero{grid-template-columns:1fr .75fr}}@media(max-width:760px){.welcome{display:block}.welcome-actions{margin-top:18px;flex-wrap:wrap}.profile-switch{width:calc(100% - 120px)}.device-hero{display:block}.device-copy{padding:30px 26px 12px}.device-visual{height:180px}.device-visual img{width:220px}.signal-grid{grid-template-columns:1fr 1fr}.dashboard-grid{grid-template-columns:1fr}.device-meta{gap:12px;flex-wrap:wrap}.device-copy h2{font-size:23px}}@media(max-width:470px){.signal-grid{grid-template-columns:1fr}.profile-switch{width:100%}.sync-button{width:100%}.weekly-stats{gap:8px}}
</style>
