package com.equipment.management.common.security;

import java.lang.annotation.*;

/**
 * 权限校验注解
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    
    /**
     * 需要的权限编码
     */
    String[] value() default {};
    
    /**
     * 逻辑关系：AND-需要全部权限，OR-需要任一权限
     */
    Logical logical() default Logical.OR;
    
    enum Logical {
        AND, OR
    }
}
