# CMS 系统设计文档

## 1. 项目概述

- **项目名称**: CMS 内容管理系统
- **类型**: 全栈 Web 应用
- **核心功能**: 后台内容管理与前台内容展示
- **目标用户**: 内容管理员、内容创作者、站点访客

## 2. 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | FastAPI + UV |
| 前端 | React + Vite |
| 数据库 | SQLite |
| MD 渲染 | marked (前端) |
| 认证 | JWT Token |

## 3. 数据模型

### 3.1 Category (分类表)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Integer | 主键，自增 |
| name | String(100) | 分类名称 |
| created_at | DateTime | 创建时间 |

### 3.2 Article (文章表)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Integer | 主键，自增 |
| title | String(200) | 文章标题 |
| content_md | Text | MD 格式源码 |
| content_html | Text | 渲染后 HTML |
| category_id | Integer | 关联分类 ID |
| status | String(20) | published/draft |
| created_at | DateTime | 创建时间 |
| updated_at | DateTime | 更新时间 |

### 3.3 User (管理员)

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Integer | 主键，自增 |
| username | String(50) | 用户名 |
| password_hash | String(255) | 密码哈希 |

## 4. API 设计

### 认证

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 登录，返回 JWT |

### 分类管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/categories | 获取所有分类 |
| POST | /api/categories | 创建分类 |
| PUT | /api/categories/:id | 更新分类 |
| DELETE | /api/categories/:id | 删除分类 |

### 文章管理

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/articles | 获取文章列表（后台） |
| GET | /api/articles/published | 获取已发布文章（前台） |
| GET | /api/articles/:id | 获取单篇文章 |
| POST | /api/articles | 创建文章 |
| PUT | /api/articles/:id | 更新文章 |
| DELETE | /api/articles/:id | 删除文章 |

## 5. 前端路由

| 路径 | 说明 |
|------|------|
| / | 首页，文章列表（按发布时间排序） |
| /article/:id | 文章详情页 |
| /admin | 登录页 |
| /admin/categories | 分类管理 |
| /admin/articles | 文章列表 |
| /admin/articles/new | 新建文章 |
| /admin/articles/:id/edit | 编辑文章 |

## 6. 核心流程

1. **后台创建/编辑文章** → 输入 MD 内容 → 保存时用 marked 渲染 HTML 存库
2. **发布/下架** → 修改 status 字段
3. **前台展示** → 获取已发布文章列表 → 详情页渲染 content_html

## 7. Vite 代理配置

```js
// vite.config.ts
export default defineConfig({
  server: {
    proxy: {
      '/api': 'http://localhost:8000'
    }
  }
})
```

## 8. 项目结构

```
superpowers/
├── backend/
│   ├── app/
│   │   ├── main.py
│   │   ├── models.py
│   │   ├── schemas.py
│   │   ├── crud.py
│   │   └── database.py
│   ├── pyproject.toml
│   └── uv.lock
└── frontend/
    ├── src/
    │   ├── components/
    │   ├── pages/
    │   ├── api/
    │   ├── App.tsx
    │   └── main.tsx
    ├── index.html
    ├── vite.config.ts
    └── package.json
```

## 9. 验收标准

- [ ] 后台：管理员可以登录
- [ ] 后台：可以创建、编辑、删除分类
- [ ] 后台：可以创建、编辑、删除文章
- [ ] 后台：可以发布/下架文章
- [ ] 前台：文章列表按发布时间倒序展示
- [ ] 前台：文章详情页正确渲染 HTML 内容
- [ ] API 通过 Vite 代理访问