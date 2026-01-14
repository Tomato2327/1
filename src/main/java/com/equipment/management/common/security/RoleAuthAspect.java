package com.equipment.management.common.security;

import com.equipment.management.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 角色权限切面
 */
@Aspect
@Component
@RequiredArgsConstructor
public class RoleAuthAspect {

    @Around("@annotation(com.equipment.management.common.security.RequireRole) || @within(com.equipment.management.common.security.RequireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException(401, "未登录");
        }

        // 获取注解
        RequireRole requireRole = getRequireRole(joinPoint);
        if (requireRole == null) {
            return joinPoint.proceed();
        }

        // 检查角色
        String userRole = loginUser.getRoleCode();
        String[] allowedRoles = requireRole.value();
        
        // ADMIN 角色拥有所有权限
        if ("ADMIN".equals(userRole)) {
            return joinPoint.proceed();
        }

        // 检查是否有权限
        boolean hasRole = Arrays.asList(allowedRoles).contains(userRole);
        if (!hasRole) {
            throw new BusinessException(403, "没有操作权限");
        }

        return joinPoint.proceed();
    }

    private RequireRole getRequireRole(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 先检查方法上的注解
        RequireRole annotation = method.getAnnotation(RequireRole.class);
        if (annotation != null) {
            return annotation;
        }
        
        // 再检查类上的注解
        return joinPoint.getTarget().getClass().getAnnotation(RequireRole.class);
    }
}
