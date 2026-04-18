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
        <el-input v-model="form.family_contact1" placeholder="联系人1" />
      </el-form-item>

      <el-form-item label="电话1">
        <el-input v-model="form.family_phone1" placeholder="电话1" />
      </el-form-item>

      <el-form-item label="联系人2">
        <el-input v-model="form.family_contact2" placeholder="联系人2" />
      </el-form-item>

      <el-form-item label="电话2">
        <el-input v-model="form.family_phone2" placeholder="电话2" />
      </el-form-item>

      <el-form-item label="身体条件备注">
        <el-input
            v-model="form.physical_notes"
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
const formRef = ref()
const form = ref<ElderItem>({
  elder_id: 0,
  name: '',
  gender: 0,
  age: 0,
  address: '',
  family_contact1: '',
  family_phone1: '',
  family_contact2: '',
  family_phone2: '',
  physical_notes: null,
  created_at: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  age: [{ required: true, message: '请输入年龄', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }]
}

watch(() => props.modelValue, async (val) => {
  dialogVisible.value = val
  if (val && props.elderId) {
    try {
      const res = await getElderDetail(props.elderId)
      form.value = { ...res.data }
    } catch {
      ElMessage.error('加载详情失败')
    }
  }
  if (!val) {
    form.value = {
      elder_id: 0,
      name: '',
      gender: 0,
      age: 0,
      address: '',
      family_contact1: '',
      family_phone1: '',
      family_contact2: '',
      family_phone2: '',
      physical_notes: null,
      created_at: ''
    }
  }
}, { immediate: true })

watch(() => dialogVisible.value, (val) => emit('update:modelValue', val))

async function submit() {
  try {
    await formRef.value.validate()
    if (props.elderId) {
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