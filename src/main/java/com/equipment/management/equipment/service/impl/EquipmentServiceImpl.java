package com.equipment.management.equipment.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.equipment.management.common.exception.BusinessException;
import com.equipment.management.equipment.dto.EquipmentQueryDTO;
import com.equipment.management.equipment.entity.Equipment;
import com.equipment.management.equipment.mapper.EquipmentMapper;
import com.equipment.management.equipment.service.EquipmentService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 设备服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EquipmentServiceImpl extends ServiceImpl<EquipmentMapper, Equipment> implements EquipmentService {

    @Value("${file.upload-path:./uploads/}")
    private String uploadPath;

    @Override
    public IPage<Equipment> queryPage(EquipmentQueryDTO query) {
        Page<Equipment> page = new Page<>(query.getPageNum(), query.getPageSize());
        return baseMapper.selectPageWithCategory(page, query);
    }

    @Override
    public Equipment getDetail(Long id) {
        return baseMapper.selectByIdWithCategory(id);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        Equipment equipment = getById(id);
        if (equipment == null) {
            throw new BusinessException("设备不存在");
        }
        
        // 验证状态值
        if (status < 1 || status > 4) {
            throw new BusinessException("无效的状态值");
        }
        
        equipment.setStatus(status);
        return updateById(equipment);
    }

    @Override
    public String generateQrCode(Long id) {
        Equipment equipment = getById(id);
        if (equipment == null) {
            throw new BusinessException("设备不存在");
        }

        try {
            // 二维码内容
            String content = "EQUIPMENT:" + equipment.getCode();
            
            // 生成二维码
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);
            
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 200, 200, hints);
            
            // 保存文件
            String fileName = "qr_" + equipment.getCode() + "_" + UUID.randomUUID().toString().substring(0, 8) + ".png";
            String qrDir = uploadPath + "qrcode/";
            File dir = new File(qrDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            Path path = Path.of(qrDir + fileName);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
            
            // 更新设备二维码路径
            String qrPath = "/uploads/qrcode/" + fileName;
            equipment.setQrCode(qrPath);
            updateById(equipment);
            
            return qrPath;
        } catch (Exception e) {
            log.error("生成二维码失败", e);
            throw new BusinessException("生成二维码失败");
        }
    }
}
