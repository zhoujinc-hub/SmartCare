<template>
  <div class="alert-list-page">
    <div class="page-header mb-4">
      <h2>告警日志管理</h2>
    </div>

    <!-- 筛选组件 -->
    <alert-filter />

    <!-- 表格组件 -->
    <alert-table
      :alert-list="alertList"
      :loading="loading"
      :page="pagination.page"
      :size="pagination.size"
      :total="pagination.total"
      @detail="handleDetail"
      @refresh="fetchList"
    />

    <!-- 详情弹窗 -->
    <alert-detail
      v-model:visible="detailDialogVisible"
      :alert-id="currentAlertId"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { useAlertStore } from '@/stores/alertStore';
import AlertFilter from '@/components/alerts/AlertFilter.vue';
import AlertTable from '@/components/alerts/AlertTable.vue';
import AlertDetail from '@/components/alerts/AlertDetail.vue';
import type { AlertLog } from '@/types/alertType';

const alertStore = useAlertStore();

// 解构store状态
const { alertList, pagination, loading, fetchAlertList } = alertStore;

// 详情弹窗
const detailDialogVisible = ref(false);
const currentAlertId = ref<bigint | null>(null);

/** 页面挂载时加载列表 */
onMounted(() => {
  fetchAlertList(true);
});

/** 刷新列表 */
const fetchList = () => {
  fetchAlertList(false);
};

/** 查看详情 */
const handleDetail = (alert: AlertLog) => {
  currentAlertId.value = alert.alert_id;
  detailDialogVisible.value = true;
};
</script>

<style scoped>
.alert-list-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>