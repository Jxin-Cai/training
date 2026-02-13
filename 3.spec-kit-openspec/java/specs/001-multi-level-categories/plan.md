# Implementation Plan: Multi-Level Categories & Rich Text Enhancement

**Branch**: `001-multi-level-categories` | **Date**: 2026-02-13 | **Spec**: [spec.md](./spec.md)
**Input**: Feature specification from `/specs/001-multi-level-categories/spec.md`

## Summary

This iteration upgrades the CMS system with three major enhancements:
1. **Multi-level category hierarchy** - Categories become self-referencing with unlimited depth
2. **Rich text editor with image upload** - Replace basic markdown editor with WYSIWYG editor supporting embedded images
3. **Frontend/Backend navigation** - Add navigation links between home (frontend) and admin (backend)

The implementation maintains backward compatibility with existing single-level categories and markdown content while adding new hierarchical navigation and rich media capabilities.

## Technical Context

**Language/Version**:
- Backend: Java 17+
- Frontend: JavaScript (ES6+)

**Primary Dependencies**:
- Backend: Spring Boot, Spring Data JPA, Thumbnailator 0.4.20, Apache Tika 2.9.1
- Frontend: Vue.js 3.x, TipTap (rich text editor), browser-image-compression
- Image Processing: Hybrid approach - client-side compression + server-side validation/processing

**Storage**:
- In-memory storage (per MVP architecture)
- File system for image uploads

**Testing**:
- Backend: JUnit 5, Spring Boot Test
- Frontend: Vitest or Jest

**Target Platform**: PC Web Application (modern browsers)

**Project Type**: Web application (frontend + backend separation)

**Performance Goals**:
- Category pages load in under 2 seconds with up to 100 items
- Image upload and embed within 10 seconds
- Navigation between frontend/backend under 1 second

**Constraints**:
- 10MB max image file size
- Support JPEG, PNG, GIF, WebP formats
- Handle up to 1000 categories and 10,000 content items
- 5+ levels of category hierarchy without performance issues

**Scale/Scope**:
- Up to 1000 concurrent users
- 1000 categories in hierarchy
- 10,000 content items

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

No constitution file found at `.specify/memory/constitution.md`. Proceeding with default architecture principles:
- ✅ DDD layered architecture (Presentation, Application, Domain, Infrastructure)
- ✅ SOLID principles
- ✅ Rich domain model (not anemic)
- ⚠️ Need to evaluate complexity of rich text editor integration

## Project Structure

### Documentation (this feature)

```text
specs/001-multi-level-categories/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output (API contracts)
└── tasks.md             # Phase 2 output (NOT created yet)
```

### Source Code (repository root)

```text
backend/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/cms/
│       │       ├── presentation/       # Controllers, DTOs
│       │       │   ├── CategoryController.java
│       │       │   ├── ContentController.java
│       │       │   └── ImageController.java
│       │       ├── application/        # Application Services
│       │       │   ├── CategoryService.java
│       │       │   ├── ContentService.java
│       │       │   └── ImageService.java
│       │       ├── domain/             # Entities, Value Objects, Domain Services
│       │       │   ├── category/
│       │       │   │   ├── Category.java
│       │       │   │   ├── CategoryRepository.java
│       │       │   │   └── CategoryTreeService.java
│       │       │   ├── content/
│       │       │   │   ├── Content.java
│       │       │   │   ├── ContentBody.java (Value Object)
│       │       │   │   └── ContentRepository.java
│       │       │   └── image/
│       │       │       ├── Image.java
│       │       │       └── ImageRepository.java
│       │       └── infrastructure/     # Repository implementations
│       │           ├── InMemoryCategoryRepository.java
│       │           ├── InMemoryContentRepository.java
│       │           ├── InMemoryImageRepository.java
│       │           └── FileImageStorage.java
│       └── resources/
│           └── uploads/                # Image storage directory
└── tests/

frontend/
├── src/
│   ├── components/
│   │   ├── common/
│   │   │   ├── NavigationBar.vue
│   │   │   └── Breadcrumb.vue
│   │   ├── category/
│   │   │   ├── CategoryTree.vue
│   │   │   └── CategoryMenu.vue
│   │   ├── content/
│   │   │   ├── ContentList.vue
│   │   │   ├── ContentDetail.vue
│   │   │   └── RichTextEditor.vue
│   │   └── admin/
│   │       ├── CategoryManager.vue
│   │       ├── CategoryTreeEditor.vue
│   │       └── ContentEditor.vue
│   ├── pages/
│   │   ├── Home.vue                   # Frontend entry point
│   │   ├── CategoryPage.vue
│   │   ├── ContentPage.vue
│   │   └── admin/
│   │       ├── AdminLayout.vue
│   │       ├── CategoryAdmin.vue
│   │       └── ContentAdmin.vue
│   ├── services/
│   │   ├── api.js
│   │   ├── categoryService.js
│   │   ├── contentService.js
│   │   └── imageService.js
│   └── router/
│       └── index.js
└── tests/

# Deployment scripts
deploy.sh                              # Linux/Mac deployment
deploy.cmd                             # Windows deployment
shutdown.sh                            # Linux/Mac shutdown
shutdown.cmd                           # Windows shutdown
```

**Structure Decision**: Using web application structure (Option 2) with frontend/backend separation. This matches the existing MVP architecture and deployment requirements specified in SPEC.md.

## Complexity Tracking

> **No violations requiring justification at this time**
