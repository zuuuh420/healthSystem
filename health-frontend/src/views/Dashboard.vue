<template>
  <section class="dashboard" v-loading="loading">
    <header class="welcome">
      <div>
        <span class="date-note">{{ fullDate }}</span>
        <h1>{{ greeting }}，{{ displayName }}</h1>
        <p>把今天的小记录，变成长期可见的改变。</p>
      </div>
      <div class="welcome-actions">
        <el-button
          class="checkin-button"
          icon="el-icon-check"
          @click="$router.push('/goal/checkin')"
          >今日打卡</el-button
        >
        <el-button
          class="record-button"
          icon="el-icon-plus"
          @click="$router.push('/sport/records/add')"
          >记录运动</el-button
        >
      </div>
    </header>

    <section v-if="overview" class="rhythm-panel">
      <div class="rhythm-copy">
        <span class="section-label">本周节奏</span>
        <div class="score-line">
          <strong>{{ scoreText }}</strong
          ><small>综合健康分</small>
        </div>
        <span class="growth-state" :class="stateClass">{{ growthState }}</span>
        <h2>{{ focusTitle }}</h2>
        <p>{{ focusDescription }}</p>
        <button class="text-action" type="button" @click="handlePrimaryAction">
          {{ primaryAction }} <i class="el-icon-right" />
        </button>
      </div>

      <div class="rhythm-data">
        <div class="growth-track" aria-label="本周健康进度">
          <div class="track-line">
            <span :style="{ width: progressRate + '%' }" />
          </div>
          <div class="track-labels">
            <span>萌芽</span><span>抽芽</span><span>舒展</span>
          </div>
        </div>
        <div class="metric-band">
          <div>
            <span>运动</span><strong>{{ overview.weeklyMinutes }}</strong
            ><small>分钟</small>
          </div>
          <div>
            <span>能量</span
            ><strong>{{ Math.round(overview.weeklyCalories) }}</strong
            ><small>kcal</small>
          </div>
          <div>
            <span>打卡</span><strong>{{ overview.currentStreak }}</strong
            ><small>天</small>
          </div>
          <div>
            <span>目标</span><strong>{{ overview.activeGoals }}</strong
            ><small>项</small>
          </div>
        </div>
      </div>
      <img class="panel-vine" src="@/assets/vine-transparent.webp" alt="" />
    </section>

    <div v-if="overview" class="content-grid">
      <section class="trend-section">
        <header class="section-heading">
          <div>
            <span class="section-label">近7天变化</span>
            <h2>运动趋势</h2>
          </div>
          <router-link to="/sport/stats"
            >查看统计 <i class="el-icon-right"
          /></router-link>
        </header>
        <div class="chart-wrap">
          <div ref="trendChart" class="trend-chart" />
          <div v-if="!hasTrend" class="trend-empty">
            <strong>这一周还没有留下轨迹</strong>
            <p>先完成一次10分钟轻量活动，曲线会从这里开始生长。</p>
          </div>
        </div>
      </section>

      <section class="action-section">
        <header class="section-heading">
          <div>
            <span class="section-label">今天可以做</span>
            <h2>建议行动</h2>
          </div>
        </header>
        <div class="action-timeline">
          <div
            v-for="(action, index) in actions"
            :key="action.title"
            class="timeline-item"
          >
            <span class="timeline-node">{{ index + 1 }}</span>
            <div>
              <strong>{{ action.title }}</strong>
              <p>{{ action.description }}</p>
            </div>
          </div>
        </div>
        <router-link class="analysis-link" to="/analysis"
          >查看完整健康分析 <i class="el-icon-right"
        /></router-link>
      </section>

      <section class="records-section">
        <header class="section-heading">
          <div>
            <span class="section-label">真实积累</span>
            <h2>最近记录</h2>
          </div>
          <router-link to="/sport/records"
            >全部记录 <i class="el-icon-right"
          /></router-link>
        </header>
        <div v-if="recentRecords.length" class="record-list">
          <div
            v-for="record in recentRecords"
            :key="record.id"
            class="record-row"
          >
            <span class="record-date">{{
              formatRecordDate(record.sportDate)
            }}</span>
            <span class="record-icon"><i class="el-icon-basketball" /></span>
            <div>
              <strong>{{ record.sportTypeName || "运动记录" }}</strong
              ><small
                >{{ record.durationMinutes }}分钟 ·
                {{ Math.round(record.caloriesBurned || 0) }} kcal</small
              >
            </div>
            <em>+{{ Math.max(1, Math.round(record.durationMinutes / 2)) }}</em>
          </div>
        </div>
        <div v-else class="records-empty">
          <span><i class="el-icon-sunrise" /></span>
          <div>
            <strong>最近还没有运动记录</strong>
            <p>不必追求强度，先留下第一条真实轨迹。</p>
          </div>
        </div>
      </section>

      <section class="goal-section">
        <span class="section-label">本周目标</span>
        <h2>{{ overview.weeklyMinutes }} / 150 分钟</h2>
        <div class="goal-progress">
          <span :style="{ width: progressRate + '%' }" />
        </div>
        <p>{{ remainingText }}</p>
        <router-link to="/goal/list"
          >管理目标 <i class="el-icon-right"
        /></router-link>
      </section>
    </div>
  </section>
</template>

<script>
import * as echarts from "echarts";
import { getDashboardOverview } from "@/api/dashboard";
import { getSportRecords } from "@/api/sport";

export default {
  name: "Dashboard",
  data: () => ({
    loading: false,
    overview: null,
    recentRecords: [],
    chart: null,
  }),
  computed: {
    displayName() {
      const user = this.$store.state.userInfo || {};
      return user.nickname || user.username || "朋友";
    },
    greeting() {
      const hour = new Date().getHours();
      return hour < 11 ? "早上好" : hour < 18 ? "下午好" : "晚上好";
    },
    fullDate() {
      return new Intl.DateTimeFormat("zh-CN", {
        month: "long",
        day: "numeric",
        weekday: "long",
      }).format(new Date());
    },
    hasTrend() {
      return ((this.overview && this.overview.trend) || []).some(
        (item) => Number(item.minutes) > 0,
      );
    },
    progressRate() {
      return Math.min(
        100,
        Math.round(
          (((this.overview && this.overview.weeklyMinutes) || 0) / 150) * 100,
        ),
      );
    },
    scoreText() {
      return String((this.overview && this.overview.healthScore) || 0).padStart(
        2,
        "0",
      );
    },
    growthState() {
      if (!this.overview.weeklyMinutes) return "萌芽期";
      if (this.overview.weeklyMinutes < 90) return "抽芽期";
      if (this.overview.weeklyMinutes < 150) return "生长期";
      return "舒展期";
    },
    stateClass() {
      return `state-${this.growthState}`;
    },
    focusTitle() {
      if (!this.overview.weeklyMinutes) return "为这一周留下第一条轨迹";
      if (this.progressRate < 100) return "你正在建立自己的节奏";
      return "本周节奏已经舒展开来";
    },
    focusDescription() {
      if (!this.overview.weeklyMinutes)
        return "完成一次轻量运动，开始积累本周健康进度。";
      if (this.progressRate < 100)
        return `已经完成${this.overview.weeklyMinutes}分钟，再积累${150 - this.overview.weeklyMinutes}分钟即可达到本周目标。`;
      return "运动目标已经完成，接下来注意休息与持续记录。";
    },
    primaryAction() {
      return this.overview.weeklyMinutes ? "继续记录" : "开始10分钟活动";
    },
    remainingText() {
      const left = Math.max(0, 150 - (this.overview.weeklyMinutes || 0));
      return left ? `距离建议运动量还差${left}分钟` : "本周建议运动量已完成";
    },
    actions() {
      if (!this.overview.weeklyMinutes)
        return [
          { title: "迈出第一步", description: "今天完成一次10分钟散步。" },
          {
            title: "找到固定节奏",
            description: "选择一个每天容易坚持的时间段。",
          },
          { title: "种下周目标", description: "建立每周150分钟运动目标。" },
        ];
      return (this.overview.suggestions || [])
        .slice(0, 3)
        .map((item, index) => ({
          title: ["延续今天的节奏", "保持稳定记录", "查看目标进度"][index],
          description: String(item).replace(/‎/g, ""),
        }));
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
        const overviewRes = await getDashboardOverview();
        this.overview = overviewRes.data;
        try {
          const recordsRes = await getSportRecords({ pageNum: 1, pageSize: 4 });
          this.recentRecords = (recordsRes.data && recordsRes.data.records) || [];
        } catch (recordsError) {
          this.recentRecords = [];
        }
        this.$nextTick(this.renderChart);
      } finally {
        this.loading = false;
      }
    },
    renderChart() {
      if (!this.$refs.trendChart) return;
      this.chart = this.chart || echarts.init(this.$refs.trendChart);
      const source = this.overview.trend || [];
      const values = this.hasTrend
        ? source.map((item) => item.minutes)
        : [5, 8, 6, 12, 9, 14, 11];
      this.chart.setOption({
        animationDuration: 650,
        grid: { left: 5, right: 8, top: 20, bottom: 5, containLabel: true },
        tooltip: {
          show: this.hasTrend,
          trigger: "axis",
          backgroundColor: "#3f5f4b",
          borderWidth: 0,
          textStyle: { color: "#fff" },
        },
        xAxis: {
          type: "category",
          boundaryGap: false,
          data: source.map((item) => item.date.slice(5)),
          axisLine: { show: false },
          axisTick: { show: false },
          axisLabel: { color: "#94a098", fontSize: 11 },
        },
        yAxis: { type: "value", show: false },
        series: [
          {
            type: "line",
            smooth: true,
            symbol: this.hasTrend ? "circle" : "none",
            symbolSize: 7,
            silent: !this.hasTrend,
            data: values,
            lineStyle: {
              width: 3,
              color: this.hasTrend ? "#6d8871" : "rgba(168,198,108,.35)",
              type: this.hasTrend ? "solid" : "dashed",
            },
            itemStyle: {
              color: "#f4f7f1",
              borderColor: "#6d8871",
              borderWidth: 2,
            },
            areaStyle: {
              color: {
                type: "linear",
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  { offset: 0, color: "rgba(168,198,108,.28)" },
                  { offset: 1, color: "rgba(220,235,221,.03)" },
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
    handlePrimaryAction() {
      this.$router.push("/sport/records/add");
    },
    formatRecordDate(value) {
      if (!value) return "--";
      const date = new Date(value);
      const today = new Date();
      if (date.toDateString() === today.toDateString()) return "今天";
      return `${date.getMonth() + 1}/${date.getDate()}`;
    },
  },
};
</script>

<style scoped>
.dashboard {
  max-width: 1280px;
  margin: 0 auto;
  color: #25342b;
}
.welcome {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  padding: 10px 2px 30px;
}
.date-note,
.section-label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: #6d8871;
}
.welcome h1 {
  margin: 8px 0 7px;
  font-size: 32px;
  font-weight: 600;
}
.welcome p {
  font-size: 15px;
  color: #68756c;
}
.welcome-actions {
  display: flex;
  gap: 10px;
}
.welcome-actions .el-button {
  height: 42px;
  padding: 0 20px;
  border: 0;
  border-radius: 22px;
}
.checkin-button {
  background: #dcebdd;
  color: #3f5f4b;
}
.record-button {
  background: #c98b62;
  color: #fff;
}
.rhythm-panel {
  position: relative;
  display: grid;
  grid-template-columns: minmax(310px, 0.8fr) 1.2fr;
  gap: 58px;
  min-height: 310px;
  padding: 42px 48px;
  overflow: hidden;
  border-radius: 28px;
  background: linear-gradient(120deg, #ecf4ea, #dcebdd);
  box-shadow: 0 12px 40px rgba(63, 95, 75, 0.08);
}
.rhythm-copy,
.rhythm-data {
  position: relative;
  z-index: 2;
}
.score-line {
  display: flex;
  align-items: flex-end;
  gap: 14px;
  margin: 8px 0 4px;
}
.score-line strong {
  font-size: 70px;
  line-height: 1;
  font-weight: 500;
  color: #3f5f4b;
}
.score-line small {
  padding-bottom: 9px;
  color: #7e8b82;
}
.growth-state {
  display: inline-block;
  margin: 10px 0 18px;
  padding: 6px 11px;
  border-radius: 14px;
  background: #f2e4d8;
  color: #946b4e;
  font-size: 12px;
}
.rhythm-copy h2 {
  font-size: 22px;
  font-weight: 600;
  color: #344c3d;
}
.rhythm-copy p {
  max-width: 440px;
  margin-top: 9px;
  font-size: 14px;
  line-height: 1.7;
  color: #68756c;
}
.text-action {
  margin-top: 22px;
  border: 0;
  background: transparent;
  color: #3f5f4b;
  font-weight: 600;
  cursor: pointer;
}
.rhythm-data {
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.track-line {
  height: 8px;
  overflow: hidden;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.7);
}
.track-line span {
  display: block;
  height: 100%;
  border-radius: 8px;
  background: #6d8871;
  transition: width 0.65s ease;
}
.track-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 8px;
  font-size: 11px;
  color: #7e8b82;
}
.metric-band {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  margin-top: 48px;
}
.metric-band > div {
  padding: 0 24px;
}
.metric-band > div + div {
  border-left: 1px solid rgba(63, 95, 75, 0.16);
}
.metric-band span,
.metric-band small {
  display: block;
  color: #7e8b82;
  font-size: 12px;
}
.metric-band strong {
  display: inline-block;
  margin: 7px 6px 3px 0;
  font-size: 32px;
  font-weight: 500;
  color: #3f5f4b;
}
.panel-vine {
  position: absolute;
  top: -28px;
  right: -30px;
  width: 300px;
  opacity: 0.09;
  pointer-events: none;
}
.content-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.7fr) minmax(280px, 0.8fr);
  gap: 34px 42px;
  margin-top: 42px;
}
.trend-section,
.action-section,
.records-section,
.goal-section {
  min-width: 0;
}
.section-heading {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  margin-bottom: 18px;
}
.section-heading h2,
.goal-section h2 {
  margin-top: 5px;
  font-size: 21px;
  font-weight: 600;
  color: #344c3d;
}
.section-heading a,
.analysis-link,
.goal-section a {
  color: #6d8871;
  font-size: 13px;
  text-decoration: none;
}
.chart-wrap {
  position: relative;
  min-height: 290px;
  padding: 15px 0 0;
  background: #f8faf6;
  border-radius: 26px;
}
.trend-chart {
  height: 275px;
}
.trend-empty {
  position: absolute;
  inset: 70px 20px auto;
  text-align: center;
  pointer-events: none;
}
.trend-empty strong {
  font-size: 16px;
  color: #3f5f4b;
}
.trend-empty p {
  margin-top: 7px;
  font-size: 13px;
  color: #7e8b82;
}
.action-section {
  padding: 28px 30px;
  border-radius: 26px;
  background: #ecf4ea;
}
.action-timeline {
  position: relative;
  margin: 24px 0;
}
.action-timeline:before {
  content: "";
  position: absolute;
  left: 13px;
  top: 12px;
  bottom: 12px;
  width: 1px;
  background: #b9cdbb;
}
.timeline-item {
  position: relative;
  display: grid;
  grid-template-columns: 28px 1fr;
  gap: 15px;
  padding-bottom: 24px;
}
.timeline-node {
  position: relative;
  z-index: 1;
  width: 27px;
  height: 27px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: #f4f7f1;
  border: 2px solid #a8c66c;
  color: #557a46;
  font-size: 11px;
}
.timeline-item strong {
  font-size: 14px;
  color: #344c3d;
}
.timeline-item p {
  margin-top: 5px;
  font-size: 12px;
  line-height: 1.55;
  color: #7e8b82;
}
.analysis-link {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-weight: 600;
}
.records-section {
  padding-top: 8px;
}
.record-list {
  border-top: 1px solid #dfe7df;
}
.record-row {
  display: grid;
  grid-template-columns: 58px 34px 1fr auto;
  align-items: center;
  gap: 13px;
  padding: 17px 0;
  border-bottom: 1px solid #dfe7df;
}
.record-date {
  font-size: 12px;
  color: #94a098;
}
.record-icon {
  width: 32px;
  height: 32px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: #dcebdd;
  color: #557a46;
}
.record-row strong,
.record-row small {
  display: block;
}
.record-row strong {
  font-size: 14px;
  color: #344c3d;
}
.record-row small {
  margin-top: 4px;
  color: #7e8b82;
}
.record-row em {
  font-style: normal;
  color: #6d8871;
  font-weight: 600;
}
.records-empty {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 28px 0;
  border-top: 1px solid #dfe7df;
  border-bottom: 1px solid #dfe7df;
}
.records-empty > span {
  width: 42px;
  height: 42px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: #dcebdd;
  color: #557a46;
}
.records-empty strong {
  color: #344c3d;
}
.records-empty p {
  margin-top: 5px;
  font-size: 13px;
  color: #7e8b82;
}
.goal-section {
  padding: 28px 30px;
  border-radius: 26px;
  background: #fcfbf7;
  box-shadow: 0 12px 40px rgba(63, 95, 75, 0.06);
}
.goal-progress {
  height: 7px;
  margin: 22px 0 12px;
  border-radius: 7px;
  background: #e3ece1;
}
.goal-progress span {
  display: block;
  height: 100%;
  border-radius: 7px;
  background: #6d8871;
}
.goal-section p {
  margin-bottom: 22px;
  font-size: 13px;
  color: #7e8b82;
}
@media (max-width: 980px) {
  .rhythm-panel {
    grid-template-columns: 1fr;
    gap: 30px;
  }
  .content-grid {
    grid-template-columns: 1fr;
  }
  .metric-band {
    margin-top: 10px;
  }
}
@media (max-width: 680px) {
  .welcome {
    display: block;
  }
  .welcome-actions {
    margin-top: 20px;
  }
  .rhythm-panel {
    padding: 30px 24px;
  }
  .metric-band {
    grid-template-columns: 1fr 1fr;
    gap: 24px 0;
  }
  .metric-band > div:nth-child(3) {
    border-left: 0;
  }
  .content-grid {
    gap: 30px;
  }
  .score-line strong {
    font-size: 58px;
  }
  .panel-vine {
    width: 220px;
  }
  .record-row {
    grid-template-columns: 46px 32px 1fr;
  }
  .record-row em {
    display: none;
  }
}
</style>
