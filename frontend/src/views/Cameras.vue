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
  padding: 20px;
  background: #f9f9f9;
}

.page-header {
  margin-bottom: 16px;
}

.search-bar {
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #e6e6e6;
}

.search-buttons {
  display: flex;
  gap: 8px;
}

.mt-4 {
  margin-top: 16px;
  text-align: right;
}
</style>