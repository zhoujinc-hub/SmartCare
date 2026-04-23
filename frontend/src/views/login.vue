<template>
  <div class="login-container">
    <div class="login-bg"></div>
    <el-card class="login-card">
      <div class="login-header">
        <div class="logo">
          <el-icon class="logo-icon"><House /></el-icon>
        </div>
        <h2>智慧社区养老监护系统</h2>
        <p class="login-desc">老人摔倒监测·智能报警·实时守护</p>
      </div>

      <LoginForm
          :init-form="initLoginForm"
          @login-success="handleLoginSuccess"
          @forgot-pwd="handleForgotPwd"
          ref="loginFormRef"
          @register="handleOpenRegister"
      />

      <div class="login-footer">
        <p>© 2026 智慧社区养老监护系统 版权所有</p>
      </div>
    </el-card>

    <ForgotPwd
        v-model="forgotPwdVisible"
        @reset-success="handleResetSuccess"
    />

    <RegisterForm
        v-model="registerVisible"
        @register-success="handleRegisterSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House } from '@element-plus/icons-vue'
import LoginForm from '@/components/login/LoginForm.vue'
import ForgotPwd from '@/components/login/ForgotPwd.vue'
import RegisterForm from '@/components/login/RegisterForm.vue'
import { getRememberUser } from '@/utils/auth'
import type { LoginForm as LoginFormType } from '@/types/user'

const router = useRouter()
const forgotPwdVisible = ref(false)
const registerVisible = ref(false)
const loginFormRef = ref<InstanceType<typeof LoginForm>>()

const initLoginForm = ref<LoginFormType>({
  username: '',
  password: '',
  userType: 2,
  rememberMe: false
})

onMounted(() => {
  const rememberUser = getRememberUser()
  initLoginForm.value = {
    username: rememberUser.username,
    password: '',
    userType: rememberUser.userType || 2,
    rememberMe: !!rememberUser.username
  }
})

const handleLoginSuccess = (userType: number) => {
  if (userType === 1) {
    router.push('/dashboard')
  } else {
    router.push('/fallEvent')
  }
}

const handleForgotPwd = () => {
  forgotPwdVisible.value = true
}

const handleResetSuccess = () => {
  loginFormRef.value?.resetForm()
}

const handleOpenRegister = () => {
  registerVisible.value = true
}

const handleRegisterSuccess = () => {
  ElMessage.success('注册成功，请登录！')
  registerVisible.value = false
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
  position: relative;
  overflow: hidden;
}
.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #409eff 0%, #69b1ff 100%);
  opacity: 0.1;
  z-index: 0;
}
.login-card {
  width: 450px;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  background-color: #fff;
  position: relative;
  z-index: 1;
}
.login-header {
  text-align: center;
  margin-bottom: 25px;
}
.logo {
  width: 60px;
  height: 60px;
  margin: 0 auto 15px;
  background-color: #409eff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
}
.login-header h2 {
  font-size: 22px;
  color: #1f2937;
  margin: 0 0 8px;
  font-weight: 600;
}
.login-desc {
  font-size: 14px;
  color: #666;
  margin: 0;
}
.login-footer {
  text-align: center;
  font-size: 12px;
  color: #999;
  margin-top: 20px;
}
</style>