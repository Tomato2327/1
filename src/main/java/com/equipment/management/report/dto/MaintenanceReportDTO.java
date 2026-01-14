package com.equipment.management.report.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 维修报表导出DTO
 */
@Data
public class MaintenanceReportDTO {

    @ExcelProperty("工单编号")
    @ColumnWidth(18)
    private String orderNo;

    @ExcelProperty("设备名称")
    @ColumnWidth(20)
    private String equipmentName;

    @ExcelProperty("故障描述")
    @ColumnWidth(30)
    private String faultDesc;

    @ExcelProperty("维修人员")
    @ColumnWidth(12)
    private String assigneeName;

    @ExcelProperty("工单状态")
    @ColumnWidth(10)
    private String statusName;

    @ExcelProperty("响应时间(小时)")
    @ColumnWidth(15)
    private Double responseHours;

    @ExcelProperty("人工成本")
    @ColumnWidth(12)
    private BigDecimal laborCost;

    @ExcelProperty("备件成本")
    @ColumnWidth(12)
    private BigDecimal partCost;

    @ExcelProperty("总成本")
    @ColumnWidth(12)
    private BigDecimal totalCost;

    @ExcelProperty("创建时间")
    @ColumnWidth(18)
    private String createTime;
}
