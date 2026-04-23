<template>
  <el-dialog v-model="visible" title="跌倒事件详情" width="700px">
    <el-descriptions :column="2" border>
      <el-descriptions-item label="事件ID">{{ detail.eventId }}</el-descriptions-item>
      <el-descriptions-item label="摄像头ID">{{ detail.cameraId }}</el-descriptions-item>
      <el-descriptions-item label="老人姓名">{{ detail.elderName || '路人' }}</el-descriptions-item>
      <el-descriptions-item label="注册状态">
        <el-tag :type="detail.isRegistered === 1 ? 'success' : 'info'">
          {{ detail.isRegistered === 1 ? '已注册' : '未注册' }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="跌倒时间">{{ detail.fallTime }}</el-descriptions-item>
      <el-descriptions-item label="检测时间">{{ detail.detectTime }}</el-descriptions-item>
      <el-descriptions-item label="置信度">{{ detail.confidence }}</el-descriptions-item>
      <el-descriptions-item label="处理状态">
        <el-tag :type="getStatusTagType(detail.status)">
          {{ getStatusText(detail.status) }}
        </el-tag>
      </el-descriptions-item>

      <el-descriptions-item label="视频路径" :span="2">
        <el-link
            :href="detail.videoPath"
            type="primary"
            target="_blank"
            :underline="false"
            :disabled="!detail.videoPath"
        >
          {{ detail.videoPath || '无视频' }}
        </el-link>
      </el-descriptions-item>

      <el-descriptions-item label="处理备注" :span="2">
        {{ detail.processNotes || '无备注' }}
      </el-descriptions-item>
    </el-descriptions>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { FallEventVo } from '@/types/fallEvent'

const props = defineProps({
  visible: Boolean,
  detail: {
    type: Object as () => FallEventVo,
    default: () => ({})
  }
})

const emit = defineEmits(['update:visible'])

const visible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val)
})

const getStatusTagType = (status: number) => {
  switch (status) {
    case 1: return 'warning'
    case 2: return 'success'
    case 3: return 'danger'
    default: return 'info'
  }
}

const getStatusText = (status: number) => {
  switch (status) {
    case 1: return '待处理'
    case 2: return '已处理'
    case 3: return '误报'
    default: return '未知'
  }
}
</script>