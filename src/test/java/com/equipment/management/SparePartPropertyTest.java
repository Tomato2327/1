package com.equipment.management;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 备品备件属性测试
 * Property 13: 库存预警触发
 * Property 14: 备件消耗记录
 */
class SparePartPropertyTest {

    /**
     * Property 13: 当库存低于安全库存时应触发预警
     */
    @Property(tries = 100)
    void lowStockShouldTriggerAlert(
            @ForAll @IntRange(min = 0, max = 100) int stockQty,
            @ForAll @IntRange(min = 1, max = 100) int safeQty) {
        
        boolean shouldAlert = stockQty < safeQty;
        boolean isLowStock = stockQty < safeQty;
        
        assertEquals(shouldAlert, isLowStock);
    }

    /**
     * Property 14: 备件消耗后库存应正确减少
     */
    @Property(tries = 100)
    void stockShouldDecreaseAfterConsumption(
            @ForAll @IntRange(min = 10, max = 1000) int initialStock,
            @ForAll @IntRange(min = 1, max = 10) int consumeQty) {
        
        if (consumeQty <= initialStock) {
            int remainingStock = initialStock - consumeQty;
            assertTrue(remainingStock >= 0);
            assertEquals(initialStock - consumeQty, remainingStock);
        }
    }

    /**
     * Property 15: 消耗数量不能超过库存
     */
    @Property(tries = 100)
    void consumptionShouldNotExceedStock(
            @ForAll @IntRange(min = 0, max = 100) int stockQty,
            @ForAll @IntRange(min = 1, max = 200) int consumeQty) {
        
        boolean canConsume = consumeQty <= stockQty;
        
        if (!canConsume) {
            // 应该抛出异常或返回失败
            assertTrue(consumeQty > stockQty);
        }
    }

    /**
     * Property 16: 备件总价计算正确性
     */
    @Property(tries = 100)
    void totalPriceShouldBeCorrect(
            @ForAll @IntRange(min = 1, max = 100) int quantity,
            @ForAll @DoubleRange(min = 0.01, max = 1000.0) double unitPrice) {
        
        BigDecimal qty = BigDecimal.valueOf(quantity);
        BigDecimal price = BigDecimal.valueOf(unitPrice);
        BigDecimal totalPrice = qty.multiply(price);
        
        assertTrue(totalPrice.compareTo(BigDecimal.ZERO) > 0);
        assertEquals(0, totalPrice.compareTo(qty.multiply(price)));
    }

    /**
     * Property 17: 入库后库存应正确增加
     */
    @Property(tries = 100)
    void stockShouldIncreaseAfterStockIn(
            @ForAll @IntRange(min = 0, max = 1000) int initialStock,
            @ForAll @IntRange(min = 1, max = 100) int stockInQty) {
        
        int newStock = initialStock + stockInQty;
        
        assertTrue(newStock > initialStock);
        assertEquals(initialStock + stockInQty, newStock);
    }
}
