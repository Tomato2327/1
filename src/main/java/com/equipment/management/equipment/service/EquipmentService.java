package com.equipment.management.equipment.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.equipment.management.equipment.dto.EquipmentQueryDTO;
import com.equipment.management.equipment.entity.Equipment;

/**
 * 设备服务接口
 */
public interface EquipmentService extends IService<Equipment> {

    /**
     * 分页查询设备
     */
    IPage<Equipment> queryPage(EquipmentQueryDTO query);

    /**
     * 根据ID查询设备详情
     */
    Equipment getDetail(Long id);

    /**
     * 更新设备状态
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 生成设备二维码
     */
    String generateQrCode(Long id);
}
