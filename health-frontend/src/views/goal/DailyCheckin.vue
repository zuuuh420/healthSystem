<template>
  <div class="daily-checkin">
    <!-- 打卡卡片 -->
    <el-card class="checkin-card">
      <div class="checkin-content">
        <div class="checkin-status">
          <div class="status-icon" :class="{ checked: isCheckedIn }">
            <i :class="isCheckedIn ? 'el-icon-check' : 'el-icon-time'"></i>
          </div>
          <h2>{{ isCheckedIn ? '今日已打卡' : '今日未打卡' }}</h2>
          <p v-if="isCheckedIn" class="checkin-time">
            打卡时间：{{ checkinTime }}
          </p>
        </div>

        <el-button
          type="primary"
          size="large"
          :disabled="isCheckedIn"
          :loading="checkinLoading"
          @click="handleCheckin"
          class="checkin-btn"
        >
          {{ isCheckedIn ? '已打卡' : '立即打卡' }}
        </el-button>
      </div>
    </el-card>

    <!-- 打卡统计 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ streakData.currentStreak || 0 }}</div>
          <div class="stat-label">连续打卡天数</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ streakData.maxStreak || 0 }}</div>
          <div class="stat-label">最长连续天数</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ streakData.totalCheckins || 0 }}</div>
          <div class="stat-label">总打卡次数</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 打卡日历 -->
    <el-card class="calendar-card">
      <template #header>
        <div class="card-header">
          <span>打卡日历</span>
          <el-date-picker
            v-model="currentMonth"
            type="month"
            placeholder="选择月份"
            value-format="yyyy-MM"
            @change="loadCalendar"
          />
        </div>
      </template>

      <div class="calendar">
        <div class="calendar-header">
          <div v-for="day in weekDays" :key="day" class="calendar-weekday">{{ day }}</div>
        </div>
        <div class="calendar-body">
          <div
            v-for="(day, index) in calendarDays"
            :key="index"
            class="calendar-day"
            :class="{
              'is-empty': !day,
              'is-today': day && day.isToday,
              'is-checked': day && day.isChecked,
              'is-future': day && day.isFuture
            }"
          >
            <span v-if="day">{{ day.date }}</span>
            <i v-if="day && day.isChecked" class="el-icon-check"></i>
          </div>
        </div>
      </div>

      <div class="calendar-legend">
        <span class="legend-item">
          <span class="legend-dot checked"></span>
          已打卡
        </span>
        <span class="legend-item">
          <span class="legend-dot today"></span>
          今天
        </span>
      </div>
    </el-card>
  </div>
</template>

<script>
import { checkin, getTodayCheckinStatus, getCheckinStreak, getCheckinCalendar } from '@/api/goal'

export default {
  name: 'DailyCheckin',
  data() {
    return {
      isCheckedIn: false,
      checkinTime: '',
      checkinLoading: false,
      streakData: {},
      currentMonth: '',
      calendarDays: [],
      checkinDays: [],
      weekDays: ['日', '一', '二', '三', '四', '五', '六']
    }
  },
  created() {
    this.currentMonth = this.getCurrentMonth()
    this.loadAllData()
  },
  methods: {
    // 获取当前月份
    getCurrentMonth() {
      const now = new Date()
      return `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
    },
    // 加载所有数据
    async loadAllData() {
      await Promise.all([
        this.loadCheckinStatus(),
        this.loadStreak(),
        this.loadCalendar()
      ])
    },
    // 加载打卡状态
    async loadCheckinStatus() {
      try {
        const res = await getTodayCheckinStatus()
        if (res.code === 200) {
          this.isCheckedIn = res.data.checkedIn
          this.checkinTime = res.data.checkinTime || ''
        }
      } catch (error) {
        console.error('加载打卡状态失败', error)
      }
    },
    // 加载连续打卡数据
    async loadStreak() {
      try {
        const res = await getCheckinStreak()
        if (res.code === 200) {
          this.streakData = res.data
        }
      } catch (error) {
        console.error('加载连续打卡数据失败', error)
      }
    },
    // 加载打卡日历
    async loadCalendar() {
      try {
        const res = await getCheckinCalendar(this.currentMonth)
        if (res.code === 200) {
          this.checkinDays = res.data.checkinDays || []
          this.generateCalendar()
        }
      } catch (error) {
        console.error('加载打卡日历失败', error)
      }
    },
    // 生成日历数据
    generateCalendar() {
      const [year, month] = this.currentMonth.split('-').map(Number)
      const today = new Date()
      const todayDate = today.getDate()
      const todayMonth = today.getMonth() + 1
      const todayYear = today.getFullYear()

      // 获取当月第一天是星期几
      const firstDay = new Date(year, month - 1, 1).getDay()
      // 获取当月天数
      const daysInMonth = new Date(year, month, 0).getDate()

      const days = []

      // 填充空白
      for (let i = 0; i < firstDay; i++) {
        days.push(null)
      }

      // 填充日期
      for (let date = 1; date <= daysInMonth; date++) {
        const isToday = date === todayDate && month === todayMonth && year === todayYear
        const isChecked = this.checkinDays.includes(date)
        const isFuture = new Date(year, month - 1, date) > today && !isToday

        days.push({
          date,
          isToday,
          isChecked,
          isFuture
        })
      }

      this.calendarDays = days
    },
    // 打卡
    async handleCheckin() {
      this.checkinLoading = true
      try {
        const res = await checkin()
        if (res.code === 200) {
          this.$message.success('打卡成功！')
          this.loadAllData()
        } else {
          this.$message.error(res.msg || '打卡失败')
        }
      } catch (error) {
        this.$message.error('打卡失败')
      } finally {
        this.checkinLoading = false
      }
    }
  }
}
</script>

<style scoped>
.daily-checkin {
  padding: 20px;
}

.checkin-card {
  margin-bottom: 20px;
}

.checkin-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
}

.checkin-status {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.status-icon {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #909399 0%, #b4b7bc 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
  transition: all 0.3s ease;
}

.status-icon.checked {
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
}

.status-icon i {
  font-size: 48px;
  color: #fff;
}

.checkin-status h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 8px;
}

.checkin-time {
  font-size: 14px;
  color: #909399;
}

.checkin-btn {
  width: 200px;
  height: 60px;
  font-size: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
  color: #409EFF;
  line-height: 1;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.calendar-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.calendar {
  max-width: 700px;
  margin: 0 auto;
}

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 5px;
  margin-bottom: 10px;
}

.calendar-weekday {
  text-align: center;
  font-weight: bold;
  color: #606266;
  padding: 10px 0;
}

.calendar-body {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 5px;
}

.calendar-day {
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: #f5f7fa;
  position: relative;
  font-size: 16px;
  color: #303133;
}

.calendar-day.is-empty {
  background: transparent;
}

.calendar-day.is-today {
  background: #ecf5ff;
  color: #409EFF;
  font-weight: bold;
  border: 2px solid #409EFF;
}

.calendar-day.is-checked {
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
  color: #fff;
}

.calendar-day.is-checked i {
  position: absolute;
  bottom: 5px;
  font-size: 12px;
}

.calendar-day.is-future {
  color: #c0c4cc;
  background: #fafafa;
}

.calendar-legend {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  gap: 30px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.legend-dot {
  width: 16px;
  height: 16px;
  border-radius: 4px;
  background: #f5f7fa;
}

.legend-dot.checked {
  background: linear-gradient(135deg, #67C23A 0%, #85ce61 100%);
}

.legend-dot.today {
  background: #ecf5ff;
  border: 2px solid #409EFF;
}
</style>
