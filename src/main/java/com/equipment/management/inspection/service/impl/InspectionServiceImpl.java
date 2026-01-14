package com.equipment.management.inspection.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.equipment.management.common.constant.Constants;
import com.equipment.management.common.exception.BusinessException;
import com.equipment.management.common.security.SecurityUtils;
import com.equipment.management.inspection.entity.InspectionPlan;
import com.equipment.management.inspection.entity.InspectionRecord;
import com.equipment.management.inspection.entity.InspectionStandard;
import com.equipment.management.inspection.entity.InspectionTask;
import com.equipment.management.inspection.mapper.InspectionPlanMapper;
import com.equipment.management.inspection.mapper.InspectionRecordMapper;
import com.equipment.management.inspection.mapper.InspectionStandardMapper;
import com.equipment.management.inspection.mapper.InspectionTaskMapper;
import com.equipment.management.inspection.service.InspectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 点检服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionServiceImpl implements InspectionService {

    private final InspectionStandardMapper standardMapper;
    private final InspectionPlanMapper planMapper;
    private final InspectionRecordMapper recordMapper;
    private final InspectionTaskMapper taskMapper;

    @Override
    public List<InspectionStandard> getStandardsByEquipment(Long equipmentId) {
        return standardMapper.selectByEquipmentId(equipmentId);
    }

    @Override
    public boolean saveStandard(InspectionStandard standard) {
        return standardMapper.insert(standard) > 0;
    }

    @Override
    public boolean updateStandard(InspectionStandard standard) {
        return standardMapper.updateById(standard) > 0;
    }

    @Override
    public boolean deleteStandard(Long id) {
        return standardMapper.deleteById(id) > 0;
    }

    @Override
    public IPage<InspectionPlan> queryPlanPage(int pageNum, int pageSize, String planType) {
        Page<InspectionPlan> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<InspectionPlan> wrapper = new LambdaQueryWrapper<>();
        if (planType != null && !planType.isEmpty()) {
            wrapper.eq(InspectionPlan::getPlanType, planType);
        }
        wrapper.orderByDesc(InspectionPlan::getCreateTime);
        return planMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean savePlan(InspectionPlan plan) {
        plan.setStatus(1);
        return planMapper.insert(plan) > 0;
    }

    @Override
    public boolean updatePlan(InspectionPlan plan) {
        return planMapper.updateById(plan) > 0;
    }

    @Override
    public boolean deletePlan(Long id) {
        return planMapper.deleteById(id) > 0;
    }

    @Override
    public IPage<InspectionRecord> queryRecordPage(int pageNum, int pageSize, Long equipmentId, Integer result) {
        Page<InspectionRecord> page = new Page<>(pageNum, pageSize);
        return recordMapper.selectPageWithEquipment(page, equipmentId, result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveRecord(InspectionRecord record) {
        // 设置点检人信息
        record.setInspectorId(SecurityUtils.getUserId());
        record.setInspectorName(SecurityUtils.getUsername());
        record.setCheckTime(LocalDateTime.now());
        
        boolean saved = recordMapper.insert(record) > 0;
        
        // 如果点检结果异常，可以触发后续流程（如创建维修工单）
        if (saved && record.getResult() != null && record.getResult() == Constants.InspectionResult.ABNORMAL) {
            log.info("点检发现异常，设备ID: {}, 项目: {}", record.getEquipmentId(), record.getItemName());
            // TODO: 触发异常处理流程
        }
        
        return saved;
    }

    @Override
    public List<InspectionRecord> getRecordsByEquipment(Long equipmentId) {
        LambdaQueryWrapper<InspectionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InspectionRecord::getEquipmentId, equipmentId);
        wrapper.orderByDesc(InspectionRecord::getCheckTime);
        wrapper.last("LIMIT 50");
        return recordMapper.selectList(wrapper);
    }

    @Override
    public IPage<InspectionTask> queryTaskPage(int pageNum, int pageSize, Integer status, Long equipmentId, LocalDate taskDate) {
        Page<InspectionTask> page = new Page<>(pageNum, pageSize);
        return taskMapper.selectPageWithDetail(page, status, equipmentId, taskDate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeTask(Long id) {
        InspectionTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (task.getStatus() != 0) {
            throw new BusinessException("只有待执行状态的任务才能完成");
        }
        task.setStatus(1); // 已完成
        return taskMapper.updateById(task) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveTask(InspectionTask task) {
        task.setStatus(0); // 待执行
        return taskMapper.insert(task) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteTask(Long id) {
        InspectionTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (task.getStatus() != 0) {
            throw new BusinessException("只有待执行状态的任务才能删除");
        }
        return taskMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void generateTasksFromPlan(Long planId) {
        InspectionPlan plan = planMapper.selectById(planId);
        if (plan == null) {
            throw new BusinessException("计划不存在");
        }
        if (plan.getEquipmentIds() == null || plan.getEquipmentIds().isEmpty()) {
            throw new BusinessException("计划未关联设备");
        }
        String[] equipmentIdArr = plan.getEquipmentIds().split(",");
        LocalDate today = LocalDate.now();
        for (String eqId : equipmentIdArr) {
            InspectionTask task = new InspectionTask();
            task.setPlanId(planId);
            task.setEquipmentId(Long.parseLong(eqId.trim()));
            task.setAssigneeId(plan.getAssigneeId());
            task.setTaskDate(today);
            task.setStatus(0);
            taskMapper.insert(task);
        }
    }
}
