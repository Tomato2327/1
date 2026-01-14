package com.equipment.management.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String realName;

    private String phone;

    private String email;

    private String avatar;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    /**
     * 角色ID（非数据库字段，用于创建/更新时传递）
     */
    @TableField(exist = false)
    private Long roleId;

    /**
     * 角色编码（非数据库字段）
     */
    @TableField(exist = false)
    private String roleCode;

    /**
     * 角色名称（非数据库字段）
     */
    @TableField(exist = false)
    private String roleName;
}
