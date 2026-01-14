package com.equipment.management.system.controller;

import com.equipment.management.common.exception.BusinessException;
import com.equipment.management.common.result.R;
import com.equipment.management.common.security.LoginUser;
import com.equipment.management.common.security.SecurityUtils;
import com.equipment.management.common.utils.JwtUtil;
import com.equipment.management.system.dto.LoginRequest;
import com.equipment.management.system.dto.LoginResponse;
import com.equipment.management.system.entity.User;
import com.equipment.management.system.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认证控制器
 */
@Api(tags = "认证管理")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public R<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        // 查询用户
        User user = userService.getByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 检查状态
        if (user.getStatus() != 1) {
            throw new BusinessException(401, "账号已被禁用");
        }

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        // 构建响应
        LoginResponse response = LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .roleCode(user.getRoleCode())
                .roleName(user.getRoleName())
                .build();

        return R.ok(response);
    }

    @ApiOperation("获取当前用户信息")
    @GetMapping("/info")
    public R<LoginUser> getUserInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new BusinessException(401, "未登录");
        }

        // 查询用户详细信息
        User user = userService.getByUsername(loginUser.getUsername());
        if (user != null) {
            loginUser.setRealName(user.getRealName());
            loginUser.setRoleCode(user.getRoleCode());
            loginUser.setRoleName(user.getRoleName());
        }

        // 查询权限
        List<String> permissions = userService.getPermissionCodes(loginUser.getUserId());
        loginUser.setPermissions(permissions);

        return R.ok(loginUser);
    }

    @ApiOperation("用户登出")
    @PostMapping("/logout")
    public R<Void> logout() {
        // JWT是无状态的，客户端删除Token即可
        return R.ok();
    }

    @ApiOperation("生成密码哈希（测试用）")
    @GetMapping("/encode")
    public R<String> encodePassword(@RequestParam String password) {
        return R.ok(passwordEncoder.encode(password));
    }
}
