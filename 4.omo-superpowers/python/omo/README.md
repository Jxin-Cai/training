# CMS System

A simple content management system with admin panel and public frontend.

## Tech Stack

- **Backend**: FastAPI + SQLite
- **Frontend**: React + Vite

## Quick Start

### Backend

```bash
cd backend

# Install dependencies
pip install -e .
# or using uv
uv sync

# Run server
uvicorn app.main:app --reload
```

The backend will start at `http://localhost:8000`

### Frontend

```bash
cd frontend

# Install dependencies
npm install

# Run dev server
npm run dev
```

The frontend will start at `http://localhost:5173`

## Default Admin Credentials

- Username: `admin`
- Password: `admin123`

## API Endpoints

### Public
- `GET /api/articles/published` - List published articles
- `GET /api/articles/published/{id}` - Get published article detail

### Admin (requires authentication)
- `POST /api/auth/login` - Login
- `GET /api/categories` - List categories
- `POST /api/categories` - Create category
- `PUT /api/categories/{id}` - Update category
- `DELETE /api/categories/{id}` - Delete category
- `GET /api/articles` - List all articles (including drafts)
- `POST /api/articles` - Create article
- `PUT /api/articles/{id}` - Update article
- `DELETE /api/articles/{id}` - Delete article

## Features

- Markdown content editing with live preview
- Auto-convert Markdown to HTML on save
- XSS protection with HTML sanitization
- Category management
- Draft/Published status
- File import for Markdown content