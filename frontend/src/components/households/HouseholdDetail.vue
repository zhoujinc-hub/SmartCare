<template>
  <el-dialog
    title="家庭详情"
    :model-value="modelValue"  
    @update:model-value="handleClose"  
    width="800px"
    destroy-on-close
    center
    custom-class="household-dialog"
  >
    <el-descriptions :column="2" border title="基础信息" style="margin-bottom: 16px">
      <el-descriptions-item label="家庭ID">{{ detailData.householdId }}</el-descriptions-item>
      <el-descriptions-item label="户主姓名">{{ detailData.householdName }}</el-descriptions-item>
      <el-descriptions-item label="所属社区">{{ detailData.communityName }}</el-descriptions-item>
      <el-descriptions-item label="社区联系电话">{{ detailData.communityContactPhone }}</el-descriptions-item>
      <el-descriptions-item label="详细地址" :span="2">{{ detailData.address }}</el-descriptions-item>
      <el-descriptions-item label="报警阈值">{{ detailData.alertThresholdSeconds }} 秒</el-descriptions-item>
      <el-descriptions-item label="家庭状态">
        <el-tag v-if="detailData.isActive" type="success" effect="light">启用</el-tag>
        <el-tag v-else type="danger" effect="light">禁用</el-tag>
      </el-descriptions-item>
    </el-descriptions>

    <el-descriptions :column="2" border title="紧急联系信息">
      <el-descriptions-item label="紧急联系人">{{ detailData.emergencyContactName }}</el-descriptions-item>
      <el-descriptions-item label="紧急联系电话">{{ detailData.emergencyContactPhone }}</el-descriptions-item>
      <el-descriptions-item label="备注信息" :span="2">{{ detailData.remark || '无' }}</el-descriptions-item>
    </el-descriptions>

    <el-descriptions :column="1" border title="关联老人信息" style="margin-top: 16px">
      <el-descriptions-item>
        <el-table :data="detailData.elderList" border size="small" style="width: 100%">
          <el-table-column prop="elderId" label="老人ID" width="100" align="center" />
          <el-table-column prop="name" label="姓名" width="100" align="center" />
          <el-table-column prop="age" label="年龄" width="80" align="center" />
          <el-table-column prop="healthStatusText" label="健康状态" width="120" align="center" />
          <el-table-column prop="emergencyPhone" label="紧急电话" width="140" align="center" />
        </el-table>
        <div v-if="!detailData.elderList.length" style="text-align: center; padding: 10px; color: #999;">
          暂无关联老人
        </div>
      </el-descriptions-item>
    </el-descriptions>

    <el-descriptions :column="1" border title="绑定摄像头信息" style="margin-top: 16px">
      <el-descriptions-item>
        <el-table :data="detailData.cameraList" border size="small" style="width: 100%">
          <el-table-column prop="cameraId" label="摄像头ID" width="100" align="center" />
          <el-table-column prop="cameraName" label="摄像头名称" width="120" align="center" />
          <el-table-column prop="locationDesc" label="安装位置" width="120" align="center" />
          <el-table-column prop="deviceSerial" label="设备序列号" width="200" align="center" />
          <el-table-column prop="statusText" label="设备状态" width="100" align="center" />
        </el-table>
        <div v-if="!detailData.cameraList.length" style="text-align: center; padding: 10px; color: #999;">
          暂无绑定摄像头
        </div>
      </el-descriptions-item>
    </el-descriptions>

    <el-descriptions :column="2" border title="系统信息" style="margin-top: 16px">
      <el-descriptions-item label="创建时间">{{ detailData.createdAt }}</el-descriptions-item>
      <el-descriptions-item label="最后更新时间">{{ detailData.updatedAt }}</el-descriptions-item>
      <el-descriptions-item label="创建人">{{ detailData.createdBy || '系统' }}</el-descriptions-item>
      <el-descriptions-item label="最后更新人">{{ detailData.updatedBy || '系统' }}</el-descriptions-item>
    </el-descriptions>

    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import type { HouseholdItem } from '@/types/household';

// Props：保留必要参数，删除未使用的冗余定义
const props = defineProps<{
  modelValue: boolean;
  detailData: HouseholdItem;
}>();

// Emits：仅保留更新modelValue的事件
const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
}>();

// 关闭弹窗：通过emit通知父组件修改modelValue（符合单向数据流）
const handleClose = () => {
  emit('update:modelValue', false);
};
</script>

<style scoped>
:deep(.household-dialog) {
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