<template>
  <div class="dashboard">
    <h2 class="page-title">首页仪表盘</h2>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #409EFF, #66b1ff)">
            <i class="el-icon-basketball"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ weeklyStats.totalMinutes || 0 }}</div>
            <div class="stat-label">本周运动(分钟)</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #F56C6C, #f89898)">
            <i class="el-icon-fire"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ weeklyStats.totalCalories || 0 }}</div>
            <div class="stat-label">消耗卡路里</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #67C23A, #85ce61)">
            <i class="el-icon-check"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ streakData.currentStreak || 0 }}</div>
            <div class="stat-label">连续打卡(天)</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" style="background: linear-gradient(135deg, #E6A23C, #ebb563)">
            <i class="el-icon-aim"></i>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ activeGoals }}</div>
            <div class="stat-label">进行中目标</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>近7天运动趋势</span>
          </template>
          <div ref="trendChart" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>今日运动</span>
          </template>
          <div v-if="todayRecords.length > 0" class="today-list">
            <div v-for="record in todayRecords" :key="record.id" class="today-item">
              <span class="sport-name">{{ record.sportTypeName }}</span>
              <span class="sport-duration">{{ record.durationMinutes }}分钟</span>
            </div>
          </div>
          <el-empty v-else description="今日暂无运动记录" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getWeeklyStats, getSportTrend, getTodayRecords } from '@/api/sport'
import { getCheckinStreak } from '@/api/goal'
import { getHealthGoals } from '@/api/goal'

export default {
  name: 'Dashboard',
  data() {
    return {
      weeklyStats: {},
      streakData: {},
      activeGoals: 0,
      todayRecords: [],
      trendChart: null
    }
  },
  created() {
    this.loadData()
  },
  mounted() {
    this.initChart()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    if (this.trendChart) {
      this.trendChart.dispose()
    }
  },
  methods: {
    async loadData() {
      try {
        await Promise.all([
          this.loadWeeklyStats(),
          this.loadStreak(),
          this.loadActiveGoals(),
          this.loadTodayRecords(),
          this.loadTrend()
        ])
      } catch (error) {
        console.error('加载数据失败', error)
      }
    },
    async loadWeeklyStats() {
      const res = await getWeeklyStats()
      if (res.code === 200) {
        this.weeklyStats = res.data
      }
    },
    async loadStreak() {
      const res = await getCheckinStreak()
      if (res.code === 200) {
        this.streakData = res.data
      }
    },
    async loadActiveGoals() {
      const res = await getHealthGoals({ status: 0, pageSize: 1 })
      if (res.code === 200) {
        this.activeGoals = res.data.total
      }
    },
    async loadTodayRecords() {
      const res = await getTodayRecords()
      if (res.code === 200) {
        this.todayRecords = res.data
      }
    },
    async loadTrend() {
      const res = await getSportTrend(7)
      if (res.code === 200) {
        this.updateChart(res.data.dailyStats || [])
      }
    },
    initChart() {
      this.trendChart = echarts.init(this.$refs.trendChart)
    },
    updateChart(data) {
      if (!this.trendChart) return

      const option = {
        tooltip: { trigger: 'axis' },
        xAxis: {
          type: 'category',
          data: data.map(item => item.date.slice(5))
        },
        yAxis: { type: 'value', name: '分钟' },
        series: [{
          data: data.map(item => item.minutes),
          type: 'line',
          smooth: true,
          areaStyle: { opacity: 0.3 },
          itemStyle: { color: '#409EFF' }
        }]
      }

      this.trendChart.setOption(option)
    },
    handleResize() {
      if (this.trendChart) {
        this.trendChart.resize()
      }
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  color: #303133;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-card .el-card__body {
  display: flex;
  align-items: center;
  width: 100%;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
}

.stat-icon i {
  font-size: 28px;
  color: #fff;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.chart-container {
  height: 300px;
}

.today-list {
  max-height: 300px;
  overflow-y: auto;
}

.today-item {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.today-item:last-child {
  border-bottom: none;
}

.sport-name {
  color: #303133;
  font-weight: 500;
}

.sport-duration {
  color: #909399;
}
</style>
