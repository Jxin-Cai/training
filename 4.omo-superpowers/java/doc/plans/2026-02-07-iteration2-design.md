# 迭代2：用户权限与内容搜索 - 设计文档

## 概述

本迭代实现三个核心功能：
1. **用户管理模块** - 管理员账号的增删改查
2. **权限控制** - 超级管理员 vs 游客的权限区分
3. **内容搜索功能** - 按标题/关键词检索

## 技术决策

| 决策项 | 选择 | 理由 |
|--------|------|------|
| 认证方案 | 简化版 Basic Auth | MVP 快速验证，实现简单 |
| 关键词存储 | 单字段字符串（逗号分隔） | 实现简单，满足需求 |
| 搜索实现 | 简单内存搜索 | 数据量小，无需引入搜索引擎 |
| 前端权限控制 | 路由守卫 | 体验好，拦截未授权访问 |

---

## 第一部分：用户领域模型

### User 实体（聚合根）

```java
public class User {
    public enum Role { SUPER_ADMIN, GUEST }
    
    private String id;
    private String username;     // 唯一标识，用于登录
    private String password;     // 明文存储（简化版）
    private Role role;           // 角色：超级管理员 / 游客
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### UserRepository 接口

```java
public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);  // 登录验证
    List<User> findAll();
    void deleteById(String id);
    boolean existsByUsername(String username);
}
```

### 初始数据

系统启动时自动创建默认超级管理员账号：
- 用户名：`admin`
- 密码：`admin123`

---

## 第二部分：认证与权限控制

### 认证 Controller

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    POST /api/auth/login    // 登录验证，返回用户信息
    POST /api/auth/logout   // 登出（前端清除状态即可）
    GET  /api/auth/me       // 获取当前登录用户
}
```

### 用户管理 Controller

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    GET    /api/users          // 用户列表
    GET    /api/users/{id}     // 用户详情
    POST   /api/users          // 创建用户
    PUT    /api/users/{id}     // 更新用户
    DELETE /api/users/{id}     // 删除用户
}
```

### 权限拦截器

创建 `AuthInterceptor`，拦截需要权限的 API：
- `/api/users/**` - 仅超级管理员
- `/api/categories/**` (POST/PUT/DELETE) - 仅超级管理员
- `/api/contents/**` (POST/PUT/DELETE) - 仅超级管理员
- GET 请求默认放行（公开读取）

### 前端路由守卫

```typescript
// router/index.ts
router.beforeEach((to, from, next) => {
  if (to.path.startsWith('/admin')) {
    const user = useAuthStore().user
    if (!user || user.role !== 'SUPER_ADMIN') {
      return next('/login')
    }
  }
  next()
})
```

---

## 第三部分：内容搜索功能

### Content 实体扩展

```java
public class Content {
    // ... 现有字段 ...
    private String keywords;  // 新增：逗号分隔的关键词
    
    public void updateKeywords(String keywords) {
        this.keywords = keywords;
        this.updatedAt = LocalDateTime.now();
    }
}
```

### 搜索 API

```
GET /api/contents/search?q={keyword}
```

搜索逻辑：
- 遍历内容，匹配标题或关键词（忽略大小写）
- 仅返回已发布的内容

### 前端搜索组件

- 前台首页添加搜索框
- 后台内容编辑器添加关键词输入框

---

## 第四部分：实施文件清单

### 后端新增/修改文件

| 层级 | 文件 | 说明 |
|------|------|------|
| Domain | `User.java` | 用户实体（聚合根） |
| Domain | `UserRepository.java` | 用户仓储接口 |
| Infrastructure | `InMemoryUserRepository.java` | 用户仓储实现 |
| Application | `UserService.java` | 用户管理服务 |
| Application | `AuthService.java` | 认证服务 |
| Application | `UserDto.java`, `CreateUserRequest.java` 等 | DTO |
| Presentation | `AuthController.java` | 认证接口 |
| Presentation | `UserController.java` | 用户管理接口 |
| Infrastructure | `AuthInterceptor.java` | 权限拦截器 |
| 修改 | `Content.java` | 添加 keywords 字段 |
| 修改 | `ContentRepository.java` | 添加 search 方法 |
| 修改 | `ContentController.java` | 添加搜索端点 |

### 前端新增/修改文件

| 目录 | 文件 | 说明 |
|------|------|------|
| views | `LoginView.vue` | 登录页面 |
| views/admin | `UsersView.vue` | 用户管理页面 |
| stores | `auth.ts` | 认证状态管理 |
| api | `index.ts` | 添加 auth/user API |
| 修改 | `router/index.ts` | 添加路由守卫 |
| 修改 | `HomeView.vue` | 添加搜索框 |
| 修改 | `ContentView.vue` | 添加关键词输入 |

---

## 创建日期

2026-02-07
