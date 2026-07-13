<template>
  <div id="app">
    <el-container v-if="isLoggedIn" class="app-shell">
      <el-aside :width="isCollapse ? '72px' : '224px'" class="sidebar">
        <div class="brand">
          <span class="brand-mark"><i class="el-icon-data-analysis" /></span>
          <div v-show="!isCollapse">
            <strong>知衡健康</strong><small>个人健康工作台</small>
          </div>
        </div>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          router
          class="sidebar-menu"
        >
          <el-menu-item index="/dashboard"
            ><i class="el-icon-data-analysis" /><span slot="title"
              >健康概览</span
            ></el-menu-item
          >
          <el-menu-item index="/analysis"
            ><i class="el-icon-pie-chart" /><span slot="title"
              >健康分析</span
            ></el-menu-item
          >
          <el-menu-item index="/report"
            ><i class="el-icon-document" /><span slot="title"
              >健康周报</span
            ></el-menu-item
          >
          <el-menu-item index="/health-data"
            ><i class="el-icon-monitor" /><span slot="title"
              >身体数据</span
            ></el-menu-item
          >
          <el-menu-item index="/diet"
            ><i class="el-icon-food" /><span slot="title"
              >饮食记录</span
            ></el-menu-item
          >
          <el-submenu index="sport"
            ><template slot="title"
              ><i class="el-icon-basketball" /><span>运动管理</span></template
            ><el-menu-item index="/sport/records">运动记录</el-menu-item
            ><el-menu-item index="/sport/plans">运动计划</el-menu-item
            ><el-menu-item index="/sport/stats"
              >运动统计</el-menu-item
            ></el-submenu
          >
          <el-submenu index="goal"
            ><template slot="title"
              ><i class="el-icon-aim" /><span>目标管理</span></template
            ><el-menu-item index="/goal/list">健康目标</el-menu-item
            ><el-menu-item index="/goal/checkin"
              >每日打卡</el-menu-item
            ></el-submenu
          >
        </el-menu>
        <div class="sidebar-vine" />
        <button class="collapse-button" type="button" @click="toggleSidebar">
          <i :class="isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'" /><span
            v-if="!isCollapse"
            >收起导航</span
          >
        </button>
      </el-aside>
      <el-container class="workspace">
        <el-header class="header"
          ><div class="header-title">
            <span class="date-label">{{ currentDate }}</span
            ><strong>{{ $route.meta.title || "个人健康" }}</strong>
          </div>
          <div class="header-tools">
            <div class="search-box">
              <i class="el-icon-search" /><input
                v-model="searchText"
                placeholder="搜索功能或记录"
                @keyup.enter="runSearch"
              />
            </div>
            <el-dropdown @command="handleCommand"
              ><span class="user-info"
                ><span class="avatar">{{ userInitial }}</span
                ><span class="user-copy"
                  ><strong>{{ userInfo.nickname || userInfo.username }}</strong
                  ><small>个人账户</small></span
                ><i class="el-icon-arrow-down" /></span
              ><el-dropdown-menu slot="dropdown"
                ><el-dropdown-item command="profile">个人中心</el-dropdown-item
                ><el-dropdown-item command="logout" divided
                  >退出登录</el-dropdown-item
                ></el-dropdown-menu
              ></el-dropdown
            >
          </div></el-header
        >
        <el-main class="main-content"><router-view /></el-main>
      </el-container>
    </el-container>
    <router-view v-else />
  </div>
</template>

<script>
import { getCurrentUser } from "@/api/auth";

export default {
  name: "App",
  data: () => ({ isCollapse: false, searchText: "" }),
  computed: {
    isLoggedIn() {
      return !!this.$store.state.token;
    },
    userInfo() {
      return this.$store.state.userInfo || {};
    },
    userInitial() {
      return (this.userInfo.nickname || this.userInfo.username || "健").slice(
        0,
        1,
      );
    },
    activeMenu() {
      return this.$route.path;
    },
    currentDate() {
      return new Intl.DateTimeFormat("zh-CN", {
        month: "long",
        day: "numeric",
        weekday: "short",
      }).format(new Date());
    },
  },
  created() {
    this.refreshCurrentUser();
  },
  methods: {
    async refreshCurrentUser() {
      if (!this.isLoggedIn) return;
      try {
        const response = await getCurrentUser();
        this.$store.commit("SET_USER_INFO", response.data);
      } catch (error) {
        // Authentication failures are handled by the shared response interceptor.
      }
    },
    toggleSidebar() {
      this.isCollapse = !this.isCollapse;
    },
    runSearch() {
      const text = this.searchText.trim();
      if (!text) return;
      if (/运动|锻炼|记录/.test(text)) this.$router.push("/sport/records");
      else if (/目标|打卡/.test(text)) this.$router.push("/goal/list");
      else if (/分析|趋势/.test(text)) this.$router.push("/analysis");
      else this.$message.info("暂未找到相关功能");
    },
    handleCommand(command) {
      if (command === "logout") {
        this.$store.dispatch("logout");
        this.$router.push("/login");
        this.$message.success("已退出登录");
      } else this.$router.push("/profile");
    },
  },
};
</script>

<style>
@import url("https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Noto+Serif+SC:wght@500;600&display=swap");
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}
html,
body,
#app {
  height: 100%;
  font-family: Inter, "PingFang SC", "Microsoft YaHei", sans-serif;
  color: #25342b;
  background: #f4f7f1;
}
body { overflow: hidden; }
.app-shell {
  min-height: 100vh;
  width: 100vw;
  overflow: hidden;
}
.sidebar {
  position: relative;
  z-index: 3;
  overflow: hidden;
  background: #fffdf8;
  border-right: 0;
  box-shadow: 4px 0 24px rgba(63, 95, 75, 0.04);
  transition: width 0.22s;
}
.brand {
  height: 94px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 0 20px;
}
.brand-mark {
  width: 39px;
  height: 39px;
  display: grid;
  place-items: center;
  border-radius: 13px;
  background: #dcebdd;
  color: #3f5f4b;
}
.brand div {
  display: flex;
  flex-direction: column;
  white-space: nowrap;
}
.brand strong {
  font-family: "Noto Serif SC", serif;
  font-size: 17px;
  color: #3f5f4b;
}
.brand small {
  margin-top: 4px;
  font-size: 10px;
  color: #94a098;
}
.sidebar-menu {
  position: relative;
  z-index: 2;
  border: 0 !important;
  background: transparent;
}
.sidebar .el-menu-item,
.sidebar .el-submenu__title {
  height: 46px;
  line-height: 46px;
  margin: 3px 12px;
  border-radius: 14px;
  color: #68756c;
}
.sidebar .el-menu-item i,
.sidebar .el-submenu__title i {
  color: #94a098;
}
.sidebar .el-menu-item:hover,
.sidebar .el-submenu__title:hover {
  background: #f1f6ee;
  color: #3f5f4b;
}
.sidebar .el-menu-item.is-active {
  background: #dcebdd;
  color: #3f5f4b;
  font-weight: 600;
  box-shadow: inset 3px 0 #6d8871;
}
.sidebar .el-menu-item.is-active i {
  color: #6d8871;
}
.sidebar .el-submenu .el-menu {
  background: transparent;
}
.sidebar .el-submenu .el-menu-item {
  padding-left: 55px !important;
  height: 40px;
  line-height: 40px;
  font-size: 13px;
}
.sidebar-vine {
  position: absolute;
  z-index: 1;
  right: -35px;
  bottom: 0;
  width: 240px;
  height: 220px;
  opacity: 0.16;
  background: url("./assets/vine-transparent.webp") right bottom/contain
    no-repeat;
  pointer-events: none;
}
.collapse-button {
  position: absolute;
  z-index: 3;
  left: 16px;
  bottom: 18px;
  width: calc(100% - 32px);
  height: 38px;
  border: 0;
  background: transparent;
  color: #94a098;
  text-align: left;
  cursor: pointer;
}
.collapse-button span {
  margin-left: 10px;
  font-size: 12px;
}
.workspace {
  min-width: 0;
  overflow: hidden;
}
.header {
  height: 78px !important;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 34px;
  background: #f4f7f1;
  border-bottom: 1px solid #e4ebe1;
}
.header-title {
  display: flex;
  flex-direction: column;
}
.date-label {
  margin-bottom: 5px;
  color: #94a098;
  font-size: 11px;
}
.header strong {
  font-size: 15px;
  color: #344c3d;
}
.header-tools {
  display: flex;
  align-items: center;
  gap: 24px;
}
.search-box {
  display: flex;
  align-items: center;
  width: 220px;
  height: 36px;
  padding: 0 12px;
  border-radius: 18px;
  background: #ecf4ea;
  color: #94a098;
}
.search-box input {
  width: 100%;
  margin-left: 8px;
  border: 0;
  outline: 0;
  background: transparent;
  color: #344c3d;
  font: inherit;
  font-size: 12px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}
.avatar {
  width: 36px;
  height: 36px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  background: #dcebdd;
  color: #3f5f4b;
  font-weight: 600;
}
.user-copy {
  display: flex;
  flex-direction: column;
}
.user-copy small {
  margin-top: 3px;
  color: #94a098;
  font-size: 10px;
}
.main-content {
  min-height: calc(100vh - 78px);
  padding: 34px;
  background: #f4f7f1;
  overflow: auto;
}
.el-card {
  border: 0 !important;
  border-radius: 24px !important;
  background: #fcfbf7 !important;
  box-shadow: 0 12px 40px rgba(63, 95, 75, 0.06) !important;
}
.el-button { border-radius: 12px; font-weight: 600; transition: background .2s, border-color .2s, transform .2s; }
.el-button:hover { transform: translateY(-1px); }
.el-button--primary { color: #fff; background: #3f5f4b; border-color: #3f5f4b; }
.el-button--primary:hover, .el-button--primary:focus { color: #fff; background: #6d8871; border-color: #6d8871; }
.el-button--danger { color: #fff; background: #c98b62; border-color: #c98b62; }
.el-button--default { color: #3f5f4b; background: #fcfbf7; border-color: #dce5da; }
@media (max-width: 820px) {
  .sidebar {
    width: 72px !important;
  }
  .brand {
    padding: 0 17px;
  }
  .brand div,
  .collapse-button span {
    display: none;
  }
  .header {
    padding: 0 18px;
  }
  .header-tools {
    gap: 12px;
  }
  .search-box {
    width: 38px;
    padding: 0 12px;
  }
  .search-box input,
  .user-copy {
    display: none;
  }
  .main-content {
    padding: 20px 16px;
  }
}
</style>
