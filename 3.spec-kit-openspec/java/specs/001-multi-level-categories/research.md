# Research: Multi-Level Categories & Rich Text Enhancement

**Date**: 2026-02-13
**Branch**: 001-multi-level-categories

## Overview

This document consolidates research findings to resolve technical decisions for the Multi-Level Categories & Rich Text Enhancement feature.

---

## Decision 1: Rich Text Editor Library

### Decision: **TipTap**

### Rationale

TipTap is the recommended rich text editor for this Vue.js 3 CMS application based on comprehensive evaluation:

1. **Excellent Vue 3 Integration**: Official Vue 3 support with dedicated packages and comprehensive documentation
2. **MIT License**: Fully open source and free for commercial use (core + many extensions)
3. **Markdown Support**: Native import/export capabilities for backward compatibility with existing content
4. **Modern Architecture**: Headless, plugin-based, tree-shakable design allowing full UI control
5. **Image Upload Ready**: Built-in Image extension and UI components for drag-drop uploads
6. **Active Maintenance**: Regular updates with 10 Pro extensions open-sourced in June 2025
7. **Reasonable Bundle Size**: 50-70KB gzipped with basic extensions; modular approach keeps bundles lean

### Alternatives Considered

| Editor | Pros | Cons | Why Rejected |
|--------|------|------|--------------|
| **Lexical** | Smallest bundle (~22KB), excellent performance | Community Vue wrapper, more setup required | Less mature Vue integration |
| **TinyMCE** | Feature-rich, official Vue support | GPL license, Markdown is paid feature, large bundle | License conflicts, cost concerns |
| **Quill** | Lightweight, mature | Reduced maintenance, community concerns | Sustainability concerns |
| **Toast UI Editor** | Native dual-mode (MD + WYSIWYG) | Official Vue wrapper deprecated, unmaintained | Not suitable for new projects |
| **md-editor-v3** | Built for Vue 3, native markdown | MD-first, WYSIWYG secondary | Doesn't match WYSIWYG requirement |

### Implementation Approach

```vue
<template>
  <EditorContent :editor="editor" />
</template>

<script setup>
import { useEditor, EditorContent } from '@tiptap/vue-3'
import StarterKit from '@tiptap/starter-kit'
import Image from '@tiptap/extension-image'
import Placeholder from '@tiptap/extension-placeholder'

const editor = useEditor({
  content: '<p>Start writing...</p>',
  extensions: [
    StarterKit,
    Image.configure({
      inline: true,
      allowBase64: true,
    }),
    Placeholder.configure({
      placeholder: 'Write something...'
    })
  ],
})
</script>
```

### Sources
- [TipTap Vue 3 Documentation](https://tiptap.dev/docs/editor/getting-started/install/vue3)
- [TipTap Image Extension](https://tiptap.dev/docs/editor/extensions/nodes/image)
- [Liveblocks: Rich Text Editor Comparison 2025](https://liveblocks.io/blog/which-rich-text-editor-framework-should-you-choose-in-2025)

---

## Decision 2: Image Processing Strategy

### Decision: **Hybrid Client + Server Processing**

### Rationale

A hybrid approach provides optimal balance of performance, security, and user experience:

**Client-Side Processing:**
- Pre-validation (file type, size)
- Compression before upload (reduces bandwidth by 60-80%)
- Instant user feedback
- Privacy-preserving (data stays on device until ready)

**Server-Side Processing:**
- Final security validation (magic bytes, malware scanning)
- Image re-writing (strips embedded malicious code)
- Thumbnail generation
- Format conversion for optimization

### Libraries Selected

| Purpose | Library | Why |
|---------|---------|-----|
| **Client Compression** | browser-image-compression | Web Workers for non-blocking, progress tracking, excellent Vue integration |
| **Server Processing** | Thumbnailator | Simple fluent API, high quality, Spring Boot integration |
| **File Type Detection** | Apache Tika | Robust magic byte validation for security |

### Implementation Workflow

```
Client Side:
1. File selection → Accept filter (image/jpeg,image/png,image/gif,image/webp)
2. Validation → Size check (<10MB), MIME type check
3. Compression → Target 2MB, max 1920px, show progress
4. Upload → FormData with progress tracking

Server Side:
5. Validation → Magic bytes (Apache Tika), content-type, size
6. Security → Image re-writing, random filename generation
7. Storage → Save to file system, store metadata
8. Response → Return image URL/ID
```

### Client Implementation

```javascript
import imageCompression from 'browser-image-compression';

async function compressAndUpload(file) {
  // Client validation
  if (!['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(file.type)) {
    throw new Error('Invalid file type');
  }
  if (file.size > 10 * 1024 * 1024) {
    throw new Error('File exceeds 10MB limit');
  }

  // Compress
  const options = {
    maxSizeMB: 2,
    maxWidthOrHeight: 1920,
    useWebWorker: true,
    onProgress: (progress) => updateUI(progress)
  };
  const compressedFile = await imageCompression(file, options);

  // Upload
  const formData = new FormData();
  formData.append('file', compressedFile);
  return await axios.post('/api/images/upload', formData);
}
```

### Server Implementation

```java
@Service
public class ImageService {
    private final Tika tika = new Tika();

    public ImageUploadResponse uploadImage(MultipartFile file) throws IOException {
        // Validate magic bytes
        String detectedType = tika.detect(file.getInputStream());
        if (!Set.of("image/jpeg", "image/png", "image/gif", "image/webp").contains(detectedType)) {
            throw new StorageException("Invalid file type detected");
        }

        // Generate safe filename
        String filename = UUID.randomUUID() + getExtension(file.getOriginalFilename());

        // Re-encode image (security: strips embedded malware)
        BufferedImage image = ImageIO.read(file.getInputStream());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);

        // Store
        Path destination = Paths.get(UPLOAD_DIR, filename);
        Files.write(destination, baos.toByteArray());

        return new ImageUploadResponse(filename, "/uploads/" + filename);
    }
}
```

### Security Measures

1. **Extension Validation**: Whitelist only (.jpg, .jpeg, .png, .gif, .webp)
2. **Magic Byte Validation**: Apache Tika detects actual file type from content
3. **Image Re-writing**: Re-encode to strip embedded malicious code
4. **Random Filenames**: UUID-based to prevent path traversal and prediction
5. **Storage Location**: Outside web root for security
6. **File Size Enforcement**: Both client and server side

### Performance Targets

| Step | Time Budget | Optimization |
|------|-------------|--------------|
| Client validation | <0.5s | Sync checks |
| Client compression | 1-3s | Web Worker (non-blocking) |
| Upload (2MB) | 2-4s | Compressed file |
| Server validation | <0.5s | Streaming |
| Storage | 1-2s | Async write |
| **Total** | **5-10s** | **Meets 10s goal** |

### Sources
- [OWASP File Upload Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/File_Upload_Cheat_Sheet.html)
- [browser-image-compression - NPM](https://www.npmjs.com/package/browser-image-compression)
- [Thumbnailator - GitHub](https://github.com/coobird/thumbnailator)

---

## Decision 3: Category Hierarchy Implementation

### Decision: **Hybrid Adjacency List + Materialized Path**

### Rationale

For the requirements of unlimited depth, 1000 categories, and future database migration, a hybrid approach provides the best balance:

**Adjacency List (Primary):**
- Simple `parentId` reference
- Natural JPA mapping
- Easy updates and inserts (O(1))
- Works well with in-memory storage

**Materialized Path (Secondary Optimization):**
- Store path as string (e.g., "/1/4/7/")
- Enables fast descendant queries
- Single query without recursion
- Easy to migrate to database LTREE later

### Data Structure

```java
@Entity
public class Category {
    @Id
    private Long id;

    private String name;
    private String description;
    private Integer displayOrder;

    // Adjacency List
    @ManyToOne
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> children = new ArrayList<>();

    // Materialized Path (cached for performance)
    private String path; // e.g., "/1/4/7/"

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Circular Reference Detection

Use Depth-First Search with recursion stack tracking:

```java
@Service
public class CategoryService {

    public boolean wouldCreateCircularReference(Long categoryId, Long newParentId) {
        if (categoryId == null || newParentId == null) return false;

        // Check if newParentId is a descendant of categoryId
        Set<Long> visited = new HashSet<>();
        return isDescendant(newParentId, categoryId, visited);
    }

    private boolean isDescendant(Long nodeId, Long potentialAncestorId, Set<Long> visited) {
        if (visited.contains(nodeId)) return false;
        visited.add(nodeId);

        Category node = categoryRepository.findById(nodeId);
        if (node == null || node.getParent() == null) return false;

        if (node.getParent().getId().equals(potentialAncestorId)) return true;

        return isDescendant(node.getParent().getId(), potentialAncestorId, visited);
    }

    @Transactional
    public void updateParent(Long categoryId, Long newParentId) {
        if (wouldCreateCircularReference(categoryId, newParentId)) {
            throw new CircularReferenceException("Cannot create circular category reference");
        }

        Category category = categoryRepository.findById(categoryId);
        Category newParent = newParentId != null ? categoryRepository.findById(newParentId) : null;

        category.setParent(newParent);
        updatePath(category);

        categoryRepository.save(category);
    }
}
```

### Efficient Descendant Query

```java
// Get all descendants of a category (including itself)
public List<Category> getAllDescendants(Long categoryId) {
    Category category = categoryRepository.findById(categoryId);
    String pathPrefix = category.getPath();

    // Single query with LIKE
    return categoryRepository.findByPathStartingWith(pathPrefix);
}

// In repository
@Query("SELECT c FROM Category c WHERE c.path LIKE CONCAT(:pathPrefix, '%')")
List<Category> findByPathStartingWith(@Param("pathPrefix") String pathPrefix);

// Get content from category and all descendants
public List<Content> getContentWithDescendants(Long categoryId) {
    List<Category> categories = getAllDescendants(categoryId);
    List<Long> categoryIds = categories.stream()
        .map(Category::getId)
        .collect(Collectors.toList());

    return contentRepository.findByCategoryIdIn(categoryIds);
}
```

### Path Maintenance

```java
private void updatePath(Category category) {
    if (category.getParent() == null) {
        category.setPath("/" + category.getId() + "/");
    } else {
        category.setPath(category.getParent().getPath() + category.getId() + "/");
    }

    // Update all children recursively
    for (Category child : category.getChildren()) {
        updatePath(child);
    }
}
```

### Frontend Tree Rendering

**Component: Element Plus Tree with Lazy Loading**

```vue
<template>
  <el-tree
    :data="treeData"
    :props="treeProps"
    :load="loadNode"
    lazy
    node-key="id"
    @node-click="handleCategorySelect">
    <template #default="{ data }">
      <span>{{ data.name }} ({{ data.contentCount }})</span>
    </template>
  </el-tree>
</template>

<script setup>
import { ref } from 'vue';

const treeProps = {
  label: 'name',
  isLeaf: (data) => !data.hasChildren
};

async function loadNode(node, resolve) {
  const parentId = node.level === 0 ? null : node.data.id;
  const categories = await categoryApi.getChildren(parentId);
  resolve(categories);
}
</script>
```

**Performance Optimization:**
- Lazy loading (load children only when parent expanded)
- Caching loaded children
- Virtualization for flat lists
- Use `Object.freeze()` for static tree data

### Database Migration Path

**Current (In-Memory):** Adjacency list + cached path

**Phase 1 (PostgreSQL):** Same structure, path as TEXT

```sql
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    parent_id BIGINT REFERENCES categories(id),
    path TEXT,
    display_order INTEGER DEFAULT 0
);

CREATE INDEX idx_parent ON categories(parent_id);
CREATE INDEX idx_path ON categories(path);
```

**Phase 2 (Optimization):** Convert to LTREE

```sql
CREATE EXTENSION IF NOT EXISTS ltree;

ALTER TABLE categories ALTER COLUMN path TYPE ltree USING path::ltree;

CREATE INDEX idx_path_gist ON categories USING GIST (path);
```

### Sources
- [Hierarchical data with Postgres and Spring Data JPA](https://dev.to/mcadariu/hierarchical-data-with-postgresql-and-spring-data-jpa-3b42)
- [LTREE vs Adjacency List vs Closure Table](https://dev.to/dowerdev/implementing-hierarchical-data-structures-in-postgresql-ltree-vs-adjacency-list-vs-closure-table-2jpb)
- [Model Tree Structures with Materialized Paths - MongoDB](https://www.mongodb.com/docs/manual/tutorial/model-tree-structures-with-materialized-paths/)

---

## Summary of Technical Decisions

| Decision | Choice | Key Benefits |
|----------|--------|--------------|
| **Rich Text Editor** | TipTap | MIT license, Vue 3 native, markdown support, image upload |
| **Image Processing** | Hybrid (browser-image-compression + Thumbnailator + Tika) | 60-80% bandwidth reduction, security, 10s performance |
| **Category Hierarchy** | Adjacency List + Materialized Path | Simple updates, fast queries, easy DB migration |
| **Circular Detection** | DFS with recursion stack | O(depth) complexity, reliable |
| **Tree Rendering** | Element Plus Tree with lazy loading | Handles 1000+ categories efficiently |

## All NEEDS CLARIFICATION Items Resolved

- ✅ Rich text editor library: **TipTap**
- ✅ Image processing approach: **Hybrid client + server**
- ✅ Client compression library: **browser-image-compression**
- ✅ Server image library: **Thumbnailator + Apache Tika**
- ✅ Tree structure pattern: **Adjacency List + Materialized Path**
- ✅ Circular reference detection: **DFS with recursion stack**
- ✅ Frontend tree component: **Element Plus Tree with lazy loading**

All technical decisions are now resolved and ready for Phase 1 design.
