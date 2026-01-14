import request from '@/utils/request'

export function pageUsers(params) {
  return request.get('/system/user/list', { params })
}

export function getUser(id) {
  return request.get(`/system/user/${id}`)
}

export function createUser(data) {
  return request.post('/system/user', data)
}

export function updateUser(id, data) {
  return request.put(`/system/user/${id}`, data)
}

export function deleteUser(id) {
  return request.delete(`/system/user/${id}`)
}

export function resetPassword(id, newPassword) {
  return request.post(`/system/user/${id}/reset-password`, null, { params: { newPassword } })
}

export function getRoles() {
  return request.get('/system/user/roles')
}

export function getMaintainers() {
  return request.get('/system/user/maintainers')
}

export function getInspectors() {
  return request.get('/system/user/inspectors')
}

export function getPatrollers() {
  return request.get('/system/user/patrollers')
}
