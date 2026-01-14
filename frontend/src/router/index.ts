import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
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
        meta: { title: '数据看板', icon: 'DataBoard' }
      },
      {
        path: 'equipment',
        name: 'Equipment',
        component: () => import('@/views/equipment/index.vue'),
        meta: { title: '设备管理', icon: 'Monitor' }
      },
      {
        path: 'inspection',
        name: 'Inspection',
        component: () => import('@/views/inspection/index.vue'),
        meta: { title: '点检管理', icon: 'Checked' }
      },
      {
        path: 'patrol',
        name: 'Patrol',
        component: () => import('@/views/patrol/index.vue'),
        meta: { title: '巡检管理', icon: 'Location' }
      },
      {
        path: 'maintenance',
        name: 'Maintenance',
        component: () => import('@/views/maintenance/index.vue'),
        meta: { title: '维修管理', icon: 'Tools' }
      },
      {
        path: 'spare-parts',
        name: 'SpareParts',
        component: () => import('@/views/spare-parts/index.vue'),
        meta: { title: '备件管理', icon: 'Box' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
