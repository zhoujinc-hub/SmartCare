<template>
  <div class="alert-filter">
    <el-form :inline="true" :model="form" class="mb-4">
      <!-- 发送状态 -->
      <el-form-item label="发送状态">
        <el-select
          v-model="form.send_status"
          placeholder="请选择"
          clearable
          style="width: 160px"
          @change="handleFilterChange"
        >
          <el-option label="成功" :value="1" />
          <el-option label="失败" :value="0" />
          <el-option label="发送中" :value="2" />
        </el-select>
      </el-form-item>

      <!-- 发送方式 -->
      <el-form-item label="发送方式">
        <el-select
          v-model="form.send_method"
          placeholder="请选择"
          clearable
          style="width: 160px"
          @change="handleFilterChange"
        >
          <el-option label="短信" :value="1" />
          <el-option label="APP推送" :value="2" />
          <el-option label="电话" :value="3" />
        </el-select>
      </el-form-item>

      <!-- 接收人类型 -->
      <el-form-item label="接收人类型">
        <el-select
          v-model="form.recipient_type"
          placeholder="请选择"
          clearable
          style="width: 160px"
          @change="handleFilterChange"
        >
          <el-option label="家属" :value="1" />
          <el-option label="管理员" :value="2" />
        </el-select>
      </el-form-item>

      <!-- 发送时间 -->
      <el-form-item label="发送时间">
        <el-date-picker
          v-model="form.timeRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          style="width: 380px"
          @change="handleTimeRangeChange"
        />
      </el-form-item>

      <!-- 按钮 -->
      <el-form-item>
        <el-button class="button1" type="primary" @click="handleFilterChange">查询</el-button>
        <el-button class="button1" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useAlertStore } from '@/stores/alertStore'
import type { AlertQueryParams } from '@/types/alertType'

const alertStore = useAlertStore()

const form = ref<{
  send_status?: 0 | 1 | 2
  send_method?: 1 | 2 | 3
  recipient_type?: 1 | 2
  timeRange: [string, string] | null
}>({
  timeRange: null
})

watch(
  () => alertStore.filterParams,
  (params) => {
    form.value.send_status = params.send_status
    form.value.send_method = params.send_method
    form.value.recipient_type = params.recipient_type
    if (params.start_time && params.end_time) {
      form.value.timeRange = [params.start_time, params.end_time]
    }
  },
  { immediate: true }
)

const handleFilterChange = () => {
  const params: Partial<AlertQueryParams> = {
    send_status: form.value.send_status,
    send_method: form.value.send_method,
    recipient_type: form.value.recipient_type
  }
  alertStore.setFilterParams(params)
}

const handleTimeRangeChange = () => {
  const params: Partial<AlertQueryParams> = {}
  if (form.value.timeRange) {
    params.start_time = form.value.timeRange[0]
    params.end_time = form.value.timeRange[1]
  } else {
    params.start_time = undefined
    params.end_time = undefined
  }
  alertStore.setFilterParams(params)
}

const handleReset = () => {
  form.value = {
    send_status: undefined,
    send_method: undefined,
    recipient_type: undefined,
    timeRange: null
  }
  alertStore.resetFilterParams()
}
</script>

<style scoped>
.alert-filter {
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.04);
  margin-bottom: 16px;
}

.alert-filter :deep(.el-form-item__label) {
  width: 90px !important;
  text-align: right;
}

.alert-filter :deep(.el-select) {
  width: 160px;
}

.button1 {
  margin-left: 20px;
}
</style>