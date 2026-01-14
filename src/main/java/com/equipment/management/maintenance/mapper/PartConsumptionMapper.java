package com.equipment.management.maintenance.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.equipment.management.maintenance.entity.PartConsumption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PartConsumptionMapper extends BaseMapper<PartConsumption> {

    @Select("SELECT * FROM part_consumption WHERE order_id = #{orderId}")
    List<PartConsumption> selectByOrderId(@Param("orderId") Long orderId);

    @Select("SELECT COALESCE(SUM(total_price), 0) FROM part_consumption WHERE order_id = #{orderId}")
    BigDecimal sumCostByOrderId(@Param("orderId") Long orderId);

    @Select("SELECT COALESCE(SUM(total_price), 0) FROM part_consumption pc " +
            "JOIN maintenance_order mo ON pc.order_id = mo.id " +
            "WHERE mo.create_time >= #{startTime} AND mo.create_time < #{endTime}")
    BigDecimal sumCostByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
