<template>
  <el-dialog
    :title="isEdit ? '编辑老人信息' : '新增老人信息'"
    :model-value="modelValue"
    @update:model-value="handleClose"
    width="700px"
    destroy-on-close
    center
    custom-class="elder-dialog"
  >
    <el-form :model="form" :rules="formRules" ref="formRef" label-width="120px">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入老人姓名" />
      </el-form-item>
      <el-form-item label="性别" prop="gender">
        <el-select v-model="form.gender" placeholder="请选择性别">
          <el-option label="男" :value="1" />
          <el-option label="女" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="年龄" prop="age">
        <el-input v-model="form.age" type="number" placeholder="请输入老人年龄" min="0" max="120" />
      </el-form-item>
      <el-form-item label="家庭地址" prop="address">
        <el-input v-model="form.address" placeholder="请输入家庭详细地址" />
      </el-form-item>
      <el-form-item label="健康备注" prop="health_notes">
        <el-input
          v-model="form.health_notes"
          type="textarea"
          placeholder="请输入老人健康状况备注（如疾病、用药等）"
          :rows="4"
        />
      </el-form-item>
      <el-form-item label="关联家属" prop="relativeIds">
        <el-select v-model="form.relativeIds" multiple placeholder="选择关联的家属（可多选）">
          <el-option
            v-for="relative in relativeList"
            :key="relative.user_id"
            :label="`${relative.real_name} (${relative.phone})`"
            :value="relative.user_id"
          />
        </el-select>
        <div style="margin-top: 8px; font-size: 12px; color: #666">
          注：家属信息关联users表（user_type=2）
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">提交</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, shallowRef, watch } from 'vue';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import type { ElderFormData } from '@/types/elder';
import type { UserItem } from '@/types/user';
import { addElder, updateElder } from '@/api/elder';

// Props
const props = defineProps<{
  modelValue: boolean;
  isEdit: boolean;
  formData: ElderFormData;
  relativeList: UserItem[];
}>();

// Emits
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'submitSuccess'): void;
}>();

// 表单引用
const formRef = shallowRef<FormInstance | null>(null);
// 本地表单数据（避免直接修改props）
const form = reactive<ElderFormData>({ ...props.formData });

// 表单校验规则
const formRules = reactive<FormRules<ElderFormData>>({
  name: [{ required: true, message: '请输入老人姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  age: [
    { required: true, message: '请输入老人年龄', trigger: 'blur' },
    { type: 'number', min: 0, max: 120, message: '年龄范围0-120岁', trigger: 'blur' }
  ],
  address: [{ required: true, message: '请输入家庭地址', trigger: 'blur' }]
});

// 监听props.formData变化，同步本地表单
watch(
  () => props.formData,
  (newVal) => {
    Object.assign(form, newVal);
  },
  { immediate: true, deep: true }
);

// 取消按钮
const handleClose = () => {
  emit('update:modelValue', false);
  resetForm();
};

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return;
  try {
    // 表单校验
    await formRef.value.validate();
    
    let success = false;
    if (props.isEdit) {
      // 编辑模式
      const res = await updateElder(form);
      success = res.code === 200;
      if (success) ElMessage.success('编辑老人信息成功');
      else ElMessage.error(res.message || '编辑老人信息失败');
    } else {
      // 新增模式
      const res = await addElder(form);
      success = res.code === 200;
      if (success) ElMessage.success('新增老人信息成功');
      else ElMessage.error(res.message || '新增老人信息失败');
    }

    if (success) {
      emit('update:modelValue', false);
      emit('submitSuccess');
      resetForm();
    }
  } catch (error) {
    ElMessage.error('表单校验失败，请检查输入内容！');
  }
};

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  Object.assign(form, {
    elder_id: 0,
    name: '',
    gender: 0,
    age: 0,
    address: '',
    health_notes: '',
    relativeIds: []
  });
};
</script>

<style scoped>
:deep(.elder-dialog) {
  --el-dialog-bg-color: #ffffff;
  --el-dialog-title-color: #1f2937;
  --el-dialog-border-color: #e6e6e6;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid #e6e6e6;
  padding-bottom: 12px;
}

:deep(.el-dialog__footer) {
  border-top: 1px solid #e6e6e6;
  padding-top: 12px;
}

:deep(.el-form-item__label) {
  color: #1f2937 !important;
}

:deep(.el-input__inner) {
  background-color: #ffffff;
  border-color: #e6e6e6;
  color: #333333;
}

:deep(.el-select .el-input__inner) {
  background-color: #ffffff;
  border-color: #e6e6e6;
  color: #333333;
}

:deep(.el-select-dropdown) {
  background-color: #ffffff;
  border-color: #e6e6e6;
}

:deep(.el-select-dropdown__item) {
  color: #333333;
}

:deep(.el-select-dropdown__item:hover) {
  background-color: #f0f9ff;
}
</style>