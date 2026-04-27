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
          <el-input v-model="elderForm.name" placeholder="请输入老人姓名" style="width: 300px" />
        </el-form-item>

        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="elderForm.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="0">女</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="年龄" prop="age">
          <el-input-number v-model="elderForm.age" :min="50" :max="120" style="width: 300px" />
        </el-form-item>

        <el-form-item label="家庭住址">
          <el-input v-model="elderForm.address" type="textarea" rows="3" style="width: 300px" />
        </el-form-item>

        <el-form-item label="健康备注">
          <el-input v-model="elderForm.healthNotes" type="textarea" rows="2" style="width: 300px" />
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
        <el-table-column label="操作">
          <template #default="scope">
            <el-button type="primary" text @click="toQueryEvents(scope.row.elderId)">
              查看摔倒记录
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 摔倒事件弹窗 -->
    <el-dialog v-model="eventVisible" title="老人摔倒事件记录" width="70%" append-to-body>
      <el-table :data="eventList" border v-loading="eventLoading">
        <el-table-column label="告警时间" prop="detectTime" width="200" />
        <el-table-column label="老人姓名" prop="elderName" />
        <el-table-column label="状态" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.status === 1 ? 'warning' : scope.row.status === 2 ? 'success' : 'info'">
              {{ scope.row.status === 1 ? '待处理' : scope.row.status === 2 ? '已处理' : '误报' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import type { Elder } from '@/types/elderType'
import { getMyElderList, addElder, getElderFallEvents } from '@/api/elderApi'

const loading = ref(false)
const eventLoading = ref(false)
const elderList = ref<any[]>([])
const eventVisible = ref(false)
const eventList = ref<any[]>([])

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
  loading.value = true
  try {
    const res = await getMyElderList()
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
    await addElder(elderForm)
    ElMessage.success('添加成功')
    formRef.value?.resetFields()
    loadMyElders()
  } catch (err) {
    ElMessage.error('添加失败')
  }
}

// 查看摔倒事件
const toQueryEvents = async (elderId: number) => {
  eventVisible.value = true
  eventLoading.value = true
  try {
    const res = await getElderFallEvents(elderId, { pageNum: 1, pageSize: 100 })
    eventList.value = res.data?.records || []
  } finally {
    eventLoading.value = false
  }
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