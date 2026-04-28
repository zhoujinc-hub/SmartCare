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
    // 管理员：跳转到管理后台
    router.push('/admin/dashboard')
  } else {
    // 家属：跳转到家属页面
    router.push('/userweb')
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
  position: relative;
  overflow: hidden;
  background:
      radial-gradient(circle at 20% 18%, rgba(255, 255, 255, 0.9), transparent 28%),
      radial-gradient(circle at 82% 78%, rgba(186, 230, 253, 0.5), transparent 32%),
      linear-gradient(135deg, #eef4fb 0%, #e7edf6 48%, #f5f7fb 100%);
}

.login-bg {
  position: absolute;
  inset: 0;
  z-index: 0;
  background:
      radial-gradient(circle at 15% 75%, rgba(64, 158, 255, 0.14), transparent 30%),
      radial-gradient(circle at 85% 20%, rgba(45, 212, 191, 0.12), transparent 28%);
}

.login-bg::before,
.login-bg::after {
  content: "";
  position: absolute;
  border-radius: 999px;
  background: #edf3fa;
  box-shadow:
      14px 14px 32px rgba(163, 177, 198, 0.28),
      -14px -14px 32px rgba(255, 255, 255, 0.85);
}

.login-bg::before {
  width: 220px;
  height: 220px;
  left: 8%;
  top: 14%;
}

.login-bg::after {
  width: 300px;
  height: 300px;
  right: 7%;
  bottom: 10%;
}

.login-card {
  width: 450px;
  padding: 34px;
  border-radius: 28px;
  position: relative;
  z-index: 1;
  border: 1px solid rgba(255, 255, 255, 0.72);
  background: linear-gradient(145deg, #f7faff, #e8eef7);
  box-shadow:
      22px 22px 48px rgba(163, 177, 198, 0.42),
      -22px -22px 48px rgba(255, 255, 255, 0.92),
      inset 1px 1px 1px rgba(255, 255, 255, 0.75);
  transition: all 200ms ease;
}

.login-card:hover {
  transform: translateY(-4px);
  box-shadow:
      28px 28px 58px rgba(163, 177, 198, 0.5),
      -24px -24px 52px rgba(255, 255, 255, 0.96),
      inset 1px 1px 1px rgba(255, 255, 255, 0.85);
}

.login-header {
  text-align: center;
  margin-bottom: 28px;
}

.logo {
  width: 68px;
  height: 68px;
  margin: 0 auto 18px;
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #409eff;
  font-size: 28px;
  background: linear-gradient(145deg, #f8fbff, #e1e8f2);
  box-shadow:
      10px 10px 22px rgba(163, 177, 198, 0.42),
      -10px -10px 22px rgba(255, 255, 255, 0.95),
      inset 1px 1px 1px rgba(255, 255, 255, 0.85);
  transition: all 180ms ease;
}

.logo:hover {
  transform: translateY(-2px);
  color: #2dd4bf;
}

.logo-icon {
  filter: drop-shadow(1px 2px 2px rgba(64, 158, 255, 0.18));
}

.login-header h2 {
  font-size: 24px;
  color: #273449;
  margin: 0 0 10px;
  font-weight: 700;
  letter-spacing: 0.04em;
}

.login-desc {
  font-size: 14px;
  color: #7b8798;
  margin: 0;
  letter-spacing: 0.06em;
}

.login-footer {
  text-align: center;
  font-size: 12px;
  color: #9aa6b8;
  margin-top: 24px;
}

/* Element Plus 表单软 UI 适配 */
:deep(.el-form) {
  position: relative;
  z-index: 1;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  border-radius: 16px;
  background: #eef3fa;
  box-shadow:
      inset 6px 6px 12px rgba(163, 177, 198, 0.28),
      inset -6px -6px 12px rgba(255, 255, 255, 0.86);
  transition: all 180ms ease;
}

:deep(.el-input__wrapper:hover),
:deep(.el-select__wrapper:hover) {
  transform: translateY(-1px);
  box-shadow:
      inset 5px 5px 10px rgba(163, 177, 198, 0.24),
      inset -5px -5px 10px rgba(255, 255, 255, 0.9),
      0 8px 18px rgba(163, 177, 198, 0.18);
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-select__wrapper.is-focused) {
  box-shadow:
      inset 4px 4px 8px rgba(163, 177, 198, 0.22),
      inset -4px -4px 8px rgba(255, 255, 255, 0.92),
      0 0 0 2px rgba(64, 158, 255, 0.18);
}

:deep(.el-input__inner) {
  color: #2f3b52;
}

:deep(.el-input__inner::placeholder) {
  color: #a0abbb;
}

:deep(.el-button) {
  border-radius: 999px;
  transition: all 180ms ease;
}

:deep(.el-button--primary) {
  border: none;
  background: linear-gradient(145deg, #5db2ff, #2f91ee);
  box-shadow:
      8px 8px 18px rgba(64, 158, 255, 0.26),
      -8px -8px 18px rgba(255, 255, 255, 0.82),
      inset 1px 1px 1px rgba(255, 255, 255, 0.38);
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  filter: brightness(1.04);
  box-shadow:
      10px 10px 22px rgba(64, 158, 255, 0.32),
      -8px -8px 18px rgba(255, 255, 255, 0.9);
}

:deep(.el-button--primary:active) {
  transform: translateY(1px) scale(0.98);
  box-shadow:
      inset 5px 5px 10px rgba(38, 115, 190, 0.36),
      inset -5px -5px 10px rgba(130, 198, 255, 0.42);
}

@media (max-width: 520px) {
  .login-card {
    width: calc(100% - 36px);
    padding: 28px 22px;
  }

  .login-header h2 {
    font-size: 21px;
  }
}
</style>