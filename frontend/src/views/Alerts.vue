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