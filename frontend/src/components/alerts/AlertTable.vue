<template>
  <div class="alert-table">
    <el-table
        v-loading="loading"
        :data="alertList"
        border
        stripe
        style="width: 100%"
        @row-click="handleDetail"
    >
      <!-- 告警ID -->
      <el-table-column prop="alertId" label="告警ID" width="120" />
      <!-- 事件ID -->
      <el-table-column prop="eventId" label="事件ID" width="120" />
      <!-- 接收人电话 -->
      <el-table-column prop="recipientPhone" label="接收人电话" min-width="160" />
      <!-- 接收人类型 -->
      <el-table-column label="接收人类型" width="120">
        <template #default="scope">
          <el-tag size="small">{{ formatRecipientType(scope.row.recipientType) }}</el-tag>
        </template>
      </el-table-column>
      <!-- 发送方式 -->
      <el-table-column label="发送方式" width="120">
        <template #default="scope">
          {{ formatSendMethod(scope.row.sendMethod) }}
        </template>
      </el-table-column>
      <!-- 发送状态 -->
      <el-table-column label="发送状态" width="120">
        <template #default="scope">
          <el-tag
              size="small"
              :type="scope.row.sendStatus === 1 ? 'success' : scope.row.sendStatus === 2 ? 'warning' : 'danger'"
          >
            {{ formatSendStatus(scope.row.sendStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <!-- 发送时间 -->
      <el-table-column prop="sentAt" label="发送时间" min-width="200">
        <template #default="scope">
          {{ formatTime(scope.row.sentAt ?? '') }}
        </template>
      </el-table-column>
      <!-- 操作 -->
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="scope">
          <el-button size="small" type="primary" @click="handleDetail(scope.row)">详情</el-button>
          <el-button
              size="small"
              type="warning"
              @click="handleResend(scope.row)"
              v-if="scope.row.sendStatus !== 1"
          >
            重发
          </el-button>
          <AlertOperate :alert="scope.row" @handle-success="handleSuccess" />
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        v-if="total > 0"
        class="mt-4"
        layout="prev, pager, next, jumper, ->, total, sizes"
        :total="total"
        :page-size="size"
        :current-page="page"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
    />
  </div>
</template>

<script setup lang="ts">
import { resendAlert } from '@/api/alertApi'
import {
  formatRecipientType,
  formatSendMethod,
  formatSendStatus,
  formatTime
} from '@/utils/format'
import type { AlertLog } from '@/types/alertType'
import AlertOperate from './AlertOperate.vue'
import { ElMessage } from 'element-plus'

const props = defineProps<{
  alertList: AlertLog[]
  loading: boolean
  page: number
  size: number
  total: number
}>()

const emit = defineEmits<{
  (e: 'detail', alert: AlertLog): void
  (e: 'refresh'): void
  (e: 'update:page', page: number): void
  (e: 'update:size', size: number): void
}>()

const handleDetail = (alert: AlertLog) => emit('detail', alert)

const handleResend = async (alert: AlertLog) => {
  try {
    await resendAlert(alert.alertId)
    ElMessage.success('重发成功')
    emit('refresh')
  } catch (e) {
    ElMessage.error('重发失败')
  }
}

const handleSuccess = () => emit('refresh')

const handleSizeChange = (newSize: number) => {
  emit('update:size', newSize)
  emit('refresh')
}
const handlePageChange = (newPage: number) => {
  emit('update:page', newPage)
  emit('refresh')
}
</script>