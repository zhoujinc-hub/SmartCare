<template>
  <div class="dashboard-container">
    <!-- 顶部统计卡片 -->
    <div class="card-group">
      <el-card class="stat-card">
        <div class="stat-item">
          <span class="stat-label">在住老人总数</span>
          <span class="stat-value">{{ elderTotal }}</span>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-item">
          <span class="stat-label">告警总数</span>
          <span class="stat-value">{{ alertTotal }}</span>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-item">
          <span class="stat-label">摔倒事件总数</span>
          <span class="stat-value">{{ fallEventTotal }}</span>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-item">
          <span class="stat-label">摄像头设备在线率</span>
          <span class="stat-value">{{ cameraOnlineRate }}</span>
        </div>
      </el-card>
    </div>

    <!-- 中间主布局 -->
    <div class="content-row">
      <!-- 左侧：地图 + 老人点位 -->
      <div class="left-content">
        <el-card class="chart-card">
          <div class="map-container" id="elderMap">
            <div class="map-placeholder">
              <p>老人位置分布地图</p>
              <div class="map-points">
                <div v-for="elder in elderList" :key="elder.elderId" class="map-point">
                  <el-tooltip :content="elder.realName" placement="top">
                    <div class="point-dot"></div>
                  </el-tooltip>
                </div>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧：上半部分告警列表 + 下半部分饼图 -->
      <div class="right-content">
        <el-card class="list-card">
          <div class="list-header">
            <span>最新告警记录（前5条）</span>
          </div>
          <el-table :data="alertList" border class="full-table" v-loading="loading">
            <el-table-column label="告警时间" width="160">
              <template #default="scope">
                {{ scope.row.detectTime || scope.row.fallTime || '-' }}
              </template>
            </el-table-column>
            <el-table-column label="老人姓名">
              <template #default="scope">
                {{ scope.row.elderName || '未知老人' }}
              </template>
            </el-table-column>
            <el-table-column label="告警类型">
              <template #default="scope">摔倒告警</template>
            </el-table-column>
            <el-table-column label="处理状态">
              <template #default="scope">
                <el-tag v-if="scope.row.status === 1" type="warning">待处理</el-tag>
                <el-tag v-else-if="scope.row.status === 2" type="success">已处理</el-tag>
                <el-tag v-else-if="scope.row.status === 3" type="info">误报</el-tag>
                <el-tag v-else type="info">未知</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <!-- 饼图：摔倒事件状态分布 -->
        <el-card class="chart-card">
          <div class="chart-header">
            <span>摔倒事件状态分布</span>
          </div>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </div>
    </div>

    <!-- 底部：柱状图 - 告警趋势 -->
    <div class="bottom-chart">
      <el-card class="chart-card">
        <div class="chart-header">
          <span>近7日告警趋势</span>
        </div>
        <div ref="barChartRef" class="chart-container"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { getElderList } from '@/api/elder'
import { getFallEventList } from '@/api/fallEvent'
import { getCameraList } from '@/api/camera'
import * as echarts from 'echarts'

const loading = ref(false)
const elderTotal = ref(0)
const alertTotal = ref(0)
const fallEventTotal = ref(0)
const cameraOnlineRate = ref('0%')

const elderList = ref<any[]>([])
const alertList = ref<any[]>([])

const pieChartRef = ref<HTMLDivElement | null>(null)
const barChartRef = ref<HTMLDivElement | null>(null)
let pieChart: echarts.ECharts | null = null
let barChart: echarts.ECharts | null = null

let refreshTimer: number | null = null

const loadDashboardData = async () => {
  loading.value = true
  try {
    const elderRes = await getElderList({ pageNum: 1, pageSize: 1000 })
    elderList.value = elderRes.data?.records ?? []
    elderTotal.value = elderList.value.length

    const fallRes = await getFallEventList({ pageNum: 1, pageSize: 1000 })
    const fallList = fallRes.data?.records ?? []

    fallEventTotal.value = fallList.length
    alertTotal.value = fallList.length

    alertList.value = fallList
        .sort((a: any, b: any) => new Date(b.detectTime || b.fallTime).getTime() - new Date(a.detectTime || a.fallTime).getTime())
        .slice(0, 5)

    const cameraRes = await getCameraList({ pageNum: 1, pageSize: 1000 })
    const cameraList = cameraRes.data?.records ?? []
    const online = cameraList.filter((it: any) => it.status === 1).length
    cameraOnlineRate.value = `${Math.round((online / (cameraList.length || 1)) * 100)}%`

    // 数据加载完后初始化图表
    initCharts(fallList)
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

// 初始化饼图和柱状图
const initCharts = (fallList: any[]) => {
  // 饼图数据：按状态统计
  const statusMap: Record<number, string> = { 1: '待处理', 2: '已处理', 3: '误报' }
  const pieData = Object.entries(
      fallList.reduce((acc: any, item: any) => {
        const status = item.status ?? 1
        acc[status] = (acc[status] || 0) + 1
        return acc
      }, {})
  ).map(([key, value]) => ({
    name: statusMap[Number(key)],
    value,
  }))

  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value)
    pieChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [
        {
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          label: { show: false },
          emphasis: {
            label: { show: true, fontSize: 14, fontWeight: 'bold' },
          },
          data: pieData,
        },
      ],
    })
  }

  // 柱状图数据：近7日告警趋势（模拟数据，按日期统计）
  const days = Array.from({ length: 7 }, (_, i) => {
    const d = new Date()
    d.setDate(d.getDate() - (6 - i))
    return d.toISOString().slice(5, 10)
  })
  const dayCount: Record<string, number> = {}
  fallList.forEach((item: any) => {
    const dateStr = (item.detectTime || item.fallTime || '').slice(5, 10)
    if (dateStr) dayCount[dateStr] = (dayCount[dateStr] || 0) + 1
  })
  const barData = days.map((day) => dayCount[day] || 0)

  if (barChartRef.value) {
    barChart = echarts.init(barChartRef.value)
    barChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: days },
      yAxis: { type: 'value' },
      series: [
        {
          type: 'bar',
          data: barData,
          itemStyle: { color: '#409eff' },
        },
      ],
    })
  }
}

onMounted(() => {
  loadDashboardData()
  refreshTimer = setInterval(loadDashboardData, 30000)

  // 窗口大小变化时重绘图表
  window.addEventListener('resize', () => {
    pieChart?.resize()
    barChart?.resize()
  })
})

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer)
  pieChart?.dispose()
  barChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  width: 100%;
  height: 100%;
  color: #333;
  background: #f9f9f9;
  padding: 20px;
  box-sizing: border-box;
}

.card-group {
  display: flex;
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  flex: 1;
  background: #fff;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.stat-item {
  text-align: center;
  padding: 15px 0;
}

.stat-label {
  font-size: 14px;
  color: #666;
  display: block;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #000;
}

.content-row {
  display: flex;
  gap: 20px;
  height: calc(100% - 220px);
  margin-bottom: 20px;
}

.left-content {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.right-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.chart-card,
.list-card {
  background: #fff;
  border: 1px solid #e6e6e6;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
}

.map-container {
  height: 100%;
  border: 1px dashed #d9d9d9;
  background: #fafafa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.map-placeholder {
  text-align: center;
}

.map-points {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 15px;
  margin-top: 20px;
}

.point-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #409eff;
}

.list-header,
.chart-header {
  font-size: 16px;
  padding: 10px 20px;
  border-bottom: 1px solid #e6e6e6;
  font-weight: 600;
}

:deep(.el-card__body) {
  padding: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.full-table {
  flex: 1;
  width: 100%;
}

:deep(.el-table) {
  height: 100% !important;
}

:deep(.el-table__body-wrapper) {
  height: auto !important;
  flex: 1;
}

:deep(.el-table__footer-wrapper) {
  display: none;
}

.chart-container {
  flex: 1;
  width: 100%;
}

/* 布局高度分配 */
.left-content > .chart-card {
  height: 100%;
}

.right-content > .list-card {
  flex: 2;
}

.right-content > .chart-card {
  flex: 1;
}

.bottom-chart > .chart-card {
  height: 220px;
}
</style>