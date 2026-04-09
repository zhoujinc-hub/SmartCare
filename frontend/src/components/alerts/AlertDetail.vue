<template>
  <el-dialog
    title="告警详情"
    :model-value="modelValue"
    @update:model-value="handleClose"
    width="700px"
    destroy-on-close
    center
  >
    <el-descriptions :column="2" border title="基础信息" style="margin-bottom: 16px">
      <el-descriptions-item label="告警ID">{{ detailData.eventId }}</el-descriptions-item>
      <el-descriptions-item label="告警类型">{{ formatAlertType(detailData.cameraType) }}</el-descriptions-item>
      <el-descriptions-item label="涉及人员">{{ detailData.elderName || '未注册路人' }}</el-descriptions-item>
      <el-descriptions-item label="是否注册">{{ formatIsRegistered(detailData.isRegistered) }}</el-descriptions-item>
      <el-descriptions-item label="摔倒发生时间">{{ detailData.fallTime }}</el-descriptions-item>
      <el-descriptions-item label="系统检测时间">{{ detailData.detectTime }}</el-descriptions-item>
      <el-descriptions-item label="AI置信度">{{ formatPercent(detailData.confidence) }}</el-descriptions-item>
      <el-descriptions-item label="告警位置" :span="2">{{ detailData.locationDesc }}</el-descriptions-item>
    </el-descriptions>
    <el-descriptions :column="2" border title="处理信息">
      <el-descriptions-item label="处理状态">
        {{ formatHandleStatus(detailData.status) }}
      </el-descriptions-item>
      <el-descriptions-item label="通知状态">{{ formatAlertSentStatus(detailData.alertSent) }}</el-descriptions-item>
      <el-descriptions-item label="处理人" :span="2">{{ detailData.processedByName || '暂无' }}</el-descriptions-item>
      <el-descriptions-item label="处理时间" :span="2">{{ detailData.processedAt || '暂无' }}</el-descriptions-item>
      <el-descriptions-item label="处理备注" :span="2">
        <el-input v-model="localProcessNotes" type="textarea" rows="3" placeholder="请输入处理备注" />
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="saveNotes">保存备注</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { reactive, watch } from 'vue'
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { AlertItem } from '@/types/fallEvent'
import { saveAlertNotes } from '@/api/fallEvent'
import { 
  formatPercent, 
  formatAlertType, 
  formatHandleStatus, 
  formatAlertSentStatus, 
  formatIsRegistered 
} from '@/utils/format'

// Props
const props = defineProps<{
  modelValue: boolean
  detailData: AlertItem
}>()

// Emits
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'notesSaved', eventId: number, processNotes: string): void
}>()

// ✅ 本地副本：避免直接修改 prop.detailData
const localProcessNotes = ref('')

// 监听 props 变化，同步本地备注
watch(
  () => props.detailData,
  (newVal) => {
    localProcessNotes.value = newVal.processNotes || ''
  },
  { immediate: true }
)

// 关闭弹窗
const handleClose = () => {
  emit('update:modelValue', false)
}

// 保存备注
const saveNotes = async () => {
  if (!localProcessNotes.value) {
    ElMessage.warning('请输入处理备注！')
    return
  }

  try {
    const res = await saveAlertNotes(props.detailData.eventId, localProcessNotes.value)
    if (res.code === 200) {
      emit('update:modelValue', false)
      emit('notesSaved', props.detailData.eventId, localProcessNotes.value)
      ElMessage.success('备注保存成功！')
    } else {
      ElMessage.error(res.message || '备注保存失败')
    }
  } catch (error) {
    console.error('保存备注异常：', error)
    ElMessage.error('网络异常，备注保存失败')
  }
}

// 暴露格式化函数到模板
defineExpose({
  formatPercent,
  formatAlertType,
  formatHandleStatus,
  formatAlertSentStatus,
  formatIsRegistered
})
</script>

<style scoped>
:deep(.el-dialog) {
  --el-dialog-bg-color: #ffffff;
  --el-dialog-title-color: #1f2937;
  --el-dialog-border-color: #e6e6e6;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

:deep(.el-descriptions) {
  --el-descriptions-label-color: #1f2937;
  --el-descriptions-content-color: #333333;
  --el-descriptions-border-color: #e6e6e6;
  --el-descriptions-title-color: #1f2937;
}

:deep(.el-input__inner) {
  background-color: #ffffff;
  border-color: #e6e6e6;
  color: #333333;
}

:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-descriptions__body) {
  padding: 16px;
}
</style>