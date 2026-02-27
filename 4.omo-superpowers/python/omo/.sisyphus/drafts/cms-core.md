# Draft: CMS（内容管理 + 前台展示）

## Requirements (confirmed)
- 后台管理端 + 前台展示端；聚焦核心内容管理与展示能力。
- 前台：内容列表（按发布时间排序）、内容详情页（渲染 MD 内容的最终展示）。
- 后台：一级分类管理（增删改查）、MD 内容管理（增删改查、发布/草稿），内容关联一级分类。
- 核心：MD 文件上传/在线编辑；保存时自动渲染为 HTML；前台展示渲染后的最终内容。

## Repo Reality Check (from exploration)
- 已存在可用的 CMS 雏形：FastAPI + SQLite + React(Vite)。
  - Backend: `4.omo-superpowers/python/superpowers/backend/app/main.py`（分类/内容 CRUD、发布/草稿、保存时 markdown→html）
  - Models: `backend/app/models.py`（Category/Content 已含 markdown_content/html_content/status/published_at）
  - Frontend: `frontend/src/pages/Home.tsx`（列表）、`ContentDetail.tsx`（dangerouslySetInnerHTML 渲染 html_content）、`pages/admin/*`（分类/内容管理 + textarea 在线编辑 + ReactMarkdown 预览）
- 明显缺口：**MD 文件导入/上传**能力未实现；测试体系基本为空；HTML 直插存在 XSS 风险；管理端编辑路由疑似不匹配（列表页链接含 /edit，但 Router 未定义）。

## Technical Decisions (proposed defaults)
- 基于现有 FastAPI + React 工程迭代（不重建新工程）。
- 数据源：继续以 DB 存储 `markdown_content` + `html_content`，保存时由后端生成 `html_content`。
- “MD 文件上传”默认实现为：管理端选择 `.md` 文件 → 浏览器读取文本 → 填入编辑器 → 走现有保存接口（不在服务端保存原始文件）。
- 安全默认：后端渲染后的 HTML 做 XSS sanitize（白名单）。

## Open Questions
1. “MD 文件上传”语义：仅导入内容（前端读取） vs 需要服务端持久化原始 md 文件（存储/下载）。
2. slug 规则：继续手填 vs 根据文件名/标题自动生成（可编辑） vs 后端生成不允许自定义。
3. Markdown 渲染能力：是否需要扩展（表格/代码块/目录/脚注）以及对应的渲染一致性要求。
4. 测试策略：现无测试——是否要求 TDD 并补齐覆盖率（>=80%）。

## Scope Boundaries
- INCLUDE: 分类 CRUD；内容 CRUD + 发布/草稿；MD 在线编辑 + 预览；MD 文件导入；保存即渲染 HTML；前台列表/详情展示最终 HTML。
- EXCLUDE (unless requested): 多级分类、评论、标签、搜索、权限模型深化、图片/附件资源管理、富文本编辑器、SSG/SEO、国际化。
