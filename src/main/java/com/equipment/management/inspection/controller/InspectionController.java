package com.equipment.management.inspection.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.equipment.management.common.result.PageResult;
import com.equipment.management.common.result.R;
import com.equipment.management.common.security.RequireRole;
import com.equipment.management.inspection.entity.InspectionPlan;
import com.equipment.management.inspection.entity.InspectionRecord;
import com.equipment.management.inspection.entity.InspectionStandard;
import com.equipment.management.inspection.entity.InspectionTask;
import com.equipment.management.inspection.service.InspectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 点检管理控制器
 */
@Api(tags = "点检管理")
@RestController
@RequestMapping("/api/inspection")
@RequiredArgsConstructor
@RequireRole({"ADMIN", "INSPECTOR"})  // 只有管理员和点检员可以访问
public class InspectionController {

    private final InspectionService inspectionService;

    // ========== 点检标准 ==========

    @ApiOperation("获取设备的点检标准列表")
    @GetMapping("/standard")
    public R<List<InspectionStandard>> getStandards(@RequestParam Long equipmentId) {
        List<InspectionStandard> list = inspectionService.getStandardsByEquipment(equipmentId);
        return R.ok(list);
    }

    @ApiOperation("新增点检标准")
    @PostMapping("/standard")
    @RequireRole({"ADMIN"})  // 只有管理员可以新增标准
    public R<Void> createStandard(@RequestBody InspectionStandard standard) {
        inspectionService.saveStandard(standard);
        return R.ok();
    }

    @ApiOperation("更新点检标准")
    @PutMapping("/standard/{id}")
    @RequireRole({"ADMIN"})  // 只有管理员可以更新标准
    public R<Void> updateStandard(@PathVariable Long id, @RequestBody InspectionStandard standard) {
        standard.setId(id);
        inspectionService.updateStandard(standard);
        return R.ok();
    }

    @ApiOperation("删除点检标准")
    @DeleteMapping("/standard/{id}")
    @RequireRole({"ADMIN"})  // 只有管理员可以删除标准
    public R<Void> deleteStandard(@PathVariable Long id) {
        inspectionService.deleteStandard(id);
        return R.ok();
    }

    // ========== 点检计划 ==========

    @ApiOperation("分页查询点检计划")
    @GetMapping("/plans")
    public R<PageResult<InspectionPlan>> getPlanPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String planType) {
        IPage<InspectionPlan> page = inspectionService.queryPlanPage(current, size, planType);
        PageResult<InspectionPlan> result = PageResult.of(
            page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()
        );
        return R.ok(result);
    }

    @ApiOperation("新增点检计划")
    @PostMapping("/plan")
    @RequireRole({"ADMIN"})  // 只有管理员可以新增计划
    public R<Void> createPlan(@RequestBody InspectionPlan plan) {
        inspectionService.savePlan(plan);
        return R.ok();
    }

    @ApiOperation("更新点检计划")
    @PutMapping("/plan/{id}")
    @RequireRole({"ADMIN"})  // 只有管理员可以更新计划
    public R<Void> updatePlan(@PathVariable Long id, @RequestBody InspectionPlan plan) {
        plan.setId(id);
        inspectionService.updatePlan(plan);
        return R.ok();
    }

    @ApiOperation("删除点检计划")
    @DeleteMapping("/plans/{id}")
    @RequireRole({"ADMIN"})  // 只有管理员可以删除计划
    public R<Void> deletePlan(@PathVariable Long id) {
        inspectionService.deletePlan(id);
        return R.ok();
    }

    // ========== 点检记录 ==========

    @ApiOperation("分页查询点检记录")
    @GetMapping("/records")
    public R<PageResult<InspectionRecord>> getRecordPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) Integer result) {
        IPage<InspectionRecord> page = inspectionService.queryRecordPage(current, size, equipmentId, result);
        PageResult<InspectionRecord> pageResult = PageResult.of(
            page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()
        );
        return R.ok(pageResult);
    }

    @ApiOperation("提交点检记录")
    @PostMapping("/record")
    public R<Void> createRecord(@RequestBody InspectionRecord record) {
        inspectionService.saveRecord(record);
        return R.ok();
    }

    @ApiOperation("获取设备的点检历史")
    @GetMapping("/record/equipment/{equipmentId}")
    public R<List<InspectionRecord>> getEquipmentRecords(@PathVariable Long equipmentId) {
        List<InspectionRecord> list = inspectionService.getRecordsByEquipment(equipmentId);
        return R.ok(list);
    }

    // ========== 点检任务 ==========

    @ApiOperation("分页查询点检任务")
    @GetMapping("/tasks")
    public R<PageResult<InspectionTask>> getTaskPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate taskDate) {
        IPage<InspectionTask> page = inspectionService.queryTaskPage(current, size, status, equipmentId, taskDate);
        PageResult<InspectionTask> result = PageResult.of(
            page.getRecords(), page.getTotal(), page.getSize(), page.getCurrent()
        );
        return R.ok(result);
    }

    @ApiOperation("完成点检任务")
    @PostMapping("/tasks/{id}/complete")
    public R<Void> completeTask(@PathVariable Long id) {
        inspectionService.completeTask(id);
        return R.ok();
    }

    @ApiOperation("新增点检任务")
    @PostMapping("/task")
    public R<Void> createTask(@RequestBody InspectionTask task) {
        inspectionService.saveTask(task);
        return R.ok();
    }

    @ApiOperation("删除点检任务")
    @DeleteMapping("/tasks/{id}")
    public R<Void> deleteTask(@PathVariable Long id) {
        inspectionService.deleteTask(id);
        return R.ok();
    }

    @ApiOperation("根据计划生成任务")
    @PostMapping("/plans/{id}/generate-tasks")
    public R<Void> generateTasks(@PathVariable Long id) {
        inspectionService.generateTasksFromPlan(id);
        return R.ok();
    }
}
