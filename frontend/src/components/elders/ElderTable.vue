<template>
  <div class="elder-management-container">
    <el-table
      :data="elderList"
      border
      stripe
      v-loading="loading"
      style="width:100%;margin-top:16px"
    >
      <el-table-column label="ID" prop="elder_id" width="100" />
      <el-table-column label="姓名" prop="name" width="120" />
      <el-table-column label="性别" width="80">
        <template #default="{ row }">
          {{ row.gender === 1 ? '男' : '女' }}
        </template>
      </el-table-column>
      <el-table-column label="年龄" prop="age" width="80" />
      <el-table-column label="家庭住址" prop="address" min-width="180" />
      <el-table-column label="联系人1" prop="family_contact1" width="120" />
      <el-table-column label="电话1" prop="family_phone1" width="140" />
      <el-table-column label="联系人2" prop="family_contact2" width="120" />
      <el-table-column label="电话2" prop="family_phone2" width="140" />
      <el-table-column label="身体条件备注" min-width="200">
        <template #default="{ row }">
          <el-tooltip :content="row.physical_notes || '无'" placement="top">
            <span class="text-ellipsis">{{ row.physical_notes || '无' }}</span>
          </el-tooltip>
        </template>
      </el-table-column>

      <el-table-column label="创建时间" prop="created_at" width="180" />

      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openDetail(row)">查看</el-button>
          <el-button size="small" type="warning" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <elder-form
      v-model="dialogVisible"
      :elder-id="currentId"
      @success="getList"
    />
    <elder-detail v-model="detailVisible" :data="detailData" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import ElderForm from './ElderForm.vue'
import ElderDetail from './ElderDetail.vue'
import { getElderList, deleteElder } from '@/api/elder'
import type { ElderItem } from '@/types/elder'

const loading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const currentId = ref(0)
const elderList = ref<ElderItem[]>([])
const detailData = ref<ElderItem>({} as ElderItem)

const query = reactive({ name: '' })
const pagination = reactive({ pageNum: 1, pageSize: 10 })

onMounted(() => getList())

async function getList() {
  loading.value = true
  try {
    const res = await getElderList(
      { pageNum: pagination.pageNum, pageSize: pagination.pageSize },
      { name: query.name }
    )
    elderList.value = res.data.list
  } finally {
    loading.value = false
  }
}

function openAdd() {
  currentId.value = 0
  dialogVisible.value = true
}

function openEdit(row: ElderItem) {
  currentId.value = row.elder_id
  dialogVisible.value = true
}

function openDetail(row: ElderItem) {
  detailData.value = row
  detailVisible.value = true
}

async function del(row: ElderItem) {
  await deleteElder(row.elder_id)
  ElMessage.success('删除成功')
  getList()
}

function resetQuery() {
  query.name = ''
  getList()
}
</script>

<style scoped>
.elder-management-container { padding: 4px; }
.search-bar { padding: 16px; background: #fff; border-radius: 8px; margin-bottom: 16px; }
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