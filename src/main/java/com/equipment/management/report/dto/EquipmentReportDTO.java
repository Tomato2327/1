package com.equipment.management.report.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 设备报表导出DTO
 */
@Data
public class EquipmentReportDTO {

    @ExcelProperty("设备编号")
    @ColumnWidth(15)
    private String code;

    @ExcelProperty("设备名称")
    @ColumnWidth(20)
    private String name;

    @ExcelProperty("设备型号")
    @ColumnWidth(15)
    private String model;

    @ExcelProperty("所属部门")
    @ColumnWidth(15)
    private String department;

    @ExcelProperty("安装位置")
    @ColumnWidth(20)
    private String location;

    @ExcelProperty("设备状态")
    @ColumnWidth(10)
    private String statusName;

    @ExcelProperty("运行率(%)")
    @ColumnWidth(12)
    private Double runningRate;

    @ExcelProperty("故障次数")
    @ColumnWidth(10)
    private Integer faultCount;
}
