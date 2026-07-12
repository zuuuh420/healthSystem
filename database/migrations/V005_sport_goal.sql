-- =============================================
-- 运动与目标管理模块数据库脚本
-- 负责人: 张森栋
-- 创建时间: 2026-07-12
-- =============================================

-- 运动类型表
CREATE TABLE IF NOT EXISTS sport_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL COMMENT '运动名称',
    category VARCHAR(30) COMMENT '分类：有氧/无氧/拉伸等',
    calories_per_minute DECIMAL(5,2) COMMENT '每分钟消耗卡路里',
    icon VARCHAR(200) COMMENT '图标地址',
    status TINYINT DEFAULT 1 COMMENT '1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) COMMENT '运动类型表';

-- 运动记录表
CREATE TABLE IF NOT EXISTS sport_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    sport_type_id BIGINT NOT NULL,
    duration_minutes INT NOT NULL COMMENT '运动时长(分钟)',
    calories_burned DECIMAL(8,2) COMMENT '消耗卡路里',
    sport_date DATE NOT NULL COMMENT '运动日期',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_date (user_id, sport_date),
    INDEX idx_sport_type (sport_type_id)
) COMMENT '运动记录表';

-- 运动计划表
CREATE TABLE IF NOT EXISTS sport_plan (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    sport_type_id BIGINT NOT NULL,
    plan_name VARCHAR(100) NOT NULL COMMENT '计划名称',
    frequency VARCHAR(30) COMMENT '频率：每天/每周几次',
    target_minutes INT COMMENT '目标时长(分钟)',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    status TINYINT DEFAULT 1 COMMENT '1启用 0禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id)
) COMMENT '运动计划表';

-- 健康目标表
CREATE TABLE IF NOT EXISTS health_goal (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    goal_type VARCHAR(30) NOT NULL COMMENT '目标类型：weight/exercise/steps等',
    goal_name VARCHAR(100) NOT NULL COMMENT '目标名称',
    target_value DECIMAL(10,2) NOT NULL COMMENT '目标值',
    current_value DECIMAL(10,2) DEFAULT 0 COMMENT '当前值',
    unit VARCHAR(20) COMMENT '单位：kg/分钟/步等',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '结束日期',
    status TINYINT DEFAULT 0 COMMENT '0进行中 1已完成 2已过期',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user (user_id),
    INDEX idx_status (status)
) COMMENT '健康目标表';

-- 每日打卡表
CREATE TABLE IF NOT EXISTS daily_checkin (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    checkin_date DATE NOT NULL COMMENT '打卡日期',
    sport_record_id BIGINT COMMENT '关联的运动记录ID',
    checkin_type VARCHAR(20) DEFAULT 'sport' COMMENT '打卡类型：sport/diet/general',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_date_type (user_id, checkin_date, checkin_type)
) COMMENT '每日打卡表';

-- 初始运动类型数据
INSERT INTO sport_type (name, category, calories_per_minute) VALUES
('跑步', '有氧', 10.00),
('快走', '有氧', 5.00),
('游泳', '有氧', 8.00),
('骑自行车', '有氧', 7.50),
('俯卧撑', '无氧', 6.00),
('仰卧起坐', '无氧', 5.00),
('深蹲', '无氧', 7.00),
('瑜伽', '拉伸', 3.50),
('跳绳', '有氧', 12.00),
('篮球', '有氧', 9.00),
('羽毛球', '有氧', 7.00),
('乒乓球', '有氧', 5.50),
('引体向上', '无氧', 8.00),
('平板支撑', '无氧', 4.00),
('开合跳', '有氧', 10.00);
