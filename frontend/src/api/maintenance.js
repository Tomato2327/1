import request from '@/utils/request'

export function pageOrders(params) {
  return request.get('/maintenance/orders', { params })
}

export function getOrder(id) {
  return request.get(`/maintenance/orders/${id}`)
}

export function createOrder(data) {
  return request.post('/maintenance/orders', data)
}

export function assignOrder(id, assigneeId, assigneeName) {
  return request.post(`/maintenance/orders/${id}/assign`, null, { params: { assigneeId, assigneeName } })
}

export function acceptOrder(id) {
  return request.post(`/maintenance/orders/${id}/accept`)
}

export function finishOrder(id, repairDesc, laborCost) {
  return request.post(`/maintenance/orders/${id}/finish`, null, { params: { repairDesc, laborCost } })
}

export function checkOrder(id, checkerId, passed) {
  return request.post(`/maintenance/orders/${id}/check`, null, { params: { checkerId, passed } })
}

export function pageParts(params) {
  return request.get('/maintenance/parts', { params })
}

export function createPart(data) {
  return request.post('/maintenance/parts', data)
}

export function updatePart(id, data) {
  return request.put(`/maintenance/parts/${id}`, data)
}

export function deletePart(id) {
  return request.delete(`/maintenance/parts/${id}`)
}

export function stockIn(id, quantity) {
  return request.post(`/maintenance/parts/${id}/stock-in`, null, { params: { quantity } })
}

export function stockOut(id, quantity) {
  return request.post(`/maintenance/parts/${id}/stock-out`, null, { params: { quantity } })
}

export function getLowStockParts() {
  return request.get('/maintenance/parts/low-stock')
}