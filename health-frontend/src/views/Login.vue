<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">健康管理系统</h2>
      <el-form ref="form" :model="form" :rules="rules" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="form.username"
            prefix-icon="el-icon-user"
            placeholder="请输入用户名"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="form.password"
            prefix-icon="el-icon-lock"
            placeholder="请输入密码"
            type="password"
            size="large"
            show-password
            @keyup.enter.native="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            class="login-btn"
          >
            登录
          </el-button>
        </el-form-item>
        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/auth'

export default {
  name: 'Login',
  data() {
    return {
      loading: false,
      form: {
        username: '',
        password: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    handleLogin() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            const res = await login(this.form)
            if (res.code === 200) {
              // 保存token和用户信息
              localStorage.setItem('token', res.data.token)
              localStorage.setItem('userInfo', JSON.stringify(res.data.userInfo))

              this.$store.dispatch('login', {
                token: res.data.token,
                userInfo: res.data.userInfo
              })

              this.$message.success('登录成功')
              this.$router.push('/dashboard')
            } else {
              this.$message.error(res.msg || '登录失败')
            }
          } catch (error) {
            this.$message.error('登录失败，请检查用户名和密码')
          } finally {
            this.loading = false
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
  font-size: 28px;
}

.login-form {
  width: 100%;
}

.login-btn {
  width: 100%;
}

.login-footer {
  text-align: center;
  margin-top: 10px;
  color: #909399;
}

.login-footer a {
  color: #409EFF;
  text-decoration: none;
  margin-left: 5px;
}
</style>
