---

description: "Task list for CMS content management system"
---

# Tasks: CMS 内容管理系统

**Input**: Design documents from `/specs/002-cms/`
**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: Tests are OPTIONAL for this feature - not requested in spec

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

## Path Conventions

- **Web app**: `backend/src/`, `frontend/src/`
- Paths shown below assume web app structure per plan.md

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 [P] Create project directory structure per plan.md (backend/src/models, backend/src/services, backend/src/api, frontend/src/components, frontend/src/pages, frontend/src/contexts, frontend/src/hooks, frontend/src/services)
- [x] T002 Initialize backend FastAPI project with Python 3.11 and uv
- [x] T003 [P] Initialize frontend React + TypeScript project with Vite
- [x] T004 Install backend dependencies (fastapi, uvicorn, sqlalchemy, pydantic, python-multipart, markdown-it)
- [x] T005 Install frontend dependencies (react-router-dom, @uiw/react-md-editor, dompurify)
- [x] T006 Configure frontend vite.config.ts with proxy to backend

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [x] T007 [P] Setup SQLite database with schema from data-model.md in backend/src/database.py
- [x] T008 [P] Create Category SQLAlchemy model in backend/src/models/category.py
- [x] T009 [P] Create Content SQLAlchemy model in backend/src/models/content.py
- [x] T010 Setup FastAPI app with CORS in backend/src/main.py
- [x] T011 Create base API router in backend/src/api/__init__.py
- [x] T012 [P] Create frontend API service layer in frontend/src/services/api.ts
- [x] T013 Create CategoryContext in frontend/src/contexts/CategoryContext.tsx
- [x] T014 Create ContentContext in frontend/src/contexts/ContentContext.tsx
- [x] T015 Setup base CSS Grid layout in frontend/src/styles/grid.css

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - 访客浏览内容列表 (Priority: P1) 🎯 MVP

**Goal**: 访客可以查看已发布内容列表，按发布时间倒序排列

**Independent Test**: 访问首页，看到已发布内容列表按时间倒序显示

### Implementation for User Story 1

- [x] T016 [P] [US1] Implement GET /api/contents endpoint (published only, ordered by published_at DESC) in backend/src/api/content.py
- [x] T017 [US1] Create HomePage component in frontend/src/pages/HomePage.tsx
- [x] T018 [US1] Create ContentList component in frontend/src/components/ContentList.tsx
- [x] T019 [US1] Integrate HomePage with ContentContext to fetch published contents
- [x] T020 [US1] Add category name display to each content item in ContentList
- [x] T021 [US1] Add empty state handling ("暂无内容" message)

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently

---

## Phase 4: User Story 2 - 访客查看内容详情 (Priority: P1)

**Goal**: 访客可以查看内容详情页，显示渲染后的 HTML

**Independent Test**: 点击内容标题，跳转到详情页，显示完整 HTML 内容

### Implementation for User Story 2

- [x] T022 [P] [US2] Implement GET /api/contents/{id} endpoint (published only) in backend/src/api/content.py
- [x] T023 [US2] Create ContentDetailPage component in frontend/src/pages/ContentDetailPage.tsx
- [x] T024 [US2] Render HTML content safely using dompurify in ContentDetailPage
- [x] T025 [US2] Add 404 handling for non-existent or draft content

**Checkpoint**: At this point, User Stories 1 AND 2 should both work independently

---

## Phase 5: User Story 3 - 管理员管理分类 (Priority: P1)

**Goal**: 管理员可以创建、查看、编辑、删除一级分类

**Independent Test**: 在后台分类管理页面完成所有 CRUD 操作

### Implementation for User Story 3

- [x] T026 [P] [US3] Implement Category CRUD endpoints in backend/src/api/category.py
- [x] T027 [P] [US3] Implement category service in backend/src/services/category_service.py
- [x] T028 [US3] Create AdminCategoryPage in frontend/src/pages/AdminCategoryPage.tsx
- [x] T029 [US3] Create CategoryList component in frontend/src/components/CategoryList.tsx
- [x] T030 [US3] Create CategoryForm component for create/edit in frontend/src/components/CategoryForm.tsx
- [x] T031 [US3] Add delete protection - check for associated contents before delete

**Checkpoint**: User Story 3 complete - all user stories can now work together

---

## Phase 6: User Story 4 - 管理员管理内容 (Priority: P1)

**Goal**: 管理员可以创建、编辑、删除内容，支持发布/草稿状态

**Independent Test**: 在后台内容管理页面完成所有 CRUD 操作，包括状态切换

### Implementation for User Story 4

- [x] T032 [P] [US4] Implement Content CRUD endpoints in backend/src/api/content.py
- [x] T033 [P] [US4] Implement content service in backend/src/services/content_service.py
- [x] T034 [US4] Create AdminContentPage in frontend/src/pages/AdminContentPage.tsx
- [x] T035 [US4] Create ContentList component in frontend/src/components/admin/ContentList.tsx
- [x] T036 [US4] Create ContentForm component in frontend/src/components/admin/ContentForm.tsx
- [x] T037 [US4] Add category selector dropdown in ContentForm
- [x] T038 [US4] Add status toggle (publish/draft) in ContentForm

---

## Phase 7: User Story 5 - MD 内容编辑与渲染 (Priority: P1)

**Goal**: 支持在线编辑 MD 或上传 MD 文件，保存时自动渲染为 HTML

**Independent Test**: 在内容编辑时编写 MD 或上传文件，保存后前台显示渲染后的 HTML

### Implementation for User Story 5

- [x] T039 [P] [US5] Create MD rendering service in backend/src/services/markdown_service.py
- [x] T040 [P] [US5] Implement file upload endpoint POST /api/contents/upload in backend/src/api/content.py
- [x] T041 [US5] Integrate MD editor component (@uiw/react-md-editor) in ContentForm
- [x] T042 [US5] Add MD file upload button with file picker in ContentForm
- [x] T043 [US5] Auto-render MD to HTML on content save
- [x] T044 [US5] Add HTML sanitization using dompurify on frontend display

**Checkpoint**: All user stories complete - system fully functional

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T045 [P] Complete responsive CSS Grid layout for mobile/tablet
- [x] T046 Add loading states and error handling across all pages
- [x] T047 [P] Add navigation between frontend and admin areas
- [ ] T048 Update data-model.md with any schema changes discovered during implementation
- [ ] T049 Run quickstart.md validation
- [ ] T050 Final integration test across all user stories

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-7)**: All depend on Foundational phase completion
  - User stories can then proceed in parallel (if staffed)
  - Or sequentially in priority order
- **Polish (Phase 8)**: Depends on all user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Depends on US1 completion (uses same API structure)
- **User Story 3 (P1)**: Can start after Foundational - Independent of frontend stories
- **User Story 4 (P1)**: Depends on US3 (needs categories for content)
- **User Story 5 (P1)**: Can start after Foundational - Uses content service

### Within Each User Story

- Models before services
- Services before endpoints
- Core implementation before integration
- Story complete before moving to next priority

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel
- All Foundational tasks marked [P] can run in parallel
- US1 and US2 can run in parallel (different pages)
- US3 and US4+US5 can run in parallel (admin area)
- Models within a story marked [P] can run in parallel

---

## Parallel Example: Phase 2 Foundational

```bash
# These can run in parallel:
Task: "Setup SQLite database with schema in backend/src/database.py"
Task: "Create Category SQLAlchemy model in backend/src/models/category.py"
Task: "Create Content SQLAlchemy model in backend/src/models/content.py"
Task: "Create frontend API service layer in frontend/src/services/api.ts"
Task: "Setup base CSS Grid layout in frontend/src/styles/grid.css"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test User Story 1 independently
5. Deploy/demo if ready

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP!)
3. Add User Story 2 → Test independently → Deploy/Demo
4. Add User Story 3 → Test independently → Deploy/Demo
5. Add User Story 4 → Test independently → Deploy/Demo
6. Add User Story 5 → Test independently → Deploy/Demo
7. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 + 2 (Frontend public pages)
   - Developer B: User Story 3 (Category management)
   - Developer C: User Story 4 + 5 (Content management + MD)
3. Stories complete and integrate independently

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Avoid: vague tasks, same file conflicts, cross-story dependencies that break independence

### Summary

- **Total Tasks**: 50
- **User Stories**: 5 (all P1 priority)
- **MVP Scope**: Phase 1 + Phase 2 + Phase 3 (User Story 1)
- **Parallel Opportunities**: 15+ tasks marked [P] can run in parallel
- **Independent Test Criteria**: Each user story has clear acceptance criteria in spec.md