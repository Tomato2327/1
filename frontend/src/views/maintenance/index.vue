<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input v-model="query.keyword" placeholder="工单号/设备名称" clearable style="width: 200px" @keyup.enter="loadData" />
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 120px">
          <el-option label="待派发" :value="1" />
          <el-option label="待接单" :value="2" />
          <el-option label="维修中" :value="3" />
          <el-option label="待验收" :value="4" />
          <el-option label="已完成" :value="5" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="handleAdd">报修</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="orderNo" label="工单号" width="150" />
        <el-table-column prop="equipmentName" label="设备" min-width="150" />
        <el-table-column prop="faultDesc" label="故障描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType[row.status]">{{ statusText[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="80">
          <template #default="{ row }">
            <el-tag :type="priorityType[row.priority]" size="small">{{ priorityText[row.priority] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assigneeName" label="维修人" width="100" />
        <el-table-column prop="totalCost" label="总成本" width="100">
          <template #default="{ row }">¥{{ row.totalCost?.toFixed(2) || '0.00' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <!-- 派发：只有管理员可以操作 -->
            <el-button v-if="row.status === 1 && isAdmin" link type="primary" @click="handleAssign(row)">派发</el-button>
            <!-- 接单：维修员可以操作 -->
            <el-button v-if="row.status === 2" link type="primary" @click="handleAccept(row)">接单</el-button>
            <!-- 完成：维修员可以操作 -->
            <el-button v-if="row.status === 3" link type="primary" @click="handleFinish(row)">完成</el-button>
            <!-- 验收/退回：只有管理员可以操作 -->
            <el-button v-if="row.status === 4 && isAdmin" link type="success" @click="handleCheck(row, true)">验收</el-button>
            <el-button v-if="row.status === 4 && isAdmin" link type="warning" @click="handleCheck(row, false)">退回</el-button>
            <!-- 维修员看到待验收状态时显示等待验收提示 -->
            <span v-if="row.status === 4 && !isAdmin" class="text-muted">等待验收</span>
            <!-- 待派发状态维修员看到等待派发提示 -->
            <span v-if="row.status === 1 && !isAdmin" class="text-muted">等待派发</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" layout="total, prev, pager, next" @change="loadData" />
      </div>
    </el-card>

    <!-- 报修弹窗 -->
    <el-dialog v-model="dialogVisible" title="报修" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="设备" prop="equipmentId">
          <el-select v-model="form.equipmentId" filterable style="width: 100%">
            <el-option v-for="e in equipments" :key="e.id" :label="e.name" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="故障描述" prop="faultDesc">
          <el-input v-model="form.faultDesc" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-radio-group v-model="form.priority">
            <el-radio :label="1">紧急</el-radio>
            <el-radio :label="2">一般</el-radio>
            <el-radio :label="3">低</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>

    <!-- 派发弹窗 -->
    <el-dialog v-model="assignDialogVisible" title="派发工单" width="400px">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="维修人员">
          <el-select v-model="assignForm.assigneeId" style="width: 100%" @change="onMaintainerChange">
            <el-option v-for="m in maintainers" :key="m.id" :label="m.realName" :value="m.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAssign">确定派发</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageOrders, createOrder, assignOrder, acceptOrder, finishOrder, checkOrder } from '@/api/maintenance'
import { pageEquipments } from '@/api/equipment'
import { getMaintainers } from '@/api/user'
import { isAdmin as checkIsAdmin } from '@/utils/permission'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const equipments = ref([])
const maintainers = ref([])
const dialogVisible = ref(false)
const assignDialogVisible = ref(false)
const formRef = ref()
const currentOrderId = ref(null)

// 权限判断
const isAdmin = computed(() => checkIsAdmin())

const statusText = { 1: '待派发', 2: '待接单', 3: '维修中', 4: '待验收', 5: '已完成' }
const statusType = { 1: 'info', 2: 'warning', 3: 'primary', 4: '', 5: 'success' }
const priorityText = { 1: '紧急', 2: '一般', 3: '低' }
const priorityType = { 1: 'danger', 2: 'warning', 3: 'info' }

const query = reactive({ current: 1, size: 10, keyword: '', status: undefined })
const form = reactive({ equipmentId: 0, faultDesc: '', priority: 2 })
const assignForm = reactive({ assigneeId: null, assigneeName: '' })
const rules = { equipmentId: [{ required: true, message: '请选择设备' }], faultDesc: [{ required: true, message: '请输入故障描述' }] }

onMounted(() => { loadData(); loadEquipments(); loadMaintainers() })

async function loadData() {
  loading.value = true
  try {
    const res = await pageOrders(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadEquipments() {
  try {
    const res = await pageEquipments({ current: 1, size: 1000 })
    equipments.value = res.data.records || []
  } catch (e) {
    equipments.value = []
  }
}

async function loadMaintainers() {
  try {
    const res = await getMaintainers()
    maintainers.value = res.data || []
  } catch (e) {
    maintainers.value = []
  }
}

function handleAdd() {
  Object.assign(form, { equipmentId: 0, faultDesc: '', priority: 2 })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  await createOrder(form)
  ElMessage.success('报修成功')
  dialogVisible.value = false
  loadData()
}

function handleAssign(row) {
  currentOrderId.value = row.id
  assignForm.assigneeId = null
  assignForm.assigneeName = ''
  assignDialogVisible.value = true
}

function onMaintainerChange(id) {
  const m = maintainers.value.find(x => x.id === id)
  assignForm.assigneeName = m ? m.realName : ''
}

async function submitAssign() {
  if (!assignForm.assigneeId) {
    ElMessage.warning('请选择维修人员')
    return
  }
  await assignOrder(currentOrderId.value, assignForm.assigneeId, assignForm.assigneeName)
  ElMessage.success('派发成功')
  assignDialogVisible.value = false
  loadData()
}

async function handleAccept(row) {
  await acceptOrder(row.id)
  ElMessage.success('接单成功')
  loadData()
}

async function handleFinish(row) {
  const { value } = await ElMessageBox.prompt('请输入维修措施', '完成维修')
  await finishOrder(row.id, value, 100)
  ElMessage.success('已完成')
  loadData()
}

async function handleCheck(row, passed) {
  await checkOrder(row.id, 1, passed)
  ElMessage.success(passed ? '验收通过' : '已退回')
  loadData()
}
</script>

<style scoped>
.search-bar { display: flex; gap: 10px; margin-bottom: 15px; flex-wrap: wrap; }
.pagination-container { margin-top: 15px; display: flex; justify-content: flex-end; }
.text-muted { color: #909399; font-size: 12px; }
</style>
