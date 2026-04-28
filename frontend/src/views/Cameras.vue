<template>
  <div class="cameras-container">
    <div class="page-header">
      <el-row class="search-bar" align="middle">
        <el-col :span="6">
          <el-input v-model="filterParams.cameraName" placeholder="设备名称" clearable />
        </el-col>
        <el-col :span="5">
          <el-select v-model="filterParams.cameraType" placeholder="摄像头类型" clearable>
            <el-option label="家庭" value="0" />
            <el-option label="社区" value="1" />
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select v-model="filterParams.status" placeholder="设备状态" clearable>
            <el-option label="在线" value="1" />
            <el-option label="离线" value="0" />
          </el-select>
        </el-col>
        <el-col :span="8" class="search-buttons">
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button type="success" @click="openAddForm">新增摄像头</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="warning" @click="handleRefreshStatus">刷新状态</el-button>
        </el-col>
      </el-row>
    </div>

    <el-table
        :data="cameraList"
        border
        stripe
        v-loading="loading"
        style="width: 100%; margin-top: 16px"
    >
      <el-table-column type="index" label="序号" align="center" width="80" />
      <el-table-column prop="cameraId" label="摄像头ID" align="center" width="100" />
      <el-table-column prop="cameraName" label="设备名称" align="center" width="140" />
      <el-table-column label="摄像头类型" align="center" width="100">
        <template #default="{ row }">
          <el-tag>{{ row.cameraType === 0 ? '家庭' : '社区' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="locationDesc" label="安装位置" align="center" min-width="120" />
      <el-table-column prop="deviceSerial" label="设备序列号" align="center" min-width="180" />
      <el-table-column label="设备状态" align="center" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">
            {{ row.status === 1 ? '在线' : '离线' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="240" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openDetail(row)">查看</el-button>
          <el-button size="small" type="warning" @click="openEditForm(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        class="mt-4"
        layout="prev, pager, next, jumper, ->, total, sizes"
        :total="pagination.total"
        v-model:page-size="pagination.pageSize"
        v-model:current-page="pagination.pageNum"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
    />

    <CameraForm
        v-model="formVisible"
        :is-edit="isEdit"
        :form-data="formData"
        @submit-success="fetchCameraList"
    />
    <CameraDetail
        v-model="detailVisible"
        :detail-data="detailData"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CameraForm from '@/components/cameras/CameraForm.vue'
import CameraDetail from '@/components/cameras/CameraDetail.vue'
import {
  getCameraList,
  getCameraDetail,
  deleteCamera,
  refreshCameraStatus
} from '@/api/camera'
import type { CameraItem, CameraBaseResponse } from '@/types/camera'

const loading = ref(false)
const cameraList = ref<CameraItem[]>([])
const formVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const detailData = ref<CameraItem>({} as CameraItem)

const filterParams = reactive({
  cameraName: '',
  cameraType: '',
  status: ''
})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formData = reactive({
  cameraId: 0,
  cameraType: 0,
  cameraName: '',
  deviceSerial: '',
  streamUrl: '',
  locationDesc: '',
  latitude: null as number | null,
  longitude: null as number | null,
  status: 1
})

onMounted(() => {
  fetchCameraList()
})

// 获取摄像头列表
const fetchCameraList = async () => {
  loading.value = true
  try {
    const params = {
      cameraName: filterParams.cameraName,
      status: filterParams.status ? Number(filterParams.status) : undefined,
      cameraType: filterParams.cameraType ? Number(filterParams.cameraType) : undefined,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }

    const res = await getCameraList(params) as unknown as CameraBaseResponse & {
      data: { records: CameraItem[], total: number }
    }

    if (res.code === 200) {
      cameraList.value = res.data.records || []
      pagination.total = res.data.total || 0
    }
  } catch (err) {
    ElMessage.error('获取摄像头列表失败')
    console.error(err)
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  pagination.pageNum = 1
  fetchCameraList()
}

// 重置
const resetQuery = () => {
  filterParams.cameraName = ''
  filterParams.cameraType = ''
  filterParams.status = ''
  pagination.pageNum = 1
  fetchCameraList()
}

// 分页切换
const handleCurrentChange = () => {
  fetchCameraList()
}

const handleSizeChange = () => {
  pagination.pageNum = 1
  fetchCameraList()
}

// 打开新增
const openAddForm = () => {
  isEdit.value = false
  Object.assign(formData, {
    cameraId: 0,
    cameraType: 0,
    cameraName: '',
    deviceSerial: '',
    streamUrl: '',
    locationDesc: '',
    latitude: null,
    longitude: null,
    status: 1
  })
  formVisible.value = true
}

// 打开编辑
const openEditForm = async (row: CameraItem) => {
  isEdit.value = true
  const res = await getCameraDetail(row.cameraId) as unknown as CameraBaseResponse & { data: CameraItem }
  if (res.code === 200) {
    Object.assign(formData, res.data)
  }
  formVisible.value = true
}

// 打开详情
const openDetail = async (row: CameraItem) => {
  const res = await getCameraDetail(row.cameraId) as unknown as CameraBaseResponse & { data: CameraItem }
  if (res.code === 200) {
    detailData.value = res.data
    detailVisible.value = true
  }
}

// 删除
const handleDelete = async (row: CameraItem) => {
  try {
    await ElMessageBox.confirm('确定删除该摄像头？', '警告', { type: 'warning' })
    const res = await deleteCamera(row.cameraId) as unknown as CameraBaseResponse
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchCameraList()
    }
  } catch {
    ElMessage.info('已取消删除')
  }
}

// 刷新状态
const handleRefreshStatus = async () => {
  loading.value = true
  try {
    const res = await refreshCameraStatus() as unknown as CameraBaseResponse
    if (res.code === 200) {
      ElMessage.success('状态刷新成功')
      fetchCameraList()
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.cameras-container {
  width: 100%;
  min-height: 100vh;
  padding: 24px;
  border-radius: 28px;
  background:
      radial-gradient(circle at 8% 6%, #ffffff 0%, transparent 28%),
      radial-gradient(circle at 92% 10%, #eaf2ff 0%, transparent 30%),
      linear-gradient(135deg, #eef3f8 0%, #f8fbff 100%);
}

.page-header {
  margin-bottom: 20px;
}

/* 搜索面板：柔和浮雕卡片 */
.search-bar {
  padding: 20px;
  border-radius: 26px;
  background: linear-gradient(145deg, #ffffff, #edf3f8);
  border: 1px solid rgba(255, 255, 255, 0.72);
  box-shadow:
      14px 14px 30px rgba(163, 177, 198, 0.26),
      -14px -14px 30px rgba(255, 255, 255, 0.92),
      inset 1px 1px 1px rgba(255, 255, 255, 0.75);
}

.search-buttons {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  flex-wrap: wrap;
}

/* 输入框 / 下拉框软内凹 */
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

:deep(.el-button--success) {
  color: #fff;
  background: linear-gradient(135deg, #72dfb0, #3bbb83);
}

:deep(.el-button--warning) {
  color: #fff;
  background: linear-gradient(135deg, #ffd27a, #f5a623);
}

:deep(.el-button--danger) {
  color: #fff;
  background: linear-gradient(135deg, #ff8a8a, #ef5f5f);
}

/* 表格软卡片 */
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

/* 表格内按钮更轻盈 */
:deep(.el-table .el-button) {
  height: 32px;
  padding: 0 12px;
  font-size: 13px;
}

/* Tag 柔和胶囊 */
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

@media (max-width: 992px) {
  :deep(.el-col) {
    max-width: 100%;
    flex: 0 0 100%;
  }

  .search-buttons {
    justify-content: flex-start;
  }
}
</style>