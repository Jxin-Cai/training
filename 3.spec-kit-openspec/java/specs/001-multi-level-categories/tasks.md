# Tasks: Multi-Level Categories & Rich Text Enhancement

**Input**: Design documents from `/specs/001-multi-level-categories/`
**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/openapi.yaml

**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3, US4)
- Include exact file paths in descriptions

## Path Conventions

- **Web app**: `backend/src/`, `frontend/src/`
- Paths shown use this web application structure per plan.md

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [ ] T001 Create backend project structure with Maven/Gradle in backend/
- [ ] T002 Create frontend project structure with Vue 3 in frontend/
- [ ] T003 [P] Add backend dependencies to backend/pom.xml (Spring Boot, Spring Data JPA, Thumbnailator 0.4.20, Apache Tika 2.9.1, OWASP HTML Sanitizer)
- [ ] T004 [P] Add frontend dependencies to frontend/package.json (Vue 3, TipTap, Element Plus, browser-image-compression, axios)
- [ ] T005 [P] Create upload directory backend/src/main/resources/uploads/
- [ ] T006 [P] Configure backend application.properties with upload settings in backend/src/main/resources/application.properties
- [ ] T007 [P] Configure frontend vite.config.js with proxy settings in frontend/vite.config.js

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core infrastructure that MUST be complete before ANY user story can be implemented

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

### Backend Domain Models (Shared)

- [ ] T008 [P] Create Category entity with self-referencing in backend/src/main/java/com/cms/domain/category/Category.java
- [ ] T009 [P] Create ContentStatus enum in backend/src/main/java/com/cms/domain/content/ContentStatus.java
- [ ] T010 [P] Create ContentBody value object in backend/src/main/java/com/cms/domain/content/ContentBody.java
- [ ] T011 [P] Create Content entity in backend/src/main/java/com/cms/domain/content/Content.java
- [ ] T012 [P] Create Image entity in backend/src/main/java/com/cms/domain/image/Image.java

### Backend Repository Interfaces

- [ ] T013 [P] Create CategoryRepository interface in backend/src/main/java/com/cms/domain/category/CategoryRepository.java
- [ ] T014 [P] Create ContentRepository interface in backend/src/main/java/com/cms/domain/content/ContentRepository.java
- [ ] T015 [P] Create ImageRepository interface in backend/src/main/java/com/cms/domain/image/ImageRepository.java

### Backend Repository Implementations

- [ ] T016 [P] Implement InMemoryCategoryRepository in backend/src/main/java/com/cms/infrastructure/InMemoryCategoryRepository.java
- [ ] T017 [P] Implement InMemoryContentRepository in backend/src/main/java/com/cms/infrastructure/InMemoryContentRepository.java
- [ ] T018 [P] Implement InMemoryImageRepository in backend/src/main/java/com/cms/infrastructure/InMemoryImageRepository.java
- [ ] T019 [P] Implement FileImageStorage in backend/src/main/java/com/cms/infrastructure/FileImageStorage.java

### Backend Base Controllers

- [ ] T020 Create base API configuration and CORS in backend/src/main/java/com/cms/presentation/config/WebConfig.java
- [ ] T021 [P] Create GlobalExceptionHandler in backend/src/main/java/com/cms/presentation/exception/GlobalExceptionHandler.java
- [ ] T022 [P] Create ErrorResponse DTO in backend/src/main/java/com/cms/presentation/dto/ErrorResponse.java

### Frontend Base Infrastructure

- [ ] T023 [P] Create API client base in frontend/src/services/api.js
- [ ] T024 [P] Configure Vue Router in frontend/src/router/index.js
- [ ] T025 [P] Create basic App.vue layout in frontend/src/App.vue

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel

---

## Phase 3: User Story 1 - Browse Content by Multi-Level Categories (Priority: P1) 🎯 MVP

**Goal**: Enable visitors to browse content organized by hierarchical categories on the frontend

**Independent Test**: Create a 3-level category hierarchy (e.g., Technology > Programming > Java), add content to each level, verify users can navigate through all levels and see appropriate content

### Backend for User Story 1

- [ ] T026 [US1] Create CategoryTreeService for tree operations in backend/src/main/java/com/cms/domain/category/CategoryTreeService.java
- [ ] T027 [US1] Create CategoryService for read operations in backend/src/main/java/com/cms/application/CategoryService.java
- [ ] T028 [US1] Create CategoryResponse DTO in backend/src/main/java/com/cms/presentation/dto/CategoryResponse.java
- [ ] T029 [US1] Create CategoryTreeNode DTO in backend/src/main/java/com/cms/presentation/dto/CategoryTreeNode.java
- [ ] T030 [US1] Create ContentSummary DTO in backend/src/main/java/com/cms/presentation/dto/ContentSummary.java
- [ ] T031 [US1] Implement CategoryController read endpoints (GET /categories, /categories/tree, /categories/{id}, /categories/{id}/children, /categories/{id}/descendants, /categories/{id}/content) in backend/src/main/java/com/cms/presentation/CategoryController.java
- [ ] T032 [US1] Create ContentService for read operations in backend/src/main/java/com/cms/application/ContentService.java
- [ ] T033 [US1] Implement ContentController read endpoints (GET /content, /content/{id}) in backend/src/main/java/com/cms/presentation/ContentController.java

### Frontend for User Story 1

- [ ] T034 [P] [US1] Create categoryService.js for category API calls in frontend/src/services/categoryService.js
- [ ] T035 [P] [US1] Create contentService.js for content API calls in frontend/src/services/contentService.js
- [ ] T036 [P] [US1] Create CategoryTree component with lazy loading in frontend/src/components/category/CategoryTree.vue
- [ ] T037 [P] [US1] Create CategoryMenu component for navigation in frontend/src/components/category/CategoryMenu.vue
- [ ] T038 [P] [US1] Create ContentList component in frontend/src/components/content/ContentList.vue
- [ ] T039 [P] [US1] Create ContentDetail component in frontend/src/components/content/ContentDetail.vue
- [ ] T040 [P] [US1] Create Breadcrumb component in frontend/src/components/common/Breadcrumb.vue
- [ ] T041 [US1] Create Home page with category navigation in frontend/src/pages/Home.vue
- [ ] T042 [US1] Create CategoryPage for category content display in frontend/src/pages/CategoryPage.vue
- [ ] T043 [US1] Create ContentPage for content detail display in frontend/src/pages/ContentPage.vue
- [ ] T044 [US1] Add routes for Home, CategoryPage, ContentPage in frontend/src/router/index.js

**Checkpoint**: User Story 1 should be fully functional - visitors can browse hierarchical categories and view content

---

## Phase 4: User Story 2 - Manage Multi-Level Categories in Admin (Priority: P2)

**Goal**: Enable content managers to create, edit, and delete categories in the admin panel

**Independent Test**: Create a 5+ level category hierarchy, edit category names and parent relationships, delete categories with content handling

### Backend for User Story 2

- [ ] T045 [US2] Create CircularReferenceException in backend/src/main/java/com/cms/domain/exception/CircularReferenceException.java
- [ ] T046 [US2] Enhance CategoryService with CRUD operations in backend/src/main/java/com/cms/application/CategoryService.java
- [ ] T047 [US2] Create CreateCategoryRequest DTO in backend/src/main/java/com/cms/presentation/dto/CreateCategoryRequest.java
- [ ] T048 [US2] Create UpdateCategoryRequest DTO in backend/src/main/java/com/cms/presentation/dto/UpdateCategoryRequest.java
- [ ] T049 [US2] Implement CategoryController write endpoints (POST /categories, PUT /categories/{id}, DELETE /categories/{id}) in backend/src/main/java/com/cms/presentation/CategoryController.java
- [ ] T050 [US2] Add circular reference validation to CategoryTreeService in backend/src/main/java/com/cms/domain/category/CategoryTreeService.java
- [ ] T051 [US2] Add path maintenance logic to Category entity in backend/src/main/java/com/cms/domain/category/Category.java

### Frontend for User Story 2

- [ ] T052 [P] [US2] Create AdminLayout component in frontend/src/pages/admin/AdminLayout.vue
- [ ] T053 [P] [US2] Create CategoryManager component in frontend/src/components/admin/CategoryManager.vue
- [ ] T054 [P] [US2] Create CategoryTreeEditor component in frontend/src/components/admin/CategoryTreeEditor.vue
- [ ] T055 [US2] Create CategoryAdmin page in frontend/src/pages/admin/CategoryAdmin.vue
- [ ] T056 [US2] Add routes for admin pages in frontend/src/router/index.js
- [ ] T057 [US2] Implement category CRUD operations in categoryService.js in frontend/src/services/categoryService.js

**Checkpoint**: User Stories 1 AND 2 should both work independently - admin can manage categories, frontend displays them

---

## Phase 5: User Story 3 - Rich Text Editor with Image Upload (Priority: P3)

**Goal**: Enable content editors to use a rich text editor with image upload capability

**Independent Test**: Create content with formatting (bold, italic, headings), upload images, verify content displays correctly with images

### Backend for User Story 3

- [ ] T058 [P] [US3] Create ImageProcessingService in backend/src/main/java/com/cms/application/ImageProcessingService.java
- [ ] T059 [P] [US3] Create ImageUploadResponse DTO in backend/src/main/java/com/cms/presentation/dto/ImageUploadResponse.java
- [ ] T060 [US3] Implement ImageController (POST /images/upload, GET /images/{id}, DELETE /images/{id}) in backend/src/main/java/com/cms/presentation/ImageController.java
- [ ] T061 [US3] Enhance ContentService with publish/unpublish operations in backend/src/main/java/com/cms/application/ContentService.java
- [ ] T062 [US3] Create CreateContentRequest DTO in backend/src/main/java/com/cms/presentation/dto/CreateContentRequest.java
- [ ] T063 [US3] Create UpdateContentRequest DTO in backend/src/main/java/com/cms/presentation/dto/UpdateContentRequest.java
- [ ] T064 [US3] Implement ContentController write endpoints (POST /content, PUT /content/{id}, DELETE /content/{id}, POST /content/{id}/publish, POST /content/{id}/unpublish) in backend/src/main/java/com/cms/presentation/ContentController.java

### Frontend for User Story 3

- [ ] T065 [P] [US3] Create imageService.js for image upload API in frontend/src/services/imageService.js
- [ ] T066 [US3] Create RichTextEditor component with TipTap in frontend/src/components/content/RichTextEditor.vue
- [ ] T067 [US3] Add image upload and compression logic to RichTextEditor in frontend/src/components/content/RichTextEditor.vue
- [ ] T068 [US3] Create ContentEditor component in frontend/src/components/admin/ContentEditor.vue
- [ ] T069 [US3] Create ContentAdmin page in frontend/src/pages/admin/ContentAdmin.vue
- [ ] T070 [US3] Implement content CRUD operations in contentService.js in frontend/src/services/contentService.js
- [ ] T071 [US3] Add content admin route in frontend/src/router/index.js

**Checkpoint**: All user stories should now be independently functional - rich text editing with images works

---

## Phase 6: User Story 4 - Navigate Between Frontend and Backend (Priority: P4)

**Goal**: Add navigation links between home (frontend) and admin (backend)

**Independent Test**: Click "Admin" button on home page to navigate to admin panel, click "View Site" button in admin to return to frontend

### Frontend for User Story 4

- [ ] T072 [P] [US4] Create NavigationBar component with admin link in frontend/src/components/common/NavigationBar.vue
- [ ] T073 [US4] Add "View Site" button to AdminLayout in frontend/src/pages/admin/AdminLayout.vue
- [ ] T074 [US4] Integrate NavigationBar into Home page in frontend/src/pages/Home.vue
- [ ] T075 [US4] Add styling for navigation buttons in frontend/src/assets/styles/navigation.css

**Checkpoint**: All four user stories should now be fully functional and integrated

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [ ] T076 [P] Create deployment script deploy.sh in project root
- [ ] T077 [P] Create deployment script deploy.cmd in project root
- [ ] T078 [P] Create shutdown script shutdown.sh in project root
- [ ] T079 [P] Create shutdown script shutdown.cmd in project root
- [ ] T080 [P] Add input validation and error handling to all frontend forms
- [ ] T081 [P] Add loading states and progress indicators to frontend components
- [ ] T082 [P] Add empty state messages for categories and content lists
- [ ] T083 Add responsive styling for PC browsers
- [ ] T084 Test circular reference prevention thoroughly
- [ ] T085 Test image upload with various file sizes and types
- [ ] T086 Verify quickstart.md validation scenarios work correctly

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3-6)**: All depend on Foundational phase completion
  - User stories can proceed in parallel (if staffed)
  - Or sequentially in priority order (P1 → P2 → P3 → P4)
- **Polish (Phase 7)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories
- **User Story 2 (P2)**: Can start after Foundational (Phase 2) - Independent of US1, but both share Category entity
- **User Story 3 (P3)**: Can start after Foundational (Phase 2) - Independent, adds Image and Content CRUD
- **User Story 4 (P4)**: Can start after Foundational (Phase 2) - Independent, only needs router setup

### Within Each User Story

- Backend models before services
- Backend services before controllers
- Frontend services before components
- Components before pages
- Pages before routes

### Parallel Opportunities

**Phase 1 (Setup)**:
- T003, T004, T005, T006, T007 can run in parallel (different files)

**Phase 2 (Foundational)**:
- T008-T012 (all entities) can run in parallel
- T013-T015 (all repository interfaces) can run in parallel
- T016-T019 (all repository implementations) can run in parallel
- T021, T022, T023, T024, T025 can run in parallel

**Phase 3 (US1)**:
- T034, T035 (frontend services) can run in parallel
- T036-T040 (all frontend components) can run in parallel

**Phase 4 (US2)**:
- T052, T053, T054 (frontend components) can run in parallel

**Phase 5 (US3)**:
- T058, T059 (backend DTOs) can run in parallel

**Phase 6 (US4)**:
- T072 (only frontend task, marked P for consistency)

**Phase 7 (Polish)**:
- T076-T079 (deployment scripts) can run in parallel
- T080-T082 (frontend polish) can run in parallel

---

## Parallel Example: User Story 1 Frontend

```bash
# After backend APIs are ready, launch all frontend components in parallel:
Task: "Create categoryService.js in frontend/src/services/categoryService.js"
Task: "Create contentService.js in frontend/src/services/contentService.js"
Task: "Create CategoryTree.vue in frontend/src/components/category/CategoryTree.vue"
Task: "Create CategoryMenu.vue in frontend/src/components/category/CategoryMenu.vue"
Task: "Create ContentList.vue in frontend/src/components/content/ContentList.vue"
Task: "Create ContentDetail.vue in frontend/src/components/content/ContentDetail.vue"
Task: "Create Breadcrumb.vue in frontend/src/components/common/Breadcrumb.vue"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - blocks all stories)
3. Complete Phase 3: User Story 1
4. **STOP and VALIDATE**: Test hierarchical category browsing independently
5. Deploy/demo if ready - users can already browse content by categories!

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready
2. Add User Story 1 → Test independently → Deploy/Demo (MVP! Users can browse content)
3. Add User Story 2 → Test independently → Deploy/Demo (Admin can manage categories)
4. Add User Story 3 → Test independently → Deploy/Demo (Rich text editing with images)
5. Add User Story 4 → Test independently → Deploy/Demo (Easy navigation)
6. Each story adds value without breaking previous stories

### Parallel Team Strategy

With multiple developers:

1. Team completes Setup + Foundational together
2. Once Foundational is done:
   - Developer A: User Story 1 (Frontend browsing)
   - Developer B: User Story 2 (Backend category management)
   - Developer C: User Story 3 (Rich text + images)
3. Stories complete and integrate independently
4. Developer D (if available): User Story 4 (Navigation)

---

## Notes

- [P] tasks = different files, no dependencies
- [Story] label maps task to specific user story for traceability
- Each user story should be independently completable and testable
- Commit after each task or logical group
- Stop at any checkpoint to validate story independently
- Backend uses in-memory storage (per MVP architecture)
- Image files stored in backend/src/main/resources/uploads/
- Frontend-backend communication via REST API (base URL: http://localhost:8080/api)

---

## Summary

- **Total Tasks**: 86
- **Phase 1 (Setup)**: 7 tasks
- **Phase 2 (Foundational)**: 18 tasks
- **Phase 3 (US1 - Browse Categories)**: 19 tasks
- **Phase 4 (US2 - Manage Categories)**: 13 tasks
- **Phase 5 (US3 - Rich Text + Images)**: 17 tasks
- **Phase 6 (US4 - Navigation)**: 4 tasks
- **Phase 7 (Polish)**: 11 tasks

**Parallel Opportunities**: 40+ tasks marked [P] can run in parallel within their phases

**MVP Scope**: Phase 1 + Phase 2 + Phase 3 = 44 tasks to deliver first working increment

**Suggested First Demo**: After completing US1, demonstrate hierarchical category browsing with sample data
