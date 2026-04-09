<template>
  <div class="elder-table-container">
    <!-- 老人信息表格 -->
    <el-table
      :data="elderList"
      border
      stripe
      v-loading="loading"
      element-loading-text="加载中..."
      style="width: 100%; margin-top: 16px"
      size="default"
      :header-cell-style="{ backgroundColor: '#f8f9fa', color: '#1f2937' }"
      :cell-style="{ backgroundColor: '#ffffff', color: '#333333' }"
    >
      <el-table-column type="index" label="序号" align="center" width="80" />
      <el-table-column prop="elder_id" label="老人ID" align="center" width="100" />
      <el-table-column prop="name" label="姓名" align="center" width="100" />
      <el-table-column label="性别" align="center" width="80">
        <template #default="scope">
          <el-tag type="info" effect="light">{{ formatGender(scope.row.gender) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="age" label="年龄" align="center" width="80" />
      <el-table-column prop="address" label="家庭地址" align="center" />
      <el-table-column prop="health_notes" label="健康备注" align="center" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ scope.row.health_notes || '无' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="家属信息" align="center" width="180">
        <template #default="scope">
          <span>{{ scope.row.relatives || '暂无' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="created_at" label="创建时间" align="center" width="180">
        <template #default="scope">
          <span>{{ formatTime(scope.row.created_at) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" fixed="right">
        <template #default="scope">
          <div class="action-buttons">
            <el-button size="small" type="primary" :icon="View" @click="handleViewDetail(scope.row)">查看</el-button>
            <el-button size="small" type="warning" :icon="Edit" @click="handleEditElder(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" :icon="Delete" @click="handleDeleteElder(scope.row)">删除</el-button>
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
import type { ElderItem, PaginationParams } from '@/types/elder';
import { deleteElder, getElderRelations } from '@/api/elder';
import { formatTime, formatGender } from '@/utils/format';

// Props
const props = defineProps<{
  elderList: ElderItem[];
  loading: boolean;
  pagination: PaginationParams;
}>();

// Emits
const emit = defineEmits<{
  (e: 'viewDetail', row: ElderItem): void;
  (e: 'editElder', row: ElderItem & { relativeIds: number[] }): void;
  (e: 'sizeChange', val: number): void;
  (e: 'currentChange', val: number): void;
  (e: 'deleteSuccess'): void;
}>();

// 查看详情
const handleViewDetail = (row: ElderItem) => {
  emit('viewDetail', row);
};

// 编辑老人
const handleEditElder = async (row: ElderItem) => {
  // 获取关联的家属ID列表
  const res = await getElderRelations(row.elder_id);
  const relativeIds = res.data?.map((rel: any) => rel.user_id) || [];
  
  emit('editElder', {
    ...row,
    relativeIds
  });
};

// 删除老人
const handleDeleteElder = async (row: ElderItem) => {
  ElMessageBox.confirm(
    '确定要删除该老人信息吗？删除后会同时删除关联的家属关系，且不可恢复！',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    const res = await deleteElder(row.elder_id);
    if (res.code === 200) {
      ElMessage.success('删除老人信息成功');
      emit('deleteSuccess');
    } else {
      ElMessage.error(res.message || '删除老人信息失败');
    }
  }).catch(() => {
    ElMessage.info('已取消删除');
  });
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
  formatTime,
  formatGender
});
</script>

<style scoped>
.elder-table-container {
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