package com.equipment.management.inspection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.equipment.management.inspection.entity.InspectionStandard;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 点检标准Mapper
 */
@Mapper
public interface InspectionStandardMapper extends BaseMapper<InspectionStandard> {

    @Select("SELECT s.*, e.name as equipment_name " +
            "FROM inspection_standard s " +
            "LEFT JOIN equipment_base e ON s.equipment_id = e.id " +
            "WHERE s.equipment_id = #{equipmentId} AND s.deleted = 0 " +
            "ORDER BY s.sort_order")
    List<InspectionStandard> selectByEquipmentId(@Param("equipmentId") Long equipmentId);
}
