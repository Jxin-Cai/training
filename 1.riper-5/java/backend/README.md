# CMS Backend - 后端服务

## 项目简介

基于Spring Boot的CMS内容管理系统后端服务，采用DDD（领域驱动设计）分层架构。

## 技术栈

- **Java**: 11
- **框架**: Spring Boot 2.7.18
- **数据库**: H2内存数据库
- **Markdown渲染**: commonmark-java 0.21.0
- **构建工具**: Maven

## 架构设计

### DDD分层架构

```
com.cms
├── presentation/        # 展示层
│   └── controller/      # REST控制器
├── application/         # 应用层
│   ├── service/         # 应用服务
│   └── dto/             # 数据传输对象
├── domain/              # 领域层
│   ├── model/           # 领域模型（实体、值对象）
│   ├── repository/      # 仓储接口
│   └── service/         # 领域服务
└── infrastructure/      # 基础设施层
    ├── repository/      # 仓储实现
    └── config/          # 配置类
```

### 领域模型

#### Category（分类聚合根）
- 属性：id, name, description, createTime, updateTime
- 业务方法：create(), updateInfo(), canBeDeleted()

#### Content（内容聚合根）
- 属性：id, title, markdownContent, renderedHtml, categoryId, status, publishTime, createTime, updateTime
- 业务方法：create(), updateContent(), publish(), unpublish(), isPublished()

#### ContentStatus（内容状态值对象）
- 枚举：DRAFT（草稿）、PUBLISHED（已发布）

## API接口

### 分类管理 (/api/categories)

- `POST /api/categories` - 创建分类
- `GET /api/categories` - 查询所有分类
- `GET /api/categories/{id}` - 查询单个分类
- `PUT /api/categories/{id}` - 更新分类
- `DELETE /api/categories/{id}` - 删除分类

### 内容管理 (/api/contents)

- `POST /api/contents` - 创建内容
- `GET /api/contents` - 查询内容列表（支持状态筛选：?status=DRAFT|PUBLISHED）
- `GET /api/contents/{id}` - 查询单个内容
- `PUT /api/contents/{id}` - 更新内容
- `DELETE /api/contents/{id}` - 删除内容
- `PUT /api/contents/{id}/publish` - 发布内容
- `PUT /api/contents/{id}/unpublish` - 取消发布

### 前台查询 (/api/frontend)

- `GET /api/frontend/contents` - 查询已发布内容列表（按发布时间降序）
- `GET /api/frontend/contents/{id}` - 查询已发布内容详情

## 本地运行

### 开发模式

```bash
cd backend
mvn spring-boot:run
```

访问：http://localhost:8080

### H2 Console

H2数据库控制台：http://localhost:8080/h2-console

连接配置：
- JDBC URL: `jdbc:h2:mem:cmsdb`
- Username: `sa`
- Password: （留空）

### 打包部署

```bash
mvn clean package
java -jar target/cms-backend.jar
```

## 重要说明

⚠️ **数据持久化**: 当前使用H2内存数据库，应用重启后数据会丢失。这是MVP版本的设计，生产环境需要切换到持久化数据库（如MySQL、PostgreSQL）。

## 核心特性

1. **DDD充血模型**: 领域对象包含业务逻辑，避免贫血模型
2. **Markdown自动渲染**: 内容保存时自动将Markdown转换为HTML
3. **状态管理**: 内容支持草稿和发布状态转换
4. **关联验证**: 删除分类前检查是否有关联内容
5. **CORS配置**: 支持前端3000端口跨域访问
