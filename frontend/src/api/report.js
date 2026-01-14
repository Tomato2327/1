import request from '@/utils/request'

export function getDashboard() {
  return request.get('/report/dashboard')
}

export function getEquipmentStats() {
  return request.get('/report/equipment/stats')
}

export function getInspectionStats(startDate, endDate) {
  return request.get('/report/inspection/stats', { params: { startDate, endDate } })
}

export function getInspectionTrend(startDate, endDate) {
  return request.get('/report/inspection/trend', { params: { startDate, endDate } })
}

export function getMaintenanceResponseStats(startDate, endDate) {
  return request.get('/report/maintenance/response', { params: { startDate, endDate } })
}

export function getMaintenanceCostStats(startDate, endDate) {
  return request.get('/report/maintenance/cost', { params: { startDate, endDate } })
}