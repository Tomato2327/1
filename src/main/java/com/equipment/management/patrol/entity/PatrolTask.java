package com.equipment.management.patrol.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 巡检任务实体
 */
@Data
@TableName("patrol_task")
public class PatrolTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long routeId;

    private Long assigneeId;

    private LocalDate taskDate;

    private Integer status; // 0待执行,1执行中,2已完成,3已过期

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String routeName;

    @TableField(exist = false)
    private String assigneeName;

    @TableField(exist = false)
    private Integer checkpointCount;

    @TableField(exist = false)
    private Integer completedCount;
}
