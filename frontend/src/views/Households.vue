<template>
  <div class="households-container">
    <!-- 页面标题+筛选栏 -->
    <div class="page-header">
      <h2>家庭信息管理</h2>
      <el-row class="search-bar" align="middle">
        <el-col :span="6">
          <el-input v-model="filterParams.householdName" placeholder="输入户主姓名搜索" clearable />
        </el-col>
        <el-col :span="5">
          <el-select v-model="filterParams.communityId" placeholder="选择所属社区" clearable>
            <el-option 
              v-for="item in communityList" 
              :key="item.communityId" 
              :label="item.communityName" 
              :value="item.communityId" 
            />
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select v-model="filterParams.hasCamera" placeholder="是否绑定摄像头" clearable>
            <el-option label="已绑定" :value="1" />
            <el-option label="未绑定" :value="0" />
          </el-select>
        </el-col>
        <el-col :span="8" class="search-buttons">
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button type="success" :icon="Plus" @click="openAddForm">新增家庭</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="warning" :icon="Setting" @click="openBatchThreshold">批量设置报警阈值</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 家庭列表表格组件 -->
    <HouseholdTable
      :household-list="householdList"
      :loading="loading"
      :pagination="pagination"
      @selectionChange="handleSelectionChange"
      @sizeChange="handleSizeChange"
      @currentChange="handleCurrentChange"
      @viewDetail="handleViewDetail"
      @edit="handleEditHousehold"
      @deleteSuccess="fetchHouseholdList"
    />

    <!-- 新增/编辑家庭弹窗组件 -->
    <HouseholdForm
      v-model="formVisible"
      :isEdit="isEdit"
      :form-data="formData"
      :community-list="communityList"
      :default-community-threshold="defaultCommunityThreshold"
      :edit-household-id="editHouseholdId"
      @submitSuccess="fetchHouseholdList"
    />

    <!-- 查看家庭详情弹窗组件 -->
    <HouseholdDetail
      v-model="detailVisible"
      :detail-data="detailData"
    />

    <!-- 批量设置报警阈值弹窗组件 -->
    <BatchThreshold
      v-model="batchThresholdVisible"
      :default-threshold="batchDefaultThreshold"
      :selected-household-ids="selectedHouseholdIds"
      @updateSuccess="fetchHouseholdList"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Plus, Setting } from '@element-plus/icons-vue';
// 导入子组件
import HouseholdTable from '@/components/households/HouseholdTable.vue';
import HouseholdForm from '@/components/households/HouseholdForm.vue';
import HouseholdDetail from '@/components/households/HouseholdDetail.vue';
import BatchThreshold from '@/components/households/BatchThreshold.vue';
// 导入类型
import type { 
  HouseholdItem, 
  HouseholdFormData, 
  HouseholdFilterParams, 
  PaginationParams,
  CommunityItem
} from '@/types/household';
// 导入API
import { 
  getCommunityList, 
  getHouseholdList, 
  getHouseholdDetail 
} from '@/api/household';

// ======================== 状态管理 ========================
// 加载状态
const loading = ref(false);
// 弹窗显隐控制
const formVisible = ref(false);
const detailVisible = ref(false);
const batchThresholdVisible = ref(false);
// 数据列表
const householdList = ref<HouseholdItem[]>([]);
const communityList = ref<CommunityItem[]>([]);
// 选中的家庭数据
const selectedHouseholds = ref<HouseholdItem[]>([]);
const selectedHouseholdIds = ref<number[]>([]);
// 默认阈值
const defaultCommunityThreshold = ref(300);
const batchDefaultThreshold = ref(300);
// 编辑状态
const isEdit = ref(false);
const editHouseholdId = ref<number>(0);
// 详情数据
const detailData = reactive<HouseholdItem>({} as HouseholdItem);
// 表单数据
const formData = reactive<HouseholdFormData>({
  householdName: '',
  communityId: 0,
  address: '',
  alertThresholdSeconds: 300,
  emergencyContactName: '',
  emergencyContactPhone: '',
  isActive: 1,
  remark: ''
});

// 筛选参数
const filterParams = reactive<HouseholdFilterParams>({
  householdName: '',
  communityId: undefined,
  hasCamera: undefined
});

// 分页参数
const pagination = reactive<PaginationParams>({
  pageNum: 1,
  pageSize: 10,
  total: 0
});

// ======================== 页面逻辑 ========================
// 页面挂载时加载数据
onMounted(() => {
  fetchCommunityList();
  fetchHouseholdList();
});

/**
 * 获取社区列表
 */
const fetchCommunityList = async () => {
  try {
    const res = await getCommunityList();
    if (res.code === 200) {
      communityList.value = res.data || [];
      if (communityList.value.length > 0) {
        defaultCommunityThreshold.value = communityList.value[0].alertThresholdSeconds;
        formData.alertThresholdSeconds = communityList.value[0].alertThresholdSeconds;
      }
    } else {
      ElMessage.error(res.message || '获取社区列表失败');
    }
  } catch (error) {
    console.error('获取社区列表异常：', error);
    ElMessage.error('网络异常，获取社区列表失败');
  }
};

/**
 * 获取家庭列表
 */
const fetchHouseholdList = async () => {
  loading.value = true;
  try {
    const res = await getHouseholdList(
      { pageNum: pagination.pageNum, pageSize: pagination.pageSize },
      filterParams
    );

    if (res.code === 200) {
      householdList.value = res.data.records || [];
      pagination.total = res.data.total || 0;
    } else {
      ElMessage.error(res.message || '获取家庭列表失败');
      householdList.value = [];
      pagination.total = 0;
    }
  } catch (error) {
    console.error('获取家庭列表异常：', error);
    ElMessage.error('网络异常，获取家庭列表失败');
    householdList.value = [];
    pagination.total = 0;
  } finally {
    loading.value = false;
  }
};

/**
 * 处理表格选择事件
 */
const handleSelectionChange = (val: HouseholdItem[]) => {
  selectedHouseholds.value = val;
  selectedHouseholdIds.value = val.map(item => item.householdId);
  if (val.length > 0) {
    batchDefaultThreshold.value = val[0].alertThresholdSeconds;
  }
};

/**
 * 查询按钮
 */
const handleQuery = () => {
  pagination.pageNum = 1;
  fetchHouseholdList();
};

/**
 * 重置筛选条件
 */
const resetQuery = () => {
  filterParams.householdName = '';
  filterParams.communityId = undefined;
  filterParams.hasCamera = undefined;
  pagination.pageNum = 1;
  fetchHouseholdList();
};

/**
 * 页码改变
 */
const handleCurrentChange = (val: number) => {
  pagination.pageNum = val;
  fetchHouseholdList();
};

/**
 * 页容量改变
 */
const handleSizeChange = (val: number) => {
  pagination.pageSize = val;
  pagination.pageNum = 1;
  fetchHouseholdList();
};

/**
 * 打开新增表单
 */
const openAddForm = () => {
  isEdit.value = false;
  editHouseholdId.value = 0;
  // 重置表单数据
  Object.assign(formData, {
    householdName: '',
    communityId: 0,
    address: '',
    alertThresholdSeconds: defaultCommunityThreshold.value,
    emergencyContactName: '',
    emergencyContactPhone: '',
    isActive: 1,
    remark: ''
  });
  formVisible.value = true;
};

/**
 * 打开编辑表单
 */
const handleEditHousehold = async (row: HouseholdItem) => {
  const res = await getHouseholdDetail(row.householdId);
  if (res.code === 200 && res.data) {
    isEdit.value = true;
    editHouseholdId.value = row.householdId;
    // 填充表单数据
    Object.assign(formData, {
      householdName: res.data.householdName,
      communityId: res.data.communityId,
      address: res.data.address,
      alertThresholdSeconds: res.data.alertThresholdSeconds,
      emergencyContactName: res.data.emergencyContactName,
      emergencyContactPhone: res.data.emergencyContactPhone,
      isActive: res.data.isActive,
      remark: res.data.remark
    });
    formVisible.value = true;
  }
};

/**
 * 查看详情
 */
const handleViewDetail = async (row: HouseholdItem) => {
  const res = await getHouseholdDetail(row.householdId);
  if (res.code === 200 && res.data) {
    Object.assign(detailData, res.data);
    detailVisible.value = true;
  }
};

/**
 * 打开批量设置阈值弹窗
 */
const openBatchThreshold = () => {
  if (selectedHouseholds.value.length === 0) {
    ElMessage.warning('请先选择需要设置的家庭！');
    return;
  }
  batchThresholdVisible.value = true;
};
</script>

<style scoped>
.households-container {
  width: 100%;
  min-height: calc(100vh - 20px);
  padding: 0;
  color: #333333;
  background-color: #f9f9f9;
}

.page-header {
  padding: 20px;
}
.page-header h2 {
  margin: 0 0 16px 0;
  font-size: 20px;
  color: #1f2937;
  font-weight: 600;
}
.search-bar {
  width: 100%;
  padding: 16px;
  background-color: #ffffff;
  border-radius: 8px;
  border: 1px solid #e6e6e6;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}
.search-buttons {
  display: flex;
  gap: 8px;
}

:deep(.el-input__inner) {
  background-color: #ffffff;
  border-color: #e6e6e6;
  color: #333333;
}
:deep(.el-select .el-input__inner) {
  background-color: #ffffff;
  border-color: #e6e6e6;
  color: #333333;
}
</style>