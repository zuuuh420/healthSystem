# 健康助手第一版 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在现有健康助手中加入通用健康问答、本人和已授权家人数据问答、生活方式建议以及症状安全分流，并保证 AI 服务不可用时仍可本地回答。

**Architecture:** 保留现有 `HealthAssistant` 对话界面和 `requestHealthAssistant` 服务。新增本地意图识别与安全分流模块，先生成结构化本地答案；远程 AI 只作为可选增强，发送给后端的数据限定为当前用户输入、本人快照和已授权家人快照。所有回答标记数据来源和非诊断边界。

**Tech Stack:** React + TypeScript + native CSS + Spring Boot 现有 AssistantController/AssistantService + Vitest-free pure TypeScript tests through TypeScript build and focused runtime checks.

## Global Constraints

- 不修改 `useSimulatedVitals`、`useFamilyMembers` 的刷新规则、设备同步逻辑和个人 Dashboard 数据逻辑。
- 家人数据只使用当前用户已授权的 `FamilyMember[]`，不接受用户输入的任意用户 ID。
- 生活方式回答优先覆盖睡眠、运动、饮食和饮水。
- 症状问题只做安全分流，不做诊断、不提供处方或替代医生判断。
- 后端不可用、未登录或远程 AI 未配置时，本地回答必须可用。
- 保留“数据仅供参考，不作为临床诊断依据”提示。

---

### Task 1: 本地问答引擎与安全分流

**Files:**
- Create: `src/services/healthAssistantLocal.ts`
- Modify: `src/components/HealthAssistant.tsx`

**Interfaces:**
- `LocalAssistantContext = { family: FamilyMember[]; userVitals: SimulatedVitals }`
- `answerLocalHealthQuestion(question: string, context: LocalAssistantContext): LocalAssistantAnswer`
- `LocalAssistantAnswer = { text: string; category: 'data' | 'lifestyle' | 'triage' | 'fallback'; severity: 'normal' | 'caution' | 'urgent'; source: 'device' | 'health-guide' | 'safety-guide' }`

- [ ] **Step 1: 建立可测试的纯函数规则表**

覆盖以下意图：本人/家人数据、睡眠、运动、饮食饮水、久坐恢复、胸痛、呼吸困难、昏厥/意识异常、持续高热、一般症状和未知问题。家人匹配只从 `context.family` 的 `name`、`originalName`、`relationship` 中选择。

- [ ] **Step 2: 运行 TypeScript 检查，确认新增类型和分支缺失**

Run: `npm run build`

Expected: 新模块导入前构建失败或暴露类型错误，随后由下一步接入修复。

- [ ] **Step 3: 将 `HealthAssistant` 的本地 fallback 替换为结构化答案**

保留远程 AI 优先策略；远程请求为空或失败时使用 `answerLocalHealthQuestion`，消息中显示来源标签，症状高风险时显示醒目的就医提示。

- [ ] **Step 4: 运行构建验证本地问答接入**

Run: `npm run build`

Expected: PASS；现有数据问题仍可回答，未知问题不再只返回单一固定句子。

---

### Task 2: 远程 AI 请求边界与失败兜底

**Files:**
- Modify: `src/services/healthAssistant.ts`
- Modify: `health-backend/src/main/java/com/health/controller/AssistantController.java`
- Modify: `health-backend/src/main/java/com/health/service/AssistantService.java`

**Interfaces:**
- 前端请求仍使用 `POST /api/assistant/chat`。
- 请求体只包含 `question`、`family`、`userVitals`，不增加任意用户标识字段。
- 后端统一返回 `{ code, msg, data: { answer } }`，空回答视为失败并由前端本地兜底。

- [ ] **Step 1: 先增加后端请求/响应边界测试**

验证未登录被拦截、空问题被拒绝、正常请求返回结构化 answer；不测试真实第三方模型网络。

- [ ] **Step 2: 修正前端响应解析**

对非 JSON、空响应、HTTP 401/403/5xx 和空 `answer` 统一抛出可识别错误，不把解析异常直接展示给用户。

- [ ] **Step 3: 约束后端提示词和上下文**

提示词明确：回答睡眠/运动/饮食优先；症状只做安全分流；不得诊断、开药、虚构数据；只能引用传入的本人和已授权家人快照。

- [ ] **Step 4: 运行后端和前端验证**

Run: `mvn -q test` and `npm run build`

Expected: PASS；AI 服务未配置时前端仍显示本地回答。

---

### Task 3: 助手界面分类与反馈状态

**Files:**
- Modify: `src/components/HealthAssistant.tsx`
- Modify: `src/styles.css`

**Interfaces:**
- 不新增路由；继续复用右下角悬浮按钮和非全屏对话框。
- 常用问题按钮分为“我的数据”“睡眠”“运动”“饮食”“家人状态”五类。

- [ ] **Step 1: 增加结构化消息元数据渲染**

为助手消息增加来源小标签：`设备数据`、`健康建议`、`安全提醒`；保留用户消息原样显示。

- [ ] **Step 2: 增加分类快捷问题**

默认展示睡眠、运动、饮食问题，同时保留本人步数和家人佩戴问题；移动端允许换行，不产生横向滚动。

- [ ] **Step 3: 增加 loading、远程失败和高风险状态样式**

高风险安全提醒使用低饱和警示色，不使用红色大面积弹窗；远程失败提示“已使用本地健康知识回答”。

- [ ] **Step 4: 运行响应式构建检查**

Run: `npm run build`

检查尺寸：390×844、768×1024、1366×768；Expected: 无横向滚动，对话框不铺满桌面视口。

---

### Task 4: 端到端验收

**Files:**
- No new production files.

- [ ] **Step 1: 启动后端和前端**

后端监听 `8082`，前端访问 `http://localhost:3202/`，使用 `demo2026 / demo123` 登录。

- [ ] **Step 2: 验证数据型问题**

询问“我今天走了多少步”“爸爸有没有佩戴”“妈妈的血氧是多少”，确认只返回本人或已授权家人数据，未佩戴家人不显示虚构指标。

- [ ] **Step 3: 验证通用健康问题**

询问“晚上睡不着怎么办”“每天运动多久”“饭后多久适合运动”，确认回答包含可执行建议和健康参考边界。

- [ ] **Step 4: 验证症状安全分流**

询问“我胸口剧烈疼痛怎么办”，确认提示立即寻求急救/医疗帮助，不给出诊断或自行用药建议。

- [ ] **Step 5: 验证后端不可用兜底**

关闭或不可用远程 AI 后重复生活方式和数据问题，确认仍能本地回答且不出现 JSON 解析错误。

- [ ] **Step 6: 最终检查**

Run: `npm run build`, `mvn -q test`, `git diff --check`

Expected: 全部通过；输出保留已有大资源包 warning，不视为失败。
