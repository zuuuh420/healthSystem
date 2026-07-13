# 数据库初始化说明

## 1. 创建数据库

```sql
CREATE DATABASE health_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

## 2. 执行建表脚本

按顺序执行以下脚本：

```bash
# 用户表
mysql -u root -p health_system < migrations/V001_user.sql

# 运动与目标表
mysql -u root -p health_system < migrations/V005_sport_goal.sql

# 饮食、健康数据与管理端表
mysql -u root -p health_system < migrations/V006_diet_admin.sql
```

## 3. 导入测试数据

```bash
# 完整测试数据（推荐）
mysql -u root -p health_system < test_data/complete_test_data.sql
```

## 4. 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | admin123 | 管理员 |
| zhangsan | user123 | 普通用户 |
| lisi | user123 | 普通用户 |

## 数据库表结构

### user 用户表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(50) | 用户名（唯一） |
| password | VARCHAR(100) | 密码（BCrypt加密） |
| nickname | VARCHAR(50) | 昵称 |
| email | VARCHAR(100) | 邮箱 |
| phone | VARCHAR(20) | 手机号 |
| avatar | VARCHAR(200) | 头像URL |
| status | TINYINT | 状态：1启用 0禁用 |
| role | VARCHAR(20) | 角色：user/admin |

### sport_type 运动类型表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR(50) | 运动名称 |
| category | VARCHAR(30) | 分类：有氧/无氧/拉伸 |
| calories_per_minute | DECIMAL(5,2) | 每分钟消耗卡路里 |
| icon | VARCHAR(200) | 图标 |
| status | TINYINT | 状态 |

### sport_record 运动记录表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| sport_type_id | BIGINT | 运动类型ID |
| duration_minutes | INT | 运动时长（分钟） |
| calories_burned | DECIMAL(8,2) | 消耗卡路里 |
| sport_date | DATE | 运动日期 |
| remark | VARCHAR(500) | 备注 |

### sport_plan 运动计划表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| sport_type_id | BIGINT | 运动类型ID |
| plan_name | VARCHAR(100) | 计划名称 |
| frequency | VARCHAR(30) | 频率 |
| target_minutes | INT | 目标时长 |
| start_date | DATE | 开始日期 |
| end_date | DATE | 结束日期 |
| status | TINYINT | 状态 |

### health_goal 健康目标表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| goal_type | VARCHAR(30) | 目标类型 |
| goal_name | VARCHAR(100) | 目标名称 |
| target_value | DECIMAL(10,2) | 目标值 |
| current_value | DECIMAL(10,2) | 当前值 |
| unit | VARCHAR(20) | 单位 |
| start_date | DATE | 开始日期 |
| end_date | DATE | 结束日期 |
| status | TINYINT | 状态：0进行中 1已完成 2已过期 |

### daily_checkin 每日打卡表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| checkin_date | DATE | 打卡日期 |
| sport_record_id | BIGINT | 关联运动记录ID |
| checkin_type | VARCHAR(20) | 打卡类型 |
