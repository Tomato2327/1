import { defineStore } from 'pinia'
import { ref } from 'vue'
import request from '@/utils/request'

interface UserInfo {
  id: number
  username: string
  realName: string
  roles: string[]
  permissions: string[]
}

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)

  async function login(username: string, password: string) {
    const res: any = await request.post('/auth/login', { username, password })
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    return res
  }

  async function getUserInfo() {
    const res: any = await request.get('/auth/info')
    userInfo.value = res.data
    return res.data
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
  }

  return { token, userInfo, login, getUserInfo, logout }
})
