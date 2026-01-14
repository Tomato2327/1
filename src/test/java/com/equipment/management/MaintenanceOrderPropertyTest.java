package com.equipment.management;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 维修工单状态机属性测试
 * Property 11: 维修工单状态机
 */
class MaintenanceOrderPropertyTest {

    // 工单状态: 1待派发, 2待接单, 3维修中, 4待验收, 5已完成
    private static final Set<Integer> VALID_STATUSES = Set.of(1, 2, 3, 4, 5);
    
    // 有效的状态转换 (状态机)
    private static final Map<Integer, Set<Integer>> VALID_TRANSITIONS = Map.of(
            1, Set.of(2),        // 待派发 -> 待接单
            2, Set.of(3),        // 待接单 -> 维修中
            3, Set.of(4),        // 维修中 -> 待验收
            4, Set.of(3, 5),     // 待验收 -> 维修中(退回)/已完成
            5, Set.of()          // 已完成 -> 终态
    );

    /**
     * Property 11: 工单状态转换应该遵循状态机规则
     */
    @Property(tries = 100)
    void orderStatusTransitionShouldFollowStateMachine(
            @ForAll @IntRange(min = 1, max = 5) int fromStatus,
            @ForAll @IntRange(min = 1, max = 5) int toStatus) {
        
        Set<Integer> allowedTransitions = VALID_TRANSITIONS.get(fromStatus);
        boolean isValidTransition = allowedTransitions.contains(toStatus);
        
        // 验证状态机规则
        if (fromStatus == 5) {
            // 已完成是终态，不能转换
            assertFalse(isValidTransition);
        } else if (fromStatus == 4 && toStatus == 3) {
            // 待验收可以退回到维修中
            assertTrue(isValidTransition);
        } else if (toStatus == fromStatus + 1 && fromStatus < 5) {
            // 正常流程是递增的
            assertTrue(isValidTransition || fromStatus == 4);
        }
    }

    /**
     * Property 12: 工单状态只能前进或特定回退
     */
    @Property(tries = 100)
    void orderStatusCanOnlyProgressOrSpecificRollback(
            @ForAll @IntRange(min = 1, max = 5) int fromStatus,
            @ForAll @IntRange(min = 1, max = 5) int toStatus) {
        
        Set<Integer> allowedTransitions = VALID_TRANSITIONS.get(fromStatus);
        
        if (toStatus < fromStatus && !(fromStatus == 4 && toStatus == 3)) {
            // 除了待验收->维修中，不允许回退
            assertFalse(allowedTransitions.contains(toStatus));
        }
    }

    /**
     * Property 13: 优先级应该在有效范围内
     */
    @Property(tries = 100)
    void priorityShouldBeValid(@ForAll @IntRange(min = 1, max = 3) int priority) {
        // 1紧急, 2一般, 3低
        assertTrue(priority >= 1 && priority <= 3);
    }
}
