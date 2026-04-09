<template>
  <div class="elders-container">
    <!-- 页面标题+筛选栏 -->
    <div class="page-header">
      <h2>老人信息管理</h2>
      <el-row class="search-bar" align="middle">
        <el-col :span="6">
          <el-input v-model="filterParams.name" placeholder="输入老人姓名搜索" clearable />
        </el-col>
        <el-col :span="5">
          <el-select v-model="filterParams.communityId" placeholder="选择所属社区" clearable disabled>
            <el-option label="幸福社区" :value="1" />
            <el-option label="阳光社区" :value="2" />
            <el-option label="和谐社区" :value="3" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="filterParams.healthStatus" placeholder="选择健康状态" clearable disabled>
            <el-option label="健康" :value="1" />
            <el-option label="需关注" :value="2" />
            <el-option label="需监护" :value="3" />
          </el-select>
        </el-col>
        <el-col :span="7" class="search-buttons">
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button type="success" :icon="Plus" @click="openAddForm">新增老人</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 老人列表表格组件 -->
    <ElderTable
      :elder-list="elderList"
      :loading="loading"
      :pagination="pagination"
      @viewDetail="handleViewDetail"
      @editElder="handleEditElder"
      @sizeChange="handleSizeChange"
      @currentChange="handleCurrentChange"
      @deleteSuccess="fetchElderList"
    />

    <!-- 新增/编辑老人表单弹窗组件 -->
    <ElderForm
      v-model="formVisible"
      :isEdit="isEdit"
      :form-data="formData"
      :relative-list="relativeList"
      @submitSuccess="fetchElderList"
    />

    <!-- 老人详情弹窗组件 -->
    <ElderDetail
      v-model="detailVisible"
      :detail-data="detailData"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Search, Plus } from '@element-plus/icons-vue';
// 导入组件
import ElderTable from '@/components/elders/ElderTable.vue';
import ElderForm from '@/components/elders/ElderForm.vue';
import ElderDetail from '@/components/elders/ElderDetail.vue';
// 导入类型
import type { ElderItem, ElderFilterParams, PaginationParams, ElderFormData, ElderDetailItem } from '@/types/elder';
import type { UserItem } from '@/types/user';
// 导入接口
import { getElderList, getElderDetail } from '@/api/elder';
import { getRelativeList } from '@/api/user';
// 导入工具函数
import { formatRelativesText } from '@/utils/format';

// 加载状态
const loading = ref(false);
// 表单弹窗显隐
const formVisible = ref(false);
// 详情弹窗显隐
const detailVisible = ref(false);
// 老人列表数据
const elderList = ref<ElderItem[]>([]);
// 家属列表
const relativeList = ref<UserItem[]>([]);
// 筛选参数
const filterParams = reactive<ElderFilterParams>({
  name: '',
  communityId: '',
  healthStatus: ''
});
// 分页参数
const pagination = reactive<PaginationParams>({
  pageNum: 1,
  pageSize: 10,
  total: 0
});
// 是否为编辑模式
const isEdit = ref(false);
// 表单数据
const formData = reactive<ElderFormData>({
  elder_id: 0,
  name: '',
  gender: 0,
  age: 0,
  address: '',
  health_notes: '',
  relativeIds: []
});
// 详情数据
const detailData = reactive<ElderDetailItem>({} as ElderDetailItem);

// 页面挂载时加载数据
onMounted(() => {
  fetchElderList();
  fetchRelativeList();
});

/**
 * 获取老人列表数据
 */
const fetchElderList = async () => {
  loading.value = true;
  try {
    const res = await getElderList(
      { pageNum: pagination.pageNum, pageSize: pagination.pageSize },
      filterParams
    );

    if (res.code === 200) {
      // 拼接家属信息显示文本
      elderList.value = res.data.list.map(item => ({
        ...item,
        relatives: formatRelativesText(item.relativeList)
      }));
      pagination.total = res.data.total;
      pagination.pageNum = res.data.pageNum;
      pagination.pageSize = res.data.pageSize;
    } else {
      ElMessage.error(res.message || '获取老人列表失败');
    }
  } catch (error) {
    console.error('获取老人列表接口异常：', error);
    ElMessage.error('网络异常，获取老人列表失败');
  } finally {
    loading.value = false;
  }
};

/**
 * 获取家属列表
 */
const fetchRelativeList = async () => {
  try {
    const res = await getRelativeList();
    if (res.code === 200) {
      relativeList.value = res.data;
    } else {
      ElMessage.error(res.message || '获取家属列表失败');
    }
  } catch (error) {
    console.error('获取家属列表接口异常：', error);
    ElMessage.error('网络异常，获取家属列表失败');
  }
};

/**
 * 查询按钮
 */
const handleQuery = () => {
  pagination.pageNum = 1;
  fetchElderList();
};

/**
 * 重置筛选条件
 */
const resetQuery = () => {
  filterParams.name = '';
  filterParams.communityId = '';
  filterParams.healthStatus = '';
  pagination.pageNum = 1;
  fetchElderList();
};

/**
 * 页码改变
 */
const handleCurrentChange = (val: number) => {
  pagination.pageNum = val;
  fetchElderList();
};

/**
 * 页容量改变
 */
const handleSizeChange = (val: number) => {
  pagination.pageSize = val;
  pagination.pageNum = 1;
  fetchElderList();
};

/**
 * 打开新增表单
 */
const openAddForm = () => {
  isEdit.value = false;
  // 重置表单数据
  Object.assign(formData, {
    elder_id: 0,
    name: '',
    gender: 0,
    age: 0,
    address: '',
    health_notes: '',
    relativeIds: []
  });
  formVisible.value = true;
};

/**
 * 打开编辑表单
 */
const handleEditElder = (row: ElderItem & { relativeIds: number[] }) => {
  isEdit.value = true;
  // 填充表单数据
  Object.assign(formData, {
    elder_id: row.elder_id,
    name: row.name,
    gender: row.gender,
    age: row.age,
    address: row.address,
    health_notes: row.health_notes,
    relativeIds: row.relativeIds
  });
  formVisible.value = true;
};

/**
 * 查看详情
 */
const handleViewDetail = async (row: ElderItem) => {
  const res = await getElderDetail(row.elder_id);
  if (res.data) {
    Object.assign(detailData, res.data);
  } else {
    Object.assign(detailData, {
      ...row,
      relativeList: row.relativeList || [],
      relativeCount: row.relativeList?.length || 0
    });
  }
  detailVisible.value = true;
};
</script>

<style scoped>
.elders-container {
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