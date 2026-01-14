package com.equipment.management.inspection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.equipment.management.inspection.entity.InspectionRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 点检记录Mapper
 */
@Mapper
public interface InspectionRecordMapper extends BaseMapper<InspectionRecord> {

    @Select("<script>" +
            "SELECT r.*, e.name as equipment_name " +
            "FROM inspection_record r " +
            "LEFT JOIN equipment_base e ON r.equipment_id = e.id " +
            "WHERE 1=1 " +
            "<if test='equipmentId != null'>" +
            "AND r.equipment_id = #{equipmentId} " +
            "</if>" +
            "<if test='result != null'>" +
            "AND r.result = #{result} " +
            "</if>" +
            "ORDER BY r.check_time DESC" +
            "</script>")
    IPage<InspectionRecord> selectPageWithEquipment(Page<InspectionRecord> page, 
            @Param("equipmentId") Long equipmentId, @Param("result") Integer result);
}
