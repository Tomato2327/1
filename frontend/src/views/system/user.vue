<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input v-model="query.keyword" placeholder="用户名/姓名/手机号" clearable style="width: 200px" @keyup.enter="loadData" />
        <el-select v-model="query.roleCode" placeholder="角色" clearable style="width: 140px">
          <el-option v-for="r in roles" :key="r.code" :label="r.name" :value="r.code" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="handleAdd">新增用户</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="姓名" width="100" />
        <el-table-column prop="roleName" label="角色" width="120">
          <template #default="{ row }">
            <el-tag>{{ row.roleName || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="warning" @click="handleResetPwd(row)">重置密码</el-button>
            <el-button v-if="row.id !== 1" link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" layout="total, prev, pager, next" @change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑用户' : '新增用户'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item v-if="!form.id" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="姓名" prop="realName">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="form.roleId" style="width: 100%">
            <el-option v-for="r in roles" :key="r.id" :label="r.name" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageUsers, createUser, updateUser, deleteUser, resetPassword, getRoles } from '@/api/user'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const roles = ref([])
const dialogVisible = ref(false)
const formRef = ref()

const query = reactive({ current: 1, size: 10, keyword: '', roleCode: '' })
const form = reactive({ id: null, username: '', password: '', realName: '', roleId: null, phone: '', email: '', status: 1 })

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

onMounted(() => { loadRoles(); loadData() })

async function loadRoles() {
  const res = await getRoles()
  roles.value = res.data
}

async function loadData() {
  loading.value = true
  try {
    const res = await pageUsers(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  Object.assign(form, { id: null, username: '', password: '', realName: '', roleId: null, phone: '', email: '', status: 1 })
  dialogVisible.value = true
}

function handleEdit(row) {
  Object.assign(form, { ...row, password: '' })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  if (form.id) {
    await updateUser(form.id, form)
  } else {
    await createUser(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该用户?', '提示', { type: 'warning' })
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  loadData()
}

async function handleResetPwd(row) {
  const { value } = await ElMessageBox.prompt('请输入新密码', '重置密码', { inputType: 'password' })
  await resetPassword(row.id, value)
  ElMessage.success('密码重置成功')
}
</script>

<style scoped>
.search-bar { display: flex; gap: 10px; margin-bottom: 15px; flex-wrap: wrap; }
.pagination-container { margin-top: 15px; display: flex; justify-content: flex-end; }
</style>
