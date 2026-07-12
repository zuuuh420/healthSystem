# 运动与目标管理模块接口文档

> 负责人: 张森栋
> 更新时间: 2026-07-12

## 目录

1. [运动类型接口](#1-运动类型接口)
2. [运动记录接口](#2-运动记录接口)
3. [运动计划接口](#3-运动计划接口)
4. [健康目标接口](#4-健康目标接口)
5. [每日打卡接口](#5-每日打卡接口)
6. [运动统计接口](#6-运动统计接口)

---

## 通用说明

### 请求头

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| Authorization | string | 是 | JWT Token，格式：Bearer {token} |
| Content-Type | string | 是 | application/json |

### 统一响应格式

```json
{
  "code": 200,
  "msg": "success",
  "data": {}
}
```

### 错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未登录或token过期 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 分页参数

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| pageNum | int | 否 | 1 | 页码 |
| pageSize | int | 否 | 10 | 每页数量 |

### 分页响应格式

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [],
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 10
  }
}
```

---

## 1. 运动类型接口

### 1.1 获取运动类型列表

**请求方式**: GET  
**请求路径**: /api/sport-types

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |
| category | string | 否 | 分类筛选：有氧/无氧/拉伸 |
| keyword | string | 否 | 搜索关键词 |

**响应示例**:

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "name": "跑步",
        "category": "有氧",
        "caloriesPerMinute": 10.00,
        "icon": null,
        "status": 1,
        "createTime": "2026-07-12 10:00:00",
        "updateTime": "2026-07-12 10:00:00"
      }
    ],
    "total": 15,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 2
  }
}
```

### 1.2 获取运动类型详情

**请求方式**: GET  
**请求路径**: /api/sport-types/{id}

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | long | 是 | 运动类型ID |

**响应示例**:

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "id": 1,
    "name": "跑步",
    "category": "有氧",
    "caloriesPerMinute": 10.00,
    "icon": null,
    "status": 1,
    "createTime": "2026-07-12 10:00:00",
    "updateTime": "2026-07-12 10:00:00"
  }
}
```

### 1.3 新增运动类型

**请求方式**: POST  
**请求路径**: /api/sport-types

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| name | string | 是 | 运动名称 |
| category | string | 否 | 分类 |
| caloriesPerMinute | decimal | 否 | 每分钟消耗卡路里 |
| icon | string | 否 | 图标地址 |

**请求示例**:

```json
{
  "name": "拳击",
  "category": "有氧",
  "caloriesPerMinute": 11.00
}
```

**响应示例**:

```json
{
  "code": 200,
  "msg": "新增成功",
  "data": {
    "id": 16,
    "name": "拳击",
    "category": "有氧",
    "caloriesPerMinute": 11.00,
    "status": 1
  }
}
```

### 1.4 修改运动类型

**请求方式**: PUT  
**请求路径**: /api/sport-types/{id}

**请求参数**: 同新增

### 1.5 删除运动类型

**请求方式**: DELETE  
**请求路径**: /api/sport-types/{id}

**响应示例**:

```json
{
  "code": 200,
  "msg": "删除成功",
  "data": null
}
```

---

## 2. 运动记录接口

### 2.1 获取运动记录列表

**请求方式**: GET  
**请求路径**: /api/sport-records

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |
| startDate | date | 否 | 开始日期，格式：yyyy-MM-dd |
| endDate | date | 否 | 结束日期，格式：yyyy-MM-dd |
| sportTypeId | long | 否 | 运动类型ID |

**响应示例**:

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "sportTypeId": 1,
        "sportTypeName": "跑步",
        "sportCategory": "有氧",
        "durationMinutes": 30,
        "caloriesBurned": 300.00,
        "sportDate": "2026-07-01",
        "remark": "晨跑3公里",
        "createTime": "2026-07-01 08:00:00"
      }
    ],
    "total": 12,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 2
  }
}
```

### 2.2 获取运动记录详情

**请求方式**: GET  
**请求路径**: /api/sport-records/{id}

### 2.3 新增运动记录

**请求方式**: POST  
**请求路径**: /api/sport-records

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| sportTypeId | long | 是 | 运动类型ID |
| durationMinutes | int | 是 | 运动时长(分钟)，必须大于0 |
| sportDate | date | 是 | 运动日期 |
| remark | string | 否 | 备注 |

**请求示例**:

```json
{
  "sportTypeId": 1,
  "durationMinutes": 30,
  "sportDate": "2026-07-12",
  "remark": "晨跑"
}
```

**响应示例**:

```json
{
  "code": 200,
  "msg": "新增成功",
  "data": {
    "id": 13,
    "userId": 1,
    "sportTypeId": 1,
    "durationMinutes": 30,
    "caloriesBurned": 300.00,
    "sportDate": "2026-07-12",
    "remark": "晨跑"
  }
}
```

**业务规则**:
- caloriesBurned 自动计算：时长 × 运动类型的 caloriesPerMinute
- sportDate 不能早于30天前

### 2.4 修改运动记录

**请求方式**: PUT  
**请求路径**: /api/sport-records/{id}

**请求参数**: 同新增

### 2.5 删除运动记录

**请求方式**: DELETE  
**请求路径**: /api/sport-records/{id}

### 2.6 获取今日运动记录

**请求方式**: GET  
**请求路径**: /api/sport-records/today

**响应示例**:

```json
{
  "code": 200,
  "msg": "success",
  "data": [
    {
      "id": 12,
      "sportTypeName": "跳绳",
      "durationMinutes": 25,
      "caloriesBurned": 300.00,
      "sportDate": "2026-07-12"
    }
  ]
}
```

---

## 3. 运动计划接口

### 3.1 获取运动计划列表

**请求方式**: GET  
**请求路径**: /api/sport-plans

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |
| status | int | 否 | 状态：1启用 0禁用 |

### 3.2 创建运动计划

**请求方式**: POST  
**请求路径**: /api/sport-plans

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| sportTypeId | long | 是 | 运动类型ID |
| planName | string | 是 | 计划名称 |
| frequency | string | 否 | 频率 |
| targetMinutes | int | 否 | 目标时长(分钟) |
| startDate | date | 否 | 开始日期 |
| endDate | date | 否 | 结束日期 |

### 3.3 修改运动计划

**请求方式**: PUT  
**请求路径**: /api/sport-plans/{id}

### 3.4 删除运动计划

**请求方式**: DELETE  
**请求路径**: /api/sport-plans/{id}

---

## 4. 健康目标接口

### 4.1 获取健康目标列表

**请求方式**: GET  
**请求路径**: /api/health-goals

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| pageNum | int | 否 | 页码 |
| pageSize | int | 否 | 每页数量 |
| status | int | 否 | 状态：0进行中 1已完成 2已过期 |
| goalType | string | 否 | 目标类型 |

**响应示例**:

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "userId": 1,
        "goalType": "weight",
        "goalName": "减重目标",
        "targetValue": 70.00,
        "currentValue": 75.00,
        "unit": "kg",
        "progress": 0,
        "startDate": "2026-07-01",
        "endDate": "2026-08-01",
        "status": 0
      }
    ],
    "total": 4,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 1
  }
}
```

### 4.2 创建健康目标

**请求方式**: POST  
**请求路径**: /api/health-goals

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| goalType | string | 是 | 目标类型：weight/exercise/steps |
| goalName | string | 是 | 目标名称 |
| targetValue | decimal | 是 | 目标值 |
| unit | string | 否 | 单位 |
| startDate | date | 否 | 开始日期 |
| endDate | date | 否 | 结束日期 |

### 4.3 修改健康目标

**请求方式**: PUT  
**请求路径**: /api/health-goals/{id}

### 4.4 删除健康目标

**请求方式**: DELETE  
**请求路径**: /api/health-goals/{id}

### 4.5 更新目标进度

**请求方式**: PUT  
**请求路径**: /api/health-goals/{id}/progress

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| currentValue | decimal | 是 | 当前值 |

**业务规则**:
- 当 currentValue >= targetValue 时，自动将 status 更新为 1（已完成）
- 当 endDate < 当前日期且 status 为 0 时，自动将 status 更新为 2（已过期）

---

## 5. 每日打卡接口

### 5.1 今日打卡

**请求方式**: POST  
**请求路径**: /api/checkin

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| sportRecordId | long | 否 | 关联的运动记录ID |
| checkinType | string | 否 | 打卡类型，默认 sport |

**响应示例**:

```json
{
  "code": 200,
  "msg": "打卡成功",
  "data": {
    "id": 6,
    "userId": 1,
    "checkinDate": "2026-07-12",
    "checkinType": "sport"
  }
}
```

**错误情况**:
- 已打卡：返回 code: 400, msg: "今日已打卡"

### 5.2 查询今日是否已打卡

**请求方式**: GET  
**请求路径**: /api/checkin/today

**响应示例**:

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "checkedIn": true,
    "checkinTime": "2026-07-12 08:30:00"
  }
}
```

### 5.3 查询连续打卡天数

**请求方式**: GET  
**请求路径**: /api/checkin/streak

**响应示例**:

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "currentStreak": 5,
    "maxStreak": 12,
    "totalCheckins": 28
  }
}
```

### 5.4 查询某月打卡日历

**请求方式**: GET  
**请求路径**: /api/checkin/calendar

**请求参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| month | string | 是 | 月份，格式：yyyy-MM |

**响应示例**:

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "month": "2026-07",
    "checkinDays": [1, 2, 3, 4, 5, 8, 9, 10, 11, 12],
    "totalDays": 10
  }
}
```

---

## 6. 运动统计接口

### 6.1 获取本周运动统计

**请求方式**: GET  
**请求路径**: /api/sport-stats/weekly

**响应示例**:

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "totalMinutes": 180,
    "totalCalories": 1800.00,
    "recordCount": 6,
    "dailyStats": [
      {
        "date": "2026-07-07",
        "minutes": 30,
        "calories": 300.00
      },
      {
        "date": "2026-07-08",
        "minutes": 40,
        "calories": 400.00
      }
    ]
  }
}
```

### 6.2 获取本月运动统计

**请求方式**: GET  
**请求路径**: /api/sport-stats/monthly

**响应示例**:

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "totalMinutes": 455,
    "totalCalories": 3785.00,
    "recordCount": 12,
    "avgMinutesPerDay": 41.36,
    "categoryStats": [
      {
        "category": "有氧",
        "minutes": 300,
        "calories": 2700.00,
        "percentage": 65.9
      },
      {
        "category": "无氧",
        "minutes": 95,
        "calories": 640.00,
        "percentage": 20.9
      },
      {
        "category": "拉伸",
        "minutes": 60,
        "calories": 210.00,
        "percentage": 13.2
      }
    ]
  }
}
```

### 6.3 获取运动趋势数据

**请求方式**: GET  
**请求路径**: /api/sport-stats/trend

**请求参数**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| days | int | 否 | 30 | 查询天数 |

**响应示例**:

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "days": 30,
    "trend": [
      {
        "date": "2026-06-13",
        "minutes": 0,
        "calories": 0
      },
      {
        "date": "2026-06-14",
        "minutes": 30,
        "calories": 300.00
      }
    ]
  }
}
```

---

## 附录：数据库表结构

### sport_type 运动类型表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR(50) | 运动名称 |
| category | VARCHAR(30) | 分类 |
| calories_per_minute | DECIMAL(5,2) | 每分钟消耗卡路里 |
| icon | VARCHAR(200) | 图标地址 |
| status | TINYINT | 状态 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### sport_record 运动记录表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| sport_type_id | BIGINT | 运动类型ID |
| duration_minutes | INT | 运动时长(分钟) |
| calories_burned | DECIMAL(8,2) | 消耗卡路里 |
| sport_date | DATE | 运动日期 |
| remark | VARCHAR(500) | 备注 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

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
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

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
| status | TINYINT | 状态 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### daily_checkin 每日打卡表

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| checkin_date | DATE | 打卡日期 |
| sport_record_id | BIGINT | 关联运动记录ID |
| checkin_type | VARCHAR(20) | 打卡类型 |
| create_time | DATETIME | 创建时间 |
