package com.equipment.management.maintenance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.equipment.management.maintenance.entity.MaintenanceOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface MaintenanceOrderMapper extends BaseMapper<MaintenanceOrder> {

    @Select("SELECT mo.*, eb.name as equipment_name FROM maintenance_order mo " +
            "LEFT JOIN equipment_base eb ON mo.equipment_id = eb.id " +
            "WHERE mo.id = #{id}")
    MaintenanceOrder selectDetailById(@Param("id") Long id);

    @Select("<script>" +
            "SELECT mo.*, eb.name as equipment_name FROM maintenance_order mo " +
            "LEFT JOIN equipment_base eb ON mo.equipment_id = eb.id " +
            "WHERE 1=1 " +
            "<if test='status != null'>AND mo.status = #{status}</if> " +
            "<if test='equipmentId != null'>AND mo.equipment_id = #{equipmentId}</if> " +
            "<if test='assigneeId != null'>AND mo.assignee_id = #{assigneeId}</if> " +
            "<if test='keyword != null and keyword != \"\"'>" +
            "AND (mo.order_no LIKE CONCAT('%',#{keyword},'%') OR eb.name LIKE CONCAT('%',#{keyword},'%'))" +
            "</if> " +
            "ORDER BY mo.create_time DESC" +
            "</script>")
    IPage<MaintenanceOrder> selectPageWithEquipment(Page<MaintenanceOrder> page,
                                                     @Param("status") Integer status,
                                                     @Param("equipmentId") Long equipmentId,
                                                     @Param("assigneeId") Long assigneeId,
                                                     @Param("keyword") String keyword);

    @Select("SELECT COALESCE(SUM(pc.total_price), 0) FROM part_consumption pc WHERE pc.order_id = #{orderId}")
    BigDecimal selectPartCostByOrderId(@Param("orderId") Long orderId);

    @Select("SELECT COUNT(*) FROM maintenance_order WHERE status = #{status}")
    int countByStatus(@Param("status") Integer status);

    @Select("SELECT AVG(TIMESTAMPDIFF(MINUTE, create_time, accept_time)) FROM maintenance_order " +
            "WHERE accept_time IS NOT NULL AND create_time >= #{startTime}")
    Double avgResponseTime(@Param("startTime") LocalDateTime startTime);

    @Select("SELECT status, COUNT(*) as count FROM maintenance_order GROUP BY status")
    List<Map<String, Object>> countGroupByStatus();

    @Select("SELECT COUNT(*) FROM maintenance_order WHERE equipment_id = #{equipmentId}")
    Integer countByEquipmentId(@Param("equipmentId") Long equipmentId);
}
