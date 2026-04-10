<template>
  <el-dialog
    title="摄像头详情"
    :model-value="modelValue"
    @update:model-value="handleClose"
    width="800px"
    destroy-on-close
    center
    custom-class="camera-dialog"
  >
    <el-descriptions :column="2" border title="基础信息" style="margin-bottom: 16px">
      <el-descriptions-item label="摄像头ID">{{ detailData.camera_id }}</el-descriptions-item>
      <el-descriptions-item label="设备名称">{{ detailData.camera_name }}</el-descriptions-item>
      <el-descriptions-item label="摄像头类型">{{ detailData.camera_type === 0 ? '家庭摄像头' : '社区摄像头' }}</el-descriptions-item>
      <el-descriptions-item label="所属家庭">{{ detailData.householdName || '未绑定' }}</el-descriptions-item>
      <el-descriptions-item label="安装位置">{{ detailData.location_desc }}</el-descriptions-item>
      <el-descriptions-item label="设备序列号">{{ detailData.device_serial }}</el-descriptions-item>
      <el-descriptions-item label="视频流地址" :span="2">{{ detailData.stream_url || '未配置' }}</el-descriptions-item>
      <el-descriptions-item label="设备状态">
        <el-tag v-if="detailData.status" type="success" effect="light">在线</el-tag>
        <el-tag v-else type="danger" effect="light">离线</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="安装坐标">
        {{ detailData.latitude || '未设置' }}, {{ detailData.longitude || '未设置' }}
      </el-descriptions-item>
    </el-descriptions>

    <el-descriptions :column="2" border title="设备状态信息">
      <el-descriptions-item label="最后心跳时间">{{ detailData.lastHeartbeat || '无' }}</el-descriptions-item>
      <el-descriptions-item label="累计运行时长">{{ detailData.totalRuntime || '0小时' }}</el-descriptions-item>
      <el-descriptions-item label="今日报警次数">{{ detailData.todayAlertCount || 0 }}次</el-descriptions-item>
      <el-descriptions-item label="累计报警次数">{{ detailData.totalAlertCount || 0 }}次</el-descriptions-item>
      <el-descriptions-item label="最后报警时间">{{ detailData.lastAlertTime || '无' }}</el-descriptions-item>
      <el-descriptions-item label="固件版本">{{ detailData.firmwareVersion || 'V1.0.0' }}</el-descriptions-item>
    </el-descriptions>

    <el-descriptions :column="2" border title="系统信息" style="margin-top: 16px">
      <el-descriptions-item label="创建时间">{{ formatTime(detailData.created_at) }}</el-descriptions-item>
      <el-descriptions-item label="最后更新时间">{{ detailData.updatedAt || '无' }}</el-descriptions-item>
      <el-descriptions-item label="创建人">{{ detailData.createdBy || '系统' }}</el-descriptions-item>
      <el-descriptions-item label="最后更新人">{{ detailData.updatedBy || '系统' }}</el-descriptions-item>
      <el-descriptions-item label="备注信息" :span="2">{{ detailData.remark || '无' }}</el-descriptions-item>
    </el-descriptions>

    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
      <el-button type="primary" @click="$emit('preview', detailData)">实时预览</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
// 修复：补充必要导入 + 解决未使用变量警告
import type { CameraItem } from '@/types/camera';
import { formatTime } from '@/utils/camera';

// 修复：添加 eslint 注释忽略未使用变量警告（若需保留）
defineProps<{
  modelValue: boolean;
  detailData: CameraItem;
}>();  

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'preview', row: CameraItem): void;
}>();

const handleClose = () => emit('update:modelValue', false);
</script>

<style scoped>
:deep(.camera-dialog) {
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
:deep(.el-descriptions__content) { border-color: #e6e6e6 !important; }
:deep(.el-tag) { font-size: 12px; padding: 2px 8px; border-radius: 4px; }
</style>