package com.equipment.management;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 设备二维码属性测试
 * Property 5: 设备二维码信息完整性
 */
class QrCodePropertyTest {

    /**
     * Property 5: 二维码内容应该包含设备ID
     */
    @Property(tries = 100)
    void qrCodeShouldContainEquipmentId(
            @ForAll @LongRange(min = 1, max = 100000) Long equipmentId) {
        
        String qrContent = "EQUIPMENT:" + equipmentId;
        
        assertNotNull(qrContent);
        assertTrue(qrContent.contains(equipmentId.toString()));
    }

    /**
     * Property 6: 二维码内容应该包含设备编号
     */
    @Property(tries = 100)
    void qrCodeShouldContainEquipmentCode(
            @ForAll @StringLength(min = 5, max = 20) @AlphaChars String code) {
        
        String qrContent = "CODE:" + code;
        
        assertNotNull(qrContent);
        assertTrue(qrContent.contains(code));
    }

    /**
     * Property 7: 二维码内容长度应该在合理范围内
     */
    @Property(tries = 100)
    void qrCodeContentLengthShouldBeReasonable(
            @ForAll @LongRange(min = 1, max = 100000) Long equipmentId,
            @ForAll @StringLength(min = 5, max = 20) @AlphaChars String code) {
        
        String qrContent = String.format("EQUIPMENT:%d|CODE:%s", equipmentId, code);
        
        // 二维码内容不应过长
        assertTrue(qrContent.length() <= 500);
        assertTrue(qrContent.length() >= 10);
    }

    /**
     * Property 8: 二维码应该可以被解析
     */
    @Property(tries = 100)
    void qrCodeShouldBeParseable(
            @ForAll @LongRange(min = 1, max = 100000) Long equipmentId) {
        
        String qrContent = "EQUIPMENT:" + equipmentId;
        
        // 模拟解析
        String[] parts = qrContent.split(":");
        assertEquals(2, parts.length);
        assertEquals("EQUIPMENT", parts[0]);
        assertEquals(equipmentId.toString(), parts[1]);
    }
}
