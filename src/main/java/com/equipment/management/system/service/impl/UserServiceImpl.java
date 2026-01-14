package com.equipment.management.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.equipment.management.system.entity.User;
import com.equipment.management.system.mapper.UserMapper;
import com.equipment.management.system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User getByUsername(String username) {
        return baseMapper.selectByUsername(username);
    }

    @Override
    public List<String> getPermissionCodes(Long userId) {
        return baseMapper.selectPermissionCodesByUserId(userId);
    }

    @Override
    public Set<String> getUserPermissions(Long userId) {
        List<String> permissions = getPermissionCodes(userId);
        return new HashSet<>(permissions);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUser(User user, Long roleId) {
        // 密码已在Controller中加密，这里不再加密
        user.setStatus(1);
        boolean saved = save(user);
        
        if (saved && roleId != null) {
            // 关联角色
            jdbcTemplate.update(
                "INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)",
                user.getId(), roleId
            );
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(User user, Long roleId) {
        // 密码已在Controller中处理，这里直接更新
        boolean updated = updateById(user);
        
        if (updated && roleId != null) {
            // 更新角色关联
            jdbcTemplate.update("DELETE FROM sys_user_role WHERE user_id = ?", user.getId());
            jdbcTemplate.update(
                "INSERT INTO sys_user_role (user_id, role_id) VALUES (?, ?)",
                user.getId(), roleId
            );
        }
        return updated;
    }
}
