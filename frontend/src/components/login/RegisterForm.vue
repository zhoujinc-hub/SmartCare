<template>
  <el-dialog
      title="注册账号"
      v-model="visible"
      width="450px"
      destroy-on-close
      center
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="账号" prop="username">
        <el-input v-model="form.username" placeholder="请输入账号"></el-input>
      </el-form-item>

      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号"></el-input>
      </el-form-item>

      <el-form-item label="验证码" prop="code">
        <el-row :gutter="12">
          <el-col :span="14">
            <el-input v-model="form.code" placeholder="请输入验证码"></el-input>
          </el-col>
          <el-col :span="10">
            <el-button
                type="text"
                class="code-btn"
                :disabled="countdown > 0"
                @click="sendCode"
            >
              {{ countdown > 0 ? `${countdown}秒后重发` : '获取验证码' }}
            </el-button>
          </el-col>
        </el-row>
      </el-form-item>

      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" type="password" placeholder="请输入密码"></el-input>
      </el-form-item>

      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码"></el-input>
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="onRegister" :loading="loading">确认注册</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { registerUser, sendVerifyCode } from '@/api/user'

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'register-success': []
}>()

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const formRef = ref<FormInstance>()
const loading = ref(false)
const countdown = ref(0)

// 用户类型固定为家属（2），前端不提供选择
const form = reactive({
  username: '',
  phone: '',
  code: '',
  password: '',
  confirmPassword: '',
  userType: 2 // 固定为家属，无法修改
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_, value, callback) => {
        if (value !== form.password) {
          callback(new Error('两次密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// 发送验证码（复用忘记密码的接口）
const sendCode = async () => {
  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  try {
    const res = await sendVerifyCode(form.phone) as unknown as any
    if (res.code === 200) {
      ElMessage.success('验证码已发送')
      countdown.value = 60
      const timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) clearInterval(timer)
      }, 1000)
    } else {
      ElMessage.error(res.message || '发送失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络异常')
  }
}

const onRegister = async () => {
  try {
    await formRef.value?.validate()
    loading.value = true

    const res = await registerUser({
      username: form.username,
      phone: form.phone,
      code: form.code,
      userType: form.userType, // 固定为家属
      password: form.password
    })

    if (res.code === 200) {
      ElMessage.success('注册成功！')
      visible.value = false
      emit('register-success')
      formRef.value?.resetFields()
    } else {
      ElMessage.error(res.message || '注册失败')
    }
  } catch (err) {
    console.error(err)
    ElMessage.error('网络异常')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.code-btn {
  width: 100%;
  color: #409eff;
  border: 1px solid #e6f7ff;
  background-color: #f0f9ff;
}
</style>