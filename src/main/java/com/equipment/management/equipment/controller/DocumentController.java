package com.equipment.management.equipment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.equipment.management.common.result.R;
import com.equipment.management.equipment.entity.EquipmentDocument;
import com.equipment.management.equipment.mapper.EquipmentDocumentMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 设备档案控制器
 */
@Api(tags = "设备档案管理")
@RestController
@RequestMapping("/api/equipment/document")
@RequiredArgsConstructor
public class DocumentController {

    private final EquipmentDocumentMapper documentMapper;

    @Value("${file.upload-path:./uploads}")
    private String uploadPath;

    @ApiOperation("上传档案")
    @PostMapping("/upload")
    public R<EquipmentDocument> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("equipmentId") Long equipmentId) throws IOException {
        
        if (file.isEmpty()) {
            return R.fail("文件不能为空");
        }

        // 生成文件名
        String originalName = file.getOriginalFilename();
        String ext = originalName != null && originalName.contains(".") 
            ? originalName.substring(originalName.lastIndexOf(".")) : "";
        String fileName = UUID.randomUUID().toString() + ext;

        // 创建目录
        File dir = new File(uploadPath + "/documents");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 保存文件
        File dest = new File(dir, fileName);
        file.transferTo(dest);

        // 保存记录
        EquipmentDocument doc = new EquipmentDocument();
        doc.setEquipmentId(equipmentId);
        doc.setName(originalName);
        doc.setFilePath("/documents/" + fileName);
        doc.setFileType(file.getContentType());
        doc.setFileSize(file.getSize());
        documentMapper.insert(doc);

        return R.ok(doc);
    }

    @ApiOperation("查询设备档案列表")
    @GetMapping("/list/{equipmentId}")
    public R<List<EquipmentDocument>> list(@PathVariable Long equipmentId) {
        List<EquipmentDocument> list = documentMapper.selectList(
            new LambdaQueryWrapper<EquipmentDocument>()
                .eq(EquipmentDocument::getEquipmentId, equipmentId)
                .orderByDesc(EquipmentDocument::getCreateTime)
        );
        return R.ok(list);
    }

    @ApiOperation("删除档案")
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        EquipmentDocument doc = documentMapper.selectById(id);
        if (doc != null) {
            // 删除文件
            File file = new File(uploadPath + doc.getFilePath());
            if (file.exists()) {
                file.delete();
            }
            documentMapper.deleteById(id);
        }
        return R.ok();
    }
}
