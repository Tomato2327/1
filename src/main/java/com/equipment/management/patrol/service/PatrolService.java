package com.equipment.management.patrol.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.equipment.management.common.exception.BusinessException;
import com.equipment.management.patrol.entity.PatrolRecord;
import com.equipment.management.patrol.entity.PatrolRoute;
import com.equipment.management.patrol.entity.PatrolTask;
import com.equipment.management.patrol.mapper.PatrolRecordMapper;
import com.equipment.management.patrol.mapper.PatrolRouteMapper;
import com.equipment.management.patrol.mapper.PatrolTaskMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PatrolService {

    private final PatrolRouteMapper routeMapper;
    private final PatrolTaskMapper taskMapper;
    private final PatrolRecordMapper recordMapper;
    private final ObjectMapper objectMapper;

    // ==================== 巡检路线管理 ====================

    public IPage<PatrolRoute> pageRoutes(Page<PatrolRoute> page, String keyword) {
        LambdaQueryWrapper<PatrolRoute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatrolRoute::getDeleted, 0);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(PatrolRoute::getName, keyword);
        }
        wrapper.orderByDesc(PatrolRoute::getCreateTime);
        return routeMapper.selectPage(page, wrapper);
    }

    public List<PatrolRoute> listAllRoutes() {
        LambdaQueryWrapper<PatrolRoute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PatrolRoute::getDeleted, 0);
        return routeMapper.selectList(wrapper);
    }

    public PatrolRoute getRouteById(Long id) {
        return routeMapper.selectById(id);
    }

    @Transactional
    public PatrolRoute createRoute(PatrolRoute route) {
        routeMapper.insert(route);
        return route;
    }

    @Transactional
    public void updateRoute(PatrolRoute route) {
        routeMapper.updateById(route);
    }

    @Transactional
    public void deleteRoute(Long id) {
        routeMapper.deleteById(id);
    }


    // ==================== 巡检任务管理 ====================

    public IPage<PatrolTask> pageTasks(Page<PatrolTask> page, Integer status, Long assigneeId, LocalDate taskDate) {
        IPage<PatrolTask> result = taskMapper.selectPageWithDetail(page, status, assigneeId, taskDate);
        result.getRecords().forEach(task -> {
            PatrolRoute route = routeMapper.selectById(task.getRouteId());
            if (route != null && route.getCheckpoints() != null) {
                try {
                    List<Map<String, Object>> checkpoints = objectMapper.readValue(
                            route.getCheckpoints(), new TypeReference<List<Map<String, Object>>>() {});
                    task.setCheckpointCount(checkpoints.size());
                } catch (Exception e) {
                    task.setCheckpointCount(0);
                }
            }
            task.setCompletedCount(recordMapper.countByTaskId(task.getId()));
        });
        return result;
    }

    public PatrolTask getTaskDetail(Long id) {
        PatrolTask task = taskMapper.selectDetailById(id);
        if (task != null) {
            PatrolRoute route = routeMapper.selectById(task.getRouteId());
            if (route != null && route.getCheckpoints() != null) {
                try {
                    List<Map<String, Object>> checkpoints = objectMapper.readValue(
                            route.getCheckpoints(), new TypeReference<List<Map<String, Object>>>() {});
                    task.setCheckpointCount(checkpoints.size());
                } catch (Exception e) {
                    task.setCheckpointCount(0);
                }
            }
            task.setCompletedCount(recordMapper.countByTaskId(id));
        }
        return task;
    }

    @Transactional
    public PatrolTask createTask(PatrolTask task) {
        task.setStatus(0); // 待执行
        taskMapper.insert(task);
        return task;
    }

    @Transactional
    public void startTask(Long id) {
        PatrolTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (task.getStatus() != 0) {
            throw new BusinessException("只有待执行状态的任务才能开始");
        }
        task.setStatus(1); // 执行中
        task.setStartTime(LocalDateTime.now());
        taskMapper.updateById(task);
    }

    @Transactional
    public void finishTask(Long id) {
        PatrolTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (task.getStatus() != 1) {
            throw new BusinessException("只有执行中状态的任务才能完成");
        }
        task.setStatus(2); // 已完成
        task.setEndTime(LocalDateTime.now());
        taskMapper.updateById(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        PatrolTask task = taskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (task.getStatus() != 0) {
            throw new BusinessException("只有待执行状态的任务才能删除");
        }
        taskMapper.deleteById(id);
    }

    // ==================== 巡检记录管理 ====================

    @Transactional
    public PatrolRecord checkIn(PatrolRecord record) {
        PatrolTask task = taskMapper.selectById(record.getTaskId());
        if (task == null) {
            throw new BusinessException("任务不存在");
        }
        if (task.getStatus() == 0) {
            // 自动开始任务
            task.setStatus(1);
            task.setStartTime(LocalDateTime.now());
            taskMapper.updateById(task);
        }
        if (task.getStatus() != 1) {
            throw new BusinessException("任务状态不允许签到");
        }
        record.setCheckTime(LocalDateTime.now());
        recordMapper.insert(record);
        return record;
    }

    public List<PatrolRecord> getRecordsByTaskId(Long taskId) {
        return recordMapper.selectByTaskId(taskId);
    }

    public List<PatrolRecord> getTaskTrack(Long taskId) {
        return recordMapper.selectByTaskId(taskId);
    }
}
