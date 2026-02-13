# Data Model: Multi-Level Categories & Rich Text Enhancement

**Date**: 2026-02-13
**Branch**: 001-multi-level-categories

## Overview

This document defines the domain model for the Multi-Level Categories & Rich Text Enhancement feature, following DDD principles with a rich domain model.

---

## Domain Entities

### 1. Category (Entity)

**Purpose**: Represents a hierarchical content category with self-referencing structure for unlimited depth.

**Attributes**:

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `id` | Long | Unique identifier | Auto-generated, immutable |
| `name` | String | Category display name | Required, 1-100 characters |
| `description` | String | Category description | Optional, max 500 characters |
| `parent` | Category | Parent category reference | Nullable (null = root category) |
| `children` | List\<Category\> | Child categories | Managed by parent relationship |
| `path` | String | Materialized path for queries | Format: "/1/4/7/", auto-maintained |
| `displayOrder` | Integer | Sort order within parent | Default 0, >= 0 |
| `createdAt` | LocalDateTime | Creation timestamp | Auto-set on creation |
| `updatedAt` | LocalDateTime | Last update timestamp | Auto-updated on modification |

**Business Rules**:
- Category cannot be its own parent (direct or indirect) - circular reference prevention
- Deleting a parent category requires handling of children (delete, reassign, or prevent)
- Path is automatically maintained when parent changes
- Name must be unique within same parent scope

**Domain Methods**:
```java
public class Category {
    // Business logic methods

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isDescendantOf(Category potentialAncestor) {
        if (potentialAncestor == null) return false;
        Category current = this.parent;
        while (current != null) {
            if (current.equals(potentialAncestor)) return true;
            current = current.getParent();
        }
        return false;
    }

    public boolean wouldCreateCircularReference(Category newParent) {
        if (newParent == null) return false;
        return newParent.isDescendantOf(this) || newParent.equals(this);
    }

    public void updateParent(Category newParent) {
        if (wouldCreateCircularReference(newParent)) {
            throw new CircularReferenceException("Cannot create circular category reference");
        }
        this.parent = newParent;
        updatePath();
    }

    private void updatePath() {
        if (parent == null) {
            this.path = "/" + id + "/";
        } else {
            this.path = parent.getPath() + id + "/";
        }
    }

    public List<Long> getPathAsIds() {
        return Arrays.stream(path.split("/"))
            .filter(s -> !s.isEmpty())
            .map(Long::parseLong)
            .collect(Collectors.toList());
    }
}
```

**Relationships**:
- **Self-referencing**: Many-to-one parent, one-to-many children
- **Content**: One-to-many (a category has many content items)

---

### 2. Content (Entity)

**Purpose**: Represents an article or content piece with rich text body and embedded images.

**Attributes**:

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `id` | Long | Unique identifier | Auto-generated, immutable |
| `title` | String | Content title | Required, 1-200 characters |
| `body` | ContentBody (Value Object) | Rich text content | Required, contains HTML with embedded images |
| `category` | Category | Associated category | Required, not null |
| `status` | ContentStatus | Publication status | Enum: DRAFT, PUBLISHED |
| `createdAt` | LocalDateTime | Creation timestamp | Auto-set on creation |
| `updatedAt` | LocalDateTime | Last update timestamp | Auto-updated on modification |
| `publishedAt` | LocalDateTime | Publication timestamp | Set when status changes to PUBLISHED |

**Business Rules**:
- Content must belong to exactly one category
- Body can contain embedded images via `<img>` tags
- Draft content is not visible on frontend
- Published content is visible on frontend and included in category listings
- Body must be valid HTML (sanitized)

**Domain Methods**:
```java
public class Content {
    public void publish() {
        if (this.status == ContentStatus.DRAFT) {
            this.status = ContentStatus.PUBLISHED;
            this.publishedAt = LocalDateTime.now();
        }
    }

    public void unpublish() {
        if (this.status == ContentStatus.PUBLISHED) {
            this.status = ContentStatus.DRAFT;
            this.publishedAt = null;
        }
    }

    public boolean isPublished() {
        return status == ContentStatus.PUBLISHED;
    }

    public void moveToCategory(Category newCategory) {
        if (newCategory == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        this.category = newCategory;
    }

    public List<String> extractImageIds() {
        // Extract image IDs from body HTML
        return body.extractImageIds();
    }
}
```

**Relationships**:
- **Category**: Many-to-one (content belongs to one category)
- **Images**: Many-to-many via body HTML (images embedded in content)

---

### 3. ContentBody (Value Object)

**Purpose**: Encapsulates rich text content with validation and image extraction capabilities.

**Attributes**:

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `html` | String | Rich text HTML content | Required, max 1MB |
| `plainText` | String | Plain text version | Derived from html |
| `imageIds` | Set\<Long\> | IDs of embedded images | Extracted from html |

**Invariants**:
- HTML must be well-formed
- HTML must be sanitized (no script tags, etc.)
- Image references must be valid IDs

**Domain Methods**:
```java
public class ContentBody {
    public static ContentBody fromHtml(String html) {
        String sanitized = sanitizeHtml(html);
        String plainText = extractPlainText(sanitized);
        Set<Long> imageIds = extractImageIds(sanitized);
        return new ContentBody(sanitized, plainText, imageIds);
    }

    private static String sanitizeHtml(String html) {
        // Use OWASP Java HTML Sanitizer or similar
        // Remove script tags, onclick handlers, etc.
        return HtmlSanitizer.sanitize(html);
    }

    private static String extractPlainText(String html) {
        // Strip HTML tags to get plain text
        return html.replaceAll("<[^>]*>", "");
    }

    private static Set<Long> extractImageIds(String html) {
        // Extract image IDs from <img src="/api/images/{id}" />
        Pattern pattern = Pattern.compile("/api/images/(\\d+)");
        Matcher matcher = pattern.matcher(html);
        Set<Long> ids = new HashSet<>();
        while (matcher.find()) {
            ids.add(Long.parseLong(matcher.group(1)));
        }
        return ids;
    }

    public String getSummary(int maxLength) {
        if (plainText.length() <= maxLength) {
            return plainText;
        }
        return plainText.substring(0, maxLength) + "...";
    }
}
```

---

### 4. Image (Entity)

**Purpose**: Represents an uploaded image file that can be embedded in content.

**Attributes**:

| Field | Type | Description | Validation |
|-------|------|-------------|------------|
| `id` | Long | Unique identifier | Auto-generated, immutable |
| `filename` | String | Storage filename (UUID-based) | Required, unique |
| `originalFilename` | String | Original uploaded filename | Required, max 255 chars |
| `mimeType` | String | Image MIME type | Required, whitelist: image/jpeg, image/png, image/gif, image/webp |
| `fileSize` | Long | File size in bytes | Required, <= 10MB |
| `width` | Integer | Image width in pixels | Optional |
| `height` | Integer | Image height in pixels | Optional |
| `url` | String | Public URL path | Derived: "/uploads/" + filename |
| `uploadedAt` | LocalDateTime | Upload timestamp | Auto-set on creation |

**Business Rules**:
- Filename is generated as UUID to prevent conflicts and path traversal
- File must be validated by magic bytes (not just extension)
- Image must be re-encoded to strip embedded malware
- Supported formats: JPEG, PNG, GIF, WebP
- Maximum file size: 10MB

**Domain Methods**:
```java
public class Image {
    public boolean isFormatSupported(String mimeType) {
        return Set.of("image/jpeg", "image/png", "image/gif", "image/webp")
            .contains(mimeType);
    }

    public String getExtension() {
        return switch (mimeType) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> throw new IllegalStateException("Unknown MIME type: " + mimeType);
        };
    }

    public static String generateFilename(String extension) {
        return UUID.randomUUID().toString() + extension;
    }
}
```

**Relationships**:
- **Content**: Many-to-many (images can be embedded in multiple content items)

---

## Enumerations

### ContentStatus

```java
public enum ContentStatus {
    DRAFT,       // Not visible on frontend
    PUBLISHED    // Visible on frontend
}
```

---

## Value Objects

### ContentBody (detailed above)

---

## Domain Services

### 1. CategoryTreeService

**Purpose**: Manages category tree operations including circular reference detection and path maintenance.

**Responsibilities**:
- Detect circular references before parent updates
- Maintain materialized paths
- Retrieve category subtrees
- Get all descendants of a category

**Methods**:
```java
public class CategoryTreeService {
    public boolean wouldCreateCircularReference(Long categoryId, Long newParentId);
    public List<Category> getAllDescendants(Long categoryId);
    public List<Category> getAncestors(Long categoryId);
    public void updateCategoryPath(Category category);
    public void updateDescendantPaths(Category category);
}
```

---

### 2. ContentPublishingService

**Purpose**: Handles content publishing workflow and visibility rules.

**Responsibilities**:
- Publish/unpublish content
- Validate content before publishing
- Manage publication timestamps

**Methods**:
```java
public class ContentPublishingService {
    public void publishContent(Long contentId);
    public void unpublishContent(Long contentId);
    public List<Content> getPublishedContentByCategory(Long categoryId, boolean includeDescendants);
}
```

---

### 3. ImageProcessingService

**Purpose**: Handles image upload, validation, and processing.

**Responsibilities**:
- Validate image files (magic bytes, size, format)
- Re-encode images for security
- Generate safe filenames
- Extract image metadata (dimensions)

**Methods**:
```java
public class ImageProcessingService {
    public Image processUpload(MultipartFile file);
    public void validateImage(MultipartFile file);
    public byte[] reencodeImage(byte[] imageData, String format);
    public ImageMetadata extractMetadata(byte[] imageData);
}
```

---

## Repository Interfaces

### CategoryRepository

```java
public interface CategoryRepository {
    Category findById(Long id);
    List<Category> findAll();
    List<Category> findByParentId(Long parentId);
    List<Category> findByParentIsNull();  // Root categories
    List<Category> findByPathStartingWith(String pathPrefix);  // Descendants
    Category save(Category category);
    void deleteById(Long id);
    boolean existsByNameAndParentId(String name, Long parentId);
}
```

### ContentRepository

```java
public interface ContentRepository {
    Content findById(Long id);
    List<Content> findAll();
    List<Content> findByCategoryId(Long categoryId);
    List<Content> findByCategoryIdIn(List<Long> categoryIds);  // With descendants
    List<Content> findByStatus(ContentStatus status);
    List<Content> findByCategoryAndStatus(Long categoryId, ContentStatus status);
    Content save(Content content);
    void deleteById(Long id);
}
```

### ImageRepository

```java
public interface ImageRepository {
    Image findById(Long id);
    List<Image> findAll();
    Image findByFilename(String filename);
    Image save(Image image);
    void deleteById(Long id);
    List<Image> findByIdIn(Set<Long> ids);
}
```

---

## State Transitions

### Content Publishing Flow

```
    +--------+                  +-----------+
    | DRAFT  | --- publish() -->| PUBLISHED |
    +--------+                  +-----------+
         ^                            |
         |                            | unpublish()
         +----------------------------+
```

**Transitions**:
- `DRAFT → PUBLISHED`: When content is ready for public viewing
  - Sets `publishedAt` timestamp
  - Content becomes visible on frontend
  - Included in category listings

- `PUBLISHED → DRAFT`: When content needs to be hidden
  - Clears `publishedAt` timestamp
  - Content hidden from frontend
  - Removed from category listings

---

## Validation Rules Summary

### Category
- Name: Required, 1-100 chars, unique within parent scope
- Description: Optional, max 500 chars
- Parent: Cannot create circular reference
- Display Order: >= 0

### Content
- Title: Required, 1-200 chars
- Body: Required, max 1MB, valid sanitized HTML
- Category: Required, must exist
- Status: DRAFT or PUBLISHED

### Image
- Filename: Auto-generated UUID
- Original Filename: Required, max 255 chars
- MIME Type: Required, must be in whitelist
- File Size: <= 10MB (10,485,760 bytes)
- Content: Must be valid image (verified by magic bytes)

---

## Migration Notes

### From MVP (Single-Level Categories)

1. **Category Migration**:
   - Add `parent` field (nullable)
   - Add `path` field
   - Set `parent = null` for all existing categories (become root categories)
   - Generate paths: `"/" + id + "/"`

2. **Content Migration**:
   - Add `status` field (default to PUBLISHED for existing content)
   - Add `publishedAt` field (set to `createdAt` for existing content)
   - Body remains compatible (markdown or HTML)

3. **Image Migration**:
   - New entity, no migration needed

---

## Diagram

```
┌──────────────┐
│   Category   │
├──────────────┤         ┌──────────────┐
│ id           │────────>│   Category   │ (parent)
│ name         │         ├──────────────┤
│ description  │<────────│ id           │
│ parent_id    │         │ children     │ (children)
│ path         │         └──────────────┘
│ displayOrder │
│ createdAt    │         ┌──────────────┐
│ updatedAt    │────────>│   Content    │
└──────────────┘         ├──────────────┤
                         │ id           │
                         │ title        │
                         │ body (HTML)  │───────> <img src="/api/images/{id}" />
                         │ category_id  │
                         │ status       │                │
                         │ createdAt    │                │
                         │ updatedAt    │                v
                         │ publishedAt  │         ┌──────────────┐
                         └──────────────┘         │    Image     │
                                                  ├──────────────┤
                                                  │ id           │
                                                  │ filename     │
                                                  │ originalName │
                                                  │ mimeType     │
                                                  │ fileSize     │
                                                  │ uploadedAt   │
                                                  └──────────────┘
```

---

## Key Design Decisions

1. **Self-Referencing Category**: Uses adjacency list + materialized path for optimal balance of update simplicity and query performance

2. **ContentBody Value Object**: Encapsulates HTML sanitization and image extraction logic, ensuring invariants are maintained

3. **Image Security**: Multiple layers of validation (client, server, magic bytes, re-encoding)

4. **UUID Filenames**: Prevents path traversal, file overwrites, and URL prediction

5. **Publication Workflow**: Simple DRAFT/PUBLISHED status with clear state transitions

6. **Path-Based Queries**: Materialized path enables single-query retrieval of all descendants without recursion
