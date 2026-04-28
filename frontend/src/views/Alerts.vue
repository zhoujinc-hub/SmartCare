<template>
  <div class="alert-list-page">
    <div class="page-header mb-4">
    </div>

    <alert-filter />

    <alert-table
        :alert-list="alertStore.alertList"
        :loading="alertStore.loading"
        :page="alertStore.pagination.pageNum"
        :size="alertStore.pagination.pageSize"
        :total="alertStore.pagination.total"
        @detail="handleDetail"
        @refresh="fetchList"
        @update:page="alertStore.changePage"
        @update:size="alertStore.changeSize"
    />

    <alert-detail
        v-model:visible="detailDialogVisible"
        :alert-id="currentAlertId"
    />
  </div>
</template>

<script setup lang="ts">
// ✅ 正确导入所有需要的 API
import { ref, onMounted } from 'vue'
import { useAlertStore } from '@/stores/alertStore'
import AlertFilter from '@/components/alerts/AlertFilter.vue'
import AlertTable from '@/components/alerts/AlertTable.vue'
import AlertDetail from '@/components/alerts/AlertDetail.vue'
import type { AlertLog } from '@/types/alertType'

const alertStore = useAlertStore()

const detailDialogVisible = ref(false)
const currentAlertId = ref<bigint | null>(null)

// 页面加载请求数据
onMounted(() => {
  alertStore.fetchAlertList(true)
})

// 刷新列表
const fetchList = () => {
  alertStore.fetchAlertList(false)
}

// 查看详情
const handleDetail = (alert: AlertLog) => {
  currentAlertId.value = alert.alertId
  detailDialogVisible.value = true
}
</script>

<style scoped>
.alert-list-page {
  position: relative;
  min-height: 100vh;
  padding: 24px;
  box-sizing: border-box;
  overflow: hidden;
  background:
      radial-gradient(circle at 12% 10%, rgba(255, 255, 255, 0.95), transparent 26%),
      radial-gradient(circle at 88% 18%, rgba(191, 219, 254, 0.5), transparent 30%),
      radial-gradient(circle at 48% 92%, rgba(204, 251, 241, 0.42), transparent 34%),
      linear-gradient(135deg, #eef4fb 0%, #e7edf6 48%, #f7f9fd 100%);
  color: #2f3b52;
}

.alert-list-page::before,
.alert-list-page::after {
  content: "";
  position: absolute;
  border-radius: 999px;
  background: #edf3fa;
  box-shadow:
      18px 18px 40px rgba(163, 177, 198, 0.28),
      -18px -18px 40px rgba(255, 255, 255, 0.86);
  pointer-events: none;
  z-index: 0;
}

.alert-list-page::before {
  width: 260px;
  height: 260px;
  top: 8%;
  left: 5%;
}

.alert-list-page::after {
  width: 340px;
  height: 340px;
  right: 6%;
  bottom: 8%;
}

.page-header {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 子组件整体软 UI 包裹 */
:deep(.el-card),
:deep(.filter-card),
:deep(.table-card),
:deep(.alert-filter),
:deep(.alert-table) {
  position: relative;
  z-index: 1;
  border-radius: 26px;
  border: 1px solid rgba(255, 255, 255, 0.72);
  background: linear-gradient(145deg, #f8fbff, #e8eef7);
  box-shadow:
      16px 16px 36px rgba(163, 177, 198, 0.34),
      -16px -16px 36px rgba(255, 255, 255, 0.92),
      inset 1px 1px 1px rgba(255, 255, 255, 0.78);
  transition: all 200ms ease;
}

:deep(.el-card:hover),
:deep(.filter-card:hover),
:deep(.table-card:hover) {
  transform: translateY(-3px);
  box-shadow:
      20px 20px 42px rgba(163, 177, 198, 0.42),
      -18px -18px 38px rgba(255, 255, 255, 0.96);
}

:deep(.el-card__body) {
  background: transparent;
}

/* 表单输入框 */
:deep(.el-input__wrapper),
:deep(.el-select__wrapper),
:deep(.el-date-editor.el-input__wrapper) {
  border-radius: 16px;
  background: #eef3fa;
  box-shadow:
      inset 6px 6px 12px rgba(163, 177, 198, 0.26),
      inset -6px -6px 12px rgba(255, 255, 255, 0.86);
  transition: all 180ms ease;
}

:deep(.el-input__wrapper:hover),
:deep(.el-select__wrapper:hover),
:deep(.el-date-editor.el-input__wrapper:hover) {
  transform: translateY(-1px);
  box-shadow:
      inset 5px 5px 10px rgba(163, 177, 198, 0.23),
      inset -5px -5px 10px rgba(255, 255, 255, 0.9),
      0 8px 18px rgba(163, 177, 198, 0.16);
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-select__wrapper.is-focused) {
  box-shadow:
      inset 4px 4px 8px rgba(163, 177, 198, 0.22),
      inset -4px -4px 8px rgba(255, 255, 255, 0.92),
      0 0 0 2px rgba(64, 158, 255, 0.16);
}

:deep(.el-input__inner) {
  color: #2f3b52;
}

:deep(.el-input__inner::placeholder) {
  color: #a0abbb;
}

/* 按钮 */
:deep(.el-button) {
  border-radius: 999px;
  border: none;
  transition: all 180ms ease;
}

:deep(.el-button:not(.el-button--primary)) {
  color: #64748b;
  background: linear-gradient(145deg, #f8fbff, #e7edf6);
  box-shadow:
      6px 6px 14px rgba(163, 177, 198, 0.26),
      -6px -6px 14px rgba(255, 255, 255, 0.86);
}

:deep(.el-button--primary) {
  color: #fff;
  background: linear-gradient(145deg, #5db2ff, #2f91ee);
  box-shadow:
      8px 8px 18px rgba(64, 158, 255, 0.26),
      -8px -8px 18px rgba(255, 255, 255, 0.82),
      inset 1px 1px 1px rgba(255, 255, 255, 0.38);
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  filter: brightness(1.03);
}

:deep(.el-button:active) {
  transform: translateY(1px) scale(0.98);
  box-shadow:
      inset 5px 5px 10px rgba(163, 177, 198, 0.3),
      inset -5px -5px 10px rgba(255, 255, 255, 0.8);
}

/* 表格 */
:deep(.el-table) {
  border-radius: 22px;
  overflow: hidden;
  background: transparent;
  color: #475569;
}

:deep(.el-table th.el-table__cell) {
  background: #edf3fa;
  color: #64748b;
  font-weight: 700;
}

:deep(.el-table tr),
:deep(.el-table td.el-table__cell) {
  background: rgba(255, 255, 255, 0.36);
}

:deep(.el-table--border .el-table__cell),
:deep(.el-table__inner-wrapper::before) {
  border-color: rgba(203, 213, 225, 0.45);
}

:deep(.el-table__body tr:hover > td.el-table__cell) {
  background: rgba(255, 255, 255, 0.72);
}

/* 标签与分页 */
:deep(.el-tag) {
  border-radius: 999px;
  border: none;
  padding: 0 12px;
  box-shadow:
      3px 3px 8px rgba(163, 177, 198, 0.24),
      -3px -3px 8px rgba(255, 255, 255, 0.86);
}

:deep(.el-pagination button),
:deep(.el-pager li) {
  border-radius: 12px;
  background: #eef3fa;
  box-shadow:
      4px 4px 10px rgba(163, 177, 198, 0.22),
      -4px -4px 10px rgba(255, 255, 255, 0.86);
}

:deep(.el-pager li.is-active) {
  color: #409eff;
  box-shadow:
      inset 4px 4px 8px rgba(163, 177, 198, 0.24),
      inset -4px -4px 8px rgba(255, 255, 255, 0.88);
}

/* 弹窗详情 */
:deep(.el-dialog) {
  border-radius: 26px;
  background: linear-gradient(145deg, #f8fbff, #e8eef7);
  box-shadow:
      22px 22px 52px rgba(163, 177, 198, 0.42),
      -18px -18px 42px rgba(255, 255, 255, 0.9);
}

:deep(.el-dialog__header) {
  padding: 22px 24px 12px;
}

:deep(.el-dialog__title) {
  color: #334155;
  font-weight: 700;
}

@media (max-width: 768px) {
  .alert-list-page {
    padding: 16px;
    overflow: auto;
  }
}
</style>