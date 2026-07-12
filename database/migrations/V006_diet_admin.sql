-- ========================================
-- 迁移脚本 V006: 饮食管理 & 管理端相关表
-- 负责人：刘子豪
-- ========================================

USE health_system;

-- 食物库表
CREATE TABLE IF NOT EXISTS food (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL COMMENT '食物名称',
    category VARCHAR(50) COMMENT '分类',
    calories_per_100g DOUBLE COMMENT '每100g热量(kcal)',
    protein_per_100g DOUBLE COMMENT '每100g蛋白质(g)',
    fat_per_100g DOUBLE COMMENT '每100g脂肪(g)',
    carbs_per_100g DOUBLE COMMENT '每100g碳水(g)',
    unit VARCHAR(20) DEFAULT 'g' COMMENT '单位',
    image_url VARCHAR(255) COMMENT '图片URL',
    description VARCHAR(500) COMMENT '描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='食物库';

-- 饮食记录表
CREATE TABLE IF NOT EXISTS diet_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    record_date DATE NOT NULL COMMENT '记录日期',
    meal_type VARCHAR(20) COMMENT '餐次: breakfast/lunch/dinner/snack',
    food_id BIGINT NOT NULL COMMENT '食物ID',
    food_name VARCHAR(100) COMMENT '食物名称',
    quantity_g DOUBLE COMMENT '食用克数',
    calories DOUBLE COMMENT '摄入热量(kcal)',
    protein DOUBLE COMMENT '蛋白质(g)',
    fat DOUBLE COMMENT '脂肪(g)',
    carbs DOUBLE COMMENT '碳水(g)',
    note VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_date (user_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='饮食记录';

-- 健康数据记录表
CREATE TABLE IF NOT EXISTS health_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    record_date DATE NOT NULL COMMENT '记录日期',
    weight DOUBLE COMMENT '体重(kg)',
    bmi DOUBLE COMMENT 'BMI指数',
    systolic_pressure INT COMMENT '收缩压(mmHg)',
    diastolic_pressure INT COMMENT '舒张压(mmHg)',
    blood_sugar DOUBLE COMMENT '血糖(mmol/L)',
    heart_rate INT COMMENT '心率(次/分)',
    note VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_date (user_id, record_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='健康数据记录';

-- ========================================
-- 初始食物数据
-- ========================================
INSERT INTO food (name, category, calories_per_100g, protein_per_100g, fat_per_100g, carbs_per_100g, unit, description) VALUES
('白米饭', '主食', 116, 2.6, 0.3, 25.9, 'g', '蒸熟白米饭'),
('馒头', '主食', 223, 7.0, 1.1, 44.2, 'g', '小麦粉馒头'),
('全麦面包', '主食', 246, 10.9, 4.3, 43.6, 'g', '全麦面包片'),
('面条', '主食', 110, 3.3, 0.3, 22.2, 'g', '煮面条'),
('鸡蛋', '蛋奶', 144, 13.3, 8.8, 2.8, 'g', '熟鸡蛋(去壳)'),
('牛奶', '蛋奶', 54, 3.0, 3.2, 3.4, 'ml', '全脂牛奶'),
('酸奶', '蛋奶', 72, 2.5, 2.7, 9.3, 'ml', '原味酸奶'),
('鸡胸肉', '肉类', 133, 31.0, 1.9, 0.5, 'g', '去皮鸡胸肉'),
('猪肉(瘦)', '肉类', 143, 20.3, 6.2, 1.5, 'g', '瘦猪肉'),
('牛肉(瘦)', '肉类', 125, 20.2, 4.2, 0.2, 'g', '瘦牛肉'),
('三文鱼', '肉类', 139, 17.2, 7.8, 0, 'g', '三文鱼'),
('西兰花', '蔬菜', 34, 2.8, 0.4, 6.6, 'g', '新鲜西兰花'),
('西红柿', '蔬菜', 18, 0.9, 0.2, 3.5, 'g', '新鲜西红柿'),
('黄瓜', '蔬菜', 15, 0.8, 0.2, 2.9, 'g', '新鲜黄瓜'),
('菠菜', '蔬菜', 23, 2.9, 0.4, 3.6, 'g', '新鲜菠菜'),
('胡萝卜', '蔬菜', 37, 1.0, 0.2, 8.8, 'g', '新鲜胡萝卜'),
('苹果', '水果', 52, 0.3, 0.2, 13.8, 'g', '新鲜红富士苹果'),
('香蕉', '水果', 91, 1.4, 0.2, 22.0, 'g', '新鲜香蕉'),
('橙子', '水果', 47, 0.8, 0.2, 11.8, 'g', '新鲜脐橙'),
('葡萄', '水果', 67, 0.5, 0.2, 17.2, 'g', '新鲜巨峰葡萄'),
('豆浆', '饮品', 14, 1.8, 0.7, 1.1, 'ml', '无糖豆浆'),
('可乐', '饮品', 42, 0, 0, 10.6, 'ml', '可口可乐'),
('豆腐', '豆制品', 81, 8.1, 3.7, 4.2, 'g', '嫩豆腐'),
('花生油', '油脂', 899, 0, 99.9, 0, 'ml', '食用花生油'),
('核桃', '坚果', 627, 14.9, 58.8, 19.1, 'g', '干核桃仁'),
('蛋糕', '甜点', 347, 8.6, 14.4, 46.7, 'g', '奶油蛋糕'),
('巧克力', '甜点', 586, 4.3, 40.1, 53.4, 'g', '牛奶巧克力'),
('蜂蜜', '调味', 321, 0.4, 1.9, 75.6, 'g', '天然蜂蜜'),
('橄榄油', '油脂', 899, 0, 99.9, 0, 'ml', '特级初榨橄榄油');
