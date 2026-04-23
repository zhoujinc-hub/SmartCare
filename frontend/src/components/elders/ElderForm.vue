<template>
  <el-dialog v-model="dialogVisible" title="老人信息" width="600px">
    <el-form ref="formRef" :model="form" label-width="100px" :rules="rules">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入姓名" />
      </el-form-item>

      <el-form-item label="性别" prop="gender">
        <el-radio-group v-model="form.gender">
          <el-radio :label="0">女</el-radio>
          <el-radio :label="1">男</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="年龄" prop="age">
        <el-input v-model.number="form.age" placeholder="请输入年龄" />
      </el-form-item>

      <el-form-item label="家庭住址">
        <el-input v-model="form.address" placeholder="请输入家庭住址" />
      </el-form-item>

      <el-form-item label="联系人1">
        <el-input v-model="form.familyContact1" placeholder="联系人1" />
      </el-form-item>

      <el-form-item label="电话1">
        <el-input v-model="form.familyPhone1" placeholder="电话1" />
      </el-form-item>

      <el-form-item label="联系人2">
        <el-input v-model="form.familyContact2" placeholder="联系人2" />
      </el-form-item>

      <el-form-item label="电话2">
        <el-input v-model="form.familyPhone2" placeholder="电话2" />
      </el-form-item>

      <el-form-item label="身体条件备注">
        <el-input
            v-model="form.healthNotes"
            type="textarea"
            :rows="3"
            placeholder="请输入身体状况、病史等备注"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="submit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { addElder, updateElder, getElderDetail } from '@/api/elder'
import type { ElderItem } from '@/types/elder'

const props = defineProps<{
  modelValue: boolean
  elderId?: number
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', val: boolean): void
  (e: 'success'): void
}>()

const dialogVisible = ref(false)
const formRef = ref<any>(null)
const form = ref<ElderItem>({
  elderId: 0,
  name: '',
  gender: 0,
  age: 0,
  address: '',
  familyContact1: '',
  familyPhone1: '',
  familyContact2: '',
  familyPhone2: '',
  healthNotes: '',
  createdAt: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  age: [{ required: true, message: '请输入年龄', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }]
}

// 监听弹窗打开 → 编辑时自动回填数据
watch(() => props.modelValue, async (val) => {
  dialogVisible.value = val

  if (val) {
    if (props.elderId && props.elderId > 0) {
      try {
        const res = await getElderDetail(props.elderId)
        if (res?.data) {
          form.value = { ...res.data }
        }
      } catch {
        ElMessage.error('加载详情失败')
      }
    } else {
      resetForm()
    }
  }
}, { immediate: true })

// 关闭弹窗清空表单
watch(() => dialogVisible.value, (val) => {
  emit('update:modelValue', val)
  if (!val) {
    resetForm()
  }
})

// 重置表单
function resetForm() {
  form.value = {
    elderId: 0,
    name: '',
    gender: 0,
    age: 0,
    address: '',
    familyContact1: '',
    familyPhone1: '',
    familyContact2: '',
    familyPhone2: '',
    healthNotes: '',
    createdAt: ''
  }
}

// 提交
async function submit() {
  try {
    await formRef.value?.validate()

    if (props.elderId && props.elderId > 0) {
      await updateElder(form.value)
      ElMessage.success('修改成功')
    } else {
      await addElder(form.value)
      ElMessage.success('新增成功')
    }

    emit('success')
    dialogVisible.value = false
  } catch {
    ElMessage.error('提交失败')
  }
}
</script>