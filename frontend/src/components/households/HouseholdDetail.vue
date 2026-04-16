<template>
  <el-dialog
    title="家庭详情"
    :model-value="modelValue"
    @update:model-value="handleClose"
    width="750px"
    destroy-on-close
    center
  >
    <el-descriptions :column="2" border>
      <el-descriptions-item label="家庭ID">{{ detailData.householdId }}</el-descriptions-item>
      <el-descriptions-item label="户主姓名">{{ detailData.householdName }}</el-descriptions-item>
      <el-descriptions-item label="详细地址" :span="2">{{ detailData.address }}</el-descriptions-item>

      <el-descriptions-item label="联系人1">{{ detailData.contact1_name }}</el-descriptions-item>
      <el-descriptions-item label="电话1">{{ detailData.contact1_phone }}</el-descriptions-item>
      <el-descriptions-item label="联系人2">{{ detailData.contact2_name || '无' }}</el-descriptions-item>
      <el-descriptions-item label="电话2">{{ detailData.contact2_phone || '无' }}</el-descriptions-item>
    </el-descriptions>

    <el-descriptions :column="1" border title="关联老人" style="margin-top:16px">
      <el-descriptions-item>
        <el-table :data="detailData.elderList" border size="small" style="width:100%">
          <el-table-column prop="elderId" label="老人ID" width="100" />
          <el-table-column prop="name" label="姓名" />
        </el-table>
        <div v-if="!detailData.elderList.length" class="text-center">暂无关联老人</div>
      </el-descriptions-item>
    </el-descriptions>

    <el-descriptions :column="1" border title="绑定摄像头" style="margin-top:16px">
      <el-descriptions-item>
        <el-table :data="detailData.cameraList" border size="small" style="width:100%">
          <el-table-column prop="cameraId" label="摄像头ID" width="100" />
          <el-table-column prop="cameraName" label="名称" />
          <el-table-column prop="locationDesc" label="位置" />
          <el-table-column prop="deviceSerial" label="序列号" />
          <el-table-column prop="statusText" label="状态" />
        </el-table>
        <div v-if="!detailData.cameraList.length" class="text-center">暂无绑定摄像头</div>
      </el-descriptions-item>
    </el-descriptions>

    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import type { HouseholdItem } from '@/types/household';

const props = defineProps<{
  modelValue: boolean;
  detailData: HouseholdItem;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
}>();

const handleClose = () => {
  emit('update:modelValue', false);
};
</script>

<style scoped>
:deep(.el-descriptions__label) { font-weight: 600; }
</style>