<template>
  <div class="sport-stats">
    <!-- 本周统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon">
              <i class="el-icon-time"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ weeklyStats.totalMinutes || 0 }}</div>
              <div class="stat-label">本周运动时长(分钟)</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon calories">
              <i class="el-icon-fire"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ weeklyStats.totalCalories || 0 }}</div>
              <div class="stat-label">本周消耗卡路里</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <div class="stat-card">
            <div class="stat-icon count">
              <i class="el-icon-document"></i>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ weeklyStats.recordCount || 0 }}</div>
              <div class="stat-label">本周运动次数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 趋势图 -->
    <el-card class="chart-card">
      <template #header>
        <div class="card-header">
          <span>运动趋势</span>
          <el-radio-group v-model="trendDays" size="small" @change="loadTrend">
            <el-radio-button :label="7">近7天</el-radio-button>
            <el-radio-button :label="14">近14天</el-radio-button>
            <el-radio-button :label="30">近30天</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div ref="trendChart" class="chart-container"></div>
      <el-empty v-if="!loading && trendData.length === 0" description="暂无运动数据" />
    </el-card>

    <!-- 分类饼图 -->
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>运动时长分布</span>
          </template>
          <div ref="categoryChart" class="chart-container"></div>
          <el-empty v-if="!loading && (!monthlyStats.categoryStats || monthlyStats.categoryStats.length === 0)" description="暂无数据" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>本月统计</span>
          </template>
          <div class="monthly-stats" v-if="monthlyStats.totalMinutes">
            <div class="monthly-item">
              <span class="label">总运动时长：</span>
              <span class="value">{{ monthlyStats.totalMinutes }} 分钟</span>
            </div>
            <div class="monthly-item">
              <span class="label">总消耗卡路里：</span>
              <span class="value">{{ monthlyStats.totalCalories }} 千卡</span>
            </div>
            <div class="monthly-item">
              <span class="label">运动次数：</span>
              <span class="value">{{ monthlyStats.recordCount }} 次</span>
            </div>
            <div class="monthly-item">
              <span class="label">平均每日运动：</span>
              <span class="value">{{ monthlyStats.avgMinutesPerDay }} 分钟</span>
            </div>
          </div>
          <el-empty v-else description="暂无本月数据" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getWeeklyStats, getMonthlyStats, getSportTrend } from '@/api/sport'

export default {
  name: 'SportStats',
  data() {
    return {
      loading: false,
      trendDays: 30,
      weeklyStats: {},
      monthlyStats: {},
      trendData: [],
      trendChart: null,
      categoryChart: null
    }
  },
  created() {
    this.loadAllStats()
  },
  mounted() {
    this.initCharts()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    if (this.trendChart) {
      this.trendChart.dispose()
    }
    if (this.categoryChart) {
      this.categoryChart.dispose()
    }
  },
  methods: {
    // 加载所有统计数据
    async loadAllStats() {
      this.loading = true
      try {
        await Promise.all([
          this.loadWeeklyStats(),
          this.loadMonthlyStats(),
          this.loadTrend()
        ])
      } finally {
        this.loading = false
      }
    },
    // 加载本周统计
    async loadWeeklyStats() {
      try {
        const res = await getWeeklyStats()
        if (res.code === 200) {
          this.weeklyStats = res.data
        }
      } catch (error) {
        console.error('加载本周统计失败', error)
      }
    },
    // 加载本月统计
    async loadMonthlyStats() {
      try {
        const res = await getMonthlyStats()
        if (res.code === 200) {
          this.monthlyStats = res.data
          this.updateCategoryChart()
        }
      } catch (error) {
        console.error('加载本月统计失败', error)
      }
    },
    // 加载趋势数据
    async loadTrend() {
      try {
        const res = await getSportTrend(this.trendDays)
        if (res.code === 200) {
          this.trendData = res.data.dailyStats || []
          this.updateTrendChart()
        }
      } catch (error) {
        console.error('加载趋势数据失败', error)
      }
    },
    // 初始化图表
    initCharts() {
      this.trendChart = echarts.init(this.$refs.trendChart)
      this.categoryChart = echarts.init(this.$refs.categoryChart)
    },
    // 更新趋势图
    updateTrendChart() {
      if (!this.trendChart || this.trendData.length === 0) return

      const dates = this.trendData.map(item => item.date)
      const minutes = this.trendData.map(item => item.minutes)
      const calories = this.trendData.map(item => item.calories)

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'cross'
          }
        },
        legend: {
          data: ['运动时长', '消耗卡路里']
        },
        xAxis: {
          type: 'category',
          data: dates,
          axisLabel: {
            rotate: 45
          }
        },
        yAxis: [
          {
            type: 'value',
            name: '时长(分钟)',
            position: 'left'
          },
          {
            type: 'value',
            name: '卡路里',
            position: 'right'
          }
        ],
        series: [
          {
            name: '运动时长',
            type: 'bar',
            data: minutes,
            itemStyle: {
              color: '#409EFF'
            }
          },
          {
            name: '消耗卡路里',
            type: 'line',
            yAxisIndex: 1,
            data: calories,
            itemStyle: {
              color: '#67C23A'
            },
            smooth: true
          }
        ]
      }

      this.trendChart.setOption(option)
    },
    // 更新分类饼图
    updateCategoryChart() {
      if (!this.categoryChart || !this.monthlyStats.categoryStats) return

      const data = this.monthlyStats.categoryStats.map(item => ({
        name: item.category,
        value: item.minutes
      }))

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c}分钟 ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: '运动分类',
            type: 'pie',
            radius: '50%',
            data: data,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      }

      this.categoryChart.setOption(option)
    },
    // 窗口大小变化
    handleResize() {
      if (this.trendChart) {
        this.trendChart.resize()
      }
      if (this.categoryChart) {
        this.categoryChart.resize()
      }
    }
  }
}
</script>

<style scoped>
.sport-stats {
  padding: 20px;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF 0%, #66b1ff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 20px;
}

.stat-icon.calories {
  background: linear-gradient(135deg, #F56C6C 0%, #f89898 100%);
}

.stat-icon.count {
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
}

.stat-icon i {
  font-size: 28px;
  color: #fff;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.chart-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  height: 400px;
  width: 100%;
}

.monthly-stats {
  padding: 20px;
}

.monthly-item {
  display: flex;
  justify-content: space-between;
  padding: 15px 0;
  border-bottom: 1px solid #ebeef5;
}

.monthly-item:last-child {
  border-bottom: none;
}

.monthly-item .label {
  color: #606266;
  font-size: 14px;
}

.monthly-item .value {
  color: #303133;
  font-weight: bold;
  font-size: 16px;
}
</style>
