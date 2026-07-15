<template>
  <main
    class="auth-hero"
    @mousemove="handlePointer"
    @touchmove.passive="handleTouch"
  >
    <div class="base-image hero-zoom" />
    <div class="reveal-image" :style="revealStyle" />
    <div class="shade" />

    <nav class="hero-nav">
      <button class="brand" type="button" @click="resetSpotlight">
        <span class="brand-mark"><i class="el-icon-data-analysis" /></span
        ><span class="wordmark">知衡</span>
      </button>
      <div class="nav-pill">
        <button class="active" type="button">健康首页</button>
        <button type="button" @click="openLogin('记录数据')">数据记录</button>
        <button type="button" @click="openLogin('运动计划')">运动计划</button>
        <button type="button" @click="openLogin('健康周报')">健康周报</button>
      </div>
      <div class="nav-actions">
        <button class="login-link" type="button" @click="openLogin()">
          登录
        </button>
        <button
          class="signup-button"
          type="button"
          @click="$router.push('/register')"
        >
          创建账户
        </button>
      </div>
    </nav>

    <section class="hero-heading">
      <span class="hero-kicker hero-anim hero-fade"
        >PERSONAL HEALTH, MADE VISIBLE</span
      >
      <h1>
        <span class="serif hero-anim hero-reveal first-line">每一次记录</span
        ><span class="hero-anim hero-reveal second-line">都让身体更清晰</span>
      </h1>
    </section>

    <div class="bottom-copy hero-anim hero-fade">
      <span>01 / HEALTH TRACE</span>
      <p>
        体重、血压、心率与每一次运动，看似零散的数据，会在时间里汇聚成属于你的健康轨迹。
      </p>
    </div>
    <div class="bottom-action hero-anim hero-fade">
      <p>
        移动光标，看看持续记录如何让状态从模糊走向清晰。你的改变，不必等到很久以后才被看见。
      </p>
      <button type="button" @click="openLogin()">
        开始记录 <i class="el-icon-right" />
      </button>
    </div>

    <transition name="panel">
      <aside v-if="loginVisible" class="login-panel">
        <button
          class="close-button"
          type="button"
          aria-label="关闭登录"
          @click="loginVisible = false"
        >
          <i class="el-icon-close" />
        </button>
        <div class="panel-heading">
          <span>WELCOME BACK</span>
          <h2>继续你的健康记录</h2>
          <p>
            {{
              intendedFeature
                ? `登录后即可进入${intendedFeature}`
                : "数据会持续保留，并只对你的账户可见。"
            }}
          </p>
        </div>
        <el-form
          ref="form"
          :model="form"
          :rules="rules"
          class="login-form"
          label-position="top"
        >
          <el-form-item label="用户名" prop="username"
            ><el-input
              v-model="form.username"
              placeholder="请输入用户名"
              autocomplete="username"
              prefix-icon="el-icon-user"
          /></el-form-item>
          <el-form-item label="密码" prop="password"
            ><el-input
              v-model="form.password"
              placeholder="请输入密码"
              type="password"
              autocomplete="current-password"
              show-password
              prefix-icon="el-icon-lock"
              @keyup.enter.native="handleLogin"
          /></el-form-item>
          <el-button
            class="submit-button"
            :loading="loading"
            @click="handleLogin"
            >进入健康工作台 <i class="el-icon-right"
          /></el-button>
        </el-form>
        <div class="panel-footer">
          <span>还没有账户？</span
          ><router-link to="/register">免费创建账户</router-link>
        </div>
        <div class="privacy-note">
          <i class="el-icon-lock" /> 健康数据采用账户隔离，仅用于个人趋势分析
        </div>
      </aside>
    </transition>
    <div
      v-if="loginVisible"
      class="panel-backdrop"
      @click="loginVisible = false"
    />
  </main>
</template>

<script>
import { login } from '@/api/auth'

const SPOTLIGHT_RADIUS = 260

export default {
  name: 'Login',
  data() {
    const startX = window.innerWidth * 0.52
    const startY = window.innerHeight * 0.56
    return {
      loading: false,
      loginVisible: false,
      intendedFeature: '',
      form: { username: '', password: '' },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ]
      },
      mouse: { x: startX, y: startY },
      smooth: { x: startX, y: startY },
      cursor: { x: startX, y: startY },
      frame: null
    }
  },
  computed: {
    revealStyle() {
      const mask = `radial-gradient(circle ${SPOTLIGHT_RADIUS}px at ${this.cursor.x}px ${this.cursor.y}px, #fff 0%, #fff 38%, rgba(255,255,255,.76) 58%, rgba(255,255,255,.35) 74%, transparent 100%)`
      return { WebkitMaskImage: mask, maskImage: mask }
    }
  },
  mounted() {
    this.animateSpotlight()
  },
  beforeDestroy() {
    cancelAnimationFrame(this.frame)
  },
  methods: {
    handlePointer(event) {
      this.mouse.x = event.clientX
      this.mouse.y = event.clientY
    },
    handleTouch(event) {
      const touch = event.touches && event.touches[0]
      if (touch) {
        this.mouse.x = touch.clientX
        this.mouse.y = touch.clientY
      }
    },
    animateSpotlight() {
      this.smooth.x += (this.mouse.x - this.smooth.x) * 0.1
      this.smooth.y += (this.mouse.y - this.smooth.y) * 0.1
      this.cursor = { x: this.smooth.x, y: this.smooth.y }
      this.frame = requestAnimationFrame(this.animateSpotlight)
    },
    resetSpotlight() {
      this.mouse = { x: window.innerWidth * 0.52, y: window.innerHeight * 0.56 }
    },
    openLogin(feature = '') {
      this.intendedFeature = feature
      this.loginVisible = true
      this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
    },
    handleLogin() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        this.loading = true
        try {
          const res = await login(this.form)
          this.$store.dispatch('login', { token: res.data.token, userInfo: res.data.userInfo })
          this.$message.success('欢迎回来')
          this.$router.push('/dashboard')
        } finally {
          this.loading = false
        }
      })
    }
  }
}
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Noto+Serif+SC:wght@500;600&display=swap");
.auth-hero {
  position: relative;
  width: 100%;
  height: 100vh;
  height: 100dvh;
  overflow: hidden;
  background: #050706;
  color: #fff;
  font-family: Inter, "Microsoft YaHei", sans-serif;
  letter-spacing: 0;
}
.base-image,
.reveal-image,
.shade {
  position: absolute;
  inset: 0;
  background-position: center;
  background-size: cover;
  background-repeat: no-repeat;
}
.base-image {
  z-index: 1;
  background-image: url("../assets/health-hero-base.webp");
}
.reveal-image {
  z-index: 2;
  background-image: url("../assets/health-hero-reveal.webp");
  pointer-events: none;
}
.shade {
  z-index: 3;
  background: linear-gradient(
    180deg,
    rgba(0, 0, 0, 0.34),
    transparent 30%,
    rgba(0, 0, 0, 0.14) 62%,
    rgba(0, 0, 0, 0.64)
  );
}
.hero-nav {
  position: fixed;
  z-index: 20;
  top: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 28px;
}
.brand {
  display: flex;
  align-items: center;
  gap: 10px;
  border: 0;
  background: none;
  color: #fff;
  cursor: pointer;
}
.brand-mark {
  width: 30px;
  height: 30px;
  display: grid;
  place-items: center;
  border: 1px solid rgba(255, 255, 255, 0.65);
  border-radius: 50%;
}
.wordmark {
  font-family: "Noto Serif SC", serif;
  font-size: 24px;
  font-weight: 600;
}
.nav-pill {
  position: absolute;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 3px;
  padding: 6px;
  background: rgba(255, 255, 255, 0.14);
  backdrop-filter: blur(18px);
  border: 1px solid rgba(255, 255, 255, 0.24);
  border-radius: 30px;
}
.nav-pill button,
.login-link {
  border: 0;
  background: transparent;
  color: rgba(255, 255, 255, 0.76);
  padding: 9px 16px;
  border-radius: 22px;
  font-size: 13px;
  cursor: pointer;
  transition: 0.2s;
}
.nav-pill button:hover,
.nav-pill .active {
  background: rgba(255, 255, 255, 0.18);
  color: #fff;
}
.nav-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.signup-button {
  border: 0;
  background: #fff;
  color: #182019;
  padding: 11px 20px;
  border-radius: 24px;
  font-weight: 700;
  cursor: pointer;
}
.hero-heading {
  position: absolute;
  z-index: 8;
  top: 14%;
  left: 0;
  right: 0;
  text-align: center;
  padding: 0 24px;
  pointer-events: none;
}
.hero-kicker {
  display: block;
  font-size: 10px;
  letter-spacing: 3px;
  color: rgba(255, 255, 255, 0.68);
  margin-bottom: 20px;
}
.hero-heading h1 {
  font-weight: 400;
  line-height: 0.98;
}
.hero-heading h1 span {
  display: block;
  font-size: 92px;
}
.serif {
  font-family: "Noto Serif SC", serif;
  font-weight: 500;
}
.second-line {
  margin-top: 4px;
}
.bottom-copy,
.bottom-action {
  position: absolute;
  z-index: 8;
  bottom: 48px;
}
.bottom-copy {
  left: 48px;
  width: 285px;
}
.bottom-copy span {
  font-size: 10px;
  letter-spacing: 2px;
  color: #d88650;
}
.bottom-copy p,
.bottom-action p {
  margin-top: 12px;
  font-size: 13px;
  line-height: 1.75;
  color: rgba(255, 255, 255, 0.72);
}
.bottom-action {
  right: 48px;
  width: 285px;
}
.bottom-action button {
  margin-top: 20px;
  border: 0;
  background: #e26f32;
  color: #fff;
  padding: 13px 24px;
  border-radius: 28px;
  font-weight: 700;
  cursor: pointer;
  transition: 0.25s;
}
.bottom-action button:hover {
  transform: translateY(-2px);
  background: #f07a39;
  box-shadow: 0 12px 28px rgba(226, 111, 50, 0.28);
}
.login-panel {
  position: fixed;
  z-index: 40;
  top: 0;
  right: 0;
  width: min(460px, 100%);
  height: 100dvh;
  background: #f6f4ef;
  color: #172019;
  padding: 86px 48px 36px;
  box-shadow: -20px 0 60px rgba(0, 0, 0, 0.25);
  overflow: auto;
}
.panel-backdrop {
  position: fixed;
  z-index: 35;
  inset: 0;
  background: rgba(0, 0, 0, 0.28);
}
.close-button {
  position: absolute;
  top: 24px;
  right: 24px;
  width: 40px;
  height: 40px;
  border: 1px solid #d6d8d3;
  border-radius: 50%;
  background: transparent;
  cursor: pointer;
}
.panel-heading > span {
  font-size: 10px;
  letter-spacing: 2px;
  color: #b95c2c;
  font-weight: 700;
}
.panel-heading h2 {
  font-family: "Noto Serif SC", serif;
  font-size: 31px;
  margin: 13px 0 9px;
}
.panel-heading p {
  color: #788079;
  font-size: 13px;
  line-height: 1.6;
}
.login-form {
  margin-top: 40px;
}
.login-form >>> .el-form-item__label {
  font-weight: 600;
  color: #343d37;
}
.login-form >>> .el-input__inner {
  height: 48px;
  border: 0;
  border-bottom: 1px solid #cbd0cb;
  border-radius: 0;
  background: transparent;
  padding-left: 34px;
}
.submit-button {
  width: 100%;
  height: 50px;
  margin-top: 10px;
  border: 0;
  background: #173f31;
  color: #fff;
  border-radius: 0;
  font-weight: 700;
}
.panel-footer {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 24px;
  font-size: 13px;
  color: #7d847f;
}
.panel-footer a {
  color: #a95025;
  font-weight: 700;
  text-decoration: none;
}
.privacy-note {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #daddd8;
  color: #929893;
  font-size: 11px;
}
.panel-enter-active,
.panel-leave-active {
  transition: transform 0.45s cubic-bezier(0.16, 1, 0.3, 1);
}
.panel-enter,
.panel-leave-to {
  transform: translateX(100%);
}
@keyframes heroReveal {
  0% {
    opacity: 0;
    transform: translateY(28px);
    filter: blur(12px);
  }
  100% {
    opacity: 1;
    transform: none;
    filter: none;
  }
}
@keyframes heroFade {
  0% {
    opacity: 0;
    transform: translateY(20px);
  }
  100% {
    opacity: 1;
    transform: none;
  }
}
@keyframes heroZoom {
  0% {
    transform: scale(1.12);
  }
  100% {
    transform: scale(1);
  }
}
.hero-anim {
  opacity: 0;
  animation-fill-mode: forwards;
  animation-timing-function: cubic-bezier(0.16, 1, 0.3, 1);
}
.hero-reveal {
  animation: heroReveal 1.1s forwards;
}
.hero-fade {
  animation: heroFade 1s forwards;
}
.hero-zoom {
  animation: heroZoom 1.8s cubic-bezier(0.16, 1, 0.3, 1) forwards;
}
.first-line {
  animation-delay: 0.25s;
}
.second-line {
  animation-delay: 0.42s;
}
.hero-kicker {
  animation-delay: 0.15s;
}
.bottom-copy {
  animation-delay: 0.7s;
}
.bottom-action {
  animation-delay: 0.85s;
}
@media (max-width: 1100px) {
  .hero-heading h1 span {
    font-size: 72px;
  }
}
@media (max-width: 820px) {
  .nav-pill {
    display: none;
  }
  .hero-nav {
    padding: 16px;
  }
  .login-link {
    display: none;
  }
  .signup-button {
    padding: 10px 15px;
  }
  .hero-heading {
    top: 17%;
  }
  .hero-heading h1 span {
    font-size: 50px;
  }
  .bottom-copy {
    display: none;
  }
  .bottom-action {
    left: 22px;
    right: 22px;
    bottom: 28px;
    width: auto;
  }
  .bottom-action p {
    max-width: 450px;
  }
  .login-panel {
    padding: 82px 28px 28px;
  }
}
@media (max-width: 430px) {
  .hero-heading h1 span {
    font-size: 42px;
  }
}
@media (prefers-reduced-motion: reduce) {
  .hero-anim,
  .hero-zoom {
    animation: none;
    opacity: 1;
  }
  .reveal-image {
    display: none;
  }
}
</style>
