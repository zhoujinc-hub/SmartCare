<template>
  <el-form 
    :model="form" 
    :rules="rules" 
    ref="formRef" 
    label-width="80px"
    class="login-form"
  >
    <!-- 用户名 -->
    <el-form-item label="账号" prop="username">
      <el-input 
        v-model="form.username" 
        placeholder="请输入登录账号"
        prefix="User"
        size="large"
      ></el-input>
    </el-form-item>
    
    <!-- 密码 -->
    <el-form-item label="密码" prop="password">
      <el-input 
        v-model="form.password" 
        type="password" 
        placeholder="请输入密码"
        prefix="Lock"
        size="large"
      ></el-input>
    </el-form-item>
    
    <!-- 用户类型选择 -->
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
    
    <!-- 记住密码+忘记密码 -->
    <div class="login-form-actions">
      <el-checkbox v-model="form.rememberMe" label="记住密码" size="small"></el-checkbox>
      <el-button type="text" @click="onForgotPwd" size="small">忘记密码？</el-button>
    </div>
    
    <!-- 登录按钮 -->
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
  </el-form>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import type { LoginForm } from '@/types/user'
import { userLogin } from '@/api/user'
import { setToken, setUserInfo, setRememberUser, clearRememberUser } from '@/utils/auth'

// Props
const props = defineProps<{
  initForm: LoginForm // 初始化表单数据
}>()

// Emits
const emit = defineEmits<{
  (e: 'login-Success', userType: number): void
  (e: 'forgot-Pwd'): void
}>()

// 表单Ref
const formRef = ref<FormInstance>()
// 加载状态
const loading = ref(false)
// 登录表单
const form = reactive<LoginForm>({
  ...props.initForm
})

// 表单校验规则
const rules = reactive<FormRules>({
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }],
  userType: [{ required: true, message: '请选择用户类型', trigger: 'change' }]
})

// 登录处理
const onLogin = async () => {
  try {
    if (!formRef.value) return
    await formRef.value.validate()

    loading.value = true
    // 调用登录接口
    const res = await userLogin(form)
    
    if (res.code === 200 && res.data.token) {
      // 校验用户状态
      if (res.data.status === 0) {
        ElMessage.error('账号已被禁用，请联系管理员')
        loading.value = false
        return
      }

      // 存储登录态
      setToken(res.data.token)
      setUserInfo({
        userId: res.data.userId,
        username: res.data.username,
        realName: res.data.realName,
        phone: res.data.phone,
        userType: res.data.userType,
        status: res.data.status
      })

      // 记住密码
      if (form.rememberMe) {
        setRememberUser(form.username, form.userType)
      } else {
        clearRememberUser()
      }

      ElMessage.success('登录成功，正在跳转...')
      emit('login-Success', form.userType)
    } else {
      ElMessage.error(res.message || '登录失败，请检查账号密码')
    }
  } catch (error) {
    console.error('登录接口调用异常：', error)
    ElMessage.error('网络异常，登录失败，请重试')
  } finally {
    loading.value = false
  }
}

// 忘记密码
const onForgotPwd = () => {
  emit('forgot-Pwd')
}

// 暴露表单重置方法
defineExpose({
  resetForm: () => {
    if (formRef.value) {
      formRef.value.resetFields()
    }
  }
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

:deep(.el-input__prefix) {
  color: #409eff;
}

.login-form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  gap: 20px; 
}

:deep(.el-checkbox__label) {
  color: #666666;
  font-size: 14px;
}

:deep(.el-button--text) {
  color: #409eff;
  font-size: 14px;
}

.login-btn {
  width: 300px;
  height: 44px;
  font-size: 16px;
  margin-left: -50px; 
  margin-right: 20px;
}

.loading-icon {
  margin-right: 8px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>