<template>
  <el-dialog
    :title="isEdit ? '编辑摄像头信息' : '新增摄像头信息'"
    :model-value="modelValue"
    @update:model-value="handleClose"
    width="700px"
    destroy-on-close
    center
    custom-class="camera-dialog"
  >
    <el-form :model="form" :rules="formRules" ref="formRef" label-width="120px">
      <el-form-item label="设备名称" prop="camera_name">
        <el-input v-model="form.camera_name" placeholder="请输入摄像头名称（如：客厅摄像头）" />
      </el-form-item>
      <el-form-item label="摄像头类型" prop="camera_type">
        <el-select v-model="form.camera_type" placeholder="请选择摄像头类型">
          <el-option label="家庭摄像头" :value="0" />
          <el-option label="社区摄像头" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="所属家庭" prop="householdId">
        <el-select v-model="form.householdId" placeholder="请选择所属家庭">
          <el-option v-for="item in householdList" :key="item.householdId" :label="`${item.householdName}(${item.address})`" :value="item.householdId" />
        </el-select>
      </el-form-item>
      <el-form-item label="安装位置" prop="location_desc">
        <el-select v-model="form.location_desc" placeholder="请选择安装位置">
          <el-option label="客厅" value="客厅" />
          <el-option label="卧室" value="卧室" />
          <el-option label="阳台" value="阳台" />
          <el-option label="卫生间" value="卫生间" />
          <el-option label="厨房" value="厨房" />
          <el-option label="其他" value="其他" />
        </el-select>
      </el-form-item>
      <el-form-item label="设备序列号" prop="device_serial">
        <el-input v-model="form.device_serial" placeholder="请输入设备序列号（唯一标识）" />
      </el-form-item>
      <el-form-item label="视频流地址" prop="stream_url">
        <el-input v-model="form.stream_url" placeholder="请输入RTSP/RTMP视频流地址" />
      </el-form-item>
      <el-form-item label="设备状态" prop="status">
        <el-select v-model="form.status" placeholder="请选择设备状态">
          <el-option label="在线" :value="1" />
          <el-option label="离线" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="纬度" prop="latitude">
        <el-input v-model="form.latitude" type="number" placeholder="请输入设备安装纬度（选填）" />
      </el-form-item>
      <el-form-item label="经度" prop="longitude">
        <el-input v-model="form.longitude" type="number" placeholder="请输入设备安装经度（选填）" />
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
// 修复：补充完整导入 + 优化类型定义 + 解决未使用变量警告
import { reactive, shallowRef, watch } from 'vue';
import { ElMessage, type FormInstance, type FormRules } from 'element-plus';
import type { CameraFormData, HouseholdItem } from '@/types/camera';
import { addCamera, updateCamera } from '@/api/camera';

// 修复：添加 eslint 注释忽略未使用变量警告
const props = defineProps<{
  modelValue: boolean;
  isEdit: boolean;
  formData: CameraFormData;
  householdList: HouseholdItem[];
  editCameraId?: number;
}>(); // eslint-disable-line @typescript-eslint/no-unused-vars

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'submitSuccess'): void;
}>();

const formRef = shallowRef<FormInstance | null>(null);
// 修复：初始化表单数据时避免空对象问题
const form = reactive<CameraFormData>({
  camera_id: 0,
  camera_type: 0,
  camera_name: '',
  device_serial: '',
  stream_url: '',
  location_desc: '',
  latitude: '',
  longitude: '',
  status: 1,
  householdId: 0,
  remark: ''
});

// 修复：表单规则类型严格匹配
const formRules = reactive<FormRules<CameraFormData>>({
  camera_name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  camera_type: [{ required: true, message: '请选择摄像头类型', trigger: 'change' }],
  householdId: [{ required: true, message: '请选择所属家庭', trigger: 'change' }],
  location_desc: [{ required: true, message: '请选择安装位置', trigger: 'change' }],
  device_serial: [
    { required: true, message: '请输入设备序列号', trigger: 'blur' },
    { pattern: /^[A-Za-z0-9]{8,20}$/, message: '序列号为8-20位字母数字组合', trigger: 'blur' }
  ],
  stream_url: [{ required: true, message: '请输入视频流地址', trigger: 'blur' }],
  status: [{ required: true, message: '请选择设备状态', trigger: 'change' }]
});

// 修复：优化 watch 监听逻辑，避免深层监听性能问题
watch(() => props.formData, (newVal) => {
  if (newVal) {
    Object.assign(form, newVal);
  }
}, { immediate: true });

const handleClose = () => {
  emit('update:modelValue', false);
  resetForm();
};

// 修复：接口调用错误处理优化
const handleSubmit = async () => {
  if (!formRef.value) return;
  try {
    await formRef.value.validate();
    let success = false;
    const res = props.isEdit && props.editCameraId 
      ? await updateCamera(form) 
      : await addCamera(form);
    
    if (res.code === 200) {
      success = true;
      ElMessage.success(props.isEdit ? '编辑摄像头成功！' : '新增摄像头成功！');
    } else {
      ElMessage.error(res.message || (props.isEdit ? '编辑摄像头失败！' : '新增摄像头失败！'));
    }

    if (success) {
      emit('update:modelValue', false);
      emit('submitSuccess');
      resetForm();
    }
  } catch (error) {
    ElMessage.error('表单校验失败，请检查输入内容！');
    console.error('表单提交失败：', error);
  }
};

// 修复：重置表单逻辑优化
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields();
  }
  Object.assign(form, {
    camera_id: 0,
    camera_type: 0,
    camera_name: '',
    device_serial: '',
    stream_url: '',
    location_desc: '',
    latitude: '',
    longitude: '',
    status: 1,
    householdId: 0,
    remark: ''
  });
};
</script>

<style scoped>
:deep(.camera-dialog) {
  --el-dialog-bg-color: #ffffff;
  --el-dialog-title-color: #1f2937;
  --el-dialog-border-color: #e6e6e6;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
:deep(.el-dialog__header) { border-bottom: 1px solid #e6e6e6; padding-bottom: 12px; }
:deep(.el-dialog__footer) { border-top: 1px solid #e6e6e6; padding-top: 12px; }
:deep(.el-form-item__label) { color: #1f2937 !important; }
:deep(.el-input__inner) { background-color: #ffffff; border-color: #e6e6e6; color: #333333; }
</style>