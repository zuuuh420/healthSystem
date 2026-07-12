<template>
  <div id="app">
    <el-container v-if="isLoggedIn" class="app-shell">
      <el-aside :width="isCollapse ? '72px' : '236px'" class="sidebar">
        <div class="brand"><span class="brand-mark">H</span><div v-show="!isCollapse"><strong>知衡健康</strong><small>个人健康工作台</small></div></div>
        <el-menu :default-active="activeMenu" :collapse="isCollapse" router class="sidebar-menu">
          <el-menu-item index="/dashboard"><i class="el-icon-data-analysis"/><span slot="title">健康概览</span></el-menu-item>
          <el-menu-item index="/analysis"><i class="el-icon-pie-chart"/><span slot="title">健康分析</span></el-menu-item>
          <el-submenu index="sport">
            <template slot="title"><i class="el-icon-basketball"/><span>运动管理</span></template>
            <el-menu-item index="/sport/records">运动记录</el-menu-item>
            <el-menu-item index="/sport/plans">运动计划</el-menu-item>
            <el-menu-item index="/sport/stats">运动统计</el-menu-item>
          </el-submenu>
          <el-submenu index="goal">
            <template slot="title"><i class="el-icon-aim"/><span>目标管理</span></template>
            <el-menu-item index="/goal/list">健康目标</el-menu-item>
            <el-menu-item index="/goal/checkin">每日打卡</el-menu-item>
          </el-submenu>
        </el-menu>
        <button class="collapse-button" type="button" @click="toggleSidebar"><i :class="isCollapse ? 'el-icon-s-unfold' : 'el-icon-s-fold'"/><span v-if="!isCollapse">收起导航</span></button>
      </el-aside>
      <el-container class="workspace">
        <el-header class="header">
          <div><span class="date-label">{{ currentDate }}</span><strong>{{ $route.meta.title || '个人健康' }}</strong></div>
          <el-dropdown @command="handleCommand">
            <span class="user-info"><span class="avatar">{{ userInitial }}</span><span class="user-copy"><strong>{{ userInfo.nickname || userInfo.username }}</strong><small>个人账户</small></span><i class="el-icon-arrow-down"/></span>
            <el-dropdown-menu slot="dropdown"><el-dropdown-item command="profile">个人中心</el-dropdown-item><el-dropdown-item command="logout" divided>退出登录</el-dropdown-item></el-dropdown-menu>
          </el-dropdown>
        </el-header>
        <el-main class="main-content"><router-view /></el-main>
      </el-container>
    </el-container>
    <router-view v-else />
  </div>
</template>

<script>
export default {
  name: 'App', data: () => ({ isCollapse: false }),
  computed: {
    isLoggedIn() { return !!this.$store.state.token },
    userInfo() { return this.$store.state.userInfo || {} },
    userInitial() { return (this.userInfo.nickname || this.userInfo.username || '健').slice(0, 1) },
    activeMenu() { return this.$route.path },
    currentDate() { return new Intl.DateTimeFormat('zh-CN', { month: 'long', day: 'numeric', weekday: 'short' }).format(new Date()) }
  },
  methods: {
    toggleSidebar() { this.isCollapse = !this.isCollapse },
    handleCommand(command) { if (command === 'logout') { this.$store.dispatch('logout'); this.$router.push('/login'); this.$message.success('已退出登录') } else { this.$router.push('/profile') } }
  }
}
</script>

<style>
*{box-sizing:border-box;margin:0;padding:0}html,body,#app{height:100%;font-family:"Microsoft YaHei","PingFang SC",Arial,sans-serif;color:#17231e;background:#f4f7f5}.app-shell{min-height:100vh}.sidebar{position:relative;background:#fff;border-right:1px solid #e1e8e4;transition:width .22s;overflow:hidden}.brand{height:86px;display:flex;align-items:center;gap:12px;padding:0 18px}.brand-mark{width:36px;height:36px;display:grid;place-items:center;background:#183f32;color:#fff;border-radius:7px;font-weight:800}.brand div{display:flex;flex-direction:column;white-space:nowrap}.brand strong{font-size:16px}.brand small,.user-copy small{font-size:11px;color:#8a9690;margin-top:3px}.sidebar-menu{border:0!important}.sidebar .el-menu-item,.sidebar .el-submenu__title{height:48px;line-height:48px;margin:3px 10px;border-radius:6px;color:#59665f}.sidebar .el-menu-item.is-active{background:#eaf5f0;color:#167653;font-weight:700}.collapse-button{position:absolute;bottom:18px;left:12px;width:calc(100% - 24px);height:42px;border:0;border-top:1px solid #edf1ef;background:#fff;color:#6f7a75;cursor:pointer;text-align:left;padding:0 14px}.collapse-button span{margin-left:12px}.workspace{min-width:0}.header{height:72px!important;background:#fff;border-bottom:1px solid #e1e8e4;display:flex;align-items:center;justify-content:space-between;padding:0 28px}.header>div{display:flex;flex-direction:column}.date-label{font-size:11px;color:#8b9791;margin-bottom:4px}.header strong{font-size:15px}.user-info{display:flex;align-items:center;gap:10px;cursor:pointer}.avatar{width:36px;height:36px;border-radius:50%;display:grid;place-items:center;background:#dff1e9;color:#176d4e;font-weight:700}.user-copy{display:flex;flex-direction:column}.main-content{padding:28px;background:#f4f7f5;min-height:calc(100vh - 72px)}.el-card{border:1px solid #e3e9e6!important;border-radius:8px!important;box-shadow:none!important}@media(max-width:760px){.sidebar{width:72px!important}.brand div,.collapse-button span{display:none}.header{padding:0 16px}.user-copy{display:none}.main-content{padding:16px}}
</style>
