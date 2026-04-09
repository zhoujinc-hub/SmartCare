<template>
  <div class="alert-table-container">
    <!-- 表格 -->
    <el-table
      :data="alertList"
      border
      stripe
      v-loading="loading"
      element-loading-text="加载中..."
      style="width: 100%; margin-top: 16px"
      size="default"
    >
      <el-table-column prop="eventId" label="告警ID" align="center" width="100" />
      <el-table-column label="告警类型" align="center" width="140">
        <template #default="scope">
          <el-tag v-if="scope.row.cameraType === 0" type="warning" effect="light">家庭摔倒</el-tag>
          <el-tag v-else type="danger" effect="light">社区摔倒</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="elderName" label="涉及人员" align="center" width="120" />
      <el-table-column prop="fallTime" label="摔倒发生时间" align="center" width="200" />
      <el-table-column prop="detectTime" label="系统检测时间" align="center" width="200" />
      <el-table-column prop="confidence" label="AI置信度" align="center" width="120">
        <template #default="scope">
          {{ formatPercent(scope.row.confidence) }}
        </template>
      </el-table-column>
      <el-table-column prop="locationDesc" label="告警位置" align="center" />
      <el-table-column label="处理状态" align="center" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 1" type="danger" effect="light">待处理</el-tag>
          <el-tag v-else-if="scope.row.status === 2" type="success" effect="light">已处理</el-tag>
          <el-tag v-else type="info" effect="light">误报</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="通知状态" align="center" width="160">
        <template #default="scope">
          <el-tag v-if="scope.row.alertSent" type="success" effect="light">已通知</el-tag>
          <el-tag v-else type="warning" effect="light">未通知</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" fixed="right">
        <template #default="scope">
          <div class="action-buttons">
            <el-button size="small" type="primary" icon="View" @click="handleViewDetail(scope.row)">查看详情</el-button>
            <el-button class="action-buttons1" size="small" type="success" icon="Check" @click="handleMarkProcess(scope.row)" v-if="scope.row.status === 1">标记处理</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页器 -->
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pagination.pageNum"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pagination.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      style="margin-top: 20px; text-align: right"
    >
    </el-pagination>
  </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus';
import type { AlertItem, PaginationParams } from '@/types/fallEvent';
import { handleAlert } from '@/api/fallEvent';
import { formatPercent } from '@/utils/format';

// Props
const props = defineProps<{
  alertList: AlertItem[];
  loading: boolean;
  pagination: PaginationParams;
}>();

// Emits
const emit = defineEmits<{
  (e: 'viewDetail', row: AlertItem): void;
  (e: 'sizeChange', val: number): void;
  (e: 'currentChange', val: number): void;
  (e: 'markProcessed', row: AlertItem): void;
}>();

// 查看详情
const handleViewDetail = (row: AlertItem) => {
  emit('viewDetail', row);
};

// 标记处理
const handleMarkProcess = async (row: AlertItem) => {
  try {
    await ElMessageBox.confirm('确定将该告警标记为已处理吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });

    // 调用标记处理接口（当前登录用户ID需替换为真实值）
    const res = await handleAlert(row.eventId, 1);
    
    if (res.code === 200) {
      emit('markProcessed', row);
      ElMessage.success('标记处理成功！');
    } else {
      ElMessage.error(res.message || '标记处理失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('标记处理异常：', error);
      ElMessage.error('网络异常，标记处理失败');
    } else {
      ElMessage.info('已取消标记');
    }
  }
};

// 页码改变
const handleCurrentChange = (val: number) => {
  emit('currentChange', val);
};

// 页容量改变
const handleSizeChange = (val: number) => {
  emit('sizeChange', val);
};

// 暴露格式化函数
defineExpose({
  formatPercent
});
</script>

<style scoped>
.alert-table-container {
  width: 100%;
}

:deep(.el-table) {
  --el-table-bg-color: #ffffff;
  --el-table-text-color: #333333 !important;
  --el-table-header-text-color: #1f2937 !important;
  --el-table-row-hover-bg-color: #f0f9ff;
  --el-table-border-color: #e6e6e6;
  --el-table-stripe-bg-color: #fafafa;
}

:deep(.el-table .el-table__body-wrapper .el-table__cell > .cell) {
  color: #333333 !important;
  height: auto !important;
}

:deep(.el-table .el-table__header-wrapper .el-table__cell > .cell) {
  color: #666666 !important;
  font-weight: 600;
}

:deep(.el-pagination) {
  --el-pagination-text-color: #333333;
  --el-pagination-button-color: #333333;
  --el-pagination-button-bg-color: #ffffff;
  --el-pagination-button-hover-bg-color: #f0f9ff;
  --el-pagination-button-active-bg-color: #409eff;
  --el-pagination-button-active-color: #ffffff;
}

:deep(.el-pagination .el-input__inner) {
  background-color: #ffffff !important;
  color: #333333 !important;
  border-color: #e6e6e6;
}

:deep(.el-tag) {
  --el-tag-light-bg-color: #fafafa;
  --el-tag-light-color: #333333;
}

:deep(.el-button) {
  --el-button-hover-bg-color: #f0f9ff;
  --el-button-hover-border-color: #409eff;
}

:deep(.el-table__cell) {
  border-color: #e6e6e6 !important;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.action-buttons .el-button {
  width: 120px;
  justify-content: center;
}

.action-buttons1 {
  background-color: #67c23a;
  border-color: #67c23a;
  margin-right: 12px;
}
</style>