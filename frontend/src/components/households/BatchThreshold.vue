<template>
  <el-dialog
    title="批量设置报警阈值"
    :model-value="modelValue"  
    @update:model-value="handleClose" 
    width="400px"
    destroy-on-close
    center
  >
    <el-form :model="form" :rules="formRules" ref="formRef" label-width="100px">
      <el-form-item label="报警阈值(秒)" prop="alertThresholdSeconds">
        <el-input v-model="form.alertThresholdSeconds" type="number" placeholder="请输入统一报警阈值" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确认设置</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, shallowRef } from 'vue';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import type { BatchThresholdForm } from '@/types/household';
import { batchUpdateThreshold } from '@/api/household';

// Props：精简未使用的冗余参数，保留核心
const props = defineProps<{
  modelValue: boolean;
  defaultThreshold: number;
  selectedHouseholdIds: number[];
}>();

// Emits：仅保留必要事件
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'updateSuccess'): void;
}>();

// 表单引用：仅保留使用的formRef，删除未使用的ref/error变量
const formRef = shallowRef<FormInstance | null>(null);

// 表单数据：初始化值从props获取
const form = reactive<BatchThresholdForm>({
  alertThresholdSeconds: props.defaultThreshold
});

// 表单校验规则：保留核心规则，删除冗余
const formRules = reactive<FormRules<BatchThresholdForm>>({
  alertThresholdSeconds: [
    { required: true, message: '请输入报警阈值', trigger: 'blur' },
    { type: 'number', min: 10, max: 3600, message: '阈值范围10-3600秒', trigger: 'blur' }
  ]
});

// 取消按钮：通过emit通知父组件关闭弹窗
const handleClose = () => {
  emit('update:modelValue', false);
};

// 确认设置：保留核心逻辑，删除未使用变量
const handleConfirm = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    
    if (props.selectedHouseholdIds.length === 0) {
      ElMessage.warning('请选择需要设置的家庭！');
      return;
    }
    
    const result = await batchUpdateThreshold(
      props.selectedHouseholdIds,
      form.alertThresholdSeconds
    );
    
    if (result.code === 200) {
      ElMessage.success(`成功为 ${props.selectedHouseholdIds.length} 个家庭更新报警阈值！`);
      emit('update:modelValue', false);
      emit('updateSuccess');
    } else {
      ElMessage.error(result.message || '批量更新阈值失败');
    }
  } catch {
    ElMessage.error('表单校验失败，请检查输入内容！');
  }
};
</script>

<style scoped>
:deep(.el-input__inner) {
  background-color: #ffffff;
  border-color: #e6e6e6;
  color: #333333;
}
</style>