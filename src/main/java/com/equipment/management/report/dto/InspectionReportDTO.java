package com.equipment.management.report.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

/**
 * 点检报表导出DTO
 */
@Data
public class InspectionReportDTO {

    @ExcelProperty("设备编号")
    @ColumnWidth(15)
    private String equipmentCode;

    @ExcelProperty("设备名称")
    @ColumnWidth(20)
    private String equipmentName;

    @ExcelProperty("点检项目")
    @ColumnWidth(20)
    private String itemName;

    @ExcelProperty("点检结果")
    @ColumnWidth(10)
    private String resultName;

    @ExcelProperty("检测值")
    @ColumnWidth(15)
    private String resultValue;

    @ExcelProperty("点检人")
    @ColumnWidth(12)
    private String inspectorName;

    @ExcelProperty("点检时间")
    @ColumnWidth(18)
    private String checkTime;

    @ExcelProperty("备注")
    @ColumnWidth(25)
    private String remark;
}
