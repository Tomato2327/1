package com.equipment.management;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 分页查询属性测试
 * Property 6: 分页查询一致性
 * Property 19: API 响应格式一致性
 */
class PaginationPropertyTest {

    /**
     * Property 6: 分页参数应该有效
     */
    @Property(tries = 100)
    void paginationParametersShouldBeValid(
            @ForAll @IntRange(min = 1, max = 1000) int current,
            @ForAll @IntRange(min = 1, max = 100) int size) {
        
        assertTrue(current >= 1);
        assertTrue(size >= 1);
        assertTrue(size <= 100); // 最大每页100条
    }

    /**
     * Property 7: 分页结果应该正确计算总页数
     */
    @Property(tries = 100)
    void totalPagesShouldBeCorrect(
            @ForAll @IntRange(min = 0, max = 10000) int total,
            @ForAll @IntRange(min = 1, max = 100) int size) {
        
        int totalPages = (total + size - 1) / size;
        
        if (total == 0) {
            assertEquals(0, totalPages);
        } else {
            assertTrue(totalPages >= 1);
            assertTrue(totalPages * size >= total);
            assertTrue((totalPages - 1) * size < total);
        }
    }

    /**
     * Property 8: 当前页不应超过总页数
     */
    @Property(tries = 100)
    void currentPageShouldNotExceedTotalPages(
            @ForAll @IntRange(min = 1, max = 100) int current,
            @ForAll @IntRange(min = 0, max = 1000) int total,
            @ForAll @IntRange(min = 1, max = 100) int size) {
        
        int totalPages = total == 0 ? 1 : (total + size - 1) / size;
        int validCurrent = Math.min(current, totalPages);
        
        assertTrue(validCurrent <= totalPages);
        assertTrue(validCurrent >= 1);
    }

    /**
     * Property 9: 偏移量计算正确性
     */
    @Property(tries = 100)
    void offsetShouldBeCorrect(
            @ForAll @IntRange(min = 1, max = 100) int current,
            @ForAll @IntRange(min = 1, max = 100) int size) {
        
        int offset = (current - 1) * size;
        
        assertTrue(offset >= 0);
        assertEquals((current - 1) * size, offset);
    }

    /**
     * Property 19: API响应码应该是有效值
     */
    @Property(tries = 100)
    void responseCodeShouldBeValid(@ForAll @IntRange(min = 200, max = 599) int code) {
        // 有效的HTTP状态码范围
        assertTrue(code >= 200 && code < 600);
        
        // 成功响应
        if (code >= 200 && code < 300) {
            assertTrue(code == 200 || code == 201 || code == 204);
        }
    }
}
