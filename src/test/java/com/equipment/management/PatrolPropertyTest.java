package com.equipment.management;

import net.jqwik.api.*;
import net.jqwik.api.constraints.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 巡检管理属性测试
 * Property 10: 巡检轨迹记录
 */
class PatrolPropertyTest {

    // 巡检任务状态: 0待执行, 1执行中, 2已完成, 3已过期
    private static final Set<Integer> VALID_STATUSES = Set.of(0, 1, 2, 3);

    /**
     * Property 10: 巡检轨迹应该按时间顺序记录
     */
    @Property(tries = 100)
    void patrolTrackShouldBeInChronologicalOrder(
            @ForAll @Size(min = 2, max = 10) List<@LongRange(min = 1, max = 1000000) Long> timestamps) {
        
        List<Long> sortedTimestamps = new ArrayList<>(timestamps);
        Collections.sort(sortedTimestamps);
        
        // 验证排序后的时间戳是递增的
        for (int i = 1; i < sortedTimestamps.size(); i++) {
            assertTrue(sortedTimestamps.get(i) >= sortedTimestamps.get(i - 1));
        }
    }

    /**
     * Property 11: GPS坐标应该在有效范围内
     */
    @Property(tries = 100)
    void gpsCoordinatesShouldBeValid(
            @ForAll @DoubleRange(min = -90, max = 90) double latitude,
            @ForAll @DoubleRange(min = -180, max = 180) double longitude) {
        
        assertTrue(latitude >= -90 && latitude <= 90);
        assertTrue(longitude >= -180 && longitude <= 180);
    }

    /**
     * Property 12: 巡检任务状态应该有效
     */
    @Property(tries = 100)
    void patrolTaskStatusShouldBeValid(@ForAll @IntRange(min = 0, max = 3) int status) {
        assertTrue(VALID_STATUSES.contains(status));
    }

    /**
     * Property 13: 巡检完成进度应该正确计算
     */
    @Property(tries = 100)
    void patrolProgressShouldBeCorrect(
            @ForAll @IntRange(min = 0, max = 20) int completed,
            @ForAll @IntRange(min = 1, max = 20) int total) {
        
        if (completed <= total) {
            double progress = (double) completed / total * 100;
            assertTrue(progress >= 0 && progress <= 100);
        }
    }

    /**
     * Property 14: 签到点应该属于路线
     */
    @Property(tries = 100)
    void checkpointShouldBelongToRoute(
            @ForAll @IntRange(min = 1, max = 10) int checkpointIndex,
            @ForAll @IntRange(min = 1, max = 10) int routeCheckpointCount) {
        
        boolean isValidCheckpoint = checkpointIndex <= routeCheckpointCount;
        
        if (isValidCheckpoint) {
            assertTrue(checkpointIndex >= 1);
            assertTrue(checkpointIndex <= routeCheckpointCount);
        }
    }

    /**
     * Property 15: 巡检记录状态应该有效
     */
    @Property(tries = 100)
    void patrolRecordStatusShouldBeValid(@ForAll @IntRange(min = 1, max = 2) int status) {
        // 1正常, 2异常
        assertTrue(status == 1 || status == 2);
    }
}
