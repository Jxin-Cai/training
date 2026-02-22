# Java CMS

一个轻量级的内容管理系统，适用于5-6人团队的内部知识分享平台。

## 技术栈

**后端**
- Java 17
- Spring Boot 3.2.0
- Spring Security
- 内存存储 (ConcurrentHashMap)

**前端**
- Vue.js 3
- Vite
- Ant Design Vue
- Markdown-it (Markdown渲染)

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- Maven 3.8+

### 启动服务

```bash
# Mac/Linux
./cms.sh start

# Windows
cms.cmd start
```

### 访问地址

- 前台首页: http://localhost:3000
- 后台管理: http://localhost:3000/admin
- 后端API: http://localhost:8080

### 其他命令

```bash
./cms.sh stop       # 停止服务
./cms.sh restart    # 重启服务
./cms.sh status     # 查看状态
./cms.sh logs       # 查看后端日志
```

## 测试账号

| 用户名 | 密码 | 角色 | 权限 |
|--------|------|------|------|
| admin | admin123 | 管理员 | 全部功能 |
| author | author123 | 作者 | 创建/编辑文章 |
| reader | reader123 | 读者 | 阅读文章 |

## 功能特性

### 前台功能
- 浏览已发布文章
- 按分类筛选
- 搜索文章
- Markdown渲染

### 后台功能
- 仪表盘
- 文章管理（创建、编辑、发布/取消发布、删除）
- 分类管理
- 用户管理

## 项目结构

```
java/
├── cms.sh / cms.cmd      # 统一启动脚本
├── java-backend/          # 后端项目
│   └── src/main/java/com/java/
│       ├── presentation/  # 控制器和DTO
│       ├── application/   # 应用服务
│       ├── domain/        # 领域模型
│       └── infrastructure/# 基础设施
└── java-frontend/         # 前端项目
    └── src/
        ├── views/         # 页面组件
        ├── components/    # 公共组件
        ├── api/           # API调用
        ├── router/        # 路由配置
        └── store/         # 状态管理
```

## API接口

### 认证
- `POST /api/auth/login` - 登录
- `POST /api/auth/logout` - 登出

### 文章
- `GET /api/articles` - 获取文章列表（默认只返回已发布）
- `GET /api/articles?all=true` - 获取所有文章（后台管理用）
- `GET /api/articles/{id}` - 获取文章详情
- `POST /api/articles` - 创建文章
- `PUT /api/articles/{id}` - 更新文章
- `DELETE /api/articles/{id}` - 删除文章
- `POST /api/articles/{id}/publish` - 发布文章
- `POST /api/articles/{id}/unpublish` - 取消发布

### 分类
- `GET /api/categories` - 获取分类列表
- `POST /api/categories` - 创建分类
- `PUT /api/categories/{id}` - 更新分类
- `DELETE /api/categories/{id}` - 删除分类

### 用户
- `GET /api/users` - 获取用户列表
- `POST /api/users` - 创建用户
- `DELETE /api/users/{id}` - 删除用户

## 注意事项

1. **数据存储**: 使用内存存储，服务重启后数据会丢失
2. **Token认证**: 登录后获取Token，后续请求需在Header中携带 `Authorization: Bearer <token>`
3. **CORS**: 已配置允许 `localhost:3000` 跨域访问

## License

内部使用
