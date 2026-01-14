package com.equipment.management.equipment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.equipment.management.equipment.dto.EquipmentQueryDTO;
import com.equipment.management.equipment.entity.Equipment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 设备Mapper
 */
@Mapper
public interface EquipmentMapper extends BaseMapper<Equipment> {

    /**
     * 分页查询设备（包含分类名称）
     */
    @Select("<script>" +
            "SELECT e.*, c.name as category_name " +
            "FROM equipment_base e " +
            "LEFT JOIN equipment_category c ON e.category_id = c.id " +
            "WHERE e.deleted = 0 " +
            "<if test='query.keyword != null and query.keyword != \"\"'>" +
            "AND (e.name LIKE CONCAT('%', #{query.keyword}, '%') " +
            "OR e.code LIKE CONCAT('%', #{query.keyword}, '%')) " +
            "</if>" +
            "<if test='query.categoryId != null'>" +
            "AND e.category_id = #{query.categoryId} " +
            "</if>" +
            "<if test='query.status != null'>" +
            "AND e.status = #{query.status} " +
            "</if>" +
            "ORDER BY e.create_time DESC" +
            "</script>")
    IPage<Equipment> selectPageWithCategory(Page<Equipment> page, @Param("query") EquipmentQueryDTO query);

    /**
     * 根据ID查询设备（包含分类名称）
     */
    @Select("SELECT e.*, c.name as category_name " +
            "FROM equipment_base e " +
            "LEFT JOIN equipment_category c ON e.category_id = c.id " +
            "WHERE e.id = #{id} AND e.deleted = 0")
    Equipment selectByIdWithCategory(@Param("id") Long id);

    /**
     * 按设备类型统计故障数量
     */
    @Select("SELECT c.name as categoryName, COUNT(mo.id) as faultCount " +
            "FROM equipment_category c " +
            "LEFT JOIN equipment_base e ON c.id = e.category_id AND e.deleted = 0 " +
            "LEFT JOIN maintenance_order mo ON e.id = mo.equipment_id " +
            "WHERE c.deleted = 0 " +
            "GROUP BY c.id, c.name")
    List<Map<String, Object>> countFaultByCategory();
}
