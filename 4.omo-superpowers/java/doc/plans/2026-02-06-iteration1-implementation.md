# 迭代1：多级分类与富文本增强 - 实现计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 将 CMS 分类系统从扁平结构升级为无限级树形结构，并为富文本编辑器添加图片上传功能。

**Architecture:** 采用物化路径（Materialized Path）模式实现分类树，使用 `path` 字段存储完整路径便于查询子孙分类。图片使用 Base64 编码直接嵌入 Markdown 内容。

**Tech Stack:** Spring Boot 3.2, Vue 3, Pinia, md-editor-v3, TypeScript

---

## Task 1: 扩展 Category 领域模型

**Files:**
- Modify: `backend/src/main/java/com/cms/domain/model/Category.java`

**Step 1: 添加多级分类字段**

修改 `Category.java`，添加 `parentId`, `level`, `path`, `sortOrder` 字段：

```java
package com.cms.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Category {
    
    private String id;
    private String name;
    private String description;
    private String parentId;      // 父分类ID，null表示顶级分类
    private int level;            // 层级深度，0表示顶级
    private String path;          // 物化路径，如 "/id1/id2/id3"
    private int sortOrder;        // 同级排序
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Category() {}
    
    public static Category create(String name, String description) {
        return create(name, description, null);
    }
    
    public static Category create(String name, String description, String parentId) {
        Category category = new Category();
        category.id = UUID.randomUUID().toString();
        category.name = Objects.requireNonNull(name, "Category name cannot be null");
        category.description = description;
        category.parentId = parentId;
        category.level = 0;  // 默认顶级，由Service设置正确值
        category.path = "/" + category.id;  // 默认路径，由Service设置正确值
        category.sortOrder = 0;
        category.createdAt = LocalDateTime.now();
        category.updatedAt = LocalDateTime.now();
        return category;
    }
    
    public static Category reconstitute(String id, String name, String description,
                                         String parentId, int level, String path, int sortOrder,
                                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        Category category = new Category();
        category.id = id;
        category.name = name;
        category.description = description;
        category.parentId = parentId;
        category.level = level;
        category.path = path;
        category.sortOrder = sortOrder;
        category.createdAt = createdAt;
        category.updatedAt = updatedAt;
        return category;
    }
    
    public void rename(String newName) {
        this.name = Objects.requireNonNull(newName, "Category name cannot be null");
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateDescription(String newDescription) {
        this.description = newDescription;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void moveTo(String newParentId, int newLevel, String newPath) {
        this.parentId = newParentId;
        this.level = newLevel;
        this.path = newPath;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updatePathInfo(int newLevel, String newPath) {
        this.level = newLevel;
        this.path = newPath;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isDescendantOf(Category ancestor) {
        return this.path.startsWith(ancestor.getPath() + "/");
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getParentId() { return parentId; }
    public int getLevel() { return level; }
    public String getPath() { return path; }
    public int getSortOrder() { return sortOrder; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
```

**Step 2: 验证编译通过**

Run: `cd backend && mvn compile -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add backend/src/main/java/com/cms/domain/model/Category.java
git commit -m "feat(domain): add hierarchical fields to Category entity"
```

---

## Task 2: 扩展 CategoryRepository 接口

**Files:**
- Modify: `backend/src/main/java/com/cms/domain/repository/CategoryRepository.java`

**Step 1: 添加层级查询方法**

```java
package com.cms.domain.repository;

import com.cms.domain.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(String id);
    List<Category> findAll();
    void deleteById(String id);
    boolean existsById(String id);
    
    // 新增方法
    List<Category> findByParentId(String parentId);
    List<Category> findRootCategories();
    List<Category> findDescendants(String categoryId);
    boolean hasChildren(String categoryId);
}
```

**Step 2: 验证编译（预期失败，因为实现类未更新）**

Run: `cd backend && mvn compile -q 2>&1 | head -20`
Expected: 编译错误，InMemoryCategoryRepository 未实现新方法

**Step 3: Commit**

```bash
git add backend/src/main/java/com/cms/domain/repository/CategoryRepository.java
git commit -m "feat(domain): add hierarchical query methods to CategoryRepository"
```

---

## Task 3: 实现 InMemoryCategoryRepository 新方法

**Files:**
- Modify: `backend/src/main/java/com/cms/infrastructure/repository/InMemoryCategoryRepository.java`

**Step 1: 实现层级查询方法**

```java
package com.cms.infrastructure.repository;

import com.cms.domain.model.Category;
import com.cms.domain.repository.CategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryCategoryRepository implements CategoryRepository {
    
    private final Map<String, Category> store = new ConcurrentHashMap<>();
    
    @Override
    public Category save(Category category) {
        store.put(category.getId(), category);
        return category;
    }
    
    @Override
    public Optional<Category> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }
    
    @Override
    public List<Category> findAll() {
        return new ArrayList<>(store.values());
    }
    
    @Override
    public void deleteById(String id) {
        store.remove(id);
    }
    
    @Override
    public boolean existsById(String id) {
        return store.containsKey(id);
    }
    
    @Override
    public List<Category> findByParentId(String parentId) {
        return store.values().stream()
                .filter(c -> {
                    if (parentId == null) {
                        return c.getParentId() == null;
                    }
                    return parentId.equals(c.getParentId());
                })
                .sorted((a, b) -> Integer.compare(a.getSortOrder(), b.getSortOrder()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Category> findRootCategories() {
        return findByParentId(null);
    }
    
    @Override
    public List<Category> findDescendants(String categoryId) {
        return findById(categoryId)
                .map(parent -> store.values().stream()
                        .filter(c -> c.getPath().startsWith(parent.getPath() + "/"))
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }
    
    @Override
    public boolean hasChildren(String categoryId) {
        return store.values().stream()
                .anyMatch(c -> categoryId.equals(c.getParentId()));
    }
}
```

**Step 2: 验证编译通过**

Run: `cd backend && mvn compile -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add backend/src/main/java/com/cms/infrastructure/repository/InMemoryCategoryRepository.java
git commit -m "feat(infra): implement hierarchical queries in InMemoryCategoryRepository"
```

---

## Task 4: 更新 DTO 支持层级结构

**Files:**
- Modify: `backend/src/main/java/com/cms/application/dto/CategoryDto.java`
- Modify: `backend/src/main/java/com/cms/application/dto/CreateCategoryRequest.java`
- Modify: `backend/src/main/java/com/cms/application/dto/UpdateCategoryRequest.java`
- Create: `backend/src/main/java/com/cms/application/dto/CategoryTreeDto.java`

**Step 1: 更新 CategoryDto**

```java
package com.cms.application.dto;

import java.time.LocalDateTime;

public record CategoryDto(
    String id,
    String name,
    String description,
    String parentId,
    int level,
    String path,
    int sortOrder,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
```

**Step 2: 更新 CreateCategoryRequest**

```java
package com.cms.application.dto;

public record CreateCategoryRequest(
    String name, 
    String description,
    String parentId  // 可选，null表示创建顶级分类
) {}
```

**Step 3: 更新 UpdateCategoryRequest**

```java
package com.cms.application.dto;

public record UpdateCategoryRequest(
    String name, 
    String description,
    String parentId,  // 移动分类时使用
    Integer sortOrder
) {}
```

**Step 4: 创建 CategoryTreeDto**

```java
package com.cms.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CategoryTreeDto(
    String id,
    String name,
    String description,
    String parentId,
    int level,
    int sortOrder,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<CategoryTreeDto> children
) {}
```

**Step 5: 验证编译通过**

Run: `cd backend && mvn compile -q`
Expected: BUILD SUCCESS

**Step 6: Commit**

```bash
git add backend/src/main/java/com/cms/application/dto/
git commit -m "feat(dto): add hierarchical fields to category DTOs"
```

---

## Task 5: 重构 CategoryService 支持层级操作

**Files:**
- Modify: `backend/src/main/java/com/cms/application/service/CategoryService.java`

**Step 1: 重写 CategoryService**

```java
package com.cms.application.service;

import com.cms.application.dto.*;
import com.cms.domain.model.Category;
import com.cms.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    public CategoryDto create(CreateCategoryRequest request) {
        Category category = Category.create(
            request.name(), 
            request.description(), 
            request.parentId()
        );
        
        // 计算层级和路径
        if (request.parentId() != null) {
            Category parent = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found: " + request.parentId()));
            category.updatePathInfo(
                parent.getLevel() + 1,
                parent.getPath() + "/" + category.getId()
            );
        }
        
        Category saved = categoryRepository.save(category);
        return toDto(saved);
    }
    
    public Optional<CategoryDto> findById(String id) {
        return categoryRepository.findById(id).map(this::toDto);
    }
    
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public List<CategoryDto> findFlat() {
        return categoryRepository.findAll().stream()
                .sorted((a, b) -> {
                    int levelCompare = Integer.compare(a.getLevel(), b.getLevel());
                    if (levelCompare != 0) return levelCompare;
                    return Integer.compare(a.getSortOrder(), b.getSortOrder());
                })
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public List<CategoryTreeDto> findTree() {
        List<Category> roots = categoryRepository.findRootCategories();
        return roots.stream()
                .map(this::buildTree)
                .collect(Collectors.toList());
    }
    
    private CategoryTreeDto buildTree(Category category) {
        List<Category> children = categoryRepository.findByParentId(category.getId());
        List<CategoryTreeDto> childDtos = children.stream()
                .map(this::buildTree)
                .collect(Collectors.toList());
        
        return new CategoryTreeDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getParentId(),
                category.getLevel(),
                category.getSortOrder(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                childDtos
        );
    }
    
    public Optional<CategoryDto> update(String id, UpdateCategoryRequest request) {
        return categoryRepository.findById(id).map(category -> {
            if (request.name() != null) {
                category.rename(request.name());
            }
            if (request.description() != null) {
                category.updateDescription(request.description());
            }
            if (request.sortOrder() != null) {
                category.setSortOrder(request.sortOrder());
            }
            // 移动分类（更改父级）
            if (request.parentId() != null && !request.parentId().equals(category.getParentId())) {
                moveCategory(category, request.parentId());
            } else if (request.parentId() == null && category.getParentId() != null) {
                // 移动到顶级
                moveCategoryToRoot(category);
            }
            
            Category updated = categoryRepository.save(category);
            return toDto(updated);
        });
    }
    
    private void moveCategory(Category category, String newParentId) {
        // 验证不能移动到自己的子孙下
        Category newParent = categoryRepository.findById(newParentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent category not found: " + newParentId));
        
        if (newParent.getPath().startsWith(category.getPath())) {
            throw new IllegalArgumentException("Cannot move category to its own descendant");
        }
        
        String oldPath = category.getPath();
        int oldLevel = category.getLevel();
        
        // 更新当前分类
        category.moveTo(
            newParentId,
            newParent.getLevel() + 1,
            newParent.getPath() + "/" + category.getId()
        );
        
        // 更新所有子孙分类的路径和层级
        updateDescendantPaths(category, oldPath, oldLevel);
    }
    
    private void moveCategoryToRoot(Category category) {
        String oldPath = category.getPath();
        int oldLevel = category.getLevel();
        
        category.moveTo(null, 0, "/" + category.getId());
        
        updateDescendantPaths(category, oldPath, oldLevel);
    }
    
    private void updateDescendantPaths(Category movedCategory, String oldPath, int oldLevel) {
        List<Category> descendants = categoryRepository.findDescendants(movedCategory.getId());
        int levelDiff = movedCategory.getLevel() - oldLevel;
        
        for (Category descendant : descendants) {
            String newPath = descendant.getPath().replace(oldPath, movedCategory.getPath());
            descendant.updatePathInfo(descendant.getLevel() + levelDiff, newPath);
            categoryRepository.save(descendant);
        }
    }
    
    public boolean delete(String id) {
        if (!categoryRepository.existsById(id)) {
            return false;
        }
        
        if (categoryRepository.hasChildren(id)) {
            throw new IllegalStateException("Cannot delete category with children");
        }
        
        categoryRepository.deleteById(id);
        return true;
    }
    
    public List<String> getDescendantIds(String categoryId) {
        List<String> ids = new ArrayList<>();
        ids.add(categoryId);
        
        List<Category> descendants = categoryRepository.findDescendants(categoryId);
        for (Category descendant : descendants) {
            ids.add(descendant.getId());
        }
        
        return ids;
    }
    
    private CategoryDto toDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getParentId(),
                category.getLevel(),
                category.getPath(),
                category.getSortOrder(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
```

**Step 2: 验证编译通过**

Run: `cd backend && mvn compile -q`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add backend/src/main/java/com/cms/application/service/CategoryService.java
git commit -m "feat(service): implement hierarchical category operations"
```

---

## Task 6: 更新 CategoryController

**Files:**
- Modify: `backend/src/main/java/com/cms/presentation/rest/CategoryController.java`

**Step 1: 添加树形和扁平列表端点**

```java
package com.cms.presentation.rest;

import com.cms.application.dto.CategoryDto;
import com.cms.application.dto.CategoryTreeDto;
import com.cms.application.dto.CreateCategoryRequest;
import com.cms.application.dto.UpdateCategoryRequest;
import com.cms.application.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "http://localhost:3000")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    @PostMapping
    public ResponseEntity<CategoryDto> create(@RequestBody CreateCategoryRequest request) {
        try {
            CategoryDto created = categoryService.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDto> getById(@PathVariable String id) {
        return categoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<CategoryTreeDto>> getTree() {
        return ResponseEntity.ok(categoryService.findTree());
    }
    
    @GetMapping("/flat")
    public ResponseEntity<List<CategoryDto>> getFlat() {
        return ResponseEntity.ok(categoryService.findFlat());
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDto> update(@PathVariable String id, 
                                               @RequestBody UpdateCategoryRequest request) {
        try {
            return categoryService.update(id, request)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            if (categoryService.delete(id)) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
```

**Step 2: 验证编译通过**

Run: `cd backend && mvn compile -q`
Expected: BUILD SUCCESS

**Step 3: 运行测试**

Run: `cd backend && mvn test -q`
Expected: Tests pass

**Step 4: Commit**

```bash
git add backend/src/main/java/com/cms/presentation/rest/CategoryController.java
git commit -m "feat(api): add tree and flat endpoints for categories"
```

---

## Task 7: 更新 ContentService 支持按分类树查询

**Files:**
- Modify: `backend/src/main/java/com/cms/domain/repository/ContentRepository.java`
- Modify: `backend/src/main/java/com/cms/infrastructure/repository/InMemoryContentRepository.java`
- Modify: `backend/src/main/java/com/cms/application/service/ContentService.java`
- Modify: `backend/src/main/java/com/cms/presentation/rest/ContentController.java`

**Step 1: 更新 ContentRepository 接口**

```java
package com.cms.domain.repository;

import com.cms.domain.model.Content;
import java.util.List;
import java.util.Optional;

public interface ContentRepository {
    Content save(Content content);
    Optional<Content> findById(String id);
    List<Content> findAll();
    List<Content> findByCategoryId(String categoryId);
    List<Content> findByCategoryIds(List<String> categoryIds);  // 新增
    List<Content> findPublished();
    void deleteById(String id);
    boolean existsById(String id);
}
```

**Step 2: 更新 InMemoryContentRepository**

在 `InMemoryContentRepository.java` 添加：

```java
@Override
public List<Content> findByCategoryIds(List<String> categoryIds) {
    return store.values().stream()
            .filter(c -> categoryIds.contains(c.getCategoryId()))
            .collect(Collectors.toList());
}
```

**Step 3: 更新 ContentService**

在 `ContentService` 添加注入 `CategoryService` 和新方法：

```java
// 在构造函数中添加 CategoryService
private final CategoryService categoryService;

public ContentService(ContentRepository contentRepository, 
                      CategoryRepository categoryRepository,
                      MarkdownRenderer markdownRenderer,
                      CategoryService categoryService) {
    this.contentRepository = contentRepository;
    this.categoryRepository = categoryRepository;
    this.markdownRenderer = markdownRenderer;
    this.categoryService = categoryService;
}

// 新增方法：查询分类及其子孙下的所有内容
public List<ContentDto> findByCategoryWithDescendants(String categoryId) {
    List<String> categoryIds = categoryService.getDescendantIds(categoryId);
    return contentRepository.findByCategoryIds(categoryIds).stream()
            .map(this::toDto)
            .collect(Collectors.toList());
}
```

**Step 4: 更新 ContentController**

修改 `/contents/category/{categoryId}` 端点使用新方法：

```java
@GetMapping("/category/{categoryId}")
public ResponseEntity<List<ContentDto>> getByCategory(@PathVariable String categoryId) {
    return ResponseEntity.ok(contentService.findByCategoryWithDescendants(categoryId));
}
```

**Step 5: 验证编译通过**

Run: `cd backend && mvn compile -q`
Expected: BUILD SUCCESS

**Step 6: Commit**

```bash
git add backend/src/main/java/com/cms/domain/repository/ContentRepository.java
git add backend/src/main/java/com/cms/infrastructure/repository/InMemoryContentRepository.java
git add backend/src/main/java/com/cms/application/service/ContentService.java
git add backend/src/main/java/com/cms/presentation/rest/ContentController.java
git commit -m "feat: content query includes descendant categories"
```

---

## Task 8: 添加图片上传 API

**Files:**
- Create: `backend/src/main/java/com/cms/application/service/ImageUploadService.java`
- Create: `backend/src/main/java/com/cms/presentation/rest/ImageController.java`

**Step 1: 创建 ImageUploadService**

```java
package com.cms.application.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Set;

@Service
public class ImageUploadService {
    
    private static final Set<String> ALLOWED_TYPES = Set.of(
        "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    
    private static final long MAX_SIZE = 2 * 1024 * 1024; // 2MB
    
    public String convertToBase64(MultipartFile file) throws IOException {
        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Unsupported image type: " + contentType);
        }
        
        // 验证文件大小
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("Image size exceeds 2MB limit");
        }
        
        // 转换为 Base64 Data URL
        byte[] bytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(bytes);
        return "data:" + contentType + ";base64," + base64;
    }
}
```

**Step 2: 创建 ImageController**

```java
package com.cms.presentation.rest;

import com.cms.application.service.ImageUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "http://localhost:3000")
public class ImageController {
    
    private final ImageUploadService imageUploadService;
    
    public ImageController(ImageUploadService imageUploadService) {
        this.imageUploadService = imageUploadService;
    }
    
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String dataUrl = imageUploadService.convertToBase64(file);
            return ResponseEntity.ok(Map.of("url", dataUrl));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Failed to process image"));
        }
    }
}
```

**Step 3: 验证编译通过**

Run: `cd backend && mvn compile -q`
Expected: BUILD SUCCESS

**Step 4: Commit**

```bash
git add backend/src/main/java/com/cms/application/service/ImageUploadService.java
git add backend/src/main/java/com/cms/presentation/rest/ImageController.java
git commit -m "feat(api): add image upload endpoint with Base64 encoding"
```

---

## Task 9: 更新前端类型定义

**Files:**
- Modify: `frontend/src/types/index.ts`

**Step 1: 添加树形分类类型**

```typescript
export interface Category {
  id: string
  name: string
  description: string
  parentId: string | null
  level: number
  path: string
  sortOrder: number
  createdAt: string
  updatedAt: string
}

export interface CategoryTreeNode {
  id: string
  name: string
  description: string
  parentId: string | null
  level: number
  sortOrder: number
  createdAt: string
  updatedAt: string
  children: CategoryTreeNode[]
}

export interface Content {
  id: string
  title: string
  markdownContent: string
  htmlContent: string
  categoryId: string
  categoryName: string | null
  status: 'DRAFT' | 'PUBLISHED'
  createdAt: string
  updatedAt: string
  publishedAt: string | null
}

export interface CreateCategoryRequest {
  name: string
  description: string
  parentId?: string | null
}

export interface UpdateCategoryRequest {
  name?: string
  description?: string
  parentId?: string | null
  sortOrder?: number
}

export interface CreateContentRequest {
  title: string
  markdownContent: string
  categoryId: string
}

export interface UpdateContentRequest {
  title: string
  markdownContent: string
  categoryId: string
}

export interface ImageUploadResponse {
  url: string
}
```

**Step 2: 验证 TypeScript 编译**

Run: `cd frontend && npx vue-tsc --noEmit`
Expected: No errors

**Step 3: Commit**

```bash
git add frontend/src/types/index.ts
git commit -m "feat(types): add hierarchical category types"
```

---

## Task 10: 更新前端 API 层

**Files:**
- Modify: `frontend/src/api/index.ts`

**Step 1: 添加新 API 调用**

```typescript
import axios from 'axios'
import type { 
  Category, 
  CategoryTreeNode,
  Content, 
  CreateCategoryRequest, 
  UpdateCategoryRequest, 
  CreateContentRequest, 
  UpdateContentRequest,
  ImageUploadResponse 
} from '@/types'

const api = axios.create({
  baseURL: '/api',
  headers: {
    'Content-Type': 'application/json',
  },
})

export const categoryApi = {
  getTree: () => api.get<CategoryTreeNode[]>('/categories'),
  getFlat: () => api.get<Category[]>('/categories/flat'),
  getById: (id: string) => api.get<Category>(`/categories/${id}`),
  create: (data: CreateCategoryRequest) => api.post<Category>('/categories', data),
  update: (id: string, data: UpdateCategoryRequest) => api.put<Category>(`/categories/${id}`, data),
  delete: (id: string) => api.delete(`/categories/${id}`),
}

export const contentApi = {
  getAll: () => api.get<Content[]>('/contents'),
  getPublished: () => api.get<Content[]>('/contents/published'),
  getById: (id: string) => api.get<Content>(`/contents/${id}`),
  getByCategory: (categoryId: string) => api.get<Content[]>(`/contents/category/${categoryId}`),
  create: (data: CreateContentRequest) => api.post<Content>('/contents', data),
  update: (id: string, data: UpdateContentRequest) => api.put<Content>(`/contents/${id}`, data),
  publish: (id: string) => api.post<Content>(`/contents/${id}/publish`),
  unpublish: (id: string) => api.post<Content>(`/contents/${id}/unpublish`),
  delete: (id: string) => api.delete(`/contents/${id}`),
}

export const imageApi = {
  upload: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return api.post<ImageUploadResponse>('/upload/image', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}

export default api
```

**Step 2: 验证 TypeScript 编译**

Run: `cd frontend && npx vue-tsc --noEmit`
Expected: No errors

**Step 3: Commit**

```bash
git add frontend/src/api/index.ts
git commit -m "feat(api): add tree/flat category endpoints and image upload"
```

---

## Task 11: 更新 Category Store

**Files:**
- Modify: `frontend/src/stores/category.ts`

**Step 1: 添加树形数据支持**

```typescript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Category, CategoryTreeNode, CreateCategoryRequest, UpdateCategoryRequest } from '@/types'
import { categoryApi } from '@/api'

export const useCategoryStore = defineStore('category', () => {
  const categoryTree = ref<CategoryTreeNode[]>([])
  const flatCategories = ref<Category[]>([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 兼容旧代码：从 flatCategories 生成 categories
  const categories = computed(() => flatCategories.value)

  async function fetchCategoryTree() {
    loading.value = true
    error.value = null
    try {
      const response = await categoryApi.getTree()
      categoryTree.value = response.data
    } catch (e) {
      error.value = 'Failed to fetch category tree'
      console.error(e)
    } finally {
      loading.value = false
    }
  }

  async function fetchFlatCategories() {
    loading.value = true
    error.value = null
    try {
      const response = await categoryApi.getFlat()
      flatCategories.value = response.data
    } catch (e) {
      error.value = 'Failed to fetch categories'
      console.error(e)
    } finally {
      loading.value = false
    }
  }

  async function fetchCategories() {
    // 同时获取树形和扁平数据
    await Promise.all([fetchCategoryTree(), fetchFlatCategories()])
  }

  async function createCategory(data: CreateCategoryRequest) {
    loading.value = true
    error.value = null
    try {
      const response = await categoryApi.create(data)
      // 刷新列表
      await fetchCategories()
      return response.data
    } catch (e) {
      error.value = 'Failed to create category'
      console.error(e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function updateCategory(id: string, data: UpdateCategoryRequest) {
    loading.value = true
    error.value = null
    try {
      const response = await categoryApi.update(id, data)
      // 刷新列表
      await fetchCategories()
      return response.data
    } catch (e) {
      error.value = 'Failed to update category'
      console.error(e)
      throw e
    } finally {
      loading.value = false
    }
  }

  async function deleteCategory(id: string) {
    loading.value = true
    error.value = null
    try {
      await categoryApi.delete(id)
      // 刷新列表
      await fetchCategories()
    } catch (e: any) {
      if (e.response?.data?.error) {
        error.value = e.response.data.error
        alert(e.response.data.error)
      } else {
        error.value = 'Failed to delete category'
      }
      console.error(e)
      throw e
    } finally {
      loading.value = false
    }
  }

  // 辅助函数：获取分类的完整路径名称
  function getCategoryPath(categoryId: string): string[] {
    const path: string[] = []
    let current = flatCategories.value.find(c => c.id === categoryId)
    while (current) {
      path.unshift(current.name)
      current = current.parentId 
        ? flatCategories.value.find(c => c.id === current!.parentId)
        : undefined
    }
    return path
  }

  return {
    categoryTree,
    flatCategories,
    categories,
    loading,
    error,
    fetchCategoryTree,
    fetchFlatCategories,
    fetchCategories,
    createCategory,
    updateCategory,
    deleteCategory,
    getCategoryPath,
  }
})
```

**Step 2: 验证 TypeScript 编译**

Run: `cd frontend && npx vue-tsc --noEmit`
Expected: No errors

**Step 3: Commit**

```bash
git add frontend/src/stores/category.ts
git commit -m "feat(store): add tree and flat category data support"
```

---

## Task 12: 更新后台分类管理页面

**Files:**
- Modify: `frontend/src/views/admin/CategoriesView.vue`

**Step 1: 改造为树形展示**

```vue
<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useCategoryStore } from '@/stores/category'
import { storeToRefs } from 'pinia'
import type { CreateCategoryRequest, UpdateCategoryRequest, CategoryTreeNode } from '@/types'

const categoryStore = useCategoryStore()
const { categoryTree, flatCategories, loading } = storeToRefs(categoryStore)

const showModal = ref(false)
const editingId = ref<string | null>(null)
const formData = ref<CreateCategoryRequest>({ name: '', description: '', parentId: null })

onMounted(() => {
  categoryStore.fetchCategories()
})

// 将树形结构扁平化用于显示（保留缩进信息）
interface FlatCategoryItem {
  id: string
  name: string
  description: string
  level: number
  parentId: string | null
  hasChildren: boolean
}

const flattenedTree = computed((): FlatCategoryItem[] => {
  const result: FlatCategoryItem[] = []
  
  function traverse(nodes: CategoryTreeNode[]) {
    for (const node of nodes) {
      result.push({
        id: node.id,
        name: node.name,
        description: node.description,
        level: node.level,
        parentId: node.parentId,
        hasChildren: node.children.length > 0
      })
      if (node.children.length > 0) {
        traverse(node.children)
      }
    }
  }
  
  traverse(categoryTree.value)
  return result
})

function openCreateModal(parentId: string | null = null) {
  editingId.value = null
  formData.value = { name: '', description: '', parentId }
  showModal.value = true
}

function openEditModal(category: FlatCategoryItem) {
  editingId.value = category.id
  formData.value = { 
    name: category.name, 
    description: category.description,
    parentId: category.parentId
  }
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  editingId.value = null
  formData.value = { name: '', description: '', parentId: null }
}

async function handleSubmit() {
  try {
    if (editingId.value) {
      await categoryStore.updateCategory(editingId.value, formData.value as UpdateCategoryRequest)
    } else {
      await categoryStore.createCategory(formData.value)
    }
    closeModal()
  } catch (e) {
    console.error(e)
  }
}

async function handleDelete(id: string) {
  if (confirm('确定要删除这个分类吗？')) {
    try {
      await categoryStore.deleteCategory(id)
    } catch (e) {
      // 错误已在 store 中处理
    }
  }
}

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  })
}

// 获取可选的父分类（排除自己和自己的子孙）
const availableParents = computed(() => {
  if (!editingId.value) {
    return flatCategories.value
  }
  
  const editing = flatCategories.value.find(c => c.id === editingId.value)
  if (!editing) return flatCategories.value
  
  // 排除自己和所有以自己path开头的分类
  return flatCategories.value.filter(c => 
    c.id !== editingId.value && !c.path.startsWith(editing.path + '/')
  )
})
</script>

<template>
  <div class="categories-view">
    <div class="page-header flex justify-between items-center mb-6">
      <h1>分类管理</h1>
      <button class="btn btn-primary" @click="openCreateModal(null)">
        <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <line x1="12" y1="5" x2="12" y2="19"/>
          <line x1="5" y1="12" x2="19" y2="12"/>
        </svg>
        新建顶级分类
      </button>
    </div>

    <div v-if="loading" class="loading">
      <p class="text-muted">加载中...</p>
    </div>

    <div v-else-if="flattenedTree.length === 0" class="empty-state">
      <p class="text-muted">暂无分类，创建第一个分类吧！</p>
    </div>

    <div v-else class="category-tree">
      <div 
        v-for="category in flattenedTree" 
        :key="category.id" 
        class="category-row"
        :style="{ paddingLeft: `${category.level * 24 + 16}px` }"
      >
        <div class="category-content flex justify-between items-center">
          <div class="category-info">
            <span v-if="category.level > 0" class="tree-indent">└─</span>
            <span class="category-name">{{ category.name }}</span>
            <span v-if="category.description" class="category-desc text-muted">
              - {{ category.description }}
            </span>
          </div>
          <div class="category-actions flex gap-2">
            <button 
              class="btn btn-secondary btn-sm" 
              @click="openCreateModal(category.id)"
              title="添加子分类"
            >
              + 子分类
            </button>
            <button class="action-btn" @click="openEditModal(category)" title="编辑">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/>
                <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/>
              </svg>
            </button>
            <button 
              class="action-btn danger" 
              @click="handleDelete(category.id)" 
              title="删除"
              :disabled="category.hasChildren"
            >
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="3 6 5 6 21 6"/>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
      <div class="modal">
        <h2>{{ editingId ? '编辑分类' : '新建分类' }}</h2>
        <form @submit.prevent="handleSubmit">
          <div class="form-group">
            <label for="parentId">父分类</label>
            <select id="parentId" v-model="formData.parentId">
              <option :value="null">-- 顶级分类 --</option>
              <option 
                v-for="cat in availableParents" 
                :key="cat.id" 
                :value="cat.id"
              >
                {{ '　'.repeat(cat.level) }}{{ cat.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label for="name">名称</label>
            <input id="name" v-model="formData.name" type="text" required placeholder="分类名称" />
          </div>
          <div class="form-group">
            <label for="description">描述</label>
            <textarea id="description" v-model="formData.description" rows="3" placeholder="分类描述"></textarea>
          </div>
          <div class="modal-actions flex gap-4 justify-between">
            <button type="button" class="btn btn-secondary" @click="closeModal">取消</button>
            <button type="submit" class="btn btn-primary">{{ editingId ? '更新' : '创建' }}</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.btn-icon {
  width: 1.25rem;
  height: 1.25rem;
}

.btn-sm {
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
}

.category-tree {
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 0.75rem;
  overflow: hidden;
}

.category-row {
  border-bottom: 1px solid var(--color-border);
  padding: 0.75rem 1rem;
  transition: background-color var(--transition-fast);
}

.category-row:last-child {
  border-bottom: none;
}

.category-row:hover {
  background-color: rgba(255, 255, 255, 0.05);
}

.category-content {
  min-height: 2rem;
}

.category-info {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.tree-indent {
  color: var(--color-text-muted);
  font-family: monospace;
}

.category-name {
  font-weight: 500;
}

.category-desc {
  font-size: 0.875rem;
}

.category-actions {
  opacity: 0;
  transition: opacity var(--transition-fast);
}

.category-row:hover .category-actions {
  opacity: 1;
}

.action-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 2rem;
  height: 2rem;
  border: none;
  background: transparent;
  color: var(--color-text-muted);
  border-radius: 0.375rem;
  cursor: pointer;
}

.action-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
  color: var(--color-text);
}

.action-btn.danger:hover {
  color: var(--color-error);
}

.action-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.action-btn svg {
  width: 1rem;
  height: 1rem;
}

.empty-state, .loading {
  text-align: center;
  padding: 4rem 0;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 0.75rem;
  padding: 2rem;
  width: 100%;
  max-width: 500px;
}

.modal h2 {
  margin-bottom: 1.5rem;
}

.modal-actions {
  margin-top: 2rem;
}
</style>
```

**Step 2: 验证前端构建**

Run: `cd frontend && npm run build 2>&1 | tail -5`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add frontend/src/views/admin/CategoriesView.vue
git commit -m "feat(ui): update category management to tree view"
```

---

## Task 13: 更新后台内容编辑页面（树形分类选择 + 图片上传）

**Files:**
- Modify: `frontend/src/views/admin/ContentView.vue`

**Step 1: 添加树形分类下拉和图片上传**

在 `<script setup>` 中添加：

```typescript
import { imageApi } from '@/api'

// 图片上传处理
const onUploadImg = async (files: File[], callback: (urls: string[]) => void) => {
  try {
    const urls = await Promise.all(
      files.map(async (file) => {
        const response = await imageApi.upload(file)
        return response.data.url
      })
    )
    callback(urls)
  } catch (e) {
    console.error('Image upload failed:', e)
    alert('图片上传失败')
  }
}
```

修改 `<MdEditor>` 组件添加 `@onUploadImg`：

```vue
<MdEditor 
  v-model="formData.markdownContent" 
  theme="dark" 
  language="zh-CN"
  :preview="true"
  style="height: 400px;"
  @onUploadImg="onUploadImg"
/>
```

修改分类选择下拉，显示层级：

```vue
<select id="categoryId" v-model="formData.categoryId" required>
  <option 
    v-for="cat in flatCategories" 
    :key="cat.id" 
    :value="cat.id"
  >
    {{ '　'.repeat(cat.level) }}{{ cat.name }}
  </option>
</select>
```

并在 script 中引入 `flatCategories`：

```typescript
const { flatCategories } = storeToRefs(categoryStore)
```

**Step 2: 验证前端构建**

Run: `cd frontend && npm run build 2>&1 | tail -5`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add frontend/src/views/admin/ContentView.vue
git commit -m "feat(ui): add tree category select and image upload to content editor"
```

---

## Task 14: 添加前台多级导航栏

**Files:**
- Modify: `frontend/src/layouts/PublicLayout.vue`

**Step 1: 添加多级导航组件**

```vue
<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCategoryStore } from '@/stores/category'
import { storeToRefs } from 'pinia'
import type { CategoryTreeNode } from '@/types'

const route = useRoute()
const router = useRouter()
const categoryStore = useCategoryStore()
const { categoryTree } = storeToRefs(categoryStore)

const hoveredCategory = ref<string | null>(null)

onMounted(() => {
  categoryStore.fetchCategories()
})

const currentCategoryId = computed(() => {
  return route.query.category as string | undefined
})

function navigateToCategory(categoryId: string | null) {
  if (categoryId) {
    router.push({ path: '/', query: { category: categoryId } })
  } else {
    router.push({ path: '/' })
  }
}

function hasDescendants(node: CategoryTreeNode): boolean {
  return node.children && node.children.length > 0
}
</script>

<template>
  <div class="public-layout">
    <header class="public-header">
      <div class="header-content">
        <router-link to="/" class="logo">CMS</router-link>
        
        <nav class="category-nav">
          <ul class="nav-list">
            <li class="nav-item">
              <a 
                href="#" 
                @click.prevent="navigateToCategory(null)"
                :class="{ active: !currentCategoryId }"
              >
                全部
              </a>
            </li>
            <li 
              v-for="category in categoryTree" 
              :key="category.id"
              class="nav-item"
              @mouseenter="hoveredCategory = category.id"
              @mouseleave="hoveredCategory = null"
            >
              <a 
                href="#"
                @click.prevent="navigateToCategory(category.id)"
                :class="{ active: currentCategoryId === category.id }"
              >
                {{ category.name }}
                <svg v-if="hasDescendants(category)" class="dropdown-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polyline points="6 9 12 15 18 9"/>
                </svg>
              </a>
              
              <!-- 子分类下拉 -->
              <ul v-if="hasDescendants(category) && hoveredCategory === category.id" class="dropdown-menu">
                <li v-for="child in category.children" :key="child.id" class="dropdown-item">
                  <a 
                    href="#"
                    @click.prevent="navigateToCategory(child.id)"
                    :class="{ active: currentCategoryId === child.id }"
                  >
                    {{ child.name }}
                  </a>
                  
                  <!-- 三级分类 -->
                  <ul v-if="hasDescendants(child)" class="sub-dropdown">
                    <li v-for="grandchild in child.children" :key="grandchild.id">
                      <a 
                        href="#"
                        @click.prevent="navigateToCategory(grandchild.id)"
                        :class="{ active: currentCategoryId === grandchild.id }"
                      >
                        {{ grandchild.name }}
                      </a>
                    </li>
                  </ul>
                </li>
              </ul>
            </li>
          </ul>
        </nav>
        
        <router-link to="/admin" class="admin-link">管理后台</router-link>
      </div>
    </header>
    
    <main class="public-main">
      <router-view />
    </main>
    
    <footer class="public-footer">
      <p>&copy; 2026 CMS. All rights reserved.</p>
    </footer>
  </div>
</template>

<style scoped>
.public-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.public-header {
  background-color: var(--color-surface);
  border-bottom: 1px solid var(--color-border);
  padding: 0 2rem;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
}

.logo {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--color-text);
  text-decoration: none;
}

.category-nav {
  flex: 1;
  display: flex;
  justify-content: center;
}

.nav-list {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: 0.5rem;
}

.nav-item {
  position: relative;
}

.nav-item > a {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.5rem 1rem;
  color: var(--color-text-muted);
  text-decoration: none;
  border-radius: 0.375rem;
  transition: all var(--transition-fast);
}

.nav-item > a:hover,
.nav-item > a.active {
  color: var(--color-text);
  background-color: rgba(255, 255, 255, 0.1);
}

.dropdown-icon {
  width: 1rem;
  height: 1rem;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  min-width: 160px;
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 0.5rem;
  padding: 0.5rem 0;
  list-style: none;
  margin: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.dropdown-item {
  position: relative;
}

.dropdown-item > a {
  display: block;
  padding: 0.5rem 1rem;
  color: var(--color-text-muted);
  text-decoration: none;
  transition: all var(--transition-fast);
}

.dropdown-item > a:hover,
.dropdown-item > a.active {
  color: var(--color-text);
  background-color: rgba(255, 255, 255, 0.1);
}

.sub-dropdown {
  display: none;
  position: absolute;
  left: 100%;
  top: 0;
  min-width: 140px;
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 0.5rem;
  padding: 0.5rem 0;
  list-style: none;
  margin: 0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

.dropdown-item:hover .sub-dropdown {
  display: block;
}

.sub-dropdown a {
  display: block;
  padding: 0.5rem 1rem;
  color: var(--color-text-muted);
  text-decoration: none;
  transition: all var(--transition-fast);
}

.sub-dropdown a:hover,
.sub-dropdown a.active {
  color: var(--color-text);
  background-color: rgba(255, 255, 255, 0.1);
}

.admin-link {
  padding: 0.5rem 1rem;
  color: var(--color-text-muted);
  text-decoration: none;
  border: 1px solid var(--color-border);
  border-radius: 0.375rem;
  transition: all var(--transition-fast);
}

.admin-link:hover {
  color: var(--color-text);
  border-color: var(--color-text-muted);
}

.public-main {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  width: 100%;
}

.public-footer {
  background-color: var(--color-surface);
  border-top: 1px solid var(--color-border);
  padding: 1.5rem;
  text-align: center;
}

.public-footer p {
  color: var(--color-text-muted);
  margin: 0;
  font-size: 0.875rem;
}
</style>
```

**Step 2: 验证前端构建**

Run: `cd frontend && npm run build 2>&1 | tail -5`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add frontend/src/layouts/PublicLayout.vue
git commit -m "feat(ui): add multi-level category navigation"
```

---

## Task 15: 更新前台首页支持分类筛选

**Files:**
- Modify: `frontend/src/views/public/HomeView.vue`

**Step 1: 添加分类筛选和面包屑**

```vue
<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useContentStore } from '@/stores/content'
import { useCategoryStore } from '@/stores/category'
import { storeToRefs } from 'pinia'
import { contentApi } from '@/api'
import type { Content } from '@/types'

const route = useRoute()
const contentStore = useContentStore()
const categoryStore = useCategoryStore()
const { loading } = storeToRefs(contentStore)
const { flatCategories } = storeToRefs(categoryStore)

const filteredContents = ref<Content[]>([])

const currentCategoryId = computed(() => route.query.category as string | undefined)

const breadcrumb = computed(() => {
  if (!currentCategoryId.value) return []
  return categoryStore.getCategoryPath(currentCategoryId.value)
})

const currentCategoryName = computed(() => {
  if (!currentCategoryId.value) return '全部内容'
  const cat = flatCategories.value.find(c => c.id === currentCategoryId.value)
  return cat?.name || '全部内容'
})

async function loadContents() {
  if (currentCategoryId.value) {
    const response = await contentApi.getByCategory(currentCategoryId.value)
    filteredContents.value = response.data.filter(c => c.status === 'PUBLISHED')
  } else {
    const response = await contentApi.getPublished()
    filteredContents.value = response.data
  }
}

onMounted(async () => {
  await categoryStore.fetchCategories()
  await loadContents()
})

watch(currentCategoryId, () => {
  loadContents()
})

function formatDate(dateStr: string): string {
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script>

<template>
  <div class="home-view">
    <!-- 面包屑导航 -->
    <div v-if="breadcrumb.length > 0" class="breadcrumb">
      <router-link to="/">首页</router-link>
      <span v-for="(name, index) in breadcrumb" :key="index">
        <span class="separator">/</span>
        <span :class="{ current: index === breadcrumb.length - 1 }">{{ name }}</span>
      </span>
    </div>

    <h1 class="page-title">{{ currentCategoryName }}</h1>

    <div v-if="loading" class="loading">
      <p class="text-muted">加载中...</p>
    </div>

    <div v-else-if="filteredContents.length === 0" class="empty-state">
      <p class="text-muted">暂无内容</p>
    </div>

    <div v-else class="content-list">
      <router-link 
        v-for="content in filteredContents" 
        :key="content.id"
        :to="`/content/${content.id}`"
        class="content-card"
      >
        <h2 class="content-title">{{ content.title }}</h2>
        <div class="content-meta">
          <span v-if="content.categoryName" class="category-tag">
            {{ content.categoryName }}
          </span>
          <span class="publish-date">{{ formatDate(content.publishedAt || content.createdAt) }}</span>
        </div>
      </router-link>
    </div>
  </div>
</template>

<style scoped>
.home-view {
  max-width: 800px;
  margin: 0 auto;
}

.breadcrumb {
  margin-bottom: 1rem;
  font-size: 0.875rem;
  color: var(--color-text-muted);
}

.breadcrumb a {
  color: var(--color-text-muted);
  text-decoration: none;
}

.breadcrumb a:hover {
  color: var(--color-text);
}

.breadcrumb .separator {
  margin: 0 0.5rem;
}

.breadcrumb .current {
  color: var(--color-text);
}

.page-title {
  margin-bottom: 2rem;
}

.content-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.content-card {
  display: block;
  padding: 1.5rem;
  background-color: var(--color-surface);
  border: 1px solid var(--color-border);
  border-radius: 0.75rem;
  text-decoration: none;
  transition: all var(--transition-fast);
}

.content-card:hover {
  border-color: var(--color-text-muted);
  transform: translateY(-2px);
}

.content-title {
  margin: 0 0 0.75rem;
  color: var(--color-text);
  font-size: 1.25rem;
}

.content-meta {
  display: flex;
  align-items: center;
  gap: 1rem;
  font-size: 0.875rem;
  color: var(--color-text-muted);
}

.category-tag {
  background-color: rgba(255, 255, 255, 0.1);
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
}

.loading, .empty-state {
  text-align: center;
  padding: 4rem 0;
}
</style>
```

**Step 2: 验证前端构建**

Run: `cd frontend && npm run build 2>&1 | tail -5`
Expected: BUILD SUCCESS

**Step 3: Commit**

```bash
git add frontend/src/views/public/HomeView.vue
git commit -m "feat(ui): add category filtering and breadcrumb to home page"
```

---

## Task 16: 最终验证

**Step 1: 运行后端测试**

Run: `cd backend && mvn test`
Expected: All tests pass

**Step 2: 运行前端构建**

Run: `cd frontend && npm run build`
Expected: BUILD SUCCESS

**Step 3: 启动应用进行手动测试**

Run: `./deploy.sh`
Expected: 前后端均启动成功

**手动测试检查项：**
1. 后台创建多级分类（顶级 > 二级 > 三级）
2. 后台编辑分类、移动分类位置
3. 后台删除无子分类的分类
4. 后台创建内容时选择树形分类
5. 后台编辑内容时上传图片
6. 前台导航栏显示多级分类菜单
7. 前台点击分类筛选内容（含子孙分类内容）
8. 前台显示面包屑导航

**Step 4: 最终提交**

```bash
git add -A
git commit -m "chore: iteration1 complete - multi-level categories and image upload"
```

---

## 总结

| Task | 描述 | 估时 |
|------|------|------|
| 1 | 扩展 Category 领域模型 | 15min |
| 2 | 扩展 CategoryRepository 接口 | 5min |
| 3 | 实现 InMemoryCategoryRepository 新方法 | 15min |
| 4 | 更新 DTO 支持层级结构 | 10min |
| 5 | 重构 CategoryService 支持层级操作 | 30min |
| 6 | 更新 CategoryController | 10min |
| 7 | 更新 ContentService 支持按分类树查询 | 15min |
| 8 | 添加图片上传 API | 15min |
| 9 | 更新前端类型定义 | 10min |
| 10 | 更新前端 API 层 | 10min |
| 11 | 更新 Category Store | 15min |
| 12 | 更新后台分类管理页面 | 30min |
| 13 | 更新后台内容编辑页面 | 15min |
| 14 | 添加前台多级导航栏 | 30min |
| 15 | 更新前台首页支持分类筛选 | 20min |
| 16 | 最终验证 | 20min |

**总计：约 4.5 小时**
