package com.equipment.management.report.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.equipment.management.equipment.entity.Equipment;
import com.equipment.management.equipment.mapper.EquipmentMapper;
import com.equipment.management.inspection.entity.InspectionRecord;
import com.equipment.management.inspection.mapper.InspectionRecordMapper;
import com.equipment.management.maintenance.entity.MaintenanceOrder;
import com.equipment.management.maintenance.mapper.MaintenanceOrderMapper;
import com.equipment.management.maintenance.mapper.PartConsumptionMapper;
import com.equipment.management.report.dto.EquipmentReportDTO;
import com.equipment.management.report.dto.InspectionReportDTO;
import com.equipment.management.report.dto.MaintenanceReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final EquipmentMapper equipmentMapper;
    private final InspectionRecordMapper inspectionRecordMapper;
    private final MaintenanceOrderMapper maintenanceOrderMapper;
    private final PartConsumptionMapper partConsumptionMapper;

    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String[] STATUS_NAMES = {"", "运行中", "停机", "维修中", "已报废"};
    private static final String[] ORDER_STATUS_NAMES = {"", "待派发", "待接单", "维修中", "待验收", "已完成"};
    private static final String[] RESULT_NAMES = {"", "正常", "异常"};

    /**
     * 设备运行状态统计
     */
    public Map<String, Object> getEquipmentStats() {
        LambdaQueryWrapper<Equipment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Equipment::getDeleted, 0);
        List<Equipment> equipments = equipmentMapper.selectList(wrapper);

        int total = equipments.size();
        int running = 0, stopped = 0, maintenance = 0, scrapped = 0;

        for (Equipment e : equipments) {
            switch (e.getStatus()) {
                case 1: running++; break;
                case 2: stopped++; break;
                case 3: maintenance++; break;
                case 4: scrapped++; break;
            }
        }

        BigDecimal runningRate = total > 0 
            ? BigDecimal.valueOf(running).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;

        BigDecimal faultRate = total > 0 
            ? BigDecimal.valueOf(maintenance).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("running", running);
        result.put("stopped", stopped);
        result.put("maintenance", maintenance);
        result.put("scrapped", scrapped);
        result.put("runningRate", runningRate);
        result.put("faultRate", faultRate);
        return result;
    }

    /**
     * 点检完成率统计
     */
    public Map<String, Object> getInspectionStats(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);

        LambdaQueryWrapper<InspectionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(InspectionRecord::getCheckTime, startTime)
               .le(InspectionRecord::getCheckTime, endTime);
        List<InspectionRecord> records = inspectionRecordMapper.selectList(wrapper);

        int total = records.size();
        int normal = 0, abnormal = 0;

        for (InspectionRecord r : records) {
            if (r.getResult() != null && r.getResult() == 1) {
                normal++;
            } else if (r.getResult() != null && r.getResult() == 2) {
                abnormal++;
            }
        }

        BigDecimal normalRate = total > 0 
            ? BigDecimal.valueOf(normal).divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
            : BigDecimal.ZERO;

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("normal", normal);
        result.put("abnormal", abnormal);
        result.put("normalRate", normalRate);
        return result;
    }

    /**
     * 维修响应时间分析
     */
    public Map<String, Object> getMaintenanceResponseStats(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);

        LambdaQueryWrapper<MaintenanceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(MaintenanceOrder::getCreateTime, startTime)
               .le(MaintenanceOrder::getCreateTime, endTime)
               .isNotNull(MaintenanceOrder::getAcceptTime);
        List<MaintenanceOrder> orders = maintenanceOrderMapper.selectList(wrapper);

        long totalMinutes = 0;
        int count = 0;

        for (MaintenanceOrder o : orders) {
            if (o.getAcceptTime() != null && o.getCreateTime() != null) {
                long minutes = Duration.between(o.getCreateTime(), o.getAcceptTime()).toMinutes();
                totalMinutes += minutes;
                count++;
            }
        }

        double avgResponseMinutes = count > 0 ? (double) totalMinutes / count : 0;

        Map<String, Object> result = new HashMap<>();
        result.put("totalOrders", orders.size());
        result.put("avgResponseMinutes", avgResponseMinutes);
        result.put("avgResponseHours", avgResponseMinutes / 60);
        return result;
    }

    /**
     * 维修成本统计
     */
    public Map<String, Object> getMaintenanceCostStats(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);

        LambdaQueryWrapper<MaintenanceOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(MaintenanceOrder::getCreateTime, startTime)
               .le(MaintenanceOrder::getCreateTime, endTime);
        List<MaintenanceOrder> orders = maintenanceOrderMapper.selectList(wrapper);

        BigDecimal totalLaborCost = BigDecimal.ZERO;
        BigDecimal totalPartCost = BigDecimal.ZERO;

        for (MaintenanceOrder o : orders) {
            if (o.getLaborCost() != null) {
                totalLaborCost = totalLaborCost.add(o.getLaborCost());
            }
            BigDecimal partCost = maintenanceOrderMapper.selectPartCostByOrderId(o.getId());
            totalPartCost = totalPartCost.add(partCost);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("orderCount", orders.size());
        result.put("totalLaborCost", totalLaborCost);
        result.put("totalPartCost", totalPartCost);
        result.put("totalCost", totalLaborCost.add(totalPartCost));
        return result;
    }

    /**
     * 获取数据看板汇总
     */
    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("equipment", getEquipmentStats());
        LocalDate today = LocalDate.now();
        dashboard.put("todayInspection", getInspectionStats(today, today));
        LocalDate monthStart = today.withDayOfMonth(1);
        dashboard.put("monthMaintenance", getMaintenanceCostStats(monthStart, today));
        dashboard.put("orderStats", maintenanceOrderMapper.countGroupByStatus());
        return dashboard;
    }

    /**
     * 按日期获取点检趋势
     */
    public List<Map<String, Object>> getInspectionTrend(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            Map<String, Object> dayStats = getInspectionStats(current, current);
            dayStats.put("date", current.toString());
            trend.add(dayStats);
            current = current.plusDays(1);
        }
        return trend;
    }

    /**
     * 按设备类型统计故障
     */
    public List<Map<String, Object>> getFaultByCategory() {
        return equipmentMapper.countFaultByCategory();
    }

    /**
     * 获取设备报表导出数据
     */
    public List<EquipmentReportDTO> getEquipmentReportData() {
        List<Equipment> equipments = equipmentMapper.selectList(
            new LambdaQueryWrapper<Equipment>().eq(Equipment::getDeleted, 0)
        );
        return equipments.stream().map(e -> {
            EquipmentReportDTO dto = new EquipmentReportDTO();
            dto.setCode(e.getCode());
            dto.setName(e.getName());
            dto.setModel(e.getModel());
            dto.setDepartment(e.getDepartment());
            dto.setLocation(e.getLocation());
            dto.setStatusName(e.getStatus() != null && e.getStatus() < STATUS_NAMES.length 
                ? STATUS_NAMES[e.getStatus()] : "未知");
            Integer faultCount = maintenanceOrderMapper.countByEquipmentId(e.getId());
            dto.setFaultCount(faultCount != null ? faultCount : 0);
            dto.setRunningRate(e.getStatus() == 1 ? 100.0 : 0.0);
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 获取维修报表导出数据
     */
    public List<MaintenanceReportDTO> getMaintenanceReportData(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        List<MaintenanceOrder> orders = maintenanceOrderMapper.selectList(
            new LambdaQueryWrapper<MaintenanceOrder>()
                .ge(MaintenanceOrder::getCreateTime, startTime)
                .le(MaintenanceOrder::getCreateTime, endTime)
                .orderByDesc(MaintenanceOrder::getCreateTime)
        );
        return orders.stream().map(o -> {
            MaintenanceReportDTO dto = new MaintenanceReportDTO();
            dto.setOrderNo(o.getOrderNo());
            dto.setEquipmentName(o.getEquipmentName());
            dto.setFaultDesc(o.getFaultDesc());
            dto.setAssigneeName(o.getAssigneeName());
            dto.setStatusName(o.getStatus() != null && o.getStatus() < ORDER_STATUS_NAMES.length 
                ? ORDER_STATUS_NAMES[o.getStatus()] : "未知");
            if (o.getAcceptTime() != null && o.getCreateTime() != null) {
                long minutes = Duration.between(o.getCreateTime(), o.getAcceptTime()).toMinutes();
                dto.setResponseHours(minutes / 60.0);
            }
            dto.setLaborCost(o.getLaborCost() != null ? o.getLaborCost() : BigDecimal.ZERO);
            BigDecimal partCost = maintenanceOrderMapper.selectPartCostByOrderId(o.getId());
            dto.setPartCost(partCost);
            dto.setTotalCost(dto.getLaborCost().add(partCost));
            dto.setCreateTime(o.getCreateTime() != null ? o.getCreateTime().format(DATE_TIME_FMT) : "");
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 获取点检报表导出数据
     */
    public List<InspectionReportDTO> getInspectionReportData(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.atTime(LocalTime.MAX);
        List<InspectionRecord> records = inspectionRecordMapper.selectList(
            new LambdaQueryWrapper<InspectionRecord>()
                .ge(InspectionRecord::getCheckTime, startTime)
                .le(InspectionRecord::getCheckTime, endTime)
                .orderByDesc(InspectionRecord::getCheckTime)
        );
        return records.stream().map(r -> {
            InspectionReportDTO dto = new InspectionReportDTO();
            if (r.getEquipmentId() != null) {
                Equipment equipment = equipmentMapper.selectById(r.getEquipmentId());
                if (equipment != null) {
                    dto.setEquipmentCode(equipment.getCode());
                    dto.setEquipmentName(equipment.getName());
                }
            }
            dto.setItemName(r.getItemName());
            dto.setResultName(r.getResult() != null && r.getResult() < RESULT_NAMES.length 
                ? RESULT_NAMES[r.getResult()] : "未知");
            dto.setResultValue(r.getResultValue());
            dto.setInspectorName(r.getInspectorName());
            dto.setCheckTime(r.getCheckTime() != null ? r.getCheckTime().format(DATE_TIME_FMT) : "");
            dto.setRemark(r.getRemark());
            return dto;
        }).collect(Collectors.toList());
    }
}
