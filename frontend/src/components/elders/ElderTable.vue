<template>
  <div class="elder-management-container">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input
          v-model="query.name"
          placeholder="输入老人姓名搜索"
          style="width: 260px"
          clearable
          @keyup.enter="getList"
      />
      <el-button type="primary" @click="getList" style="margin-left: 8px">
        <el-icon><Search /></el-icon> 查询
      </el-button>
      <el-button type="success" @click="openAdd" style="margin-left: 8px">
        <el-icon><Plus /></el-icon> 新增
      </el-button>
      <el-button @click="resetQuery" style="margin-left: 8px">重置</el-button>
    </div>

    <el-table
        :data="elderList"
        border
        stripe
        v-loading="loading"
        style="width:100%;margin-top:16px"
    >
      <el-table-column label="ID" prop="elderId" width="100" />
      <el-table-column label="姓名" prop="name" width="120" />
      <el-table-column label="性别" width="80">
        <template #default="{ row }">
          {{ row.gender === 1 ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column label="年龄" prop="age" width="80" />
      <el-table-column label="家庭住址" prop="address" min-width="180" />
      <el-table-column label="健康备注" prop="healthNotes" min-width="180" />
      <el-table-column label="创建时间" prop="createdAt" width="180" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openDetail(row)">查看</el-button>
          <el-button size="small" type="warning" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="total"
        layout="prev, pager, next, jumper, ->, total"
        @size-change="getList"
        @current-change="getList"
        style="margin-top:16px; text-align:right"
    />

    <ElderForm
        v-model="dialogVisible"
        :elder-id="currentId"
        @success="getList"
    />
    <ElderDetail v-model="detailVisible" :data="detailData" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import ElderForm from './ElderForm.vue'
import ElderDetail from './ElderDetail.vue'
import { getElderList, deleteElder } from '@/api/elder'
import type { ElderItem } from '@/types/elder'

const loading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const currentId = ref<number | undefined>(undefined)
const elderList = ref<ElderItem[]>([])
const detailData = ref<ElderItem | undefined>(undefined)
const total = ref(0)

const query = reactive({ name: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10 })

onMounted(() => getList())

async function getList() {
  loading.value = true
  try {
    const res = await getElderList(
        {
          pageNum: pagination.pageNum,
          pageSize: pagination.pageSize,
          name: query.name
        },

    )
    elderList.value = res.data?.list ?? []
    total.value = res.data?.total ?? 0

  } catch (err) {
    ElMessage.error('加载列表失败')
  } finally {
    loading.value = false
  }
}

function openAdd() {
  currentId.value = undefined
  dialogVisible.value = true
}

function openEdit(row: ElderItem) {
  currentId.value = row.elderId
  dialogVisible.value = true
}

function openDetail(row: ElderItem) {
  detailData.value = row
  detailVisible.value = true
}

async function handleDelete(row: ElderItem) {
  try {
    await ElMessageBox.confirm('确定删除？', '提示')
    // 额外防护：确保 elderId 存在再调用删除
    if (!row.elderId) {
      ElMessage.warning('老人ID不存在，无法删除')
      return
    }
    await deleteElder(row.elderId)
    ElMessage.success('删除成功')
    getList()
  } catch (err) {
    // 区分取消操作和真实错误
    if (err !== 'cancel') {
      ElMessage.error('删除失败')
    } else {
      ElMessage.info('已取消')
    }
  }
}

function resetQuery() {
  query.name = ''
  pagination.pageNum = 1
  getList()
}
</script>

<style scoped>
.elder-management-container { padding: 4px; }
.search-bar {
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
  align-items: center;
}
.text-ellipsis {
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
:deep(.el-table__header .el-table__cell) {
  color: #1f2937 !important;
  font-weight: 600;
}
:deep(.el-table__empty-text) {
  color: #6b7280 !important;
}
</style>