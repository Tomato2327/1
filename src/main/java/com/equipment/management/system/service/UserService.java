package com.equipment.management.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.equipment.management.system.entity.User;

import java.util.List;
import java.util.Set;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查询用户
     */
    User getByUsername(String username);

    /**
     * 查询用户权限编码列表
     */
    List<String> getPermissionCodes(Long userId);

    /**
     * 查询用户权限编码集合（用于权限校验）
     */
    Set<String> getUserPermissions(Long userId);

    /**
     * 创建用户
     */
    boolean createUser(User user, Long roleId);

    /**
     * 更新用户
     */
    boolean updateUser(User user, Long roleId);
}
