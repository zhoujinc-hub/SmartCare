<template>
  <el-table
      :data="data"
      border
      stripe
      :loading="loading"
      style="width: 100%; margin-top: 16px"
  >
    <el-table-column prop="eventId" label="事件ID" align="center" width="90" />
    <el-table-column prop="cameraId" label="摄像头ID" align="center" width="100" />
    <el-table-column prop="elderName" label="老人/路人" align="center" />
    <el-table-column prop="isRegistered" label="注册状态" align="center" width="100">
      <template #default="{ row }">
        <el-tag :type="row.isRegistered === 1 ? 'success' : 'info'">
          {{ row.isRegistered === 1 ? '已注册' : '未注册' }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="fallTime" label="跌倒时间" align="center" width="180" />

    <el-table-column label="视频路径" align="center" min-width="220">
      <template #default="{ row }">
        <el-link
            :href="row.videoPath"
            type="primary"
            target="_blank"
            :underline="false"
            :disabled="!row.videoPath"
        >
          {{ row.videoPath || '无视频' }}
        </el-link>
      </template>
    </el-table-column>

    <el-table-column prop="confidence" label="置信度" align="center" width="80" />
    <el-table-column label="处理状态" align="center" width="100">
      <template #default="{ row }">
        <el-tag :type="getStatusTagType(row.status)">
          {{ getStatusText(row.status) }}
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column label="操作" align="center" width="300" fixed="right">
      <template #default="{ row }">
        <div class="action-row">
          <el-button
              class="btn-main"
              size="small"
              @click="$emit('open-detail', row)"
          >
            查看详情
          </el-button>

          <el-button
              class="btn-success"
              size="small"
              @click="$emit('open-handle', row)"
          >
            标注处理
          </el-button>

          <el-button
              class="btn-note"
              size="small"
          >
            添加备注
          </el-button>
        </div>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'
import type { FallEventVo } from '@/types/fallEvent'

const props = defineProps<{
  data: FallEventVo[]
  loading: boolean
}>()

const emit = defineEmits<{
  'open-detail': [row: FallEventVo]
  'open-handle': [row: FallEventVo]
}>()

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