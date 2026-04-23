<template>
  <el-dialog
      :model-value="visible"
      title="告警日志详情"
      width="800px"
      destroy-on-close
      @close="handleClose"
  >
    <div v-if="alert" class="alert-detail">
      <el-descriptions :column="2" border class="mb-4">
        <el-descriptions-item label="告警ID">{{ alert.alertId }}</el-descriptions-item>
        <el-descriptions-item label="事件ID">{{ alert.eventId }}</el-descriptions-item>
        <el-descriptions-item label="接收人电话">{{ alert.recipientPhone }}</el-descriptions-item>
        <el-descriptions-item label="接收人类型">{{ formatRecipientType(alert.recipientType) }}</el-descriptions-item>
        <el-descriptions-item label="发送方式">{{ formatSendMethod(alert.sendMethod) }}</el-descriptions-item>
        <el-descriptions-item label="发送状态">
          <el-tag :type="alert.sendStatus === 1 ? 'success' : alert.sendStatus === 2 ? 'warning' : 'danger'">
            {{ formatSendStatus(alert.sendStatus) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发送时间">{{ formatTime(alert.sentAt ?? '') }}</el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2">
          {{ alert.errorMsg || '-' }}
        </el-descriptions-item>
      </el-descriptions>
    </div>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { getAlertDetail } from '@/api/alertApi'
import {
  formatSendStatus,
  formatSendMethod,
  formatRecipientType,
  formatTime,
} from '@/utils/format'
import type { AlertLog } from '@/types/alertType'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  visible: boolean
  alertId: bigint | null
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
}>()

const alert = ref<AlertLog | null>(null)

const handleClose = () => {
  emit('update:visible', false)
}

watch(
    () => props.alertId,
    async (id) => {
      if (id && props.visible) {
        try {
          const res = await getAlertDetail(id)
          alert.value = res.data || res
        } catch (error) {
          ElMessage.error('获取失败')
          console.error(error)
        }
      }
    },
    { immediate: true }
)

watch(
    () => props.visible,
    (val) => {
      if (!val) alert.value = null
    }
)
</script>

<style scoped>
.alert-detail {
  font-size: 14px;
}
</style>