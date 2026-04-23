<template>
  <el-dialog
      :title="isEdit ? '编辑摄像头' : '新增摄像头'"
      v-model="dialogVisible"
      width="600px"
  >
    <el-form :model="form" :rules="rules" ref="formRef" label-width="110px">
      <el-form-item label="设备名称" prop="cameraName">
        <el-input v-model="form.cameraName" placeholder="请输入设备名称" />
      </el-form-item>
      <el-form-item label="摄像头类型" prop="cameraType">
        <el-select v-model="form.cameraType" placeholder="请选择类型">
          <el-option label="家庭" :value="0" />
          <el-option label="社区" :value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="序列号" prop="deviceSerial">
        <el-input v-model="form.deviceSerial" placeholder="请输入设备序列号" />
      </el-form-item>
      <el-form-item label="视频流地址" prop="streamUrl">
        <el-input v-model="form.streamUrl" placeholder="请输入RTSP/RTMP地址" />
      </el-form-item>
      <el-form-item label="安装位置" prop="locationDesc">
        <el-input v-model="form.locationDesc" placeholder="请输入安装位置" />
      </el-form-item>
      <el-form-item label="设备状态" prop="status">
        <el-select v-model="form.status" placeholder="请选择状态">
          <el-option label="在线" :value="1" />
          <el-option label="离线" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="纬度">
        <el-input v-model.number="form.latitude" placeholder="选填" />
      </el-form-item>
      <el-form-item label="经度">
        <el-input v-model.number="form.longitude" placeholder="选填" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="submitForm">提交</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { addCamera, updateCamera } from '@/api/camera'
import type { CameraAddDto, CameraUpdateDto, CameraBaseResponse } from '@/types/camera'

const props = defineProps({
  modelValue: Boolean,
  isEdit: Boolean,
  formData: {
    type: Object,
    default: () => ({})
  }
})
const emit = defineEmits(['update:modelValue', 'submitSuccess'])

const dialogVisible = computed({
  get() { return props.modelValue },
  set(val) { emit('update:modelValue', val) }
})

const formRef = ref()
const form = ref<CameraUpdateDto>({
  cameraId: 0,
  cameraName: '',
  cameraType: 0,
  deviceSerial: '',
  streamUrl: '',
  locationDesc: '',
  latitude: undefined,
  longitude: undefined,
  status: 1
})

const rules = {
  cameraName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  deviceSerial: [{ required: true, message: '请输入序列号', trigger: 'blur' }]
}

watch(() => props.formData, (val) => {
  if (val) {
    form.value = {
      cameraId: val.cameraId,
      cameraName: val.cameraName,
      cameraType: val.cameraType,
      deviceSerial: val.deviceSerial,
      streamUrl: val.streamUrl,
      locationDesc: val.locationDesc,
      latitude: val.latitude,
      longitude: val.longitude,
      status: val.status
    }
  } else {
    form.value = {
      cameraId: 0,
      cameraName: '',
      cameraType: 0,
      deviceSerial: '',
      streamUrl: '',
      locationDesc: '',
      latitude: undefined,
      longitude: undefined,
      status: 1
    }
  }
}, { immediate: true })

const handleClose = () => {
  dialogVisible.value = false
  formRef.value?.resetFields()
}

const submitForm = async () => {
  try {
    await formRef.value.validate()

    let res: CameraBaseResponse
    if (props.isEdit) {
      res = await updateCamera(form.value as CameraUpdateDto) as unknown as CameraBaseResponse
    } else {
      const addData: CameraAddDto = {
        cameraName: form.value.cameraName,
        cameraType: form.value.cameraType,
        deviceSerial: form.value.deviceSerial,
        streamUrl: form.value.streamUrl,
        locationDesc: form.value.locationDesc,
        latitude: form.value.latitude,
        longitude: form.value.longitude,
        status: form.value.status
      }
      res = await addCamera(addData) as unknown as CameraBaseResponse
    }

    if (res.code === 200) {
      ElMessage.success(props.isEdit ? '编辑成功' : '新增成功')
      handleClose()
      emit('submitSuccess')
    } else {
      ElMessage.error(res.message || '操作失败')
    }
  } catch {
    ElMessage.error('表单校验失败')
  }
}
</script>

<style scoped>
</style>