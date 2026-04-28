<template>
  <div class="family-container">
    <div class="page-header">
      <h2>家属用户中心</h2>
      <p>管理老人信息 & 查看摔倒告警</p>
    </div>

    <!-- 添加老人 -->
    <el-card class="card-box" shadow="hover">
      <div class="card-title">添加老人信息</div>
      <el-form
          ref="formRef"
          :model="elderForm"
          :rules="elderRules"
          label-width="80px"
          style="max-width: 600px"
      >
        <el-form-item label="姓名" prop="name">
          <el-input v-model="elderForm.name" placeholder="请输入姓名" style="width: 300px" />
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="elderForm.gender">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="0">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="elderForm.age" :min="50" :max="120" style="width: 300px" />
        </el-form-item>

        <el-form-item label="家庭住址">
          <el-input v-model="elderForm.address" type="textarea" :rows="3" style="width: 300px" />
        </el-form-item>

        <el-form-item label="健康备注">
          <el-input v-model="elderForm.healthNotes" type="textarea" :rows="2" style="width: 300px" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleAddElder">提交添加</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 我的老人列表 -->
    <el-card class="card-box" shadow="hover">
      <div class="card-title">我的老人</div>
      <el-table :data="elderList" border v-loading="loading">
        <el-table-column label="姓名" prop="name" />
        <el-table-column label="性别">
          <template #default="scope">{{ scope.row.gender === 1 ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column label="年龄" prop="age" />
        <el-table-column label="住址" prop="address" />
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" text @click="openFallDialog(scope.row)">
              查看摔倒记录
            </el-button>
            <el-button type="danger" text @click="handleDelete(scope.row.elderId)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

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
.family-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 22px;
  margin: 0 0 6px 0;
}

.page-header p {
  color: #666;
  margin: 0;
}

.card-box {
  margin-bottom: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}
</style>