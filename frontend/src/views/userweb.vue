<template>
  <div class="family-container">

    <!-- 标题 -->
    <div class="page-header">
      <div>
        <h2>家属用户中心</h2>
        <p>管理老人信息 & 查看摔倒告警</p>
      </div>
    </div>

    <!-- ===== 上半区：左右布局 ===== -->
    <div class="top-grid">

      <!-- 左：添加老人 -->
      <el-card class="card-box form-card">
        <div class="card-title">添加老人信息</div>

        <el-form
            ref="formRef"
            :model="elderForm"
            :rules="elderRules"
            label-width="80px"
            class="form-grid"
        >
          <el-form-item label="姓名" prop="name">
            <el-input v-model="elderForm.name" placeholder="请输入姓名" />
          </el-form-item>

          <el-form-item label="性别">
            <el-radio-group v-model="elderForm.gender">
              <el-radio :value="1">男</el-radio>
              <el-radio :value="0">女</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="年龄">
            <el-input-number v-model="elderForm.age" :min="50" :max="120" />
          </el-form-item>

          <el-form-item label="家庭住址" class="full">
            <el-input v-model="elderForm.address" type="textarea" :rows="2" />
          </el-form-item>

          <el-form-item label="健康备注" class="full">
            <el-input v-model="elderForm.healthNotes" type="textarea" :rows="2" />
          </el-form-item>

          <el-form-item class="full">
            <el-button type="primary" @click="handleAddElder">
              提交添加
            </el-button>
          </el-form-item>

        </el-form>
      </el-card>

      <!-- 右：信息卡片（新增） -->
      <el-card class="card-box info-card">
        <div class="card-title">使用提示</div>

        <ul class="info-list">
          <li>✔ 添加老人后即可查看告警记录</li>
          <li>✔ 系统自动检测摔倒事件</li>
          <li>✔ 支持视频与截图回放</li>
          <li>✔ 建议完善健康备注信息</li>
        </ul>
      </el-card>

    </div>

    <!-- ===== 下半区：老人列表 ===== -->
    <el-card class="card-box">
      <div class="card-title">我的老人</div>

      <el-table :data="elderList" v-loading="loading">
        <el-table-column label="姓名" prop="name" />
        <el-table-column label="性别">
          <template #default="scope">
            {{ scope.row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column label="年龄" prop="age" />
        <el-table-column label="住址" prop="address" />

        <el-table-column label="操作" width="220">
          <template #default="scope">
            <div class="action-group">
              <el-button type="primary" @click="openFallDialog(scope.row)">
                查看记录
              </el-button>

              <el-button type="danger" @click="handleDelete(scope.row.elderId)">
                删除
              </el-button>
              <!-- 摔倒事件弹窗（含截图和视频查看） -->
              <el-dialog v-model="eventVisible" title="老人摔倒事件记录" width="80%" append-to-body>
                <el-table :data="eventList" border v-loading="eventLoading">
                  <el-table-column label="告警时间" prop="detectTime" width="200" />
                  <el-table-column label="老人姓名">
                    <template #default="scope">
                      {{ currentElder?.name || '-' }}
                    </template>
                  </el-table-column>
                  <el-table-column label="位置">
                    <template #default="scope">
                      {{ scope.row.locationDesc || '无数据' }}
                    </template>
                  </el-table-column>
                  <el-table-column label="状态" width="120">
                    <template #default="scope">
                      <el-tag :type="scope.row.status === 1 ? 'warning' : scope.row.status === 2 ? 'success' : 'info'">
                        {{ scope.row.status === 1 ? '待处理' : scope.row.status === 2 ? '已处理' : '误报' }}
                      </el-tag>
                    </template>
                  </el-table-column>
                  <el-table-column label="截图" width="100">
                    <template #default="scope">
                      <el-button
                          v-if="scope.row.screenshotPath"
                          type="primary"
                          size="small"
                          link
                          @click="viewScreenshot(scope.row.screenshotPath)"
                      >
                        查看截图
                      </el-button>
                      <span v-else style="color: #999">无截图</span>
                    </template>
                  </el-table-column>
                  <el-table-column label="视频" width="100">
                    <template #default="scope">
                      <el-button
                          v-if="scope.row.videoPath"
                          type="primary"
                          size="small"
                          link
                          @click="playVideo(scope.row.videoPath)"
                      >
                        查看视频
                      </el-button>
                      <span v-else style="color: #999">无视频</span>
                    </template>
                  </el-table-column>
                </el-table>
              </el-dialog>

              <!-- 视频播放弹窗 -->
              <el-dialog v-model="videoVisible" title="摔倒事件视频" width="60%" append-to-body>
                <video controls :src="currentVideoUrl" style="width: 100%" />
              </el-dialog>

              <!-- 图片预览弹窗 -->
              <el-dialog v-model="imageVisible" title="摔倒事件截图" width="50%" append-to-body>
                <img :src="currentImageUrl" style="width: 100%; border-radius: 4px" />
              </el-dialog>
            </div>
          </template>
        </el-table-column>
      </el-table>

    </el-card>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance } from 'element-plus'
import type { Elder } from '@/types/elderType'
import { getMyElderList, addElder, getElderFallEvents, deleteElder } from '@/api/elderApi'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const currentUserId = computed(() => userStore.userInfo.userId)

const loading = ref(false)
const eventLoading = ref(false)
const elderList = ref<any[]>([])
const eventVisible = ref(false)
const eventList = ref<any[]>([])
const videoVisible = ref(false)
const imageVisible = ref(false)
const currentVideoUrl = ref('')
const currentImageUrl = ref('')

// 保存当前打开弹窗的老人信息
const currentElder = ref<Elder | null>(null)

const formRef = ref<FormInstance>()
const elderForm = reactive<Elder>({
  name: '',
  gender: 1,
  age: 60,
  address: '',
  healthNotes: ''
})

const elderRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
}

// 获取我的老人
const loadMyElders = async () => {
  if (!currentUserId.value) {
    ElMessage.error('请先登录')
    return
  }

  loading.value = true
  try {
    const res = await getMyElderList(currentUserId.value)
    elderList.value = res.data || []
  } catch (err) {
    console.error(err)
  } finally {
    loading.value = false
  }
}

// 添加老人
const handleAddElder = async () => {
  await formRef.value?.validate()
  try {
    await addElder(currentUserId.value, elderForm)
    ElMessage.success('添加成功')
    formRef.value?.resetFields()
    loadMyElders()
  } catch (err) {
    ElMessage.error('添加失败')
    console.error(err)
  }
}

// 删除老人（修正：传入 userId + elderId）
const handleDelete = async (elderId: number) => {
  await ElMessageBox.confirm(
      '确定要删除该老人信息吗？删除后无法恢复！',
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
  )
  try {
    await deleteElder(currentUserId.value, elderId)
    ElMessage.success('删除成功')
    loadMyElders()
  } catch (err) {
    ElMessage.error('删除失败')
    console.error(err)
  }
}

// 打开摔倒记录弹窗：把当前点击的老人信息传进来
const openFallDialog = async (elder: Elder) => {
  // 保存当前老人信息
  currentElder.value = elder
  eventVisible.value = true
  eventLoading.value = true
  try {
    const res = await getElderFallEvents(currentUserId.value, elder.elderId!, { pageNum: 1, pageSize: 100 })
    eventList.value = res.data?.records || []
  } catch (err) {
    console.error(err)
  } finally {
    eventLoading.value = false
  }
}

// 查看截图
const viewScreenshot = (url: string) => {
  currentImageUrl.value = url
  imageVisible.value = true
}

// 播放视频
const playVideo = (url: string) => {
  currentVideoUrl.value = url
  videoVisible.value = true
}

onMounted(() => {
  loadMyElders()
})
</script>

<style scoped>

/* ===== 页面背景 ===== */
.family-container {
  padding: 24px;
  min-height: 100vh;

  background:
      radial-gradient(circle at 10% 10%, #ffffff, transparent 30%),
      radial-gradient(circle at 90% 20%, #eaf2ff, transparent 35%),
      linear-gradient(135deg, #eef3f8, #f8fbff);
}

/* ===== 标题区 ===== */
.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 22px;
  font-weight: 700;
  color: #344054;
}

.page-header p {
  color: #98a2b3;
}

/* ===== 卡片（浮层）===== */
.card-box {
  margin-bottom: 20px;
  border-radius: 22px;

  background: linear-gradient(145deg, #f8fbff, #e7edf6);

  box-shadow:
      12px 12px 26px rgba(163, 177, 198, 0.28),
      -12px -12px 26px rgba(255, 255, 255, 0.9);

  transition: all 0.18s ease;
}

.card-box:hover {
  transform: translateY(-3px);
}

/* ===== 卡片标题 ===== */
.card-title {
  font-size: 15px;
  font-weight: 700;
  padding: 16px 18px;
  border-bottom: 1px solid rgba(203, 213, 225, 0.5);
  color: #344054;
}

/* ===== 表单区域（内凹）===== */
:deep(.el-form) {
  margin-top: 12px;
  padding: 16px;
  border-radius: 18px;

  background: linear-gradient(145deg, #edf3fa, #f8fbff);

  box-shadow:
      inset 6px 6px 14px rgba(163, 177, 198, 0.22),
      inset -6px -6px 14px rgba(255, 255, 255, 0.9);
}

/* ===== 输入框 ===== */
:deep(.el-input__wrapper),
:deep(.el-textarea__inner),
:deep(.el-input-number),
:deep(.el-select__wrapper) {
  border-radius: 999px;
  background: #f3f7fb;

  box-shadow:
      inset 4px 4px 8px rgba(163, 177, 198, 0.2),
      inset -4px -4px 8px rgba(255, 255, 255, 0.9);

  border: none;
}

/* textarea 特殊处理 */
:deep(.el-textarea__inner) {
  border-radius: 12px;
}

/* ===== 单选按钮 ===== */
:deep(.el-radio) {
  margin-right: 16px;
}

/* ===== 胶囊按钮 ===== */
:deep(.el-button) {
  border-radius: 999px;
  border: none;
  padding: 8px 18px;
  font-weight: 600;

  box-shadow:
      6px 6px 14px rgba(163, 177, 198, 0.28),
      -6px -6px 14px rgba(255, 255, 255, 0.9);

  transition: all 0.18s ease;
}

:deep(.el-button:hover) {
  transform: translateY(-2px);
}

:deep(.el-button:active) {
  transform: translateY(1px);

  box-shadow:
      inset 3px 3px 6px rgba(0,0,0,0.12),
      inset -3px -3px 6px rgba(255,255,255,0.4);
}

/* 主按钮 */
:deep(.el-button--primary) {
  background: linear-gradient(135deg, #7c8cff, #5b6ee1);
  color: #fff;
}

/* 危险按钮 */
:deep(.el-button--danger) {
  background: linear-gradient(135deg, #ff8a8a, #ef5f5f);
  color: #fff;
}

/* ===== 表格 ===== */
/* ===== 表格整体透明 ===== */
:deep(.el-table) {
  border-radius: 18px;
  overflow: hidden;

  background: transparent !important;
  --el-table-bg-color: transparent;
  --el-table-tr-bg-color: transparent;
}

/* 表头 */
:deep(.el-table th.el-table__cell) {
  background: rgba(237, 243, 250, 0.8) !important;
  color: #64748b;
  font-weight: 700;
}

/* 表体单元格 */
:deep(.el-table td.el-table__cell) {
  background: transparent !important;
}

/* 行背景（关键） */
:deep(.el-table__body tr) {
  background: transparent !important;
}

/* hover 效果（柔和一点，不要纯白） */
:deep(.el-table__body tr:hover > td) {
  background: rgba(255,255,255,0.25) !important;
}

/* 去掉 stripe 斑马纹 */
:deep(.el-table--striped .el-table__body tr.el-table__row--striped > td) {
  background: transparent !important;
}

/* ===== Tag ===== */
:deep(.el-tag) {
  border-radius: 999px;
  border: none;

  box-shadow:
      3px 3px 8px rgba(163, 177, 198, 0.25),
      -3px -3px 8px rgba(255, 255, 255, 0.9);
}

/* ===== 弹窗（重点）===== */
:deep(.el-dialog) {
  border-radius: 22px;

  background: linear-gradient(145deg, #f8fbff, #e7edf6);

  box-shadow:
      20px 20px 40px rgba(163, 177, 198, 0.3),
      -20px -20px 40px rgba(255, 255, 255, 0.95);
}

/* 弹窗内容内凹 */
:deep(.el-dialog__body) {
  border-radius: 18px;
  background: linear-gradient(145deg, #edf3fa, #f8fbff);

  box-shadow:
      inset 6px 6px 14px rgba(163, 177, 198, 0.22),
      inset -6px -6px 14px rgba(255, 255, 255, 0.9);
}

/* ===== 视频 / 图片 ===== */
video, img {
  border-radius: 14px;
  box-shadow:
      6px 6px 14px rgba(163, 177, 198, 0.28),
      -6px -6px 14px rgba(255, 255, 255, 0.9);
}
/* ===== 上半区布局 ===== */
.top-grid {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

/* ===== 表单两列布局 ===== */
.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

/* 占满一整行 */
.form-grid .full {
  grid-column: span 2;
}

/* ===== 信息卡片 ===== */
.info-card {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.info-list {
  padding: 16px;
  line-height: 2;
  color: #667085;
  font-size: 14px;
}

/* ===== 操作按钮 ===== */
.action-group {
  display: flex;
  gap: 8px;
}

/* ===== 响应式 ===== */
@media (max-width: 900px) {
  .top-grid {
    grid-template-columns: 1fr;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .form-grid .full {
    grid-column: span 1;
  }
}
</style>