package com.equipment.management.equipment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 设备实体
 */
@Data
@TableName("equipment_base")
public class Equipment implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String code;

    private String name;

    private String model;

    private Long categoryId;

    private Integer status;

    private String location;

    private String department;

    private LocalDate purchaseDate;

    private String manufacturer;

    private String specification;

    private String qrCode;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    /**
     * 分类名称（非数据库字段）
     */
    @TableField(exist = false)
    private String categoryName;
}
