<template>
  <div class="page-container">
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="点检计划" name="plan">
        <el-card>
          <div class="search-bar">
            <el-select v-model="planQuery.planType" placeholder="计划类型" clearable style="width: 120px">
              <el-option label="日检" value="daily" />
              <el-option label="周检" value="weekly" />
              <el-option label="月检" value="monthly" />
            </el-select>
            <el-button type="primary" @click="loadPlans">查询</el-button>
            <el-button v-if="isAdmin" type="success" @click="handleAddPlan">新增计划</el-button>
          </div>
          <el-table :data="plans" border>
            <el-table-column prop="name" label="计划名称" />
            <el-table-column prop="planType" label="类型" width="100">
              <template #default="{ row }">{{ typeText[row.planType] }}</template>
            </el-table-column>
            <el-table-column prop="assigneeName" label="负责人" width="100" />
            <el-table-column prop="startDate" label="开始日期" width="120" />
            <el-table-column prop="endDate" label="结束日期" width="120" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button v-if="isAdmin" link type="primary" @click="handleEditPlan(row)">编辑</el-button>
                <el-button v-if="isAdmin" link type="success" @click="handleGenerateTasks(row)">生成任务</el-button>
                <el-button v-if="isAdmin" link type="danger" @click="handleDeletePlan(row)">删除</el-button>
                <span v-if="!isAdmin" class="text-muted">仅查看</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="点检任务" name="task">
        <el-card>
          <div class="search-bar">
            <el-select v-model="taskQuery.status" placeholder="状态" clearable style="width: 120px">
              <el-option label="待执行" :value="0" />
              <el-option label="已完成" :value="1" />
              <el-option label="已过期" :value="2" />
            </el-select>
            <el-date-picker v-model="taskQuery.taskDate" type="date" placeholder="任务日期" value-format="YYYY-MM-DD" />
            <el-button type="primary" @click="loadTasks">查询</el-button>
            <el-button v-if="isAdmin" type="success" @click="handleAddTask">新增任务</el-button>
          </div>
          <el-table :data="tasks" border>
            <el-table-column prop="equipmentName" label="设备" />
            <el-table-column prop="assigneeName" label="执行人" width="100" />
            <el-table-column prop="taskDate" label="任务日期" width="120" />
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="taskStatusType[row.status]">{{ taskStatusText[row.status] }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <!-- 点检员可以完成任务 -->
                <el-button v-if="row.status === 0" link type="success" @click="handleCompleteTask(row)">完成</el-button>
                <!-- 只有管理员可以删除任务 -->
                <el-button v-if="row.status === 0 && isAdmin" link type="danger" @click="handleDeleteTask(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="点检记录" name="record">
        <el-card>
          <div class="search-bar">
            <el-date-picker v-model="recordQuery.dateRange" type="daterange" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
            <el-select v-model="recordQuery.result" placeholder="结果" clearable style="width: 100px">
              <el-option label="正常" :value="1" />
              <el-option label="异常" :value="2" />
            </el-select>
            <el-button type="primary" @click="loadRecords">查询</el-button>
          </div>
          <el-table :data="records" border>
            <el-table-column prop="itemName" label="点检项目" />
            <el-table-column prop="result" label="结果" width="80">
              <template #default="{ row }">
                <el-tag :type="row.result === 1 ? 'success' : 'danger'">{{ row.result === 1 ? '正常' : '异常' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="resultValue" label="检测值" width="120" />
            <el-table-column prop="inspectorName" label="点检人" width="100" />
            <el-table-column prop="checkTime" label="点检时间" width="160" />
            <el-table-column prop="remark" label="备注" show-overflow-tooltip />
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 新增/编辑计划对话框 -->
    <el-dialog v-model="planDialogVisible" :title="planForm.id ? '编辑计划' : '新增计划'" width="550px">
      <el-form ref="planFormRef" :model="planForm" :rules="planRules" label-width="90px">
        <el-form-item label="计划名称" prop="name">
          <el-input v-model="planForm.name" placeholder="请输入计划名称" />
        </el-form-item>
        <el-form-item label="计划类型" prop="planType">
          <el-select v-model="planForm.planType" placeholder="请选择计划类型" style="width: 100%">
            <el-option label="日检" value="daily" />
            <el-option label="周检" value="weekly" />
            <el-option label="月检" value="monthly" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联设备" prop="equipmentIds">
          <el-select v-model="planForm.equipmentIds" multiple placeholder="请选择设备" style="width: 100%">
            <el-option v-for="eq in equipments" :key="eq.id" :label="eq.name" :value="eq.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人" prop="assigneeId">
          <el-select v-model="planForm.assigneeId" placeholder="请选择负责人" style="width: 100%">
            <el-option v-for="u in users" :key="u.id" :label="u.realName || u.username" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="planForm.startDate" type="date" placeholder="请选择开始日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker v-model="planForm.endDate" type="date" placeholder="请选择结束日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="planForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="planDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPlanForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增任务对话框 -->
    <el-dialog v-model="taskDialogVisible" title="新增点检任务" width="500px">
      <el-form ref="taskFormRef" :model="taskForm" :rules="taskRules" label-width="90px">
        <el-form-item label="设备" prop="equipmentId">
          <el-select v-model="taskForm.equipmentId" placeholder="请选择设备" style="width: 100%">
            <el-option v-for="eq in equipments" :key="eq.id" :label="eq.name" :value="eq.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行人" prop="assigneeId">
          <el-select v-model="taskForm.assigneeId" placeholder="请选择执行人" style="width: 100%">
            <el-option v-for="u in users" :key="u.id" :label="u.realName || u.username" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务日期" prop="taskDate">
          <el-date-picker v-model="taskForm.taskDate" type="date" placeholder="请选择任务日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="taskDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTaskForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { isAdmin as checkIsAdmin } from '@/utils/permission'

const activeTab = ref('plan')
const plans = ref([])
const tasks = ref([])
const records = ref([])
const equipments = ref([])
const users = ref([])

// 权限判断
const isAdmin = computed(() => checkIsAdmin())

const typeText = { daily: '日检', weekly: '周检', monthly: '月检' }
const taskStatusText = { 0: '待执行', 1: '已完成', 2: '已过期' }
const taskStatusType = { 0: 'info', 1: 'success', 2: 'danger' }

const planQuery = reactive({ planType: '' })
const taskQuery = reactive({ status: undefined, taskDate: '' })
const recordQuery = reactive({ dateRange: [], result: undefined })

// 计划表单
const planDialogVisible = ref(false)
const planFormRef = ref(null)
const planForm = reactive({ id: null, name: '', planType: '', equipmentIds: [], assigneeId: null, startDate: '', endDate: '', status: 1 })
const planRules = {
  name: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  planType: [{ required: true, message: '请选择计划类型', trigger: 'change' }],
  equipmentIds: [{ required: true, message: '请选择关联设备', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }]
}

// 任务表单
const taskDialogVisible = ref(false)
const taskFormRef = ref(null)
const taskForm = reactive({ equipmentId: null, assigneeId: null, taskDate: '' })
const taskRules = {
  equipmentId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  assigneeId: [{ required: true, message: '请选择执行人', trigger: 'change' }],
  taskDate: [{ required: true, message: '请选择任务日期', trigger: 'change' }]
}

onMounted(() => { loadPlans(); loadEquipments(); loadUsers() })

function handleTabChange(tab) {
  if (tab === 'task') loadTasks()
  else if (tab === 'record') loadRecords()
}

async function loadPlans() {
  const res = await request.get('/inspection/plans', { params: { current: 1, size: 100, ...planQuery } })
  plans.value = res.data.records || []
}

async function loadTasks() {
  const res = await request.get('/inspection/tasks', { params: { current: 1, size: 100, ...taskQuery } })
  tasks.value = res.data?.records || []
}

async function loadRecords() {
  const params = { current: 1, size: 100, result: recordQuery.result }
  if (recordQuery.dateRange?.length === 2) {
    params.startDate = recordQuery.dateRange[0]
    params.endDate = recordQuery.dateRange[1]
  }
  const res = await request.get('/inspection/records', { params })
  records.value = res.data.records || []
}

async function loadEquipments() {
  try {
    const res = await request.get('/equipment/list', { params: { current: 1, size: 1000 } })
    equipments.value = res.data.records || []
  } catch (e) { equipments.value = [] }
}

async function loadUsers() {
  try {
    const res = await request.get('/system/user/inspectors')
    users.value = res.data || []
  } catch (e) { users.value = [] }
}

function handleAddPlan() {
  Object.assign(planForm, { id: null, name: '', planType: '', equipmentIds: [], assigneeId: null, startDate: '', endDate: '', status: 1 })
  planDialogVisible.value = true
}

function handleEditPlan(row) {
  Object.assign(planForm, {
    id: row.id, name: row.name, planType: row.planType,
    equipmentIds: row.equipmentIds ? row.equipmentIds.split(',').map(Number) : [],
    assigneeId: row.assigneeId, startDate: row.startDate, endDate: row.endDate, status: row.status
  })
  planDialogVisible.value = true
}

async function submitPlanForm() {
  await planFormRef.value.validate()
  const data = { ...planForm, equipmentIds: planForm.equipmentIds.join(',') }
  if (planForm.id) {
    await request.put(`/inspection/plan/${planForm.id}`, data)
    ElMessage.success('更新成功')
  } else {
    await request.post('/inspection/plan', data)
    ElMessage.success('新增成功')
  }
  planDialogVisible.value = false
  loadPlans()
}

async function handleDeletePlan(row) {
  await ElMessageBox.confirm('确定删除该计划?', '提示', { type: 'warning' })
  await request.delete(`/inspection/plans/${row.id}`)
  ElMessage.success('删除成功')
  loadPlans()
}

async function handleGenerateTasks(row) {
  await ElMessageBox.confirm('确定根据此计划生成今日点检任务?', '提示', { type: 'info' })
  await request.post(`/inspection/plans/${row.id}/generate-tasks`)
  ElMessage.success('任务生成成功')
  activeTab.value = 'task'
  loadTasks()
}

function handleAddTask() {
  Object.assign(taskForm, { equipmentId: null, assigneeId: null, taskDate: '' })
  taskDialogVisible.value = true
}

async function submitTaskForm() {
  await taskFormRef.value.validate()
  await request.post('/inspection/task', taskForm)
  ElMessage.success('新增成功')
  taskDialogVisible.value = false
  loadTasks()
}

async function handleCompleteTask(row) {
  await request.post(`/inspection/tasks/${row.id}/complete`)
  ElMessage.success('任务已完成')
  loadTasks()
}

async function handleDeleteTask(row) {
  await ElMessageBox.confirm('确定删除该任务?', '提示', { type: 'warning' })
  await request.delete(`/inspection/tasks/${row.id}`)
  ElMessage.success('删除成功')
  loadTasks()
}
</script>

<style scoped>
.search-bar { display: flex; gap: 10px; margin-bottom: 15px; flex-wrap: wrap; }
.text-muted { color: #909399; font-size: 12px; }
</style>
