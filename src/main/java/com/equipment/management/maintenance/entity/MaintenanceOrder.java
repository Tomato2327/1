package com.equipment.management.maintenance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 维修工单实体
 */
@Data
@TableName("maintenance_order")
public class MaintenanceOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long equipmentId;

    private Integer faultType;

    private String faultDesc;

    private Integer status;

    private Integer priority;

    private Long reporterId;

    private String reporterName;

    private Long assigneeId;

    private String assigneeName;

    private String repairDesc;

    private LocalDateTime acceptTime;

    private LocalDateTime startTime;

    private LocalDateTime finishTime;

    private LocalDateTime checkTime;

    private Long checkerId;

    private BigDecimal laborCost;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String equipmentName;

    @TableField(exist = false)
    private BigDecimal partCost;

    @TableField(exist = false)
    private BigDecimal totalCost;
}
