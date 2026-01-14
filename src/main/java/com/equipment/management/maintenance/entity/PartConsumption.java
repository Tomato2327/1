package com.equipment.management.maintenance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 备件消耗记录实体
 */
@Data
@TableName("part_consumption")
public class PartConsumption implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private Long partId;

    private String partName;

    private Integer quantity;

    private BigDecimal unitPrice;

    private BigDecimal totalPrice;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
