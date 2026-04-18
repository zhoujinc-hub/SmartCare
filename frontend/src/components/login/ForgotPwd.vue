<template>
  <el-dialog
      title="忘记密码"
      :model-value="modelValue"
      @update:model-value="emit('update:modelValue', $event)"
      width="400px"
      destroy-on-close
      center
      @close="handleClose"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入注册手机号"></el-input>
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
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="form.newPassword" type="password" placeholder="请输入新密码"></el-input>
      </el-form-item>
      <el-form-item label="确认密码" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入新密码"></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="emit('update:modelValue', false)">取消</el-button>
      <el-button type="primary" @click="resetPassword">确认重置</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import type { ForgotForm, CodeResponse, ResetPwdResponse } from '@/types/user'
import { sendVerifyCode, resetUserPassword } from '@/api/user'

const props = defineProps<{
  modelValue: boolean
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'reset-success'): void
}>()

const formRef = ref<FormInstance>()
const countdown = ref(0)
const form = reactive<ForgotForm>({
  phone: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
})

const rules: FormRules = {
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确手机号', trigger: 'blur' }
  ],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (_, value, callback) => {
        if (value !== form.newPassword) {
          callback(new Error('两次密码输入不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

// ✅ 修复：直接使用类型断言，避免和 request.ts 的响应拦截器冲突
const sendCode = async () => {
  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    ElMessage.warning('请输入正确的手机号')
    return
  }
  try {
    // 用 unknown 中转，避免类型冲突
    const res = await sendVerifyCode(form.phone) as unknown as CodeResponse
    if (res.code === 200) {
      ElMessage.success('验证码已发送至您的手机')
      countdown.value = 60
      const timer = setInterval(() => {
        countdown.value--
        if (countdown.value <= 0) clearInterval(timer)
      }, 1000)
    } else {
      ElMessage.error(res.message || '验证码发送失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络异常，验证码发送失败')
  }
}

const resetPassword = async () => {
  try {
    await formRef.value?.validate()
    const res = await resetUserPassword({
      phone: form.phone,
      code: form.code,
      newPassword: form.newPassword
    }) as unknown as ResetPwdResponse

    if (res.code === 200) {
      ElMessage.success('密码重置成功，请重新登录')
      emit('update:modelValue', false)
      emit('reset-success')
    } else {
      ElMessage.error(res.message || '密码重置失败')
    }
  } catch (error) {
    console.error(error)
    ElMessage.error('网络异常，密码重置失败')
  }
}

const handleClose = () => {
  form.phone = ''
  form.code = ''
  form.newPassword = ''
  form.confirmPassword = ''
  countdown.value = 0
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