<template>
  <div class="dashboard-container">
    <!-- 1. 顶部数字概览卡片 -->
    <div class="card-group">
      <el-card class="stat-card">
        <div class="stat-item">
          <span class="stat-label">在住老人总数</span>
          <span class="stat-value">128</span>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-item">
          <span class="stat-label">今日告警数</span>
          <span class="stat-value">5</span>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-item">
          <span class="stat-label">健康异常数</span>
          <span class="stat-value">2</span>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-item">
          <span class="stat-label">设备在线率</span>
          <span class="stat-value">98%</span>
        </div>
      </el-card>
    </div>

    <!-- 2. 中间双列布局（左侧图表/地图 + 右侧数据列表） -->
    <div class="content-row">
      <!-- 左侧：图表/地图占位区（核心可视化区域） -->
      <div class="left-content">
        <el-card class="chart-card">
          <div class="chart-placeholder">
            <span>区域老人分布地图/趋势图表</span>
          </div>
        </el-card>
      </div>

      <!-- 右侧：告警/异常数据列表（右侧的表格区域） -->
      <div class="right-content">
        <el-card class="list-card">
          <div class="list-header">
            <span>最新告警记录</span>
          </div>
          <el-table :data="alertList" border class="data-table">
            <el-table-column prop="time" label="告警时间" />
            <el-table-column prop="elderName" label="老人姓名" />
            <el-table-column prop="type" label="告警类型" />
            <el-table-column prop="status" label="处理状态">
              <template #default="scope">
                <el-tag v-if="scope.row.status === '未处理'" type="danger">未处理</el-tag>
                <el-tag v-else type="success">已处理</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 模拟告警数据（后续可替换为接口请求）
interface AlertItem {
  time: string;
  elderName: string;
  type: string;
  status: string;
}

const alertList: AlertItem[] = [
  { time: '2026-03-16 09:15', elderName: '张大爷', type: '心率异常', status: '未处理' },
  { time: '2026-03-16 08:30', elderName: '李奶奶', type: '跌倒预警', status: '已处理' },
  { time: '2026-03-16 07:45', elderName: '王爷爷', type: '血氧偏低', status: '未处理' },
];
</script>

<style scoped>
/* 整体容器 - 白色主题 */
.dashboard-container {
  width: 100%;
  height: 100%;
  color: #333333;
  background-color: #f9f9f9;
  padding: 0;
}

/* 顶部数字卡片组 */
.card-group {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  flex: 1;
  background-color: #ffffff;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.stat-item {
  text-align: center;
  padding: 15px 0;
}

.stat-label {
  font-size: 14px;
  color: #666666;
  display: block;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #000000;
}

/* 中间内容行 */
.content-row {
  display: flex;
  gap: 20px;
  height: calc(100% - 120px);
}

.left-content {
  flex: 2;
}

.right-content {
  flex: 1;
}

/* 图表/地图占位卡片 - 白色主题 */
.chart-card {
  height: 100%;
  background-color: #ffffff;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.chart-placeholder {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666666;
  font-size: 18px;
  border: 1px dashed #d9d9d9;
  background-color: #fafafa;
}

/* 右侧列表卡片 - 白色主题 */
.list-card {
  height: 100%;
  background-color: #ffffff;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.list-header {
  font-size: 16px;
  padding: 10px 20px;
  border-bottom: 1px solid #e6e6e6;
  margin-bottom: 10px;
  color: #1f2937;
  font-weight: 600;
}

/* 数据表格 - 白色主题 */
.data-table {
  --el-table-text-color: #333333;
  --el-table-header-text-color: #1f2937;
  --el-table-row-hover-bg-color: #f0f9ff;
  --el-table-border-color: #e6e6e6;
  --el-table-bg-color: #ffffff;
  --el-table-header-text-color: #666666;
  --el-table-stripe-bg-color: #fafafa;
}

/* 修复Element UI卡片默认样式 */
:deep(.el-card) {
  --el-card-bg-color: #ffffff;
  --el-card-border-color: #e6e6e6;
  --el-card-header-text-color: #333333;
}

:deep(.el-card__body) {
  padding: 20px;
}

/* 表格单元格样式优化 */
:deep(.el-table__cell) {
  border-color: #e6e6e6 !important;
}

:deep(.el-table .el-table__header-wrapper .el-table__cell > .cell) {
  font-weight: 600;
}
</style>