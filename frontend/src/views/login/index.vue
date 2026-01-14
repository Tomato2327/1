<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="card-header">
          <el-icon size="24"><Monitor /></el-icon>
          <span>设备管理系统</span>
        </div>
      </template>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value?.validate()
  loading.value = true
  try {
    const res = await userStore.login(form.username, form.password)
    ElMessage.success('登录成功')
    
    // 根据角色跳转到对应页面
    const roleCode = res.data.roleCode
    let targetPath = '/dashboard'
    
    if (roleCode === 'MAINTAINER') {
      targetPath = '/maintenance'
    } else if (roleCode === 'INSPECTOR') {
      targetPath = '/inspection'
    } else if (roleCode === 'PATROLLER') {
      targetPath = '/patrol'
    } else if (roleCode === 'EQUIPMENT_ADMIN') {
      targetPath = '/equipment'
    }
    
    router.push(targetPath)
  } catch (e) {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.login-container {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-card {
  width: 400px;
  
  .card-header {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    font-size: 20px;
    font-weight: bold;
  }
}
</style>
