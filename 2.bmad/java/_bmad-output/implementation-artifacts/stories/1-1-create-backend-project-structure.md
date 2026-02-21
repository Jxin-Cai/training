# Story 1.1: 创建后端项目结构

Status: ready-for-dev

## Story

As a **开发者**,
I want **创建Spring Boot后端项目，采用DDD分层架构**,
so that **后续功能开发有清晰的架构基础**.

## Acceptance Criteria

1. **AC1: Spring Boot项目初始化**
   - Given 开发环境已准备好（JDK 17+, Maven）
   - When 创建Spring Boot项目
   - Then 项目成功创建，包含pom.xml和基本配置

2. **AC2: DDD分层目录结构**
   - Given Spring Boot项目已创建
   - When 创建DDD分层目录
   - Then 项目包含以下目录结构：
     ```
     src/main/java/com/java/
     ├── presentation/
     │   ├── controller/
     │   └── dto/
     ├── application/
     │   └── service/
     ├── domain/
     │   ├── entity/
     │   ├── valueobject/
     │   ├── service/
     │   └── repository/
     ├── infrastructure/
     │   ├── repository/
     │   └── config/
     └── util/
     ```

3. **AC3: Maven依赖配置**
   - Given DDD目录结构已创建
   - When 配置pom.xml
   - Then 包含以下依赖：
     - Spring Boot Starter Web
     - Spring Boot Starter Security
     - Spring Boot Starter Validation
     - Lombok
     - Spring Boot Starter Test (测试)

4. **AC4: 应用配置文件**
   - Given pom.xml已配置
   - When 创建application.yml
   - Then 包含基础配置（端口、应用名称等）

## Tasks / Subtasks

- [x] Task 1: 初始化Spring Boot项目 (AC: 1)
  - [x] 1.1 使用Spring Initializr创建项目
  - [x] 1.2 配置JDK 17和Maven
  - [x] 1.3 验证项目可启动

- [x] Task 2: 创建DDD分层目录结构 (AC: 2)
  - [x] 2.1 创建presentation层目录
  - [x] 2.2 创建application层目录
  - [x] 2.3 创建domain层目录
  - [x] 2.4 创建infrastructure层目录
  - [x] 2.5 创建util目录

- [x] Task 3: 配置Maven依赖 (AC: 3)
  - [x] 3.1 添加Spring Boot Web依赖
  - [x] 3.2 添加Spring Security依赖
  - [x] 3.3 添加Lombok依赖
  - [x] 3.4 添加测试依赖

- [x] Task 4: 创建配置文件 (AC: 4)
  - [x] 4.1 创建application.yml
  - [x] 4.2 配置服务器端口
  - [x] 4.3 配置应用名称

## Dev Notes

### 架构约束 (from Architecture.md)

**DDD分层架构必须遵循：**

1. **Presentation Layer（展示层）**
   - Controller：API控制器
   - DTO：数据传输对象
   - 职责：处理HTTP请求/响应

2. **Application Layer（应用层）**
   - Service：应用服务
   - 职责：编排领域对象，协调业务流程

3. **Domain Layer（领域层）**
   - Entity：具有唯一标识的实体
   - Value Object：不可变值对象
   - Domain Service：领域服务
   - Repository Interface：仓储接口
   - 职责：核心业务逻辑

4. **Infrastructure Layer（基础设施层）**
   - Repository Implementation：仓储实现（内存存储）
   - Config：配置类
   - 职责：技术实现细节

### 技术栈 (from Architecture.md)

| 组件 | 版本/说明 |
|------|----------|
| **语言** | Java 17+ |
| **框架** | Spring Boot 3.x |
| **构建工具** | Maven |
| **数据存储** | 内存存储（ConcurrentHashMap） |
| **安全** | Spring Security + BCrypt |

### 代码规范 (from doc/SPEC.md)

1. **DDD POJO定义**：使用充血模型，而非贫血模型
2. **SOLID原则**：遵循面向对象设计原则
3. **代码质量**：
   - 避免重复代码
   - 避免过长方法
   - 避免过大类
   - 合理使用设计模式

### API响应格式 (from Architecture.md)

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### Project Structure Notes

**完整后端项目结构：**

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
│   │   │   │   └── dto/
│   │   │   ├── application/
│   │   │   │   └── service/
│   │   │   ├── domain/
│   │   │   │   ├── entity/
│   │   │   │   ├── valueobject/
│   │   │   │   ├── service/
│   │   │   │   └── repository/
│   │   │   ├── infrastructure/
│   │   │   │   ├── repository/
│   │   │   │   └── config/
│   │   │   └── util/
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/java/
└── README.md
```

### References

- [Source: _bmad-output/planning-artifacts/architecture.md#DDD分层架构]
- [Source: _bmad-output/planning-artifacts/epics.md#Story 1.1]
- [Source: doc/SPEC.md#后端架构规范]
- [Source: _bmad-output/planning-artifacts/prd.md#Technical Constraints]

## Dev Agent Record

### Agent Model Used

(待开发时填写)

### Debug Log References

(待开发时填写)

### Completion Notes List

(待开发时填写)

### File List

(待开发时填写)
