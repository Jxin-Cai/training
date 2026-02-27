# Data Model: CMS 内容管理系统

**Feature**: 002-cms  
**Date**: 2026-02-27

## Entities

### Category (分类)

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | INTEGER | PRIMARY KEY, AUTOINCREMENT | 分类唯一标识 |
| name | VARCHAR(100) | NOT NULL, UNIQUE | 分类名称 |
| created_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| updated_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 更新时间 |

**Validation Rules**:
- name: 1-100 characters, required, unique
- name 不允许重复

**State Transitions**: N/A (simple CRUD)

---

### Content (内容)

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| id | INTEGER | PRIMARY KEY, AUTOINCREMENT | 内容唯一标识 |
| title | VARCHAR(200) | NOT NULL | 内容标题 |
| category_id | INTEGER | NOT NULL, FOREIGN KEY | 关联分类 ID |
| markdown_content | TEXT | NOT NULL | MD 原始内容 |
| html_content | TEXT | NOT NULL | 渲染后的 HTML |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'draft' | 状态: published/draft |
| created_at | DATETIME | NOT NULL, DEFAULT CURRENT_TIMESTAMP | 创建时间 |
| published_at | DATETIME | NULL | 发布时间 |

**Validation Rules**:
- title: 1-200 characters, required
- category_id: required, must exist in categories table
- markdown_content: required
- html_content: required, auto-generated from markdown_content
- status: enum('published', 'draft'), default 'draft'

**State Transitions**:
```
draft → published (when published)
published → draft (when unpublished or edited)
```

---

## Relationships

```
Category (1) ──────< Content (N)
```

- One Category can have many Contents
- One Content belongs to exactly one Category
- Deleting Category is only allowed when no Contents reference it

---

## API Data Structures

### Category DTO

```typescript
interface Category {
  id: number;
  name: string;
  created_at: string;
  updated_at: string;
}

interface CreateCategoryRequest {
  name: string;
}

interface UpdateCategoryRequest {
  name: string;
}
```

### Content DTO

```typescript
interface Content {
  id: number;
  title: string;
  category_id: number;
  category_name?: string;
  markdown_content: string;
  html_content: string;
  status: 'published' | 'draft';
  created_at: string;
  published_at: string | null;
}

interface CreateContentRequest {
  title: string;
  category_id: number;
  markdown_content: string;
  status: 'published' | 'draft';
}

interface UpdateContentRequest {
  title?: string;
  category_id?: number;
  markdown_content?: string;
  status?: 'published' | 'draft';
}
```

---

## Database Schema (SQLite)

```sql
CREATE TABLE categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE contents (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title VARCHAR(200) NOT NULL,
    category_id INTEGER NOT NULL,
    markdown_content TEXT NOT NULL,
    html_content TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'draft',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at DATETIME,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Index for content listing by published_at
CREATE INDEX idx_contents_published_at ON contents(published_at) WHERE status = 'published';

-- Index for content by category
CREATE INDEX idx_contents_category ON contents(category_id);
```