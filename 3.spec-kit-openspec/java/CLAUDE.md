# Project Context for Claude Code

This file provides context about the project structure, technologies, and conventions for AI-assisted development.

---

## Project Overview

**Project Type**: Web Application (Frontend + Backend Separation)
**Primary Domain**: CMS (Content Management System)
**Current Feature**: 001-multi-level-categories

---

## Technology Stack

### Backend
- **Language**: Java 17+
- **Framework**: Spring Boot
- **Data Access**: Spring Data JPA
- **Image Processing**: Thumbnailator 0.4.20
- **File Type Detection**: Apache Tika 2.9.1
- **HTML Sanitization**: OWASP Java HTML Sanitizer
- **Storage**: In-memory (MVP phase), File system for images

### Frontend
- **Language**: JavaScript (ES6+)
- **Framework**: Vue.js 3.x
- **UI Components**: Element Plus
- **Rich Text Editor**: TipTap (with Image extension)
- **Image Compression**: browser-image-compression

---

## Project Structure

```
backend/
├── src/main/java/com/cms/
│   ├── presentation/          # Controllers, DTOs
│   ├── application/           # Application Services
│   ├── domain/               # Entities, Value Objects, Domain Services
│   │   ├── category/
│   │   ├── content/
│   │   └── image/
│   └── infrastructure/        # Repository implementations
└── src/main/resources/
    └── uploads/              # Image storage

frontend/
├── src/
│   ├── components/
│   │   ├── common/
│   │   ├── category/
│   │   ├── content/
│   │   └── admin/
│   ├── pages/
│   ├── services/
│   └── router/
└── tests/
```

---

## Architecture Principles

### Backend Architecture
- **DDD Layered Architecture**:
  - Presentation Layer (Controllers, DTOs)
  - Application Layer (Services)
  - Domain Layer (Entities, Value Objects, Domain Services)
  - Infrastructure Layer (Repository implementations)

- **Rich Domain Model**: Entities contain business logic, not just data
- **SOLID Principles**: Single Responsibility, Open/Closed, Liskov Substitution, Interface Segregation, Dependency Inversion

### Frontend Architecture
- **Component-Based**: Vue 3 Composition API
- **Service Layer**: API calls abstracted into service modules
- **Router-Based Navigation**: Vue Router for SPA navigation

---

## Current Feature: Multi-Level Categories & Rich Text Enhancement

### Feature Branch
`001-multi-level-categories`

### Key Features
1. **Multi-level Category Hierarchy**: Self-referencing categories with unlimited depth
2. **Rich Text Editor**: TipTap-based editor with image upload
3. **Image Upload**: Client compression + server validation
4. **Frontend/Backend Navigation**: Home page links to admin panel

### Design Decisions

#### Category Hierarchy
- **Pattern**: Hybrid Adjacency List + Materialized Path
- **Why**: Simple updates (adjacency) + fast descendant queries (path)
- **Circular Reference Prevention**: DFS algorithm checks before parent update

#### Rich Text Editor
- **Library**: TipTap
- **Why**: MIT license, Vue 3 native, markdown support, image upload

#### Image Processing
- **Approach**: Hybrid client + server
- **Client**: browser-image-compression (target 2MB, max 1920px)
- **Server**: Magic byte validation (Tika), re-encoding (security), UUID filename

---

## API Conventions

### Base URL
- Development: `http://localhost:8080/api`
- Production: `https://api.cms.example.com/api`

### Response Format
- Success: JSON with entity data
- Error: JSON with `timestamp`, `status`, `error`, `message`, `path`

### Key Endpoints

#### Categories
- `GET /api/categories` - List all categories
- `GET /api/categories/tree` - Get hierarchical tree
- `POST /api/categories` - Create category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category
- `GET /api/categories/{id}/children` - Get direct children
- `GET /api/categories/{id}/descendants` - Get all descendants
- `GET /api/categories/{id}/content` - Get category content

#### Content
- `GET /api/content` - List all content
- `POST /api/content` - Create content
- `PUT /api/content/{id}` - Update content
- `DELETE /api/content/{id}` - Delete content
- `POST /api/content/{id}/publish` - Publish content
- `POST /api/content/{id}/unpublish` - Unpublish content

#### Images
- `POST /api/images/upload` - Upload image (multipart)
- `GET /api/images/{id}` - Get image metadata
- `DELETE /api/images/{id}` - Delete image

---

## Coding Standards

### Java
- Use builder pattern for entities with multiple optional fields
- Prefer Optional over null for optional return values
- Domain logic belongs in entities (rich model)
- Validate at service layer boundaries
- Use meaningful exception types (e.g., `CircularReferenceException`)

### JavaScript/Vue
- Use Composition API (`<script setup>`)
- Emit events for component communication
- Async/await for promises
- Compress images before upload
- Handle API errors gracefully

### Security
- Never trust client-side validation alone
- Validate file uploads by magic bytes, not extension
- Re-encode images to strip embedded malware
- Use UUID filenames to prevent path traversal
- Sanitize HTML content (OWASP sanitizer)

---

## Testing Guidelines

### Backend
- Unit tests: JUnit 5, Mockito
- Integration tests: Spring Boot Test
- Focus on domain logic and service layer

### Frontend
- Unit tests: Vitest or Jest
- Component tests: Vue Test Utils
- E2E tests: Cypress (optional)

---

## Performance Considerations

- Category tree: Use lazy loading (load children on expand)
- Image upload: Compress to 2MB before upload
- Content list: Paginate (50 items per page)
- Descendant queries: Use path prefix matching
- Cache category tree until changes detected

---

## Deployment

### Scripts
- `deploy.sh` / `deploy.cmd`: Deploy frontend + backend
- `shutdown.sh` / `shutdown.cmd`: Stop services

### Frontend Deployment
1. `npm install`
2. `npm run build`
3. Serve static files

### Backend Deployment
1. `mvn clean package`
2. Run Spring Boot JAR

---

## Common Patterns

### Circular Reference Check
```java
public boolean wouldCreateCircularReference(Long categoryId, Long newParentId) {
    // DFS traversal to check if newParentId is descendant of categoryId
}
```

### Image Upload Flow
```javascript
// 1. Validate (type, size)
// 2. Compress (target 2MB, max 1920px)
// 3. Upload to /api/images/upload
// 4. Insert image URL into editor
```

### Category Content Query
```java
// Get content from category + all descendants
WHERE path LIKE '/1/4/%'  // Single query, no recursion
```

---

## Feature Documentation

- **Spec**: `specs/001-multi-level-categories/spec.md`
- **Plan**: `specs/001-multi-level-categories/plan.md`
- **Research**: `specs/001-multi-level-categories/research.md`
- **Data Model**: `specs/001-multi-level-categories/data-model.md`
- **API Contracts**: `specs/001-multi-level-categories/contracts/openapi.yaml`
- **Quickstart**: `specs/001-multi-level-categories/quickstart.md`

---

## Notes

- This file is auto-generated by the agent context update script
- Manual additions should be placed between designated markers
- Update this file when adding new technologies or changing architecture
