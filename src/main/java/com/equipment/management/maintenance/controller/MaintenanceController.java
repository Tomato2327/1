package com.equipment.management.maintenance.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.equipment.management.common.result.R;
import com.equipment.management.common.security.RequireRole;
import com.equipment.management.maintenance.entity.MaintenanceOrder;
import com.equipment.management.maintenance.entity.PartConsumption;
import com.equipment.management.maintenance.entity.SparePart;
import com.equipment.management.maintenance.service.MaintenanceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Api(tags = "维修管理")
@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
@RequireRole({"ADMIN", "MAINTAINER"})  // 只有管理员和维修员可以访问
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    // ==================== 维修工单接口 ====================

    @ApiOperation("分页查询工单")
    @GetMapping("/orders")
    public R<IPage<MaintenanceOrder>> pageOrders(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long equipmentId,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) String keyword) {
        Page<MaintenanceOrder> page = new Page<>(current, size);
        IPage<MaintenanceOrder> result = maintenanceService.pageOrders(page, status, equipmentId, assigneeId, keyword);
        return R.ok(result);
    }

    @ApiOperation("获取工单详情")
    @GetMapping("/orders/{id}")
    public R<MaintenanceOrder> getOrderDetail(@PathVariable Long id) {
        return R.ok(maintenanceService.getOrderDetail(id));
    }

    @ApiOperation("创建工单")
    @PostMapping("/orders")
    public R<MaintenanceOrder> createOrder(@RequestBody MaintenanceOrder order) {
        return R.ok(maintenanceService.createOrder(order));
    }

    @ApiOperation("派发工单")
    @PostMapping("/orders/{id}/assign")
    @RequireRole({"ADMIN"})  // 只有管理员可以派发
    public R<Void> assignOrder(@PathVariable Long id,
                               @RequestParam Long assigneeId,
                               @RequestParam String assigneeName) {
        maintenanceService.assignOrder(id, assigneeId, assigneeName);
        return R.ok();
    }


    @ApiOperation("接单")
    @PostMapping("/orders/{id}/accept")
    @RequireRole({"ADMIN", "MAINTAINER"})  // 维修员接单
    public R<Void> acceptOrder(@PathVariable Long id) {
        maintenanceService.acceptOrder(id);
        return R.ok();
    }

    @ApiOperation("完成维修")
    @PostMapping("/orders/{id}/finish")
    @RequireRole({"ADMIN", "MAINTAINER"})  // 维修员完成
    public R<Void> finishOrder(@PathVariable Long id,
                               @RequestParam String repairDesc,
                               @RequestParam BigDecimal laborCost) {
        maintenanceService.finishOrder(id, repairDesc, laborCost);
        return R.ok();
    }

    @ApiOperation("验收工单")
    @PostMapping("/orders/{id}/check")
    @RequireRole({"ADMIN"})  // 只有管理员可以验收
    public R<Void> checkOrder(@PathVariable Long id,
                              @RequestParam Long checkerId,
                              @RequestParam boolean passed) {
        maintenanceService.checkOrder(id, checkerId, passed);
        return R.ok();
    }

    @ApiOperation("获取工单统计")
    @GetMapping("/orders/stats")
    public R<Map<String, Object>> getOrderStats() {
        return R.ok(maintenanceService.getOrderStats());
    }

    // ==================== 备品备件接口 ====================

    @ApiOperation("分页查询备件")
    @GetMapping("/parts")
    public R<IPage<SparePart>> pageParts(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword) {
        Page<SparePart> page = new Page<>(current, size);
        return R.ok(maintenanceService.pageParts(page, keyword));
    }

    @ApiOperation("获取备件详情")
    @GetMapping("/parts/{id}")
    public R<SparePart> getPartById(@PathVariable Long id) {
        return R.ok(maintenanceService.getPartById(id));
    }

    @ApiOperation("创建备件")
    @PostMapping("/parts")
    public R<SparePart> createPart(@RequestBody SparePart part) {
        return R.ok(maintenanceService.createPart(part));
    }

    @ApiOperation("更新备件")
    @PutMapping("/parts/{id}")
    public R<Void> updatePart(@PathVariable Long id, @RequestBody SparePart part) {
        part.setId(id);
        maintenanceService.updatePart(part);
        return R.ok();
    }

    @ApiOperation("删除备件")
    @DeleteMapping("/parts/{id}")
    public R<Void> deletePart(@PathVariable Long id) {
        maintenanceService.deletePart(id);
        return R.ok();
    }

    @ApiOperation("备件入库")
    @PostMapping("/parts/{id}/stock-in")
    public R<Void> stockIn(@PathVariable Long id, @RequestParam Integer quantity) {
        maintenanceService.stockIn(id, quantity);
        return R.ok();
    }

    @ApiOperation("备件出库")
    @PostMapping("/parts/{id}/stock-out")
    public R<Void> stockOut(@PathVariable Long id, @RequestParam Integer quantity) {
        maintenanceService.stockOut(id, quantity);
        return R.ok();
    }

    @ApiOperation("获取库存预警列表")
    @GetMapping("/parts/low-stock")
    public R<List<SparePart>> getLowStockParts() {
        return R.ok(maintenanceService.getLowStockParts());
    }

    // ==================== 备件消耗接口 ====================

    @ApiOperation("消耗备件")
    @PostMapping("/orders/{orderId}/consume")
    public R<Void> consumePart(@PathVariable Long orderId,
                               @RequestParam Long partId,
                               @RequestParam Integer quantity) {
        maintenanceService.consumePart(orderId, partId, quantity);
        return R.ok();
    }

    @ApiOperation("获取工单备件消耗")
    @GetMapping("/orders/{orderId}/consumptions")
    public R<List<PartConsumption>> getConsumptions(@PathVariable Long orderId) {
        return R.ok(maintenanceService.getConsumptionsByOrderId(orderId));
    }
}
