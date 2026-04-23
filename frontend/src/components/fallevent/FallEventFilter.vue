<template>
  <div class="search-bar">
    <el-form :inline="true" :model="localQuery" label-width="70px">
      <el-form-item label="老人姓名">
        <el-input v-model="localQuery.elderName" placeholder="输入老人姓名" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="localQuery.status" placeholder="请选择状态" clearable>
          <el-option label="待处理" value="1" />
          <el-option label="已处理" value="2" />
          <el-option label="误报" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="摄像头ID">
        <el-input v-model.number="localQuery.cameraId" placeholder="摄像头ID" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { FallEventQueryParams } from '@/types/fallEvent'

const props = defineProps<{
  query: FallEventQueryParams
}>()

const emit = defineEmits<{
  search: []
  reset: []
  'update:query': [val: FallEventQueryParams]
}>()

// 声明一个本地副本，避免直接修改 props
const localQuery = computed({
  get() {
    return { ...props.query }
  },
  set(val) {
    emit('update:query', val)
  }
})

const handleSearch = () => {
  emit('update:query', localQuery.value)
  emit('search')
}

const handleReset = () => {
  emit('reset')
}
</script>

<style scoped>
.search-bar {
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #e6e6e6;
  margin-bottom: 16px;
}
</style>