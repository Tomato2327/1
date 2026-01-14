package com.equipment.management.equipment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.equipment.management.common.result.PageResult;
import com.equipment.management.common.result.R;
import com.equipment.management.common.security.RequireRole;
import com.equipment.management.equipment.dto.EquipmentQueryDTO;
import com.equipment.management.equipment.entity.Equipment;
import com.equipment.management.equipment.entity.EquipmentCategory;
import com.equipment.management.equipment.mapper.EquipmentCategoryMapper;
import com.equipment.management.equipment.service.EquipmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设备管理控制器
 */
@Api(tags = "设备管理")
@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
@RequireRole({"ADMIN", "EQUIPMENT_ADMIN"})  // 只有管理员和设备管理员可以访问
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final EquipmentCategoryMapper categoryMapper;

    @ApiOperation("分页查询设备列表")
    @GetMapping("/list")
    public R<PageResult<Equipment>> list(EquipmentQueryDTO query) {
        IPage<Equipment> page = equipmentService.queryPage(query);
        PageResult<Equipment> result = PageResult.of(
            page.getRecords(), 
            page.getTotal(), 
            page.getSize(), 
            page.getCurrent()
        );
        return R.ok(result);
    }

    @ApiOperation("获取设备详情")
    @GetMapping("/{id}")
    public R<Equipment> getById(@PathVariable Long id) {
        Equipment equipment = equipmentService.getDetail(id);
        return R.ok(equipment);
    }

    @ApiOperation("新增设备")
    @PostMapping
    public R<Void> create(@Validated @RequestBody Equipment equipment) {
        equipmentService.save(equipment);
        return R.ok();
    }

    @ApiOperation("更新设备")
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id, @Validated @RequestBody Equipment equipment) {
        equipment.setId(id);
        equipmentService.updateById(equipment);
        return R.ok();
    }

    @ApiOperation("删除设备")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        equipmentService.removeById(id);
        return R.ok();
    }

    @ApiOperation("更新设备状态")
    @PutMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        equipmentService.updateStatus(id, status);
        return R.ok();
    }

    @ApiOperation("生成设备二维码")
    @PostMapping("/{id}/qrcode")
    public R<String> generateQrCode(@PathVariable Long id) {
        String qrPath = equipmentService.generateQrCode(id);
        return R.ok(qrPath);
    }

    @ApiOperation("获取设备分类列表")
    @GetMapping("/categories")
    public R<List<EquipmentCategory>> categoryList() {
        LambdaQueryWrapper<EquipmentCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(EquipmentCategory::getSortOrder);
        List<EquipmentCategory> list = categoryMapper.selectList(wrapper);
        return R.ok(list);
    }

    @ApiOperation("新增设备分类")
    @PostMapping("/category")
    public R<Void> createCategory(@RequestBody EquipmentCategory category) {
        categoryMapper.insert(category);
        return R.ok();
    }

    @ApiOperation("更新设备分类")
    @PutMapping("/category/{id}")
    public R<Void> updateCategory(@PathVariable Long id, @RequestBody EquipmentCategory category) {
        category.setId(id);
        categoryMapper.updateById(category);
        return R.ok();
    }

    @ApiOperation("删除设备分类")
    @DeleteMapping("/category/{id}")
    public R<Void> deleteCategory(@PathVariable Long id) {
        categoryMapper.deleteById(id);
        return R.ok();
    }
}
