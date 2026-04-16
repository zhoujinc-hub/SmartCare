<template>
  <div class="households-container">
    <!-- 页面标题+筛选栏 -->
    <div class="page-header">
      <h2>家庭信息管理</h2>
      <el-row class="search-bar" align="middle">
        <el-col :span="8">
          <el-input v-model="filterParams.householdName" placeholder="输入户主姓名搜索" clearable />
        </el-col>

        <el-col :span="8" class="search-buttons">
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button type="success" :icon="Plus" @click="openAddForm">新增家庭</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 家庭列表表格组件 -->
    <HouseholdTable
      :household-list="householdList"
      :loading="loading"
      :pagination="pagination"
      @selection-change="handleSelectionChange"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      @view-detail="handleViewDetail"
      @edit="handleEditHousehold"
      @delete-success="fetchHouseholdList"
    />

    <!-- 新增/编辑家庭弹窗组件 -->
    <HouseholdForm
      v-model="formVisible"
      :is-edit="isEdit"
      :form-data="formData"
      :edit-household-id="editHouseholdId"
      @submit-success="fetchHouseholdList"
    />

    <!-- 查看家庭详情弹窗组件 -->
    <HouseholdDetail
      v-model="detailVisible"
      :detail-data="detailData"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Plus } from '@element-plus/icons-vue';

import HouseholdTable from '@/components/households/HouseholdTable.vue';
import HouseholdForm from '@/components/households/HouseholdForm.vue';
import HouseholdDetail from '@/components/households/HouseholdDetail.vue';

import type {
  HouseholdItem,
  HouseholdFormData,
  HouseholdFilterParams,
  PaginationParams,
} from '@/types/household';

import {
  getHouseholdList,
  getHouseholdDetail
} from '@/api/household';

// ======================== 状态管理 ========================
const loading = ref(false);
const formVisible = ref(false);
const detailVisible = ref(false);
const householdList = ref<HouseholdItem[]>([]);

const selectedHouseholds = ref<HouseholdItem[]>([]);
const isEdit = ref(false);
const editHouseholdId = ref<number>(0);

const detailData = reactive<HouseholdItem>({} as HouseholdItem);

const formData = reactive<HouseholdFormData>({
  householdName: '',
  address: '',
  contact1_name: '',
  contact1_phone: '',
  contact2_name: '',
  contact2_phone: '',
});

const filterParams = reactive<HouseholdFilterParams>({
  householdName: '',
  hasCamera: undefined
});

const pagination = reactive<PaginationParams>({
  pageNum: 1,
  pageSize: 10,
  total: 0
});

// ======================== 页面逻辑 ========================
onMounted(() => {
  fetchHouseholdList();
});

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
    console.error(error);
    ElMessage.error('网络异常');
  } finally {
    loading.value = false;
  }
};

const handleSelectionChange = (val: HouseholdItem[]) => {
  selectedHouseholds.value = val;
};

const handleQuery = () => {
  pagination.pageNum = 1;
  fetchHouseholdList();
};

const resetQuery = () => {
  filterParams.householdName = '';
  filterParams.hasCamera = undefined;
  pagination.pageNum = 1;
  fetchHouseholdList();
};

const handleCurrentChange = (val: number) => {
  pagination.pageNum = val;
  fetchHouseholdList();
};

const handleSizeChange = (val: number) => {
  pagination.pageSize = val;
  pagination.pageNum = 1;
  fetchHouseholdList();
};

const openAddForm = () => {
  isEdit.value = false;
  editHouseholdId.value = 0;
  Object.assign(formData, {
    householdName: '',
    address: '',
    contact1_name: '',
    contact1_phone: '',
    contact2_name: '',
    contact2_phone: '',
  });
  formVisible.value = true;
};

const handleEditHousehold = async (row: HouseholdItem) => {
  const res = await getHouseholdDetail(row.householdId);
  if (res.code === 200 && res.data) {
    isEdit.value = true;
    editHouseholdId.value = row.householdId;
    Object.assign(formData, {
      householdName: res.data.householdName,
      address: res.data.address,
      contact1_name: res.data.contact1_name,
      contact1_phone: res.data.contact1_phone,
      contact2_name: res.data.contact2_name,
      contact2_phone: res.data.contact2_phone,
    });
    formVisible.value = true;
  }
};

const handleViewDetail = async (row: HouseholdItem) => {
  const res = await getHouseholdDetail(row.householdId);
  if (res.code === 200 && res.data) {
    Object.assign(detailData, res.data);
    detailVisible.value = true;
  }
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
</style>