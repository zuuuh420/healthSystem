# 健康数据管理模块 - 接口文档

> **负责人**：李滔  
> **分支**：feature/health-data

---

## 通用说明

所有接口需在 Header 中携带 JWT Token：`Authorization: Bearer <token>`

### 分页参数
| 参数 | 类型 | 必填 | 默认值 |
|------|------|------|--------|
| pageNum | int | 否 | 1 |
| pageSize | int | 否 | 10 |
| startDate | string | 否 | - |
| endDate | string | 否 | - |

---

## 1. 体重管理 `/api/health-weight`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 分页查询 |
| GET | `/trend?limit=30` | 最近N条趋势 |
| GET | `/{id}` | 详情 |
| POST | `/` | 新增 |
| PUT | `/{id}` | 修改 |
| DELETE | `/{id}` | 删除 |

**请求体（新增/修改）：**
```json
{ "weight": 72.5, "heightCm": 175.0, "recordDate": "2026-07-01", "note": "" }
```

---

## 2. 血压管理 `/api/health-blood-pressure`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/` | 分页查询 |
| GET | `/trend?limit=30` | 趋势 |
| GET | `/{id}` | 详情 |
| POST | `/` | 新增 |
| PUT | `/{id}` | 修改 |
| DELETE | `/{id}` | 删除 |

**请求体：**
```json
{ "systolic": 120, "diastolic": 80, "heartRateBpm": 72, "recordDate": "2026-07-01", "recordTime": "08:00:00", "note": "" }
```

---

## 3. 血糖管理 `/api/health-blood-sugar`

**请求体：**
```json
{ "sugarLevel": 5.2, "measureType": "空腹", "recordDate": "2026-07-01", "recordTime": "07:00:00", "note": "" }
```
> measureType: 空腹 / 餐后 / 随机

---

## 4. 心率管理 `/api/health-heart-rate`

**请求体：**
```json
{ "heartRate": 72, "measureType": "静息", "recordDate": "2026-07-01", "recordTime": "08:00:00", "note": "" }
```
> measureType: 静息 / 运动后 / 睡眠

---

## 数据表

| 表名 | 说明 |
|------|------|
| health_weight | 体重&BMI（BMI自动计算） |
| health_blood_pressure | 血压（收缩压/舒张压/心率） |
| health_blood_sugar | 血糖（空腹/餐后/随机） |
| health_heart_rate | 心率（静息/运动后/睡眠） |
