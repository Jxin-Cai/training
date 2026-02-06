# Draft: CMS System Implementation Plan

## Requirements (confirmed)
- Build complete CMS system with Vue.js frontend and Spring Boot backend
- Frontend/Backend run as separate processes (deployment scripts)
- MVP from doc/MVP.md:
  - Frontend: Content list (by publish time), Content detail (MD rendered)
  - Backend: Category CRUD, MD content CRUD (publish/draft), content-category linkage
  - Core: MD upload/edit, auto-render to HTML on save, frontend displays rendered HTML
- Technical stack from doc/SPEC.md:
  - Frontend: Vue.js, npm run build
  - Backend: Java Spring Boot, in-memory storage
  - Architecture: DDD layered (Presentation/Application/Domain/Infrastructure)
  - Deployment: Shell/cmd scripts for Linux/Mac/Windows

## Technical Decisions
- Frontend: Vue 3 + Vite + TypeScript + Vue Router + Pinia
- Backend: Spring Boot with Maven, Flexmark for Markdown processing
- Database: In-memory (H2 or simple collections)
- UI Theme: Dark luxury (Liquid Glass) - black primary, gold accents
- Editor: md-editor-v3 for Markdown editing

## Research Findings
- Current state: Greenfield project (only documentation exists)
- Project structure: /Users/jxin/Agent/VB-Coding-Demo/training/4.omo-superpowers/java
- Documentation: MVP.md (5 lines), SPEC.md (159 lines) available
- No existing Spring Boot or Vue.js code found

## Open Questions
- Custom port numbers to be specified by user

## Test Strategy Decision
- **Infrastructure exists**: NO (greenfield project)
- **Automated tests**: YES (TDD)
- **Framework**: Backend (JUnit 5, Mockito), Frontend (Vitest)
- **TDD Workflow**: RED-GREEN-REFACTOR for all features

## Deployment Configuration
- **Ports**: Custom ports to be specified by user
- **Package Structure**: Standard Maven for backend, Vite/Vue CLI for frontend

## Scope Boundaries
- INCLUDE: Complete CRUD for categories/content, MD rendering, deployment scripts
- EXCLUDE: User authentication, advanced search, media management, comments
- Frontend: Public pages + admin interface (single Vue app)
- Backend: REST API with in-memory storage only