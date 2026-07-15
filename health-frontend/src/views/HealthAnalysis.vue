<template>
  <section class="analysis-page" v-loading="loading">
    <header class="page-heading">
      <div><span class="eyebrow">HEALTH INSIGHT</span><h1>健康分析</h1></div>
      <el-button icon="el-icon-refresh" @click="loadOverview">刷新数据</el-button>
    </header>
    <div class="analysis-grid" v-if="overview">
      <article class="score-panel">
        <el-progress type="dashboard" :percentage="overview.healthScore" :width="190" :stroke-width="12" :color="scoreColors" />
        <h2>{{ overview.scoreLevel }}</h2><p>{{ overview.summary }}</p>
        <small>依据近7天运动、目标与打卡数据计算，不作为医疗诊断。</small>
      </article>
      <article class="detail-panel">
        <h2>本周构成</h2>
        <div class="metric-row"><span>运动完成度</span><strong>{{ sportRate }}%</strong></div>
        <el-progress :percentage="sportRate" :show-text="false" color="#27a376" />
        <div class="metric-row"><span>连续习惯</span><strong>{{ overview.currentStreak }}天</strong></div>
        <el-progress :percentage="habitRate" :show-text="false" color="#ef8354" />
        <div class="metric-row"><span>目标管理</span><strong>{{ overview.completedGoals }}/{{ overview.activeGoals + overview.completedGoals }}</strong></div>
        <el-progress :percentage="goalRate" :show-text="false" color="#5470c6" />
      </article>
      <article class="suggestion-panel">
        <div class="section-title"><div><span class="eyebrow">NEXT STEP</span><h2>下一步建议</h2></div></div>
        <div v-for="(item, index) in overview.suggestions" :key="item" class="suggestion-item">
          <span>{{ String(index + 1).padStart(2, '0') }}</span><p>{{ cleanText(item) }}</p>
        </div>
      </article>
    </div>
  </section>
</template>

<script>
import { getDashboardOverview } from '@/api/dashboard'
export default {
  name: 'HealthAnalysis',
  data: () => ({ loading: false, overview: null, scoreColors: [{ color: '#ef8354', percentage: 59 }, { color: '#e0a53b', percentage: 79 }, { color: '#27a376', percentage: 100 }] }),
  computed: {
    sportRate() { return Math.min(100, Math.round((this.overview.weeklyMinutes || 0) / 150 * 100)) },
    habitRate() { return Math.min(100, Math.round((this.overview.currentStreak || 0) / 7 * 100)) },
    goalRate() { const total = this.overview.activeGoals + this.overview.completedGoals; return total ? Math.round(this.overview.completedGoals / total * 100) : 0 }
  },
  created() { this.loadOverview() },
  methods: {
    async loadOverview() { this.loading = true; try { const res = await getDashboardOverview(); this.overview = res.data } finally { this.loading = false } },
    cleanText(value) { return (value || '').replace(/‎/g, '') }
  }
}
</script>

<style scoped>
.analysis-page{max-width:1180px;margin:0 auto}.page-heading{display:flex;justify-content:space-between;align-items:flex-end;margin-bottom:24px}.page-heading h1{font-size:30px;color:#17231e;margin-top:4px}.eyebrow{font-size:11px;font-weight:700;color:#27a376;letter-spacing:1.5px}.analysis-grid{display:grid;grid-template-columns:360px 1fr;gap:16px}.score-panel,.detail-panel,.suggestion-panel{background:#fff;border:1px solid #e5ebe8;border-radius:8px;padding:28px}.score-panel{text-align:center}.score-panel h2{font-size:22px;margin:10px 0}.score-panel p{line-height:1.7;color:#56635d}.score-panel small{display:block;margin-top:24px;color:#99a39e}.detail-panel h2,.suggestion-panel h2{font-size:19px;margin-bottom:26px}.metric-row{display:flex;justify-content:space-between;margin:22px 0 10px;color:#53615b}.suggestion-panel{grid-column:1/-1}.suggestion-item{display:grid;grid-template-columns:38px 1fr;gap:12px;padding:16px 0;border-top:1px solid #edf1ef}.suggestion-item span{font-weight:700;color:#27a376}.suggestion-item p{color:#43504a;line-height:1.6}@media(max-width:760px){.analysis-grid{grid-template-columns:1fr}.suggestion-panel{grid-column:auto}.page-heading{align-items:flex-start}.page-heading h1{font-size:25px}}
</style>
