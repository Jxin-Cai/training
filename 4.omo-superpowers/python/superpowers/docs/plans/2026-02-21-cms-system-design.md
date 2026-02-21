# CMS System Design

## Project Overview

- **Project Name**: CMS System
- **Type**: Content Management System with Admin Panel & Public Frontend
- **Core Functionality**: Category management, Markdown content creation with auto-rendering, content publishing workflow
- **Target Users**: Content creators, blog owners, small teams

## Technology Stack

| Layer | Technology |
|-------|------------|
| Backend | Python FastAPI + SQLAlchemy |
| Database | SQLite |
| Frontend | React + TypeScript + Vite |
| MD Rendering | marked + highlight.js |
| Authentication | JWT |

## Database Schema

### users
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER PK | User ID |
| username | VARCHAR(50) UNIQUE | Username |
| password_hash | VARCHAR(255) | Hashed password |
| role | VARCHAR(20) | admin/editor |
| created_at | DATETIME | Creation timestamp |

### categories
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER PK | Category ID |
| name | VARCHAR(100) | Category name |
| slug | VARCHAR(100) UNIQUE | URL slug |
| sort_order | INTEGER | Display order |
| created_at | DATETIME | Creation timestamp |

### contents
| Column | Type | Description |
|--------|------|-------------|
| id | INTEGER PK | Content ID |
| title | VARCHAR(200) | Content title |
| slug | VARCHAR(200) UNIQUE | URL slug |
| category_id | INTEGER FK | Category reference |
| markdown_content | TEXT | Raw Markdown |
| html_content | TEXT | Rendered HTML |
| status | VARCHAR(20) | draft/published |
| author_id | INTEGER FK | Author reference |
| created_at | DATETIME | Creation timestamp |
| updated_at | DATETIME | Last update |
| published_at | DATETIME | Publish timestamp |

## API Design

### Authentication
- `POST /api/auth/login` - Login with username/password
- `POST /api/auth/register` - Register first admin user
- `GET /api/auth/me` - Get current user info

### Category Management (Auth Required)
- `GET /api/admin/categories` - List all categories
- `POST /api/admin/categories` - Create category
- `PUT /api/admin/categories/{id}` - Update category
- `DELETE /api/admin/categories/{id}` - Delete category

### Content Management (Auth Required)
- `GET /api/admin/contents` - List contents (filter by status)
- `POST /api/admin/contents` - Create content
- `PUT /api/admin/contents/{id}` - Update content
- `DELETE /api/admin/contents/{id}` - Delete content
- `POST /api/admin/contents/{id}/publish` - Publish content
- `POST /api/admin/contents/{id}/unpublish` - Unpublish content

### Public API
- `GET /api/contents` - List published contents (sorted by published_at DESC)
- `GET /api/contents/{slug}` - Get content by slug
- `GET /api/categories` - List categories for navigation

## Frontend Pages

### Frontend (Public)
- `/` - Content list page
- `/content/:slug` - Content detail page

### Backend (Admin)
- `/admin/login` - Login page
- `/admin` - Dashboard
- `/admin/categories` - Category management
- `/admin/contents` - Content list
- `/admin/contents/new` - Create content
- `/admin/contents/:id/edit` - Edit content

## Key Features

1. **MD Editor**: Online Markdown editor with live preview
2. **Auto-render**: Save content → convert MD to HTML → store both
3. **Publishing Workflow**: Draft → Published status management
4. **Category Association**: Each content links to one category
5. **JWT Auth**: Secure API access with token-based auth

## Acceptance Criteria

1. Can create/edit/delete categories
2. Can create/edit/delete content with Markdown
3. Markdown auto-converts to HTML on save
4. Public can view only published content
5. Content sorted by published date (newest first)
6. Admin requires authentication
7. Multi-user support with role-based access