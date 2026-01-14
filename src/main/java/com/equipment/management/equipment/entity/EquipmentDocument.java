package com.equipment.management.equipment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 设备档案实体
 */
@Data
@TableName("equipment_document")
public class EquipmentDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long equipmentId;

    private String name;

    private String filePath;

    private String fileType;

    private Long fileSize;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
