<template>
  <el-dialog
    :title="isEdit ? '编辑家庭信息' : '新增家庭信息'"
    :model-value="modelValue"
    @update:model-value="handleClose"
    width="700px"
    destroy-on-close
    center
    custom-class="household-dialog"
  >
    <el-form :model="form" :rules="formRules" ref="formRef" label-width="120px">
      <el-form-item label="户主姓名" prop="householdName">
        <el-input v-model="form.householdName" placeholder="请输入户主姓名" />
      </el-form-item>
      <el-form-item label="所属社区" prop="communityId">
        <el-select v-model="form.communityId" placeholder="请选择所属社区">
          <el-option 
            v-for="item in communityList" 
            :key="item.communityId" 
            :label="item.communityName" 
            :value="item.communityId" 
          />
        </el-select>
      </el-form-item>
      <el-form-item label="详细地址" prop="address">
        <el-input v-model="form.address" placeholder="请输入家庭详细地址" />
      </el-form-item>
      <el-form-item label="报警阈值(秒)" prop="alertThresholdSeconds">
        <el-input v-model="form.alertThresholdSeconds" type="number" placeholder="请输入报警阈值（秒）" />
        <div style="font-size: 12px; color: #666; margin-top: 4px;">
          提示：社区默认阈值为 {{ defaultCommunityThreshold }} 秒
        </div>
      </el-form-item>
      <el-form-item label="紧急联系人" prop="emergencyContactName">
        <el-input v-model="form.emergencyContactName" placeholder="请输入紧急联系人姓名" />
      </el-form-item>
      <el-form-item label="紧急联系电话" prop="emergencyContactPhone">
        <el-input v-model="form.emergencyContactPhone" placeholder="请输入紧急联系人电话" />
      </el-form-item>
      <el-form-item label="家庭状态" prop="isActive">
        <el-select v-model="form.isActive" placeholder="请选择家庭状态">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注信息" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息（选填）" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleSubmit">提交</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, shallowRef, watch } from 'vue';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import type { HouseholdFormData, CommunityItem } from '@/types/household';
import { addHousehold, updateHousehold } from '@/api/household';

const props = defineProps<{
  modelValue: boolean;
  isEdit: boolean;
  formData: HouseholdFormData;
  communityList: CommunityItem[];
  defaultCommunityThreshold: number;
  editHouseholdId?: number;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'submitSuccess'): void;
}>();

const formRef = shallowRef<FormInstance | null>(null);
const form = reactive<HouseholdFormData>({ ...props.formData });

const formRules = reactive<FormRules<HouseholdFormData>>({
  householdName: [{ required: true, message: '请输入户主姓名', trigger: 'blur' }],
  communityId: [{ required: true, message: '请选择所属社区', trigger: 'change' }],
  address: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
  alertThresholdSeconds: [
    { required: true, message: '请输入报警阈值', trigger: 'blur' },
    { type: 'number', min: 10, max: 3600, message: '阈值范围10-3600秒', trigger: 'blur' }
  ],
  emergencyContactName: [{ required: true, message: '请输入紧急联系人', trigger: 'blur' }],
  emergencyContactPhone: [
    { required: true, message: '请输入紧急联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  isActive: [{ required: true, message: '请选择家庭状态', trigger: 'change' }]
});

watch(() => form.communityId, (newVal) => {
  if (newVal) {
    const community = props.communityList.find(item => item.communityId === newVal);
    if (community && form.alertThresholdSeconds === props.defaultCommunityThreshold) {
      form.alertThresholdSeconds = community.alertThresholdSeconds;
    }
  }
});

watch(() => props.formData, (newVal) => {
  Object.assign(form, newVal);
}, { immediate: true, deep: true });

const handleClose = () => {
  emit('update:modelValue', false);
  resetForm();
};

const handleSubmit = async () => {
  if (!formRef.value) return;
  
  try {
    await formRef.value.validate();
    let result = false;
    
    if (props.isEdit && props.editHouseholdId) {
      result = (await updateHousehold(props.editHouseholdId, form)).code === 200;
      if (result) ElMessage.success('编辑家庭成功！');
    } else {
      result = (await addHousehold(form)).code === 200;
      if (result) ElMessage.success('新增家庭成功！');
    }
    
    if (result) {
      emit('update:modelValue', false);
      emit('submitSuccess');
      resetForm();
    } else {
      ElMessage.error(props.isEdit ? '编辑家庭失败' : '新增家庭失败');
    }
  } catch {
    ElMessage.error('表单校验失败，请检查输入内容！');
  }
};

const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  Object.assign(form, props.formData);
};
</script>

<style scoped>
:deep(.household-dialog) {
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
</style>