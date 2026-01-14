package com.equipment.management.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.equipment.management.common.exception.BusinessException;
import com.equipment.management.common.result.R;
import com.equipment.management.common.security.RequireRole;
import com.equipment.management.system.entity.Role;
import com.equipment.management.system.entity.User;
import com.equipment.management.system.mapper.RoleMapper;
import com.equipment.management.system.mapper.UserMapper;
import com.equipment.management.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户管理控制器
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
@RequireRole({"ADMIN"})  // 只有管理员可以访问用户管理
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    @ApiOperation("分页查询用户")
    @GetMapping("/list")
    public R<IPage<User>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String roleCode) {
        Page<User> page = new Page<>(current, size);
        IPage<User> result = userMapper.selectPageWithRole(page, keyword, roleCode);
        // 隐藏密码
        result.getRecords().forEach(u -> u.setPassword(null));
        return R.ok(result);
    }

    @ApiOperation("获取用户详情")
    @GetMapping("/{id}")
    public R<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return R.ok(user);
    }

    @ApiOperation("创建用户")
    @PostMapping
    public R<Void> create(@RequestBody User user) {
        // 检查用户名是否存在
        if (userService.getByUsername(user.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.createUser(user, user.getRoleId());
        return R.ok();
    }

    @ApiOperation("更新用户")
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        // 如果密码为空，不更新密码
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(null);
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        userService.updateUser(user, user.getRoleId());
        return R.ok();
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        if (id == 1L) {
            throw new BusinessException("不能删除管理员账号");
        }
        userService.removeById(id);
        return R.ok();
    }

    @ApiOperation("重置密码")
    @PostMapping("/{id}/reset-password")
    public R<Void> resetPassword(@PathVariable Long id, @RequestParam String newPassword) {
        User user = userService.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateById(user);
        return R.ok();
    }

    @ApiOperation("获取角色列表")
    @GetMapping("/roles")
    public R<List<Role>> getRoles() {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getDeleted, 0).eq(Role::getStatus, 1);
        return R.ok(roleMapper.selectList(wrapper));
    }

    @ApiOperation("获取维修人员列表")
    @GetMapping("/maintainers")
    public R<List<User>> getMaintainers() {
        List<User> list = userMapper.selectByRoleCode("MAINTAINER");
        list.forEach(u -> u.setPassword(null));
        return R.ok(list);
    }

    @ApiOperation("获取点检员列表")
    @GetMapping("/inspectors")
    public R<List<User>> getInspectors() {
        List<User> list = userMapper.selectByRoleCode("INSPECTOR");
        list.forEach(u -> u.setPassword(null));
        return R.ok(list);
    }

    @ApiOperation("获取巡检员列表")
    @GetMapping("/patrollers")
    public R<List<User>> getPatrollers() {
        List<User> list = userMapper.selectByRoleCode("PATROLLER");
        list.forEach(u -> u.setPassword(null));
        return R.ok(list);
    }

    @ApiOperation("获取所有启用用户列表")
    @GetMapping("/all")
    public R<List<User>> getAllUsers() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getDeleted, 0)
               .eq(User::getStatus, 1)
               .orderByAsc(User::getId);
        List<User> list = userService.list(wrapper);
        list.forEach(u -> u.setPassword(null));
        return R.ok(list);
    }
}
