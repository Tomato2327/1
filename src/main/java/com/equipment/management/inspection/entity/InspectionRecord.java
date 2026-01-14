package com.equipment.management.inspection.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 点检记录实体
 */
@Data
@TableName("inspection_record")
public class InspectionRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long taskId;

    private Long planId;

    private Long equipmentId;

    private Long standardId;

    private String itemName;

    private Integer result;

    private String resultValue;

    private String remark;

    private String images;

    private Long inspectorId;

    private String inspectorName;

    private LocalDateTime checkTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String equipmentName;
}
