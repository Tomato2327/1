/**
 * 权限工具函数
 */

// 角色权限映射
const rolePermissions = {
  ADMIN: ['*'], // 管理员拥有所有权限
  EQUIPMENT_ADMIN: ['equipment:view', 'equipment:add', 'equipment:edit', 'equipment:delete'],
  INSPECTOR: ['inspection:view', 'inspection:record'],
  MAINTAINER: ['maintenance:view', 'maintenance:accept', 'maintenance:finish', 'maintenance:report'],
  PATROLLER: ['patrol:view', 'patrol:start', 'patrol:finish', 'patrol:checkin']
}

/**
 * 获取当前用户角色
 */
export function getUserRole() {
  const userInfoStr = localStorage.getItem('userInfo')
  if (userInfoStr) {
    const userInfo = JSON.parse(userInfoStr)
    return userInfo.roleCode
  }
  return null
}

/**
 * 检查是否是管理员
 */
export function isAdmin() {
  return getUserRole() === 'ADMIN'
}

/**
 * 检查是否有指定权限
 * @param {string} permission 权限标识
 */
export function hasPermission(permission) {
  const roleCode = getUserRole()
  if (!roleCode) return false
  
  // 管理员拥有所有权限
  if (roleCode === 'ADMIN') return true
  
  const permissions = rolePermissions[roleCode] || []
  return permissions.includes(permission) || permissions.includes('*')
}

/**
 * 检查是否有任一权限
 * @param {string[]} permissions 权限标识数组
 */
export function hasAnyPermission(permissions) {
  return permissions.some(p => hasPermission(p))
}

/**
 * 检查是否是指定角色
 * @param {string|string[]} roles 角色或角色数组
 */
export function hasRole(roles) {
  const roleCode = getUserRole()
  if (!roleCode) return false
  
  if (Array.isArray(roles)) {
    return roles.includes(roleCode)
  }
  return roleCode === roles
}
