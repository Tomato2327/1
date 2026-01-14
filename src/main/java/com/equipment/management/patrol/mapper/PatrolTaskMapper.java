package com.equipment.management.patrol.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.equipment.management.patrol.entity.PatrolTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface PatrolTaskMapper extends BaseMapper<PatrolTask> {

    @Select("<script>" +
            "SELECT pt.*, pr.name as route_name, su.real_name as assignee_name " +
            "FROM patrol_task pt " +
            "LEFT JOIN patrol_route pr ON pt.route_id = pr.id " +
            "LEFT JOIN sys_user su ON pt.assignee_id = su.id " +
            "WHERE 1=1 " +
            "<if test='status != null'>AND pt.status = #{status}</if> " +
            "<if test='assigneeId != null'>AND pt.assignee_id = #{assigneeId}</if> " +
            "<if test='taskDate != null'>AND pt.task_date = #{taskDate}</if> " +
            "ORDER BY pt.task_date DESC, pt.create_time DESC" +
            "</script>")
    IPage<PatrolTask> selectPageWithDetail(Page<PatrolTask> page,
                                            @Param("status") Integer status,
                                            @Param("assigneeId") Long assigneeId,
                                            @Param("taskDate") LocalDate taskDate);

    @Select("SELECT pt.*, pr.name as route_name FROM patrol_task pt " +
            "LEFT JOIN patrol_route pr ON pt.route_id = pr.id WHERE pt.id = #{id}")
    PatrolTask selectDetailById(@Param("id") Long id);
}
