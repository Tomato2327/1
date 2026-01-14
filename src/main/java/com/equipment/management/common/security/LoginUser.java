package com.equipment.management.common.security;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 登录用户信息
 */
@Data
public class LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private String username;

    private String realName;

    private String roleCode;

    private String roleName;

    private List<String> permissions;
}
