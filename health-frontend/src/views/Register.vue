<template>
  <main class="register-page">
    <section class="visual-side">
      <div class="visual-image" />
      <router-link class="brand" to="/login"
        ><i class="el-icon-data-analysis" /> 知衡</router-link
      >
      <div class="visual-copy">
        <span>BEGIN YOUR TRACE</span>
        <h1>让今天的记录，<br />成为明天的依据。</h1>
        <p>建立个人账户，持续整理身体指标、运动与饮食变化。</p>
      </div>
    </section>
    <section class="form-side">
      <router-link class="back-link" to="/login"
        ><i class="el-icon-back" /> 返回首页</router-link
      >
      <div class="form-wrap">
        <span class="eyebrow">CREATE ACCOUNT</span>
        <h2>创建你的健康档案</h2>
        <p class="intro">只需要基础信息，稍后可以继续完善个人资料。</p>
        <el-form ref="form" :model="form" :rules="rules" label-position="top">
          <div class="field-grid">
            <el-form-item label="用户名" prop="username"
              ><el-input
                v-model="form.username"
                placeholder="3-20个字符" /></el-form-item
            ><el-form-item label="昵称" prop="nickname"
              ><el-input v-model="form.nickname" placeholder="怎么称呼你"
            /></el-form-item>
          </div>
          <el-form-item label="邮箱" prop="email"
            ><el-input v-model="form.email" placeholder="name@example.com"
          /></el-form-item>
          <div class="field-grid">
            <el-form-item label="密码" prop="password"
              ><el-input
                v-model="form.password"
                type="password"
                show-password
                placeholder="至少6位" /></el-form-item
            ><el-form-item label="确认密码" prop="confirmPassword"
              ><el-input
                v-model="form.confirmPassword"
                type="password"
                show-password
                placeholder="再次输入"
                @keyup.enter.native="handleRegister"
            /></el-form-item>
          </div>
          <el-button
            class="register-button"
            :loading="loading"
            @click="handleRegister"
            >创建账户 <i class="el-icon-right"
          /></el-button>
        </el-form>
        <p class="login-tip">
          已有账户？<router-link to="/login">返回登录</router-link>
        </p>
      </div>
    </section>
  </main>
</template>
<script>
import { register } from "@/api/auth";
export default {
  name: "Register",
  data() {
    const confirm = (r, v, c) =>
      v !== this.form.password ? c(new Error("两次输入的密码不一致")) : c();
    return {
      loading: false,
      form: {
        username: "",
        nickname: "",
        email: "",
        password: "",
        confirmPassword: "",
      },
      rules: {
        username: [
          { required: true, message: "请输入用户名", trigger: "blur" },
          { min: 3, max: 20, message: "长度为3到20个字符", trigger: "blur" },
        ],
        nickname: [{ required: true, message: "请输入昵称", trigger: "blur" }],
        email: [
          { required: true, message: "请输入邮箱", trigger: "blur" },
          { type: "email", message: "邮箱格式不正确", trigger: "blur" },
        ],
        password: [
          { required: true, message: "请输入密码", trigger: "blur" },
          { min: 6, message: "密码不能少于6位", trigger: "blur" },
        ],
        confirmPassword: [
          { required: true, message: "请确认密码", trigger: "blur" },
          { validator: confirm, trigger: "blur" },
        ],
      },
    };
  },
  methods: {
    handleRegister() {
      this.$refs.form.validate(async (valid) => {
        if (!valid) return;
        this.loading = true;
        try {
          const { confirmPassword, ...data } = this.form;
          await register(data);
          this.$message.success("账户创建成功，请登录");
          this.$router.push("/login");
        } catch (e) {
        } finally {
          this.loading = false;
        }
      });
    },
  },
};
</script>
<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&family=Noto+Serif+SC:wght@500;600&display=swap");
.register-page {
  min-height: 100vh;
  display: grid;
  grid-template-columns: 44% 56%;
  font-family: Inter, "Microsoft YaHei", sans-serif;
  background: #f6f4ef;
}
.visual-side {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
  background: #050706;
  color: #fff;
}
.visual-image {
  position: absolute;
  inset: 0;
  background: url("../assets/health-hero-reveal.webp") center/cover no-repeat;
  opacity: 0.78;
}
.visual-side:after {
  content: "";
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.16), rgba(0, 0, 0, 0.72));
}
.brand {
  position: absolute;
  z-index: 2;
  top: 28px;
  left: 32px;
  color: #fff;
  text-decoration: none;
  font-family: "Noto Serif SC", serif;
  font-size: 23px;
}
.visual-copy {
  position: absolute;
  z-index: 2;
  left: 44px;
  right: 44px;
  bottom: 48px;
}
.visual-copy span,
.eyebrow {
  font-size: 10px;
  letter-spacing: 2px;
  color: #e58248;
  font-weight: 700;
}
.visual-copy h1 {
  font-family: "Noto Serif SC", serif;
  font-size: 39px;
  line-height: 1.35;
  margin: 15px 0;
}
.visual-copy p {
  font-size: 13px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.7);
}
.form-side {
  position: relative;
  display: grid;
  place-items: center;
  padding: 70px;
}
.back-link {
  position: absolute;
  top: 28px;
  right: 34px;
  color: #616b65;
  text-decoration: none;
  font-size: 13px;
}
.form-wrap {
  width: min(580px, 100%);
}
.form-wrap h2 {
  font-family: "Noto Serif SC", serif;
  font-size: 32px;
  margin: 13px 0 8px;
}
.intro {
  color: #7b837e;
  margin-bottom: 32px;
  font-size: 13px;
}
.field-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 18px;
}
.form-wrap >>> .el-form-item__label {
  font-weight: 600;
  color: #303a34;
}
.form-wrap >>> .el-input__inner {
  height: 47px;
  border: 1px solid #d2d6d1;
  border-radius: 0;
  background: transparent;
}
.register-button {
  width: 100%;
  height: 50px;
  border: 0;
  border-radius: 0;
  background: #173f31;
  color: #fff;
  font-weight: 700;
  margin-top: 8px;
}
.login-tip {
  text-align: center;
  margin-top: 22px;
  color: #7d847f;
  font-size: 13px;
}
.login-tip a {
  color: #a95025;
  font-weight: 700;
  text-decoration: none;
  margin-left: 6px;
}
@media (max-width: 780px) {
  .register-page {
    display: block;
  }
  .visual-side {
    min-height: 260px;
  }
  .visual-copy {
    left: 24px;
    bottom: 24px;
  }
  .visual-copy h1 {
    font-size: 30px;
  }
  .form-side {
    padding: 60px 24px;
  }
  .back-link {
    top: 20px;
    right: 22px;
  }
  .field-grid {
    grid-template-columns: 1fr;
    gap: 0;
  }
}
</style>
