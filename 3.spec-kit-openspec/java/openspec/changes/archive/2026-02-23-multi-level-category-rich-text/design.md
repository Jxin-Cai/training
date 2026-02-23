## Context

The CMS currently uses TipTap editor for content authoring, storing content as HTML. Categories support multi-level hierarchy (self-referential parent/children) but the frontend navigation is basic. This design addresses:
1. Adding Markdown authoring support (MD is preferred by technical writers)
2. Enhancing category navigation to expose the hierarchical structure

**Constraints** (from SPEC.md):
- Frontend: Vue.js with Element Plus
- Backend: Spring Boot with in-memory DB
- Follow DDD layered architecture with rich domain model
- Follow SOLID principles and avoid code smells

## Goals / Non-Goals

**Goals:**
- Enable Markdown authoring with live preview and MD↔HTML conversion
- Support code blocks, tables, blockquotes in editor
- Show multi-level category tree with content counts
- Display subcategories on category detail pages

**Non-Goals:**
- Real-time collaborative editing
- Server-side Markdown rendering (HTML stored in DB, MD is authoring convenience)
- Category reordering via drag-and-drop

## Decisions

### 1. Markdown Editor Implementation

**Decision:** Extend existing TipTap editor with `@tiptap/extension-typography` and `tiptap-markdown` for MD support.

**Rationale:**
- Already using TipTap - minimizes refactoring
- `tiptap-markdown` provides bidirectional MD↔HTML conversion
- Prosemirror (TipTap's core) has excellent MD support

**Alternatives considered:**
- SimpleMDE/Milkdown: Would require replacing entire editor
- CodeMirror MD mode: Less WYSIWYG-friendly

### 2. Markdown Conversion Strategy

**Decision:** Frontend-only MD conversion. Content stored as HTML in backend.

**Rationale:**
- Consistent with existing storage (Content.body is HTML)
- No backend changes required
- Users can toggle between MD and WYSIWYG modes

**Implementation:**
```
[MD Input] → prosemirror-markdown → TipTap Document → HTML → Backend
[HTML from DB] → TipTap Document → prosemirror-markdown → MD Display
```

### 3. Category Content Count

**Decision:** Add `contentCount` to CategoryTreeNode DTO, computed via repository query.

**Rationale:**
- Single query for tree with counts (efficient)
- Cached in DTO, not stored in entity (avoids stale data)

**Alternatives considered:**
- Store count in Category entity: Risk of inconsistency
- Compute on frontend: Multiple API calls

### 4. Subcategory Display on Category Page

**Decision:** Add `children` array to CategoryResponse and render as nested list on CategoryPage.

**Rationale:**
- Backend already has parent/children relationship
- Simple DTO mapping, no entity changes

## Risks / Trade-offs

| Risk | Mitigation |
|------|------------|
| MD ↔ HTML conversion may lose some formatting | Define supported MD subset; warn users about complex HTML |
| Large category trees may impact performance | Add lazy loading for tree nodes > 3 levels deep |
| MD tables differ from HTML tables | Use tiptap-table-extension for consistent rendering |

## Open Questions

- None identified - requirements are clear from iteration spec
