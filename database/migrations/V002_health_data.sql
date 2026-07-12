-- =============================================
-- 健康数据管理模块数据库脚本
-- 负责人: 李滔
-- 创建时间: 2026-07-12
-- =============================================

-- 体重记录表（含BMI自动计算）
CREATE TABLE IF NOT EXISTS health_weight (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    weight DECIMAL(5,2) NOT NULL COMMENT '体重(kg)',
    height_cm DECIMAL(5,1) NOT NULL COMMENT '身高(cm)',
    bmi DECIMAL(4,1) GENERATED ALWAYS AS (
        ROUND(weight / (height_cm / 100) / (height_cm / 100), 1)
    ) STORED COMMENT 'BMI（自动计算）',
    record_date DATE NOT NULL COMMENT '记录日期',
    note VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_weight_user_date (user_id, record_date DESC)
) COMMENT '体重与BMI记录表';

-- 血压记录表
CREATE TABLE IF NOT EXISTS health_blood_pressure (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    systolic INT NOT NULL COMMENT '收缩压(mmHg)',
    diastolic INT NOT NULL COMMENT '舒张压(mmHg)',
    heart_rate_bpm INT COMMENT '心率(bpm)',
    record_date DATE NOT NULL COMMENT '记录日期',
    record_time TIME COMMENT '记录时间',
    note VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_bp_user_date (user_id, record_date DESC)
) COMMENT '血压记录表';

-- 血糖记录表
CREATE TABLE IF NOT EXISTS health_blood_sugar (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    sugar_level DECIMAL(4,1) NOT NULL COMMENT '血糖值(mmol/L)',
    measure_type VARCHAR(20) NOT NULL DEFAULT '空腹' COMMENT '测量类型：空腹/餐后/随机',
    record_date DATE NOT NULL COMMENT '记录日期',
    record_time TIME COMMENT '记录时间',
    note VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_bs_user_date (user_id, record_date DESC)
) COMMENT '血糖记录表';

-- 心率记录表
CREATE TABLE IF NOT EXISTS health_heart_rate (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    heart_rate INT NOT NULL COMMENT '心率(bpm)',
    measure_type VARCHAR(20) NOT NULL DEFAULT '静息' COMMENT '测量类型：静息/运动后/睡眠',
    record_date DATE NOT NULL COMMENT '记录日期',
    record_time TIME COMMENT '记录时间',
    note VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_hr_user_date (user_id, record_date DESC)
) COMMENT '心率记录表';
