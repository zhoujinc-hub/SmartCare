<template>
  <div class="elder-management-container">
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
        <el-icon><Plus /></el-icon> 新增老人
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
      <el-table-column label="身体条件备注" min-width="200">
        <template #default="{ row }">
          <el-tooltip :content="row.healthNotes || '无'" placement="top">
            <span class="text-ellipsis">{{ row.healthNotes || '无' }}</span>
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button size="small" type="primary" @click="openDetail(row)">查看</el-button>
          <el-button size="small" type="warning" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="del(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        v-if="pagination.total > 0"
        class="mt-4"
        layout="prev, pager, next, jumper, ->, total, sizes"
        :total="pagination.total"
        v-model:page-size="pagination.pageSize"
        v-model:current-page="pagination.pageNum"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
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
import { ElMessage } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import ElderForm from "@/components/elders/ElderForm.vue";
import ElderDetail from "@/components/elders/ElderDetail.vue";
import { getElderList, deleteElder } from '@/api/elder'
import type { ElderItem } from '@/types/elder'

const loading = ref(false)
const dialogVisible = ref(false)
const detailVisible = ref(false)
const currentId = ref(0)
const elderList = ref<ElderItem[]>([])
const detailData = ref<ElderItem>({} as ElderItem)

const query = reactive({ name: '' })
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

onMounted(() => getList())

async function getList() {
  loading.value = true
  try {
    const params = {
      name: query.name,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    console.log('请求参数：', params);

    const res = await getElderList(params)

    if (res?.data) {
      elderList.value = res.data.records ?? []
      pagination.total = res.data.total ?? 0
    } else {
      elderList.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('获取长辈列表失败：', error)
    ElMessage.error('获取列表失败，请稍后重试')
    elderList.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

//  翻页事件处理
function handleSizeChange() {
  pagination.pageNum = 1 // 切换每页条数时回到第一页
  getList()
}

function handlePageChange() {
  console.log('当前页码：', pagination.pageNum); // 调试用，可删除
  getList()
}

function openAdd() {
  currentId.value = 0
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

async function del(row: ElderItem) {
  await deleteElder(row.elderId)
  ElMessage.success('删除成功')
  pagination.pageNum = 1
  getList()
}

function resetQuery() {
  query.name = ''
  pagination.pageNum = 1
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