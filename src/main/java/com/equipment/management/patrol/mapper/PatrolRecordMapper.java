package com.equipment.management.patrol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.equipment.management.patrol.entity.PatrolRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PatrolRecordMapper extends BaseMapper<PatrolRecord> {

    @Select("SELECT * FROM patrol_record WHERE task_id = #{taskId} ORDER BY check_time")
    List<PatrolRecord> selectByTaskId(@Param("taskId") Long taskId);

    @Select("SELECT COUNT(*) FROM patrol_record WHERE task_id = #{taskId}")
    int countByTaskId(@Param("taskId") Long taskId);

    @Select("SELECT COUNT(*) FROM patrol_record WHERE task_id = #{taskId} AND status = 2")
    int countAbnormalByTaskId(@Param("taskId") Long taskId);
}
