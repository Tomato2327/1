<template>
  <div class="page-container">
    <el-card>
      <div class="search-bar">
        <el-input v-model="query.keyword" placeholder="备件名称/编号" clearable style="width: 200px" @keyup.enter="loadData" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="handleAdd">新增备件</el-button>
        <el-button type="warning" @click="showLowStock">库存预警</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="code" label="备件编号" width="120" />
        <el-table-column prop="name" label="备件名称" min-width="150" />
        <el-table-column prop="model" label="规格型号" width="120" />
        <el-table-column prop="stockQty" label="库存数量" width="100">
          <template #default="{ row }">
            <span :class="{ 'text-danger': row.lowStock }">{{ row.stockQty }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="safeQty" label="安全库存" width="100" />
        <el-table-column prop="price" label="单价" width="100">
          <template #default="{ row }">¥{{ row.price?.toFixed(2) || '0.00' }}</template>
        </el-table-column>
        <el-table-column prop="location" label="存放位置" width="120" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="success" @click="handleStockIn(row)">入库</el-button>
            <el-button link type="warning" @click="handleStockOut(row)">出库</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" :total="total" layout="total, prev, pager, next" @change="loadData" />
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑备件' : '新增备件'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="备件编号" prop="code">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="备件名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="规格型号">
          <el-input v-model="form.model" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" />
        </el-form-item>
        <el-form-item label="单价">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="安全库存">
          <el-input-number v-model="form.safeQty" :min="0" />
        </el-form-item>
        <el-form-item label="存放位置">
          <el-input v-model="form.location" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="lowStockVisible" title="库存预警" width="600px">
      <el-table :data="lowStockList" border>
        <el-table-column prop="code" label="编号" width="100" />
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="stockQty" label="当前库存" width="100">
          <template #default="{ row }"><span class="text-danger">{{ row.stockQty }}</span></template>
        </el-table-column>
        <el-table-column prop="safeQty" label="安全库存" width="100" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageParts, createPart, updatePart, deletePart, stockIn, stockOut, getLowStockParts } from '@/api/maintenance'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const lowStockVisible = ref(false)
const lowStockList = ref([])
const formRef = ref()

const query = reactive({ current: 1, size: 10, keyword: '' })
const form = reactive({ code: '', name: '', stockQty: 0, safeQty: 0 })
const rules = { code: [{ required: true, message: '请输入备件编号' }], name: [{ required: true, message: '请输入备件名称' }] }

onMounted(() => loadData())

async function loadData() {
  loading.value = true
  try {
    const res = await pageParts(query)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  Object.assign(form, { id: undefined, code: '', name: '', model: '', unit: '', price: 0, safeQty: 0, location: '' })
  dialogVisible.value = true
}

function handleEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  if (form.id) {
    await updatePart(form.id, form)
  } else {
    await createPart(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  loadData()
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除?', '提示', { type: 'warning' })
  await deletePart(row.id)
  ElMessage.success('删除成功')
  loadData()
}

async function handleStockIn(row) {
  const { value } = await ElMessageBox.prompt('请输入入库数量', '入库')
  await stockIn(row.id, parseInt(value))
  ElMessage.success('入库成功')
  loadData()
}

async function handleStockOut(row) {
  const { value } = await ElMessageBox.prompt('请输入出库数量', '出库')
  await stockOut(row.id, parseInt(value))
  ElMessage.success('出库成功')
  loadData()
}

async function showLowStock() {
  const res = await getLowStockParts()
  lowStockList.value = res.data
  lowStockVisible.value = true
}
</script>

<style scoped>
.text-danger { color: #f56c6c; font-weight: bold; }
</style>
