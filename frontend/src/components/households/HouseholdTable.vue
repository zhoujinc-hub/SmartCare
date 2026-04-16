<template>
  <div class="household-table-container">
    <el-table
      :data="householdList"
      border
      stripe
      v-loading="loading"
      style="width: 100%; margin-top: 8px"
      size="default"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="index" label="序号" width="80" align="center" />
      <el-table-column prop="householdId" label="家庭ID" width="100" align="center" />
      <el-table-column prop="householdName" label="户主姓名" width="120" align="center" />
      <el-table-column prop="address" label="详细地址" min-width="200" align="center" />

      <el-table-column label="联系人1" prop="contact1_name" width="120" align="center" />
      <el-table-column label="电话1" prop="contact1_phone" width="140" align="center" />
      <el-table-column label="联系人2" prop="contact2_name" width="120" align="center" />
      <el-table-column label="电话2" prop="contact2_phone" width="140" align="center" />

      <el-table-column label="关联老人" align="center" min-width="150">
        <template #default="scope">
          <span v-if="scope.row.elderList.length">
            {{ scope.row.elderList.map((e: { name: string }) => e.name).join('、') }}
          </span>
          <span v-else>暂无</span>
        </template>
      </el-table-column>

      <el-table-column label="绑定摄像头" align="center" min-width="150">
        <template #default="scope">
          <span v-if="scope.row.cameraList.length">
            {{ scope.row.cameraList.map((c: { cameraName: string }) => c.cameraName).join('、') }}
          </span>
          <span v-else>未绑定</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" width="220" fixed="right">
        <template #default="scope">
          <el-button size="small" type="primary" @click="handleViewDetail(scope.row)">查看</el-button>
          <el-button size="small" type="warning" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pagination.pageNum"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pagination.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      style="margin-top: 20px; text-align: right"
    />
  </div>
</template>

<script setup lang="ts">
import { ElMessage, ElMessageBox } from 'element-plus';
import type { HouseholdItem, PaginationParams } from '@/types/household';
import { deleteHousehold } from '@/api/household';

const _props = defineProps<{
  householdList: HouseholdItem[];
  loading: boolean;
  pagination: PaginationParams;
}>();

const emit = defineEmits<{
  (e: 'selection-change', val: HouseholdItem[]): void;
  (e: 'size-change', val: number): void;
  (e: 'current-change', val: number): void;
  (e: 'view-detail', row: HouseholdItem): void;
  (e: 'edit', row: HouseholdItem): void;
  (e: 'delete-success'): void;
}>();

const handleSelectionChange = (val: HouseholdItem[]) => {
  emit('selection-change', val);
};

const handleCurrentChange = (val: number) => {
  emit('current-change', val);
};

const handleSizeChange = (val: number) => {
  emit('size-change', val);
};

const handleViewDetail = (row: HouseholdItem) => {
  emit('view-detail', row);
};

const handleEdit = (row: HouseholdItem) => {
  emit('edit', row);
};

const handleDelete = (row: HouseholdItem) => {
  ElMessageBox.confirm('确定删除该家庭吗？', '警告', { type: 'warning' })
    .then(async () => {
      const result = await deleteHousehold(row.householdId);
      if (result.code === 200) {
        ElMessage.success('删除成功');
        emit('delete-success');
      } else {
        ElMessage.error(result.message || '删除失败');
      }
    }).catch(() => {
      ElMessage.info('已取消');
    });
};
</script>

<style scoped>
.household-table-container { width: 100%; }
:deep(.el-table) {
  --el-table-bg-color: #ffffff;
  --el-table-text-color: #333333;
  --el-table-header-text-color: #1f2937;
  --el-table-border-color: #e6e6e6;
}
:deep(.el-table__cell) {
  border-color: #e6e6e6 !important;
}
</style>