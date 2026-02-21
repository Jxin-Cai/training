---
stepsCompleted: [1, 2, 3, 4, 5, 6, 7, 8]
inputDocuments: [
  '_bmad-output/planning-artifacts/prd.md',
  '_bmad-output/planning-artifacts/ux-design-specification.md',
  '_bmad-output/planning-artifacts/product-brief-java-2026-02-17.md',
  '_bmad-output/planning-artifacts/research/domain-CMS最佳实践-research-2026-02-14.md',
  'doc/SPEC.md'
]
workflowType: 'architecture'
lastStep: 8
status: 'complete'
completedAt: '2026-02-17'
---

# Architecture Decision Record - java

**Author:** Jxin
**Date:** 2026-02-17

---

## Project Context Analysis

### Requirements Overview

**Functional Requirements:**
- 28条功能需求，分为4个主要类别
- 用户管理（FR1-FR6）
- 分类管理（FR7-FR10）
- 文章管理（FR11-FR21）
- 内容浏览（FR22-FR28）

**Non-Functional Requirements:**
- 性能：页面加载 < 2秒，列表加载 < 1秒
- 安全：密码加密、XSS/CSRF防护
- 可靠性：99.9%可用性，自动保存
- 可用性：新用户无需培训

**Scale & Complexity:**
- Primary domain: Web应用（前后台分离）
- Complexity level: 低-中
- Estimated architectural components: 前台展示、后台管理、API服务

### Technical Constraints (from doc/SPEC.md)

- **前端框架**：Vue.js
- **后端框架**：Spring Boot (Java)
- **数据存储**：内存存储（非持久化数据库）
- **部署方式**：前后端分离部署

---

## Starter Template Evaluation

### Primary Technology Domain

**Web应用 - 前后台分离CMS系统**

基于项目需求分析：
- 前台展示端：内容阅读体验
- 后台管理端：内容管理CRUD

### Selected Technology Stack (from doc/SPEC.md)

**后端：Spring Boot (Java)**
- 语言：Java
- 框架：Spring Boot
- 数据存储：内存存储
- 架构模式：DDD分层架构

**前端：Vue.js**
- 框架：Vue.js
- 构建工具：npm run build
- 组件库：可与Ant Design Vue配合

---

## Core Architectural Decisions

### 1. System Architecture

**决策：前后台分离的DDD分层架构**

```
┌─────────────────────────────────────────────────┐
│                   前台展示端                      │
│               (Vue.js SPA)                      │
├─────────────────────────────────────────────────┤
│                   后台管理端                      │
│               (Vue.js SPA)                      │
├─────────────────────────────────────────────────┤
│                   HTTP/HTTPS API                 │
├─────────────────────────────────────────────────┤
│              Spring Boot Backend                 │
│    ┌─────────────────────────────────────────┐  │
│    │  Presentation Layer (Controller, DTO)   │  │
│    ├─────────────────────────────────────────┤  │
│    │  Application Layer (Service)            │  │
│    ├─────────────────────────────────────────┤  │
│    │  Domain Layer (Entity, Value Object,    │  │
│    │               Domain Service, Repo IF)  │  │
│    ├─────────────────────────────────────────┤  │
│    │  Infrastructure Layer (Repo Impl)       │  │
│    └─────────────────────────────────────────┘  │
├─────────────────────────────────────────────────┤
│              In-Memory Storage                   │
└─────────────────────────────────────────────────┘
```

### 2. DDD Layered Architecture

**Presentation Layer（展示层）**
- Controller：API控制器
- DTO：数据传输对象

**Application Layer（应用层）**
- Service：应用服务
- 编排领域对象

**Domain Layer（领域层）**
- Entity：具有唯一标识的实体
- Value Object：不可变值对象
- Domain Service：领域服务
- Repository Interface：仓储接口

**Infrastructure Layer（基础设施层）**
- Repository Implementation：仓储实现（内存存储）
- 外部服务调用

### 3. Data Architecture

**数据存储：内存存储（In-Memory）**

**数据模型：**
- User（用户实体）
- Article（文章实体）
- Category（分类实体）

**存储实现：**
- 使用 Java ConcurrentHashMap 或类似结构
- 应用重启后数据清空

### 4. Authentication & Security

**认证方案：Session-Based认证**
- Spring Security
- 密码BCrypt加密
- Session管理（内存存储）

**安全措施：**
- XSS防护（输入过滤、输出编码）
- CSRF防护（Token验证）

### 5. API & Communication

**API设计规范：RESTful**

| 操作 | 方法 | 端点 |
|------|------|------|
| 获取文章列表 | GET | /api/articles |
| 获取文章详情 | GET | /api/articles/{id} |
| 创建文章 | POST | /api/articles |
| 更新文章 | PUT | /api/articles/{id} |
| 删除文章 | DELETE | /api/articles/{id} |

**API响应格式：**
```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 6. Frontend Architecture

**技术栈：**
- Vue.js 3.x
- Vue Router
- Pinia（状态管理）
- Axios（HTTP客户端）
- Markdown渲染组件

### 7. Infrastructure & Deployment

**部署方案（from doc/SPEC.md）：**
- 前端：静态资源服务（npm run build）
- 后端：独立Spring Boot服务（mvn clean package）

**部署脚本：**
```bash
# 一键部署
./deploy.sh          # Linux/Mac
deploy.cmd           # Windows

# 单独部署
./deploy.sh frontend
./deploy.sh backend
```

**停止脚本：**
```bash
# 一键停止
./shutdown.sh        # Linux/Mac
shutdown.cmd         # Windows
```

---

## Implementation Patterns & Consistency Rules

### Code Quality Standards (from doc/SPEC.md)

**面向对象编程原则：**
- 遵循 **SOLID 原则**
- 合理使用设计模式
- 避免面向过程式编程

**重构规范：**
识别并规避代码坏味道：
- 重复代码（Duplicated Code）
- 过长方法（Long Method）
- 过大类（Large Class）
- 过长参数列表（Long Parameter List）
- 发散式变化（Divergent Change）
- 霰弹式修改（Shotgun Surgery）

**Effective Java规范：**
- 使用构建器（Builder）处理多参数构造
- 优先使用枚举而非 int 常量
- 最小化可变性
- 合理使用 Optional
- 优先使用标准异常

### DDD POJO Definition Standards (from doc/SPEC.md)

- 使用 **DDD 战术模式** 定义 POJO：
  - Entity：具有唯一标识的对象
  - Value Object：不可变的值对象
  - Aggregate Root：聚合根
- 遵循 **充血模型**，而非贫血模型

### Naming Patterns

**API命名：**
- 端点：复数名词（/api/articles, /api/categories）
- 查询参数：camelCase（?categoryId=1）
- 路径参数：{id}

**代码命名：**
- 类名：PascalCase（ArticleService, UserController）
- 方法名：camelCase（getArticleById, createArticle）
- 变量名：camelCase（articleList, categoryId）
- 常量名：UPPER_SNAKE_CASE（MAX_PAGE_SIZE）

### Structure Patterns

**后端DDD项目结构：**
```
src/main/java/com/java/
├── presentation/          # 展示层
│   ├── controller/        # API控制器
│   └── dto/               # 数据传输对象
├── application/           # 应用层
│   └── service/           # 应用服务
├── domain/                # 领域层
│   ├── entity/            # 实体
│   ├── valueobject/       # 值对象
│   ├── service/           # 领域服务
│   └── repository/        # 仓储接口
├── infrastructure/        # 基础设施层
│   ├── repository/        # 仓储实现
│   └── config/            # 配置
└── util/                  # 工具类
```

**前端Vue项目结构：**
```
src/
├── views/            # 页面组件
├── components/       # 通用组件
├── api/              # API服务
├── store/            # Pinia状态
├── router/           # 路由配置
├── utils/            # 工具函数
└── styles/           # 样式文件
```

### Format Patterns

**API响应格式：**
```json
// 成功响应
{
  "code": 200,
  "message": "success",
  "data": { ... }
}

// 错误响应
{
  "code": 400,
  "message": "错误描述",
  "data": null
}
```

**日期格式：** ISO 8601（yyyy-MM-dd'T'HH:mm:ss）

---

## Project Structure & Boundaries

### Complete Project Directory Structure

**后端项目结构（DDD分层）：**
```
java-backend/
├── pom.xml
├── deploy.sh
├── shutdown.sh
├── src/
│   ├── main/
│   │   ├── java/com/java/
│   │   │   ├── JavaApplication.java
│   │   │   ├── presentation/
│   │   │   │   ├── controller/
│   │   │   │   │   ├── ArticleController.java
│   │   │   │   │   ├── CategoryController.java
│   │   │   │   │   ├── UserController.java
│   │   │   │   │   └── AuthController.java
│   │   │   │   └── dto/
│   │   │   │       ├── ArticleDTO.java
│   │   │   │       └── UserDTO.java
│   │   │   ├── application/
│   │   │   │   └── service/
│   │   │   │       ├── ArticleService.java
│   │   │   │       ├── CategoryService.java
│   │   │   │       └── UserService.java
│   │   │   ├── domain/
│   │   │   │   ├── entity/
│   │   │   │   │   ├── Article.java
│   │   │   │   │   ├── Category.java
│   │   │   │   │   └── User.java
│   │   │   │   ├── valueobject/
│   │   │   │   │   └── ArticleStatus.java
│   │   │   │   ├── service/
│   │   │   │   │   └── ArticleDomainService.java
│   │   │   │   └── repository/
│   │   │   │       ├── ArticleRepository.java
│   │   │   │       ├── CategoryRepository.java
│   │   │   │       └── UserRepository.java
│   │   │   ├── infrastructure/
│   │   │   │   ├── repository/
│   │   │   │   │   ├── InMemoryArticleRepository.java
│   │   │   │   │   ├── InMemoryCategoryRepository.java
│   │   │   │   │   └── InMemoryUserRepository.java
│   │   │   │   └── config/
│   │   │   │       ├── SecurityConfig.java
│   │   │   │       └── WebConfig.java
│   │   │   └── util/
│   │   │       └── MarkdownUtil.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/java/
└── README.md
```

**前端项目结构（Vue.js）：**
```
java-frontend/
├── package.json
├── deploy.sh
├── shutdown.sh
├── src/
│   ├── main.js
│   ├── App.vue
│   ├── views/
│   │   ├── Home.vue
│   │   ├── Article.vue
│   │   ├── Category.vue
│   │   └── admin/
│   │       ├── Login.vue
│   │       ├── Dashboard.vue
│   │       ├── ArticleList.vue
│   │       ├── ArticleEdit.vue
│   │       ├── CategoryManage.vue
│   │       └── UserManage.vue
│   ├── components/
│   │   ├── Header.vue
│   │   ├── Footer.vue
│   │   ├── ArticleCard.vue
│   │   ├── CategoryFilter.vue
│   │   └── MarkdownEditor.vue
│   ├── api/
│   │   └── index.js
│   ├── store/
│   │   └── index.js
│   ├── router/
│   │   └── index.js
│   └── styles/
│       └── global.css
└── public/
    └── index.html
```

### Requirements to Structure Mapping

| 功能需求 | 后端位置 | 前端位置 |
|---------|---------|---------|
| 用户登录 | AuthController | Login.vue |
| 文章CRUD | ArticleController | ArticleList/Edit.vue |
| 分类管理 | CategoryController | CategoryManage.vue |
| 用户管理 | UserController | UserManage.vue |
| 文章阅读 | ArticleController | Home.vue, Article.vue |

---

## Architecture Validation Results

### Coherence Validation ✅

**Decision Compatibility:**
- Spring Boot + Vue.js 完美兼容
- DDD分层架构清晰
- 前后端RESTful通信标准

**Pattern Consistency:**
- 命名规范统一
- API格式一致
- DDD领域模型清晰

**Structure Alignment:**
- 项目结构支持DDD分层
- 组件边界清晰
- 集成点明确

### Requirements Coverage Validation ✅

**Functional Requirements:**
- ✅ 用户管理：AuthController + UserController
- ✅ 分类管理：CategoryController + CategoryService
- ✅ 文章管理：ArticleController + MarkdownEditor
- ✅ 内容浏览：前台Vue应用

**Non-Functional Requirements:**
- ✅ 性能：单页应用 + 内存存储
- ✅ 安全：Spring Security + XSS/CSRF防护
- ✅ 可靠性：自动保存
- ✅ 可用性：Vue组件化开发

### Architecture Completeness Checklist

- [x] 项目上下文分析
- [x] 技术栈选型（遵循SPEC.md）
- [x] 核心架构决策（DDD分层）
- [x] 实现模式定义
- [x] 项目结构设计
- [x] 需求映射完成

### Architecture Readiness Assessment

**Overall Status:** ✅ READY FOR IMPLEMENTATION

**Confidence Level:** 高

**Key Strengths:**
- 遵循SPEC.md技术规范
- DDD分层架构，职责清晰
- 内存存储简化部署
- 部署脚本完善

**Important Notes:**
- 内存存储：应用重启后数据丢失
- 适合MVP验证，后续可迁移到持久化存储

---

## Implementation Handoff

### 开发优先级

1. **Phase 1: 项目初始化**
   - 创建后端项目（Spring Boot + DDD结构）
   - 创建前端项目（Vue.js）
   - 配置部署脚本

2. **Phase 2: 核心功能**
   - 用户认证
   - 文章CRUD
   - 分类管理

3. **Phase 3: 前台展示**
   - 首页文章列表
   - 文章详情页
   - 分类浏览

### AI Agent Guidelines

- **严格遵循 doc/SPEC.md 的技术规范**
- 遵循DDD分层架构
- 遵循SOLID原则和代码质量规范
- 使用定义的实现模式
- 尊重项目结构和边界
