package com.equipment.management.report.controller;

import com.alibaba.excel.EasyExcel;
import com.equipment.management.common.result.R;
import com.equipment.management.report.dto.EquipmentReportDTO;
import com.equipment.management.report.dto.InspectionReportDTO;
import com.equipment.management.report.dto.MaintenanceReportDTO;
import com.equipment.management.report.service.ReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Api(tags = "报表统计")
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @ApiOperation("获取数据看板")
    @GetMapping("/dashboard")
    public R<Map<String, Object>> getDashboard() {
        return R.ok(reportService.getDashboard());
    }

    @ApiOperation("设备运行状态统计")
    @GetMapping("/equipment/stats")
    public R<Map<String, Object>> getEquipmentStats() {
        return R.ok(reportService.getEquipmentStats());
    }

    @ApiOperation("点检完成率统计")
    @GetMapping("/inspection/stats")
    public R<Map<String, Object>> getInspectionStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return R.ok(reportService.getInspectionStats(startDate, endDate));
    }

    @ApiOperation("点检趋势")
    @GetMapping("/inspection/trend")
    public R<List<Map<String, Object>>> getInspectionTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return R.ok(reportService.getInspectionTrend(startDate, endDate));
    }

    @ApiOperation("维修响应时间分析")
    @GetMapping("/maintenance/response")
    public R<Map<String, Object>> getMaintenanceResponseStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return R.ok(reportService.getMaintenanceResponseStats(startDate, endDate));
    }

    @ApiOperation("维修成本统计")
    @GetMapping("/maintenance/cost")
    public R<Map<String, Object>> getMaintenanceCostStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        return R.ok(reportService.getMaintenanceCostStats(startDate, endDate));
    }

    @ApiOperation("按设备类型统计故障")
    @GetMapping("/fault/by-category")
    public R<List<Map<String, Object>>> getFaultByCategory() {
        return R.ok(reportService.getFaultByCategory());
    }

    @ApiOperation("导出设备报表")
    @GetMapping("/export/equipment")
    public void exportEquipment(HttpServletResponse response) throws IOException {
        setExcelResponse(response, "设备报表");
        List<EquipmentReportDTO> data = reportService.getEquipmentReportData();
        EasyExcel.write(response.getOutputStream(), EquipmentReportDTO.class)
                .sheet("设备报表")
                .doWrite(data);
    }

    @ApiOperation("导出维修报表")
    @GetMapping("/export/maintenance")
    public void exportMaintenance(
            HttpServletResponse response,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) throws IOException {
        setExcelResponse(response, "维修报表");
        List<MaintenanceReportDTO> data = reportService.getMaintenanceReportData(startDate, endDate);
        EasyExcel.write(response.getOutputStream(), MaintenanceReportDTO.class)
                .sheet("维修报表")
                .doWrite(data);
    }

    @ApiOperation("导出点检报表")
    @GetMapping("/export/inspection")
    public void exportInspection(
            HttpServletResponse response,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) throws IOException {
        setExcelResponse(response, "点检报表");
        List<InspectionReportDTO> data = reportService.getInspectionReportData(startDate, endDate);
        EasyExcel.write(response.getOutputStream(), InspectionReportDTO.class)
                .sheet("点检报表")
                .doWrite(data);
    }

    private void setExcelResponse(HttpServletResponse response, String fileName) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                .replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFileName + ".xlsx");
    }
}
