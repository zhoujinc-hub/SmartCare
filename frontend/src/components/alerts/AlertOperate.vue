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
import { ArrowDown } from '@element-plus/icons-vue'

const props = defineProps<{
  alert: AlertLog
}>()

const emit = defineEmits<{
  (e: 'handle-success'): void
}>()

const handleCommand = async (command: string) => {
  const sendStatus = Number(command) as 0 | 1 | 2
  try {
    const { value: errorMsg } = await ElMessageBox.prompt(
        '请输入处理备注',
        '处理摔倒事件',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputPlaceholder: '如：已联系家属，老人无大碍...'
        }
    )

    await handleAlert({
      alertId: props.alert.alertId,       // 告警ID
      sendStatus: sendStatus,             // 发送状态
      errorMsg: errorMsg                  // 错误信息/备注
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