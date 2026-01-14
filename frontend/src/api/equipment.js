import request from '@/utils/request'

export function pageEquipments(params) {
  return request.get('/equipment/list', { params })
}

export function getEquipment(id) {
  return request.get(`/equipment/${id}`)
}

export function createEquipment(data) {
  return request.post('/equipment', data)
}

export function updateEquipment(id, data) {
  return request.put(`/equipment/${id}`, data)
}

export function deleteEquipment(id) {
  return request.delete(`/equipment/${id}`)
}

export function updateEquipmentStatus(id, status) {
  return request.put(`/equipment/${id}/status`, null, { params: { status } })
}

export function generateQrCode(id) {
  return request.get(`/equipment/${id}/qrcode`, { responseType: 'blob' })
}

export function getCategories() {
  return request.get('/equipment/categories')
}