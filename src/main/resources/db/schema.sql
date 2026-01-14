-- 设备管理系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS equipment_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE equipment_db;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(255) COMMENT '头像',
    status TINYINT DEFAULT 1 COMMENT '状态:1启用,0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色ID',
    name VARCHAR(50) NOT NULL COMMENT '角色名称',
    code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    description VARCHAR(200) COMMENT '描述',
    status TINYINT DEFAULT 1 COMMENT '状态:1启用,0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS sys_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
    name VARCHAR(50) NOT NULL COMMENT '权限名称',
    code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限编码',
    type TINYINT COMMENT '类型:1菜单,2按钮',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    path VARCHAR(200) COMMENT '路由路径',
    icon VARCHAR(50) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    UNIQUE KEY uk_user_role (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS sys_role_permission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';

-- 设备分类表
CREATE TABLE IF NOT EXISTS equipment_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    code VARCHAR(50) COMMENT '分类编码',
    parent_id BIGINT DEFAULT 0 COMMENT '父级ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备分类表';

-- 设备基本信息表
CREATE TABLE IF NOT EXISTS equipment_base (
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
    specification TEXT COMMENT '规格参数',
    qr_code VARCHAR(255) COMMENT '二维码路径',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备基本信息表';

-- 点检标准表
CREATE TABLE IF NOT EXISTS inspection_standard (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '标准ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    item_name VARCHAR(100) NOT NULL COMMENT '点检项目',
    check_method VARCHAR(500) COMMENT '点检方法',
    standard_value VARCHAR(200) COMMENT '判定标准',
    cycle VARCHAR(20) COMMENT '点检周期:daily,weekly,monthly',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点检标准表';

-- 点检计划表
CREATE TABLE IF NOT EXISTS inspection_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '计划ID',
    name VARCHAR(100) NOT NULL COMMENT '计划名称',
    plan_type VARCHAR(20) NOT NULL COMMENT '计划类型:daily,weekly,monthly',
    equipment_ids VARCHAR(1000) COMMENT '设备ID列表,逗号分隔',
    assignee_id BIGINT COMMENT '负责人ID',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    status TINYINT DEFAULT 1 COMMENT '状态:1启用,0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点检计划表';

-- 点检任务表
CREATE TABLE IF NOT EXISTS inspection_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    plan_id BIGINT COMMENT '计划ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    assignee_id BIGINT COMMENT '执行人ID',
    task_date DATE NOT NULL COMMENT '任务日期',
    status TINYINT DEFAULT 0 COMMENT '状态:0待执行,1已完成,2已过期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点检任务表';

-- 点检记录表
CREATE TABLE IF NOT EXISTS inspection_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    task_id BIGINT COMMENT '任务ID',
    plan_id BIGINT COMMENT '计划ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    standard_id BIGINT COMMENT '标准ID',
    item_name VARCHAR(100) COMMENT '点检项目',
    result TINYINT COMMENT '结果:1正常,2异常',
    result_value VARCHAR(200) COMMENT '检测值',
    remark VARCHAR(500) COMMENT '备注',
    images VARCHAR(1000) COMMENT '图片URL,逗号分隔',
    inspector_id BIGINT COMMENT '点检人ID',
    inspector_name VARCHAR(50) COMMENT '点检人姓名',
    check_time DATETIME COMMENT '点检时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点检记录表';

-- 巡检路线表
CREATE TABLE IF NOT EXISTS patrol_route (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '路线ID',
    name VARCHAR(100) NOT NULL COMMENT '路线名称',
    description VARCHAR(500) COMMENT '描述',
    checkpoints TEXT COMMENT '检查点JSON',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检路线表';

-- 巡检任务表
CREATE TABLE IF NOT EXISTS patrol_task (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    route_id BIGINT NOT NULL COMMENT '路线ID',
    assignee_id BIGINT COMMENT '执行人ID',
    task_date DATE NOT NULL COMMENT '任务日期',
    status TINYINT DEFAULT 0 COMMENT '状态:0待执行,1执行中,2已完成,3已过期',
    start_time DATETIME COMMENT '开始时间',
    end_time DATETIME COMMENT '结束时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检任务表';

-- 巡检记录表
CREATE TABLE IF NOT EXISTS patrol_record (
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
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='巡检记录表';

-- 维修工单表
CREATE TABLE IF NOT EXISTS maintenance_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '工单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '工单编号',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    fault_type TINYINT COMMENT '故障类型',
    fault_desc VARCHAR(1000) COMMENT '故障描述',
    status TINYINT DEFAULT 1 COMMENT '状态:1待派发,2待接单,3维修中,4待验收,5已完成',
    priority TINYINT DEFAULT 2 COMMENT '优先级:1紧急,2一般,3低',
    reporter_id BIGINT COMMENT '报修人ID',
    reporter_name VARCHAR(50) COMMENT '报修人姓名',
    assignee_id BIGINT COMMENT '维修人ID',
    assignee_name VARCHAR(50) COMMENT '维修人姓名',
    repair_desc VARCHAR(1000) COMMENT '维修措施',
    accept_time DATETIME COMMENT '接单时间',
    start_time DATETIME COMMENT '开始时间',
    finish_time DATETIME COMMENT '完成时间',
    check_time DATETIME COMMENT '验收时间',
    checker_id BIGINT COMMENT '验收人ID',
    labor_cost DECIMAL(10,2) DEFAULT 0 COMMENT '人工成本',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修工单表';

-- 备品备件表
CREATE TABLE IF NOT EXISTS spare_part (
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
    deleted TINYINT DEFAULT 0 COMMENT '删除标记'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备品备件表';

-- 备件消耗记录表
CREATE TABLE IF NOT EXISTS part_consumption (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '记录ID',
    order_id BIGINT NOT NULL COMMENT '工单ID',
    part_id BIGINT NOT NULL COMMENT '备件ID',
    part_name VARCHAR(100) COMMENT '备件名称',
    quantity INT NOT NULL COMMENT '消耗数量',
    unit_price DECIMAL(10,2) COMMENT '单价',
    total_price DECIMAL(10,2) COMMENT '总价',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备件消耗记录表';

-- 库存预警记录表
CREATE TABLE IF NOT EXISTS stock_alert (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '预警ID',
    part_id BIGINT NOT NULL COMMENT '备件ID',
    part_name VARCHAR(100) COMMENT '备件名称',
    current_qty INT COMMENT '当前库存',
    safe_qty INT COMMENT '安全库存',
    status TINYINT DEFAULT 0 COMMENT '状态:0未处理,1已处理',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存预警记录表';

-- 设备档案表
CREATE TABLE IF NOT EXISTS equipment_document (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '档案ID',
    equipment_id BIGINT NOT NULL COMMENT '设备ID',
    name VARCHAR(100) NOT NULL COMMENT '文件名称',
    file_path VARCHAR(255) NOT NULL COMMENT '文件路径',
    file_type VARCHAR(50) COMMENT '文件类型',
    file_size BIGINT COMMENT '文件大小',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备档案表';

-- 初始化管理员账号 (密码: admin123)
INSERT INTO sys_user (username, password, real_name, status) VALUES 
('admin', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '系统管理员', 1);

-- 初始化角色
INSERT INTO sys_role (name, code, description) VALUES 
('管理员', 'ADMIN', '系统管理员'),
('设备管理员', 'EQUIPMENT_ADMIN', '设备管理员'),
('点检员', 'INSPECTOR', '点检人员'),
('维修员', 'MAINTAINER', '维修人员');

-- 关联管理员角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 初始化设备分类
INSERT INTO equipment_category (name, code, sort_order) VALUES 
('压力机', 'PRESS', 1),
('加热炉', 'FURNACE', 2),
('冷却装置', 'COOLER', 3),
('输送设备', 'CONVEYOR', 4),
('检测设备', 'DETECTOR', 5);
