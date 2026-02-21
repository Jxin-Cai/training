# Story 1.2: 创建前端项目结构

Status: ready-for-dev

## Story

As a **开发者**,
I want **创建Vue.js前端项目**,
so that **后续UI开发有统一的基础**.

## Acceptance Criteria

1. **AC1: Vue.js项目初始化**
   - Given 开发环境已准备好（Node.js 18+）
   - When 创建Vue.js项目
   - Then 项目成功创建，包含package.json和基本配置

2. **AC2: 前端目录结构**
   - Given Vue.js项目已创建
   - When 创建前端目录结构
   - Then 项目包含以下目录结构：
     ```
     src/
     ├── views/           # 页面组件
     ├── components/      # 通用组件
     ├── api/             # API服务
     ├── store/           # Pinia状态
     ├── router/          # 路由配置
     ├── utils/           # 工具函数
     └── styles/          # 样式文件
     ```

3. **AC3: 依赖配置**
   - Given 目录结构已创建
   - When 配置package.json
   - Then 包含以下依赖：
     - Vue 3.x
     - Vue Router 4.x
     - Pinia（状态管理）
     - Axios（HTTP客户端）
     - Ant Design Vue（UI组件库）
     - Markdown-it（Markdown渲染）

4. **AC4: 基础配置文件**
   - Given 依赖已配置
   - When 创建配置文件
   - Then 包含：
     - vite.config.js（构建配置）
     - router/index.js（路由配置）
     - store/index.js（状态管理配置）
     - api/index.js（API封装）

5. **AC5: 入口文件**
   - Given 配置文件已创建
   - When 创建入口文件
   - Then 包含：
     - main.js（应用入口）
     - App.vue（根组件）
     - index.html（HTML模板）

## Tasks / Subtasks

- [ ] Task 1: 初始化Vue.js项目 (AC: 1)
  - [ ] 1.1 使用npm create vue@latest创建项目
  - [ ] 1.2 配置Node.js 18+
  - [ ] 1.3 验证项目可启动

- [ ] Task 2: 创建目录结构 (AC: 2)
  - [ ] 2.1 创建views目录
  - [ ] 2.2 创建components目录
  - [ ] 2.3 创建api目录
  - [ ] 2.4 创建store目录
  - [ ] 2.5 创建router目录
  - [ ] 2.6 创建utils目录
  - [ ] 2.7 创建styles目录

- [ ] Task 3: 安装依赖 (AC: 3)
  - [ ] 3.1 安装Vue Router
  - [ ] 3.2 安装Pinia
  - [ ] 3.3 安装Axios
  - [ ] 3.4 安装Ant Design Vue
  - [ ] 3.5 安装Markdown-it

- [ ] Task 4: 创建配置文件 (AC: 4)
  - [ ] 4.1 配置vite.config.js
  - [ ] 4.2 配置router/index.js
  - [ ] 4.3 配置store/index.js
  - [ ] 4.4 配置api/index.js

- [ ] Task 5: 创建入口文件 (AC: 5)
  - [ ] 5.1 创建main.js
  - [ ] 5.2 创建App.vue
  - [ ] 5.3 创建index.html

## Dev Notes

### 架构约束 (from Architecture.md)

**前端技术栈：**

| 组件 | 版本/说明 |
|------|----------|
| **框架** | Vue.js 3.x |
| **构建工具** | Vite |
| **路由** | Vue Router 4.x |
| **状态管理** | Pinia |
| **HTTP客户端** | Axios |
| **UI组件库** | Ant Design Vue |
| **Markdown渲染** | Markdown-it |

### API服务封装 (from Architecture.md)

**Axios封装要求：**
- 基础URL配置
- 请求/响应拦截器
- 统一错误处理
- Token自动添加

**API响应格式：**
```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### UX设计要求 (from UX Design)

**设计系统：Ant Design Vue**

| 组件 | 用途 |
|------|------|
| Button | 操作按钮 |
| Input | 文本输入 |
| Table | 文章列表、用户列表 |
| Form | 文章表单、分类表单 |
| Modal | 确认对话框 |
| Menu | 侧边栏导航 |
| Pagination | 分页 |

**自定义组件（后续创建）：**
- MarkdownEditor - 左右分栏编辑器
- ArticleCard - 文章卡片
- CategoryFilter - 分类筛选

### 视觉规范 (from UX Design)

**配色：**
- 主色：#1890ff（蓝色）
- 成功色：#52c41a（绿色）
- 警告色：#faad14（橙色）
- 错误色：#ff4d4f（红色）

**布局：**
- 前台：单栏居中，最大宽度720px
- 后台：左侧边栏(200px) + 右侧内容区

### Project Structure Notes

**完整前端项目结构：**

```
java-frontend/
├── package.json
├── vite.config.js
├── index.html
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
│   ├── utils/
│   │   └── index.js
│   └── styles/
│       └── global.css
└── public/
    └── favicon.ico
```

### References

- [Source: _bmad-output/planning-artifacts/architecture.md#Frontend Architecture]
- [Source: _bmad-output/planning-artifacts/epics.md#Story 1.2]
- [Source: _bmad-output/planning-artifacts/ux-design-specification.md#Design System Foundation]
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
