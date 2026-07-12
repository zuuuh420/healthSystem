<template>
  <div id="app">
    <el-container v-if="isLoggedIn">
      <!-- 侧边栏 -->
      <el-aside width="220px" class="sidebar">
        <div class="logo">
          <h2>健康管理系统</h2>
        </div>
        <el-menu
          :default-active="activeMenu"
          class="sidebar-menu"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router
        >
          <el-menu-item index="/dashboard">
            <i class="el-icon-s-home"></i>
            <span>首页仪表盘</span>
          </el-menu-item>

          <el-submenu index="sport">
            <template slot="title">
              <i class="el-icon-basketball"></i>
              <span>运动管理</span>
            </template>
            <el-menu-item index="/sport/records">运动记录</el-menu-item>
            <el-menu-item index="/sport/plans">运动计划</el-menu-item>
            <el-menu-item index="/sport/stats">运动统计</el-menu-item>
          </el-submenu>

          <el-submenu index="goal">
            <template slot="title">
              <i class="el-icon-aim"></i>
              <span>目标管理</span>
            </template>
            <el-menu-item index="/goal/list">健康目标</el-menu-item>
            <el-menu-item index="/goal/checkin">每日打卡</el-menu-item>
          </el-submenu>
        </el-menu>
      </el-aside>

      <!-- 主内容区 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-left">
            <i class="el-icon-s-fold toggle-btn" @click="toggleSidebar"></i>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="$route.meta.title">{{ $route.meta.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <i class="el-icon-user-solid"></i>
                {{ userInfo.nickname || userInfo.username }}
                <i class="el-icon-arrow-down"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 页面内容 -->
        <el-main class="main-content">
          <router-view />
        </el-main>
      </el-container>
    </el-container>

    <!-- 未登录时显示登录页 -->
    <router-view v-else />
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      isCollapse: false
    }
  },
  computed: {
    isLoggedIn() {
      return !!localStorage.getItem('token')
    },
    userInfo() {
      const info = localStorage.getItem('userInfo')
      return info ? JSON.parse(info) : {}
    },
    activeMenu() {
      return this.$route.path
    }
  },
  methods: {
    toggleSidebar() {
      this.isCollapse = !this.isCollapse
    },
    handleCommand(command) {
      if (command === 'logout') {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        this.$router.push('/login')
        this.$message.success('已退出登录')
      } else if (command === 'profile') {
        this.$router.push('/profile')
      }
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
}

#app {
  height: 100%;
}

.sidebar {
  background-color: #304156;
  min-height: 100vh;
  overflow-y: auto;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #263445;
}

.logo h2 {
  color: #fff;
  font-size: 18px;
  white-space: nowrap;
}

.sidebar-menu {
  border-right: none;
}

.header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.toggle-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #606266;
}

.main-content {
  background-color: #f0f2f5;
  min-height: calc(100vh - 60px);
}
</style>
