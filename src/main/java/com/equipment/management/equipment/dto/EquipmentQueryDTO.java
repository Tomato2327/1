package com.equipment.management.equipment.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 设备查询DTO
 */
@Data
@ApiModel("设备查询参数")
public class EquipmentQueryDTO {

    @ApiModelProperty("关键词（设备名称/编号）")
    private String keyword;

    @ApiModelProperty("分类ID")
    private Long categoryId;

    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("页码")
    private Integer current = 1;

    @ApiModelProperty("每页数量")
    private Integer size = 10;

    public Integer getPageNum() {
        return current != null ? current : 1;
    }

    public Integer getPageSize() {
        return size != null ? size : 10;
    }
}
