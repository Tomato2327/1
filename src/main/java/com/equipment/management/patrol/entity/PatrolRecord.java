package com.equipment.management.patrol.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 巡检记录实体
 */
@Data
@TableName("patrol_record")
public class PatrolRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long taskId;

    private String checkpointId;

    private String checkpointName;

    private Integer status; // 1正常,2异常

    private String remark;

    private String images;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private LocalDateTime checkTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
