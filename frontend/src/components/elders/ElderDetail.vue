<template>
  <el-dialog title="老人详情" v-model="visible" width="600px">
    <el-descriptions :column="1" border>
      <el-descriptions-item label="老人ID">{{ data?.elderId || '-' }}</el-descriptions-item>
      <el-descriptions-item label="姓名">{{ data?.name || '-' }}</el-descriptions-item>
      <el-descriptions-item label="性别">{{ data?.gender === 1 ? '男' : '女' }}</el-descriptions-item>
      <el-descriptions-item label="年龄">{{ data?.age || '-' }}</el-descriptions-item>
      <el-descriptions-item label="家庭地址">{{ data?.address || '-' }}</el-descriptions-item>
      <el-descriptions-item label="家属联系人1">{{ data?.familyContact1 || '-' }}</el-descriptions-item>
      <el-descriptions-item label="电话1">{{ data?.familyPhone1 || '-' }}</el-descriptions-item>
      <el-descriptions-item label="家属联系人2">{{ data?.familyContact2 || '-' }}</el-descriptions-item>
      <el-descriptions-item label="电话2">{{ data?.familyPhone2 || '-' }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ data?.createdAt || '-' }}</el-descriptions-item>
    </el-descriptions>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import type { ElderItem } from '@/types/elder'

const props = defineProps<{
  modelValue: boolean
  data?: ElderItem
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', val: boolean): void
}>()

const visible = ref(props.modelValue)
watch(() => props.modelValue, (val) => visible.value = val)
watch(visible, (val) => emit('update:modelValue', val))
</script>