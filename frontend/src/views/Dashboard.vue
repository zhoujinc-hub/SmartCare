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
      <!-- 左侧：报警地图 -->
      <div class="left-content">
        <el-card class="chart-card map-card">
          <div class="map-header">
            <span>报警位置分布地图</span>
            <span :class="['ws-status', wsConnected ? 'online' : 'offline']">
              {{ wsConnected ? '实时连接中' : '定时刷新中' }}
            </span>
          </div>

          <div ref="alarmMapRef" class="real-map"></div>
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
              <template #default>
                摔倒告警
              </template>
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
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { getElderList } from '@/api/elder'
import { getFallEventList } from '@/api/fallEvent'
import { getCameraList } from '@/api/camera'
import { getAlarmMapList } from '@/api/alarmMap'
import AMapLoader from '@amap/amap-jsapi-loader'
import * as echarts from 'echarts'

const loading = ref(false)

const elderTotal = ref(0)
const alertTotal = ref(0)
const fallEventTotal = ref(0)
const cameraOnlineRate = ref('0%')

const elderList = ref<any[]>([])
const alertList = ref<any[]>([])
const alarmMapList = ref<any[]>([])

const pieChartRef = ref<HTMLDivElement | null>(null)
const barChartRef = ref<HTMLDivElement | null>(null)
const alarmMapRef = ref<HTMLDivElement | null>(null)

let pieChart: echarts.ECharts | null = null
let barChart: echarts.ECharts | null = null

let AMap: any = null
let map: any = null
let markers: any[] = []

let socket: WebSocket | null = null
const wsConnected = ref(false)

let refreshTimer: number | null = null
let alarmRefreshTimer: number | null = null
let reconnectTimer: number | null = null

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
        .sort((a: any, b: any) => {
          return new Date(b.detectTime || b.fallTime).getTime() - new Date(a.detectTime || a.fallTime).getTime()
        })
        .slice(0, 5)

    const cameraRes = await getCameraList({ pageNum: 1, pageSize: 1000 })
    const cameraList = cameraRes.data?.records ?? []
    const online = cameraList.filter((it: any) => it.status === 1).length
    cameraOnlineRate.value = `${Math.round((online / (cameraList.length || 1)) * 100)}%`

    initCharts(fallList)
  } catch (err) {
    console.error('Dashboard 数据加载失败：', err)
  } finally {
    loading.value = false
  }
}

const loadAlarmMapData = async () => {
  try {
    const res = await getAlarmMapList()

    /**
     * 兼容两种返回：
     * 1. axios 原始返回：res.data = []
     * 2. 后端统一封装：res.data.data = []
     */
    const data = Array.isArray(res.data)
        ? res.data
        : Array.isArray((res.data as any)?.data)
            ? (res.data as any).data
            : []

    alarmMapList.value = data.filter((item: any) => {
      return item.longitude !== null &&
          item.longitude !== undefined &&
          item.latitude !== null &&
          item.latitude !== undefined
    })

    renderAlarmMarkers()
  } catch (err) {
    console.error('报警地图数据加载失败：', err)
  }
}

const initAlarmMap = async () => {
  if (!alarmMapRef.value) return

  try {
    AMap = await AMapLoader.load({
      key: import.meta.env.VITE_AMAP_KEY,
      version: '2.0'
    })

    map = new AMap.Map(alarmMapRef.value, {
      zoom: 13,
      center: [116.397428, 39.90923],
      resizeEnable: true
    })

    await loadAlarmMapData()
    connectWebSocket()
  } catch (err) {
    console.error('高德地图初始化失败：', err)
  }
}

const renderAlarmMarkers = () => {
  if (!map || !AMap) return

  clearMarkers()

  markers = alarmMapList.value.map((item: any) => {
    const marker = new AMap.Marker({
      position: [Number(item.longitude), Number(item.latitude)],
      title: item.locationDesc || item.cameraName || '报警点',
      content: `<div class="alarm-marker ${statusClass(item.status)}"></div>`
    })

    marker.on('click', () => {
      showAlarmInfoWindow(item)
    })

    map.add(marker)
    return marker
  })

  if (markers.length > 0) {
    map.setFitView(markers, false, [60, 60, 60, 60])
  }
}

const clearMarkers = () => {
  if (map && markers.length > 0) {
    map.remove(markers)
  }
  markers = []
}

const showAlarmInfoWindow = (item: any) => {
  if (!map || !AMap) return

  const infoWindow = new AMap.InfoWindow({
    offset: new AMap.Pixel(0, -28),
    content: `
      <div class="info-window">
        <h4>${item.elderName || '未知老人'} 跌倒报警</h4>
        <p>位置：${item.locationDesc || item.cameraName || '-'}</p>
        <p>检测时间：${item.detectTime || '-'}</p>
        <p>置信度：${item.confidence ?? '-'}</p>
        <p>状态：${statusText(item.status)}</p>
      </div>
    `
  })

  infoWindow.open(map, [Number(item.longitude), Number(item.latitude)])
}

const connectWebSocket = () => {
  const apiBase = import.meta.env.VITE_API_BASE_URL || window.location.origin
  const wsUrl = apiBase.replace(/^http/, 'ws') + '/ws/alarm'

  socket = new WebSocket(wsUrl)

  socket.onopen = () => {
    wsConnected.value = true
    console.log('报警 WebSocket 已连接')
  }

  socket.onmessage = async (event) => {
    try {
      const msg = JSON.parse(event.data)

      if (msg.type === 'NEW_ALARM') {
        await loadAlarmMapData()
        await loadDashboardData()
      }

      if (msg.type === 'ALARM_STATUS_CHANGED') {
        await loadAlarmMapData()
        await loadDashboardData()
      }
    } catch (err) {
      console.error('报警 WebSocket 消息解析失败：', err)
    }
  }

  socket.onclose = () => {
    wsConnected.value = false
    reconnectTimer = window.setTimeout(connectWebSocket, 5000)
  }

  socket.onerror = () => {
    wsConnected.value = false
    socket?.close()
  }
}

const initCharts = (fallList: any[]) => {
  const statusMap: Record<number, string> = {
    1: '待处理',
    2: '已处理',
    3: '误报'
  }

  const pieData = Object.entries(
      fallList.reduce((acc: any, item: any) => {
        const status = item.status ?? 1
        acc[status] = (acc[status] || 0) + 1
        return acc
      }, {})
  ).map(([key, value]) => ({
    name: statusMap[Number(key)] || '未知',
    value
  }))

  if (pieChartRef.value) {
    if (!pieChart) {
      pieChart = echarts.init(pieChartRef.value)
    }

    pieChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [
        {
          type: 'pie',
          radius: ['40%', '70%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: { show: false },
          emphasis: {
            label: {
              show: true,
              fontSize: 14,
              fontWeight: 'bold'
            }
          },
          data: pieData
        }
      ]
    })
  }

  const days = Array.from({ length: 7 }, (_, i) => {
    const d = new Date()
    d.setDate(d.getDate() - (6 - i))
    return d.toISOString().slice(5, 10)
  })

  const dayCount: Record<string, number> = {}

  fallList.forEach((item: any) => {
    const dateStr = (item.detectTime || item.fallTime || '').slice(5, 10)
    if (dateStr) {
      dayCount[dateStr] = (dayCount[dateStr] || 0) + 1
    }
  })

  const barData = days.map((day) => dayCount[day] || 0)

  if (barChartRef.value) {
    if (!barChart) {
      barChart = echarts.init(barChartRef.value)
    }

    barChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: days
      },
      yAxis: {
        type: 'value'
      },
      series: [
        {
          type: 'bar',
          data: barData,
          itemStyle: {
            color: '#409eff'
          }
        }
      ]
    })
  }
}

const statusText = (status: number) => {
  if (status === 1) return '待处理'
  if (status === 2) return '已处理'
  if (status === 3) return '误报'
  return '未知'
}

const statusClass = (status: number) => {
  if (status === 1) return 'pending'
  if (status === 2) return 'done'
  if (status === 3) return 'false-alarm'
  return 'unknown'
}

const handleResize = () => {
  pieChart?.resize()
  barChart?.resize()
  map?.resize()
}

onMounted(async () => {
  await nextTick()

  await loadDashboardData()
  await initAlarmMap()

  refreshTimer = window.setInterval(loadDashboardData, 30000)

  alarmRefreshTimer = window.setInterval(() => {
    if (!wsConnected.value) {
      loadAlarmMapData()
    }
  }, 10000)

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer)
  if (alarmRefreshTimer) clearInterval(alarmRefreshTimer)
  if (reconnectTimer) clearTimeout(reconnectTimer)

  socket?.close()

  clearMarkers()
  map?.destroy()

  pieChart?.dispose()
  barChart?.dispose()

  window.removeEventListener('resize', handleResize)
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

.map-card {
  height: 100%;
  overflow: hidden;
}

.map-header,
.list-header,
.chart-header {
  font-size: 16px;
  padding: 10px 20px;
  border-bottom: 1px solid #e6e6e6;
  font-weight: 600;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.real-map {
  flex: 1;
  width: 100%;
  min-height: 420px;
}

.ws-status {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 14px;
  background: #f1f1f1;
  font-weight: normal;
}

.ws-status.online {
  color: #0f8a35;
}

.ws-status.offline {
  color: #b45b00;
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

<style>
.alarm-marker {
  width: 18px;
  height: 18px;
  border-radius: 50%;
  border: 3px solid #fff;
  box-shadow: 0 0 12px rgba(0, 0, 0, 0.35);
}

.alarm-marker.pending {
  background: #e53935;
  animation: alarm-pulse 1.2s infinite;
}

.alarm-marker.done {
  background: #43a047;
}

.alarm-marker.false-alarm {
  background: #9e9e9e;
}

.alarm-marker.unknown {
  background: #909399;
}

.info-window {
  min-width: 220px;
  font-size: 13px;
}

.info-window h4 {
  margin: 0 0 8px;
}

.info-window p {
  margin: 5px 0;
}

@keyframes alarm-pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(229, 57, 53, 0.55);
  }

  70% {
    box-shadow: 0 0 0 14px rgba(229, 57, 53, 0);
  }

  100% {
    box-shadow: 0 0 0 0 rgba(229, 57, 53, 0);
  }
}
</style>