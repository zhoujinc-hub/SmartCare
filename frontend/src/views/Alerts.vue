<template>
  <div class="alerts-container">
    <!-- 页面标题+筛选栏 -->
    <div class="page-header">
      <h2>告警记录管理</h2>
      <el-row class="search-bar" align="middle">
        <el-col :span="6">
          <el-select v-model="filterParams.eventType" placeholder="选择告警类型" clearable>
            <el-option label="家庭摔倒告警" value="1" />
            <el-option label="社区摔倒告警" value="2" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="filterParams.status" placeholder="选择处理状态" clearable>
            <el-option label="未处理" value="1" />
            <el-option label="短暂异常" value="2" />
            <el-option label="已处理" value="3" />
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

    <!-- 告警表格 -->
    <el-table
      :data="alertList"
      border
      stripe
      v-loading="loading"
      element-loading-text="加载中..."
      style="width: 100%; margin-top: 16px"
      size="default"
    >
      <el-table-column prop="eventId" label="告警ID" align="center" width="100" />
      <el-table-column label="告警类型" align="center" width="140">
        <template #default="scope">
          <el-tag v-if="scope.row.eventType === 1" type="warning" effect="light">家庭摔倒</el-tag>
          <el-tag v-else type="danger" effect="light">社区摔倒</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="elderName" label="涉及人员" align="center" width="120" />
      <el-table-column prop="alertTime" label="告警触发时间" align="center" width="200" />
      <el-table-column prop="triggerThreshold" label="触发阈值(秒)" align="center" width="140" />
      <el-table-column prop="location" label="告警位置" align="center" />
      <el-table-column label="处理状态" align="center" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.status === 1" type="danger" effect="light">未处理</el-tag>
          <el-tag v-else-if="scope.row.status === 2" type="info" effect="light">短暂异常</el-tag>
          <el-tag v-else type="success" effect="light">已处理</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="通知状态" align="center" width="160">
        <template #default="scope">
          <el-tag v-if="scope.row.alertSent" type="success" effect="light">已通知</el-tag>
          <el-tag v-else type="warning" effect="light">未通知</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" fixed="right">
        <template #default="scope">
          <div class="action-buttons">
            <el-button size="small" type="primary" icon="View" @click="viewDetail(scope.row)">查看详情</el-button>
            <el-button class="action-buttons1" size="small" type="success" icon="Check" @click="handleAlert(scope.row)" v-if="scope.row.status === 1">标记处理</el-button>
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
    >
    </el-pagination>

    <!-- 告警详情弹窗 -->
    <el-dialog
      title="告警详情"
      v-model="dialogVisible"
      width="700px"
      destroy-on-close
      center
    >
      <el-descriptions :column="2" border title="基础信息" style="margin-bottom: 16px">
        <el-descriptions-item label="告警ID">{{ detailData.eventId }}</el-descriptions-item>
        <el-descriptions-item label="告警类型">{{ detailData.eventType === 1 ? '家庭摔倒' : '社区摔倒' }}</el-descriptions-item>
        <el-descriptions-item label="涉及人员">{{ detailData.elderName }}</el-descriptions-item>
        <el-descriptions-item label="摔倒开始时间">{{ detailData.fallStartTime }}</el-descriptions-item>
        <el-descriptions-item label="告警触发时间">{{ detailData.alertTime }}</el-descriptions-item>
        <el-descriptions-item label="触发阈值">{{ detailData.triggerThreshold }} 秒</el-descriptions-item>
        <el-descriptions-item label="告警位置" :span="2">{{ detailData.location }}</el-descriptions-item>
      </el-descriptions>
      <el-descriptions :column="2" border title="处理信息" >
        <el-descriptions-item label="处理状态">
          {{ detailData.status === 1 ? '未处理' : detailData.status === 2 ? '短暂异常' : '已处理' }}
        </el-descriptions-item>
        <el-descriptions-item label="通知状态">{{ detailData.alertSent ? '已通知' : '未通知' }}</el-descriptions-item>
        <el-descriptions-item label="处理人" :span="2">{{ detailData.handledBy || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间" :span="2">{{ detailData.handledAt || '暂无' }}</el-descriptions-item>
        <el-descriptions-item label="处理备注" :span="2">
          <el-input v-model="detailData.handleNotes" type="textarea" rows="3" placeholder="请输入处理备注" />
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveHandleNotes">保存备注</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// 加载状态
const loading = ref(false)
// 弹窗显隐
const dialogVisible = ref(false)
// 告警列表数据
const alertList = ref<AlertItem[]>([])
// 筛选参数
const filterParams = reactive({
  eventType: '' as string | number, // 1家庭 2社区
  status: '' as string | number, // 1未处理 2短暂异常 3已处理
  dateRange: [] as string[] // 时间范围 [开始, 结束]
})
// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})
// 详情弹窗数据
const detailData = reactive<AlertItem>({} as AlertItem)

// 定义接口：匹配数据库fall_events+alerts核心字段
interface AlertItem {
  eventId: number; // 事件ID(fall_events.event_id)
  eventType: number; // 告警类型(1家庭2社区, fall_events.event_type)
  elderName: string; // 涉及人员(fall_events.elderly_name/victim_name)
  fallStartTime: string; // 摔倒开始时间(fall_events.fall_start_time)
  alertTime: string; // 告警触发时间(fall_events.alert_triggered_at)
  triggerThreshold: number; // 触发阈值(fall_events.threshold_seconds)
  location: string; // 告警位置(家庭/社区摄像头位置)
  status: number; // 处理状态(1未2暂3已, fall_events.status)
  alertSent: boolean; // 是否通知(fall_events.alert_sent_to_user/Admin/community)
  handledBy?: string; // 处理人(users.real_name)
  handledAt?: string; // 处理时间(fall_events.handled_at)
  handleNotes?: string; // 处理备注(fall_events.handle_notes)
}

// 模拟完整数据源（所有数据）
const fullData: AlertItem[] = [
  {
    eventId: 1001,
    eventType: 1,
    elderName: '张大爷',
    fallStartTime: '2026-03-18 09:12:35',
    alertTime: '2026-03-18 09:17:35',
    triggerThreshold: 300,
    location: '幸福小区3栋2单元101室（客厅）',
    status: 1,
    alertSent: true,
    handledBy: '',
    handledAt: '',
    handleNotes: ''
  },
  {
    eventId: 1002,
    eventType: 2,
    elderName: '李奶奶',
    fallStartTime: '2026-03-18 10:05:20',
    alertTime: '2026-03-18 10:10:20',
    triggerThreshold: 300,
    location: '幸福小区东门广场（社区摄像头）',
    status: 3,
    alertSent: true,
    handledBy: '社区管理员-王芳',
    handledAt: '2026-03-18 10:20:15',
    handleNotes: '现场已处理，老人无大碍，已联系家属'
  },
  {
    eventId: 1003,
    eventType: 1,
    elderName: '王爷爷',
    fallStartTime: '2026-03-18 14:30:10',
    alertTime: '2026-03-18 14:35:10',
    triggerThreshold: 60,
    location: '阳光社区5栋1单元502室（卧室）',
    status: 2,
    alertSent: false,
    handledBy: '',
    handledAt: '',
    handleNotes: ''
  },
  {
    eventId: 1004,
    eventType: 2,
    elderName: '李奶奶',
    fallStartTime: '2026-03-18 10:05:20',
    alertTime: '2026-03-18 10:10:20',
    triggerThreshold: 300,
    location: '幸福小区东门广场（社区摄像头）',
    status: 3,
    alertSent: true,
    handledBy: '社区管理员-王芳',
    handledAt: '2026-03-18 10:20:15',
    handleNotes: '现场已处理，老人无大碍，已联系家属'
  },
  {
    eventId: 1005,
    eventType: 2,
    elderName: '李奶奶',
    fallStartTime: '2026-03-18 10:05:20',
    alertTime: '2026-03-18 10:10:20',
    triggerThreshold: 300,
    location: '幸福小区东门广场（社区摄像头）',
    status: 3,
    alertSent: true,
    handledBy: '社区管理员-王芳',
    handledAt: '2026-03-18 10:20:15',
    handleNotes: '现场已处理，老人无大碍，已联系家属'
  },
  {
    eventId: 1006,
    eventType: 2,
    elderName: '李奶奶',
    fallStartTime: '2026-03-18 10:05:20',
    alertTime: '2026-03-18 10:10:20',
    triggerThreshold: 300,
    location: '幸福小区东门广场（社区摄像头）',
    status: 3,
    alertSent: true,
    handledBy: '社区管理员-王芳',
    handledAt: '2026-03-18 10:20:15',
    handleNotes: '现场已处理，老人无大碍，已联系家属'
  },
  {
    eventId: 1007,
    eventType: 2,
    elderName: '李奶奶',
    fallStartTime: '2026-03-18 10:05:20',
    alertTime: '2026-03-18 10:10:20',
    triggerThreshold: 300,
    location: '幸福小区东门广场（社区摄像头）',
    status: 3,
    alertSent: true,
    handledBy: '社区管理员-王芳',
    handledAt: '2026-03-18 10:20:15',
    handleNotes: '现场已处理，老人无大碍，已联系家属'
  },
  {
    eventId: 1008,
    eventType: 2,
    elderName: '李奶奶',
    fallStartTime: '2026-03-18 10:05:20',
    alertTime: '2026-03-18 10:10:20',
    triggerThreshold: 300,
    location: '幸福小区东门广场（社区摄像头）',
    status: 3,
    alertSent: true,
    handledBy: '社区管理员-王芳',
    handledAt: '2026-03-18 10:20:15',
    handleNotes: '现场已处理，老人无大碍，已联系家属'
  },
  {
    eventId: 1009,
    eventType: 2,
    elderName: '李奶奶',
    fallStartTime: '2026-03-18 10:05:20',
    alertTime: '2026-03-18 10:10:20',
    triggerThreshold: 300,
    location: '幸福小区东门广场（社区摄像头）',
    status: 3,
    alertSent: true,
    handledBy: '社区管理员-王芳',
    handledAt: '2026-03-18 10:20:15',
    handleNotes: '现场已处理，老人无大碍，已联系家属'
  },
  {
    eventId: 1010,
    eventType: 2,
    elderName: '李奶奶',
    fallStartTime: '2026-03-18 10:05:20',
    alertTime: '2026-03-18 10:10:20',
    triggerThreshold: 300,
    location: '幸福小区东门广场（社区摄像头）',
    status: 3,
    alertSent: true,
    handledBy: '社区管理员-王芳',
    handledAt: '2026-03-18 10:20:15',
    handleNotes: '现场已处理，老人无大碍，已联系家属'
  },
  {
    eventId: 1011,
    eventType: 2,
    elderName: '李奶奶',
    fallStartTime: '2026-03-18 10:05:20',
    alertTime: '2026-03-18 10:10:20',
    triggerThreshold: 300,
    location: '幸福小区东门广场（社区摄像头）',
    status: 3,
    alertSent: true,
    handledBy: '社区管理员-王芳',
    handledAt: '2026-03-18 10:20:15',
    handleNotes: '现场已处理，老人无大碍，已联系家属'
  },
  {
    eventId: 1012,
    eventType: 2,
    elderName: '李奶奶',
    fallStartTime: '2026-03-18 10:05:20',
    alertTime: '2026-03-18 10:10:20',
    triggerThreshold: 300,
    location: '幸福小区东门广场（社区摄像头）',
    status: 3,
    alertSent: true,
    handledBy: '社区管理员-王芳',
    handledAt: '2026-03-18 10:20:15',
    handleNotes: '现场已处理，老人无大碍，已联系家属'
  },
]

// 页面挂载时加载数据
onMounted(() => {
  getAlertList()
})

// 获取告警列表（支持筛选+分页）
const getAlertList = () => {
  loading.value = true
  setTimeout(() => {
    // 1. 多条件筛选
    let filteredList = [...fullData]

    // 按告警类型筛选
    if (filterParams.eventType) {
      const type = Number(filterParams.eventType)
      filteredList = filteredList.filter(item => item.eventType === type)
    }

    // 按处理状态筛选
    if (filterParams.status) {
      const status = Number(filterParams.status)
      filteredList = filteredList.filter(item => item.status === status)
    }

    // 按日期范围筛选
    if (filterParams.dateRange && filterParams.dateRange.length === 2) {
      const [startTime, endTime] = filterParams.dateRange
      filteredList = filteredList.filter(item => {
        const alertTime = new Date(item.alertTime).getTime()
        const start = new Date(startTime).getTime()
        const end = new Date(endTime).getTime()
        return alertTime >= start && alertTime <= end
      })
    }

    // 2. 分页截取
    pagination.total = filteredList.length
    const start = (pagination.pageNum - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    alertList.value = filteredList.slice(start, end)

    loading.value = false
  }, 500)
}

// 查询按钮：重置页码后加载数据
const handleQuery = () => {
  pagination.pageNum = 1
  getAlertList()
}

// 重置按钮：清空筛选条件后加载数据
const resetQuery = () => {
  filterParams.eventType = ''
  filterParams.status = ''
  filterParams.dateRange = []
  pagination.pageNum = 1
  getAlertList()
}

// 页码改变
const handleCurrentChange = (val: number) => {
  pagination.pageNum = val
  getAlertList()
}

// 页容量改变
const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  pagination.pageNum = 1 // 切换每页条数时，回到第1页，避免数据越界
  getAlertList()
}

// 查看详情
const viewDetail = (row: AlertItem) => {
  Object.assign(detailData, row)
  dialogVisible.value = true
}

// 标记处理
const handleAlert = (row: AlertItem) => {
  ElMessageBox.confirm('确定将该告警标记为已处理吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 模拟接口请求，后续替换为真实API
    row.status = 3
    row.handledBy = '系统管理员-张三'
    row.handledAt = new Date().toLocaleString()
    ElMessage.success('标记处理成功！')
  }).catch(() => {
    ElMessage.info('已取消标记')
  })
}

// 保存处理备注
const saveHandleNotes = () => {
  if (!detailData.handleNotes) {
    ElMessage.warning('请输入处理备注！')
    return
  }
  // 模拟接口保存，后续替换为真实API
  const index = alertList.value.findIndex(item => item.eventId === detailData.eventId)
  if (index > -1) {
    alertList.value[index].handleNotes = detailData.handleNotes
  }
  dialogVisible.value = false
  ElMessage.success('备注保存成功！')
}
</script>

<style scoped>
/* 页面容器 - 白色主题 */
.alerts-container {
  width: 100%;
  height: 100%;
  padding: 20px;
  color: #333333;
  background-color: #f9f9f9;
}

/* 页面标题+筛选栏 */
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

/* 表格样式 - 白色主题 */
:deep(.el-table) {
  --el-table-bg-color: #ffffff;
  --el-table-text-color: #333333 !important;
  --el-table-header-text-color: #1f2937 !important;
  --el-table-row-hover-bg-color: #f0f9ff;
  --el-table-border-color: #e6e6e6;
  --el-table-stripe-bg-color: #fafafa;
}

/* 表格单元格文字样式 */
:deep(.el-table .el-table__body-wrapper .el-table__cell > .cell) {
  color: #333333 !important;
  height: auto !important;
  
}

/* 表头文字样式 */
:deep(.el-table .el-table__header-wrapper .el-table__cell > .cell) {
  color: #666666 !important;
  font-weight: 600;
}

/* 分页器样式 - 白色主题 */
:deep(.el-pagination) {
  --el-pagination-text-color: #333333;
  --el-pagination-button-color: #333333;
  --el-pagination-button-bg-color: #ffffff;
  --el-pagination-button-hover-bg-color: #f0f9ff;
  --el-pagination-button-active-bg-color: #409eff;
  --el-pagination-button-active-color: #ffffff;
}

/* 分页器输入框样式 */
:deep(.el-pagination .el-input__inner) {
  background-color: #ffffff !important;
  color: #333333 !important;
  border-color: #e6e6e6;
}

/* 弹窗样式 - 白色主题 */
:deep(.el-dialog) {
  --el-dialog-bg-color: #ffffff;
  --el-dialog-title-color: #1f2937;
  --el-dialog-border-color: #e6e6e6;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

/* 描述列表样式 - 白色主题 */
:deep(.el-descriptions) {
  --el-descriptions-label-color: #1f2937;
  --el-descriptions-content-color: #333333;
  --el-descriptions-border-color: #e6e6e6;
  --el-descriptions-title-color: #1f2937;
}

/* 输入框样式 - 白色主题 */
:deep(.el-input__inner) {
  background-color: #ffffff;
  border-color: #e6e6e6;
  color: #333333;
}

/* 下拉选择框样式 - 白色主题 */
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

/* 日期选择器样式 - 白色主题 */
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

/* 操作按钮容器 */
.action-buttons {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px; /* 按钮之间的间距 */
}

/* 让两个按钮宽度一致 */
.action-buttons .el-button {
  width: 120px;
  justify-content: center;
}

/* 标记处理按钮样式 */
.action-buttons1 {
  background-color: #67c23a;
  border-color: #67c23a;
  margin-right: 12px;
}

/* 标签样式优化 */
:deep(.el-tag) {
  --el-tag-light-bg-color: #fafafa;
  --el-tag-light-color: #333333;
}

/* 按钮样式优化 */
:deep(.el-button) {
  --el-button-hover-bg-color: #f0f9ff;
  --el-button-hover-border-color: #409eff;
}

/* 修复表格边框样式 */
:deep(.el-table__cell) {
  border-color: #e6e6e6 !important;
}

/* 修复卡片/弹窗内边距 */
:deep(.el-dialog__body) {
  padding: 20px;
}

:deep(.el-descriptions__body) {
  padding: 16px;
}
</style>