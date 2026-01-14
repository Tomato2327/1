package com.equipment.management.inspection.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.equipment.management.inspection.entity.InspectionPlan;
import com.equipment.management.inspection.entity.InspectionRecord;
import com.equipment.management.inspection.entity.InspectionStandard;
import com.equipment.management.inspection.entity.InspectionTask;

import java.time.LocalDate;
import java.util.List;

/**
 * 点检服务接口
 */
public interface InspectionService {

    // 点检标准
    List<InspectionStandard> getStandardsByEquipment(Long equipmentId);
    boolean saveStandard(InspectionStandard standard);
    boolean updateStandard(InspectionStandard standard);
    boolean deleteStandard(Long id);

    // 点检计划
    IPage<InspectionPlan> queryPlanPage(int pageNum, int pageSize, String planType);
    boolean savePlan(InspectionPlan plan);
    boolean updatePlan(InspectionPlan plan);
    boolean deletePlan(Long id);

    // 点检记录
    IPage<InspectionRecord> queryRecordPage(int pageNum, int pageSize, Long equipmentId, Integer result);
    boolean saveRecord(InspectionRecord record);
    List<InspectionRecord> getRecordsByEquipment(Long equipmentId);

    // 点检任务
    IPage<InspectionTask> queryTaskPage(int pageNum, int pageSize, Integer status, Long equipmentId, LocalDate taskDate);
    boolean completeTask(Long id);
    boolean saveTask(InspectionTask task);
    boolean deleteTask(Long id);
    void generateTasksFromPlan(Long planId);
}
