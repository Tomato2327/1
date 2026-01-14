<template>
  <div class="page-container">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="巡检路线" name="route">
        <el-card>
          <div class="search-bar">
            <el-button v-if="isAdmin" type="success" @click="handleAddRoute">新增路线</el-button>
          </div>
          <el-table :data="routes" border>
            <el-table-column prop="name" label="路线名称" />
            <el-table-column prop="description" label="描述" show-overflow-tooltip />
            <el-table-column prop="createTime" label="创建时间" width="160" />
            <el-table-column label="操作" width="150">
              <template #default="{ row }">
                <el-button v-if="isAdmin" link type="primary" @click="handleEditRoute(row)">编辑</el-button>
                <el-button v-if="isAdmin" link type="danger" @click="handleDeleteRoute(row)">删除</el-button>
                <span v-if="!isAdmin" class="text-muted">仅查看</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane label="巡检任务" name="task">
        <el-card>
          <div class="search-bar">
            <el-select v-model="taskQuery.status" placeholder="状态" clearable style="width: 120px">
              <el-option label="待执行" :value="0" />
              <el-option label="执行中" :value="1" />
              <el-option label="已完成" :value="2" />
              <el-option label="已过期" :value="3" />
            </el-select>
            <el-date-picker v-model="taskQuery.taskDate" type="date" placeholder="任务日期" value-format="YYYY-MM-DD" />
            <el-button type="primary" @click="loadTasks">查询</el-button>
            <el-button v-if="isAdmin" type="success" @click="handleAddTask">新增任务</el-button>
          </div>
          <el-table :data="tasks" border>
            <el-table-column prop="routeName" label="路线" />
            <el-table-column prop="assigneeName" label="执行人" width="100" />
            <el-table-column prop="taskDate" label="任务日期" width="120" />
            <el-table-column prop="status" label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="taskStatusType[row.status]">{{ taskStatusText[row.status] }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="进度" width="120">
              <template #default="{ row }">{{ row.completedCount || 0 }} / {{ row.checkpointCount || 0 }}</template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <!-- 巡检员可以开始和完成任务 -->
                <el-button v-if="row.status === 0" link type="primary" @click="handleStartTask(row)">开始</el-button>
                <el-button v-if="row.status === 1" link type="success" @click="handleFinishTask(row)">完成</el-button>
                <el-button link type="info" @click="handleViewTrack(row)">轨迹</el-button>
                <!-- 只有管理员可以删除任务 -->
                <el-button v-if="row.status === 0 && isAdmin" link type="danger" @click="handleDeleteTask(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 新增/编辑路线对话框 -->
    <el-dialog v-model="routeDialogVisible" :title="routeForm.id ? '编辑路线' : '新增路线'" width="500px">
      <el-form ref="routeFormRef" :model="routeForm" :rules="routeRules" label-width="80px">
        <el-form-item label="路线名称" prop="name">
          <el-input v-model="routeForm.name" placeholder="请输入路线名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="routeForm.description" type="textarea" :rows="3" placeholder="请输入路线描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="routeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRouteForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 新增任务对话框 -->
    <el-dialog v-model="taskDialogVisible" title="新增任务" width="500px">
      <el-form ref="taskFormRef" :model="taskForm" :rules="taskRules" label-width="80px">
        <el-form-item label="巡检路线" prop="routeId">
          <el-select v-model="taskForm.routeId" placeholder="请选择巡检路线" style="width: 100%">
            <el-option v-for="r in routes" :key="r.id" :label="r.name" :value="r.id" />
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

    <!-- 轨迹查看对话框 -->
    <el-dialog v-model="trackDialogVisible" title="巡检轨迹" width="700px">
      <div v-if="trackRecords.length === 0" style="text-align: center; padding: 40px; color: #999;">
        暂无巡检记录
      </div>
      <el-timeline v-else>
        <el-timeline-item v-for="(record, index) in trackRecords" :key="index" :timestamp="record.checkTime" placement="top" :type="record.status === 1 ? 'success' : 'danger'">
          <el-card>
            <h4>{{ record.checkpointName || '检查点 ' + (index + 1) }}</h4>
            <p>状态: <el-tag :type="record.status === 1 ? 'success' : 'danger'" size="small">{{ record.status === 1 ? '正常' : '异常' }}</el-tag></p>
            <p v-if="record.remark">备注: {{ record.remark }}</p>
            <p v-if="record.latitude && record.longitude">位置: {{ record.latitude }}, {{ record.longitude }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <template #footer>
        <el-button @click="trackDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { isAdmin as checkIsAdmin } from '@/utils/permission'

const activeTab = ref('route')
const routes = ref([])
const tasks = ref([])
const users = ref([])
const trackRecords = ref([])

// 权限判断
const isAdmin = computed(() => checkIsAdmin())

const taskStatusText = { 0: '待执行', 1: '执行中', 2: '已完成', 3: '已过期' }
const taskStatusType = { 0: 'info', 1: 'primary', 2: 'success', 3: 'danger' }
const taskQuery = reactive({ status: undefined, taskDate: '' })

// 路线表单
const routeDialogVisible = ref(false)
const routeFormRef = ref(null)
const routeForm = reactive({ id: null, name: '', description: '' })
const routeRules = { name: [{ required: true, message: '请输入路线名称', trigger: 'blur' }] }

// 任务表单
const taskDialogVisible = ref(false)
const taskFormRef = ref(null)
const taskForm = reactive({ routeId: null, assigneeId: null, taskDate: '' })
const taskRules = {
  routeId: [{ required: true, message: '请选择巡检路线', trigger: 'change' }],
  assigneeId: [{ required: true, message: '请选择执行人', trigger: 'change' }],
  taskDate: [{ required: true, message: '请选择任务日期', trigger: 'change' }]
}

// 轨迹对话框
const trackDialogVisible = ref(false)

onMounted(() => { loadRoutes(); loadTasks(); loadUsers() })

async function loadRoutes() {
  const res = await request.get('/patrol/routes', { params: { current: 1, size: 100 } })
  routes.value = res.data.records || []
}

async function loadTasks() {
  const res = await request.get('/patrol/tasks', { params: { current: 1, size: 100, ...taskQuery } })
  tasks.value = res.data.records || []
}

async function loadUsers() {
  try {
    const res = await request.get('/system/user/patrollers')
    users.value = res.data || []
  } catch (e) { users.value = [] }
}

function handleAddRoute() {
  Object.assign(routeForm, { id: null, name: '', description: '' })
  routeDialogVisible.value = true
}

function handleEditRoute(row) {
  Object.assign(routeForm, { id: row.id, name: row.name, description: row.description || '' })
  routeDialogVisible.value = true
}

async function submitRouteForm() {
  await routeFormRef.value.validate()
  if (routeForm.id) {
    await request.put(`/patrol/routes/${routeForm.id}`, routeForm)
    ElMessage.success('更新成功')
  } else {
    await request.post('/patrol/routes', routeForm)
    ElMessage.success('新增成功')
  }
  routeDialogVisible.value = false
  loadRoutes()
}

async function handleDeleteRoute(row) {
  await ElMessageBox.confirm('确定删除?', '提示', { type: 'warning' })
  await request.delete(`/patrol/routes/${row.id}`)
  ElMessage.success('删除成功')
  loadRoutes()
}

function handleAddTask() {
  Object.assign(taskForm, { routeId: null, assigneeId: null, taskDate: '' })
  taskDialogVisible.value = true
}

async function submitTaskForm() {
  await taskFormRef.value.validate()
  await request.post('/patrol/tasks', taskForm)
  ElMessage.success('新增成功')
  taskDialogVisible.value = false
  loadTasks()
}

async function handleStartTask(row) {
  await request.post(`/patrol/tasks/${row.id}/start`)
  ElMessage.success('任务已开始')
  loadTasks()
}

async function handleFinishTask(row) {
  await request.post(`/patrol/tasks/${row.id}/finish`)
  ElMessage.success('任务已完成')
  loadTasks()
}

async function handleDeleteTask(row) {
  await ElMessageBox.confirm('确定删除该任务?', '提示', { type: 'warning' })
  await request.delete(`/patrol/tasks/${row.id}`)
  ElMessage.success('删除成功')
  loadTasks()
}

async function handleViewTrack(row) {
  const res = await request.get(`/patrol/tasks/${row.id}/track`)
  trackRecords.value = res.data || []
  trackDialogVisible.value = true
}
</script>

<style scoped>
.search-bar { display: flex; gap: 10px; margin-bottom: 15px; flex-wrap: wrap; }
.text-muted { color: #909399; font-size: 12px; }
</style>
