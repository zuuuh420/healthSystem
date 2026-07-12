-- =============================================
-- 用户表数据库脚本
-- 创建时间: 2026-07-12
-- =============================================

-- 用户表
CREATE TABLE IF NOT EXISTS user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    nickname VARCHAR(50) COMMENT '昵称',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(200) COMMENT '头像URL',
    status TINYINT DEFAULT 1 COMMENT '状态：1启用 0禁用',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色：user/admin',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) COMMENT '用户表';

-- 初始管理员用户（密码: admin123，BCrypt加密）
INSERT INTO user (username, password, nickname, email, role) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '管理员', 'admin@health.com', 'admin');

-- 初始测试用户（密码: user123，BCrypt加密）
INSERT INTO user (username, password, nickname, email, role) VALUES
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '测试用户', 'test@health.com', 'user');
