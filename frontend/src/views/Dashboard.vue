<template>
  <div class="dashboard-container">
    <!-- KPI 卡片 -->
    <div class="card-group">
      <el-card class="stat-card">
        <div class="stat-item">
          <span class="stat-label">在住老人</span>
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
          <span class="stat-label">摔倒事件</span>
          <span class="stat-value">{{ fallEventTotal }}</span>
        </div>
      </el-card>
      <el-card class="stat-card">
        <div class="stat-item">
          <span class="stat-label">设备在线率</span>
          <span class="stat-value">{{ cameraOnlineRate }}</span>
        </div>
      </el-card>
    </div>

    <!-- 主内容 -->
    <div class="content-row">
      <!-- 左侧地图 -->
      <div class="left-content">
        <el-card class="map-card">
          <div class="map-header">
            <span>老人位置分布地图</span>
            <span :class="['ws-status', wsConnected ? 'online' : 'offline']">
              {{ wsConnected ? '实时连接中' : '定时刷新中' }}
            </span>
          </div>
          <div ref="alarmMapRef" class="real-map"></div>
        </el-card>
      </div>

      <!-- 右侧告警表 + 饼图 -->
      <div class="right-content">
        <el-card class="list-card">
          <div class="list-header">最新告警记录</div>
          <el-table :data="alertList" border class="full-table" v-loading="loading">
            <el-table-column label="告警时间" width="160">
              <template #default="scope">{{ scope.row.detectTime || scope.row.fallTime || '-' }}</template>
            </el-table-column>
            <el-table-column label="老人姓名">
              <template #default="scope">{{ scope.row.elderName || '未知老人' }}</template>
            </el-table-column>
            <el-table-column label="告警类型">
              <template #default>摔倒告警</template>
            </el-table-column>
            <el-table-column label="处理状态">
              <template #default="scope">
                <el-tag v-if="scope.row.status===1" type="warning">待处理</el-tag>
                <el-tag v-else-if="scope.row.status===2" type="success">已处理</el-tag>
                <el-tag v-else-if="scope.row.status===3" type="info">误报</el-tag>
                <el-tag v-else type="info">未知</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>

        <el-card class="chart-card">
          <div class="chart-header">事件状态分布</div>
          <div ref="pieChartRef" class="chart-container"></div>
        </el-card>
      </div>
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
const alarmMapRef = ref<HTMLDivElement | null>(null)

let pieChart: echarts.ECharts | null = null
let AMap: any = null
let map: any = null
let markers: any[] = []

let socket: WebSocket | null = null
const wsConnected = ref(false)
let refreshTimer: number | null = null
let alarmRefreshTimer: number | null = null
let reconnectTimer: number | null = null

// 加载 KPI + 告警列表
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
        .sort((a,b)=>new Date(b.detectTime||b.fallTime).getTime() - new Date(a.detectTime||a.fallTime).getTime())
        .slice(0,5)

    const cameraRes = await getCameraList({ pageNum: 1, pageSize: 1000 })
    const cameraList = cameraRes.data?.records ?? []
    const online = cameraList.filter(it=>it.status===1).length
    cameraOnlineRate.value = `${Math.round((online/(cameraList.length||1))*100)}%`

    nextTick(() => { if(pieChartRef.value) initCharts(fallList) })
  } finally { loading.value = false }
}

// === 地图逻辑 ===
const loadAlarmMapData = async () => {
  try {
    const res = await getAlarmMapList()
    const data = Array.isArray(res.data)?res.data:(Array.isArray((res.data as any)?.data)?(res.data as any).data:[])
    alarmMapList.value = data.filter(item=>item.longitude != null && item.latitude != null)
    renderAlarmMarkers()
  } catch(e){ console.error(e) }
}

const initAlarmMap = async () => {
  if(!alarmMapRef.value) return
  AMap = await AMapLoader.load({key:import.meta.env.VITE_AMAP_KEY,version:'2.0'})
  map = new AMap.Map(alarmMapRef.value, { zoom: 13, center: [116.397428,39.90923], resizeEnable: true })
  await loadAlarmMapData()
  connectWebSocket()
}

const renderAlarmMarkers = () => {
  if(!map || !AMap) return
  clearMarkers()
  markers = alarmMapList.value.map(item=>{
    const marker = new AMap.Marker({
      position: [Number(item.longitude), Number(item.latitude)],
      title: item.locationDesc || item.cameraName || '报警点',
      content: `<div class="alarm-marker ${statusClass(item.status)}"></div>`
    })
    marker.on('click',()=>showAlarmInfoWindow(item))
    map.add(marker)
    return marker
  })
  if(markers.length) map.setFitView(markers, false, [60,60,60,60])
}

const clearMarkers = () => { if(map && markers.length) map.remove(markers); markers = [] }

const showAlarmInfoWindow = (item:any) => {
  if(!map || !AMap) return
  const infoWindow = new AMap.InfoWindow({
    offset: new AMap.Pixel(0,-28),
    content: `<div class="info-window"><h4>${item.elderName||'未知老人'} 跌倒报警</h4>
      <p>位置：${item.locationDesc||item.cameraName||'-'}</p>
      <p>检测时间：${item.detectTime||'-'}</p>
      <p>置信度：${item.confidence??'-'}</p>
      <p>状态：${statusText(item.status)}</p>
    </div>`
  })
  infoWindow.open(map, [Number(item.longitude), Number(item.latitude)])
}

const connectWebSocket = () => {
  const apiBase = import.meta.env.VITE_API_BASE_URL||window.location.origin
  socket = new WebSocket(apiBase.replace(/^http/,'ws')+'/ws/alarm')
  socket.onopen = ()=> wsConnected.value = true
  socket.onmessage = async e => {
    const msg = JSON.parse(e.data)
    if(msg.type==='NEW_ALARM' || msg.type==='ALARM_STATUS_CHANGED'){
      await loadAlarmMapData(); await loadDashboardData()
    }
  }
  socket.onclose = ()=> { wsConnected.value = false; reconnectTimer = setTimeout(connectWebSocket,5000) }
  socket.onerror = ()=> { wsConnected.value = false; socket?.close() }
}

// === 饼图 ===
const initCharts = (fallList:any[]) => {
  const statusMap:Record<number,string> = {1:'待处理',2:'已处理',3:'误报'}
  const pieData = Object.entries(fallList.reduce((acc:any,item:any)=>{
    acc[item.status??1] = (acc[item.status??1]||0)+1
    return acc
  }, {})).map(([k,v]) => ({ name: statusMap[Number(k)]||'未知', value: v }))

  if(pieChartRef.value){
    pieChart = pieChart || echarts.init(pieChartRef.value)
    pieChart.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      series: [{
        type: 'pie',
        radius: ['40%','70%'],
        avoidLabelOverlap: false,
        itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
        label: { show: false },
        emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
        data: pieData
      }]
    })
  }
}

const statusText = (status:number) => status===1?'待处理':status===2?'已处理':status===3?'误报':'未知'
const statusClass = (status:number) => status===1?'pending':status===2?'done':status===3?'false-alarm':'unknown'

onMounted(async () => {
  await nextTick()
  await loadDashboardData()
  await initAlarmMap()
  refreshTimer = setInterval(loadDashboardData, 30000)
  alarmRefreshTimer = setInterval(()=>{
    if(!wsConnected.value) loadAlarmMapData()
    nextTick(() => { if(pieChartRef.value) pieChart?.resize() })
  }, 10000)

  window.addEventListener('resize', () => {
    map?.resize()
    pieChart?.resize()
  })
})

onUnmounted(()=>{
  refreshTimer && clearInterval(refreshTimer)
  alarmRefreshTimer && clearInterval(alarmRefreshTimer)
  reconnectTimer && clearTimeout(reconnectTimer)
  socket?.close()
  clearMarkers()
  map?.destroy()
  pieChart?.dispose()
  window.removeEventListener('resize', () => {})
})
</script>

<style scoped>
/* ===== 页面背景（统一风格） ===== */
.dashboard-container {
  position: relative;
  min-height: 100vh;
  padding: 24px;
  box-sizing: border-box;
  overflow: hidden;
  background:
      radial-gradient(circle at 12% 10%, rgba(255, 255, 255, 0.95), transparent 26%),
      radial-gradient(circle at 88% 18%, rgba(191, 219, 254, 0.5), transparent 30%),
      radial-gradient(circle at 48% 92%, rgba(204, 251, 241, 0.42), transparent 34%),
      linear-gradient(135deg, #eef4fb 0%, #e7edf6 48%, #f7f9fd 100%);
  color: #2f3b52;
}
.dashboard-container,
.content-row,
.left-content,
.right-content {
  overflow: hidden;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

.dashboard-container::-webkit-scrollbar,
.content-row::-webkit-scrollbar,
.left-content::-webkit-scrollbar,
.right-content::-webkit-scrollbar {
  display: none;
}
/* 背景装饰圆 */
.dashboard-container::before,
.dashboard-container::after {
  content: "";
  position: absolute;
  border-radius: 999px;
  background: #edf3fa;
  box-shadow:
      18px 18px 40px rgba(163, 177, 198, 0.28),
      -18px -18px 40px rgba(255, 255, 255, 0.86);
  pointer-events: none;
  z-index: 0;
}

.dashboard-container::before {
  width: 260px;
  height: 260px;
  top: 8%;
  left: 5%;
}

.dashboard-container::after {
  width: 340px;
  height: 340px;
  right: 6%;
  bottom: 8%;
}

/* 内容浮在背景之上 */
.dashboard-container > * {
  position: relative;
  z-index: 1;
}

/* ===== KPI 卡片 ===== */
.card-group {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.55);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255,255,255,0.6);
  box-shadow:
      16px 16px 36px rgba(163, 177, 198, 0.34),
      -16px -16px 36px rgba(255, 255, 255, 0.92);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  gap: 8px;
}

.stat-label {
  font-size: 13px;
  color: #7b8798;
  letter-spacing: 0.05em;
}

.stat-value {
  font-size: 32px;
  font-weight: 800;
  color: #2f3b52;
}

/* ===== 布局 ===== */
.content-row {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
}

.left-content,
.right-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* ===== 通用卡片（统一风格） ===== */
.map-card {
  height: 752px;
  display: flex;
  flex-direction: column;
}

.real-map {
  flex: 1;
  width: 100%;
  min-height: 400px;
  border-radius: 16px;
}
.map-card,
.list-card,
.chart-card {
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.55);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255,255,255,0.6);
  box-shadow:
      16px 16px 36px rgba(163, 177, 198, 0.34),
      -16px -16px 36px rgba(255, 255, 255, 0.92);
  display: flex;
  flex-direction: column;
}

/* ===== 头部 ===== */
.map-header,
.list-header,
.chart-header {
  font-size: 16px;
  padding: 12px 20px;
  border-bottom: 1px solid rgba(203,213,225,0.5);
  font-weight: 600;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* ===== 地图（关键优化） ===== */
.real-map {
  flex: 1;
  width: 100%;
  height: 100%;
  border-radius: 16px;
  overflow: hidden;
}

/* ===== 表格 ===== */
.full-table {
  flex: 1;
  width: 100%;
}

/* ===== 图表 ===== */
.chart-container {
  flex: 1;
  width: 100%;
  height: 300px;
}

/* ===== WS 状态 ===== */
.ws-status {
  font-size: 12px;
  padding: 2px 10px;
  border-radius: 999px;
}

.ws-status.online {
  color: #22c55e;
  background: rgba(34,197,94,0.12);
}

.ws-status.offline {
  color: #f59e0b;
  background: rgba(245,158,11,0.12);
}
</style>

<style>
.alarm-marker{width:18px;height:18px;border-radius:50%;border:3px solid #fff;box-shadow:0 0 12px rgba(0,0,0,.35)}
.alarm-marker.pending{background:#e53935;animation:alarm-pulse 1.2s infinite}
.alarm-marker.done{background:#43a047}
.alarm-marker.false-alarm{background:#9e9e9e}
.alarm-marker.unknown{background:#909399}
.info-window{min-width:220px;font-size:13px}
.info-window h4{margin:0 0 8px}
.info-window p{margin:5px 0}
@keyframes alarm-pulse{0%{box-shadow:0 0 0 0 rgba(229,57,53,.55)}70%{box-shadow:0 0 0 14px rgba(229,57,53,0)}100%{box-shadow:0 0 0 0 rgba(229,57,53,0)}}
</style>