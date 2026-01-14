import request from '@/utils/request'

export interface MaintenanceOrder {
  id?: number
  orderNo?: string
  equipmentId: number
  equipmentName?: string
  faultType?: number
  faultDesc?: string
  status?: number
  priority?: number
  reporterId?: number
  reporterName?: string
  assigneeId?: number
  assigneeName?: string
  repairDesc?: string
  laborCost?: number
  partCost?: number
  totalCost?: number
  createTime?: string
}

export interface SparePart {
  id?: number
  code: string
  name: string
  model?: string
  unit?: string
  stockQty: number
  safeQty: number
  price?: number
  location?: string
  lowStock?: boolean
}

export function pageOrders(params: any) {
  return request.get('/maintenance/orders', { params })
}

export function getOrder(id: number) {
  return request.get(`/maintenance/orders/${id}`)
}

export function createOrder(data: MaintenanceOrder) {
  return request.post('/maintenance/orders', data)
}

export function assignOrder(id: number, assigneeId: number, assigneeName: string) {
  return request.post(`/maintenance/orders/${id}/assign`, null, { params: { assigneeId, assigneeName } })
}

export function acceptOrder(id: number) {
  return request.post(`/maintenance/orders/${id}/accept`)
}

export function finishOrder(id: number, repairDesc: string, laborCost: number) {
  return request.post(`/maintenance/orders/${id}/finish`, null, { params: { repairDesc, laborCost } })
}

export function checkOrder(id: number, checkerId: number, passed: boolean) {
  return request.post(`/maintenance/orders/${id}/check`, null, { params: { checkerId, passed } })
}

export function pageParts(params: any) {
  return request.get('/maintenance/parts', { params })
}

export function createPart(data: SparePart) {
  return request.post('/maintenance/parts', data)
}

export function updatePart(id: number, data: SparePart) {
  return request.put(`/maintenance/parts/${id}`, data)
}

export function deletePart(id: number) {
  return request.delete(`/maintenance/parts/${id}`)
}

export function stockIn(id: number, quantity: number) {
  return request.post(`/maintenance/parts/${id}/stock-in`, null, { params: { quantity } })
}

export function stockOut(id: number, quantity: number) {
  return request.post(`/maintenance/parts/${id}/stock-out`, null, { params: { quantity } })
}

export function getLowStockParts() {
  return request.get('/maintenance/parts/low-stock')
}
