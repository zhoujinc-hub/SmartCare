<template>
  <el-dialog title="摄像头详情" v-model="dialogVisible" width="700px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="摄像头ID">{{ detailData?.cameraId ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="设备名称">{{ detailData?.cameraName ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="摄像头类型">
        {{ detailData?.cameraType === 0 ? '家庭' : '社区' }}
      </el-descriptions-item>
      <el-descriptions-item label="设备序列号">{{ detailData?.deviceSerial ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="视频流地址" :span="2">{{ detailData?.streamUrl ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="安装位置">{{ detailData?.locationDesc ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="设备状态">
        <el-tag :type="detailData?.status === 1 ? 'success' : 'danger'">
          {{ detailData?.status === 1 ? '在线' : '离线' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="纬度">{{ detailData?.latitude ?? '-' }}</el-descriptions-item>
      <el-descriptions-item label="经度">{{ detailData?.longitude ?? '-' }}</el-descriptions-item>
    </el-descriptions>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { CameraItem } from '@/types/camera'

const props = defineProps({
  modelValue: Boolean,
  detailData: {
    type: Object as () => CameraItem,
    default: () => ({})
  }
})
const emit = defineEmits(['update:modelValue'])

const dialogVisible = computed({
  get() { return props.modelValue },
  set(val) { emit('update:modelValue', val) }
})
</script>