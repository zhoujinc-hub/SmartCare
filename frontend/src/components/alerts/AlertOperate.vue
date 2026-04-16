<template>
  <el-dropdown @command="handleCommand">
    <el-button size="small" type="info">
      处理事件 <el-icon class="el-icon--right"><ArrowDown /></el-icon>
    </el-button>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="1">标记为待处理</el-dropdown-item>
        <el-dropdown-item command="2">标记为已处理</el-dropdown-item>
        <el-dropdown-item command="3">标记为误报</el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus'
import { handleAlert } from '@/api/alertApi'
import type { AlertLog } from '@/types/alertType'

// 正确写法：setup 语法糖自带，无需导入
const props = defineProps<{
  alert: AlertLog
}>()

const emit = defineEmits<{
  (e: 'handle-success'): void
}>()

const handleCommand = async (command: string) => {
  const status = Number(command) as 1 | 2 | 3
  try {
    const { value: process_notes } = await ElMessageBox.prompt(
      '请输入处理备注',
      '处理摔倒事件',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '如：已联系家属，老人无大碍...'
      }
    )

    await handleAlert({
      alert_id: props.alert.alert_id,
      status,
      process_notes
    })

    ElMessage.success('处理成功！')
    emit('handle-success')
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('处理失败！')
      console.error(error)
    }
  }
}
</script>

<style scoped>
</style>