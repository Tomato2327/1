package com.equipment.management.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.equipment.management.system.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用户Mapper
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据用户名查询用户（包含角色信息）
     */
    @Select("SELECT u.*, r.code as role_code, r.name as role_name " +
            "FROM sys_user u " +
            "LEFT JOIN sys_user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN sys_role r ON ur.role_id = r.id " +
            "WHERE u.username = #{username} AND u.deleted = 0")
    User selectByUsername(@Param("username") String username);

    /**
     * 分页查询用户（包含角色信息）
     */
    @Select("<script>" +
            "SELECT u.*, r.id as role_id, r.code as role_code, r.name as role_name " +
            "FROM sys_user u " +
            "LEFT JOIN sys_user_role ur ON u.id = ur.user_id " +
            "LEFT JOIN sys_role r ON ur.role_id = r.id " +
            "WHERE u.deleted = 0 " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (u.username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.real_name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.phone LIKE CONCAT('%', #{keyword}, '%')) " +
            "</if>" +
            "<if test='roleCode != null and roleCode != \"\"'>" +
            "AND r.code = #{roleCode} " +
            "</if>" +
            "ORDER BY u.create_time DESC" +
            "</script>")
    IPage<User> selectPageWithRole(Page<User> page, @Param("keyword") String keyword, @Param("roleCode") String roleCode);

    /**
     * 查询用户的权限编码列表
     */
    @Select("SELECT DISTINCT p.code FROM sys_permission p " +
            "INNER JOIN sys_role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN sys_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.deleted = 0")
    List<String> selectPermissionCodesByUserId(@Param("userId") Long userId);

    /**
     * 根据角色编码查询用户列表
     */
    @Select("SELECT u.*, r.code as role_code, r.name as role_name " +
            "FROM sys_user u " +
            "INNER JOIN sys_user_role ur ON u.id = ur.user_id " +
            "INNER JOIN sys_role r ON ur.role_id = r.id " +
            "WHERE u.deleted = 0 AND u.status = 1 AND r.code = #{roleCode}")
    List<User> selectByRoleCode(@Param("roleCode") String roleCode);
}
