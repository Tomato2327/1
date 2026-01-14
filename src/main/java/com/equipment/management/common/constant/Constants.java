package com.equipment.management.common.constant;

/**
 * 系统常量
 */
public class Constants {

    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * Token请求头
     */
    public static final String TOKEN_HEADER = "Authorization";

    /**
     * 设备状态
     */
    public static class EquipmentStatus {
        public static final int RUNNING = 1;      // 运行
        public static final int STOPPED = 2;      // 停机
        public static final int MAINTENANCE = 3;  // 维修
        public static final int SCRAPPED = 4;     // 报废
    }

    /**
     * 点检结果
     */
    public static class InspectionResult {
        public static final int NORMAL = 1;    // 正常
        public static final int ABNORMAL = 2;  // 异常
    }

    /**
     * 维修工单状态
     */
    public static class MaintenanceStatus {
        public static final int PENDING_ASSIGN = 1;   // 待派发
        public static final int PENDING_ACCEPT = 2;   // 待接单
        public static final int IN_PROGRESS = 3;      // 维修中
        public static final int PENDING_CHECK = 4;    // 待验收
        public static final int COMPLETED = 5;        // 已完成
    }

    /**
     * 优先级
     */
    public static class Priority {
        public static final int URGENT = 1;   // 紧急
        public static final int NORMAL = 2;   // 一般
        public static final int LOW = 3;      // 低
    }
}
