import request from '@/utils/request'

export interface Equipment {
  id?: number
  code: string
  name: string
  model?: string
  categoryId?: number
  categoryName?: string
  status: number
  location?: string
  department?: string
  purchaseDate?: string
  manufacturer?: string
  specification?: string
  qrCode?: string
}

export interface EquipmentQuery {
  current: number
  size: number
  keyword?: string
  categoryId?: number
  status?: number
}

export function pageEquipments(params: EquipmentQuery) {
  return request.get('/equipment/list', { params })
}

export function getEquipment(id: number) {
  return request.get(`/equipment/${id}`)
}

export function createEquipment(data: Equipment) {
  return request.post('/equipment', data)
}

export function updateEquipment(id: number, data: Equipment) {
  return request.put(`/equipment/${id}`, data)
}

export function deleteEquipment(id: number) {
  return request.delete(`/equipment/${id}`)
}

export function updateEquipmentStatus(id: number, status: number) {
  return request.put(`/equipment/${id}/status`, null, { params: { status } })
}

export function generateQrCode(id: number) {
  return request.get(`/equipment/${id}/qrcode`, { responseType: 'blob' })
}

export function getCategories() {
  return request.get('/equipment/categories')
}
