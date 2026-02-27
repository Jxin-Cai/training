# Quickstart: CMS 内容管理系统

**Feature**: 002-cms  
**Date**: 2026-02-27

## Prerequisites

- Node.js 18+
- Python 3.11+
- uv (Python package manager)

---

## Project Setup

### 1. Backend Setup

```bash
# Create backend directory
mkdir -p backend/src/models backend/src/services backend/src/api
cd backend

# Create virtual environment with uv
uv venv
source .venv/bin/activate

# Install dependencies
uv pip install fastapi uvicorn sqlalchemy pydantic python-multipart markdown-it

# Create requirements.txt
cat > requirements.txt <<EOF
fastapi==0.109.0
uvicorn==0.27.0
sqlalchemy==2.0.25
pydantic==2.5.3
python-multipart==0.0.6
markdown-it==0.1.1
EOF
```

### 2. Frontend Setup

```bash
# Create frontend directory
cd ..
mkdir -p frontend
cd frontend

# Create Vite React TypeScript project
npm create vite@latest . -- --template react-ts

# Install dependencies
npm install
npm install react-router-dom @uiw/react-md-editor dompurify

# Install dev dependencies
npm install -D @types/dompurify
```

---

## Running the Application

### Start Backend

```bash
cd backend
source .venv/bin/activate
uvicorn src.main:app --reload --port 8000
```

Backend API will be available at `http://localhost:8000`

### Start Frontend

```bash
cd frontend
npm run dev
```

Frontend will be available at `http://localhost:5173`

---

## API Endpoints

### Categories

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/categories` | List all categories |
| POST | `/api/categories` | Create category |
| GET | `/api/categories/{id}` | Get category |
| PUT | `/api/categories/{id}` | Update category |
| DELETE | `/api/categories/{id}` | Delete category |

### Contents (Public)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/contents` | List published contents |
| GET | `/api/contents/{id}` | Get content detail |

### Contents (Admin)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/admin/contents` | List all contents |
| POST | `/api/contents` | Create content |
| GET | `/api/admin/contents/{id}` | Get content (all) |
| PUT | `/api/contents/{id}` | Update content |
| DELETE | `/api/contents/{id}` | Delete content |
| POST | `/api/contents/upload` | Upload MD file |

---

## Frontend Routes

| Route | Description |
|-------|-------------|
| `/` | 首页 - 内容列表 |
| `/content/:id` | 内容详情页 |
| `/admin/categories` | 分类管理 |
| `/admin/contents` | 内容管理 |
| `/admin/contents/new` | 新建内容 |
| `/admin/contents/:id/edit` | 编辑内容 |

---

## Development Workflow

1. **Backend**: 修改 `backend/src/` 下的代码
2. **Frontend**: 修改 `frontend/src/` 下的代码
3. **API 测试**: 访问 `http://localhost:8000/docs` 查看 OpenAPI 文档

---

## Testing

### Backend Tests

```bash
cd backend
pytest
```

### Frontend Tests

```bash
cd frontend
npm test
```