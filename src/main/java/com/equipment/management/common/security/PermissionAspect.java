package com.equipment.management.common.security;

import com.equipment.management.common.exception.BusinessException;
import com.equipment.management.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 权限校验切面
 */
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final UserService userService;

    @Around("@annotation(com.equipment.management.common.security.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission annotation = method.getAnnotation(RequirePermission.class);
        
        if (annotation == null) {
            return joinPoint.proceed();
        }
        
        String[] requiredPermissions = annotation.value();
        if (requiredPermissions.length == 0) {
            return joinPoint.proceed();
        }
        
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException(401, "未登录");
        }
        
        Set<String> userPermissions = userService.getUserPermissions(loginUser.getUserId());
        
        boolean hasPermission;
        if (annotation.logical() == RequirePermission.Logical.AND) {
            hasPermission = hasAllPermissions(userPermissions, requiredPermissions);
        } else {
            hasPermission = hasAnyPermission(userPermissions, requiredPermissions);
        }
        
        if (!hasPermission) {
            throw new BusinessException(403, "无权限访问");
        }
        
        return joinPoint.proceed();
    }
    
    private boolean hasAllPermissions(Set<String> userPermissions, String[] required) {
        for (String perm : required) {
            if (!userPermissions.contains(perm)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean hasAnyPermission(Set<String> userPermissions, String[] required) {
        for (String perm : required) {
            if (userPermissions.contains(perm)) {
                return true;
            }
        }
        return false;
    }
}
