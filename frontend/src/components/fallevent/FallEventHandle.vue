<template>
  <el-dialog v-model="visible" title="处理事件" width="500px">
    <el-form :model="form" label-width="80px">
      <el-form-item label="处理结果">
        <el-select v-model="form.status">
          <el-option label="已处理" value="2" />
          <el-option label="误报" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="处理备注">
        <el-input v-model="form.processNotes" type="textarea" rows="3" placeholder="请输入处理备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="submit">确认提交</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { defineProps, defineEmits, computed, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { handleFallEvent, saveFallEventNotes } from '@/api/fallEvent'

const props = defineProps<{
  visible: boolean
  eventId: number
}>()

const emit = defineEmits<{
  'update:visible': [value: boolean]
  success: []
}>()

const visible = computed({
  get() { return props.visible },
  set(val) { emit('update:visible', val) }
})

const form = reactive({
  status: 2 as 2 | 3,
  processNotes: ''
})

const submit = async () => {
  try {
    await handleFallEvent(props.eventId, { processedBy: 1 })
    await saveFallEventNotes(props.eventId, { processNotes: form.processNotes })
    ElMessage.success('操作成功！')
    visible.value = false
    emit('success')
  } catch (err) {
    ElMessage.error('操作失败，请重试')
    console.error(err)
  }
}
</script>