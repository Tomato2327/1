package com.equipment.management.maintenance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.equipment.management.common.exception.BusinessException;
import com.equipment.management.equipment.entity.Equipment;
import com.equipment.management.equipment.mapper.EquipmentMapper;
import com.equipment.management.maintenance.entity.MaintenanceOrder;
import com.equipment.management.maintenance.entity.PartConsumption;
import com.equipment.management.maintenance.entity.SparePart;
import com.equipment.management.maintenance.mapper.MaintenanceOrderMapper;
import com.equipment.management.maintenance.mapper.PartConsumptionMapper;
import com.equipment.management.maintenance.mapper.SparePartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceOrderMapper orderMapper;
    private final SparePartMapper partMapper;
    private final PartConsumptionMapper consumptionMapper;
    private final EquipmentMapper equipmentMapper;

    private static final AtomicInteger ORDER_SEQ = new AtomicInteger(0);

    // ==================== 维修工单管理 ====================

    public IPage<MaintenanceOrder> pageOrders(Page<MaintenanceOrder> page, Integer status,
                                               Long equipmentId, Long assigneeId, String keyword) {
        IPage<MaintenanceOrder> result = orderMapper.selectPageWithEquipment(page, status, equipmentId, assigneeId, keyword);
        result.getRecords().forEach(order -> {
            BigDecimal partCost = orderMapper.selectPartCostByOrderId(order.getId());
            order.setPartCost(partCost);
            order.setTotalCost(partCost.add(order.getLaborCost() != null ? order.getLaborCost() : BigDecimal.ZERO));
        });
        return result;
    }

    public MaintenanceOrder getOrderDetail(Long id) {
        MaintenanceOrder order = orderMapper.selectDetailById(id);
        if (order != null) {
            BigDecimal partCost = orderMapper.selectPartCostByOrderId(id);
            order.setPartCost(partCost);
            order.setTotalCost(partCost.add(order.getLaborCost() != null ? order.getLaborCost() : BigDecimal.ZERO));
        }
        return order;
    }

    @Transactional
    public MaintenanceOrder createOrder(MaintenanceOrder order) {
        order.setOrderNo(generateOrderNo());
        order.setStatus(1); // 待派发
        orderMapper.insert(order);
        // 更新设备状态为维修中
        Equipment equipment = equipmentMapper.selectById(order.getEquipmentId());
        if (equipment != null) {
            equipment.setStatus(3); // 维修中
            equipmentMapper.updateById(equipment);
        }
        return order;
    }


    @Transactional
    public void assignOrder(Long id, Long assigneeId, String assigneeName) {
        MaintenanceOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (order.getStatus() != 1) {
            throw new BusinessException("只有待派发状态的工单才能派发");
        }
        order.setAssigneeId(assigneeId);
        order.setAssigneeName(assigneeName);
        order.setStatus(2); // 待接单
        orderMapper.updateById(order);
    }

    @Transactional
    public void acceptOrder(Long id) {
        MaintenanceOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException("只有待接单状态的工单才能接单");
        }
        order.setStatus(3); // 维修中
        order.setAcceptTime(LocalDateTime.now());
        order.setStartTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Transactional
    public void finishOrder(Long id, String repairDesc, BigDecimal laborCost) {
        MaintenanceOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (order.getStatus() != 3) {
            throw new BusinessException("只有维修中状态的工单才能完成");
        }
        order.setStatus(4); // 待验收
        order.setRepairDesc(repairDesc);
        order.setLaborCost(laborCost);
        order.setFinishTime(LocalDateTime.now());
        orderMapper.updateById(order);
    }

    @Transactional
    public void checkOrder(Long id, Long checkerId, boolean passed) {
        MaintenanceOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("工单不存在");
        }
        if (order.getStatus() != 4) {
            throw new BusinessException("只有待验收状态的工单才能验收");
        }
        if (passed) {
            order.setStatus(5); // 已完成
            order.setCheckerId(checkerId);
            order.setCheckTime(LocalDateTime.now());
            // 恢复设备状态为运行
            Equipment equipment = equipmentMapper.selectById(order.getEquipmentId());
            if (equipment != null) {
                equipment.setStatus(1); // 运行
                equipmentMapper.updateById(equipment);
            }
        } else {
            order.setStatus(3); // 退回维修中
        }
        orderMapper.updateById(order);
    }

    private String generateOrderNo() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int seq = ORDER_SEQ.incrementAndGet() % 10000;
        return "WX" + date + String.format("%04d", seq);
    }

    // ==================== 备品备件管理 ====================

    public IPage<SparePart> pageParts(Page<SparePart> page, String keyword) {
        LambdaQueryWrapper<SparePart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SparePart::getDeleted, 0);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(SparePart::getName, keyword)
                    .or().like(SparePart::getCode, keyword));
        }
        wrapper.orderByDesc(SparePart::getCreateTime);
        IPage<SparePart> result = partMapper.selectPage(page, wrapper);
        result.getRecords().forEach(part -> part.setLowStock(part.getStockQty() < part.getSafeQty()));
        return result;
    }

    public SparePart getPartById(Long id) {
        return partMapper.selectById(id);
    }

    @Transactional
    public SparePart createPart(SparePart part) {
        if (partMapper.selectByCode(part.getCode()) != null) {
            throw new BusinessException("备件编号已存在");
        }
        partMapper.insert(part);
        return part;
    }

    @Transactional
    public void updatePart(SparePart part) {
        SparePart existing = partMapper.selectByCode(part.getCode());
        if (existing != null && !existing.getId().equals(part.getId())) {
            throw new BusinessException("备件编号已存在");
        }
        partMapper.updateById(part);
    }

    @Transactional
    public void deletePart(Long id) {
        partMapper.deleteById(id);
    }

    @Transactional
    public void stockIn(Long partId, Integer quantity) {
        if (quantity <= 0) {
            throw new BusinessException("入库数量必须大于0");
        }
        partMapper.increaseStock(partId, quantity);
    }

    @Transactional
    public void stockOut(Long partId, Integer quantity) {
        if (quantity <= 0) {
            throw new BusinessException("出库数量必须大于0");
        }
        int affected = partMapper.decreaseStock(partId, quantity);
        if (affected == 0) {
            throw new BusinessException("库存不足");
        }
    }

    public List<SparePart> getLowStockParts() {
        return partMapper.selectLowStockParts();
    }

    // ==================== 备件消耗管理 ====================

    @Transactional
    public void consumePart(Long orderId, Long partId, Integer quantity) {
        SparePart part = partMapper.selectById(partId);
        if (part == null) {
            throw new BusinessException("备件不存在");
        }
        int affected = partMapper.decreaseStock(partId, quantity);
        if (affected == 0) {
            throw new BusinessException("库存不足");
        }
        PartConsumption consumption = new PartConsumption();
        consumption.setOrderId(orderId);
        consumption.setPartId(partId);
        consumption.setPartName(part.getName());
        consumption.setQuantity(quantity);
        consumption.setUnitPrice(part.getPrice());
        consumption.setTotalPrice(part.getPrice().multiply(BigDecimal.valueOf(quantity)));
        consumptionMapper.insert(consumption);
    }

    public List<PartConsumption> getConsumptionsByOrderId(Long orderId) {
        return consumptionMapper.selectByOrderId(orderId);
    }

    // ==================== 统计相关 ====================

    public Map<String, Object> getOrderStats() {
        List<Map<String, Object>> statusCounts = orderMapper.countGroupByStatus();
        return Map.of("statusCounts", statusCounts);
    }

    public Double getAvgResponseTime(LocalDateTime startTime) {
        return orderMapper.avgResponseTime(startTime);
    }
}
