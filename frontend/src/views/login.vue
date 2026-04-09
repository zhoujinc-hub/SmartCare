<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>智慧社区养老监护系统</h2>
      <el-form 
        :model="loginForm" 
        :rules="loginRules" 
        ref="loginFormRef" 
        label-width="80px"
      >
        <el-form-item label="账号" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入账号"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" class="login-btn">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
// 1. 导入依赖并指定 TS 类型
import { ref, reactive } from 'vue'
import { useRouter, type Router } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import request from '../utils/request'

// 2. 定义类型接口（核心：强类型约束）
/** 登录表单数据类型 */
interface LoginForm {
  username: string
  password: string
}

/** 登录接口返回数据类型 */
interface LoginResponse {
  code: number
  message: string
  data: {
    token: string
    username?: string
    expires?: number
  }
}

// 3. 初始化变量并指定类型
const router: Router = useRouter()
const loginFormRef = ref<FormInstance>() // 表单 Ref 类型

// 登录表单（指定 LoginForm 类型）
const loginForm = reactive<LoginForm>({
  username: '',
  password: ''
})

// 表单校验规则（指定 FormRules 类型）
const loginRules = reactive<FormRules>({
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
})

// 4. 登录方法（完善 TS 类型）
const handleLogin = async (): Promise<void> => {
  try {
    // 表单校验（非空断言 + 类型守卫）
    if (!loginFormRef.value) return
    await loginFormRef.value.validate()

    // 调用登录接口（指定返回值类型为 LoginResponse）
    const res = await request<LoginResponse>({
      url: '/api/user/login',
      method: 'POST',
      data: loginForm
    })

    // 接口返回值校验（TS 类型保护）
    if (res.code === 200 && res.data.token) {
      // 存储 token
      localStorage.setItem('token', res.data.token)
      ElMessage.success('登录成功')
      // 跳转到老人列表页
      await router.push('/elderly/list')
    } else {
      ElMessage.error(res.message || '登录失败，请重试')
    }
  } catch (error: unknown) { // 捕获未知类型错误
    // 错误类型判断（TS 类型收窄）
    if (error instanceof Error) {
      console.error('登录失败：', error.message)
    } else {
      console.error('登录失败：', error)
    }
    ElMessage.error('账号或密码错误')
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f5f5f5;
}
.login-card {
  width: 400px;
  padding: 20px;
}
.login-btn {
  width: 100%;
}
</style>