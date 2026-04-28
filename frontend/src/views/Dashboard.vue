<template>
  <div class="dashboard-container">

    <!-- ===== KPI ===== -->
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

    <!-- ===== 主内容 Grid ===== -->
    <div class="content-grid">

      <!-- 地图 -->
      <el-card class="map-card">
        <div class="section-title">老人位置分布</div>

        <div class="map-container">
          <div class="map-placeholder">
            <p>老人位置分布地图</p>

            <div class="map-points">
              <div
                  v-for="elder in elderList"
                  :key="elder.elderId"
                  class="map-point"
              >
                <el-tooltip :content="elder.realName">
                  <div class="point-dot"></div>
                </el-tooltip>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 告警列表 -->
      <el-card class="list-card">
        <div class="section-title">最新告警记录</div>

        <el-table :data="alertList" v-loading="loading" class="soft-table">
          <el-table-column label="时间" width="160">
            <template #default="scope">
              {{ scope.row.detectTime || scope.row.fallTime || '-' }}
            </template>
          </el-table-column>

          <el-table-column label="老人">
            <template #default="scope">
              {{ scope.row.elderName || '未知老人' }}
            </template>
          </el-table-column>

          <el-table-column label="类型">
            摔倒告警
          </el-table-column>

          <el-table-column label="状态">
            <template #default="scope">
              <el-tag v-if="scope.row.status === 1" type="warning">待处理</el-tag>
              <el-tag v-else-if="scope.row.status === 2" type="success">已处理</el-tag>
              <el-tag v-else-if="scope.row.status === 3" type="info">误报</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 饼图 -->
      <el-card class="chart-card">
        <div class="section-title">事件状态分布</div>
        <div ref="pieChartRef" class="chart-container"></div>
      </el-card>

    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
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

let pieChart: echarts.ECharts | null = null

const loadDashboardData = async () => {
  loading.value = true
  try {
    const elderRes = await getElderList({ pageNum: 1, pageSize: 1000 })
    elderList.value = elderRes.data?.records ?? []
    elderTotal.value = elderList.value.length

    const fallRes = await getFallEventList({ pageNum: 1, pageSize: 1000 })
    const fallList = fallRes.data?.records ?? []

    alertTotal.value = fallList.length
    fallEventTotal.value = fallList.length

    alertList.value = fallList.slice(0, 5)

    const cameraRes = await getCameraList({ pageNum: 1, pageSize: 1000 })
    const cameraList = cameraRes.data?.records ?? []

    const online = cameraList.filter((i: any) => i.status === 1).length
    cameraOnlineRate.value = `${Math.round((online / cameraList.length) * 100)}%`

    initCharts()
  } finally {
    loading.value = false
  }
}

const initCharts = () => {
  if (pieChartRef.value) {
    pieChart = echarts.init(pieChartRef.value)
    pieChart.setOption({
      series: [{
        type: 'pie',
        radius: ['45%', '70%'],
        data: [
          { value: 10, name: '待处理' },
          { value: 20, name: '已处理' },
          { value: 5, name: '误报' }
        ]
      }]
    })
  }
}

onMounted(loadDashboardData)
</script>

<style scoped>

/* ===== 背景 ===== */
.dashboard-container {
  padding: 24px;
  background:
      radial-gradient(circle at 10% 10%, #ffffff, transparent 30%),
      linear-gradient(135deg, #eef3f8, #f8fbff);
}

/* ===== KPI ===== */
.card-group {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 20px;
  background: linear-gradient(145deg, #f8fbff, #e7edf6);
  box-shadow: 10px 10px 24px #cfd8e3, -10px -10px 24px #ffffff;
}

/* ⭐⭐⭐ 关键修改在这里 ⭐⭐⭐ */
.stat-item {
  display: flex;
  flex-direction: column;   /* 👈 改成上下结构 */
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
  line-height: 1;
}

/* ===== GRID ===== */
.content-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  grid-template-rows: 360px 240px;
  gap: 20px;
}

/* ===== 地图 ===== */
.map-card {
  grid-row: span 2;
  border-radius: 24px;
  background: linear-gradient(145deg, #eef3f8, #ffffff);
  box-shadow: inset 10px 10px 24px #cfd8e3, inset -10px -10px 24px #ffffff;
}

/* ===== 普通卡 ===== */
.chart-card, .list-card {
  border-radius: 24px;
  background: linear-gradient(145deg, #f8fbff, #e7edf6);
  box-shadow: 10px 10px 24px #cfd8e3, -10px -10px 24px #ffffff;
}

.section-title {
  padding: 14px;
  font-weight: bold;
}

.map-container {
  margin: 12px;
  height: calc(100% - 50px);
  border-radius: 18px;
  box-shadow: inset 6px 6px 12px #cfd8e3, inset -6px -6px 12px #ffffff;
}

.point-dot {
  width: 10px;
  height: 10px;
  background: #409eff;
  border-radius: 50%;
}

.chart-container {
  height: calc(100% - 40px);
}

/* ===== 响应式 ===== */
@media (max-width: 1100px) {
  .content-grid {
    grid-template-columns: 1fr;
  }
}

</style>