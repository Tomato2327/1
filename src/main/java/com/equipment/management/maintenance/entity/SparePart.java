package com.equipment.management.maintenance.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 备品备件实体
 */
@Data
@TableName("spare_part")
public class SparePart implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    private String name;

    private String model;

    private String unit;

    private Integer stockQty;

    private Integer safeQty;

    private BigDecimal price;

    private String location;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    /**
     * 是否低库存（非数据库字段）
     */
    @TableField(exist = false)
    private Boolean lowStock;
}
