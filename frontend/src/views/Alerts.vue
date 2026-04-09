<template>
  <div class="alerts-container">
    <!-- 页面标题+筛选栏 -->
    <div class="page-header">
      <h2>告警记录管理</h2>
      <el-row class="search-bar" align="middle">
        <el-col :span="6">
          <el-select v-model="filterParams.eventType" placeholder="选择告警类型" clearable>
            <el-option label="家庭摔倒告警" value="0" />
            <el-option label="社区摔倒告警" value="1" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="filterParams.status" placeholder="选择处理状态" clearable>
            <el-option label="待处理" value="1" />
            <el-option label="已处理" value="2" />
            <el-option label="误报" value="3" />
          </el-select>
        </el-col>
        <el-col :span="8">
          <el-date-picker
            v-model="filterParams.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
          />
        </el-col>
        <el-col :span="4">
          <el-button type="primary" icon="Search" @click="handleQuery">查询</el-button>
          <el-button style="margin-left: 8px" @click="resetQuery">重置</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 告警表格组件 -->
    <AlertTable
      :alert-list="alertList"
      :loading="loading"
      :pagination="pagination"
      @viewDetail="handleViewDetail"
      @sizeChange="handleSizeChange"
      @currentChange="handleCurrentChange"
      @markProcessed="handleMarkProcessed"
    />

    <!-- 告警详情弹窗组件 -->
    <AlertDetail
      v-model="dialogVisible"
      :detail-data="detailData"
      @notesSaved="handleNotesSaved"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
// 导入组件
import AlertTable from '@/components/alerts/AlertTable.vue';
import AlertDetail from '@/components/alerts/AlertDetail.vue';
// 导入类型
import type { AlertItem, AlertFilterParams, PaginationParams } from '@/types/fallEvent';
// 导入接口
import { getAlertList } from '@/api/fallEvent';

// 加载状态
const loading = ref(false);
// 弹窗显隐
const dialogVisible = ref(false);
// 告警列表数据
const alertList = ref<AlertItem[]>([]);
// 筛选参数
const filterParams = reactive<AlertFilterParams>({
  eventType: '',
  status: '',
  dateRange: []
});
// 分页参数
const pagination = reactive<PaginationParams>({
  pageNum: 1,
  pageSize: 10,
  total: 0
});
// 详情弹窗数据
const detailData = reactive<AlertItem>({} as AlertItem);

// 页面挂载时加载数据
onMounted(() => {
  fetchAlertList();
});

/**
 * 获取告警列表数据
 */
const fetchAlertList = async () => {
  loading.value = true;
  try {
    const res = await getAlertList(
      { pageNum: pagination.pageNum, pageSize: pagination.pageSize },
      filterParams
    );

    if (res.code === 200) {
      alertList.value = res.data.list || [];
      pagination.total = res.data.total || 0;
    } else {
      ElMessage.error(res.message || '获取告警列表失败');
    }
  } catch (error) {
    console.error('获取告警列表异常：', error);
    ElMessage.error('网络异常，获取告警列表失败');
  } finally {
    loading.value = false;
  }
};

/**
 * 查询按钮事件
 */
const handleQuery = () => {
  pagination.pageNum = 1;
  fetchAlertList();
};

/**
 * 重置筛选条件
 */
const resetQuery = () => {
  filterParams.eventType = '';
  filterParams.status = '';
  filterParams.dateRange = [];
  pagination.pageNum = 1;
  fetchAlertList();
};

/**
 * 页码改变事件
 */
const handleCurrentChange = (val: number) => {
  pagination.pageNum = val;
  fetchAlertList();
};

/**
 * 页容量改变事件
 */
const handleSizeChange = (val: number) => {
  pagination.pageSize = val;
  pagination.pageNum = 1;
  fetchAlertList();
};

/**
 * 查看详情事件
 */
const handleViewDetail = (row: AlertItem) => {
  Object.assign(detailData, row);
  dialogVisible.value = true;
};

/**
 * 标记处理成功后更新本地数据
 */
const handleMarkProcessed = (row: AlertItem) => {
  // 本地更新数据
  const index = alertList.value.findIndex(item => item.eventId === row.eventId);
  if (index > -1) {
    alertList.value[index].status = 2;
    alertList.value[index].processedByName = '系统管理员-张三'; // 替换为真实用户名
    alertList.value[index].processedAt = new Date().toLocaleString();
  }
};

/**
 * 备注保存成功后更新本地数据
 */
const handleNotesSaved = (eventId: number, processNotes: string) => {
  const index = alertList.value.findIndex(item => item.eventId === eventId);
  if (index > -1) {
    alertList.value[index].processNotes = processNotes;
  }
};
</script>

<style scoped>
.alerts-container {
  width: 100%;
  height: 100%;
  padding: 20px;
  color: #333333;
  background-color: #f9f9f9;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 16px;
}

.page-header h2 {
  margin: 0;
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

:deep(.el-select .el-input__inner) {
  background-color: #ffffff;
  border-color: #e6e6e6;
  color: #333333;
}

:deep(.el-select-dropdown) {
  background-color: #ffffff;
  border-color: #e6e6e6;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

:deep(.el-select-dropdown__item) {
  color: #333333;
}

:deep(.el-select-dropdown__item:hover) {
  background-color: #f0f9ff;
}

:deep(.el-date-picker__editor input) {
  background-color: #ffffff !important;
  color: #333333 !important;
  border-color: #e6e6e6 !important;
}

:deep(.el-date-picker) {
  --el-date-picker-content-bg-color: #ffffff;
  --el-date-picker-cell-hover-bg-color: #f0f9ff;
  --el-date-picker-cell-active-bg-color: #409eff;
}
</style>