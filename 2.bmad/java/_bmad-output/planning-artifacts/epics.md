---
stepsCompleted: [1, 2, 3, 4]
inputDocuments: [
  '_bmad-output/planning-artifacts/prd.md',
  '_bmad-output/planning-artifacts/architecture.md',
  '_bmad-output/planning-artifacts/ux-design-specification.md'
]
status: 'complete'
completedAt: '2026-02-17'
---

# java - Epic Breakdown

## Overview

本文档提供 java 项目的完整Epic和Story分解，将PRD、UX设计和架构中的需求分解为可实施的故事。

---

## Requirements Inventory

### Functional Requirements

**用户管理（FR1-FR6）**
- FR1: 用户可以通过账号密码登录系统
- FR2: 用户可以登出系统
- FR3: 管理员可以创建新用户账号
- FR4: 管理员可以为用户分配角色（作者/读者）
- FR5: 作者可以访问后台管理功能
- FR6: 读者只能访问前台阅读功能

**分类管理（FR7-FR10）**
- FR7: 作者可以创建新分类
- FR8: 作者可以编辑分类名称和描述
- FR9: 作者可以删除空分类
- FR10: 作者可以查看所有分类列表

**文章管理（FR11-FR21）**
- FR11: 作者可以创建新文章
- FR12: 作者可以用Markdown编辑文章内容
- FR13: 作者可以实时预览Markdown渲染效果
- FR14: 作者可以为文章选择分类
- FR15: 作者可以将文章保存为草稿
- FR16: 作者可以发布文章
- FR17: 作者可以将已发布文章改为草稿状态
- FR18: 作者可以编辑自己的文章
- FR19: 作者可以删除文章
- FR20: 作者可以查看文章列表
- FR21: 系统自动保存编辑中的文章

**内容浏览（FR22-FR28）**
- FR22: 读者可以访问前台首页
- FR23: 读者可以查看按时间倒序排列的文章列表
- FR24: 读者可以按分类筛选文章
- FR25: 读者可以点击文章查看详情
- FR26: 读者可以看到Markdown渲染后的HTML内容
- FR27: 读者可以搜索文章（按标题）
- FR28: 未登录用户可以访问前台阅读内容

### NonFunctional Requirements

**性能（NFR1-NFR4）**
- NFR1: 页面首次加载时间 < 2秒
- NFR2: 文章列表加载时间 < 1秒
- NFR3: Markdown编辑器响应流畅，无明显延迟
- NFR4: 支持5-6人同时在线使用

**安全（NFR5-NFR8）**
- NFR5: 用户密码必须加密存储
- NFR6: 所有API请求需要身份验证（除前台阅读）
- NFR7: 防止XSS攻击（Markdown内容渲染）
- NFR8: 防止CSRF攻击（表单提交）

**可靠性（NFR9-NFR11）**
- NFR9: 系统可用性 ≥ 99.9%
- NFR10: 文章数据不会丢失（自动保存机制）
- NFR11: 错误操作有明确提示和恢复路径

**可用性（NFR12-NFR14）**
- NFR12: 新用户无需培训即可完成基本操作
- NFR13: 界面简洁，核心功能不超过3次点击可达
- NFR14: 支持主流浏览器（Chrome、Firefox、Safari）

### Additional Requirements

**架构要求（from Architecture.md）：**
- 采用DDD分层架构（Presentation/Application/Domain/Infrastructure）
- 前端使用Vue.js 3.x + Vue Router + Pinia + Axios
- 后端使用Spring Boot + 内存存储
- 遵循SOLID原则和代码质量规范
- 遵循充血模型，使用DDD战术模式定义POJO
- 提供部署脚本（deploy.sh, shutdown.sh）
- API响应格式统一：{code, message, data}

**UX要求（from UX Design）：**
- 前台：单栏居中布局，最大宽度720px
- 后台：左侧边栏(200px) + 右侧内容区
- Markdown编辑器：左右分栏（左编辑/右预览）
- 设计系统：Ant Design Vue
- 反馈模式：成功Toast 2秒消失，错误需手动关闭
- 最小支持宽度：1024px（桌面端优先）
- 遵循WCAG AA无障碍标准

### FR Coverage Map

| Epic | 覆盖的FR |
|------|---------|
| Epic 1: 项目初始化 | 技术基础设施 |
| Epic 2: 用户认证 | FR1, FR2, FR5, FR6 |
| Epic 3: 分类管理 | FR7, FR8, FR9, FR10 |
| Epic 4: 文章管理 | FR11-FR21 |
| Epic 5: 前台展示 | FR22-FR28 |
| Epic 6: 用户管理 | FR3, FR4 |

---

## Epic List

1. **Epic 1: 项目初始化** - 创建前后端项目基础结构
2. **Epic 2: 用户认证** - 用户登录/登出和权限控制
3. **Epic 3: 分类管理** - 分类的增删改查
4. **Epic 4: 文章管理** - 文章的创建、编辑、发布、删除
5. **Epic 5: 前台展示** - 读者浏览和阅读内容
6. **Epic 6: 用户管理** - 管理员管理用户账号

---

## Epic 1: 项目初始化

建立项目基础设施，包括后端DDD架构、前端Vue项目、部署脚本。

### Story 1.1: 创建后端项目结构

As a **开发者**,
I want **创建Spring Boot后端项目，采用DDD分层架构**,
So that **后续功能开发有清晰的架构基础**.

**Acceptance Criteria:**

**Given** 开发环境已准备好（JDK 17+, Maven）
**When** 创建Spring Boot项目
**Then** 项目包含以下DDD分层结构：
- presentation/controller/
- presentation/dto/
- application/service/
- domain/entity/
- domain/valueobject/
- domain/repository/
- infrastructure/repository/
- infrastructure/config/

**And** pom.xml包含必要依赖（Spring Web, Spring Security, Lombok等）

---

### Story 1.2: 创建前端项目结构

As a **开发者**,
I want **创建Vue.js前端项目**,
So that **后续UI开发有统一的基础**.

**Acceptance Criteria:**

**Given** 开发环境已准备好（Node.js 18+）
**When** 创建Vue.js项目
**Then** 项目包含以下结构：
- views/（页面组件）
- components/（通用组件）
- api/（API服务）
- store/（Pinia状态）
- router/（路由配置）
- styles/（样式文件）

**And** package.json包含必要依赖（Vue 3, Vue Router, Pinia, Axios, Ant Design Vue）

---

### Story 1.3: 配置部署脚本

As a **运维人员**,
I want **有一键部署和停止脚本**,
So that **可以快速部署和管理应用**.

**Acceptance Criteria:**

**Given** 前后端项目已创建
**When** 执行部署脚本
**Then** 前端执行 npm install && npm run build
**And** 后端执行 mvn clean package
**And** 服务成功启动

**Given** 应用正在运行
**When** 执行停止脚本
**Then** 前后端服务优雅关闭

---

### Story 1.4: 定义领域实体

As a **开发者**,
I want **定义核心领域实体（User, Article, Category）**,
So that **后续功能有明确的领域模型**.

**Acceptance Criteria:**

**Given** DDD项目结构已创建
**When** 定义领域实体
**Then** User实体包含：id, username, password, role, createdAt
**And** Article实体包含：id, title, content, status, categoryId, authorId, createdAt, updatedAt
**And** Category实体包含：id, name, description, createdAt
**And** 实体使用充血模型，包含必要的业务方法

---

## Epic 2: 用户认证

实现用户登录、登出和权限控制。

### Story 2.1: 实现登录功能

As a **用户**,
I want **通过账号密码登录系统**,
So that **我可以访问我的账号功能**.

**Acceptance Criteria:**

**Given** 用户账号已存在
**When** 用户输入正确的用户名和密码并提交
**Then** 系统验证密码（BCrypt加密）
**And** 创建Session
**And** 跳转到对应页面（作者→后台，读者→前台）

**Given** 用户账号已存在
**When** 用户输入错误的密码
**Then** 显示"用户名或密码错误"提示

---

### Story 2.2: 实现登出功能

As a **已登录用户**,
I want **登出系统**,
So that **我的账号安全得到保护**.

**Acceptance Criteria:**

**Given** 用户已登录
**When** 用户点击"登出"按钮
**Then** Session被清除
**And** 跳转到登录页面

---

### Story 2.3: 实现权限控制

As a **系统管理员**,
I want **不同角色有不同权限**,
So that **系统安全得到保障**.

**Acceptance Criteria:**

**Given** 用户角色为"作者"
**When** 用户访问后台管理页面
**Then** 允许访问

**Given** 用户角色为"读者"
**When** 用户尝试访问后台管理页面
**Then** 拒绝访问并提示无权限

**Given** 用户未登录
**When** 访问前台阅读页面
**Then** 允许访问（FR28）

---

## Epic 3: 分类管理

实现分类的增删改查功能。

### Story 3.1: 查看分类列表

As a **作者**,
I want **查看所有分类列表**,
So that **我了解现有分类情况**.

**Acceptance Criteria:**

**Given** 系统中有多个分类
**When** 作者访问分类管理页面
**Then** 显示所有分类的列表（名称、描述、文章数量）
**And** 列表按创建时间排序

---

### Story 3.2: 创建新分类

As a **作者**,
I want **创建新分类**,
So that **文章可以按主题组织**.

**Acceptance Criteria:**

**Given** 作者已登录后台
**When** 作者填写分类名称和描述并提交
**Then** 新分类创建成功
**And** 显示成功提示
**And** 分类列表自动刷新

---

### Story 3.3: 编辑分类

As a **作者**,
I want **编辑分类名称和描述**,
So that **分类信息保持准确**.

**Acceptance Criteria:**

**Given** 分类已存在
**When** 作者修改分类信息并保存
**Then** 分类信息更新成功
**And** 显示成功提示

---

### Story 3.4: 删除分类

As a **作者**,
I want **删除空分类**,
So that **不需要的分类可以被清理**.

**Acceptance Criteria:**

**Given** 分类下没有文章
**When** 作者点击删除并确认
**Then** 分类被删除
**And** 显示成功提示

**Given** 分类下有文章
**When** 作者尝试删除
**Then** 提示"该分类下有文章，无法删除"

---

## Epic 4: 文章管理

实现文章的创建、编辑、发布、删除功能。

### Story 4.1: 查看文章列表

As a **作者**,
I want **查看文章列表**,
So that **我可以管理我的文章**.

**Acceptance Criteria:**

**Given** 系统中有文章
**When** 作者访问文章管理页面
**Then** 显示文章列表（标题、分类、状态、创建时间）
**And** 支持按状态筛选（全部/草稿/已发布）
**And** 支持分页

---

### Story 4.2: 创建新文章

As a **作者**,
I want **创建新文章**,
So that **我可以分享知识**.

**Acceptance Criteria:**

**Given** 作者已登录后台
**When** 作者点击"新建文章"
**Then** 进入文章编辑页面
**And** 显示左右分栏编辑器（左Markdown/右预览）
**And** 有标题输入框、分类选择器
**And** 有"保存草稿"和"发布"按钮

---

### Story 4.3: Markdown编辑和实时预览

As a **作者**,
I want **用Markdown编辑文章并实时预览**,
So that **我可以专注于内容创作**.

**Acceptance Criteria:**

**Given** 作者在文章编辑页面
**When** 作者在左侧输入Markdown内容
**Then** 右侧实时显示渲染后的HTML
**And** 支持代码块语法高亮
**And** 编辑器响应流畅，无明显延迟（NFR3）

---

### Story 4.4: 自动保存草稿

As a **作者**,
I want **系统自动保存我的编辑内容**,
So that **我不会丢失工作**.

**Acceptance Criteria:**

**Given** 作者正在编辑文章
**When** 每隔30秒
**Then** 系统自动保存当前内容
**And** 显示"已自动保存"提示

---

### Story 4.5: 发布文章

As a **作者**,
I want **发布文章**,
So that **读者可以看到我的内容**.

**Acceptance Criteria:**

**Given** 文章内容已填写完成
**When** 作者点击"发布"按钮
**Then** 文章状态变为"已发布"
**And** 显示发布成功提示
**And** 文章在前台立即可见

---

### Story 4.6: 切换文章状态

As a **作者**,
I want **将已发布文章改为草稿状态**,
So that **我可以暂时隐藏需要修改的文章**.

**Acceptance Criteria:**

**Given** 文章状态为"已发布"
**When** 作者点击"转为草稿"
**Then** 文章状态变为"草稿"
**And** 文章从前台消失

---

### Story 4.7: 编辑文章

As a **作者**,
I want **编辑已创建的文章**,
So that **我可以更新内容**.

**Acceptance Criteria:**

**Given** 文章已存在
**When** 作者点击文章的"编辑"按钮
**Then** 进入编辑页面，显示现有内容
**And** 编辑完成后可保存或发布

---

### Story 4.8: 删除文章

As a **作者**,
I want **删除不需要的文章**,
So that **文章列表保持整洁**.

**Acceptance Criteria:**

**Given** 文章已存在
**When** 作者点击删除并确认
**Then** 文章被删除
**And** 显示成功提示
**And** 列表自动刷新

---

## Epic 5: 前台展示

实现读者浏览和阅读内容的功能。

### Story 5.1: 首页文章列表

As a **读者**,
I want **在首页看到最新文章列表**,
So that **我可以发现新内容**.

**Acceptance Criteria:**

**Given** 有已发布的文章
**When** 读者访问首页
**Then** 显示文章列表（标题、摘要、分类、作者、时间）
**And** 列表按时间倒序排列
**And** 页面加载时间 < 2秒（NFR1）

---

### Story 5.2: 分类筛选

As a **读者**,
I want **按分类筛选文章**,
So that **我可以找到特定主题的内容**.

**Acceptance Criteria:**

**Given** 有多个分类和文章
**When** 读者点击某个分类
**Then** 显示该分类下的所有文章
**And** 当前分类高亮显示

---

### Story 5.3: 搜索文章

As a **读者**,
I want **搜索文章标题**,
So that **我可以快速找到需要的内容**.

**Acceptance Criteria:**

**Given** 有已发布的文章
**When** 读者输入关键词并搜索
**Then** 显示标题包含关键词的文章
**And** 搜索结果按时间排序

---

### Story 5.4: 阅读文章详情

As a **读者**,
I want **阅读文章的完整内容**,
So that **我可以学习知识**.

**Acceptance Criteria:**

**Given** 文章已发布
**When** 读者点击文章标题
**Then** 显示文章详情页
**And** Markdown内容正确渲染为HTML
**And** 代码块有语法高亮
**And** 页面布局单栏居中，最大宽度720px

---

### Story 5.5: 未登录访问

As a **未登录用户**,
I want **访问前台阅读内容**,
So that **我可以获取公开信息**.

**Acceptance Criteria:**

**Given** 用户未登录
**When** 访问前台首页或文章详情
**Then** 允许访问，无需登录

---

## Epic 6: 用户管理

实现管理员管理用户账号的功能。

### Story 6.1: 查看用户列表

As a **管理员**,
I want **查看所有用户列表**,
So that **我可以了解系统用户情况**.

**Acceptance Criteria:**

**Given** 管理员已登录
**When** 访问用户管理页面
**Then** 显示用户列表（用户名、角色、创建时间）
**And** 支持分页

---

### Story 6.2: 创建新用户

As a **管理员**,
I want **创建新用户账号**,
So that **新成员可以加入系统**.

**Acceptance Criteria:**

**Given** 管理员已登录
**When** 填写用户名、密码、角色并提交
**Then** 新用户创建成功
**And** 密码已BCrypt加密存储（NFR5）
**And** 显示成功提示

---

### Story 6.3: 分配用户角色

As a **管理员**,
I want **为用户分配角色**,
So that **用户获得相应权限**.

**Acceptance Criteria:**

**Given** 用户已存在
**When** 管理员修改用户角色并保存
**Then** 用户角色更新成功
**And** 用户下次登录时获得新权限
