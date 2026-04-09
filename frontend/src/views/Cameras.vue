<template>
  <div class="cameras-container">
    <!-- 页面标题+筛选栏 -->
    <div class="page-header">
      <h2>摄像头设备管理</h2>
      <el-row class="search-bar" align="middle">
        <el-col :span="6">
          <el-input v-model="filterParams.cameraName" placeholder="输入摄像头名称搜索" clearable />
        </el-col>
        <el-col :span="5">
          <el-select v-model="filterParams.householdId" placeholder="选择所属家庭" clearable>
            <el-option v-for="item in householdList" :key="item.householdId" :label="`${item.householdName}(${item.address})`" :value="item.householdId" />
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select v-model="filterParams.status" placeholder="设备状态" clearable>
            <el-option label="在线" :value="1" />
            <el-option label="离线" :value="0" />
          </el-select>
        </el-col>
        <el-col :span="8" class="search-buttons">
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button type="success" :icon="Plus" @click="openAddForm">新增摄像头</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="warning" :icon="Refresh" @click="refreshDeviceStatus">刷新设备状态</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 摄像头表格 -->
    <el-table
      :data="cameraList"
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
      <el-table-column prop="camera_id" label="摄像头ID" align="center" width="100" />
      <el-table-column prop="camera_name" label="设备名称" align="center" width="140" />
      <el-table-column prop="householdName" label="所属家庭" align="center" width="180">
        <template #default="scope">
          <span>{{ scope.row.householdName || '未绑定' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="location_desc" label="安装位置" align="center" width="120" />
      <el-table-column prop="device_serial" label="设备序列号" align="center" width="200" />
      <el-table-column label="实时预览" align="center" width="100">
        <template #default="scope">
          <el-button
            size="small"
            type="primary"
            icon="VideoPlay"
            @click="openPreview(scope.row)"
            :disabled="!scope.row.status"
          >
            预览
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="设备状态" align="center" width="100">
        <template #default="scope">
          <el-tag v-if="scope.row.status" type="success" effect="light">在线</el-tag>
          <el-tag v-else type="danger" effect="light">离线</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="lastHeartbeat" label="最后心跳" align="center" width="180">
        <template #default="scope">
          <span>{{ scope.row.lastHeartbeat || '无' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="240" fixed="right">
        <template #default="scope">
          <div class="action-buttons">
            <el-button size="small" type="primary" :icon="View" @click="openDetail(scope.row)">查看</el-button>
            <el-button size="small" type="warning" :icon="Edit" @click="openEditForm(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
            <el-button
              size="small"
              :type="scope.row.status ? 'danger' : 'success'"
              :icon="scope.row.status ? 'SwitchButton' : 'Check'"
              @click="toggleStatus(scope.row)"
            >
              {{ scope.row.status ? '禁用' : '启用' }}
            </el-button>
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
    />

    <!-- 新增/编辑弹窗 -->
    <CameraForm
      v-model="formVisible"
      :is-edit="isEdit"
      :form-data="formData"
      :household-list="householdList"
      :edit-camera-id="editCameraId"
      @submit-success="fetchCameraList"
    />

    <!-- 详情弹窗 -->
    <CameraDetail
      v-model="detailVisible"
      :detail-data="detailData"
      @preview="openPreview"
    />

    <!-- 预览弹窗 -->
    <CameraPreview
      v-model="previewVisible"
      :preview-data="previewData"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Plus, View, Edit, Delete, Refresh } from '@element-plus/icons-vue';//我删除了SwitchButton, Check, VideoPlay
import CameraForm from '@/components/cameras/CameraForm.vue';
import CameraDetail from '@/components/cameras/CameraDetail.vue';
import CameraPreview from '@/components/cameras/CameraPreview.vue';
import type { CameraItem, CameraFormData, HouseholdItem } from '@/types/camera';
import { getCameraList, getHouseholdList, getCameraDetail, deleteCamera, refreshCameraStatus, updateCamera } from '@/api/camera';

// 状态
const loading = ref(false);
const formVisible = ref(false);
const detailVisible = ref(false);
const previewVisible = ref(false);
const cameraList = ref<CameraItem[]>([]);
const householdList = ref<HouseholdItem[]>([]);
const selectedCameras = ref<CameraItem[]>([]);
const isEdit = ref(false);
const editCameraId = ref<number>(0);
const detailData = reactive<CameraItem>({} as CameraItem);
const previewData = reactive<CameraItem>({} as CameraItem);

// 筛选参数
const filterParams = reactive({
  cameraName: '',
  householdId: '' as number | '',
  status: '' as number | ''
});

// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
});

// 表单数据
const formData = reactive<CameraFormData>({
  camera_id: 0,
  camera_type: 0,
  camera_name: '',
  device_serial: '',
  stream_url: '',
  location_desc: '',
  latitude: '',
  longitude: '',
  status: 1,
  householdId: 0,
  remark: ''
});

// 初始化数据
onMounted(async () => {
  await fetchHouseholdList();
  await fetchCameraList();
});

// 获取摄像头列表
const fetchCameraList = async () => {
  loading.value = true;
  try {
    const res = await getCameraList({
      ...filterParams,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    });
    if (res.code === 200) {
      cameraList.value = res.data.list;
      pagination.total = res.data.total;
    }
  } catch (err) {
    ElMessage.error('获取摄像头列表失败');
  } finally {
    loading.value = false;
  }
};

// 获取家庭列表
const fetchHouseholdList = async () => {
  const res = await getHouseholdList();
  if (res.code === 200) householdList.value = res.data;
};

// 表格选择
const handleSelectionChange = (val: CameraItem[]) => selectedCameras.value = val;

// 查询
const handleQuery = () => {
  pagination.pageNum = 1;
  fetchCameraList();
};

// 重置查询
const resetQuery = () => {
  filterParams.cameraName = '';
  filterParams.householdId = '';
  filterParams.status = '';
  pagination.pageNum = 1;
  fetchCameraList();
};

// 分页变化
const handleCurrentChange = (val: number) => {
  pagination.pageNum = val;
  fetchCameraList();
};
const handleSizeChange = (val: number) => {
  pagination.pageSize = val;
  pagination.pageNum = 1;
  fetchCameraList();
};

// 打开新增表单
const openAddForm = () => {
  isEdit.value = false;
  editCameraId.value = 0;
  Object.assign(formData, {
    camera_id: 0,
    camera_type: 0,
    camera_name: '',
    device_serial: '',
    stream_url: '',
    location_desc: '',
    latitude: '',
    longitude: '',
    status: 1,
    householdId: 0,
    remark: ''
  });
  formVisible.value = true;
};

// 打开编辑表单
const openEditForm = async (row: CameraItem) => {
  isEdit.value = true;
  editCameraId.value = row.camera_id;
  const detail = await getCameraDetail(row.camera_id);
  if (detail.data) {
    Object.assign(formData, {
      camera_id: detail.data.camera_id,
      camera_type: detail.data.camera_type,
      camera_name: detail.data.camera_name,
      device_serial: detail.data.device_serial,
      stream_url: detail.data.stream_url,
      location_desc: detail.data.location_desc,
      latitude: detail.data.latitude || '',
      longitude: detail.data.longitude || '',
      status: detail.data.status,
      householdId: detail.data.householdId || 0,
      remark: detail.data.remark || ''
    });
  }
  formVisible.value = true;
};

// 打开详情
const openDetail = async (row: CameraItem) => {
  const detail = await getCameraDetail(row.camera_id);
  if (detail.data) Object.assign(detailData, detail.data);
  else Object.assign(detailData, row);
  detailVisible.value = true;
};

// 打开预览
const openPreview = (row: CameraItem) => {
  Object.assign(previewData, row);
  previewVisible.value = true;
};

// 删除
const handleDelete = async (row: CameraItem) => {
  try {
    await ElMessageBox.confirm('确定删除该摄像头？删除后无法恢复！', '警告', { type: 'warning' });
    const res = await deleteCamera(row.camera_id);
    if (res.code === 200) {
      ElMessage.success('删除成功');
      fetchCameraList();
    }
  } catch {
    ElMessage.info('已取消删除');
  }
};

// 切换状态
const toggleStatus = async (row: CameraItem) => {
  const newStatus = row.status ? 0 : 1;
  try {
    await ElMessageBox.confirm(`确定${newStatus ? '启用' : '禁用'}该摄像头？`, '提示', { type: 'info' });
    const res = await updateCamera({
      ...formData,
      camera_id: row.camera_id,
      camera_type: row.camera_type,
      camera_name: row.camera_name,
      device_serial: row.device_serial,
      stream_url: row.stream_url,
      location_desc: row.location_desc,
      latitude: row.latitude,
      longitude: row.longitude,
      status: newStatus,
      householdId: row.householdId || 0,
      remark: row.remark || ''
    });
    if (res.code === 200) {
      ElMessage.success('状态更新成功');
      fetchCameraList();
    }
  } catch {
    ElMessage.info('已取消操作');
  }
};

// 刷新设备状态
const refreshDeviceStatus = async () => {
  loading.value = true;
  try {
    const res = await refreshCameraStatus();
    if (res.code === 200) {
      ElMessage.success('状态刷新成功');
      fetchCameraList();
    }
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped>
.cameras-container {
  width: 100%;
  min-height: calc(100vh - 20px);
  padding: 0;
  color: #333333;
  background-color: #f9f9f9;
}
.page-header { padding: 20px; }
.page-header h2 { margin: 0 0 16px; font-size: 20px; color: #1f2937; font-weight: 600; }
.search-bar {
  width: 100%;
  padding: 16px;
  background-color: #ffffff;
  border-radius: 8px;
  border: 1px solid #e6e6e6;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}
.search-buttons { display: flex; gap: 8px; }
:deep(.el-table) {
  --el-table-bg-color: #ffffff;
  --el-table-text-color: #333333;
  --el-table-header-text-color: #1f2937;
  --el-table-row-hover-bg-color: #f0f9ff;
  --el-table-border-color: #e6e6e6;
  --el-table-stripe-bg-color: #fafafa;
}
:deep(.el-table__cell) { border-color: #e6e6e6 !important; }
.action-buttons { display: flex; align-items: center; gap: 8px; justify-content: center; }
.action-buttons .el-button { justify-content: center; padding: 6px 10px; font-size: 12px; min-width: auto; border-radius: 4px; }
:deep(.el-tag) { font-size: 12px; padding: 2px 8px; border-radius: 4px; }
</style>