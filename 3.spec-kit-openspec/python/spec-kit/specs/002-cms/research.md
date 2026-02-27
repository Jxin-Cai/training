# Research: CMS 内容管理系统

**Date**: 2026-02-27  
**Feature**: 002-cms

## Decisions Made

### 1. Frontend Technology Stack

**Decision**: React 18 + TypeScript + Vite  
**Rationale**: 
- React 18 提供更好的性能和并发特性
- TypeScript 提供类型安全，减少运行时错误
- Vite 提供极快的开发服务器启动和热更新
- 社区活跃，生态丰富

**Alternatives Considered**:
- Next.js: 过于复杂，对于 SPA 应用
- Vue 3: 团队技术栈偏好 React

### 2. State Management

**Decision**: React Context API + useReducer  
**Rationale**:
- 符合用户需求：使用 Context API 管理状态
- 适合 CMS 规模，无需引入 Redux
- 可预测的状态更新模式

**Alternatives Considered**:
- Redux: 过度设计
- Zustand: 非官方方案

### 3. Data Persistence

**Decision**: localStorage (frontend) + SQLite (backend)  
**Rationale**:
- 符合用户需求：localStorage 存储数据
- SQLite 轻量级，无需额外数据库服务
- 适合单用户场景

**Alternatives Considered**:
- PostgreSQL: 过度配置
- Redis: 不适合持久化

### 4. Backend Framework

**Decision**: FastAPI  
**Rationale**:
- 符合用户需求：使用 FastAPI
- 自动生成 OpenAPI 文档
- 异步支持，高性能
- Python 3.11 原生类型提示支持

**Alternatives Considered**:
- Flask: 缺少原生异步支持
- Django: 过度重量级

### 5. Markdown Rendering

**Decision**: markdown-it  
**Rationale**:
- 轻量级，性能好
- 丰富的插件生态
- 支持 GitHub Flavored Markdown

**Alternatives Considered**:
- marked: 功能较少
- remark: 配置复杂

### 6. CSS Layout

**Decision**: CSS Grid + 响应式设计  
**Rationale**:
- 符合用户需求：CSS Grid 布局
- 天然支持响应式
- 代码简洁

### 7. HTML 安全

**Decision**: 保存时渲染，输出时使用 dangerouslySetInnerHTML  
**Rationale**:
- MD 渲染在服务端完成，前端直接展示
- 需对用户输入进行 XSS 过滤
- 使用 DOMPurify 净化 HTML

---

## Implementation Notes

### 前端架构

```
src/
├── contexts/
│   ├── CategoryContext.tsx    # 分类状态管理
│   └── ContentContext.tsx     # 内容状态管理
├── components/
│   ├── Layout/                # 布局组件
│   ├── Category/              # 分类管理组件
│   └── Content/               # 内容管理组件
├── pages/
│   ├── HomePage.tsx           # 前台首页
│   ├── ContentPage.tsx        # 前台详情页
│   ├── AdminCategory.tsx      # 后台分类管理
│   └── AdminContent.tsx       # 后台内容管理
└── services/
    └── api.ts                 # API 调用
```

### 后端架构

```
backend/src/
├── models/
│   ├── category.py            # Category 模型
│   └── content.py             # Content 模型
├── services/
│   ├── category_service.py    # 分类业务逻辑
│   └── content_service.py     # 内容业务逻辑
├── api/
│   ├── category.py            # 分类路由
│   └── content.py             # 内容路由
└── main.py                    # FastAPI 入口
```

### 安全考虑

- MD 内容保存前需净化 HTML
- 分类删除前检查关联内容
- CORS 配置
- 输入验证