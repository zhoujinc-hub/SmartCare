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
import { getElderList, deleteElder,getElderDetail  } from '@/api/elder'
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

async function openDetail(row: ElderItem) {
  try {
    // 1. 发起详情接口请求
    const res = await getElderDetail(row.elderId)
    if (res?.data) {
      // 2. 给详情数据赋值
      detailData.value = res.data
      // 3. 打开弹窗
      detailVisible.value = true
    } else {
      ElMessage.error('获取详情失败')
    }
  } catch (error) {
    console.error('获取老人详情失败：', error)
    ElMessage.error('获取详情失败，请稍后重试')
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

async function openEdit(row: ElderItem) {
  try {
    const res = await getElderDetail(row.elderId)
    if (res?.data) {
      currentId.value = row.elderId
      dialogVisible.value = true
    }
  } catch (e) {
    ElMessage.error('获取编辑数据失败')
  }
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
.elder-management-container {
  position: relative;
  min-height: 100vh;
  padding: 24px;
  box-sizing: border-box;
  overflow: hidden;
  border-radius: 0;
  background:
      radial-gradient(circle at 12% 10%, rgba(255, 255, 255, 0.95), transparent 26%),
      radial-gradient(circle at 88% 18%, rgba(191, 219, 254, 0.5), transparent 30%),
      radial-gradient(circle at 48% 92%, rgba(204, 251, 241, 0.42), transparent 34%),
      linear-gradient(135deg, #eef4fb 0%, #e7edf6 48%, #f7f9fd 100%);
  color: #2f3b52;
}

.elder-management-container::before,
.elder-management-container::after {
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

.elder-management-container::before {
  width: 260px;
  height: 260px;
  top: 8%;
  left: 5%;
}

.elder-management-container::after {
  width: 340px;
  height: 340px;
  right: 6%;
  bottom: 8%;
}

.search-bar,
:deep(.el-table),
:deep(.el-pagination) {
  position: relative;
  z-index: 1;
}

/* 搜索区域软卡片 */
.search-bar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 24px;
  background: linear-gradient(145deg, #ffffff, #eef3f8);
  box-shadow:
      12px 12px 24px rgba(163, 177, 198, 0.28),
      -12px -12px 24px rgba(255, 255, 255, 0.9),
      inset 1px 1px 1px rgba(255, 255, 255, 0.7);
}

/* 输入框新拟物 */
:deep(.el-input__wrapper) {
  height: 42px;
  border-radius: 999px;
  background: #f3f7fb;
  box-shadow:
      inset 5px 5px 10px rgba(163, 177, 198, 0.22),
      inset -5px -5px 10px rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.7);
  transition: all 0.18s ease;
}

:deep(.el-input__wrapper.is-focus) {
  box-shadow:
      inset 3px 3px 7px rgba(163, 177, 198, 0.25),
      inset -3px -3px 7px rgba(255, 255, 255, 0.95),
      0 0 0 3px rgba(99, 102, 241, 0.08);
}

/* 胶囊按钮 */
:deep(.el-button) {
  height: 40px;
  padding: 0 18px;
  border: none;
  border-radius: 999px;
  font-weight: 600;
  transition: all 0.18s ease;
  box-shadow:
      6px 6px 14px rgba(163, 177, 198, 0.28),
      -6px -6px 14px rgba(255, 255, 255, 0.9);
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
  box-shadow:
      9px 9px 18px rgba(163, 177, 198, 0.32),
      -9px -9px 18px rgba(255, 255, 255, 0.95);
}

:deep(.el-button:active) {
  transform: translateY(1px);
  box-shadow:
      inset 4px 4px 8px rgba(0, 0, 0, 0.12),
      inset -4px -4px 8px rgba(255, 255, 255, 0.45);
}

/* 主操作色块 */
:deep(.el-button--primary) {
  background: linear-gradient(135deg, #7c8cff, #5b6ee1);
  color: #fff;
}

:deep(.el-button--success) {
  background: linear-gradient(135deg, #72dfb0, #3bbb83);
  color: #fff;
}

:deep(.el-button--warning) {
  background: linear-gradient(135deg, #ffd27a, #f5a623);
  color: #fff;
}

:deep(.el-button--danger) {
  background: linear-gradient(135deg, #ff8a8a, #ef5f5f);
  color: #fff;
}

/* 表格整体软卡片 */
:deep(.el-table) {
  overflow: hidden;
  border-radius: 24px;
  background: linear-gradient(145deg, #ffffff, #eef3f8);
  box-shadow:
      14px 14px 30px rgba(163, 177, 198, 0.26),
      -14px -14px 30px rgba(255, 255, 255, 0.9);
}

:deep(.el-table__inner-wrapper::before) {
  display: none;
}

:deep(.el-table th.el-table__cell) {
  background: rgba(246, 249, 253, 0.9) !important;
  color: #344054 !important;
  font-weight: 700;
}

:deep(.el-table td.el-table__cell) {
  background: rgba(255, 255, 255, 0.55);
  color: #475467;
}

:deep(.el-table--striped .el-table__body tr.el-table__row--striped td.el-table__cell) {
  background: rgba(244, 248, 252, 0.7);
}

:deep(.el-table__body tr) {
  transition: all 0.18s ease;
}

:deep(.el-table__body tr:hover > td.el-table__cell) {
  background: #f7faff !important;
}

/* 小按钮更精致 */
:deep(.el-table .el-button) {
  height: 32px;
  padding: 0 12px;
  font-size: 13px;
}

/* 分页软浮雕 */
/* ===== 分页软浮雕 ===== */
:deep(.el-pagination) {
  margin-top: 20px;
  padding: 14px 18px;
  border-radius: 20px;
  background: linear-gradient(145deg, #ffffff, #eef3f8);
  box-shadow:
      8px 8px 18px rgba(163, 177, 198, 0.24),
      -8px -8px 18px rgba(255, 255, 255, 0.9);
}

/* 页码按钮 */
:deep(.el-pager li),
:deep(.el-pagination button) {
  border-radius: 12px;
  background: #f3f7fb;
  box-shadow:
      4px 4px 9px rgba(163, 177, 198, 0.2),
      -4px -4px 9px rgba(255, 255, 255, 0.9);
}

/* 当前页 */
:deep(.el-pager li.is-active) {
  color: #fff;
  background: linear-gradient(135deg, #7c8cff, #5b6ee1);
}

/* ===== ⭐关键：分页 sizes 下拉框 ===== */
:deep(.el-pagination .el-select__wrapper) {
  height: 32px;
  border-radius: 14px;
  background: #f3f7fb;
  border: none;
  box-shadow:
      inset 4px 4px 8px rgba(163, 177, 198, 0.22),
      inset -4px -4px 8px rgba(255, 255, 255, 0.9);
}

/* hover */
:deep(.el-pagination .el-select__wrapper:hover) {
  box-shadow:
      inset 3px 3px 6px rgba(163, 177, 198, 0.22),
      inset -3px -3px 6px rgba(255, 255, 255, 0.95),
      0 6px 12px rgba(163, 177, 198, 0.15);
}

/* focus */
:deep(.el-pagination .el-select__wrapper.is-focused) {
  box-shadow:
      inset 3px 3px 6px rgba(163, 177, 198, 0.25),
      inset -3px -3px 6px rgba(255, 255, 255, 0.95),
      0 0 0 2px rgba(124, 140, 255, 0.18);
}

/* Go to 输入框 */
:deep(.el-pagination .el-input__wrapper) {
  height: 32px;
  border-radius: 14px;
  background: #f3f7fb;
  box-shadow:
      inset 4px 4px 8px rgba(163, 177, 198, 0.22),
      inset -4px -4px 8px rgba(255, 255, 255, 0.9);
}

/* 文本省略 */
.text-ellipsis {
  display: inline-block;
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* 空状态 */
:deep(.el-table__empty-text) {
  color: #98a2b3 !important;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .elder-management-container {
    padding: 14px;
  }

  .search-bar {
    padding: 16px;
    border-radius: 20px;
  }

  :deep(.el-input) {
    width: 100% !important;
  }
}
</style>