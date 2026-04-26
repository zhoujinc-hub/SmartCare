<template>
  <el-form
      :model="form"
      :rules="rules"
      ref="formRef"
      label-width="80px"
      class="login-form"
  >
    <el-form-item label="账号" prop="username">
      <el-input
          v-model="form.username"
          placeholder="请输入登录账号"
          prefix="User"
          size="large"
      ></el-input>
    </el-form-item>

    <el-form-item label="密码" prop="password">
      <el-input
          v-model="form.password"
          type="password"
          placeholder="请输入密码"
          prefix="Lock"
          size="large"
      ></el-input>
    </el-form-item>

    <el-form-item label="用户类型" prop="userType">
      <el-select
          v-model="form.userType"
          placeholder="请选择用户类型"
          size="large"
      >
        <el-option label="家属" :value="2" />
        <el-option label="管理员" :value="1" />
      </el-select>
    </el-form-item>

    <div class="login-form-actions">
      <el-checkbox v-model="form.rememberMe" label="记住密码" size="small"></el-checkbox>
      <el-button type="text" @click="onForgotPwd" size="small">忘记密码？</el-button>
    </div>

    <el-form-item>
      <el-button
          type="primary"
          @click="onLogin"
          class="login-btn"
          size="large"
          :loading="loading"
      >
        <el-icon v-if="loading" class="loading-icon"><Loading /></el-icon>
        登录系统
      </el-button>
    </el-form-item>

    <div class="register-row">
      <span>还没有账号？</span>
      <el-button type="text" @click="goRegister" class="register-btn">立即注册</el-button>
    </div>
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import type { LoginForm, LoginResponse } from '@/types/user'
import { userLogin } from '@/api/user'
import { setToken, setUserInfo, setRememberUser, clearRememberUser } from '@/utils/auth'

const props = defineProps({
  initForm: {
    type: Object as () => LoginForm,
    default: () => ({
      username: '',
      password: '',
      userType: 1,
      rememberMe: false
    })
  }
})

const emit = defineEmits<{
  'login-success': [userType: number]
  'forgot-pwd': []
  'register': []
}>()

const formRef = ref<FormInstance>()
const loading = ref(false)
const form = reactive<LoginForm>({ ...props.initForm })

const rules: FormRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
  ],
  userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }]
}

const onLogin = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true

    const { username, password, userType } = form
    const response = await userLogin({ username, password, userType })

    console.log('登录接口返回（原始响应）：', response)

    const resData = response.data as LoginResponse
    console.log('登录接口返回（data部分）：', resData)

    if (!resData?.token) {
      ElMessage.error('登录失败：未返回token')
      return
    }

    setToken(resData.token)
    setUserInfo(resData)
    ElMessage.success('登录成功！')

    if (form.rememberMe) {
      setRememberUser({ username, userType })
    } else {
      clearRememberUser()
    }

    emit('login-success', userType)

  } catch (err) {
    console.error(err)
    ElMessage.error('网络异常')
  } finally {
    loading.value = false
  }
}

const onForgotPwd = () => {
  emit('forgot-pwd')
}

const goRegister = () => {
  emit('register')
}

defineExpose({
  resetForm: () => formRef.value?.resetFields()
})
</script>

<style scoped>
.login-form {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

:deep(.el-form-item) {
  margin-bottom: 20px;
  width: 100%;
}

.login-form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  gap: 20px;
}

.login-btn {
  width: 300px;
  height: 44px;
  font-size: 16px;
}

.loading-icon {
  margin-right: 8px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.register-row {
  margin-top: 8px;
  font-size: 14px;
  color: #666;
}
.register-btn {
  padding: 0 4px;
  color: #409eff;
}
</style>