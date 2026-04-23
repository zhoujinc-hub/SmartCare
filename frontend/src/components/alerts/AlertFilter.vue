<template>
  <div class="alert-filter">
    <el-form :inline="true" :model="form" class="mb-4">
      <el-form-item label="发送状态">
        <el-select
            v-model="form.sendStatus"
            placeholder="请选择"
            clearable
            style="width:160px"
        >
          <el-option label="成功" :value="1" />
          <el-option label="失败" :value="0" />
          <el-option label="发送中" :value="2" />
        </el-select>
      </el-form-item>

      <el-form-item label="发送方式">
        <el-select
            v-model="form.sendMethod"
            placeholder="请选择"
            clearable
            style="width:160px"
        >
          <el-option label="短信" :value="1" />
          <el-option label="APP推送" :value="2" />
          <el-option label="电话" :value="3" />
        </el-select>
      </el-form-item>

      <el-form-item label="接收人类型">
        <el-select
            v-model="form.recipientType"
            placeholder="请选择"
            clearable
            style="width:160px"
        >
          <el-option label="家属" :value="1" />
          <el-option label="管理员" :value="2" />
        </el-select>
      </el-form-item>

      <el-form-item label="发送时间">
        <el-date-picker
            v-model="form.timeRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width:380px"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="doSearch">查询</el-button>
        <el-button @click="doReset">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useAlertStore } from '@/stores/alertStore'
import type { AlertQueryParams } from '@/types/alertType'

const alertStore = useAlertStore()

const form = ref<{
  sendStatus: 0 | 1 | 2 | undefined
  sendMethod: 1 | 2 | 3 | undefined
  recipientType: 1 | 2 | undefined
  timeRange: string[] | null
}>({
  sendStatus: undefined,
  sendMethod: undefined,
  recipientType: undefined,
  timeRange: null
})

// 搜索
const doSearch = () => {
  const params: Partial<AlertQueryParams> = {}

  if (form.value.sendStatus !== undefined) {
    params.sendStatus = form.value.sendStatus
  }
  if (form.value.sendMethod !== undefined) {
    params.sendMethod = form.value.sendMethod
  }
  if (form.value.recipientType !== undefined) {
    params.recipientType = form.value.recipientType
  }

  if (form.value.timeRange) {
    params.startTime = form.value.timeRange[0]
    params.endTime = form.value.timeRange[1]
  }

  alertStore.setFilterParams(params)
}

// 重置
const doReset = () => {
  form.value = {
    sendStatus: undefined,
    sendMethod: undefined,
    recipientType: undefined,
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
}
</style>