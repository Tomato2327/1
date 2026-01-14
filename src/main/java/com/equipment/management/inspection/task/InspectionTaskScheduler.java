package com.equipment.management.inspection.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.equipment.management.inspection.entity.InspectionPlan;
import com.equipment.management.inspection.mapper.InspectionPlanMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * 点检任务自动生成定时任务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InspectionTaskScheduler {

    private final InspectionPlanMapper planMapper;
    private final JdbcTemplate jdbcTemplate;

    /**
     * 每天凌晨1点自动生成当天的点检任务
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void generateDailyTasks() {
        LocalDate today = LocalDate.now();
        log.info("开始生成 {} 的点检任务", today);
        
        // 查询所有启用的计划
        List<InspectionPlan> plans = planMapper.selectList(
            new LambdaQueryWrapper<InspectionPlan>()
                .eq(InspectionPlan::getStatus, 1)
                .le(InspectionPlan::getStartDate, today)
                .ge(InspectionPlan::getEndDate, today)
        );
        
        int taskCount = 0;
        for (InspectionPlan plan : plans) {
            if (shouldGenerateTask(plan, today)) {
                taskCount += generateTasksForPlan(plan, today);
            }
        }
        
        log.info("点检任务生成完成，共生成 {} 个任务", taskCount);
    }

    /**
     * 判断是否应该生成任务
     */
    private boolean shouldGenerateTask(InspectionPlan plan, LocalDate date) {
        String planType = plan.getPlanType();
        
        switch (planType) {
            case "daily":
                return true;
            case "weekly":
                // 每周一生成
                return date.getDayOfWeek() == DayOfWeek.MONDAY;
            case "monthly":
                // 每月1号生成
                return date.getDayOfMonth() == 1;
            default:
                return false;
        }
    }

    /**
     * 为计划生成任务
     */
    private int generateTasksForPlan(InspectionPlan plan, LocalDate taskDate) {
        String equipmentIds = plan.getEquipmentIds();
        if (equipmentIds == null || equipmentIds.isEmpty()) {
            return 0;
        }
        
        List<String> ids = Arrays.asList(equipmentIds.split(","));
        int count = 0;
        
        for (String equipmentId : ids) {
            try {
                Long eqId = Long.parseLong(equipmentId.trim());
                
                // 检查是否已存在任务
                Integer exists = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM inspection_task WHERE plan_id = ? AND equipment_id = ? AND task_date = ?",
                    Integer.class, plan.getId(), eqId, taskDate
                );
                
                if (exists == null || exists == 0) {
                    jdbcTemplate.update(
                        "INSERT INTO inspection_task (plan_id, equipment_id, assignee_id, task_date, status) VALUES (?, ?, ?, ?, 0)",
                        plan.getId(), eqId, plan.getAssigneeId(), taskDate
                    );
                    count++;
                }
            } catch (NumberFormatException e) {
                log.warn("无效的设备ID: {}", equipmentId);
            }
        }
        
        return count;
    }

    /**
     * 手动触发任务生成（供测试使用）
     */
    public int generateTasksManually(LocalDate date) {
        List<InspectionPlan> plans = planMapper.selectList(
            new LambdaQueryWrapper<InspectionPlan>()
                .eq(InspectionPlan::getStatus, 1)
                .le(InspectionPlan::getStartDate, date)
                .ge(InspectionPlan::getEndDate, date)
        );
        
        int taskCount = 0;
        for (InspectionPlan plan : plans) {
            if (shouldGenerateTask(plan, date)) {
                taskCount += generateTasksForPlan(plan, date);
            }
        }
        return taskCount;
    }
}
