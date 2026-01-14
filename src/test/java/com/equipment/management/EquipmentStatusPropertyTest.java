package com.equipment.management;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 设备状态转换属性测试
 * Property 4: 设备状态转换有效性
 */
class EquipmentStatusPropertyTest {

    // 设备状态: 1运行, 2停机, 3维修, 4报废
    private static final Set<Integer> VALID_STATUSES = Set.of(1, 2, 3, 4);
    
    // 有效的状态转换
    private static final Map<Integer, Set<Integer>> VALID_TRANSITIONS = Map.of(
            1, Set.of(2, 3, 4),  // 运行 -> 停机/维修/报废
            2, Set.of(1, 3, 4),  // 停机 -> 运行/维修/报废
            3, Set.of(1, 2, 4),  // 维修 -> 运行/停机/报废
            4, Set.of()          // 报废 -> 无法转换
    );

    /**
     * Property 4: 状态转换应该遵循有效的转换规则
     */
    @Property(tries = 100)
    void statusTransitionShouldBeValid(
            @ForAll @IntRange(min = 1, max = 4) int fromStatus,
            @ForAll @IntRange(min = 1, max = 4) int toStatus) {
        
        boolean isValidTransition = VALID_TRANSITIONS.get(fromStatus).contains(toStatus);
        
        if (fromStatus == toStatus) {
            // 相同状态不需要转换
            assertTrue(true);
        } else if (fromStatus == 4) {
            // 报废状态不能转换
            assertFalse(isValidTransition);
        } else {
            // 其他状态可以转换到非当前状态
            assertTrue(isValidTransition || toStatus == fromStatus);
        }
    }

    /**
     * Property 5: 所有状态值都应该在有效范围内
     */
    @Property(tries = 100)
    void statusShouldBeInValidRange(@ForAll @IntRange(min = 1, max = 4) int status) {
        assertTrue(VALID_STATUSES.contains(status));
    }

    /**
     * Property 6: 无效状态值应该被拒绝
     */
    @Property(tries = 100)
    void invalidStatusShouldBeRejected(
            @ForAll @IntRange(min = -100, max = 0) int invalidStatus) {
        assertFalse(VALID_STATUSES.contains(invalidStatus));
    }

    @Property(tries = 100)
    void invalidStatusAboveRangeShouldBeRejected(
            @ForAll @IntRange(min = 5, max = 100) int invalidStatus) {
        assertFalse(VALID_STATUSES.contains(invalidStatus));
    }
}
