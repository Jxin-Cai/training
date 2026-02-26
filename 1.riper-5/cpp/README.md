# 轻量级 CMS 示例（Vue + C++ Poco）

本项目是一个前后端分离的轻量级 CMS（内容管理系统）示例，前端基于 Vue，后端基于 C++17/20 与 Poco 框架开发，数据暂存于内存。  
它聚焦于 **Markdown 内容管理** 与 **前台展示** 的核心能力，适合作为教学与原型示例。

---

## 功能概览

- **前台展示端**
  - **内容列表页**：按发布时间倒序展示已发布内容，可查看标题、发布时间、所属一级分类等基础信息。
  - **内容详情页**：展示指定内容的详情，渲染并展示保存时生成的 HTML 内容。

- **后台管理端**
  - **一级分类管理**
    - 分类列表：查看所有一级分类。
    - 新增分类：创建新的一级分类（名称唯一）。
    - 编辑分类：修改分类名称等基础信息。
    - 删除分类：删除无内容引用的分类（若有内容引用，删除策略依照实现约定，可在 DEV_PLAN 中查看）。

  - **Markdown 内容管理**
    - 内容列表：查看所有内容，支持按状态（草稿 / 发布）区分。
    - 新建内容：创建新的 Markdown 内容，关联一个一级分类。
    - 编辑内容：修改标题、所属分类、Markdown 正文等信息。
    - 删除内容：从系统中删除指定内容。
    - 状态切换：在“草稿 / 发布”状态间切换，发布时会设置发布时间（`publishedAt`）。
    - 详情查看：在后台查看内容详情和渲染后的 HTML。

- **Markdown 文件上传 / 在线编辑**
  - **文件上传**：上传本地 `.md` 文件，由后端接收后解析为 Markdown 文本并创建对应内容（默认为草稿或根据实现约定）。
  - **在线编辑**：在后台页面中直接编辑 Markdown 文本，保存时自动调用后端渲染为 HTML 并持久化于内存。

---

## 系统架构与技术栈

### 架构概览

- **前后端分离部署**
  - 前端：构建为静态资源，可通过任意静态服务器（如 `serve` 或 Nginx）托管。
  - 后端：独立的 C++ Poco HTTP API 服务，对外提供 JSON API。
  - 前后端通过 HTTP/HTTPS 接口通信。

### 前端技术栈

- **框架**：Vue.js
- **构建工具**：npm + `npm run build`
- **主要职责**：
  - 提供后台管理 UI（分类管理、内容管理、Markdown 编辑与上传）。
  - 提供前台展示 UI（内容列表与内容详情页）。
  - 通过封装的 API 模块与 C++ 后端交互。

### 后端技术栈

- **语言**：C++17/20
- **框架**：Poco (C++ Portable Components)
- **构建系统**：CMake
- **数据存储**：内存存储（基于标准容器的 Repository 实现）
- **架构风格**：DDD 分层（Presentation / Application / Domain / Infrastructure）

---

## 功能说明（用户视角）

### 前台展示端

- **内容列表页**
  - 路由示例：`/` 或 `/posts`
  - 行为：
    - 显示所有已发布内容（`status = published`）。
    - 按发布时间 `publishedAt` 倒序排序。
    - 列表项展示：标题、分类名和发布时间等。
    - 可根据后续扩展支持按分类过滤/分页（详见 `DEV_PLAN.md` 的 Roadmap）。

- **内容详情页**
  - 路由示例：`/posts/:id`
  - 行为：
    - 根据 URL 中的内容 ID 调用后端 API 获取内容详情。
    - 展示：标题、分类、发布时间、渲染后的 HTML 内容。
    - HTML 内容来源于后端保存时渲染的结果（非前端实时 Markdown 渲染）。

### 后台管理端

- **一级分类管理**
  - 分类列表页：
    - 展示所有一级分类。
    - 提供“新增”、“编辑”、“删除”等操作入口。
  - 新增 / 编辑分类：
    - 字段：分类名称（必填，通常要求唯一）、可选描述等。
    - 提交时会调用后端对应 API。
  - 删除分类：
    - 删除前，会检查是否存在关联内容。
    - 若有内容引用，则阻止删除并返回错误（或提示用户先清理内容）；具体策略以实现为准，并在 `DEV_PLAN.md` 中明确。

- **Markdown 内容管理**
  - 内容列表页：
    - 展示所有内容记录。
    - 字段：标题、所属分类、状态（草稿 / 发布）、发布时间、创建时间等。
    - 操作：编辑、删除、切换状态（草稿 ↔ 发布）。
  - 新建 / 编辑内容：
    - 字段：
      - 标题
      - 所属一级分类
      - Markdown 正文（文本编辑区）
      - 状态（草稿 / 发布）
    - 保存行为：
      - 后端接收 Markdown 正文。
      - 后端在保存过程中，调用 Markdown 渲染器生成 HTML，并与 Markdown 原文一并保存。
  - 发布 / 草稿切换：
    - 由后台操作触发。
    - 切换为“发布”时，如果此前从未发布，则设置 `publishedAt` 为当前时间。
    - 切换回“草稿”时，保留历史 `publishedAt` 或清空，由实现约定并在 `DEV_PLAN.md` 写明。

- **Markdown 文件上传 / 在线编辑**
  - 文件上传：
    - 在内容新建页面，支持选择本地 `.md` 文件上传。
    - 后端读取文件内容作为 Markdown 正文。
    - 默认创建新的内容记录（状态默认为草稿或依据实现约定）。
  - 在线编辑：
    - 内嵌 Markdown 编辑器组件。
    - 支持基本的 Markdown 语法编辑。
    - 可在前端即时预览（可选），实际用于前台展示的 HTML 以后台渲染结果为准。

---

## 依赖与构建

- **后端**：需要 C++17 工具链、CMake、**Poco**（Net、JSON、Foundation、Util）。  
  - macOS：`brew install poco`  
  - 其他系统请参考 [Poco 官方文档](https://pocoproject.org/) 安装后，再执行后端构建。
- **前端**：Node.js 18+、npm。

## 快速开始

> 以下步骤假定你已正确安装 C++17/20 工具链、CMake、Node.js、Poco 等依赖。

### 1. 克隆项目并进入目录

```bash
git clone <your-repo-url>.git
cd training/1.riper-5/cpp
```

### 2. 构建并运行后端（C++ Poco）

```bash
cd backend
mkdir -p build
cd build
cmake .. -DCMAKE_BUILD_TYPE=Debug   # 或 Release
cmake --build . --config Debug
./app   # 或 ./bin/app，视 CMake 生成路径而定
```

后端默认监听 **http://localhost:8080**。  
运行单元测试：在 `backend/build` 下执行 `ctest` 或直接运行 `./unit_repos`。

### 3. 安装并运行前端（Vue）

```bash
cd frontend
npm install
npm run dev
```

开发模式下前端运行在 **http://localhost:5173**，并通过 Vite 代理将 `/api` 转发到后端 8080 端口。

### 4. 体验完整内容发布流程

1. 打开后台管理端（如 `http://localhost:5173/admin`）。
2. 创建一个一级分类，例如“技术博客”。
3. 新建一篇内容：
   - 选择刚创建的分类。
   - 编写标题与 Markdown 正文，或上传一个 `.md` 文件。
   - 保存为“草稿”，确认在列表中出现。
4. 将该内容状态切换为“发布”。
5. 打开前台展示端（如 `http://localhost:5173/` 或 `/posts`），查看列表中是否出现刚刚发布的内容。
6. 点击进入详情页面，确认 HTML 内容渲染正确。

---

## 项目目录结构（1.0 MVP）

```bash
1.riper-5/cpp/
├── frontend/                # Vue 3 + Vite 前端
│   ├── src/views/           # 前台 PostList、PostDetail；后台 admin/CategoryList、ContentList、ContentForm
│   ├── src/router/          # 路由
│   ├── src/api/client.js    # REST 封装
│   ├── package.json
│   └── vite.config.js       # 开发代理 /api -> localhost:8080
├── backend/                  # C++17 Poco 后端
│   ├── src/presentation/    # CmsRequestHandler、Factory
│   ├── src/application/      # CategoryService、ContentService
│   ├── src/domain/           # Category、Content、Repository 接口
│   ├── src/infrastructure/   # InMemory 仓储、SimpleMarkdownRenderer
│   ├── tests/unit_repos.cpp  # 单元测试
│   └── CMakeLists.txt
├── doc/                      # MVP.md、SPEC-poco.md
├── DEV_PLAN.md
└── README.md
```

---

## API 与数据模型概览（简要）

### 实体模型（简要）

- **Category**
  - `id`: 分类唯一标识
  - `name`: 分类名称（通常要求唯一）
  - `description`: 描述（可选）
  - `createdAt`, `updatedAt`: 创建/更新时间

- **Content**
  - `id`: 内容唯一标识
  - `title`: 标题
  - `categoryId`: 所属分类 ID
  - `markdownBody`: Markdown 原文
  - `htmlBody`: 渲染后的 HTML
  - `status`: `draft` 或 `published`
  - `publishedAt`: 发布时间（仅发布状态有值）
  - `createdAt`, `updatedAt`: 创建/更新时间

### 主要 API 分组（路径仅作示意）

- `GET /api/categories`：获取分类列表
- `POST /api/categories`：创建分类
- `PUT /api/categories/{id}`：更新分类
- `DELETE /api/categories/{id}`：删除分类

- `GET /api/contents`：获取内容列表（可选参数：状态、分类等）
- `GET /api/contents/{id}`：获取内容详情
- `POST /api/contents`：创建内容
- `PUT /api/contents/{id}`：更新内容
- `DELETE /api/contents/{id}`：删除内容
- `POST /api/contents/{id}/publish`：发布内容
- `POST /api/contents/{id}/unpublish`：取消发布

- `POST /api/uploads/markdown`：上传 Markdown 文本并创建内容草稿（JSON：`markdownBody`、`title`、`categoryId`）

> 详细字段与错误码等规范见 `DEV_PLAN.md`。1.0 使用内存存储，无持久化。

---

## Roadmap / 后续规划

当前版本聚焦于：

- 单用户、无登录权限系统。
- 内存存储，不提供持久化数据库。
- 基础 Markdown 渲染与前台展示能力。

未来可以考虑的扩展方向：

- 接入真实数据库（如 PostgreSQL / MySQL）。
- 引入多用户与权限管理（管理员 / 编辑 / 访客）。
- 列表分页、按分类/关键字搜索。
- SEO 友好的 URL 结构与 meta 信息。
- 更丰富的 Markdown 配置与安全策略（如自定义白名单标签等）。

---

## 贡献与许可证

- 欢迎通过 Issue 与 Pull Request 对代码和文档提出改进建议。
- 许可证与本仓库其他示例项目保持一致（若主仓库有统一 LICENSE，则在此复用该声明）。

