<template>
  <div class="household-table-container">
    <!-- 家庭信息表格 -->
    <el-table
      :data="householdList"
      border
      stripe
      v-loading="loading"
      element-loading-text="加载中..."
      style="width: 100%; margin-top: 16px"
      size="default"
      :header-cell-style="{ backgroundColor: '#f8f9fa', color: '#1f2937' }"
      :cell-style="{ backgroundColor: '#ffffff', color: '#333333' }"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" />
      <el-table-column type="index" label="序号" align="center" width="80" />
      <el-table-column prop="householdId" label="家庭ID" align="center" width="100" />
      <el-table-column prop="householdName" label="户主姓名" align="center" width="120" />
      <el-table-column prop="communityName" label="所属社区" align="center" width="140" />
      <el-table-column prop="address" label="详细地址" align="center" min-width="200" />
      <el-table-column label="关联老人" align="center" min-width="180">
        <template #default="scope">
          <el-tag
            v-for="elder in scope.row.elderList"
            :key="elder.elderId"
            type="info"
            effect="light"
            style="margin: 2px;"
          >
            {{ elder.name }}
          </el-tag>
          <span v-if="!scope.row.elderList.length">暂无关联</span>
        </template>
      </el-table-column>
      <el-table-column label="绑定摄像头" align="center" min-width="180">
        <template #default="scope">
          <el-tag
            v-for="camera in scope.row.cameraList"
            :key="camera.cameraId"
            type="success"
            effect="light"
            style="margin: 2px;"
          >
            {{ camera.cameraName }}
          </el-tag>
          <span v-if="!scope.row.cameraList.length">未绑定</span>
        </template>
      </el-table-column>
      <el-table-column prop="alertThresholdSeconds" label="报警阈值(秒)" align="center" width="120">
        <template #default="scope">
          <span>{{ scope.row.alertThresholdSeconds }}s</span>
        </template>
      </el-table-column>
      <el-table-column prop="emergencyContactPhone" label="紧急联系电话" align="center" width="140" />
      <el-table-column label="状态" align="center" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.isActive" type="success" effect="light">启用</el-tag>
          <el-tag v-else type="danger" effect="light">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" fixed="right">
        <template #default="scope">
          <div class="action-buttons">
            <el-button size="small" type="primary" :icon="View" @click="handleViewDetail(scope.row)">查看</el-button>
            <el-button size="small" type="warning" :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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
import { View, Edit, Delete } from '@element-plus/icons-vue';
import type { 
  HouseholdItem, 
  PaginationParams 
} from '@/types/household';
import { deleteHousehold } from '@/api/household';

// Props
const props = defineProps<{
  householdList: HouseholdItem[];
  loading: boolean;
  pagination: PaginationParams;
}>();

// Emits
const emit = defineEmits<{
  (e: 'selectionChange', val: HouseholdItem[]): void;
  (e: 'sizeChange', val: number): void;
  (e: 'currentChange', val: number): void;
  (e: 'viewDetail', row: HouseholdItem): void;
  (e: 'edit', row: HouseholdItem): void;
  (e: 'deleteSuccess'): void;
}>();

// 表格选择事件
const handleSelectionChange = (val: HouseholdItem[]) => {
  emit('selectionChange', val);
};

// 页码改变
const handleCurrentChange = (val: number) => {
  emit('currentChange', val);
};

// 页容量改变
const handleSizeChange = (val: number) => {
  emit('sizeChange', val);
};

// 查看详情
const handleViewDetail = (row: HouseholdItem) => {
  emit('viewDetail', row);
};

// 编辑
const handleEdit = (row: HouseholdItem) => {
  emit('edit', row);
};

// 删除
const handleDelete = (row: HouseholdItem) => {
  ElMessageBox.confirm(
    '确定要删除该家庭信息吗？删除后关联的老人和摄像头信息不会被删除！',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    const result = await deleteHousehold(row.householdId);
    if (result.code === 200) {
      ElMessage.success('删除家庭成功！');
      emit('deleteSuccess');
    } else {
      ElMessage.error(result.message || '删除家庭失败');
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
};
</script>

<style scoped>
.household-table-container {
  width: 100%;
}
:deep(.el-table) {
  --el-table-bg-color: #ffffff;
  --el-table-text-color: #333333;
  --el-table-header-text-color: #1f2937;
  --el-table-row-hover-bg-color: #f0f9ff;
  --el-table-border-color: #e6e6e6;
  --el-table-stripe-bg-color: #fafafa;
}
:deep(.el-table--border::after),
:deep(.el-table--group::after),
:deep(.el-table::before) {
  background-color: #e6e6e6;
}
:deep(.el-table__cell) {
  border-color: #e6e6e6 !important;
}
:deep(.el-table .el-table__header-wrapper .el-table__cell > .cell) {
  font-weight: 600;
}
:deep(.el-pagination) {
  --el-pagination-text-color: #333333;
  --el-pagination-button-color: #333333;
  --el-pagination-button-bg-color: #ffffff;
  --el-pagination-button-hover-bg-color: #f0f0f0;
  --el-pagination-button-active-bg-color: #409eff;
  --el-pagination-button-active-color: #ffffff;
}
.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
}
.action-buttons .el-button {
  justify-content: center;
  padding: 6px 10px;
  font-size: 12px;
  min-width: auto;
  border-radius: 4px;
}
:deep(.el-tag) {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}
</style>