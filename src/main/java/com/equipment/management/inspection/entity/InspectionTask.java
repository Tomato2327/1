package com.equipment.management.inspection.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 点检任务实体
 */
@Data
@TableName("inspection_task")
public class InspectionTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long planId;

    private Long equipmentId;

    private Long assigneeId;

    private LocalDate taskDate;

    private Integer status; // 0待执行,1已完成,2已过期

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String equipmentName;

    @TableField(exist = false)
    private String assigneeName;

    @TableField(exist = false)
    private String planName;
}
