<template>
  <div class="alarm-map-page">
    <div class="toolbar">
      <div>
        <h2>报警地图</h2>
        <p>WebSocket实时推送，断开后自动回退到定时刷新</p>
      </div>
      <div class="actions">
        <select v-model="statusFilter" @change="reloadAlarms">
          <option :value="undefined">全部</option>
          <option :value="1">待处理</option>
          <option :value="2">已处理</option>
          <option :value="3">误报</option>
        </select>
        <span :class="['ws-status', wsConnected ? 'online' : 'offline']">
          {{ wsConnected ? '实时连接中' : '定时刷新中' }}
        </span>
      </div>
    </div>

    <div class="content">
      <div id="mapContainer" class="map"></div>

      <div class="side-panel">
        <h3>报警列表</h3>
        <div v-for="item in alarmList" :key="item.eventId" class="alarm-card" @click="focusAlarm(item)">
          <div class="row between">
            <strong>{{ item.elderName || '未知老人' }}</strong>
            <span :class="['tag', statusClass(item.status)]">{{ statusText(item.status) }}</span>
          </div>
          <p>{{ item.locationDesc || item.cameraName || '暂无位置描述' }}</p>
          <p>检测时间：{{ item.detectTime || '-' }}</p>
          <p>置信度：{{ item.confidence ?? '-' }}</p>
          <div class="card-actions" v-if="item.status === 1">
            <button @click.stop="handleStatus(item.eventId, 2)">标记已处理</button>
            <button @click.stop="handleStatus(item.eventId, 3)">标记误报</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import AMapLoader from '@amap/amap-jsapi-loader'
import { getAlarmMapList, updateAlarmStatus } from '@/api/alarmMap'
import type { AlarmMapPoint, AlarmWsMessage } from '@/types/alarmMap'

const alarmList = ref<AlarmMapPoint[]>([])
const statusFilter = ref<number | undefined>(undefined)
const wsConnected = ref(false)

let map: any = null
let AMap: any = null
let markers: any[] = []
let socket: WebSocket | null = null
let refreshTimer: number | undefined
let reconnectTimer: number | undefined

onMounted(async () => {
  AMap = await AMapLoader.load({
    key: import.meta.env.VITE_AMAP_KEY,
    version: '2.0'
  })

  map = new AMap.Map('mapContainer', {
    zoom: 13,
    center: [116.397428, 39.90923]
  })

  await reloadAlarms()
  connectWebSocket()
  startFallbackRefresh()
})

onBeforeUnmount(() => {
  socket?.close()
  if (refreshTimer) window.clearInterval(refreshTimer)
  if (reconnectTimer) window.clearTimeout(reconnectTimer)
  clearMarkers()
  map?.destroy()
})

async function reloadAlarms() {
  const res = await getAlarmMapList(statusFilter.value)
  alarmList.value = res.data || []
  renderMarkers()
}

function connectWebSocket() {
  const apiBase = import.meta.env.VITE_API_BASE_URL || window.location.origin
  const wsUrl = apiBase.replace(/^http/, 'ws') + '/ws/alarm'

  socket = new WebSocket(wsUrl)

  socket.onopen = () => {
    wsConnected.value = true
  }

  socket.onmessage = async event => {
    const msg: AlarmWsMessage = JSON.parse(event.data)

    if (msg.type === 'NEW_ALARM' && msg.data) {
      upsertAlarm(msg.data)
      renderMarkers()
      focusAlarm(msg.data)
    }

    if (msg.type === 'ALARM_STATUS_CHANGED') {
      await reloadAlarms()
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

function startFallbackRefresh() {
  refreshTimer = window.setInterval(() => {
    if (!wsConnected.value) {
      reloadAlarms()
    }
  }, 10000)
}

function upsertAlarm(alarm: AlarmMapPoint) {
  const index = alarmList.value.findIndex(item => item.eventId === alarm.eventId)
  if (index >= 0) {
    alarmList.value[index] = alarm
  } else {
    alarmList.value.unshift(alarm)
  }
}

function renderMarkers() {
  clearMarkers()

  markers = alarmList.value.map(item => {
    const marker = new AMap.Marker({
      position: [Number(item.longitude), Number(item.latitude)],
      title: item.locationDesc || item.cameraName || '报警点',
      content: `<div class="alarm-marker ${statusClass(item.status)}"></div>`
    })

    marker.on('click', () => showInfoWindow(item))
    map.add(marker)
    return marker
  })

  if (markers.length) {
    map.setFitView(markers, false, [80, 80, 80, 80])
  }
}

function clearMarkers() {
  if (map && markers.length) {
    map.remove(markers)
  }
  markers = []
}

function showInfoWindow(item: AlarmMapPoint) {
  const infoWindow = new AMap.InfoWindow({
    offset: new AMap.Pixel(0, -28),
    content: `
      <div class="info-window">
        <h4>${item.elderName || '未知老人'}跌倒报警</h4>
        <p>位置：${item.locationDesc || item.cameraName || '-'}</p>
        <p>检测时间：${item.detectTime || '-'}</p>
        <p>置信度：${item.confidence ?? '-'}</p>
        <p>状态：${statusText(item.status)}</p>
      </div>
    `
  })
  infoWindow.open(map, [Number(item.longitude), Number(item.latitude)])
}

function focusAlarm(item: AlarmMapPoint) {
  map.setZoomAndCenter(17, [Number(item.longitude), Number(item.latitude)])
  showInfoWindow(item)
}

async function handleStatus(eventId: number, status: number) {
  await updateAlarmStatus(eventId, {
    status,
    processedBy: 1,
    processNotes: status === 2 ? '地图端标记已处理' : '地图端标记误报'
  })
  await reloadAlarms()
}

function statusText(status: number) {
  if (status === 1) return '待处理'
  if (status === 2) return '已处理'
  if (status === 3) return '误报'
  return '未知'
}

function statusClass(status: number) {
  if (status === 1) return 'pending'
  if (status === 2) return 'done'
  if (status === 3) return 'false-alarm'
  return 'unknown'
}
</script>

<style scoped>
.alarm-map-page {
  height: 100%;
  padding: 16px;
  box-sizing: border-box;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.toolbar h2 {
  margin: 0;
}

.toolbar p {
  margin: 4px 0 0;
  color: #666;
  font-size: 14px;
}

.actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.actions select {
  height: 34px;
  padding: 0 10px;
}

.ws-status {
  font-size: 13px;
  padding: 6px 10px;
  border-radius: 14px;
  background: #f1f1f1;
}

.ws-status.online {
  color: #0f8a35;
}

.ws-status.offline {
  color: #b45b00;
}

.content {
  display: grid;
  grid-template-columns: 1fr 330px;
  gap: 12px;
  height: calc(100vh - 110px);
}

.map {
  width: 100%;
  height: 100%;
  border-radius: 10px;
  overflow: hidden;
}

.side-panel {
  overflow: auto;
  background: #fff;
  border-radius: 10px;
  padding: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.alarm-card {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 10px;
  cursor: pointer;
}

.alarm-card p {
  margin: 6px 0;
  color: #555;
  font-size: 13px;
}

.row.between {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tag {
  font-size: 12px;
  border-radius: 12px;
  padding: 3px 8px;
  background: #eee;
}

.tag.pending {
  color: #c62828;
}

.tag.done {
  color: #2e7d32;
}

.tag.false-alarm {
  color: #777;
}

.card-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.card-actions button {
  border: 0;
  border-radius: 6px;
  padding: 6px 10px;
  cursor: pointer;
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

.info-window {
  min-width: 210px;
  font-size: 13px;
}

.info-window h4 {
  margin: 0 0 8px;
}

.info-window p {
  margin: 5px 0;
}

@keyframes alarm-pulse {
  0% { box-shadow: 0 0 0 0 rgba(229, 57, 53, 0.55); }
  70% { box-shadow: 0 0 0 14px rgba(229, 57, 53, 0); }
  100% { box-shadow: 0 0 0 0 rgba(229, 57, 53, 0); }
}
</style>
