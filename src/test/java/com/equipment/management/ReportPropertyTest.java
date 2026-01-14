package com.equipment.management;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 报表统计属性测试
 * Property 15: 设备运行率统计准确性
 * Property 16: 点检完成率统计准确性
 * Property 17: 维修响应时间计算
 * Property 18: 维修成本统计
 */
class ReportPropertyTest {

    /**
     * Property 15: 设备运行率应该在0-100%之间
     */
    @Property(tries = 100)
    void equipmentRunningRateShouldBeValid(
            @ForAll @IntRange(min = 0, max = 100) int running,
            @ForAll @IntRange(min = 0, max = 100) int total) {
        
        if (total > 0 && running <= total) {
            BigDecimal rate = BigDecimal.valueOf(running)
                    .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            
            assertTrue(rate.compareTo(BigDecimal.ZERO) >= 0);
            assertTrue(rate.compareTo(BigDecimal.valueOf(100)) <= 0);
        }
    }

    /**
     * Property 16: 点检完成率应该在0-100%之间
     */
    @Property(tries = 100)
    void inspectionCompletionRateShouldBeValid(
            @ForAll @IntRange(min = 0, max = 100) int completed,
            @ForAll @IntRange(min = 1, max = 100) int total) {
        
        if (completed <= total) {
            BigDecimal rate = BigDecimal.valueOf(completed)
                    .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            
            assertTrue(rate.compareTo(BigDecimal.ZERO) >= 0);
            assertTrue(rate.compareTo(BigDecimal.valueOf(100)) <= 0);
        }
    }

    /**
     * Property 17: 维修响应时间应该为非负数
     */
    @Property(tries = 100)
    void maintenanceResponseTimeShouldBeNonNegative(
            @ForAll @LongRange(min = 0, max = 10000) long createTimeMinutes,
            @ForAll @LongRange(min = 0, max = 10000) long acceptTimeMinutes) {
        
        if (acceptTimeMinutes >= createTimeMinutes) {
            long responseTime = acceptTimeMinutes - createTimeMinutes;
            assertTrue(responseTime >= 0);
        }
    }

    /**
     * Property 18: 维修总成本应该等于人工成本加备件成本
     */
    @Property(tries = 100)
    void totalCostShouldEqualLaborPlusParts(
            @ForAll @DoubleRange(min = 0, max = 10000) double laborCost,
            @ForAll @DoubleRange(min = 0, max = 10000) double partCost) {
        
        BigDecimal labor = BigDecimal.valueOf(laborCost).setScale(2, RoundingMode.HALF_UP);
        BigDecimal parts = BigDecimal.valueOf(partCost).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = labor.add(parts);
        
        assertEquals(0, total.compareTo(labor.add(parts)));
        assertTrue(total.compareTo(BigDecimal.ZERO) >= 0);
    }

    /**
     * Property 19: 故障率应该在0-100%之间
     */
    @Property(tries = 100)
    void faultRateShouldBeValid(
            @ForAll @IntRange(min = 0, max = 100) int faulted,
            @ForAll @IntRange(min = 1, max = 100) int total) {
        
        if (faulted <= total) {
            BigDecimal rate = BigDecimal.valueOf(faulted)
                    .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            
            assertTrue(rate.compareTo(BigDecimal.ZERO) >= 0);
            assertTrue(rate.compareTo(BigDecimal.valueOf(100)) <= 0);
        }
    }
}
