<template>
  <el-dialog
    title="摄像头实时预览"
    :model-value="modelValue"
    @update:model-value="handleClose"
    width="900px"
    destroy-on-close
    center
  >
    <div class="camera-preview-container">
      <div v-if="previewData.status" class="video-container">
        <video
          id="camera-preview-video"
          controls
          autoplay
          muted
          style="width: 100%; height: 400px; object-fit: cover; border-radius: 8px;"
        >
          <source :src="previewData.stream_url" type="application/x-mpegURL">
          您的浏览器不支持HTML5视频播放
        </video>
        <div class="video-controls">
          <el-button size="small" icon="FullScreen" @click="handleFullScreen">全屏</el-button>
          <el-button size="small" icon="Refresh" @click="handleRestartVideo">刷新视频</el-button>
          <el-button size="small" type="warning" icon="Bell" @click="handleTestAlert">测试报警</el-button>
        </div>
      </div>
      <div v-else class="offline-tip">
        <el-empty description="设备离线，无法预览视频流" />
      </div>
      
      <div class="camera-info-card" style="margin-top: 16px;">
        <el-card>
          <el-descriptions :column="3" border size="small">
            <el-descriptions-item label="设备名称">{{ previewData.camera_name }}</el-descriptions-item>
            <el-descriptions-item label="设备状态">
              <el-tag v-if="previewData.status" type="success">在线</el-tag>
              <el-tag v-else type="danger">离线</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="最后心跳">{{ previewData.lastHeartbeat || '无' }}</el-descriptions-item>
            <el-descriptions-item label="所属家庭">{{ previewData.householdName || '未绑定' }}</el-descriptions-item>
            <el-descriptions-item label="安装位置">{{ previewData.location_desc }}</el-descriptions-item>
            <el-descriptions-item label="摄像头类型">{{ previewData.camera_type === 0 ? '家庭' : '社区' }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </div>
    </div>
    
    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus' // ✅ 补充 ElMessageBox 导入
import type { CameraItem } from '@/types/camera'
import { toggleFullScreen, restartVideo } from '@/utils/camera'

const props = defineProps<{
  modelValue: boolean
  previewData: CameraItem
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
}>()

// ✅ 直接监听 props.previewData，不再重复定义 previewData
watch(() => props.previewData, (newVal) => {
  if (newVal.status) {
    setTimeout(() => {
      const video = document.getElementById('camera-preview-video') as HTMLVideoElement
      if (video) video.load()
    }, 500)
  }
}, { immediate: true })

const handleClose = () => emit('update:modelValue', false)

const handleFullScreen = () => {
  const video = document.getElementById('camera-preview-video') as HTMLVideoElement
  toggleFullScreen(video)
}

const handleRestartVideo = () => {
  const video = document.getElementById('camera-preview-video') as HTMLVideoElement
  restartVideo(video)
  ElMessage.success('视频已刷新！')
}

const handleTestAlert = async () => {
  try {
    await ElMessageBox.confirm(
      '确定要发送测试报警吗？这将触发真实的报警流程！',
      '警告',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    ElMessage.success('测试报警已发送！')
  } catch {
    ElMessage.info('已取消测试报警')
  }
}
</script>

<style scoped>
.video-container { position: relative; }
.video-controls { display: flex; gap: 8px; margin-top: 12px; justify-content: flex-end; }
.offline-tip { display: flex; align-items: center; justify-content: center; height: 400px; background-color: #f5f5f5; border-radius: 8px; }
.camera-info-card { margin-top: 16px; }
</style>