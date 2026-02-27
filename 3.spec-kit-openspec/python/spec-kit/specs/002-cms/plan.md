# Implementation Plan: CMS 内容管理系统

**Branch**: `002-cms` | **Date**: 2026-02-27 | **Spec**: [link](spec.md)
**Input**: Feature specification from `/specs/002-cms/spec.md`

**Note**: This template is filled in by the `/speckit.plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

开发一个 CMS 内容管理系统，包含前台展示端和后台管理端。前台：内容列表（按发布时间排序）、内容详情页（渲染 MD 格式内容）。后台：分类管理（CRUD）、内容管理（CRUD、发布/草稿）、MD 文件上传/在线编辑、内容保存时自动渲染为 HTML。

## Technical Context

**Language/Version**: TypeScript 5.x, Python 3.11  
**Primary Dependencies**: React 18, FastAPI, markdown-it, uvicorn  
**Storage**: SQLite (localStorage for frontend state cache)  
**Testing**: Jest + React Testing Library, pytest  
**Target Platform**: Web Browser (SPA), Linux Server  
**Project Type**: Web Application (Frontend + Backend)  
**Performance Goals**: 首页加载 < 3s, 详情页 < 2s, API 响应 < 500ms  
**Constraints**: 单用户管理员场景，响应式设计支持移动端  
**Scale/Scope**: 10-100 条内容，5-10 个分类

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

This is a web application project with clear separation:
- Frontend: React SPA with Context API for state management
- Backend: FastAPI REST API with SQLite database

## Project Structure

### Documentation (this feature)

```text
specs/002-cms/
├── plan.md              # This file (/speckit.plan command output)
├── research.md          # Phase 0 output (/speckit.plan command)
├── data-model.md        # Phase 1 output (/speckit.plan command)
├── quickstart.md        # Phase 1 output (/speckit.plan command)
├── contracts/           # Phase 1 output (/speckit.plan command)
└── tasks.md             # Phase 2 output (/speckit.tasks command - NOT created by /speckit.plan)
```

### Source Code (repository root)

```text
# Web Application Structure
backend/
├── src/
│   ├── models/          # SQLAlchemy models
│   ├── services/        # Business logic
│   ├── api/             # FastAPI routes
│   └── main.py          # Entry point
├── tests/
│   ├── integration/
│   └── unit/
└── requirements.txt

frontend/
├── src/
│   ├── components/      # React components
│   ├── pages/           # Page components
│   ├── contexts/        # Context API providers
│   ├── hooks/           # Custom hooks
│   ├── services/        # API calls
│   └── styles/          # CSS files
├── public/
├── tests/
├── index.html
├── package.json
└── vite.config.ts
```

**Structure Decision**: Web application with separate frontend (React + TypeScript SPA) and backend (FastAPI + SQLite). Frontend uses Context API for state management, localStorage for data persistence. CSS Grid for responsive layout.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because |
|-----------|------------|-------------------------------------|
| N/A | N/A | N/A |

---

## Phase 0: Research

### Research Tasks

1. **React Context API Best Practices**: 研究如何在 CMS 应用中高效使用 Context API 管理分类和内容状态
2. **CSS Grid Responsive Design**: 研究响应式布局最佳实践
3. **FastAPI + SQLite Integration**: 研究 FastAPI 与 SQLite 的最佳集成方式
4. **Markdown Rendering**: 研究 markdown-it 或其他 MD 渲染库的 HTML 转义处理

---

## Phase 1: Design & Contracts

### Data Model

**Category Entity**:
- id: integer (primary key)
- name: string (unique, required)
- created_at: datetime
- updated_at: datetime

**Content Entity**:
- id: integer (primary key)
- title: string (required)
- category_id: integer (foreign key, required)
- markdown_content: text (MD source)
- html_content: text (rendered HTML)
- status: string (published/draft)
- created_at: datetime
- published_at: datetime (nullable)

### API Contracts

**Categories API**:
- GET /api/categories - List all categories
- POST /api/categories - Create category
- GET /api/categories/{id} - Get category by ID
- PUT /api/categories/{id} - Update category
- DELETE /api/categories/{id} - Delete category

**Contents API**:
- GET /api/contents - List published contents (frontend)
- GET /api/admin/contents - List all contents (admin)
- POST /api/contents - Create content
- GET /api/contents/{id} - Get content by ID
- PUT /api/contents/{id} - Update content
- DELETE /api/contents/{id} - Delete content

---

## Next Steps

- Phase 1 完成后执行 `.specify/scripts/bash/update-agent-context.sh opencode`
- 准备进入 Phase 2 实现