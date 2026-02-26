## C++ CMS DEV_PLAN

本文件是 C++ CMS（后端：C++17/20 + Poco，前端：Vue）在本仓库下的**版本规划 + 工程任务分解 + AI 编程助手协作提示集合**。  
它与以下文档协同使用：

- `doc/MVP.md`：最小可行产品能力说明
- `../doc/迭代1.md`：多级分类与富文本增强
- `../doc/迭代2.md`：用户权限与内容搜索
- `../doc/迭代3.md`：反馈与数据化能力
- `doc/SPEC-poco.md`：后端技术细节与接口规范（如已存在）

本计划只聚焦 **C++ 实现的 CMS**，不约束其他语言版本。

---

## 1. 版本概览

### 1.0 版本：最小可行 CMS（MVP）

**目标**：交付一个可用的、内存存储的最小 CMS，使用户可以通过后台管理 Markdown 内容，并在前台浏览发布的内容。

- 关联文档：`doc/MVP.md`
- 范围：
  - 后台：
    - 一级分类管理（增删改查）
    - Markdown 内容管理（增删改查、草稿 / 发布切换、关联分类）
    - Markdown 文件上传 / 在线编辑，保存时由后端渲染为 HTML
  - 前台：
    - 内容列表页（按发布时间倒序）
    - 内容详情页（展示后端渲染后的 HTML）
- 非目标：
  - 不引入真实数据库，仅使用内存存储
  - 不实现用户权限体系
  - 不实现评论、点赞、统计等增强能力

### 2.0 版本：结构与编辑增强 + 权限与搜索

**目标**：在 1.0 的基础上增强内容结构、编辑体验，并增加基础权限与搜索能力。

- 关联文档：`../doc/迭代1.md`、`../doc/迭代2.md`
- 范围：
  - 多级自关联分类（无限级），前台按多级分类分层展示内容
  - 后台内容编辑支持富文本编辑器（兼容 Markdown），支持图片上传并嵌入
  - 前台多级分类导航栏，按分类筛选内容
  - 后台用户管理：管理员账号增删改查，区分超级管理员 / 游客
  - 内容权限控制：仅超级管理员可访问后台并进行内容写操作，游客仅能访问前台
  - 前台内容搜索：按标题 / 关键词检索，后台内容需维护关键词字段
- 非目标：
  - 不实现复杂 RBAC，仅实现超级管理员 / 游客两级
  - 不实现全文搜索引擎（如 Elasticsearch），仅在内存/简单结构上搜索

### 3.0 版本：互动与数据化能力

**目标**：在 2.0 的基础上加强用户互动与数据可视化能力。

- 关联文档：`../doc/迭代3.md`
- 范围：
  - 内容评论：前台提交评论，后台审核 / 删除
  - 点赞：每个登录游客对单篇内容可点赞一次
  - 数据统计模块：各分类内容数量、访问量、点赞次数等聚合指标 + 简单可视化展示
- 非目标：
  - 不接入第三方统计平台
  - 不构建复杂 BI 系统，仅做基础可视化

---

## 2. 任务与状态规范

### 2.1 TASK 编号规则

- 任务编号形式：`{Version}-TASK{NNN}`，如：
  - `1.0-TASK001`
  - `2.0-TASK003`
- 每个版本内部从 `TASK001` 开始递增；不同版本之间可以重复数字，但必须带版本前缀。
- 编号排序以「技术依赖 + 用户价值」为准：
  - 先完成基础 Domain / Application / Infrastructure，再完成 API / 前端，再补测试与文档。

### 2.2 任务元信息

每个任务必须包含以下字段：

- **任务编号**：如 `1.0-TASK001`
- **任务名称**：一句话概述（例如：`实现一级分类与内容基础领域模型`）
- **所属版本**：`1.0` / `2.0` / `3.0`
- **当前状态**（枚举）：
  - `Planned`：计划中
  - `UnitTests`：测试单元编写中
  - `InProgress`：开发中
  - `Blocked`：被阻塞
  - `Review`：评审中
  - `Done`：完成
- **简要描述**：2–3 行，说明任务目标、范围与非目标
- **相关文档**：列出关联的需求 / 规格说明（如 `doc/MVP.md` 中的对应条目）

### 2.3 子任务与最小粒度定义

在本 DEV_PLAN 中，「最小粒度子任务」统一定义为：

> 一次与 AI 编程助手交互即可合理完成的、在单一技术层与单一主题上聚焦的小工作单元。

具体约束：

- 子任务聚焦某一层或某一类活动，例如：
  - `Domain` / `Application` / `Infrastructure` / `API` / `Frontend` / `Testing` / `Docs`
- 子任务只做一件清晰的小事，例如：
  - 为 `Content` 增加状态字段并保证不变量
  - 为 `GET /api/contents` 编写集成测试
  - 在后台 Vue 管理端为分类列表添加删除确认对话框
- 每个子任务后都附带一整段**完整的 AI 编程助手提示词**，用于指导 AI 完成该子任务。

---

## 3. AI 提示词模板规范

### 3.1 通用结构

每个子任务的 AI 提示词都应遵循以下结构：

1. **Context（上下文）**
   - 项目背景简述（C++ CMS，Poco，Vue 前后端分离）
   - 当前目标版本与任务编号
   - 当前子任务名称
2. **Existing State（已有信息）**
   - 本子任务所依赖的已实现或已决定内容
   - 需要参考的文档和代码路径
3. **Goal（目标）**
   - 本次交互希望 AI 完成的具体成果（3–5 条）
4. **Constraints & Non-goals（约束与非目标）**
   - 必须遵守的技术/架构约束
   - 明确不在本次子任务范围内的内容
5. **Expected Output（期望输出）**
   - 说明 AI 响应要使用的结构和格式（如是否需要先给计划再给代码）
6. **模式要求（RIPER-5）**
   - 指定 AI 应从哪个模式开始（通常为 `PLAN` 或 `EXECUTE`）
   - 是否允许在提示内部切换模式

### 3.2 模板示例（带占位符）

下面是将被用于具体子任务实例化的通用模板示例：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: {VersionNumber}, Task: {TaskId}, Subtask: {SubtaskName}.

Context:
- {BulletPointContext1}
- {BulletPointContext2}

Existing state:
- {WhatIsAlreadyImplementedOrDecided}

Goal:
- {GoalItem1}
- {GoalItem2}

Constraints:
- {Constraint1}
- {Constraint2}

Non-goals:
- {NonGoal1}

Expected output:
- {OutputExpectation1}
- {OutputExpectation2}

Mode requirements (RIPER-5):
- Start in [MODE: {MODE}] and follow the corresponding protocol.
- Do not switch modes unless explicitly instructed in this prompt.
```

在后续各版本任务中，将基于该模板为每个“最小粒度子任务”给出一个**具体实例化的提示词**。

---

## 4. 1.0 版本计划（MVP）

### 4.1 任务列表概览

- `1.0-TASK001`：领域模型与内存仓储
- `1.0-TASK002`：分类与内容 REST API（后端）
- `1.0-TASK003`：后台管理 UI（分类 + 内容）
- `1.0-TASK004`：前台展示（列表 + 详情）
- `1.0-TASK005`：基础测试与文档

下面依次详细展开。

---

### 1.0-TASK001 领域模型与内存仓储

- **任务编号**：`1.0-TASK001`
- **所属版本**：`1.0`
- **当前状态**：`Planned`
- **相关文档**：`doc/MVP.md`
- **任务名称**：实现 CMS 的基础领域模型与内存仓储
- **简要描述**：
  - 在 Domain 层定义 `Category` 与 `Content` 等核心实体及其约束（如草稿/发布状态）。
  - 在 Infrastructure 层实现面向内存的 Repository，用标准容器持久化运行时数据。
  - 不涉及 API 与前端，仅聚焦后端领域与仓储实现。

#### 子任务 1：Domain - 定义 Category 实体与基本约束

**子任务名称**：`Domain - 定义 Category 实体与基本约束`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 1.0, Task: 1.0-TASK001, Subtask: Domain - 定义 Category 实体与基本约束.

Context:
- We are implementing the minimal CMS described in doc/MVP.md.
- This subtask focuses on the domain model for a single-level Category.

Existing state:
- The project is organized in a DDD style with layers: presentation / application / domain / infrastructure.
- No concrete Category entity has been defined yet.

Goal:
- Define a `Category` entity in the domain layer that represents a first-level category.
- Include fields such as id, name, optional description, createdAt, updatedAt.
- Enforce invariants such as non-empty name and uniqueness of id within the repository (repository-level responsibility can be documented).
- Provide simple value objects / types if appropriate (e.g., `CategoryId`, `CategoryName`), but keep the model minimal and clear.

Constraints:
- Use modern C++17/20 style and align with the existing Poco-based project conventions (see doc/SPEC-poco.md if available).
- Do not introduce database-specific types; this version uses in-memory storage only.

Non-goals:
- Do not implement multi-level or self-referential categories (that will be introduced in version 2.0).
- Do not implement repository logic or persistence; that will be handled in other subtasks.

Expected output:
- A brief explanation of the proposed `Category` entity design.
- One or more C++ code snippets (using markdown code blocks) for the entity and related types, ready to be placed under the domain layer (e.g., `backend/src/domain/category/*.hpp`).

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] and first propose the entity structure.
- After presenting the plan, switch to [MODE: EXECUTE] within the same answer to provide the concrete code snippets.
```

#### 子任务 2：Domain - 定义 Content 实体与草稿 / 发布状态

**子任务名称**：`Domain - 定义 Content 实体与草稿 / 发布状态`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 1.0, Task: 1.0-TASK001, Subtask: Domain - 定义 Content 实体与草稿 / 发布状态.

Context:
- We are implementing the minimal CMS described in doc/MVP.md.
- This subtask focuses on the Content entity that holds Markdown and rendered HTML.

Existing state:
- A Category entity is (or will be) available in the domain layer.
- There is no Content entity yet.

Goal:
- Define a `Content` entity in the domain layer with fields such as id, title, categoryId, markdownBody, htmlBody, status (draft/published), publishedAt, createdAt, updatedAt.
- Model the draft/published status using a clear enumeration or type-safe structure.
- Ensure invariants such as: published contents must have a `publishedAt` timestamp; drafts may omit it.

Constraints:
- Use C++17/20 and follow the existing domain style; keep business logic (like state transitions) in the domain layer.
- Do not implement markdown-to-HTML rendering here; assume an external service or adapter will fill `htmlBody`.

Non-goals:
- Do not implement repository or HTTP handlers.
- Do not implement multi-category or tagging; only a single Category reference is needed.

Expected output:
- A short design explanation of the `Content` entity and its state handling.
- C++ code snippets for the entity definition and status enum/type, suitable for placement under the domain layer (e.g., `backend/src/domain/content/*.hpp`).

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to outline the structure.
- Then switch to [MODE: EXECUTE] within the answer to provide code.
```

#### 子任务 3：Infrastructure - 实现内存版 CategoryRepository

**子任务名称**：`Infrastructure - 实现内存版 CategoryRepository`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 1.0, Task: 1.0-TASK001, Subtask: Infrastructure - 实现内存版 CategoryRepository.

Context:
- The minimal CMS (MVP) uses in-memory storage for all data.
- A Category domain entity is defined (or will be defined) in the domain layer.

Existing state:
- There is (or will be) an abstract repository interface for Category (or we can define it in this subtask).
- No concrete in-memory implementation exists yet.

Goal:
- Define a `ICategoryRepository` interface (if not already defined) with methods such as `create`, `update`, `remove`, `findById`, `findAll`, and a way to enforce unique names if desired.
- Implement an in-memory `CategoryRepositoryInMemory` using standard containers (e.g., `std::unordered_map`).
- Ensure basic error handling (e.g., when id is not found) and discuss how errors are represented (exceptions, optional, expected type, etc.).

Constraints:
- Use standard C++17/20 library types; do not depend on any database or external storage.
- Keep thread-safety considerations simple; a single-threaded assumption is acceptable for 1.0 unless otherwise stated in SPEC-poco.

Non-goals:
- Do not implement persistence to disk or a database.
- Do not implement advanced query capabilities; simple CRUD is enough.

Expected output:
- Interface and class design description.
- C++ code snippets for the repository interface and the in-memory implementation, suitable for placement under `backend/src/domain` and `backend/src/infrastructure`.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to propose the repository API.
- Then switch to [MODE: EXECUTE] to provide code.
```

#### 子任务 4：Infrastructure - 实现内存版 ContentRepository

**子任务名称**：`Infrastructure - 实现内存版 ContentRepository`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 1.0, Task: 1.0-TASK001, Subtask: Infrastructure - 实现内存版 ContentRepository.

Context:
- The minimal CMS (MVP) uses in-memory storage for Content entities.
- The Content domain entity holds markdown and rendered HTML, and references a Category.

Existing state:
- A Content entity is (or will be) defined in the domain layer.
- Repository patterns for other entities (like Category) are available as references.

Goal:
- Define a `IContentRepository` interface (if not yet defined) with methods like `create`, `update`, `remove`, `findById`, `findAll`, and queries such as “find published contents ordered by publishedAt”.
- Implement an in-memory `ContentRepositoryInMemory` using appropriate containers and sorting.
- Support basic filtering by status (draft/published) and by categoryId where reasonable.

Constraints:
- Use C++17/20 and standard containers.
- Keep the implementation simple and focused on the MVP requirements.

Non-goals:
- Do not implement full-text search or complex query language.
- Do not deal with user permissions; that comes in later versions.

Expected output:
- A short design explanation and C++ code snippets for the repository interface and in-memory implementation.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to sketch the interface and data structures.
- Then switch to [MODE: EXECUTE] to present the concrete code.
```

---

### 1.0-TASK002 分类与内容 REST API（后端）

- **任务编号**：`1.0-TASK002`
- **所属版本**：`1.0`
- **当前状态**：`Planned`
- **相关文档**：`doc/MVP.md`
- **任务名称**：实现分类与内容的基础 REST API
- **简要描述**：
  - 基于 Poco HTTPServer 暴露分类与内容的 CRUD 接口。
  - 支持内容的草稿 / 发布状态切换，并设置 `publishedAt`。
  - 为前台列表 / 详情与后台管理提供必要的 API。

#### 子任务 1：API - 设计分类接口契约（请求 / 响应模型）

**子任务名称**：`API - 设计分类接口契约（请求 / 响应模型）`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 1.0, Task: 1.0-TASK002, Subtask: API - 设计分类接口契约（请求 / 响应模型）.

Context:
- We need REST-style endpoints for managing first-level categories in the admin backend.
- The backend is built with Poco and follows DDD layering.

Existing state:
- Category domain entity and in-memory repository are defined (from 1.0-TASK001).
- There is no public HTTP API for categories yet.

Goal:
- Define the HTTP endpoints for category management (e.g., `GET /api/categories`, `POST /api/categories`, `PUT /api/categories/{id}`, `DELETE /api/categories/{id}`).
- Specify JSON request and response schemas, including status codes and error formats.
- Map domain entities to DTOs and ensure fields are clear and minimal.

Constraints:
- Follow RESTful conventions and keep URLs and methods intuitive.
- Align error handling with any existing conventions described in doc/SPEC-poco.md, if present.

Non-goals:
- Do not implement the actual Poco handlers in this subtask.
- Do not design multi-level categories (only single-level for version 1.0).

Expected output:
- A concise API specification in markdown (endpoint table or bullet list).
- DTO structure definitions in C++ (e.g., simple structs) or pseudo-code, ready to be used in handlers.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to design the API contract.
- Then switch to [MODE: EXECUTE] to provide DTO definitions in code snippets.
```

#### 子任务 2：API - 实现分类 CRUD Handler

**子任务名称**：`API - 实现分类 CRUD Handler`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 1.0, Task: 1.0-TASK002, Subtask: API - 实现分类 CRUD Handler.

Context:
- We have designed the category API contract (endpoints, DTOs).
- Now we need to implement the Poco HTTP handlers.

Existing state:
- Category domain entity and in-memory repository are available.
- Project-level HTTP server bootstrap code exists or is assumed (see doc/SPEC-poco.md).

Goal:
- Implement Poco-based HTTP handlers for category CRUD endpoints.
- Wire handlers into the HTTP server routing so that the endpoints become accessible.
- Convert between HTTP JSON payloads and domain entities via DTOs.

Constraints:
- Use Poco HTTPServer and JSON utilities as per existing project conventions.
- Ensure basic validation and error handling (e.g., 400 for invalid input, 404 for not found).

Non-goals:
- Do not implement authentication/authorization here.
- Do not add multi-level category logic.

Expected output:
- C++ code snippets for the handlers and route registration.
- A brief note on where these files should live in the project structure (e.g., `backend/src/presentation/http/category_*`).

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to outline handler responsibilities and routing.
- Then switch to [MODE: EXECUTE] to provide handler implementation code.
```

#### 子任务 3：API - 实现内容 CRUD + 发布 / 草稿切换 Handler

**子任务名称**：`API - 实现内容 CRUD + 发布 / 草稿切换 Handler`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 1.0, Task: 1.0-TASK002, Subtask: API - 实现内容 CRUD + 发布 / 草稿切换 Handler.

Context:
- The CMS must support creating, editing, deleting, and publishing/unpublishing content entities.
- Frontend requires endpoints for listing content and updating its status.

Existing state:
- Content domain entity and in-memory repository are defined.
- Category API and handlers provide a reference pattern.

Goal:
- Implement CRUD HTTP handlers for Content, including creation, update, deletion, and fetching one or many contents.
- Implement endpoints for publishing/unpublishing content that manage the status and `publishedAt`.
- Ensure that list endpoints can filter by status (draft/published) and order published contents by `publishedAt`.

Constraints:
- Follow RESTful conventions and reuse DTO mapping patterns from the category handlers.
- Keep logic that changes status/business rules in the domain/application layer where possible.

Non-goals:
- Do not implement search or keyword-based filtering (that is a 2.0 feature).
- Do not implement permissions (everyone can call these APIs in 1.0).

Expected output:
- Handler and routing code snippets for the content endpoints.
- A brief explanation of how status transitions are validated and where that logic resides.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to enumerate endpoints and status change flows.
- Then switch to [MODE: EXECUTE] to write the handler code.
```

#### 子任务 4：API - 前台列表 / 详情所需只读接口

**子任务名称**：`API - 前台列表 / 详情所需只读接口`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 1.0, Task: 1.0-TASK002, Subtask: API - 前台列表 / 详情所需只读接口.

Context:
- The frontend needs read-only endpoints to show the content list and detail pages.
- Only published contents should appear in the public list.

Existing state:
- Content entity and repository support status and `publishedAt`.
- Admin APIs for content management are being implemented or already exist.

Goal:
- Define and implement read-only endpoints such as `GET /api/public/contents` and `GET /api/public/contents/{id}`.
- Ensure the list endpoint returns only published contents, ordered by `publishedAt` descending.
- Ensure the detail endpoint returns rendered HTML along with metadata (title, category, publishedAt).

Constraints:
- Do not expose draft contents through public endpoints.
- Reuse existing DTOs or define public-specific DTOs if necessary, keeping them minimal.

Non-goals:
- Do not implement filters beyond what MVP requires (no search, no pagination unless already decided).

Expected output:
- API design description and corresponding handler code snippets.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to clarify endpoint behavior.
- Then switch to [MODE: EXECUTE] to implement the handlers.
```

---

### 1.0-TASK003 后台管理 UI（分类 + 内容）

- **任务编号**：`1.0-TASK003`
- **所属版本**：`1.0`
- **当前状态**：`Planned`
- **相关文档**：`doc/MVP.md`
- **任务名称**：实现基础后台管理 UI
- **简要描述**：
  - 使用 Vue 实现分类与内容的后台管理页面。
  - 支持分类和内容的增删改查操作。
  - 支持 Markdown 文件上传与在线编辑，调用后端接口保存并渲染。

#### 子任务 1：Frontend - 分类列表与表单页

**子任务名称**：`Frontend - 分类列表与表单页`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 1.0, Task: 1.0-TASK003, Subtask: Frontend - 分类列表与表单页.

Context:
- We need an admin UI for managing first-level categories.
- The frontend is built with Vue and communicates with the C++ Poco backend via JSON APIs.

Existing state:
- Category REST APIs for CRUD are defined and implemented.
- The frontend project skeleton and routing exist (see `frontend` directory).

Goal:
- Implement a category list page that displays all categories and provides actions for create, edit, and delete.
- Implement a category form (could be a separate page or a modal) that uses the backend APIs to create and update categories.
- Handle loading, success, and error states with clear user feedback.

Constraints:
- Use the existing component and routing structure of the frontend project.
- Keep styling simple but clean; focus on UX clarity over visual complexity.

Non-goals:
- Do not implement multi-level categories; this is strictly for 1.0 single-level categories.

Expected output:
- Vue component code snippets for the list and form views.
- A short explanation of how these components integrate with the router and API layer.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to outline component structure and data flow.
- Then switch to [MODE: EXECUTE] to provide component implementations.
```

#### 子任务 2：Frontend - 内容列表与表单页

**子任务名称**：`Frontend - 内容列表与表单页`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 1.0, Task: 1.0-TASK003, Subtask: Frontend - 内容列表与表单页.

Context:
- Admin users need to manage content entities via a web UI.
- Content has title, category, markdownBody, htmlBody (rendered by backend), status, and timestamps.

Existing state:
- Content REST APIs (CRUD + publish/unpublish) are available.
- Category list and form pages exist or are being implemented.

Goal:
- Implement a content list page showing title, category name, status, createdAt, and publishedAt.
- Implement a content form page that allows creating and editing content, selecting a category, and viewing status.
- Integrate publish/unpublish actions into the UI (buttons or toggles) that call corresponding backend endpoints.

Constraints:
- Use Vue best practices for state management (local component state or lightweight store, depending on project setup).
- Keep forms accessible and handle basic validation (e.g., required title and category).

Non-goals:
- Do not implement rich text editing yet; focus on plain textarea for markdown in 1.0.

Expected output:
- Vue component code snippets for the list and form views, including API calls.
- A brief description of UX flows for common actions (create, edit, publish, unpublish, delete).

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] for screen and flow design.
- Then switch to [MODE: EXECUTE] to produce code.
```

#### 子任务 3：Frontend - Markdown 文件上传与在线编辑集成

**子任务名称**：`Frontend - Markdown 文件上传与在线编辑集成`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 1.0, Task: 1.0-TASK003, Subtask: Frontend - Markdown 文件上传与在线编辑集成.

Context:
- The CMS must support uploading `.md` files and online editing of markdown content in the admin UI.
- The backend provides an endpoint to receive markdown content and render it to HTML on save.

Existing state:
- Content APIs exist, and there is a basic content form page.
- The frontend build system is set up for handling file uploads via HTTP.

Goal:
- Add a file upload UI element that accepts `.md` files and sends their content to the backend for new or existing content.
- Integrate a markdown editing area (simple textarea is enough for 1.0) that stays in sync with the uploaded content.
- Ensure that saving the form triggers backend rendering and that the resulting HTML is stored and later used in the public view.

Constraints:
- Limit accepted file types to `.md` (or a small set of text-based formats).
- Provide clear error messages when upload fails or file type is invalid.

Non-goals:
- Do not implement live preview or rich text editing; this is for 1.0 only.

Expected output:
- Vue code snippets demonstrating file input handling, reading markdown content, and submitting it to the backend.
- Explanation of how this integrates with the existing content form and API module.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to describe the UX and data flow.
- Then switch to [MODE: EXECUTE] to implement the components.
```

---

### 1.0-TASK004 前台展示（列表 + 详情）

- **任务编号**：`1.0-TASK004`
- **所属版本**：`1.0`
- **当前状态**：`Planned`
- **相关文档**：`doc/MVP.md`
- **任务名称**：实现前台内容列表与详情页
- **简要描述**：
  - 为前台用户提供内容列表与详情浏览能力。
  - 仅展示已发布内容，列表按发布时间倒序排序。

#### 子任务 1：Frontend - 列表页按发布时间排序展示

**子任务名称**：`Frontend - 列表页按发布时间排序展示`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 1.0, Task: 1.0-TASK004, Subtask: Frontend - 列表页按发布时间排序展示.

Context:
- Public users need to see a list of published contents ordered by publishedAt descending.
- Backend read-only public endpoints are available for listing contents.

Existing state:
- Routing infrastructure for the frontend exists.
- API module has methods for calling the public content list.

Goal:
- Implement a public-facing list page (e.g., `/` or `/posts`) that fetches published contents from the backend.
- Display key metadata: title, category name, publishedAt, and possibly a short excerpt.
- Ensure correct ordering by `publishedAt` descending and basic loading/error handling.

Constraints:
- Use Vue idioms for data fetching (e.g., composition API or options API according to project style).
- Keep the UI simple and focused on readability.

Non-goals:
- Do not implement filtering, search, or pagination in 1.0.

Expected output:
- Vue component code for the list page and API integration.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to outline the component and data flow.
- Then switch to [MODE: EXECUTE] to provide code.
```

#### 子任务 2：Frontend - 详情页渲染 HTML

**子任务名称**：`Frontend - 详情页渲染 HTML`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 1.0, Task: 1.0-TASK004, Subtask: Frontend - 详情页渲染 HTML.

Context:
- Each content has a rendered HTML body stored by the backend when saved.
- The public detail page should show this HTML safely.

Existing state:
- A public detail endpoint exists, returning content metadata and rendered HTML.
- Frontend routing can navigate to a detail route like `/posts/:id`.

Goal:
- Implement a detail view component that fetches a single published content by id and displays its title, category, publishedAt, and HTML body.
- Render the HTML body into the page while considering basic security practices (e.g., using `v-html` with trusted backend output and server-side sanitization).

Constraints:
- Assume backend sanitizes HTML; do not implement complex client-side sanitization here.
- Handle loading and error states clearly (e.g., content not found).

Non-goals:
- Do not implement comments or likes (these are 3.0 features).

Expected output:
- Vue component code snippets for the detail page, including route configuration and data fetching.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to describe data flow and template structure.
- Then switch to [MODE: EXECUTE] to write the component code.
```

---

### 1.0-TASK005 基础测试与文档

- **任务编号**：`1.0-TASK005`
- **所属版本**：`1.0`
- **当前状态**：`Planned`
- **相关文档**：`doc/MVP.md`
- **任务名称**：补充关键测试与使用文档
- **简要描述**：
  - 为后端关键用例编写单元测试 / 集成测试。
  - 为 1.0 能力编写 README / 部署说明。

#### 子任务 1：Testing - 后端关键用例测试补齐

**子任务名称**：`Testing - 后端关键用例测试补齐`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 1.0, Task: 1.0-TASK005, Subtask: Testing - 后端关键用例测试补齐.

Context:
- We need automated tests to validate core backend behaviors for the MVP.
- Tests should cover domain logic and HTTP endpoints for categories and contents.

Existing state:
- Backend code for domain entities, repositories, and HTTP handlers exists.
- The project uses CMake; a test framework (e.g., GoogleTest, Catch2) may already be integrated or needs to be confirmed.

Goal:
- Identify the most critical backend behaviors (e.g., creating content, publishing/unpublishing, listing published contents).
- Implement unit tests for domain and repository logic.
- Implement integration tests for key HTTP endpoints, using the chosen test framework.

Constraints:
- Follow existing test framework and directory conventions if already defined.
- Keep tests deterministic and reasonably fast.

Non-goals:
- Do not aim for 100% coverage in this subtask; focus on high-value paths.

Expected output:
- A short test plan outlining which behaviors are covered.
- C++ test code snippets for selected unit and integration tests.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to outline the test plan.
- Then switch to [MODE: EXECUTE] to provide test code.
```

#### 子任务 2：Docs - 使用文档与部署说明（1.0）

**子任务名称**：`Docs - 使用文档与部署说明（1.0）`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 1.0, Task: 1.0-TASK005, Subtask: Docs - 使用文档与部署说明（1.0）.

Context:
- We need clear documentation so that users can build, run, and use the 1.0 MVP.
- The project already has some README files at the repository and language-specific levels.

Existing state:
- `1.riper-5/cpp/README.md` describes the project in general terms.
- MVP-specific behaviors are described in `doc/MVP.md`.

Goal:
- Produce or update documentation that explains how to build and run the C++ backend and Vue frontend for 1.0.
- Describe the main features available in 1.0 (admin categories/contents, markdown upload/edit, public list/detail).
- Provide example commands and typical workflows for a new user.

Constraints:
- Keep documentation concise but complete enough for new developers and users.
- Align with the structure and tone of existing READMEs.

Non-goals:
- Do not document future 2.0/3.0 features here; focus on 1.0 only.

Expected output:
- Markdown snippets that can be merged into `1.riper-5/cpp/README.md` or a dedicated 1.0 usage section.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to structure the documentation sections.
- Then switch to [MODE: EXECUTE] to write the content.
```

---

## 5. 2.0 版本计划（结构 + 富文本 + 权限 + 搜索）

### 5.1 任务列表概览

- `2.0-TASK001`：多级分类数据模型与 API 升级
- `2.0-TASK002`：多级分类前台展示与导航
- `2.0-TASK003`：富文本编辑器与图片上传集成
- `2.0-TASK004`：用户管理与权限控制
- `2.0-TASK005`：内容搜索（关键词 / 标题）

---

### 2.0-TASK001 多级分类数据模型与 API 升级

- **任务编号**：`2.0-TASK001`
- **所属版本**：`2.0`
- **当前状态**：`Planned`
- **相关文档**：`../doc/迭代1.md`
- **任务名称**：将分类升级为多级自关联并更新相关 API
- **简要描述**：
  - 将 1.0 的单级 Category 扩展为多级自关联（parent-child 结构）。
  - 更新 domain、repository 和 API，以支持多级结构的创建、读取与删除。

#### 子任务 1：Domain - 扩展 Category 为多级自关联模型

**子任务名称**：`Domain - 扩展 Category 为多级自关联模型`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 2.0, Task: 2.0-TASK001, Subtask: Domain - 扩展 Category 为多级自关联模型.

Context:
- We are upgrading from single-level categories to a multi-level, self-referential category model as described in ../doc/迭代1.md.

Existing state:
- A single-level Category entity is implemented from 1.0.
- Repositories and APIs assume a flat category structure.

Goal:
- Extend the Category domain model to support parent-child relationships (e.g., optional parentId, children traversal).
- Define invariants to prevent cycles and maintain a valid tree/forest structure.
- Document any domain services or helper functions needed for managing these relationships.

Constraints:
- Keep backward compatibility where reasonable, or clearly document breaking changes.
- Do not add persistence beyond in-memory for now.

Non-goals:
- Do not yet change the frontend; this subtask is domain-only.

Expected output:
- Updated or new C++ code for the Category entity and any related domain services.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to design the multi-level model.
- Then switch to [MODE: EXECUTE] to modify/add code.
```

#### 子任务 2：Infrastructure - 更新 CategoryRepository 支持多级结构

**子任务名称**：`Infrastructure - 更新 CategoryRepository 支持多级结构`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 2.0, Task: 2.0-TASK001, Subtask: Infrastructure - 更新 CategoryRepository 支持多级结构.

Context:
- The Category domain model has been extended to support parent-child relationships.

Existing state:
- An in-memory CategoryRepository exists for a flat model.

Goal:
- Update the in-memory CategoryRepository to handle parent-child relationships (e.g., retrieval of subtrees, validation on delete).
- Ensure that operations like create, update, and delete respect tree constraints (e.g., cannot set parentId to create cycles).

Constraints:
- Keep the repository API as simple as possible while supporting required operations.

Non-goals:
- Do not implement complex graph algorithms; keep it sufficient for navigation and basic integrity.

Expected output:
- Adjusted repository interface and implementation code snippets.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] then proceed to [MODE: EXECUTE].
```

#### 子任务 3：API - 分类 API 升级以支持多级结构

**子任务名称**：`API - 分类 API 升级以支持多级结构`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 2.0, Task: 2.0-TASK001, Subtask: API - 分类 API 升级以支持多级结构.

Context:
- Multi-level categories require API changes for create/update/list operations.

Existing state:
- 1.0 category APIs support only flat categories.

Goal:
- Update or extend category APIs to accept an optional parentId on create/update.
- Provide endpoints or response structures that allow the frontend to reconstruct the category tree for navigation.

Constraints:
- Aim for backward compatibility where feasible (e.g., parentId omitted => root category).

Non-goals:
- Do not implement frontend changes in this subtask.

Expected output:
- API contract updates and handler code adjustments.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN], then [MODE: EXECUTE].
```

---

### 2.0-TASK002 多级分类前台展示与导航

- **任务编号**：`2.0-TASK002`
- **所属版本**：`2.0`
- **当前状态**：`Planned`
- **相关文档**：`../doc/迭代1.md`
- **任务名称**：在前台展示多级分类并支持导航筛选

#### 子任务 1：Frontend - 多级分类导航栏 UI

**子任务名称**：`Frontend - 多级分类导航栏 UI`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 2.0, Task: 2.0-TASK002, Subtask: Frontend - 多级分类导航栏 UI.

Context:
- We have upgraded categories to be multi-level and self-referential in the backend (see 2.0-TASK001 and ../doc/迭代1.md).
- The frontend must display a navigable multi-level category tree for users.

Existing state:
- Category APIs now provide enough information (e.g., parentId or tree structure) to reconstruct the hierarchy.
- The public content list page for 1.0 is already implemented.

Goal:
- Design and implement a Vue-based navigation UI that visualizes multi-level categories (e.g., nested menu, tree, or breadcrumb).
- Ensure the navigation can highlight the currently selected category.
- Provide a way to trigger content list filtering when a category is selected (the actual filtering behavior will be implemented in another subtask).

Constraints:
- Reuse existing UI components and routing structure where possible.
- Keep the visual design simple and suitable for a learning/demo project.

Non-goals:
- Do not implement search or other filters here; focus only on category navigation.

Expected output:
- Vue component code snippets for the multi-level category navigation bar.
- A short explanation of how this component receives category data (props vs API calls) and emits selection events.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to outline the component hierarchy and props/events.
- Then switch to [MODE: EXECUTE] to provide concrete Vue code.
```

#### 子任务 2：Frontend - 按分类筛选内容列表

**子任务名称**：`Frontend - 按分类筛选内容列表`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 2.0, Task: 2.0-TASK002, Subtask: Frontend - 按分类筛选内容列表.

Context:
- The public frontend already has a content list page for 1.0 that shows all published contents.
- A multi-level category navigation UI is (or will be) available from a sibling subtask.

Existing state:
- Backend APIs support listing contents by categoryId (or can be extended easily).
- The list page component fetches and displays published contents.

Goal:
- Extend the public list page to filter contents by the currently selected category (and possibly its descendants if desired).
- Wire the list page to respond to events or route parameters from the category navigation component.
- Ensure the UX clearly indicates the active filter and allows users to reset to “all categories”.

Constraints:
- Keep the filtering logic simple and driven by either query parameters (e.g., `?categoryId=...`) or a lightweight client-side state.
- Avoid duplicating API logic; consider adding optional query parameters to existing endpoints instead of new ones if appropriate.

Non-goals:
- Do not implement keyword search; that belongs to 2.0-TASK005.

Expected output:
- Updated Vue list page code with category-based filtering behavior.
- A description of how navigation selection and list filtering are connected (e.g., via router, props, or global store).

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to define the filtering flow and API usage.
- Then switch to [MODE: EXECUTE] to adjust the Vue components.
```

---

### 2.0-TASK003 富文本编辑器与图片上传集成

- **任务编号**：`2.0-TASK003`
- **所属版本**：`2.0`
- **当前状态**：`Planned`
- **相关文档**：`../doc/迭代1.md`
- **任务名称**：在后台内容编辑页集成富文本编辑器与图片上传

#### 子任务 1：Frontend - 集成富文本编辑器组件

**子任务名称**：`Frontend - 集成富文本编辑器组件`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 2.0, Task: 2.0-TASK003, Subtask: Frontend - 集成富文本编辑器组件.

Context:
- In 1.0, the content form uses a simple textarea for markdown editing.
- For 2.0, we want a richer editing experience while still supporting markdown-compatible content.

Existing state:
- Admin content list and form pages are implemented.
- Backend still expects markdown or HTML content fields as defined in the domain model.

Goal:
- Choose and integrate a Vue-compatible rich text editor (or markdown editor with toolbar) into the content form.
- Ensure the editor’s value maps cleanly to the backend’s markdownBody and/or htmlBody fields.
- Provide basic formatting options (headings, bold, italic, links, lists) suitable for CMS usage.

Constraints:
- Prefer lightweight, well-documented editor libraries with permissive licenses.
- Keep configuration minimal; advanced plugins can be deferred.

Non-goals:
- Do not implement collaborative editing or complex plugin systems.

Expected output:
- Vue code snippets showing how the rich text editor is integrated into the content form component.
- Notes on how editor content is transformed or passed to backend fields.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to compare 1–2 editor options and mapping strategies.
- Then switch to [MODE: EXECUTE] to implement the chosen integration.
```

#### 子任务 2：Frontend - 图片上传与插入内容

**子任务名称**：`Frontend - 图片上传与插入内容`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 2.0, Task: 2.0-TASK003, Subtask: Frontend - 图片上传与插入内容.

Context:
- The editor should support uploading images and inserting them into the content body.
- The backend will expose an image upload endpoint to return a URL for the uploaded image.

Existing state:
- The rich text editor component is integrated or planned (see related subtask).
- No dedicated frontend UI yet for image upload and insertion.

Goal:
- Implement a UI flow for selecting image files (e.g., from disk) and uploading them to the backend.
- Upon successful upload, insert the returned image URL into the editor content (as `<img>` tag or markdown image syntax).
- Handle upload progress, success, and error states gracefully.

Constraints:
- Restrict accepted file types to images (e.g., PNG, JPEG).
- Ensure that the upload endpoint URL and expected response format align with backend design.

Non-goals:
- Do not implement image management (gallery, deletion) in this subtask.

Expected output:
- Vue code that wires file input, API call, and editor insertion together.
- A brief description of the UX, including how users trigger image insertion.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to outline the upload flow and integration points.
- Then switch to [MODE: EXECUTE] to implement it.
```

#### 子任务 3：Backend - 图片上传接口与存储策略

**子任务名称**：`Backend - 图片上传接口与存储策略`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 2.0, Task: 2.0-TASK003, Subtask: Backend - 图片上传接口与存储策略.

Context:
- The admin editor in 2.0 needs an endpoint to upload images and receive a URL.
- Storage can be simple (e.g., local filesystem in a configured directory) for now.

Existing state:
- Backend uses Poco HTTPServer and has patterns for handling JSON APIs and file uploads (if any).
- There is no dedicated image upload API yet.

Goal:
- Design and implement a file upload endpoint (e.g., `POST /api/uploads/images`) that accepts image files.
- Store uploaded files in a predictable location (e.g., `uploads/images/`) and generate accessible URLs.
- Return a JSON response containing at least the image URL and maybe metadata.

Constraints:
- Keep the implementation simple and suitable for a demo (no complex security or CDN integration).
- Consider basic file name collision handling (e.g., UUID-based names).

Non-goals:
- Do not implement authentication/authorization on upload in this subtask unless required elsewhere.

Expected output:
- API contract description and C++ handler code snippets for the image upload endpoint.
- Notes on configuration (e.g., base upload directory, URL base path).

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to design the endpoint and storage approach.
- Then switch to [MODE: EXECUTE] to implement it.
```

---

### 2.0-TASK004 用户管理与权限控制

- **任务编号**：`2.0-TASK004`
- **所属版本**：`2.0`
- **当前状态**：`Planned`
- **相关文档**：`../doc/迭代2.md`
- **任务名称**：实现后台用户管理与权限控制（超级管理员 / 游客）

#### 子任务 1：Domain - 用户与角色模型

**子任务名称**：`Domain - 用户与角色模型`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 2.0, Task: 2.0-TASK004, Subtask: Domain - 用户与角色模型.

Context:
- We are adding basic user and role support for the admin backend as described in ../doc/迭代2.md.
- Only two roles are needed: super admin and guest.

Existing state:
- No explicit user domain model exists yet for authentication/authorization.

Goal:
- Define a User entity in the domain layer with fields such as id, username, passwordHash, role, createdAt, updatedAt.
- Define a simple Role representation (e.g., enum) with at least SUPER_ADMIN and GUEST.
- Capture core invariants, such as unique username and basic role semantics.

Constraints:
- Do not store plaintext passwords; model passwordHash as an opaque value (actual hashing strategy may be defined elsewhere).
- Keep the model minimal and focused on the needs of 2.0.

Non-goals:
- Do not design multi-role or permission matrices.

Expected output:
- C++ code snippets for the User entity and role representation, ready for placement in the domain layer.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to outline fields and invariants.
- Then switch to [MODE: EXECUTE] to write the code.
```

#### 子任务 2：Infrastructure - 用户存储与认证机制（简单方案）

**子任务名称**：`Infrastructure - 用户存储与认证机制（简单方案）`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 2.0, Task: 2.0-TASK004, Subtask: Infrastructure - 用户存储与认证机制（简单方案）.

Context:
- The CMS needs a simple way to store users and authenticate admin logins.
- A full-blown identity system is not required; a minimal approach is acceptable for this training project.

Existing state:
- A User domain model exists or will be created in a sibling subtask.
- There is no persistent user store or authentication flow yet.

Goal:
- Implement an in-memory or simple file-based User repository that supports basic CRUD operations.
- Design a simple authentication mechanism (e.g., username + password) and produce a session or token representation.
- Document how authentication data (e.g., session id or token) is expected to be transported (cookies, headers, etc.).

Constraints:
- Keep security considerations at a “training/demo” level; avoid storing plaintext passwords.
- Avoid external dependencies beyond what is already used in the project.

Non-goals:
- Do not implement OAuth/OIDC or complex session clustering.

Expected output:
- C++ code snippets for the user repository and authentication helper logic.
- A brief description of the authentication flow and where it plugs into request handling.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to define repository interface and auth flow.
- Then switch to [MODE: EXECUTE] to implement them.
```

#### 子任务 3：API - 用户管理接口

**子任务名称**：`API - 用户管理接口`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 2.0, Task: 2.0-TASK004, Subtask: API - 用户管理接口.

Context:
- Admins need HTTP endpoints to manage user accounts (at least for super admins).
- The frontend will provide a basic user management UI.

Existing state:
- User domain model and repository are defined.
- HTTP server infrastructure exists, but no user management endpoints yet.

Goal:
- Design and implement REST-style endpoints for listing, creating, updating, and deleting users where appropriate.
- Ensure that sensitive information (like password hashes) is never exposed in responses.
- Provide a login endpoint that verifies credentials and issues a session/token.

Constraints:
- Follow existing API design patterns used elsewhere in the project.
- Keep response formats consistent and minimal.

Non-goals:
- Do not implement password reset or advanced account management features.

Expected output:
- API specification overview and handler implementation code snippets.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to design endpoints and DTOs.
- Then switch to [MODE: EXECUTE] to code the handlers.
```

#### 子任务 4：Backend - 权限中间件 / 拦截器

**子任务名称**：`Backend - 权限中间件 / 拦截器`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 2.0, Task: 2.0-TASK004, Subtask: Backend - 权限中间件 / 拦截器.

Context:
- Only super admins should be able to access admin APIs (content/category/user management).
- Guests should only see public frontend endpoints.

Existing state:
- User authentication flow and session/token mechanism are defined or planned.
- Admin and public HTTP routes exist from previous tasks.

Goal:
- Implement a middleware/interceptor mechanism that checks authentication and role before processing protected routes.
- Apply this mechanism to admin endpoints while leaving public endpoints accessible without authentication.
- Define clear error responses for unauthorized/forbidden access.

Constraints:
- Use patterns compatible with Poco and the existing HTTP server setup.
- Keep role checks simple (SUPER_ADMIN vs GUEST).

Non-goals:
- Do not implement fine-grained per-endpoint permissions beyond role type.

Expected output:
- C++ code snippets for middleware/interceptor components and integration points.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to design the middleware structure.
- Then switch to [MODE: EXECUTE] to implement it.
```

#### 子任务 5：Frontend - 后台登录与会话管理

**子任务名称**：`Frontend - 后台登录与会话管理`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 2.0, Task: 2.0-TASK004, Subtask: Frontend - 后台登录与会话管理.

Context:
- Admin users must log in before accessing the admin UI.
- The backend provides login and possibly logout endpoints and relies on sessions/tokens.

Existing state:
- Admin pages for categories and contents exist from 1.0.
- No frontend login page or session handling is implemented yet.

Goal:
- Implement a login page in the frontend that collects credentials and calls the backend login API.
- Store and manage session/token information on the client side in a simple, secure manner (e.g., HTTP-only cookies if possible, or a lightweight token storage strategy).
- Guard admin routes so that unauthenticated users are redirected to the login page.

Constraints:
- Keep the client-side session logic minimal and consistent with the backend’s auth design.
- Avoid storing sensitive data (like password) beyond the login request.

Non-goals:
- Do not implement “remember me” or multi-factor authentication.

Expected output:
- Vue components and router guard code snippets for login and protected admin routes.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to design the login flow and route guards.
- Then switch to [MODE: EXECUTE] to implement them.
```

---

### 2.0-TASK005 内容搜索（关键词 / 标题）

- **任务编号**：`2.0-TASK005`
- **所属版本**：`2.0`
- **当前状态**：`Planned`
- **相关文档**：`../doc/迭代2.md`
- **任务名称**：实现按标题 / 关键词检索内容

#### 子任务 1：Domain - 为 Content 增加关键词字段

**子任务名称**：`Domain - 为 Content 增加关键词字段`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 2.0, Task: 2.0-TASK005, Subtask: Domain - 为 Content 增加关键词字段.

Context:
- The CMS needs simple search over contents by title and keywords, as described in ../doc/迭代2.md.

Existing state:
- Content entity includes title, categoryId, markdownBody, htmlBody, status, and timestamps.
- There is no dedicated keywords field yet.

Goal:
- Extend the Content domain model to include a keywords field (e.g., a list of strings or a single string with delimiters).
- Define basic conventions for how keywords are stored (case sensitivity, separators).
- Ensure that any new invariants are documented (e.g., max number of keywords or length limits).

Constraints:
- Keep the model simple and geared towards in-memory filtering.

Non-goals:
- Do not implement search algorithms here; that is for infrastructure and API subtasks.

Expected output:
- Updated C++ definition of the Content entity including keywords.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to decide on the keywords representation.
- Then switch to [MODE: EXECUTE] to update the code.
```

#### 子任务 2：Infrastructure - 简单搜索实现（内存过滤）

**子任务名称**：`Infrastructure - 简单搜索实现（内存过滤）`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 2.0, Task: 2.0-TASK005, Subtask: Infrastructure - 简单搜索实现（内存过滤）.

Context:
- We want to search contents by title and keywords without external search engines.

Existing state:
- ContentRepositoryInMemory supports basic CRUD and status/category filters.
- Content entities now contain a keywords field.

Goal:
- Extend the repository capability to support simple in-memory search by title and keywords (e.g., substring or case-insensitive match).
- Define how search queries are represented (e.g., query string, list of tokens).
- Ensure performance is acceptable for the small scale of an in-memory demo.

Constraints:
- Avoid introducing heavy dependencies; use standard library algorithms.

Non-goals:
- Do not build an index or advanced ranking logic.

Expected output:
- Repository method signatures and implementation snippets for search operations.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to define search semantics.
- Then switch to [MODE: EXECUTE] to implement them.
```

#### 子任务 3：API - 搜索接口设计与实现

**子任务名称**：`API - 搜索接口设计与实现`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 2.0, Task: 2.0-TASK005, Subtask: API - 搜索接口设计与实现.

Context:
- Frontend users need an endpoint to search contents by title and keywords.

Existing state:
- Repository-level search functions exist or will be created in a sibling subtask.
- Public read-only endpoints for listing contents are available from 1.0.

Goal:
- Design and implement a public search endpoint (e.g., `GET /api/public/contents/search?q=...`).
- Accept a query parameter and map it to repository search behavior.
- Return results in a format compatible with the existing list views.

Constraints:
- Avoid overloading existing endpoints with too many responsibilities; a dedicated search endpoint is acceptable.

Non-goals:
- Do not implement advanced filtering (e.g., by date range or category) unless explicitly required.

Expected output:
- API design description and Poco handler implementation snippets.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to define query semantics and response schema.
- Then switch to [MODE: EXECUTE] to implement the endpoint.
```

#### 子任务 4：Frontend - 搜索框与结果展示

**子任务名称**：`Frontend - 搜索框与结果展示`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 2.0, Task: 2.0-TASK005, Subtask: Frontend - 搜索框与结果展示.

Context:
- Public users should be able to search for contents by entering a query.

Existing state:
- A public list page shows published contents.
- A backend search endpoint is available.

Goal:
- Add a search input (e.g., in the header or list page) that sends queries to the backend search endpoint.
- Display search results in the list page, clearly indicating when a filter is active.
- Provide a way to clear the search and return to the normal list view.

Constraints:
- Keep UI behavior simple and responsive; debounce requests if necessary.

Non-goals:
- Do not implement advanced UI like faceted search or suggestions.

Expected output:
- Vue component updates implementing the search box, API call, and results rendering.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to outline UX and state handling.
- Then switch to [MODE: EXECUTE] to code it.
```

---

## 6. 3.0 版本计划（评论 + 点赞 + 统计）

### 6.1 任务列表概览

- `3.0-TASK001`：评论领域模型与 API
- `3.0-TASK002`：前台评论交互与后台审核 UI
- `3.0-TASK003`：点赞模型与前台交互
- `3.0-TASK004`：统计与可视化（访问量、点赞数、分类数量）

---

### 3.0-TASK001 评论领域模型与 API

- **任务编号**：`3.0-TASK001`
- **所属版本**：`3.0`
- **当前状态**：`Planned`
- **相关文档**：`../doc/迭代3.md`
- **任务名称**：为内容增加评论能力（模型与 API）

#### 子任务 1：Domain - Comment 实体与约束

**子任务名称**：`Domain - Comment 实体与约束`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 3.0, Task: 3.0-TASK001, Subtask: Domain - Comment 实体与约束.

Context:
- We are adding a comment feature to contents as described in ../doc/迭代3.md.

Existing state:
- Content domain entities and repositories exist.
- User/guest models and permissions exist from 2.0.

Goal:
- Define a Comment entity that references a contentId and (optionally) a userId, with fields like body, status (pending/approved/rejected), createdAt, updatedAt.
- Specify invariants, such as non-empty body and valid status transitions.
- Document how anonymous/guest comments are represented if applicable.

Constraints:
- Keep the model simple enough for in-memory storage.

Non-goals:
- Do not implement spam filtering or complex moderation rules here.

Expected output:
- C++ code snippets for the Comment entity and related enums/types.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to design fields and states.
- Then switch to [MODE: EXECUTE] to implement them.
```

#### 子任务 2：Infrastructure - 评论存储实现

**子任务名称**：`Infrastructure - 评论存储实现`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 3.0, Task: 3.0-TASK001, Subtask: Infrastructure - 评论存储实现.

Context:
- Comments must be stored and retrieved efficiently enough for a small CMS.

Existing state:
- In-memory repositories exist for other entities like Content and Category.

Goal:
- Define a Comment repository interface and implement an in-memory repository for comments.
- Support operations like create, update status, delete, list by contentId, and possibly pagination for listings.

Constraints:
- Use standard containers and keep implementation simple.

Non-goals:
- Do not implement persistent storage beyond in-memory.

Expected output:
- Repository interface and implementation code snippets.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] then [MODE: EXECUTE] to implement the repository.
```

#### 子任务 3：API - 评论提交与查询接口

**子任务名称**：`API - 评论提交与查询接口`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 3.0, Task: 3.0-TASK001, Subtask: API - 评论提交与查询接口.

Context:
- Frontend users need to submit comments on content and view approved comments.

Existing state:
- Comment domain and repository implementations are available.
- HTTP server is set up with routes for contents and admin features.

Goal:
- Implement public endpoints for submitting new comments to a content item.
- Implement read-only endpoints for fetching approved comments for a given content.
- Ensure input validation and basic rate-limiting or anti-abuse considerations (lightweight).

Constraints:
- Keep endpoint design consistent with existing API patterns.

Non-goals:
- Do not implement complex anti-spam mechanisms.

Expected output:
- API design description and handler implementation snippets.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to design endpoints and flows.
- Then switch to [MODE: EXECUTE] to implement them.
```

---

### 3.0-TASK002 前台评论交互与后台审核 UI

- **任务编号**：`3.0-TASK002`
- **所属版本**：`3.0`
- **当前状态**：`Planned`
- **相关文档**：`../doc/迭代3.md`
- **任务名称**：实现前台评论提交与后台评论审核界面

#### 子任务 1：Frontend - 详情页评论列表与提交表单

**子任务名称**：`Frontend - 详情页评论列表与提交表单`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 3.0, Task: 3.0-TASK002, Subtask: Frontend - 详情页评论列表与提交表单.

Context:
- The content detail page should display approved comments and allow users to submit new ones.

Existing state:
- The detail page shows content HTML and metadata.
- Comment APIs for submit and list are available.

Goal:
- Add a comment list section to the content detail page, showing approved comments in chronological or reverse-chronological order.
- Add a comment submission form with basic validation (e.g., non-empty body, optional name).
- Integrate with the backend APIs for loading and posting comments, including success and error feedback.

Constraints:
- Keep the UI simple and avoid heavy client-side state management.

Non-goals:
- Do not implement real-time updates (e.g., websockets).

Expected output:
- Vue code snippets for the comment list and form components integrated into the detail page.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to design the layout and API calls.
- Then switch to [MODE: EXECUTE] to implement them.
```

#### 子任务 2：Frontend - 后台评论审核列表

**子任务名称**：`Frontend - 后台评论审核列表`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 3.0, Task: 3.0-TASK002, Subtask: Frontend - 后台评论审核列表.

Context:
- Admin users need a UI to review, approve, or delete comments.

Existing state:
- Admin UI exists for other domain entities.
- Backend provides comment moderation APIs (list pending, approve, delete).

Goal:
- Implement an admin page that lists comments with filters by status (pending, approved, rejected).
- Provide actions to approve or delete comments, updating the backend accordingly.
- Show clear status indicators and feedback messages.

Constraints:
- Reuse existing admin layout and table components where possible.

Non-goals:
- Do not implement complex moderation workflows beyond basic approve/delete.

Expected output:
- Vue component code snippets for the admin comment moderation page.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to define table columns, filters, and actions.
- Then switch to [MODE: EXECUTE] to implement the page.
```

---

### 3.0-TASK003 点赞模型与前台交互

- **任务编号**：`3.0-TASK003`
- **所属版本**：`3.0`
- **当前状态**：`Planned`
- **相关文档**：`../doc/迭代3.md`
- **任务名称**：实现每个登录游客对内容的点赞能力

#### 子任务 1：Domain - Like / Reaction 模型

**子任务名称**：`Domain - Like / Reaction 模型`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 3.0, Task: 3.0-TASK003, Subtask: Domain - Like / Reaction 模型.

Context:
- We want each logged-in guest user to be able to like a content item once.

Existing state:
- User and Content domain models exist.

Goal:
- Define a Like or Reaction domain model that records which user liked which content and when.
- Enforce the invariant that a given user can like a given content at most once.

Constraints:
- Keep the model minimal (e.g., fields: id, userId, contentId, createdAt).

Non-goals:
- Do not support other reaction types (e.g., dislikes, emojis) in this subtask.

Expected output:
- C++ code snippets for the Like/Reaction entity and any helper logic.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to design fields and constraints.
- Then switch to [MODE: EXECUTE] to implement them.
```

#### 子任务 2：API - 点赞 / 取消点赞接口

**子任务名称**：`API - 点赞 / 取消点赞接口`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 3.0, Task: 3.0-TASK003, Subtask: API - 点赞 / 取消点赞接口.

Context:
- Frontend clients need endpoints to like or unlike a content item for the current user.

Existing state:
- Like/Reaction domain model and repository exist or will be created.
- User authentication and identification are available from 2.0.

Goal:
- Implement endpoints to like a content (creating or ensuring a Like record exists) and optionally unlike (removing the record).
- Enforce the one-like-per-user-per-content rule at the API or domain level.
- Return updated like counts where appropriate.

Constraints:
- Reuse authentication information (e.g., current user id from session or token).

Non-goals:
- Do not implement rate limiting or anti-abuse beyond the one-like rule.

Expected output:
- API design and handler implementation snippets for like/unlike operations.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to design endpoints and flows.
- Then switch to [MODE: EXECUTE] to implement them.
```

#### 子任务 3：Frontend - 点赞按钮与状态展示

**子任务名称**：`Frontend - 点赞按钮与状态展示`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 3.0, Task: 3.0-TASK003, Subtask: Frontend - 点赞按钮与状态展示.

Context:
- Users should be able to see and toggle like status on content items.

Existing state:
- Content detail and/or list views already exist.
- Backend like/unlike APIs are available.

Goal:
- Add a like button and current like count display to the content detail (and optionally list) views.
- Reflect the current user’s like state (liked vs not liked) and allow toggling via API calls.
- Handle loading/error states gracefully without breaking the page.

Constraints:
- Keep client-side state simple; no complex caching is required.

Non-goals:
- Do not implement real-time updates across clients.

Expected output:
- Vue component code snippets integrating like UI and API calls.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to design UI and interactions.
- Then switch to [MODE: EXECUTE] to implement them.
```

---

### 3.0-TASK004 统计与可视化（访问量、点赞数、分类数量）

- **任务编号**：`3.0-TASK004`
- **所属版本**：`3.0`
- **当前状态**：`Planned`
- **相关文档**：`../doc/迭代3.md`
- **任务名称**：实现基础统计与简单可视化图表

#### 子任务 1：Infrastructure - 统计数据采集与聚合

**子任务名称**：`Infrastructure - 统计数据采集与聚合`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 3.0, Task: 3.0-TASK004, Subtask: Infrastructure - 统计数据采集与聚合.

Context:
- The CMS should provide basic metrics like content counts per category, visit counts, and like counts.

Existing state:
- Domain models and repositories exist for contents, categories, likes, and possibly visits (or visit logging may need to be added).

Goal:
- Design a simple way to collect and aggregate statistics for the required metrics.
- Implement data structures and helper functions to compute aggregates on demand (or maintain counters).

Constraints:
- Keep the implementation simple and in-memory; avoid heavy analytics frameworks.

Non-goals:
- Do not implement time-series or historical analytics beyond simple counts.

Expected output:
- C++ code snippets for aggregation logic and any additional fields needed in repositories.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to choose aggregation strategies.
- Then switch to [MODE: EXECUTE] to implement them.
```

#### 子任务 2：API - 统计数据接口

**子任务名称**：`API - 统计数据接口`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 3.0, Task: 3.0-TASK004, Subtask: API - 统计数据接口.

Context:
- The admin dashboard needs endpoints to retrieve aggregated statistics.

Existing state:
- Aggregation logic is implemented in infrastructure or domain services.

Goal:
- Design and implement admin-only endpoints that return statistics such as:
  - Number of contents per category.
  - Total likes per content or per category.
  - Basic visit counts if tracked.
- Define clear JSON response structures suited for chart rendering.

Constraints:
- Protect these endpoints with admin authorization.

Non-goals:
- Do not implement user-specific analytics.

Expected output:
- API specification and handler code snippets.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to define what stats are exposed.
- Then switch to [MODE: EXECUTE] to implement endpoints.
```

#### 子任务 3：Frontend - 后台统计仪表盘与图表组件

**子任务名称**：`Frontend - 后台统计仪表盘与图表组件`

**AI 提示词**：

```markdown
You are an AI coding assistant working on the C++ CMS project (backend: C++17/20 + Poco, frontend: Vue).
Current version: 3.0, Task: 3.0-TASK004, Subtask: Frontend - 后台统计仪表盘与图表组件.

Context:
- Admin users should see a simple dashboard with key metrics visualized.

Existing state:
- Admin frontend and authentication exist.
- Stats APIs are implemented.

Goal:
- Implement a dashboard page that fetches statistics from the backend and displays them using simple charts or visual components (e.g., bar charts, counters).
- Provide at least:
  - Counts of contents per category.
  - Total likes and/or visits for top contents or categories.

Constraints:
- Prefer lightweight charting solutions or even simple HTML/CSS visualizations.

Non-goals:
- Do not build a full analytics suite; focus on a small number of high-level metrics.

Expected output:
- Vue component code for the dashboard and chart rendering, plus explanation of how data is mapped to visuals.

Mode requirements (RIPER-5):
- Start in [MODE: PLAN] to design dashboard layout and chart types.
- Then switch to [MODE: EXECUTE] to implement them.
```

---

## 7. 当前执行步骤

当前执行步骤（示例）：  
`1. 在项目根目录下新建或打开 DEV_PLAN.md 文件，并填入 1.0 版本的完整任务与子任务定义及对应 AI 提示词。`

后续在实际执行中，可在此处更新当前步骤描述，例如：  
`"5. 为 1.0 每个子任务实例化一段 AI Prompt"` 等。

