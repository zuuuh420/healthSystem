<template>
  <div class="calorie-stats">
    <h2 class="page-title">热量与营养统计</h2>

    <el-row :gutter="20" class="stat-cards">
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.todayCalories || 0 }}</div>
          <div class="stat-label">今日摄入(kcal)</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.weeklyAvgCalories || 0 }}</div>
          <div class="stat-label">近7天日均(kcal)</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.monthlyAvgCalories || 0 }}</div>
          <div class="stat-label">近30天日均(kcal)</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.todayProtein || 0 }}g</div>
          <div class="stat-label">今日蛋白质</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.todayFat || 0 }}g</div>
          <div class="stat-label">今日脂肪</div>
        </el-card>
      </el-col>
      <el-col :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ stats.todayCarbs || 0 }}g</div>
          <div class="stat-label">今日碳水</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <div slot="header"><span>今日营养素占比</span></div>
          <div ref="pieChart" style="height: 300px;"></div>
          <el-empty v-if="!hasNutrients" description="暂无数据" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <div slot="header"><span>近7天热量趋势</span></div>
          <div ref="barChart" style="height: 300px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getDietStats } from '@/api/dietRecord'

export default {
  name: 'CalorieStats',
  data() {
    return {
      stats: {},
      pieChart: null,
      barChart: null
    }
  },
  computed: {
    hasNutrients() {
      const s = this.stats
      return (s.todayProtein || s.todayFat || s.todayCarbs) && (s.todayProtein + s.todayFat + s.todayCarbs > 0)
    }
  },
  created() { this.loadData() },
  mounted() {
    this.$nextTick(() => {
      this.pieChart = echarts.init(this.$refs.pieChart)
      this.barChart = echarts.init(this.$refs.barChart)
      window.addEventListener('resize', this.handleResize)
    })
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    if (this.pieChart) this.pieChart.dispose()
    if (this.barChart) this.barChart.dispose()
  },
  methods: {
    async loadData() {
      try {
        const res = await getDietStats()
        if (res.code === 200) {
          this.stats = res.data
          this.$nextTick(() => { this.renderCharts() })
        }
      } catch (e) {}
    },
    renderCharts() {
      if (this.pieChart && this.hasNutrients) {
        this.pieChart.setOption({
          tooltip: { trigger: 'item' },
          series: [{
            type: 'pie', radius: ['40%', '70%'],
            data: [
              { value: this.stats.todayProtein || 0, name: '蛋白质', itemStyle: { color: '#409eff' } },
              { value: this.stats.todayFat || 0, name: '脂肪', itemStyle: { color: '#e6a23c' } },
              { value: this.stats.todayCarbs || 0, name: '碳水', itemStyle: { color: '#67c23a' } }
            ]
          }]
        })
      }
      if (this.barChart && this.stats.dailyCalories) {
        const data = this.stats.dailyCalories
        this.barChart.setOption({
          tooltip: { trigger: 'axis' },
          xAxis: { type: 'category', data: data.map(d => d.date) },
          yAxis: { type: 'value', name: 'kcal' },
          series: [{
            type: 'bar', data: data.map(d => d.calories),
            itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] }
          }]
        })
      }
    },
    handleResize() {
      if (this.pieChart) this.pieChart.resize()
      if (this.barChart) this.barChart.resize()
    }
  }
}
</script>

<style scoped>
.calorie-stats { padding: 20px; }
.page-title { margin-bottom: 20px; color: #303133; }
.stat-cards { margin-bottom: 20px; }
.stat-card { text-align: center; }
.stat-value { font-size: 24px; font-weight: bold; color: #409eff; }
.stat-label { font-size: 13px; color: #909399; margin-top: 6px; }
</style>
