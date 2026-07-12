<template>
  <section class="dashboard" v-loading="loading">
    <header class="welcome-row">
      <div>
        <span class="eyebrow">DAILY OVERVIEW</span>
        <h1>{{ greeting }}，{{ displayName }}</h1>
        <p>把今天的小记录，变成长期可见的改变。</p>
      </div>
      <div class="quick-actions">
        <el-button
          type="primary"
          icon="el-icon-plus"
          @click="$router.push('/sport/records/add')"
          >记录运动</el-button
        ><el-button icon="el-icon-check" @click="$router.push('/goal/checkin')"
          >今日打卡</el-button
        >
      </div>
    </header>

    <div v-if="overview" class="dashboard-grid">
      <article class="score-card">
        <div>
          <span class="eyebrow light">HEALTH SCORE</span>
          <h2>本周健康状态</h2>
        </div>
        <div class="score-body">
          <strong>{{ overview.healthScore }}</strong
          ><span>/ 100</span>
        </div>
        <div class="score-status"><i />{{ overview.scoreLevel }}</div>
        <p>{{ overview.summary }}</p>
        <button @click="$router.push('/analysis')">
          查看分析依据 <i class="el-icon-right" />
        </button>
      </article>

      <div class="metric-strip">
        <article class="strip-metric">
          <span>本周运动</span
          ><strong>{{ overview.weeklyMinutes }}<small> 分钟</small></strong
          ><em
            >{{
              Math.min(100, Math.round((overview.weeklyMinutes / 150) * 100))
            }}% 周目标</em
          >
        </article>
        <article class="strip-metric">
          <span>能量消耗</span
          ><strong
            >{{ Math.round(overview.weeklyCalories)
            }}<small> kcal</small></strong
          ><em>来自运动记录</em>
        </article>
        <article class="strip-metric">
          <span>连续打卡</span
          ><strong>{{ overview.currentStreak }}<small> 天</small></strong
          ><em>保持稳定节奏</em>
        </article>
        <article class="strip-metric">
          <span>进行中目标</span
          ><strong>{{ overview.activeGoals }}<small> 项</small></strong
          ><em>{{ overview.completedGoals }}项已完成</em>
        </article>
      </div>

      <article class="trend-panel">
        <div class="panel-heading">
          <div>
            <span class="eyebrow">7-DAY TREND</span>
            <h2>运动趋势</h2>
          </div>
          <span>分钟</span>
        </div>
        <div v-if="hasTrend" ref="trendChart" class="chart" />
        <div v-else class="chart-empty">
          <span class="seed-icon"><i class="el-icon-sunny" /></span>
          <strong>本周的生长还未开始</strong>
          <p>记录一次10分钟散步，让第一条健康轨迹出现。</p>
          <el-button
            type="primary"
            plain
            @click="$router.push('/sport/records/add')"
            >记录第一次运动</el-button
          >
        </div>
      </article>
      <article class="next-panel">
        <div class="panel-heading">
          <div>
            <span class="eyebrow">TODAY</span>
            <h2>建议行动</h2>
          </div>
        </div>
        <div
          v-for="(item, index) in overview.suggestions.slice(0, 3)"
          :key="item"
          class="action-item"
        >
          <span>{{ index + 1 }}</span>
          <p>{{ cleanText(item) }}</p>
        </div>
        <el-button plain @click="$router.push('/analysis')"
          >查看完整分析</el-button
        >
      </article>
    </div>
    <div v-else-if="!loading" class="empty-panel">
      <i class="el-icon-data-analysis" />
      <h2>暂时没有健康概览</h2>
      <p>记录一次运动后，这里会生成你的趋势与建议。</p>
    </div>
  </section>
</template>

<script>
import * as echarts from "echarts";
import { getDashboardOverview } from "@/api/dashboard";
export default {
  name: "Dashboard",
  data: () => ({ loading: false, overview: null, chart: null }),
  computed: {
    hasTrend() {
      const trend = (this.overview && this.overview.trend) || [];
      return trend.some((item) => Number(item.minutes) > 0);
    },
    displayName() {
      const user = this.$store.state.userInfo || {};
      return user.nickname || user.username || "朋友";
    },
    greeting() {
      const hour = new Date().getHours();
      return hour < 11 ? "早上好" : hour < 18 ? "下午好" : "晚上好";
    },
  },
  created() {
    this.loadData();
  },
  mounted() {
    window.addEventListener("resize", this.resizeChart);
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.resizeChart);
    if (this.chart) this.chart.dispose();
  },
  methods: {
    async loadData() {
      this.loading = true;
      try {
        const res = await getDashboardOverview();
        this.overview = res.data;
        this.$nextTick(this.renderChart);
      } finally {
        this.loading = false;
      }
    },
    renderChart() {
      if (!this.$refs.trendChart) return;
      this.chart = this.chart || echarts.init(this.$refs.trendChart);
      const data = this.overview.trend || [];
      this.chart.setOption({
        grid: { left: 8, right: 8, top: 24, bottom: 4, containLabel: true },
        tooltip: { trigger: "axis" },
        xAxis: {
          type: "category",
          boundaryGap: false,
          data: data.map((i) => i.date.slice(5)),
          axisLine: { lineStyle: { color: "#dce4e0" } },
          axisLabel: { color: "#84908a" },
        },
        yAxis: {
          type: "value",
          splitLine: { lineStyle: { color: "#eef2f0" } },
          axisLabel: { color: "#84908a" },
        },
        series: [
          {
            type: "line",
            smooth: true,
            symbolSize: 7,
            data: data.map((i) => i.minutes),
            lineStyle: { width: 3, color: "#27a376" },
            itemStyle: { color: "#27a376" },
            areaStyle: {
              color: {
                type: "linear",
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: "rgba(39,163,118,.24)" },
                  { offset: 1, color: "rgba(39,163,118,0)" },
                ],
              },
            },
          },
        ],
      });
    },
    resizeChart() {
      if (this.chart) this.chart.resize();
    },
    cleanText(value) {
      return (value || "").replace(/‎/g, "");
    },
  },
};
</script>

<style scoped>
.dashboard {
  max-width: 1240px;
  margin: 0 auto;
}
.welcome-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-bottom: 26px;
}
.eyebrow {
  font-size: 10px;
  letter-spacing: 1.5px;
  font-weight: 800;
  color: #21845f;
}
.eyebrow.light {
  color: #9fd7c1;
}
.welcome-row h1 {
  font-size: 28px;
  margin: 5px 0 7px;
  color: #17231e;
}
.welcome-row p {
  color: #77827d;
}
.dashboard-grid {
  display: grid;
  grid-template-columns: 1.5fr 1fr;
  gap: 16px;
}
.score-card {
  background: #183f32;
  color: #fff;
  border-radius: 8px;
  padding: 26px;
  min-height: 235px;
}
.score-card h2 {
  font-size: 18px;
  margin-top: 7px;
}
.score-body {
  margin: 20px 0 4px;
}
.score-body strong {
  font-size: 56px;
  line-height: 1;
}
.score-body span {
  color: #b5c9c1;
}
.score-status {
  float: right;
  margin-top: -44px;
  color: #bfe5d6;
  font-size: 13px;
}
.score-status i {
  display: inline-block;
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #69d19f;
  margin-right: 7px;
}
.score-card p {
  color: #c8d8d2;
  line-height: 1.6;
  max-width: 500px;
}
.score-card button {
  margin-top: 20px;
  border: 0;
  background: transparent;
  color: #fff;
  cursor: pointer;
}
.metric-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}
.metric-card {
  background: #fff;
  border: 1px solid #e2e9e5;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  flex-direction: column;
}
.metric-card > span {
  font-size: 13px;
  color: #7d8983;
}
.metric-card strong {
  font-size: 28px;
  margin: 15px 0;
  color: #17231e;
}
.metric-card small {
  font-size: 12px;
  font-weight: 500;
}
.metric-card em {
  font-size: 11px;
  color: #27936c;
  font-style: normal;
}
.trend-panel,
.next-panel {
  background: #fff;
  border: 1px solid #e2e9e5;
  border-radius: 8px;
  padding: 22px;
}
.panel-heading {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.panel-heading h2 {
  font-size: 18px;
  margin-top: 4px;
}
.panel-heading > span {
  font-size: 11px;
  color: #8c9792;
}
.chart {
  height: 280px;
}
.action-item {
  display: grid;
  grid-template-columns: 28px 1fr;
  gap: 10px;
  padding: 15px 0;
  border-bottom: 1px solid #edf1ef;
}
.action-item span {
  width: 24px;
  height: 24px;
  display: grid;
  place-items: center;
  background: #e9f5ef;
  color: #1b7f5b;
  border-radius: 50%;
  font-size: 11px;
  font-weight: 700;
}
.action-item p {
  font-size: 13px;
  line-height: 1.55;
  color: #4f5c56;
}
.next-panel .el-button {
  width: 100%;
  margin-top: 18px;
}
.empty-panel {
  text-align: center;
  padding: 80px;
  background: #fff;
  border: 1px solid #e2e9e5;
}
.empty-panel i {
  font-size: 42px;
  color: #27a376;
}
.empty-panel h2 {
  margin: 16px;
}
.empty-panel p {
  color: #8b9691;
}
@media (max-width: 980px) {
  .dashboard-grid {
    grid-template-columns: 1fr;
  }
  .welcome-row {
    align-items: flex-start;
  }
  .quick-actions {
    display: flex;
  }
  .metric-grid {
    min-height: 220px;
  }
}
@media (max-width: 620px) {
  .welcome-row {
    display: block;
  }
  .quick-actions {
    margin-top: 18px;
  }
  .metric-grid {
    grid-template-columns: 1fr 1fr;
  }
  .metric-card {
    padding: 15px;
  }
  .metric-card strong {
    font-size: 23px;
  }
  .score-status {
    float: none;
    margin: 10px 0;
  }
  .welcome-row h1 {
    font-size: 24px;
  }
}
.dashboard {
  color: #25332c;
}
.welcome-row {
  padding-bottom: 8px;
}
.welcome-row h1 {
  font-family: "Noto Serif SC", "Microsoft YaHei", serif;
  font-weight: 600;
  letter-spacing: 0;
}
.welcome-row p {
  color: #858278;
}
.quick-actions .el-button {
  border-radius: 0;
  border-color: #d5d1c5;
  background: #faf9f5;
}
.quick-actions .el-button--primary {
  border-color: #e07035;
  background: #e07035;
}
.score-card {
  position: relative;
  overflow: hidden;
  background: #112f26;
  border-radius: 0;
  min-height: 250px;
}
.score-card:after {
  content: "";
  position: absolute;
  right: -10%;
  bottom: -34%;
  width: 70%;
  height: 90%;
  opacity: 0.32;
  background: url("../assets/health-hero-reveal.webp") center / cover no-repeat;
  transform: rotate(-7deg);
  pointer-events: none;
}
.score-card > * {
  position: relative;
  z-index: 1;
}
.metric-card {
  border-radius: 0;
  background: #faf9f5;
  border-color: #ddd9cf;
}
.metric-card strong {
  font-family: "Noto Serif SC", "Microsoft YaHei", serif;
  font-weight: 600;
}
.trend-panel,
.next-panel {
  border-radius: 0;
  background: #faf9f5;
  border-color: #ddd9cf;
}
.panel-heading h2 {
  font-family: "Noto Serif SC", "Microsoft YaHei", serif;
  font-weight: 600;
}
.action-item span {
  background: #e07035;
  color: #fff;
  border-radius: 0;
}
.next-panel .el-button {
  border-radius: 0;
  border-color: #cfcabe;
  color: #365244;
}
.welcome-row {
  position: relative;
  overflow: hidden;
  min-height: 104px;
  padding: 16px 210px 20px 6px;
}
.welcome-row:after {
  content: "";
  position: absolute;
  top: -26px;
  right: 0;
  width: 250px;
  height: 150px;
  opacity: 0.1;
  background: url("../assets/vine-transparent.webp") center top / contain
    no-repeat;
  pointer-events: none;
}
.quick-actions {
  position: relative;
  z-index: 1;
  white-space: nowrap;
}
.dashboard-grid {
  grid-template-columns: 1.55fr 1fr;
  gap: 18px;
}
.score-card {
  grid-column: 1 / -1;
  min-height: 280px;
  padding: 32px;
  border-radius: 18px;
}
.score-card:after {
  right: -4%;
  bottom: -18%;
  width: 78%;
  height: 92%;
  opacity: 0.2;
  background-image: url("../assets/vine-transparent.webp");
  background-size: contain;
  background-position: right bottom;
  transform: none;
}
.score-body strong {
  font-family: "Noto Serif SC", serif;
  font-size: 76px;
  color: #f5f0e6;
}
.score-status {
  display: inline-flex;
  align-items: center;
  float: none;
  margin: 14px 0 0 20px;
  vertical-align: top;
}
.metric-strip {
  grid-column: 1 / -1;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  background: #fcfbf7;
  border-radius: 18px;
  box-shadow: 0 12px 30px rgba(41, 58, 49, 0.06);
  overflow: hidden;
}
.strip-metric {
  position: relative;
  min-width: 0;
  padding: 22px 24px;
  display: flex;
  flex-direction: column;
}
.strip-metric + .strip-metric:before {
  content: "";
  position: absolute;
  left: 0;
  top: 22px;
  bottom: 22px;
  width: 1px;
  background: #e7e2d7;
}
.strip-metric span {
  color: #7f847c;
  font-size: 12px;
}
.strip-metric strong {
  margin: 10px 0 7px;
  font-family: "Noto Serif SC", serif;
  font-size: 27px;
  color: #172b24;
}
.strip-metric small {
  font-family: Inter, sans-serif;
  font-size: 11px;
  font-weight: 500;
}
.strip-metric em {
  color: #557a46;
  font-size: 11px;
  font-style: normal;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
.trend-panel,
.next-panel {
  border: 0;
  border-radius: 18px;
  box-shadow: 0 12px 30px rgba(41, 58, 49, 0.055);
}
.chart-empty {
  height: 280px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: #738078;
}
.chart-empty .seed-icon {
  width: 50px;
  height: 50px;
  display: grid;
  place-items: center;
  margin-bottom: 14px;
  border-radius: 50%;
  background: #edf2df;
  color: #557a46;
  font-size: 22px;
}
.chart-empty strong {
  font-family: "Noto Serif SC", serif;
  color: #24382f;
  font-size: 18px;
}
.chart-empty p {
  margin: 8px 0 18px;
  font-size: 12px;
}
.chart-empty .el-button {
  border-radius: 22px;
  color: #557a46;
  border-color: #a8c66c;
  background: #f5f7ed;
}
@media (max-width: 980px) {
  .metric-strip {
    grid-template-columns: 1fr 1fr;
  }
  .strip-metric:nth-child(3):before {
    display: none;
  }
}
@media (max-width: 720px) {
  .welcome-row {
    padding-right: 6px;
  }
  .welcome-row:after {
    display: none;
  }
  .metric-strip {
    grid-template-columns: 1fr;
  }
  .strip-metric + .strip-metric:before {
    top: 0;
    bottom: auto;
    right: 22px;
    width: auto;
    height: 1px;
  }
}
</style>
