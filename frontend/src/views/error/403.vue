<template>
  <div class="error-page">
    <div class="error-content">
      <h1>403</h1>
      <p>抱歉，您没有权限访问此页面</p>
      <el-button type="primary" @click="goBack">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function goBack() {
  router.push('/dashboard')
}
</script>

<style scoped>
.error-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f2f5;
}

.error-content {
  text-align: center;
}

.error-content h1 {
  font-size: 120px;
  color: #409eff;
  margin: 0;
}

.error-content p {
  font-size: 18px;
  color: #666;
  margin: 20px 0 30px;
}
</style>
