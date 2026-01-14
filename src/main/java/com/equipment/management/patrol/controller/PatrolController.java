package com.equipment.management.patrol.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.equipment.management.common.result.R;
import com.equipment.management.common.security.RequireRole;
import com.equipment.management.patrol.entity.PatrolRecord;
import com.equipment.management.patrol.entity.PatrolRoute;
import com.equipment.management.patrol.entity.PatrolTask;
import com.equipment.management.patrol.service.PatrolService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Api(tags = "巡检管理")
@RestController
@RequestMapping("/api/patrol")
@RequiredArgsConstructor
@RequireRole({"ADMIN", "PATROLLER"})  // 只有管理员和巡检员可以访问
public class PatrolController {

    private final PatrolService patrolService;

    // ==================== 巡检路线接口 ====================

    @ApiOperation("分页查询路线")
    @GetMapping("/routes")
    public R<IPage<PatrolRoute>> pageRoutes(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<PatrolRoute> page = new Page<>(current, size);
        IPage<PatrolRoute> result = patrolService.pageRoutes(page, keyword);
        return R.ok(result);
    }

    @ApiOperation("获取所有路线")
    @GetMapping("/routes/all")
    public R<List<PatrolRoute>> listAllRoutes() {
        return R.ok(patrolService.listAllRoutes());
    }

    @ApiOperation("获取路线详情")
    @GetMapping("/routes/{id}")
    public R<PatrolRoute> getRouteById(@PathVariable Long id) {
        return R.ok(patrolService.getRouteById(id));
    }

    @ApiOperation("创建路线")
    @PostMapping("/routes")
    @RequireRole({"ADMIN"})  // 只有管理员可以创建路线
    public R<PatrolRoute> createRoute(@RequestBody PatrolRoute route) {
        return R.ok(patrolService.createRoute(route));
    }

    @ApiOperation("更新路线")
    @PutMapping("/routes/{id}")
    @RequireRole({"ADMIN"})  // 只有管理员可以更新路线
    public R<Void> updateRoute(@PathVariable Long id, @RequestBody PatrolRoute route) {
        route.setId(id);
        patrolService.updateRoute(route);
        return R.ok();
    }

    @ApiOperation("删除路线")
    @DeleteMapping("/routes/{id}")
    @RequireRole({"ADMIN"})  // 只有管理员可以删除路线
    public R<Void> deleteRoute(@PathVariable Long id) {
        patrolService.deleteRoute(id);
        return R.ok();
    }


    // ==================== 巡检任务接口 ====================

    @ApiOperation("分页查询任务")
    @GetMapping("/tasks")
    public R<IPage<PatrolTask>> pageTasks(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate taskDate) {
        Page<PatrolTask> page = new Page<>(current, size);
        IPage<PatrolTask> result = patrolService.pageTasks(page, status, assigneeId, taskDate);
        return R.ok(result);
    }

    @ApiOperation("获取任务详情")
    @GetMapping("/tasks/{id}")
    public R<PatrolTask> getTaskDetail(@PathVariable Long id) {
        return R.ok(patrolService.getTaskDetail(id));
    }

    @ApiOperation("创建任务")
    @PostMapping("/tasks")
    public R<PatrolTask> createTask(@RequestBody PatrolTask task) {
        return R.ok(patrolService.createTask(task));
    }

    @ApiOperation("开始任务")
    @PostMapping("/tasks/{id}/start")
    public R<Void> startTask(@PathVariable Long id) {
        patrolService.startTask(id);
        return R.ok();
    }

    @ApiOperation("完成任务")
    @PostMapping("/tasks/{id}/finish")
    public R<Void> finishTask(@PathVariable Long id) {
        patrolService.finishTask(id);
        return R.ok();
    }

    @ApiOperation("删除任务")
    @DeleteMapping("/tasks/{id}")
    public R<Void> deleteTask(@PathVariable Long id) {
        patrolService.deleteTask(id);
        return R.ok();
    }

    // ==================== 巡检记录接口 ====================

    @ApiOperation("签到打卡")
    @PostMapping("/records/check-in")
    public R<PatrolRecord> checkIn(@RequestBody PatrolRecord record) {
        return R.ok(patrolService.checkIn(record));
    }

    @ApiOperation("获取任务记录")
    @GetMapping("/tasks/{taskId}/records")
    public R<List<PatrolRecord>> getRecordsByTaskId(@PathVariable Long taskId) {
        return R.ok(patrolService.getRecordsByTaskId(taskId));
    }

    @ApiOperation("获取巡检轨迹")
    @GetMapping("/tasks/{taskId}/track")
    public R<List<PatrolRecord>> getTaskTrack(@PathVariable Long taskId) {
        return R.ok(patrolService.getTaskTrack(taskId));
    }
}
