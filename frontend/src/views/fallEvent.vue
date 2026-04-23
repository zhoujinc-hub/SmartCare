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
  padding: 4px;
}
.search-bar {
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
  align-items: center;
}
.mt-4 {
  margin-top: 16px;
  text-align: right;
}
:deep(.el-table__header .el-table__cell) {
  color: #1f2937 !important;
  font-weight: 600;
}
:deep(.el-table__empty-text) {
  color: #6b7280 !important;
}
</style>