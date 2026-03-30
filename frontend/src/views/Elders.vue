<template>
  <div class="elders-container">
    <!-- 页面标题+筛选栏 -->
    <div class="page-header">
      <h2>老人信息管理</h2>
      <el-row class="search-bar" align="middle">
        <el-col :span="6">
          <el-input v-model="filterParams.name" placeholder="输入老人姓名搜索" clearable />
        </el-col>
        <el-col :span="5">
          <el-select v-model="filterParams.communityId" placeholder="选择所属社区" clearable>
            <el-option label="幸福社区" :value="1" />
            <el-option label="阳光社区" :value="2" />
            <el-option label="和谐社区" :value="3" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="filterParams.healthStatus" placeholder="选择健康状态" clearable>
            <el-option label="健康" :value="1" />
            <el-option label="需关注" :value="2" />
            <el-option label="需监护" :value="3" />
          </el-select>
        </el-col>
        <el-col :span="7" class="search-buttons">
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button type="success" :icon="Plus" @click="dialogVisible = true">新增老人</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 老人信息表格 -->
    <el-table
      :data="elderList"
      border
      stripe
      v-loading="loading"
      element-loading-text="加载中..."
      style="width: 100%; margin-top: 16px"
      size="default"
      :header-cell-style="{ backgroundColor: '#f8f9fa', color: '#1f2937' }"
      :cell-style="{ backgroundColor: '#ffffff', color: '#333333' }"
    >
      <el-table-column type="index" label="序号" align="center" width="80" />
      <el-table-column prop="elderId" label="老人ID" align="center" width="100" />
      <el-table-column prop="name" label="姓名" align="center" width="100" />
      <el-table-column label="性别" align="center" width="80">
        <template #default="scope">
          <el-tag type="info" effect="light">{{ scope.row.gender === 1 ? '男' : '女' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="age" label="年龄" align="center" width="80" />
      <el-table-column prop="communityName" label="所属社区" align="center" width="140" />
      <el-table-column prop="householdAddress" label="家庭地址" align="center" />
      <el-table-column label="健康状态" align="center" width="120">
        <template #default="scope">
          <el-tag v-if="scope.row.healthStatus === 1" type="success" effect="light">健康</el-tag>
          <el-tag v-else-if="scope.row.healthStatus === 2" type="warning" effect="light">需关注</el-tag>
          <el-tag v-else type="danger" effect="light">需监护</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="emergencyContact" label="紧急联系人" align="center" width="120" />
      <el-table-column prop="emergencyPhone" label="紧急电话" align="center" width="140" />
      <el-table-column label="负责管理员" align="center" width="160">
        <template #default="scope">
          <span>{{ scope.row.managerName || '暂无' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" fixed="right">
        <template #default="scope">
          <div class="action-buttons">
            <el-button size="small" type="primary" :icon="View" @click="viewDetail(scope.row)">查看</el-button>
            <el-button size="small" type="warning" :icon="Edit" @click="editElder(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" :icon="Delete" @click="deleteElder(scope.row)">删除</el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页器 -->
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pagination.pageNum"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pagination.pageSize"
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      style="margin-top: 20px; text-align: right"
    >
    </el-pagination>

    <!-- 新增/编辑老人弹窗 -->
    <el-dialog
      :title="isEdit ? '编辑老人信息' : '新增老人信息'"
      v-model="dialogVisible"
      width="700px"
      destroy-on-close
      center
      custom-class="elder-dialog"
    >
      <el-form :model="form" :rules="formRules" ref="formRef" label-width="120px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入老人姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select v-model="form.gender" placeholder="请选择性别">
            <el-option label="男" :value="1" />
            <el-option label="女" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="年龄" prop="age">
          <el-input v-model="form.age" type="number" placeholder="请输入老人年龄" />
        </el-form-item>
        <el-form-item label="所属社区" prop="communityId">
          <el-select v-model="form.communityId" placeholder="请选择所属社区">
            <el-option label="幸福社区" :value="1" />
            <el-option label="阳光社区" :value="2" />
            <el-option label="和谐社区" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="家庭地址" prop="householdAddress">
          <el-input v-model="form.householdAddress" placeholder="请输入家庭详细地址" />
        </el-form-item>
        <el-form-item label="健康状态" prop="healthStatus">
          <el-select v-model="form.healthStatus" placeholder="请选择健康状态">
            <el-option label="健康" :value="1" />
            <el-option label="需关注" :value="2" />
            <el-option label="需监护" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急联系人" prop="emergencyContact">
          <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人姓名" />
        </el-form-item>
        <el-form-item label="紧急电话" prop="emergencyPhone">
          <el-input v-model="form.emergencyPhone" placeholder="请输入紧急联系人电话" />
        </el-form-item>
        <el-form-item label="负责管理员" prop="managerId">
          <el-select v-model="form.managerId" placeholder="选择社区管理员（关联users表）">
            <el-option label="王芳（幸福社区）" :value="101" />
            <el-option label="李强（阳光社区）" :value="102" />
            <el-option label="赵敏（和谐社区）" :value="103" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">提交</el-button>
      </template>
    </el-dialog>

    <!-- 查看老人详情弹窗 -->
    <el-dialog
      title="老人详情"
      v-model="detailVisible"
      width="700px"
      destroy-on-close
      center
      custom-class="elder-dialog"
    >
      <el-descriptions :column="2" border title="基础信息" style="margin-bottom: 16px">
        <el-descriptions-item label="老人ID">{{ detailData.elderId }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ detailData.gender === 1 ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="年龄">{{ detailData.age }} 岁</el-descriptions-item>
        <el-descriptions-item label="所属社区">{{ detailData.communityName }}</el-descriptions-item>
        <el-descriptions-item label="家庭地址">{{ detailData.householdAddress }}</el-descriptions-item>
        <el-descriptions-item label="健康状态">
          <el-tag v-if="detailData.healthStatus === 1" type="success" effect="light">健康</el-tag>
          <el-tag v-else-if="detailData.healthStatus === 2" type="warning" effect="light">需关注</el-tag>
          <el-tag v-else type="danger" effect="light">需监护</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ detailData.createdAt }}</el-descriptions-item>
      </el-descriptions>
      <el-descriptions :column="2" border title="紧急联系人信息">
        <el-descriptions-item label="紧急联系人">{{ detailData.emergencyContact }}</el-descriptions-item>
        <el-descriptions-item label="紧急电话">{{ detailData.emergencyPhone }}</el-descriptions-item>
        <el-descriptions-item label="负责管理员">{{ detailData.managerName }}</el-descriptions-item>
        <el-descriptions-item label="管理员电话">{{ detailData.managerPhone }}</el-descriptions-item>
        <el-descriptions-item label="最后更新时间" :span="2">{{ detailData.updatedAt }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, shallowRef } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Search, Plus, View, Edit, Delete } from '@element-plus/icons-vue'

// 加载状态
const loading = ref(false)
// 新增/编辑弹窗显隐
const dialogVisible = ref(false)
// 详情弹窗显隐
const detailVisible = ref(false)
// 老人列表数据
const elderList = ref<ElderItem[]>([])
// 筛选参数
const filterParams = reactive({
  name: '', // 老人姓名
  communityId: '' as number | '', // 所属社区ID
  healthStatus: '' as number | '' // 健康状态
})
// 分页参数
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})
// 表单引用
const formRef = shallowRef<FormInstance | null>(null)
// 新增/编辑表单数据
const form = reactive<Omit<ElderItem, 'elderId' | 'createdAt' | 'updatedAt' | 'communityName' | 'managerName' | 'managerPhone'>>({
  name: '',
  gender: 0,
  age: 0,
  communityId: 0,
  householdAddress: '',
  healthStatus: 0,
  emergencyContact: '',
  emergencyPhone: '',
  managerId: 0
})
// 表单校验规则
const formRules = reactive<FormRules<typeof form>>({
  name: [{ required: true, message: '请输入老人姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  age: [{ required: true, message: '请输入老人年龄', trigger: 'blur' }, { type: 'number', min: 0, max: 120, message: '年龄范围0-120岁', trigger: 'blur' }],
  communityId: [{ required: true, message: '请选择所属社区', trigger: 'change' }],
  householdAddress: [{ required: true, message: '请输入家庭地址', trigger: 'blur' }],
  healthStatus: [{ required: true, message: '请选择健康状态', trigger: 'change' }],
  emergencyContact: [{ required: true, message: '请输入紧急联系人', trigger: 'blur' }],
  emergencyPhone: [{ required: true, message: '请输入紧急联系电话', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  managerId: [{ required: true, message: '请选择负责管理员', trigger: 'change' }]
})
// 是否为编辑模式
const isEdit = ref(false)
// 详情弹窗数据
const detailData = reactive<ElderItem>({} as ElderItem)

// 定义接口：关联users表（管理员信息）+ 老人核心信息
interface ElderItem {
  elderId: number; // 老人唯一ID
  name: string; // 姓名
  gender: number; // 性别(0女1男)
  age: number; // 年龄
  communityId: number; // 所属社区ID(关联communities表)
  communityName: string; // 社区名称
  householdAddress: string; // 家庭地址
  healthStatus: number; // 健康状态(1健康2需关注3需监护)
  emergencyContact: string; // 紧急联系人
  emergencyPhone: string; // 紧急联系电话
  managerId: number; // 负责管理员ID(关联users表user_id)
  managerName: string; // 管理员姓名(关联users表real_name)
  managerPhone: string; // 管理员电话(关联users表phone)
  createdAt: string; // 创建时间
  updatedAt: string; // 更新时间
}

// 模拟管理员数据（关联users表）
const managerList = [
  { managerId: 101, managerName: '王芳', managerPhone: '13800138001', communityId: 1 },
  { managerId: 102, managerName: '李强', managerPhone: '13800138002', communityId: 2 },
  { managerId: 103, managerName: '赵敏', managerPhone: '13800138003', communityId: 3 }
]

// 模拟老人数据（关联users表管理员）
const fullElderData: ElderItem[] = [
  {
    elderId: 1001,
    name: '张大爷',
    gender: 1,
    age: 78,
    communityId: 1,
    communityName: '幸福社区',
    householdAddress: '幸福小区3栋2单元101室',
    healthStatus: 2,
    emergencyContact: '张小明',
    emergencyPhone: '13900139001',
    managerId: 101,
    managerName: '王芳',
    managerPhone: '13800138001',
    createdAt: '2026-01-15 10:30:00',
    updatedAt: '2026-03-10 14:20:00'
  },
  {
    elderId: 1002,
    name: '李奶奶',
    gender: 0,
    age: 82,
    communityId: 1,
    communityName: '幸福社区',
    householdAddress: '幸福小区5栋1单元302室',
    healthStatus: 3,
    emergencyContact: '李小华',
    emergencyPhone: '13900139002',
    managerId: 101,
    managerName: '王芳',
    managerPhone: '13800138001',
    createdAt: '2026-01-20 09:15:00',
    updatedAt: '2026-03-05 09:45:00'
  },
  {
    elderId: 1003,
    name: '王爷爷',
    gender: 1,
    age: 75,
    communityId: 2,
    communityName: '阳光社区',
    householdAddress: '阳光社区5栋1单元502室',
    healthStatus: 1,
    emergencyContact: '王丽',
    emergencyPhone: '13900139003',
    managerId: 102,
    managerName: '李强',
    managerPhone: '13800138002',
    createdAt: '2026-02-05 11:20:00',
    updatedAt: '2026-03-18 11:30:00'
  },
  {
    elderId: 1004,
    name: '赵奶奶',
    gender: 0,
    age: 72,
    communityId: 3,
    communityName: '和谐社区',
    householdAddress: '和谐社区2栋3单元201室',
    healthStatus: 2,
    emergencyContact: '赵强',
    emergencyPhone: '13900139004',
    managerId: 103,
    managerName: '赵敏',
    managerPhone: '13800138003',
    createdAt: '2026-02-10 14:00:00',
    updatedAt: '2026-03-12 15:10:00'
  },
  {
    elderId: 1005,
    name: '孙大爷',
    gender: 1,
    age: 85,
    communityId: 2,
    communityName: '阳光社区',
    householdAddress: '阳光社区8栋3单元102室',
    healthStatus: 3,
    emergencyContact: '孙悦',
    emergencyPhone: '13900139005',
    managerId: 102,
    managerName: '李强',
    managerPhone: '13800138002',
    createdAt: '2026-02-15 08:30:00',
    updatedAt: '2026-03-15 10:00:00'
  },
  {
    elderId: 1006,
    name: '周大爷',
    gender: 1,
    age: 70,
    communityId: 1,
    communityName: '幸福社区',
    householdAddress: '幸福小区2栋1单元402室',
    healthStatus: 1,
    emergencyContact: '周明',
    emergencyPhone: '13900139006',
    managerId: 101,
    managerName: '王芳',
    managerPhone: '13800138001',
    createdAt: '2026-01-20 10:30:00',
    updatedAt: '2026-03-10 14:20:00'
  },
  {
    elderId: 1007,
    name: '吴奶奶',
    gender: 0,
    age: 78,
    communityId: 2,
    communityName: '阳光社区',
    householdAddress: '阳光社区3栋2单元201室',
    healthStatus: 2,
    emergencyContact: '吴芳',
    emergencyPhone: '13900139007',
    managerId: 102,
    managerName: '李强',
    managerPhone: '13800138002',
    createdAt: '2026-02-05 11:20:00',
    updatedAt: '2026-03-18 11:30:00'
  },
  {
    elderId: 1008,
    name: '郑爷爷',
    gender: 1,
    age: 81,
    communityId: 3,
    communityName: '和谐社区',
    householdAddress: '和谐社区1栋3单元501室',
    healthStatus: 3,
    emergencyContact: '郑强',
    emergencyPhone: '13900139008',
    managerId: 103,
    managerName: '赵敏',
    managerPhone: '13800138003',
    createdAt: '2026-02-10 14:00:00',
    updatedAt: '2026-03-12 15:10:00'
  },
  {
    elderId: 1009,
    name: '冯大爷',
    gender: 1,
    age: 68,
    communityId: 1,
    communityName: '幸福社区',
    householdAddress: '幸福小区4栋1单元101室',
    healthStatus: 1,
    emergencyContact: '冯明',
    emergencyPhone: '13900139009',
    managerId: 101,
    managerName: '王芳',
    managerPhone: '13800138001',
    createdAt: '2026-01-15 10:30:00',
    updatedAt: '2026-03-10 14:20:00'
  },
  {
    elderId: 1010,
    name: '陈奶奶',
    gender: 0,
    age: 75,
    communityId: 2,
    communityName: '阳光社区',
    householdAddress: '阳光社区6栋2单元302室',
    healthStatus: 2,
    emergencyContact: '陈芳',
    emergencyPhone: '13900139010',
    managerId: 102,
    managerName: '李强',
    managerPhone: '13800138002',
    createdAt: '2026-02-05 11:20:00',
    updatedAt: '2026-03-18 11:30:00'
  },
  {
    elderId: 1011,
    name: '褚爷爷',
    gender: 1,
    age: 83,
    communityId: 3,
    communityName: '和谐社区',
    householdAddress: '和谐社区5栋1单元202室',
    healthStatus: 3,
    emergencyContact: '褚强',
    emergencyPhone: '13900139011',
    managerId: 103,
    managerName: '赵敏',
    managerPhone: '13800138003',
    createdAt: '2026-02-10 14:00:00',
    updatedAt: '2026-03-12 15:10:00'
  },
  {
    elderId: 1012,
    name: '卫大爷',
    gender: 1,
    age: 72,
    communityId: 1,
    communityName: '幸福社区',
    householdAddress: '幸福小区1栋2单元601室',
    healthStatus: 1,
    emergencyContact: '卫明',
    emergencyPhone: '13900139012',
    managerId: 101,
    managerName: '王芳',
    managerPhone: '13800138001',
    createdAt: '2026-01-15 10:30:00',
    updatedAt: '2026-03-10 14:20:00'
  }
]

// 页面挂载时加载数据
onMounted(() => {
  getElderList()
})

// 获取老人列表（支持筛选+分页）
const getElderList = () => {
  loading.value = true
  setTimeout(() => {
    // 1. 多条件筛选
    let filteredList = [...fullElderData]

    // 按姓名筛选
    if (filterParams.name) {
      filteredList = filteredList.filter(item => item.name.includes(filterParams.name))
    }

    // 按社区筛选
    if (filterParams.communityId) {
      filteredList = filteredList.filter(item => item.communityId === filterParams.communityId)
    }

    // 按健康状态筛选
    if (filterParams.healthStatus) {
      filteredList = filteredList.filter(item => item.healthStatus === filterParams.healthStatus)
    }

    // 2. 分页截取
    pagination.total = filteredList.length
    const start = (pagination.pageNum - 1) * pagination.pageSize
    const end = start + pagination.pageSize
    elderList.value = filteredList.slice(start, end)

    loading.value = false
  }, 500)
}

// 查询按钮
const handleQuery = () => {
  pagination.pageNum = 1
  getElderList()
}

// 重置按钮
const resetQuery = () => {
  filterParams.name = ''
  filterParams.communityId = ''
  filterParams.healthStatus = ''
  pagination.pageNum = 1
  getElderList()
}

// 页码改变
const handleCurrentChange = (val: number) => {
  pagination.pageNum = val
  getElderList()
}

// 页容量改变
const handleSizeChange = (val: number) => {
  pagination.pageSize = val
  pagination.pageNum = 1
  getElderList()
}

// 查看详情
const viewDetail = (row: ElderItem) => {
  Object.assign(detailData, row)
  detailVisible.value = true
}

// 编辑老人
const editElder = (row: ElderItem) => {
  isEdit.value = true
  // 填充表单数据
  form.name = row.name
  form.gender = row.gender
  form.age = row.age
  form.communityId = row.communityId
  form.householdAddress = row.householdAddress
  form.healthStatus = row.healthStatus
  form.emergencyContact = row.emergencyContact
  form.emergencyPhone = row.emergencyPhone
  form.managerId = row.managerId
  dialogVisible.value = true
}

// 删除老人
const deleteElder = (row: ElderItem) => {
  ElMessageBox.confirm('确定要删除该老人信息吗？删除后不可恢复！', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 模拟接口删除
    const index = elderList.value.findIndex(item => item.elderId === row.elderId)
    if (index > -1) {
      elderList.value.splice(index, 1)
      pagination.total--
    }
    ElMessage.success('删除成功！')
  }).catch(() => {
    ElMessage.info('已取消删除')
  })
}

// 提交表单（新增/编辑）
const submitForm = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    // 关联管理员信息（从managerList匹配）
    const manager = managerList.find(item => item.managerId === form.managerId)
    const communityName = form.communityId === 1 ? '幸福社区' : form.communityId === 2 ? '阳光社区' : '和谐社区'

    if (isEdit.value) {
      // 编辑逻辑
      const index = elderList.value.findIndex(item => item.elderId === detailData.elderId)
      if (index > -1) {
        elderList.value[index] = {
          ...elderList.value[index],
          name: form.name,
          gender: form.gender,
          age: form.age,
          communityId: form.communityId,
          communityName,
          householdAddress: form.householdAddress,
          healthStatus: form.healthStatus,
          emergencyContact: form.emergencyContact,
          emergencyPhone: form.emergencyPhone,
          managerId: form.managerId,
          managerName: manager?.managerName || '',
          managerPhone: manager?.managerPhone || '',
          updatedAt: new Date().toLocaleString()
        }
      }
      ElMessage.success('编辑成功！')
    } else {
      // 新增逻辑
      const newElder: ElderItem = {
        elderId: elderList.value.length > 0 ? Math.max(...elderList.value.map(item => item.elderId)) + 1 : 1001,
        name: form.name,
        gender: form.gender,
        age: form.age,
        communityId: form.communityId,
        communityName,
        householdAddress: form.householdAddress,
        healthStatus: form.healthStatus,
        emergencyContact: form.emergencyContact,
        emergencyPhone: form.emergencyPhone,
        managerId: form.managerId,
        managerName: manager?.managerName || '',
        managerPhone: manager?.managerPhone || '',
        createdAt: new Date().toLocaleString(),
        updatedAt: new Date().toLocaleString()
      }
      elderList.value.unshift(newElder)
      pagination.total++
      ElMessage.success('新增成功！')
    }
    // 关闭弹窗并重置表单
    dialogVisible.value = false
    resetForm()
  } catch (error) {
    ElMessage.error('表单校验失败，请检查输入内容！')
  }
}

// 重置表单
const resetForm = () => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  isEdit.value = false
  Object.assign(form, {
    name: '',
    gender: 0,
    age: 0,
    communityId: 0,
    householdAddress: '',
    healthStatus: 0,
    emergencyContact: '',
    emergencyPhone: '',
    managerId: 0
  })
}
</script>

<style scoped>
/* 页面容器 - 纯净白色主题 */
.elders-container {
  width: 100%;
  min-height: calc(100vh - 20px); /* 确保容器撑满可视区域 */
  padding: 0;
  color: #333333;
  background-color: #f9f9f9;
}

/* 顶部平台标题栏 - 浅蓝白配色 */
.platform-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background-color: #ffffff;
  border-bottom: 1px solid #e6e6e6;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}
.platform-header h1 {
  margin: 0;
  font-size: 20px;
  color: #1f2937;
  font-weight: 600;
}
.platform-subtitle {
  font-size: 14px;
  color: #666666;
}

/* 页面标题+筛选栏 */
.page-header {
  padding: 20px;
}
.page-header h2 {
  margin: 0 0 16px 0;
  font-size: 20px;
  color: #1f2937;
  font-weight: 600;
}
.search-bar {
  width: 100%;
  padding: 16px;
  background-color: #ffffff;
  border-radius: 8px;
  border: 1px solid #e6e6e6;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}
.search-buttons {
  display: flex;
  gap: 8px;
}

/* 表格样式 - 纯净白色主题 */
:deep(.el-table) {
  --el-table-bg-color: #ffffff;
  --el-table-text-color: #333333;
  --el-table-header-text-color: #1f2937;
  --el-table-row-hover-bg-color: #f0f9ff;
  --el-table-border-color: #e6e6e6;
  --el-table-stripe-bg-color: #fafafa;
}
:deep(.el-table--border::after),
:deep(.el-table--group::after),
:deep(.el-table::before) {
  background-color: #e6e6e6;
}
:deep(.el-table__cell) {
  border-color: #e6e6e6 !important;
}
:deep(.el-table .el-table__header-wrapper .el-table__cell > .cell) {
  font-weight: 600;
}

/* 分页器样式 - 白色主题 */
.pagination-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background-color: #ffffff;
  margin-top: 16px;
  border-radius: 8px;
  border: 1px solid #e6e6e6;
}
.pagination-total {
  color: #666666;
  font-size: 14px;
}
:deep(.el-pagination) {
  --el-pagination-text-color: #333333;
  --el-pagination-button-color: #333333;
  --el-pagination-button-bg-color: #ffffff;
  --el-pagination-button-hover-bg-color: #f0f0f0;
  --el-pagination-button-active-bg-color: #409eff;
  --el-pagination-button-active-color: #ffffff;
}

/* 弹窗样式 - 白色主题 */
:deep(.elder-dialog) {
  --el-dialog-bg-color: #ffffff;
  --el-dialog-title-color: #1f2937;
  --el-dialog-border-color: #e6e6e6;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
:deep(.el-dialog__header) {
  border-bottom: 1px solid #e6e6e6;
  padding-bottom: 12px;
}
:deep(.el-dialog__footer) {
  border-top: 1px solid #e6e6e6;
  padding-top: 12px;
}

/* 描述列表样式 - 白色主题 */
:deep(.el-descriptions) {
  --el-descriptions-label-color: #1f2937;
  --el-descriptions-content-color: #333333;
  --el-descriptions-border-color: #e6e6e6;
}
:deep(.el-descriptions__label),
:deep(.el-descriptions__content) {
  border-color: #e6e6e6 !important;
}

/* 表单样式 - 白色主题 */
:deep(.el-form-item__label) {
  color: #1f2937 !important;
}
:deep(.el-input__inner) {
  background-color: #ffffff;
  border-color: #e6e6e6;
  color: #333333;
}
:deep(.el-select .el-input__inner) {
  background-color: #ffffff;
  border-color: #e6e6e6;
  color: #333333;
}
:deep(.el-select-dropdown) {
  background-color: #ffffff;
  border-color: #e6e6e6;
}
:deep(.el-select-dropdown__item) {
  color: #333333;
}
:deep(.el-select-dropdown__item:hover) {
  background-color: #f0f9ff;
}

/* 操作按钮容器 */
.action-buttons {
  display: flex;
  align-items: center;
  gap: 8px;
  justify-content: center;
}
/* 操作按钮样式优化 */
.action-buttons .el-button {
  justify-content: center;
  padding: 6px 10px;
  font-size: 12px;
  min-width: auto;
  border-radius: 4px;
}

/* 标签样式优化 */
:deep(.el-tag) {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}
</style>