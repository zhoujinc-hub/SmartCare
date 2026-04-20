<template>
  <div class="fall-event-page">
    <el-card class="search-card" shadow="hover">
      <el-form :inline="true" :model="queryParams" label-width="70px">
        <el-form-item label="老人姓名">
          <el-input v-model="queryParams.elderName" placeholder="输入老人姓名" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable>
            <el-option label="待处理" :value="1" />
            <el-option label="已处理" :value="2" />
            <el-option label="误报" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="摄像头ID">
          <el-input v-model.number="queryParams.cameraId" placeholder="ID" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="hover">
      <el-table
          :data="list"
          border
          stripe
          v-loading="loading"
          @row-click="openDetail"
      >
        <el-table-column prop="eventId" label="事件ID" width="90" align="center" />
        <el-table-column prop="cameraId" label="摄像头ID" width="100" align="center" />
        <el-table-column prop="elderName" label="老人/路人" />
        <el-table-column prop="isRegistered" label="注册状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isRegistered ? 'success' : 'info'">
              {{ row.isRegistered ? '已注册' : '未注册' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fallTime" label="跌倒时间" width="180" align="center" />
        <el-table-column prop="confidence" label="置信度" width="80" align="center" />
        <el-table-column prop="status" label="处理状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="
              row.status === 1 ? 'warning' :
              row.status === 2 ? 'success' : 'danger'
            ">
              {{
                row.status === 1 ? '待处理' :
                    row.status === 2 ? '已处理' : '误报'
              }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click.stop="openDetail(row)">查看详情</el-button>
            <el-button type="success" link @click.stop="openHandle(row)">标记处理</el-button>
            <el-button type="info" link @click.stop="openNotes(row)">添加备注</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-box" style="margin-top:16px; text-align:right">
        <el-pagination
            v-model:current-page="queryParams.pageNum"
            v-model:page-size="queryParams.pageSize"
            :total="total"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="getList"
            @current-change="getList"
        />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="跌倒事件详情" width="700px">
      <div v-if="currentRow">
        <el-descriptions border :column="1">
          <el-descriptions-item label="事件ID">{{ currentRow.eventId }}</el-descriptions-item>
          <el-descriptions-item label="摄像头ID">{{ currentRow.cameraId }}</el-descriptions-item>
          <el-descriptions-item label="老人姓名">{{ currentRow.elderName || '未注册' }}</el-descriptions-item>
          <el-descriptions-item label="跌倒时间">{{ currentRow.fallTime }}</el-descriptions-item>
          <el-descriptions-item label="检测时间">{{ currentRow.detectTime }}</el-descriptions-item>
          <el-descriptions-item label="置信度">{{ currentRow.confidence }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            {{ currentRow.status === 1 ? '待处理' : currentRow.status === 2 ? '已处理' : '误报' }}
          </el-descriptions-item>
          <el-descriptions-item label="备注">{{ currentRow.processNotes || '无' }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>

    <!-- 处理弹窗 -->
    <el-dialog v-model="handleVisible" title="处理事件" width="500px">
      <el-form :model="handleForm" label-width="80px">
        <el-form-item label="处理结果">
          <el-select v-model="handleForm.status">
            <el-option label="已处理" :value="2" />
            <el-option label="误报" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理备注">
          <el-input v-model="handleForm.processNotes" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleVisible = false">取消</el-button>
        <el-button type="primary" @click="submitHandle">确认提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getFallEventList,
  handleFallEvent
} from '@/api/fallEvent'

// 定义行数据类型
interface FallEventRow {
  eventId: number
  cameraId: number
  elderId?: number
  elderName?: string
  isRegistered: 0 | 1
  fallTime: string
  detectTime: string
  confidence?: number
  status: 1 | 2 | 3
  processNotes?: string
}

const loading = ref(false)
const list = ref<FallEventRow[]>([])
const total = ref(0)

const queryParams = reactive({
  elderName: '',
  cameraId: undefined as number | undefined,
  status: undefined as number | undefined,
  pageNum: 1,
  pageSize: 10
})

const currentRow = ref<FallEventRow | null>(null)
const detailVisible = ref(false)
const handleVisible = ref(false)

const handleForm = reactive({
  eventId: 0,
  status: 2 as 2 | 3,
  processNotes: ''
})

const getList = async () => {
  loading.value = true
  try {
    const res = await getFallEventList(queryParams)
    list.value = res.data.list || []
    total.value = res.data.total || 0
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryParams.elderName = ''
  queryParams.cameraId = undefined
  queryParams.status = undefined
  queryParams.pageNum = 1
  getList()
}

const openDetail = (row: FallEventRow) => {
  currentRow.value = row
  detailVisible.value = true
}

const openHandle = (row: FallEventRow) => {
  currentRow.value = row
  handleForm.eventId = row.eventId
  handleForm.status = 2
  handleForm.processNotes = row.processNotes || ''
  handleVisible.value = true
}

const openNotes = (row: FallEventRow) => {
  openHandle(row)
}

const submitHandle = async () => {
  try {
    await handleFallEvent(handleForm.eventId, {
      status: handleForm.status,
      processNotes: handleForm.processNotes
    })
    ElMessage.success('操作成功！')
    handleVisible.value = false
    getList()
  } catch (err) {
    ElMessage.error('操作失败，请重试')
    console.error(err)
  }
}

onMounted(() => getList())
</script>

<style scoped>
.fall-event-page {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}
.search-card {
  margin-bottom: 16px;
}
.table-card {
  padding: 16px;
}
.pagination-box {
  margin-top: 16px;
  text-align: right;
}
</style>