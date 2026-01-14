<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <el-icon><Monitor /></el-icon>
        <span>设备管理系统</span>
      </div>
      <el-menu
        :default-active="route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataBoard /></el-icon>
          <span>数据看板</span>
        </el-menu-item>
        <el-menu-item v-if="hasPermission('equipment')" index="/equipment">
          <el-icon><Monitor /></el-icon>
          <span>设备管理</span>
        </el-menu-item>
        <el-menu-item v-if="hasPermission('inspection')" index="/inspection">
          <el-icon><Checked /></el-icon>
          <span>点检管理</span>
        </el-menu-item>
        <el-menu-item v-if="hasPermission('patrol')" index="/patrol">
          <el-icon><Location /></el-icon>
          <span>巡检管理</span>
        </el-menu-item>
        <el-menu-item v-if="hasPermission('maintenance')" index="/maintenance">
          <el-icon><Tools /></el-icon>
          <span>维修管理</span>
        </el-menu-item>
        <el-menu-item v-if="hasPermission('maintenance')" index="/spare-parts">
          <el-icon><Box /></el-icon>
          <span>备件管理</span>
        </el-menu-item>
        <el-menu-item v-if="hasPermission('system')" index="/system/user">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-tag v-if="userStore.userInfo?.roleName" type="info" size="small">
            {{ userStore.userInfo.roleName }}
          </el-tag>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ userStore.userInfo?.realName || '用户' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { onMounted, computed } from 'vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 角色权限映射
const rolePermissions = {
  ADMIN: ['dashboard', 'equipment', 'inspection', 'patrol', 'maintenance', 'system'],
  EQUIPMENT_ADMIN: ['dashboard', 'equipment'],
  INSPECTOR: ['dashboard', 'inspection'],
  MAINTAINER: ['dashboard', 'maintenance'],
  PATROLLER: ['dashboard', 'patrol']
}

// 检查是否有权限
function hasPermission(permission) {
  const roleCode = userStore.userInfo?.roleCode
  if (!roleCode) return false
  const permissions = rolePermissions[roleCode] || []
  return permissions.includes(permission)
}

onMounted(async () => {
  if (userStore.token && !userStore.userInfo) {
    await userStore.getUserInfo()
  }
})

function handleCommand(command) {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped lang="scss">
.layout-container {
  height: 100%;
}

.aside {
  background-color: #304156;
  
  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 18px;
    font-weight: bold;
    gap: 8px;
  }
}

.header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  
  .header-left {
    display: flex;
    align-items: center;
  }
  
  .header-right {
    display: flex;
    align-items: center;
  }
  
  .user-info {
    display: flex;
    align-items: center;
    gap: 4px;
    cursor: pointer;
  }
}

.main {
  background: #f0f2f5;
}
</style>
