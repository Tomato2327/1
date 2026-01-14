<template>
  <div class="page-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value">{{ dashboard.equipment?.total || 0 }}</div>
          <div class="stat-label">设备总数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value" style="color: #67c23a">{{ dashboard.equipment?.runningRate?.toFixed(1) || 0 }}%</div>
          <div class="stat-label">设备运行率</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value" style="color: #e6a23c">{{ dashboard.todayInspection?.total || 0 }}</div>
          <div class="stat-label">今日点检数</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-value" style="color: #f56c6c">{{ dashboard.monthMaintenance?.orderCount || 0 }}</div>
          <div class="stat-label">本月维修单</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card>
          <template #header>设备状态分布</template>
          <div ref="equipmentChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>工单状态统计</template>
          <div ref="orderChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getDashboard } from '@/api/report'

const dashboard = ref({})
const equipmentChartRef = ref()
const orderChartRef = ref()

onMounted(async () => {
  const res = await getDashboard()
  dashboard.value = res.data
  initCharts()
})

function initCharts() {
  // 设备状态饼图
  if (equipmentChartRef.value) {
    const chart = echarts.init(equipmentChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'item' },
      series: [{
        type: 'pie',
        radius: ['40%', '70%'],
        data: [
          { value: dashboard.value.equipment?.running || 0, name: '运行中', itemStyle: { color: '#67c23a' } },
          { value: dashboard.value.equipment?.stopped || 0, name: '停机', itemStyle: { color: '#909399' } },
          { value: dashboard.value.equipment?.maintenance || 0, name: '维修中', itemStyle: { color: '#e6a23c' } },
          { value: dashboard.value.equipment?.scrapped || 0, name: '已报废', itemStyle: { color: '#f56c6c' } }
        ]
      }]
    })
  }

  // 工单状态柱状图
  if (orderChartRef.value) {
    const chart = echarts.init(orderChartRef.value)
    const statusMap = { 1: '待派发', 2: '待接单', 3: '维修中', 4: '待验收', 5: '已完成' }
    const stats = dashboard.value.orderStats || []
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: stats.map((s) => statusMap[s.status] || s.status) },
      yAxis: { type: 'value' },
      series: [{ type: 'bar', data: stats.map((s) => s.count), itemStyle: { color: '#409eff' } }]
    })
  }
}
</script>
