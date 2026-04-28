<template>
  <div class="fall-event-page">
    <div class="search-bar">
      <el-input
          v-model="query.elderName"
          placeholder="输入老人姓名搜索"
          style="width: 260px"
          clearable
          @keyup.enter="getList"
      />
      <el-select
          v-model="query.status"
          placeholder="选择状态"
          style="width: 150px; margin-left: 8px"
          clearable
          @change="getList"
      >
        <el-option label="待处理" :value="1" />
        <el-option label="已处理" :value="2" />
        <el-option label="误报" :value="3" />
      </el-select>
      <el-input
          v-model.number="query.cameraId"
          placeholder="摄像头ID"
          style="width: 150px; margin-left: 8px"
          clearable
          @keyup.enter="getList"
      />
      <el-button type="primary" @click="getList" style="margin-left: 8px">
        <el-icon><Search /></el-icon> 查询
      </el-button>
      <el-button @click="resetQuery" style="margin-left: 8px">重置</el-button>
    </div>

    <FallEventTable
        :data="tableData"
        :loading="loading"
        @open-detail="openDetail"
        @open-handle="openHandle"
    />

    <el-pagination
        v-if="pagination.total > 0"
        class="mt-4"
        layout="prev, pager, next, jumper, ->, total, sizes"
        :total="pagination.total"
        v-model:page-size="pagination.pageSize"
        v-model:current-page="pagination.pageNum"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
    />

    <FallEventDetail v-model:visible="detailVisible" :detail="detailData" />
    <FallEventHandle v-model:visible="handleVisible" :event-id="currentEventId" @success="getList" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import FallEventTable from '@/components/fallevent/FallEventTable.vue'
import FallEventDetail from '@/components/fallevent/FallEventDetail.vue'
import FallEventHandle from '@/components/fallevent/FallEventHandle.vue'
import { getFallEventList } from '@/api/fallEvent'
import type { FallEventVo, FallEventQueryParams } from '@/types/fallEvent'

const loading = ref(false)
const detailVisible = ref(false)
const handleVisible = ref(false)
const currentEventId = ref(0)
const tableData = ref<FallEventVo[]>([])
const detailData = ref<FallEventVo>({} as FallEventVo)

const query = reactive<FallEventQueryParams>({
  elderName: '',
  status: undefined,
  cameraId: undefined,
  pageNum: 1,
  pageSize: 10
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

onMounted(() => getList())

async function getList() {
  loading.value = true
  try {
    const params = {
      elderName: query.elderName,
      status: query.status,
      cameraId: query.cameraId,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    const res = await getFallEventList(params)
    if (res?.data) {
      tableData.value = res.data.records ?? []
      pagination.total = res.data.total ?? 0
    } else {
      tableData.value = []
      pagination.total = 0
    }
  } catch (err) {
    console.error('获取跌倒事件列表失败：', err)
    ElMessage.error('获取列表失败，请稍后重试')
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

function handleSizeChange() {
  pagination.pageNum = 1
  getList()
}

function handlePageChange() {
  getList()
}

async function openDetail(row: FallEventVo) {
  detailData.value = row
  detailVisible.value = true
}

function openHandle(row: FallEventVo) {
  currentEventId.value = row.eventId
  handleVisible.value = true
}

function resetQuery() {
  query.elderName = ''
  query.status = undefined
  query.cameraId = undefined
  pagination.pageNum = 1
  getList()
}
</script>

<style scoped>
.fall-event-page {
  min-height: 100vh;
  padding: 24px;
  border-radius: 28px;
  background:
      radial-gradient(circle at 8% 6%, #ffffff 0%, transparent 30%),
      radial-gradient(circle at 92% 8%, #eef4ff 0%, transparent 32%),
      linear-gradient(135deg, #eef3f8 0%, #f8fbff 100%);
}

/* 搜索软卡片 */
.search-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 26px;
  background: linear-gradient(145deg, #ffffff, #edf3f8);
  border: 1px solid rgba(255, 255, 255, 0.72);
  box-shadow:
      14px 14px 30px rgba(163, 177, 198, 0.26),
      -14px -14px 30px rgba(255, 255, 255, 0.92),
      inset 1px 1px 1px rgba(255, 255, 255, 0.75);
}

/* 覆盖行内 margin，统一用 gap */
.search-bar :deep(.el-input),
.search-bar :deep(.el-select),
.search-bar :deep(.el-button) {
  margin-left: 0 !important;
}

/* 输入框 / 下拉框内凹质感 */
:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  min-height: 42px;
  border-radius: 999px;
  background: #f3f7fb;
  border: 1px solid rgba(255, 255, 255, 0.72);
  box-shadow:
      inset 5px 5px 10px rgba(163, 177, 198, 0.22),
      inset -5px -5px 10px rgba(255, 255, 255, 0.95);
  transition: all 0.18s ease;
}

:deep(.el-input__wrapper.is-focus),
:deep(.el-select__wrapper.is-focused) {
  box-shadow:
      inset 3px 3px 7px rgba(163, 177, 198, 0.25),
      inset -3px -3px 7px rgba(255, 255, 255, 0.95),
      0 0 0 3px rgba(99, 102, 241, 0.08);
}

/* 胶囊按钮 */
:deep(.el-button) {
  height: 40px;
  padding: 0 18px;
  border: none;
  border-radius: 999px;
  font-weight: 600;
  transition: all 0.18s ease;
  box-shadow:
      6px 6px 14px rgba(163, 177, 198, 0.28),
      -6px -6px 14px rgba(255, 255, 255, 0.92);
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow:
      9px 9px 18px rgba(163, 177, 198, 0.33),
      -9px -9px 18px rgba(255, 255, 255, 0.96);
}

:deep(.el-button:active) {
  transform: translateY(1px);
  box-shadow:
      inset 4px 4px 8px rgba(0, 0, 0, 0.12),
      inset -4px -4px 8px rgba(255, 255, 255, 0.45);
}

:deep(.el-button--primary) {
  color: #fff;
  background: linear-gradient(135deg, #7c8cff, #5b6ee1);
}

/* 表格柔和卡片 */
:deep(.el-table) {
  overflow: hidden;
  border-radius: 26px;
  border: 1px solid rgba(255, 255, 255, 0.72);
  background: linear-gradient(145deg, #ffffff, #edf3f8);
  box-shadow:
      16px 16px 34px rgba(163, 177, 198, 0.26),
      -16px -16px 34px rgba(255, 255, 255, 0.92);
}

:deep(.el-table__inner-wrapper::before) {
  display: none;
}

:deep(.el-table th.el-table__cell) {
  background: rgba(246, 249, 253, 0.92) !important;
  color: #344054 !important;
  font-weight: 700;
}

:deep(.el-table td.el-table__cell) {
  background: rgba(255, 255, 255, 0.58);
  color: #475467;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
  background: rgba(244, 248, 252, 0.72);
}

:deep(.el-table__body tr) {
  transition: all 0.18s ease;
}

:deep(.el-table__body tr:hover > td.el-table__cell) {
  background: #f7faff !important;
}

/* 表格内按钮 */
:deep(.el-table .el-button) {
  height: 32px;
  padding: 0 12px;
  font-size: 13px;
}

/* 状态标签柔和胶囊 */
:deep(.el-tag) {
  border: none;
  border-radius: 999px;
  padding: 0 12px;
  font-weight: 600;
  background: #eef3f8;
  color: #667085;
  box-shadow:
      inset 2px 2px 5px rgba(163, 177, 198, 0.18),
      inset -2px -2px 5px rgba(255, 255, 255, 0.9);
}

:deep(.el-tag--success) {
  background: #e8f8f0;
  color: #24a06b;
}

:deep(.el-tag--warning) {
  background: #fff6df;
  color: #c98212;
}

:deep(.el-tag--danger) {
  background: #fff0f0;
  color: #e45656;
}

/* 分页软面板 */
.mt-4 {
  margin-top: 20px;
  text-align: right;
}

:deep(.el-pagination) {
  display: inline-flex;
  padding: 14px 18px;
  border-radius: 22px;
  background: linear-gradient(145deg, #ffffff, #edf3f8);
  box-shadow:
      8px 8px 18px rgba(163, 177, 198, 0.24),
      -8px -8px 18px rgba(255, 255, 255, 0.92);
}

:deep(.el-pager li),
:deep(.el-pagination button) {
  border-radius: 12px;
  background: #f3f7fb;
  box-shadow:
      4px 4px 9px rgba(163, 177, 198, 0.2),
      -4px -4px 9px rgba(255, 255, 255, 0.9);
}

:deep(.el-pager li.is-active) {
  color: #fff;
  background: linear-gradient(135deg, #7c8cff, #5b6ee1);
}

:deep(.el-table__empty-text) {
  color: #98a2b3 !important;
}

@media (max-width: 768px) {
  .fall-event-page {
    padding: 14px;
  }

  .search-bar {
    padding: 16px;
    border-radius: 22px;
  }

  .search-bar :deep(.el-input),
  .search-bar :deep(.el-select) {
    width: 100% !important;
  }
}
/* ===== 操作区强制单行 ===== */
:deep(.action-row) {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  flex-wrap: nowrap;       /* ❗关键：不换行 */
}

/* ===== 按钮统一尺寸 ===== */
:deep(.action-row .el-button) {
  height: 30px;
  padding: 0 12px;
  font-size: 12px;
  border-radius: 999px;
  white-space: nowrap;     /* ❗防止文字换行 */
  flex-shrink: 0;          /* ❗防压缩 */
  border: none;
  transition: all 0.18s ease;
}

/* ===== 主按钮（查看详情）===== */
:deep(.btn-main) {
  color: #fff;
  background: linear-gradient(135deg, #7c8cff, #5b6ee1);
  box-shadow:
      4px 4px 10px rgba(163, 177, 198, 0.28),
      -4px -4px 10px rgba(255, 255, 255, 0.9);
}

/* ===== 次按钮（标注处理）===== */
:deep(.btn-success) {
  color: #fff;
  background: linear-gradient(135deg, #72dfb0, #3bbb83);
  box-shadow:
      3px 3px 8px rgba(163, 177, 198, 0.25),
      -3px -3px 8px rgba(255, 255, 255, 0.9);
}

/* ===== 弱按钮（备注）===== */
:deep(.btn-note) {
  background: #eef3f8;
  color: #667085;
  box-shadow:
      inset 2px 2px 5px rgba(163, 177, 198, 0.2),
      inset -2px -2px 5px rgba(255, 255, 255, 0.9);
}

/* ===== hover 浮起 ===== */
:deep(.action-row .el-button:hover) {
  transform: translateY(-2px);
}

/* ===== 点击按压 ===== */
:deep(.action-row .el-button:active) {
  transform: translateY(1px);
  box-shadow:
      inset 3px 3px 6px rgba(0, 0, 0, 0.12),
      inset -3px -3px 6px rgba(255, 255, 255, 0.4);
}
</style>