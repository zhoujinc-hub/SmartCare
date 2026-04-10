<template>
  <el-dialog
    title="老人详情"
    :model-value="modelValue"
    @update:model-value="handleClose"
    width="700px"
    destroy-on-close
    center
    custom-class="elder-dialog"
  >
    <el-descriptions :column="2" border title="基础信息" style="margin-bottom: 16px">
      <el-descriptions-item label="老人ID">{{ detailData.elder_id }}</el-descriptions-item>
      <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
      <el-descriptions-item label="性别">{{ formatGender(detailData.gender) }}</el-descriptions-item>
      <el-descriptions-item label="年龄">{{ detailData.age }} 岁</el-descriptions-item>
      <el-descriptions-item label="家庭地址">{{ detailData.address }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ formatTime(detailData.created_at) }}</el-descriptions-item>
      <el-descriptions-item label="健康备注" :span="2">
        <span>{{ detailData.health_notes || '无' }}</span>
      </el-descriptions-item>
    </el-descriptions>
    <el-descriptions :column="2" border title="家属关联信息">
      <el-descriptions-item label="关联家属数量">{{ detailData.relativeCount || 0 }} 人</el-descriptions-item>
      <el-descriptions-item label="家属列表" :span="1">
        <el-tag
          v-for="(relative, index) in detailData.relativeList"
          :key="index"
          type="info"
          style="margin: 2px"
        >
          {{ relative.real_name }} ({{ relative.phone }})
        </el-tag>
        <span v-if="!detailData.relativeList || detailData.relativeList.length === 0">暂无</span>
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import type { ElderDetailItem } from '@/types/elder';
import { formatTime, formatGender } from '@/utils/format';

// Props
const props = defineProps<{
  modelValue: boolean;
  detailData: ElderDetailItem;
}>();

// Emits
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
}>();

// 关闭弹窗
const handleClose = () => {
  emit('update:modelValue', false);
};

// 暴露格式化函数到模板
defineExpose({
  formatTime,
  formatGender
});
</script>

<style scoped>
:deep(.elder-dialog) {
  --el-dialog-bg-color: #ffffff;
  --el-dialog-title-color: #1f2937;
  --el-dialog-border-color: #e6e6e6;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

:deep(.el-descriptions) {
  --el-descriptions-label-color: #1f2937;
  --el-descriptions-content-color: #333333;
  --el-descriptions-border-color: #e6e6e6;
}

:deep(.el-descriptions__label),
:deep(.el-descriptions__content) {
  border-color: #e6e6e6 !important;
}

:deep(.el-tag) {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}
</style>