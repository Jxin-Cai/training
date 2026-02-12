# 迭代2：用户权限与内容搜索 - 实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 实现用户管理、权限控制和内容搜索功能

**Architecture:** 后端采用 DDD 分层架构，新增 User 聚合根和认证服务；前端使用 Pinia 管理认证状态，路由守卫控制后台访问

**Tech Stack:** Spring Boot, Vue 3, Pinia, Axios

**Worktree:** `.worktrees/iteration2/4.omo-superpowers/java`

---

## Task 1: 用户领域模型

**Files:**
- Create: `backend/src/main/java/com/cms/domain/model/User.java`
- Create: `backend/src/main/java/com/cms/domain/repository/UserRepository.java`
- Create: `backend/src/main/java/com/cms/infrastructure/repository/InMemoryUserRepository.java`

**Step 1: 创建 User 实体**

```java
// backend/src/main/java/com/cms/domain/model/User.java
package com.cms.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class User {
    
    public enum Role {
        SUPER_ADMIN, GUEST
    }
    
    private String id;
    private String username;
    private String password;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private User() {}
    
    public static User create(String username, String password, Role role) {
        User user = new User();
        user.id = UUID.randomUUID().toString();
        user.username = Objects.requireNonNull(username, "Username cannot be null");
        user.password = Objects.requireNonNull(password, "Password cannot be null");
        user.role = Objects.requireNonNull(role, "Role cannot be null");
        user.createdAt = LocalDateTime.now();
        user.updatedAt = LocalDateTime.now();
        return user;
    }
    
    public static User reconstitute(String id, String username, String password, 
                                     Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        User user = new User();
        user.id = id;
        user.username = username;
        user.password = password;
        user.role = role;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        return user;
    }
    
    public void updatePassword(String newPassword) {
        this.password = Objects.requireNonNull(newPassword, "Password cannot be null");
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateRole(Role newRole) {
        this.role = Objects.requireNonNull(newRole, "Role cannot be null");
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean checkPassword(String password) {
        return this.password.equals(password);
    }
    
    public boolean isSuperAdmin() {
        return this.role == Role.SUPER_ADMIN;
    }
    
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Role getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
```

**Step 2: 创建 UserRepository 接口**

```java
// backend/src/main/java/com/cms/domain/repository/UserRepository.java
package com.cms.domain.repository;

import com.cms.domain.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    void deleteById(String id);
    boolean existsByUsername(String username);
    boolean existsById(String id);
}
```

**Step 3: 创建 InMemoryUserRepository 实现**

```java
// backend/src/main/java/com/cms/infrastructure/repository/InMemoryUserRepository.java
package com.cms.infrastructure.repository;

import com.cms.domain.model.User;
import com.cms.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserRepository implements UserRepository {
    
    private final Map<String, User> users = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        // 创建默认超级管理员
        User admin = User.create("admin", "admin123", User.Role.SUPER_ADMIN);
        users.put(admin.getId(), admin);
    }
    
    @Override
    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }
    
    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst();
    }
    
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
    
    @Override
    public void deleteById(String id) {
        users.remove(id);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return users.values().stream()
                .anyMatch(u -> u.getUsername().equals(username));
    }
    
    @Override
    public boolean existsById(String id) {
        return users.containsKey(id);
    }
}
```

**Step 4: 编译验证**

Run: `cd backend && mvn compile -q`
Expected: BUILD SUCCESS

**Step 5: Commit**

```bash
git add backend/src/main/java/com/cms/domain/model/User.java \
        backend/src/main/java/com/cms/domain/repository/UserRepository.java \
        backend/src/main/java/com/cms/infrastructure/repository/InMemoryUserRepository.java
git commit -m "feat(domain): add User entity and repository"
```

---

## Task 2: 用户应用层服务和 DTO

**Files:**
- Create: `backend/src/main/java/com/cms/application/dto/UserDto.java`
- Create: `backend/src/main/java/com/cms/application/dto/CreateUserRequest.java`
- Create: `backend/src/main/java/com/cms/application/dto/UpdateUserRequest.java`
- Create: `backend/src/main/java/com/cms/application/dto/LoginRequest.java`
- Create: `backend/src/main/java/com/cms/application/dto/LoginResponse.java`
- Create: `backend/src/main/java/com/cms/application/service/UserService.java`
- Create: `backend/src/main/java/com/cms/application/service/AuthService.java`

**Step 1: 创建 DTO**

```java
// backend/src/main/java/com/cms/application/dto/UserDto.java
package com.cms.application.dto;

import java.time.LocalDateTime;

public record UserDto(
    String id,
    String username,
    String role,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

```java
// backend/src/main/java/com/cms/application/dto/CreateUserRequest.java
package com.cms.application.dto;

public record CreateUserRequest(
    String username,
    String password,
    String role
) {}
```

```java
// backend/src/main/java/com/cms/application/dto/UpdateUserRequest.java
package com.cms.application.dto;

public record UpdateUserRequest(
    String password,
    String role
) {}
```

```java
// backend/src/main/java/com/cms/application/dto/LoginRequest.java
package com.cms.application.dto;

public record LoginRequest(
    String username,
    String password
) {}
```

```java
// backend/src/main/java/com/cms/application/dto/LoginResponse.java
package com.cms.application.dto;

public record LoginResponse(
    UserDto user,
    String message
) {}
```

**Step 2: 创建 UserService**

```java
// backend/src/main/java/com/cms/application/service/UserService.java
package com.cms.application.service;

import com.cms.application.dto.*;
import com.cms.domain.model.User;
import com.cms.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public UserDto create(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists: " + request.username());
        }
        
        User.Role role = User.Role.valueOf(request.role());
        User user = User.create(request.username(), request.password(), role);
        User saved = userRepository.save(user);
        return toDto(saved);
    }
    
    public Optional<UserDto> findById(String id) {
        return userRepository.findById(id).map(this::toDto);
    }
    
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public Optional<UserDto> update(String id, UpdateUserRequest request) {
        return userRepository.findById(id).map(user -> {
            if (request.password() != null && !request.password().isEmpty()) {
                user.updatePassword(request.password());
            }
            if (request.role() != null) {
                user.updateRole(User.Role.valueOf(request.role()));
            }
            User updated = userRepository.save(user);
            return toDto(updated);
        });
    }
    
    public boolean delete(String id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }
    
    private UserDto toDto(User user) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getRole().name(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
```

**Step 3: 创建 AuthService**

```java
// backend/src/main/java/com/cms/application/service/AuthService.java
package com.cms.application.service;

import com.cms.application.dto.*;
import com.cms.domain.model.User;
import com.cms.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    public Optional<LoginResponse> login(LoginRequest request) {
        return userRepository.findByUsername(request.username())
                .filter(user -> user.checkPassword(request.password()))
                .map(user -> new LoginResponse(toDto(user), "Login successful"));
    }
    
    public Optional<UserDto> findByUsername(String username) {
        return userRepository.findByUsername(username).map(this::toDto);
    }
    
    private UserDto toDto(User user) {
        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getRole().name(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }
}
```

**Step 4: 编译验证**

Run: `cd backend && mvn compile -q`
Expected: BUILD SUCCESS

**Step 5: Commit**

```bash
git add backend/src/main/java/com/cms/application/dto/UserDto.java \
        backend/src/main/java/com/cms/application/dto/CreateUserRequest.java \
        backend/src/main/java/com/cms/application/dto/UpdateUserRequest.java \
        backend/src/main/java/com/cms/application/dto/LoginRequest.java \
        backend/src/main/java/com/cms/application/dto/LoginResponse.java \
        backend/src/main/java/com/cms/application/service/UserService.java \
        backend/src/main/java/com/cms/application/service/AuthService.java
git commit -m "feat(application): add user and auth services with DTOs"
```

---

## Task 3: 认证和用户管理 Controller

**Files:**
- Create: `backend/src/main/java/com/cms/presentation/rest/AuthController.java`
- Create: `backend/src/main/java/com/cms/presentation/rest/UserController.java`

**Step 1: 创建 AuthController**

```java
// backend/src/main/java/com/cms/presentation/rest/AuthController.java
package com.cms.presentation.rest;

import com.cms.application.dto.*;
import com.cms.application.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AuthController {
    
    private final AuthService authService;
    
    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return authService.login(request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401)
                        .body(Map.of("error", "Invalid username or password")));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logout successful"));
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader(value = "X-Username", required = false) String username) {
        if (username == null || username.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));
        }
        return authService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).body(Map.of("error", "User not found")));
    }
}
```

**Step 2: 创建 UserController**

```java
// backend/src/main/java/com/cms/presentation/rest/UserController.java
package com.cms.presentation.rest;

import com.cms.application.dto.*;
import com.cms.application.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class UserController {
    
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public ResponseEntity<List<UserDto>> getAll() {
        return ResponseEntity.ok(userService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById(@PathVariable String id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateUserRequest request) {
        try {
            UserDto created = userService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable String id, 
                                          @RequestBody UpdateUserRequest request) {
        return userService.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (userService.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
```

**Step 3: 编译验证**

Run: `cd backend && mvn compile -q`
Expected: BUILD SUCCESS

**Step 4: Commit**

```bash
git add backend/src/main/java/com/cms/presentation/rest/AuthController.java \
        backend/src/main/java/com/cms/presentation/rest/UserController.java
git commit -m "feat(api): add auth and user management endpoints"
```

---

## Task 4: 权限拦截器

**Files:**
- Create: `backend/src/main/java/com/cms/infrastructure/config/AuthInterceptor.java`
- Create: `backend/src/main/java/com/cms/infrastructure/config/WebMvcConfig.java`

**Step 1: 创建 AuthInterceptor**

```java
// backend/src/main/java/com/cms/infrastructure/config/AuthInterceptor.java
package com.cms.infrastructure.config;

import com.cms.domain.model.User;
import com.cms.domain.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    private final UserRepository userRepository;
    
    public AuthInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                            Object handler) throws Exception {
        String method = request.getMethod();
        String path = request.getRequestURI();
        
        // OPTIONS 请求直接放行（CORS 预检）
        if ("OPTIONS".equals(method)) {
            return true;
        }
        
        // GET 请求默认放行（公开读取）
        if ("GET".equals(method) && !path.startsWith("/api/users")) {
            return true;
        }
        
        // 检查认证
        String username = request.getHeader("X-Username");
        if (username == null || username.isEmpty()) {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Authentication required\"}");
            return false;
        }
        
        // 检查用户是否为超级管理员
        return userRepository.findByUsername(username)
                .map(user -> {
                    if (user.isSuperAdmin()) {
                        return true;
                    }
                    try {
                        response.setStatus(403);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"Access denied. Super admin required.\"}");
                    } catch (Exception e) {
                        // ignore
                    }
                    return false;
                })
                .orElseGet(() -> {
                    try {
                        response.setStatus(401);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"User not found\"}");
                    } catch (Exception e) {
                        // ignore
                    }
                    return false;
                });
    }
}
```

**Step 2: 创建 WebMvcConfig**

```java
// backend/src/main/java/com/cms/infrastructure/config/WebMvcConfig.java
package com.cms.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    private final AuthInterceptor authInterceptor;
    
    public WebMvcConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/auth/**",           // 认证接口放行
                    "/api/contents/published", // 已发布内容放行
                    "/api/contents/search"     // 搜索接口放行
                );
    }
}
```

**Step 3: 编译验证**

Run: `cd backend && mvn compile -q`
Expected: BUILD SUCCESS

**Step 4: Commit**

```bash
git add backend/src/main/java/com/cms/infrastructure/config/AuthInterceptor.java \
        backend/src/main/java/com/cms/infrastructure/config/WebMvcConfig.java
git commit -m "feat(security): add auth interceptor for permission control"
```

---

## Task 5: 内容搜索功能（后端）

**Files:**
- Modify: `backend/src/main/java/com/cms/domain/model/Content.java`
- Modify: `backend/src/main/java/com/cms/domain/repository/ContentRepository.java`
- Modify: `backend/src/main/java/com/cms/infrastructure/repository/InMemoryContentRepository.java`
- Modify: `backend/src/main/java/com/cms/application/dto/ContentDto.java`
- Modify: `backend/src/main/java/com/cms/application/dto/CreateContentRequest.java`
- Modify: `backend/src/main/java/com/cms/application/dto/UpdateContentRequest.java`
- Modify: `backend/src/main/java/com/cms/application/service/ContentService.java`
- Modify: `backend/src/main/java/com/cms/presentation/rest/ContentController.java`

**Step 1: 修改 Content 实体添加 keywords 字段**

在 `Content.java` 中添加:
- 字段: `private String keywords;`
- 方法: `public void updateKeywords(String keywords)`
- Getter: `public String getKeywords()`
- 修改 `create()` 和 `reconstitute()` 方法

**Step 2: 修改 ContentRepository 添加 search 方法**

```java
// 在 ContentRepository.java 中添加
List<Content> search(String keyword);
```

**Step 3: 实现 search 方法**

```java
// 在 InMemoryContentRepository.java 中添加
@Override
public List<Content> search(String keyword) {
    String lowerKeyword = keyword.toLowerCase();
    return contents.values().stream()
            .filter(c -> c.getStatus() == Content.Status.PUBLISHED)
            .filter(c -> {
                boolean titleMatch = c.getTitle().toLowerCase().contains(lowerKeyword);
                boolean keywordMatch = c.getKeywords() != null && 
                        c.getKeywords().toLowerCase().contains(lowerKeyword);
                return titleMatch || keywordMatch;
            })
            .collect(Collectors.toList());
}
```

**Step 4: 修改 DTO 添加 keywords**

在 `ContentDto.java`, `CreateContentRequest.java`, `UpdateContentRequest.java` 中添加 keywords 字段

**Step 5: 修改 ContentService**

添加 `search(String keyword)` 方法，更新 `create()` 和 `update()` 处理 keywords

**Step 6: 修改 ContentController 添加搜索端点**

```java
@GetMapping("/search")
public ResponseEntity<List<ContentDto>> search(@RequestParam String q) {
    return ResponseEntity.ok(contentService.search(q));
}
```

**Step 7: 编译验证**

Run: `cd backend && mvn compile -q`
Expected: BUILD SUCCESS

**Step 8: Commit**

```bash
git add backend/src/main/java/com/cms/domain/model/Content.java \
        backend/src/main/java/com/cms/domain/repository/ContentRepository.java \
        backend/src/main/java/com/cms/infrastructure/repository/InMemoryContentRepository.java \
        backend/src/main/java/com/cms/application/dto/ContentDto.java \
        backend/src/main/java/com/cms/application/dto/CreateContentRequest.java \
        backend/src/main/java/com/cms/application/dto/UpdateContentRequest.java \
        backend/src/main/java/com/cms/application/service/ContentService.java \
        backend/src/main/java/com/cms/presentation/rest/ContentController.java
git commit -m "feat(content): add keywords field and search functionality"
```

---

## Task 6: 前端认证状态管理

**Files:**
- Create: `frontend/src/stores/auth.ts`
- Create: `frontend/src/types/auth.ts`
- Modify: `frontend/src/api/index.ts`
- Modify: `frontend/src/types/index.ts`

**Step 1: 创建认证类型定义**

```typescript
// frontend/src/types/auth.ts
export interface User {
  id: string
  username: string
  role: 'SUPER_ADMIN' | 'GUEST'
  createdAt: string
  updatedAt: string
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResponse {
  user: User
  message: string
}

export interface CreateUserRequest {
  username: string
  password: string
  role: string
}

export interface UpdateUserRequest {
  password?: string
  role?: string
}
```

**Step 2: 更新 types/index.ts 导出**

在 `frontend/src/types/index.ts` 末尾添加:
```typescript
export * from './auth'
```

**Step 3: 添加 API 方法**

在 `frontend/src/api/index.ts` 中添加:
```typescript
import type { User, LoginRequest, LoginResponse, CreateUserRequest, UpdateUserRequest } from '@/types'

// 添加请求拦截器
api.interceptors.request.use((config) => {
  const username = localStorage.getItem('username')
  if (username) {
    config.headers['X-Username'] = username
  }
  return config
})

export const authApi = {
  login: (data: LoginRequest) => api.post<LoginResponse>('/auth/login', data),
  logout: () => api.post('/auth/logout'),
  me: () => api.get<User>('/auth/me'),
}

export const userApi = {
  getAll: () => api.get<User[]>('/users'),
  getById: (id: string) => api.get<User>(`/users/${id}`),
  create: (data: CreateUserRequest) => api.post<User>('/users', data),
  update: (id: string, data: UpdateUserRequest) => api.put<User>(`/users/${id}`, data),
  delete: (id: string) => api.delete(`/users/${id}`),
}
```

**Step 4: 创建认证 Store**

```typescript
// frontend/src/stores/auth.ts
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api'
import type { User, LoginRequest } from '@/types'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<User | null>(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  const isAuthenticated = computed(() => !!user.value)
  const isSuperAdmin = computed(() => user.value?.role === 'SUPER_ADMIN')

  async function login(credentials: LoginRequest) {
    loading.value = true
    error.value = null
    try {
      const { data } = await authApi.login(credentials)
      user.value = data.user
      localStorage.setItem('username', data.user.username)
      return true
    } catch (e: any) {
      error.value = e.response?.data?.error || 'Login failed'
      return false
    } finally {
      loading.value = false
    }
  }

  async function logout() {
    try {
      await authApi.logout()
    } finally {
      user.value = null
      localStorage.removeItem('username')
    }
  }

  async function checkAuth() {
    const username = localStorage.getItem('username')
    if (!username) return false
    try {
      const { data } = await authApi.me()
      user.value = data
      return true
    } catch {
      localStorage.removeItem('username')
      return false
    }
  }

  return {
    user,
    loading,
    error,
    isAuthenticated,
    isSuperAdmin,
    login,
    logout,
    checkAuth,
  }
})
```

**Step 5: Commit**

```bash
git add frontend/src/types/auth.ts \
        frontend/src/types/index.ts \
        frontend/src/api/index.ts \
        frontend/src/stores/auth.ts
git commit -m "feat(frontend): add auth store and API integration"
```

---

## Task 7: 登录页面

**Files:**
- Create: `frontend/src/views/LoginView.vue`
- Modify: `frontend/src/router/index.ts`

**Step 1: 创建登录页面**

```vue
<!-- frontend/src/views/LoginView.vue -->
<template>
  <div class="login-container">
    <div class="login-card">
      <h1>CMS 管理后台</h1>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">用户名</label>
          <input 
            id="username" 
            v-model="username" 
            type="text" 
            placeholder="请输入用户名"
            required
          />
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input 
            id="password" 
            v-model="password" 
            type="password" 
            placeholder="请输入密码"
            required
          />
        </div>
        <div v-if="authStore.error" class="error-message">
          {{ authStore.error }}
        </div>
        <button type="submit" :disabled="authStore.loading">
          {{ authStore.loading ? '登录中...' : '登录' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const username = ref('')
const password = ref('')

async function handleLogin() {
  const success = await authStore.login({
    username: username.value,
    password: password.value
  })
  if (success) {
    router.push('/admin')
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
}

.login-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  width: 100%;
  max-width: 400px;
}

.login-card h1 {
  text-align: center;
  margin-bottom: 2rem;
  color: #333;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #666;
}

.form-group input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.error-message {
  color: #e53935;
  margin-bottom: 1rem;
  text-align: center;
}

button {
  width: 100%;
  padding: 0.75rem;
  background: #1976d2;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  cursor: pointer;
}

button:hover:not(:disabled) {
  background: #1565c0;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
```

**Step 2: 添加路由和路由守卫**

修改 `frontend/src/router/index.ts`:
```typescript
import { createRouter, createWebHistory } from 'vue-router'
import PublicLayout from '@/layouts/PublicLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue')
    },
    {
      path: '/',
      component: PublicLayout,
      children: [
        {
          path: '',
          name: 'home',
          component: () => import('@/views/public/HomeView.vue')
        },
        {
          path: 'content/:id',
          name: 'content-detail',
          component: () => import('@/views/public/ContentDetailView.vue')
        }
      ]
    },
    {
      path: '/admin',
      component: AdminLayout,
      redirect: '/admin/content',
      meta: { requiresAuth: true },
      children: [
        {
          path: 'categories',
          name: 'admin-categories',
          component: () => import('@/views/admin/CategoriesView.vue')
        },
        {
          path: 'content',
          name: 'admin-content',
          component: () => import('@/views/admin/ContentView.vue')
        },
        {
          path: 'users',
          name: 'admin-users',
          component: () => import('@/views/admin/UsersView.vue')
        }
      ]
    }
  ]
})

router.beforeEach(async (to, from, next) => {
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const authStore = useAuthStore()
    
    if (!authStore.isAuthenticated) {
      await authStore.checkAuth()
    }
    
    if (!authStore.isSuperAdmin) {
      return next('/login')
    }
  }
  next()
})

export default router
```

**Step 3: Commit**

```bash
git add frontend/src/views/LoginView.vue \
        frontend/src/router/index.ts
git commit -m "feat(frontend): add login page with route guards"
```

---

## Task 8: 用户管理页面

**Files:**
- Create: `frontend/src/views/admin/UsersView.vue`

**Step 1: 创建用户管理页面**

```vue
<!-- frontend/src/views/admin/UsersView.vue -->
<template>
  <div class="users-view">
    <div class="header">
      <h1>用户管理</h1>
      <button class="btn-primary" @click="showCreateDialog = true">新增用户</button>
    </div>

    <table class="users-table">
      <thead>
        <tr>
          <th>用户名</th>
          <th>角色</th>
          <th>创建时间</th>
          <th>操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.id">
          <td>{{ user.username }}</td>
          <td>
            <span :class="['role-badge', user.role.toLowerCase()]">
              {{ user.role === 'SUPER_ADMIN' ? '超级管理员' : '游客' }}
            </span>
          </td>
          <td>{{ formatDate(user.createdAt) }}</td>
          <td>
            <button class="btn-small" @click="editUser(user)">编辑</button>
            <button class="btn-small btn-danger" @click="deleteUser(user)">删除</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- 新增/编辑对话框 -->
    <div v-if="showCreateDialog || editingUser" class="dialog-overlay">
      <div class="dialog">
        <h2>{{ editingUser ? '编辑用户' : '新增用户' }}</h2>
        <form @submit.prevent="saveUser">
          <div class="form-group">
            <label>用户名</label>
            <input v-model="form.username" :disabled="!!editingUser" required />
          </div>
          <div class="form-group">
            <label>密码{{ editingUser ? '（留空不修改）' : '' }}</label>
            <input v-model="form.password" type="password" :required="!editingUser" />
          </div>
          <div class="form-group">
            <label>角色</label>
            <select v-model="form.role" required>
              <option value="SUPER_ADMIN">超级管理员</option>
              <option value="GUEST">游客</option>
            </select>
          </div>
          <div class="dialog-actions">
            <button type="button" @click="closeDialog">取消</button>
            <button type="submit" class="btn-primary">保存</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { userApi } from '@/api'
import type { User } from '@/types'

const users = ref<User[]>([])
const showCreateDialog = ref(false)
const editingUser = ref<User | null>(null)

const form = reactive({
  username: '',
  password: '',
  role: 'GUEST'
})

onMounted(async () => {
  await loadUsers()
})

async function loadUsers() {
  const { data } = await userApi.getAll()
  users.value = data
}

function editUser(user: User) {
  editingUser.value = user
  form.username = user.username
  form.password = ''
  form.role = user.role
}

async function saveUser() {
  if (editingUser.value) {
    await userApi.update(editingUser.value.id, {
      password: form.password || undefined,
      role: form.role
    })
  } else {
    await userApi.create({
      username: form.username,
      password: form.password,
      role: form.role
    })
  }
  closeDialog()
  await loadUsers()
}

async function deleteUser(user: User) {
  if (confirm(`确定要删除用户 ${user.username} 吗？`)) {
    await userApi.delete(user.id)
    await loadUsers()
  }
}

function closeDialog() {
  showCreateDialog.value = false
  editingUser.value = null
  form.username = ''
  form.password = ''
  form.role = 'GUEST'
}

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleString('zh-CN')
}
</script>

<style scoped>
.users-view { padding: 1rem; }
.header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 1rem; }
.users-table { width: 100%; border-collapse: collapse; }
.users-table th, .users-table td { padding: 0.75rem; text-align: left; border-bottom: 1px solid #eee; }
.role-badge { padding: 0.25rem 0.5rem; border-radius: 4px; font-size: 0.875rem; }
.role-badge.super_admin { background: #e3f2fd; color: #1976d2; }
.role-badge.guest { background: #f5f5f5; color: #666; }
.btn-primary { background: #1976d2; color: white; border: none; padding: 0.5rem 1rem; border-radius: 4px; cursor: pointer; }
.btn-small { padding: 0.25rem 0.5rem; margin-right: 0.5rem; border: 1px solid #ddd; background: white; border-radius: 4px; cursor: pointer; }
.btn-danger { color: #e53935; border-color: #e53935; }
.dialog-overlay { position: fixed; top: 0; left: 0; right: 0; bottom: 0; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; }
.dialog { background: white; padding: 1.5rem; border-radius: 8px; width: 100%; max-width: 400px; }
.form-group { margin-bottom: 1rem; }
.form-group label { display: block; margin-bottom: 0.5rem; }
.form-group input, .form-group select { width: 100%; padding: 0.5rem; border: 1px solid #ddd; border-radius: 4px; }
.dialog-actions { display: flex; justify-content: flex-end; gap: 0.5rem; margin-top: 1rem; }
</style>
```

**Step 2: Commit**

```bash
git add frontend/src/views/admin/UsersView.vue
git commit -m "feat(frontend): add user management page"
```

---

## Task 9: 前台搜索功能

**Files:**
- Modify: `frontend/src/api/index.ts`
- Modify: `frontend/src/types/index.ts`
- Modify: `frontend/src/views/public/HomeView.vue`

**Step 1: 添加搜索 API**

在 `contentApi` 中添加:
```typescript
search: (q: string) => api.get<Content[]>('/contents/search', { params: { q } }),
```

**Step 2: 更新 Content 类型添加 keywords**

在 `frontend/src/types/index.ts` 的 Content 接口添加:
```typescript
keywords: string | null
```

**Step 3: 更新首页添加搜索框**

在 HomeView.vue 中添加搜索框和搜索结果展示

**Step 4: Commit**

```bash
git add frontend/src/api/index.ts \
        frontend/src/types/index.ts \
        frontend/src/views/public/HomeView.vue
git commit -m "feat(frontend): add content search functionality"
```

---

## Task 10: 后台内容编辑器添加关键词

**Files:**
- Modify: `frontend/src/views/admin/ContentView.vue`

**Step 1: 添加关键词输入框**

在内容编辑表单中添加关键词输入框

**Step 2: 更新请求添加 keywords**

**Step 3: Commit**

```bash
git add frontend/src/views/admin/ContentView.vue
git commit -m "feat(frontend): add keywords field to content editor"
```

---

## Task 11: 后台布局添加用户菜单和登出

**Files:**
- Modify: `frontend/src/layouts/AdminLayout.vue`

**Step 1: 添加用户信息和登出按钮**

**Step 2: Commit**

```bash
git add frontend/src/layouts/AdminLayout.vue
git commit -m "feat(frontend): add user menu and logout to admin layout"
```

---

## Task 12: 最终测试和清理

**Step 1: 运行后端测试**

Run: `cd backend && mvn test`
Expected: All tests pass

**Step 2: 运行前端测试**

Run: `cd frontend && npm test`
Expected: All tests pass

**Step 3: 启动服务并手动验证**

1. 启动后端: `cd backend && mvn spring-boot:run`
2. 启动前端: `cd frontend && npm run dev`
3. 验证登录功能
4. 验证用户管理
5. 验证权限控制
6. 验证内容搜索

**Step 4: 最终提交**

```bash
git add .
git commit -m "chore: iteration2 complete - user auth and content search"
```

---

## 任务汇总

| Task | 描述 | 预估时间 |
|------|------|----------|
| Task 1 | 用户领域模型 | 10 min |
| Task 2 | 用户应用层服务和 DTO | 15 min |
| Task 3 | 认证和用户管理 Controller | 10 min |
| Task 4 | 权限拦截器 | 10 min |
| Task 5 | 内容搜索功能（后端） | 15 min |
| Task 6 | 前端认证状态管理 | 15 min |
| Task 7 | 登录页面 | 10 min |
| Task 8 | 用户管理页面 | 15 min |
| Task 9 | 前台搜索功能 | 10 min |
| Task 10 | 后台内容编辑器添加关键词 | 5 min |
| Task 11 | 后台布局添加用户菜单 | 5 min |
| Task 12 | 最终测试和清理 | 10 min |
| **Total** | | **~130 min** |
