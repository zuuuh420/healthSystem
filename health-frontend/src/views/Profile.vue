<template>
  <div class="profile">
    <h2 class="page-title">个人中心</h2>

    <el-row :gutter="20">
      <el-col :span="8">
        <el-card class="user-card">
          <div class="user-avatar">
            <i class="el-icon-user-solid"></i>
          </div>
          <h3>{{ userInfo.nickname || userInfo.username }}</h3>
          <p class="user-email">{{ userInfo.email }}</p>
          <p class="user-role">角色：{{ userInfo.role === 'admin' ? '管理员' : '普通用户' }}</p>
        </el-card>
      </el-col>

      <el-col :span="16">
        <el-card>
          <template #header>
            <span>修改密码</span>
          </template>
          <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="旧密码" prop="oldPassword">
              <el-input v-model="form.oldPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="form.newPassword" type="password" show-password />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="form.confirmPassword" type="password" show-password />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleChangePassword" :loading="loading">
                修改密码
              </el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { changePassword } from '@/api/auth'

export default {
  name: 'Profile',
  data() {
    const validateConfirm = (rule, value, callback) => {
      if (value !== this.form.newPassword) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }

    return {
      loading: false,
      form: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      rules: {
        oldPassword: [
          { required: true, message: '请输入旧密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirm, trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    userInfo() {
      const info = localStorage.getItem('userInfo')
      return info ? JSON.parse(info) : {}
    }
  },
  methods: {
    async handleChangePassword() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          this.loading = true
          try {
            const res = await changePassword({
              oldPassword: this.form.oldPassword,
              newPassword: this.form.newPassword
            })
            if (res.code === 200) {
              this.$message.success('密码修改成功')
              this.form = { oldPassword: '', newPassword: '', confirmPassword: '' }
            } else {
              this.$message.error(res.msg || '修改失败')
            }
          } catch (error) {
            this.$message.error('修改失败')
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
.profile {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  color: #303133;
}

.user-card {
  text-align: center;
  padding: 30px 20px;
}

.user-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: linear-gradient(135deg, #409EFF, #66b1ff);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
}

.user-avatar i {
  font-size: 50px;
  color: #fff;
}

.user-card h3 {
  font-size: 20px;
  color: #303133;
  margin-bottom: 10px;
}

.user-email {
  color: #909399;
  margin-bottom: 5px;
}

.user-role {
  color: #606266;
}
</style>
