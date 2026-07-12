# 个人健康管理系统 (Health System)

## 项目简介

采用前后端分离架构的个人健康管理系统，支持用户注册登录、健康数据记录、运动管理、饮食管理、健康目标设置、健康分析和周报生成。

## 技术栈

- **前端**: Vue 3 + Element Plus + ECharts
- **后端**: Spring Boot + MyBatis Plus + JWT
- **数据库**: MySQL 8.0

## 项目结构

```
healthSystem/
├─ health-backend/       # Spring Boot后端
├─ health-frontend/      # Vue前端
├─ database/             # 初始化和数据库变更脚本
│   └─ migrations/       # 数据库迁移脚本
├─ docs/                 # 需求、接口、测试和答辩文档
│   └─ api/              # 接口文档
├─ contributions/        # 成员贡献记录
├─ .env.example          # 环境变量示例
└─ README.md
```

## 分支管理

| 分支 | 用途 | 负责人 |
|------|------|--------|
| main | 稳定演示版本 | 赵恒硕 |
| develop | 日常集成版本 | 全组 |
| feature/base-dashboard | 工程骨架、登录鉴权、首页 | 赵恒硕 |
| feature/health-data | 健康数据管理 | 李滔 |
| feature/report-profile | 健康报告与个人中心 | 卢双信 |
| feature/sport-goal | 运动与目标管理 | 张森栋 |
| feature/diet-admin | 饮食、管理端与测试 | 刘子豪 |

## 开发规范

1. 任何成员不得直接向 main 提交
2. 个人功能先通过 PR 合并到 develop
3. 数据库变更必须在 database/migrations 中增加 SQL 文件
4. 接口变更必须同步更新接口文档

## 小组成员

| 成员 | 角色 | 主要职责 |
|------|------|----------|
| 赵恒硕 | 项目负责人 | 工程骨架、登录鉴权、健康分析、部署 |
| 李滔 | 需求与健康数据 | 数据库设计、健康数据CRUD |
| 卢双信 | 报告与个人中心 | 个人资料、健康周报、PPT |
| 张森栋 | 运动与目标 | 运动库、运动记录、目标、打卡 |
| 刘子豪 | 饮食与管理端 | 食物库、饮食记录、管理端、测试 |
