-- ============================================
-- 设备管理系统 - MySQL 5.7+ 建表脚本
-- 数据库名: equipment_db
-- 字符集: utf8mb4
-- 排序规则: utf8mb4_unicode_ci
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS equipment_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE equipment_db;

-- ============================================
-- 一、系统管理模块
-- ============================================

-- 1.1 用户表
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(BCrypt加密)',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态:1启用,0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记:0未删除,1已删除',
    INDEX idx_username (username),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 1.2 角色表
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '角色描述',
    status TINYINT DEFAULT 1 COMMENT '状态:1启用,0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 1.3 权限表
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    type TINYINT COMMENT '类型:1菜单,2按钮',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    path VARCHAR(200) COMMENT '路由路径',
    icon VARCHAR(50) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_parent_id (parent_id),
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 1.4 用户角色关联表
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY uk_user_role (user_id, role_id),
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 1.5 角色权限关联表
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    UNIQUE KEY uk_role_permission (role_id, permission_id),
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- ============================================
-- 二、设备管理模块
-- ============================================

-- 2.1 设备分类表
DROP TABLE IF EXISTS equipment_category;
CREATE TABLE equipment_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    code VARCHAR(50) COMMENT '分类编码',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_parent_id (parent_id),
    INDEX idx_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备分类表';

-- 2.2 设备基本信息表
DROP TABLE IF EXISTS equipment_base;
CREATE TABLE equipment_base (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '设备ID',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '设备编号',
    name VARCHAR(100) NOT NULL COMMENT '设备名称',
    model VARCHAR(100) COMMENT '设备型号',
    category_id BIGINT COMMENT '分类ID',
    status TINYINT DEFAULT 1 COMMENT '状态:1运行,2停机,3维修,4报废',
    location VARCHAR(200) COMMENT '安装位置',
    department VARCHAR(100) COMMENT '所属部门',
    purchase_date DATE COMMENT '购置日期',
    manufacturer VARCHAR(100) COMMENT '制造商',
    specification TEXT COMMENT '规格参数(JSON)',
    qr_code VARCHAR(255) COMMENT '二维码路径',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_code (code),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status),
    INDEX idx_department (department)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备基本信息表';

-- 2.3 设备档案表
DROP TABLE IF EXISTS equipment_document;
CREATE TABLE equipment_document (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '档案ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    name VARCHAR(100) NOT NULL COMMENT '文件名称',
    file_path VARCHAR(255) NOT NULL COMMENT '文件路径',
    file_type VARCHAR(50) COMMENT '文件类型',
    file_size BIGINT COMMENT '文件大小(字节)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_equipment_id (equipment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备档案表';

-- ============================================
-- 三、点检管理模块
-- ============================================

-- 3.1 点检标准表
DROP TABLE IF EXISTS inspection_standard;
CREATE TABLE inspection_standard (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标准ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    item_name VARCHAR(100) NOT NULL COMMENT '点检项目名称',
    check_method VARCHAR(500) COMMENT '点检方法',
    standard_value VARCHAR(200) COMMENT '判定标准',
    cycle VARCHAR(20) COMMENT '点检周期:daily日检,weekly周检,monthly月检',
    sort_order INT DEFAULT 0 COMMENT '排序号',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_equipment_id (equipment_id),
    INDEX idx_cycle (cycle)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点检标准表';

-- 3.2 点检计划表
DROP TABLE IF EXISTS inspection_plan;
CREATE TABLE inspection_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '计划ID',
    name VARCHAR(100) NOT NULL COMMENT '计划名称',
    plan_type VARCHAR(20) NOT NULL COMMENT '计划类型:daily日检,weekly周检,monthly月检',
    equipment_ids VARCHAR(1000) COMMENT '设备ID列表(逗号分隔)',
    assignee_id BIGINT COMMENT '负责人ID',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    status TINYINT DEFAULT 1 COMMENT '状态:1启用,0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_plan_type (plan_type),
    INDEX idx_assignee_id (assignee_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点检计划表';

-- 3.3 点检任务表
DROP TABLE IF EXISTS inspection_task;
CREATE TABLE inspection_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    plan_id BIGINT COMMENT '计划ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    assignee_id BIGINT COMMENT '执行人ID',
    task_date DATE NOT NULL COMMENT '任务日期',
    status TINYINT DEFAULT 0 COMMENT '状态:0待执行,1已完成,2已过期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_plan_id (plan_id),
    INDEX idx_equipment_id (equipment_id),
    INDEX idx_assignee_id (assignee_id),
    INDEX idx_task_date (task_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点检任务表';

-- 3.4 点检记录表
DROP TABLE IF EXISTS inspection_record;
CREATE TABLE inspection_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    task_id BIGINT COMMENT '任务ID',
    plan_id BIGINT COMMENT '计划ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    standard_id BIGINT COMMENT '标准ID',
    item_name VARCHAR(100) COMMENT '点检项目',
    result TINYINT COMMENT '结果:1正常,2异常',
    result_value VARCHAR(200) COMMENT '检测值',
    remark VARCHAR(500) COMMENT '备注',
    images VARCHAR(1000) COMMENT '图片URL(逗号分隔)',
    inspector_id BIGINT COMMENT '点检人ID',
    inspector_name VARCHAR(50) COMMENT '点检人姓名',
    check_time DATETIME COMMENT '点检时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_task_id (task_id),
    INDEX idx_equipment_id (equipment_id),
    INDEX idx_inspector_id (inspector_id),
    INDEX idx_check_time (check_time),
    INDEX idx_result (result)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='点检记录表';

-- ============================================
-- 四、巡检管理模块
-- ============================================

-- 4.1 巡检路线表
DROP TABLE IF EXISTS patrol_route;
CREATE TABLE patrol_route (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '路线ID',
    name VARCHAR(100) NOT NULL COMMENT '路线名称',
    description VARCHAR(500) COMMENT '路线描述',
    checkpoints TEXT COMMENT '检查点JSON数据',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检路线表';

-- 4.2 巡检任务表
DROP TABLE IF EXISTS patrol_task;
CREATE TABLE patrol_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    route_id BIGINT NOT NULL COMMENT '路线ID',
    assignee_id BIGINT COMMENT '执行人ID',
    task_date DATE NOT NULL COMMENT '任务日期',
    status TINYINT DEFAULT 0 COMMENT '状态:0待执行,1执行中,2已完成,3已过期',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_route_id (route_id),
    INDEX idx_assignee_id (assignee_id),
    INDEX idx_task_date (task_date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检任务表';

-- 4.3 巡检记录表
DROP TABLE IF EXISTS patrol_record;
CREATE TABLE patrol_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    checkpoint_id VARCHAR(50) COMMENT '检查点ID',
    checkpoint_name VARCHAR(100) COMMENT '检查点名称',
    status TINYINT DEFAULT 1 COMMENT '状态:1正常,2异常',
    remark VARCHAR(500) COMMENT '备注',
    images VARCHAR(1000) COMMENT '图片URL',
    latitude DECIMAL(10,7) COMMENT '纬度',
    longitude DECIMAL(10,7) COMMENT '经度',
    check_time DATETIME COMMENT '签到时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_task_id (task_id),
    INDEX idx_check_time (check_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡检记录表';

-- ============================================
-- 五、维修管理模块
-- ============================================

-- 5.1 维修工单表
DROP TABLE IF EXISTS maintenance_order;
CREATE TABLE maintenance_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '工单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '工单编号',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    fault_type TINYINT COMMENT '故障类型:1机械故障,2电气故障,3其他',
    fault_desc VARCHAR(1000) COMMENT '故障描述',
    status TINYINT DEFAULT 1 COMMENT '状态:1待派发,2待接单,3维修中,4待验收,5已完成',
    priority TINYINT DEFAULT 2 COMMENT '优先级:1紧急,2一般,3低',
    reporter_id BIGINT COMMENT '报修人ID',
    reporter_name VARCHAR(50) COMMENT '报修人姓名',
    assignee_id BIGINT COMMENT '维修人ID',
    assignee_name VARCHAR(50) COMMENT '维修人姓名',
    repair_desc VARCHAR(1000) COMMENT '维修措施',
    accept_time DATETIME COMMENT '接单时间',
    start_time DATETIME COMMENT '开始维修时间',
    finish_time DATETIME COMMENT '完成时间',
    check_time DATETIME COMMENT '验收时间',
    checker_id BIGINT COMMENT '验收人ID',
    labor_cost DECIMAL(10,2) DEFAULT 0.00 COMMENT '人工成本',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_equipment_id (equipment_id),
    INDEX idx_status (status),
    INDEX idx_assignee_id (assignee_id),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='维修工单表';

-- 5.2 备品备件表
DROP TABLE IF EXISTS spare_part;
CREATE TABLE spare_part (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '备件ID',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '备件编号',
    name VARCHAR(100) NOT NULL COMMENT '备件名称',
    model VARCHAR(100) COMMENT '规格型号',
    unit VARCHAR(20) COMMENT '单位',
    stock_qty INT DEFAULT 0 COMMENT '库存数量',
    safe_qty INT DEFAULT 0 COMMENT '安全库存',
    price DECIMAL(10,2) COMMENT '单价',
    location VARCHAR(100) COMMENT '存放位置',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    INDEX idx_code (code),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='备品备件表';

-- 5.3 备件消耗记录表
DROP TABLE IF EXISTS part_consumption;
CREATE TABLE part_consumption (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    order_id BIGINT NOT NULL COMMENT '工单ID',
    part_id BIGINT NOT NULL COMMENT '备件ID',
    part_name VARCHAR(100) COMMENT '备件名称',
    quantity INT NOT NULL COMMENT '消耗数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    total_price DECIMAL(10,2) COMMENT '总价',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_order_id (order_id),
    INDEX idx_part_id (part_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='备件消耗记录表';

-- 5.4 库存预警记录表
DROP TABLE IF EXISTS stock_alert;
CREATE TABLE stock_alert (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预警ID',
    part_id BIGINT NOT NULL COMMENT '备件ID',
    part_name VARCHAR(100) COMMENT '备件名称',
    current_qty INT COMMENT '当前库存',
    safe_qty INT COMMENT '安全库存',
    status TINYINT DEFAULT 0 COMMENT '状态:0未处理,1已处理',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_part_id (part_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='库存预警记录表';

-- ============================================
-- 六、初始化数据
-- ============================================

-- 6.1 初始化管理员账号 (密码: admin123)
-- BCrypt 加密后的密码
INSERT INTO sys_user (username, password, real_name, phone, status) VALUES 
('admin', '$2a$10$pAuvsZkp12GgrTGKLIn.duZFGDmNGuEZLiCimPpdgDxF4.KQqfUb6', '系统管理员', '13800000000', 1),
('maintainer1', '$2a$10$pAuvsZkp12GgrTGKLIn.duZFGDmNGuEZLiCimPpdgDxF4.KQqfUb6', '张维修', '13800000001', 1),
('maintainer2', '$2a$10$pAuvsZkp12GgrTGKLIn.duZFGDmNGuEZLiCimPpdgDxF4.KQqfUb6', '李维修', '13800000002', 1),
('inspector1', '$2a$10$pAuvsZkp12GgrTGKLIn.duZFGDmNGuEZLiCimPpdgDxF4.KQqfUb6', '王点检', '13800000003', 1),
('patroller1', '$2a$10$pAuvsZkp12GgrTGKLIn.duZFGDmNGuEZLiCimPpdgDxF4.KQqfUb6', '赵巡检', '13800000004', 1);

-- 6.2 初始化角色
INSERT INTO sys_role (name, code, description) VALUES 
('管理员', 'ADMIN', '系统管理员,拥有所有权限'),
('设备管理员', 'EQUIPMENT_ADMIN', '设备管理员,负责设备台账管理'),
('点检员', 'INSPECTOR', '点检人员,负责设备点检工作'),
('维修员', 'MAINTAINER', '维修人员,负责设备维修工作'),
('巡检员', 'PATROLLER', '巡检人员,负责设备巡检工作');

-- 6.3 关联用户角色
INSERT INTO sys_user_role (user_id, role_id) VALUES 
(1, 1),  -- admin -> 管理员
(2, 4),  -- maintainer1 -> 维修员
(3, 4),  -- maintainer2 -> 维修员
(4, 3),  -- inspector1 -> 点检员
(5, 5);  -- patroller1 -> 巡检员

-- 6.4 初始化设备分类
INSERT INTO equipment_category (name, code, sort_order) VALUES 
('压力机', 'PRESS', 1),
('加热炉', 'FURNACE', 2),
('冷却装置', 'COOLER', 3),
('输送设备', 'CONVEYOR', 4),
('检测设备', 'DETECTOR', 5),
('包装设备', 'PACKER', 6);

-- 6.5 初始化权限菜单
INSERT INTO sys_permission (name, code, type, parent_id, path, icon, sort_order) VALUES 
('首页', 'dashboard', 1, 0, '/dashboard', 'HomeFilled', 1),
('设备管理', 'equipment', 1, 0, '/equipment', 'Monitor', 2),
('设备台账', 'equipment:list', 1, 2, '/equipment/list', '', 1),
('设备分类', 'equipment:category', 1, 2, '/equipment/category', '', 2),
('点检管理', 'inspection', 1, 0, '/inspection', 'Checked', 3),
('点检计划', 'inspection:plan', 1, 5, '/inspection/plan', '', 1),
('点检记录', 'inspection:record', 1, 5, '/inspection/record', '', 2),
('点检标准', 'inspection:standard', 1, 5, '/inspection/standard', '', 3),
('巡检管理', 'patrol', 1, 0, '/patrol', 'Location', 4),
('巡检路线', 'patrol:route', 1, 9, '/patrol/route', '', 1),
('巡检任务', 'patrol:task', 1, 9, '/patrol/task', '', 2),
('维修管理', 'maintenance', 1, 0, '/maintenance', 'Tools', 5),
('维修工单', 'maintenance:order', 1, 12, '/maintenance/order', '', 1),
('备件管理', 'maintenance:part', 1, 12, '/maintenance/part', '', 2),
('报表统计', 'report', 1, 0, '/report', 'DataAnalysis', 6),
('系统管理', 'system', 1, 0, '/system', 'Setting', 7),
('用户管理', 'system:user', 1, 16, '/system/user', '', 1),
('角色管理', 'system:role', 1, 16, '/system/role', '', 2);

-- 6.6 初始化角色权限
-- 管理员拥有所有权限
INSERT INTO sys_role_permission (role_id, permission_id) 
SELECT 1, id FROM sys_permission;

-- 设备管理员权限 (首页 + 设备管理)
INSERT INTO sys_role_permission (role_id, permission_id) 
SELECT 2, id FROM sys_permission WHERE code IN ('dashboard', 'equipment', 'equipment:list', 'equipment:category');

-- 点检员权限 (首页 + 点检管理)
INSERT INTO sys_role_permission (role_id, permission_id) 
SELECT 3, id FROM sys_permission WHERE code IN ('dashboard', 'inspection', 'inspection:plan', 'inspection:record', 'inspection:standard');

-- 维修员权限 (首页 + 维修管理)
INSERT INTO sys_role_permission (role_id, permission_id) 
SELECT 4, id FROM sys_permission WHERE code IN ('dashboard', 'maintenance', 'maintenance:order', 'maintenance:part');

-- 巡检员权限 (首页 + 巡检管理)
INSERT INTO sys_role_permission (role_id, permission_id) 
SELECT 5, id FROM sys_permission WHERE code IN ('dashboard', 'patrol', 'patrol:route', 'patrol:task');

-- 6.7 初始化示例设备数据
INSERT INTO equipment_base (code, name, model, category_id, status, location, department, manufacturer) VALUES 
('EQ-001', '1号压力机', 'YP-500T', 1, 1, 'A车间-1号工位', '生产部', '济南二机床'),
('EQ-002', '2号压力机', 'YP-500T', 1, 1, 'A车间-2号工位', '生产部', '济南二机床'),
('EQ-003', '1号加热炉', 'RJJ-75-9', 2, 1, 'B车间-热处理区', '生产部', '洛阳万基'),
('EQ-004', '冷却塔', 'LCT-100', 3, 1, '厂区北侧', '设备部', '山东金光'),
('EQ-005', '皮带输送机', 'TD75-500', 4, 1, 'C车间-包装线', '生产部', '新乡威猛');

-- 6.8 初始化示例备件数据
INSERT INTO spare_part (code, name, model, unit, stock_qty, safe_qty, price, location) VALUES 
('SP-001', '液压油', '46#抗磨液压油', '桶', 20, 5, 280.00, 'A库-1排'),
('SP-002', '密封圈', 'O型-50*3.5', '个', 100, 20, 5.50, 'A库-2排'),
('SP-003', '轴承', '6205-2RS', '个', 30, 10, 35.00, 'B库-1排'),
('SP-004', '皮带', 'B-2000', '条', 10, 3, 120.00, 'B库-2排'),
('SP-005', '接触器', 'CJX2-25', '个', 15, 5, 85.00, 'C库-电气区');
