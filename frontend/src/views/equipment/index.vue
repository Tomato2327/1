<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input v-model="query.keyword" placeholder="设备名称/编号" clearable style="width: 200px" @keyup.enter="loadData" />
        <el-select v-model="query.categoryId" placeholder="设备分类" clearable style="width: 150px">
          <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 120px">
          <el-option label="运行" :value="1" />
          <el-option label="停机" :value="2" />
          <el-option label="维修" :value="3" />
          <el-option label="报废" :value="4" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="handleAdd">新增设备</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="code" label="设备编号" width="120" />
        <el-table-column prop="name" label="设备名称" min-width="150" />
        <el-table-column prop="model" label="型号" width="120" />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusType[row.status]">{{ statusText[row.status] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" width="150" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleQrCode(row)">二维码</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="query.current"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @change="loadData"
        />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑设备' : '新增设备'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="设备编号" prop="code">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="设备名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="型号">
          <el-input v-model="form.model" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" style="width: 100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="制造商">
          <el-input v-model="form.manufacturer" />
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
import { pageEquipments, createEquipment, updateEquipment, deleteEquipment, getCategories, generateQrCode } from '@/api/equipment'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const categories = ref([])
const dialogVisible = ref(false)
const formRef = ref()

const statusText = { 1: '运行', 2: '停机', 3: '维修', 4: '报废' }
const statusType = { 1: 'success', 2: 'info', 3: 'warning', 4: 'danger' }

const query = reactive({ current: 1, size: 10, keyword: '', categoryId: undefined, status: undefined })
const form = reactive({ code: '', name: '', status: 1 })
const rules = { code: [{ required: true, message: '请输入设备编号' }], name: [{ required: true, message: '请输入设备名称' }] }

onMounted(() => { loadData(); loadCategories() })

async function loadData() {
  loading.value = true
  try {
    const res = await pageEquipments(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadCategories() {
  const res = await getCategories()
  categories.value = res.data
}

function handleAdd() {
  Object.assign(form, { id: undefined, code: '', name: '', model: '', categoryId: undefined, location: '', manufacturer: '', status: 1 })
  dialogVisible.value = true
}

function handleEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  if (form.id) {
    await updateEquipment(form.id, form)
  } else {
    await createEquipment(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该设备?', '提示', { type: 'warning' })
  await deleteEquipment(row.id)
  ElMessage.success('删除成功')
  loadData()
}

async function handleQrCode(row) {
  const res = await generateQrCode(row.id)
  const url = URL.createObjectURL(res)
  const a = document.createElement('a')
  a.href = url
  a.download = `${row.code}_qrcode.png`
  a.click()
}
</script>
