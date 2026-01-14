import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  async function login(username, password) {
    const res = await request.post('/auth/login', { username, password })
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    
    // 保存基本用户信息
    const info = {
      userId: res.data.userId,
      username: res.data.username,
      realName: res.data.realName,
      roleCode: res.data.roleCode,
      roleName: res.data.roleName
    }
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
    
    return res
  }

  async function getUserInfo() {
    const res = await request.get('/auth/info')
    const info = {
      userId: res.data.userId,
      username: res.data.username,
      realName: res.data.realName,
      roleCode: res.data.roleCode,
      roleName: res.data.roleName,
      permissions: res.data.permissions
    }
    userInfo.value = info
    localStorage.setItem('userInfo', JSON.stringify(info))
    return info
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  return { token, userInfo, login, getUserInfo, logout }
})
