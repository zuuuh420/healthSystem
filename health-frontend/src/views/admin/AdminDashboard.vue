<template>
  <div class="admin-dashboard">
    <h2 class="page-title">系统概览</h2>

    <el-row :gutter="20" class="stat-cards">
      <el-col :span="4" v-for="item in statItems" :key="item.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ item.value }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <div slot="header"><span>系统数据分布</span></div>
      <div ref="chart" style="height: 350px;"></div>
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getAdminStats } from '@/api/admin'

export default {
  name: 'AdminDashboard',
  data() {
    return {
      statItems: [
        { label: '用户总数', value: 0 },
        { label: '健康记录', value: 0 },
        { label: '饮食记录', value: 0 },
        { label: '运动记录', value: 0 },
        { label: '食物库条目', value: 0 }
      ],
      chart: null
    }
  },
  created() { this.loadData() },
  mounted() {
    this.$nextTick(() => {
      this.chart = echarts.init(this.$refs.chart)
      window.addEventListener('resize', this.handleResize)
    })
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    if (this.chart) this.chart.dispose()
  },
  methods: {
    async loadData() {
      try {
        const res = await getAdminStats()
        if (res.code === 200) {
          const d = res.data
          this.statItems[0].value = d.totalUsers
          this.statItems[1].value = d.totalHealthRecords
          this.statItems[2].value = d.totalDietRecords
          this.statItems[3].value = d.totalSportRecords
          this.statItems[4].value = d.totalFoods
          this.$nextTick(() => { this.renderChart() })
        }
      } catch (e) {}
    },
    renderChart() {
      if (!this.chart) return
      this.chart.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { type: 'category', data: this.statItems.map(s => s.label) },
        yAxis: { type: 'value' },
        series: [{
          type: 'bar', data: this.statItems.map(s => s.value),
          itemStyle: { color: '#409eff', borderRadius: [6, 6, 0, 0] },
          barWidth: '40%'
        }]
      })
    },
    handleResize() {
      if (this.chart) this.chart.resize()
    }
  }
}
</script>

<style scoped>
.admin-dashboard { padding: 20px; }
.page-title { margin-bottom: 20px; color: #303133; }
.stat-cards { margin-bottom: 20px; }
.stat-card { text-align: center; }
.stat-value { font-size: 28px; font-weight: bold; color: #409eff; }
.stat-label { font-size: 14px; color: #909399; margin-top: 8px; }
</style>
