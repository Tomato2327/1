package com.equipment.management.inspection.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.equipment.management.inspection.entity.InspectionTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface InspectionTaskMapper extends BaseMapper<InspectionTask> {

    @Select("<script>" +
            "SELECT it.*, eb.name as equipment_name, su.real_name as assignee_name, ip.name as plan_name " +
            "FROM inspection_task it " +
            "LEFT JOIN equipment_base eb ON it.equipment_id = eb.id " +
            "LEFT JOIN sys_user su ON it.assignee_id = su.id " +
            "LEFT JOIN inspection_plan ip ON it.plan_id = ip.id " +
            "WHERE 1=1 " +
            "<if test='status != null'>AND it.status = #{status}</if> " +
            "<if test='equipmentId != null'>AND it.equipment_id = #{equipmentId}</if> " +
            "<if test='taskDate != null'>AND it.task_date = #{taskDate}</if> " +
            "ORDER BY it.task_date DESC, it.create_time DESC" +
            "</script>")
    IPage<InspectionTask> selectPageWithDetail(Page<InspectionTask> page,
                                                @Param("status") Integer status,
                                                @Param("equipmentId") Long equipmentId,
                                                @Param("taskDate") LocalDate taskDate);
}
