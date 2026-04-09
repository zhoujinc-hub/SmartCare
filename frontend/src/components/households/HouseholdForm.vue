<template>
  <el-dialog
    :title="isEdit ? '编辑家庭' : '新增家庭'"
    :model-value="modelValue"
    @update:model-value="handleClose"
    width="650px"
    destroy-on-close
    center
  >
    <el-form :model="form" :rules="formRules" ref="formRef" label-width="130px">
      <el-form-item label="户主姓名" prop="householdName">
        <el-input v-model="form.householdName" />
      </el-form-item>

      <el-form-item label="详细地址" prop="address">
        <el-input v-model="form.address" />
      </el-form-item>

      <el-form-item label="联系人1" prop="contact1_name">
        <el-input v-model="form.contact1_name" />
      </el-form-item>

      <el-form-item label="电话1" prop="contact1_phone">
        <el-input v-model="form.contact1_phone" />
      </el-form-item>

      <el-form-item label="联系人2">
        <el-input v-model="form.contact2_name" />
      </el-form-item>

      <el-form-item label="电话2">
        <el-input v-model="form.contact2_phone" />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">提交</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, ref, watch } from 'vue';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import type { HouseholdFormData } from '@/types/household';
import { addHousehold, updateHousehold } from '@/api/household';

const props = defineProps<{
  modelValue: boolean;
  isEdit: boolean;
  formData: HouseholdFormData;
  householdId?: number;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'submitSuccess'): void;
}>();

const formRef = ref<FormInstance | null>(null);
const form = reactive<HouseholdFormData>({ ...props.formData });

const formRules = reactive<FormRules<HouseholdFormData>>({
  householdName: [{ required: true, message: '请输入户主姓名', trigger: 'blur' }],
  address: [{ required: true, message: '请输入地址', trigger: 'blur' }],
  contact1_name: [{ required: true, message: '请输入联系人1', trigger: 'blur' }],
  contact1_phone: [{ required: true, message: '请输入电话1', trigger: 'blur' }]
});

watch(() => props.formData, () => {
  Object.assign(form, props.formData);
}, { immediate: true, deep: true });

const handleClose = () => {
  emit('update:modelValue', false);
  resetForm();
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
    let res;
    if (props.isEdit && props.householdId) {
      res = await updateHousehold(props.householdId, form);
    } else {
      res = await addHousehold(form);
    }
    if (res.code === 200) {
      ElMessage.success(props.isEdit ? '编辑成功' : '新增成功');
      emit('submitSuccess');
      emit('update:modelValue', false);
    } else {
      ElMessage.error(res.message || '操作失败');
    }
  } catch (err) {
    ElMessage.error('表单校验失败');
  }
};

const resetForm = () => {
  if (formRef.value) formRef.value.resetFields();
  Object.assign(form, props.formData);
};
</script>

<style scoped>
:deep(.el-form-item__label) { color: #1f2937; }
</style>