import { createRouter, createWebHistory } from 'vue-router'

// 角色权限映射
const rolePermissions = {
  ADMIN: ['dashboard', 'equipment', 'inspection', 'patrol', 'maintenance', 'system'],
  EQUIPMENT_ADMIN: ['dashboard', 'equipment'],
  INSPECTOR: ['dashboard', 'inspection'],
  MAINTAINER: ['dashboard', 'maintenance'],
  PATROLLER: ['dashboard', 'patrol']
}

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '数据看板', permission: 'dashboard' }
      },
      {
        path: 'equipment',
        name: 'Equipment',
        component: () => import('@/views/equipment/index.vue'),
        meta: { title: '设备管理', permission: 'equipment' }
      },
      {
        path: 'inspection',
        name: 'Inspection',
        component: () => import('@/views/inspection/index.vue'),
        meta: { title: '点检管理', permission: 'inspection' }
      },
      {
        path: 'patrol',
        name: 'Patrol',
        component: () => import('@/views/patrol/index.vue'),
        meta: { title: '巡检管理', permission: 'patrol' }
      },
      {
        path: 'maintenance',
        name: 'Maintenance',
        component: () => import('@/views/maintenance/index.vue'),
        meta: { title: '维修管理', permission: 'maintenance' }
      },
      {
        path: 'spare-parts',
        name: 'SpareParts',
        component: () => import('@/views/spare-parts/index.vue'),
        meta: { title: '备件管理', permission: 'maintenance' }
      },
      {
        path: 'system/user',
        name: 'UserManagement',
        component: () => import('@/views/system/user.vue'),
        meta: { title: '用户管理', permission: 'system' }
      }
    ]
  },
  {
    path: '/403',
    name: 'Forbidden',
    component: () => import('@/views/error/403.vue')
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const token = localStorage.getItem('token')
  
  // 未登录跳转登录页
  if (to.path !== '/login' && !token) {
    next('/login')
    return
  }
  
  // 登录页直接放行
  if (to.path === '/login') {
    next()
    return
  }
  
  // 检查权限
  const permission = to.meta?.permission
  if (permission) {
    // 从 localStorage 获取用户角色
    const userInfoStr = localStorage.getItem('userInfo')
    if (userInfoStr) {
      const userInfo = JSON.parse(userInfoStr)
      const roleCode = userInfo.roleCode
      const permissions = rolePermissions[roleCode] || []
      
      if (!permissions.includes(permission)) {
        next('/403')
        return
      }
    }
  }
  
  next()
})

export default router
