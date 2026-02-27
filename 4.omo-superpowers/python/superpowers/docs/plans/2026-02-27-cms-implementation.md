# CMS Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 构建一个包含后台管理和前台展示的 CMS 系统，支持 MD 格式内容管理

**Architecture:** 前后端分离架构，后端 FastAPI + SQLite，前端 React + Vite，通过 Vite 代理访问 API

**Tech Stack:** FastAPI, UV, React, Vite, SQLite, marked

---

## Phase 1: Backend Setup

### Task 1: Initialize FastAPI Project

**Files:**
- Create: `backend/pyproject.toml`
- Create: `backend/app/__init__.py`
- Create: `backend/app/main.py`
- Create: `backend/app/database.py`
- Create: `backend/app/models.py`
- Create: `backend/app/schemas.py`
- Create: `backend/app/crud.py`

**Step 1: Create pyproject.toml**

```toml
[project]
name = "cms-backend"
version = "0.1.0"
description = "CMS Backend API"
requires-python = ">=3.10"
dependencies = [
    "fastapi>=0.109.0",
    "uvicorn>=0.27.0",
    "sqlalchemy>=2.0.0",
    "pydantic>=2.5.0",
    "python-jose[cryptography]>=3.3.0",
    "passlib[bcrypt]>=1.7.4",
    "python-multipart>=0.0.6",
]

[tool.uv]
dev-dependencies = [
    "pytest>=7.4.0",
    "httpx>=0.26.0",
]
```

**Step 2: Install dependencies**

Run: `cd backend && uv sync`

**Step 3: Commit**

```bash
git add backend/
git commit -m "feat: initialize FastAPI backend project"
```

---

### Task 2: Create Database Models

**Files:**
- Modify: `backend/app/models.py`
- Modify: `backend/app/database.py`

**Step 1: Write database.py**

```python
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base

SQLALCHEMY_DATABASE_URL = "sqlite:///./cms.db"

engine = create_engine(
    SQLALCHEMY_DATABASE_URL, connect_args={"check_same_thread": False}
)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
```

**Step 2: Write models.py**

```python
from sqlalchemy import Column, Integer, String, Text, DateTime, ForeignKey
from sqlalchemy.orm import relationship
from datetime import datetime
from database import Base

class Category(Base):
    __tablename__ = "categories"
    
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), unique=True, nullable=False)
    created_at = Column(DateTime, default=datetime.utcnow)
    
    articles = relationship("Article", back_populates="category")

class Article(Base):
    __tablename__ = "articles"
    
    id = Column(Integer, primary_key=True, index=True)
    title = Column(String(200), nullable=False)
    content_md = Column(Text, nullable=False)
    content_html = Column(Text, nullable=False)
    category_id = Column(Integer, ForeignKey("categories.id"))
    status = Column(String(20), default="draft")
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    
    category = relationship("Category", back_populates="articles")

class User(Base):
    __tablename__ = "users"
    
    id = Column(Integer, primary_key=True, index=True)
    username = Column(String(50), unique=True, nullable=False)
    password_hash = Column(String(255), nullable=False)
```

**Step 3: Create database tables**

Run: `cd backend && python -c "from app.database import engine, Base; Base.metadata.create_all(bind=engine)"`

**Step 4: Commit**

```bash
git add backend/app/database.py backend/app/models.py
git commit -m "feat: add database models and setup"
```

---

### Task 3: Create Pydantic Schemas

**Files:**
- Modify: `backend/app/schemas.py`

**Step 1: Write schemas.py**

```python
from pydantic import BaseModel
from datetime import datetime
from typing import Optional

class CategoryBase(BaseModel):
    name: str

class CategoryCreate(CategoryBase):
    pass

class CategoryResponse(CategoryBase):
    id: int
    created_at: datetime
    
    class Config:
        from_attributes = True

class ArticleBase(BaseModel):
    title: str
    content_md: str
    category_id: int
    status: str = "draft"

class ArticleCreate(ArticleBase):
    pass

class ArticleUpdate(BaseModel):
    title: Optional[str] = None
    content_md: Optional[str] = None
    category_id: Optional[int] = None
    status: Optional[str] = None

class ArticleResponse(BaseModel):
    id: int
    title: str
    content_md: str
    content_html: str
    category_id: int
    status: str
    created_at: datetime
    updated_at: datetime
    
    class Config:
        from_attributes = True

class UserLogin(BaseModel):
    username: str
    password: str

class Token(BaseModel):
    access_token: str
    token_type: str
```

**Step 2: Commit**

```bash
git add backend/app/schemas.py
git commit -m "feat: add Pydantic schemas"
```

---

### Task 4: Implement CRUD Operations

**Files:**
- Modify: `backend/app/crud.py`

**Step 1: Write crud.py**

```python
from sqlalchemy.orm import Session
from models import Category, Article, User
from passlib.context import CryptContext
from datetime import datetime

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

def get_password_hash(password: str) -> str:
    return pwd_context.hash(password)

def verify_password(plain_password: str, hashed_password: str) -> bool:
    return pwd_context.verify(plain_password, hashed_password)

# Category CRUD
def get_categories(db: Session):
    return db.query(Category).all()

def get_category(db: Session, category_id: int):
    return db.query(Category).filter(Category.id == category_id).first()

def create_category(db: Session, name: str):
    category = Category(name=name)
    db.add(category)
    db.commit()
    db.refresh(category)
    return category

def update_category(db: Session, category_id: int, name: str):
    category = get_category(db, category_id)
    if category:
        category.name = name
        db.commit()
        db.refresh(category)
    return category

def delete_category(db: Session, category_id: int):
    category = get_category(db, category_id)
    if category:
        db.delete(category)
        db.commit()
    return category

# Article CRUD
def get_articles(db: Session, skip: int = 0, limit: int = 100):
    return db.query(Article).order_by(Article.created_at.desc()).offset(skip).limit(limit).all()

def get_published_articles(db: Session, skip: int = 0, limit: int = 100):
    return db.query(Article).filter(Article.status == "published").order_by(Article.created_at.desc()).offset(skip).limit(limit).all()

def get_article(db: Session, article_id: int):
    return db.query(Article).filter(Article.id == article_id).first()

def create_article(db: Session, title: str, content_md: str, content_html: str, category_id: int, status: str = "draft"):
    article = Article(
        title=title,
        content_md=content_md,
        content_html=content_html,
        category_id=category_id,
        status=status
    )
    db.add(article)
    db.commit()
    db.refresh(article)
    return article

def update_article(db: Session, article_id: int, **kwargs):
    article = get_article(db, article_id)
    if article:
        for key, value in kwargs.items():
            if value is not None and hasattr(article, key):
                setattr(article, key, value)
        article.updated_at = datetime.utcnow()
        db.commit()
        db.refresh(article)
    return article

def delete_article(db: Session, article_id: int):
    article = get_article(db, article_id)
    if article:
        db.delete(article)
        db.commit()
    return article

# User
def get_user_by_username(db: Session, username: str):
    return db.query(User).filter(User.username == username).first()

def create_user(db: Session, username: str, password: str):
    user = User(username=username, password_hash=get_password_hash(password))
    db.add(user)
    db.commit()
    db.refresh(user)
    return user
```

**Step 2: Commit**

```bash
git add backend/app/crud.py
git commit -m "feat: add CRUD operations"
```

---

### Task 5: Implement API Routes

**Files:**
- Modify: `backend/app/main.py`

**Step 1: Write main.py**

```python
from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session
from database import get_db
import crud
import schemas
from jose import JWTError, jwt
from datetime import datetime, timedelta
from passlib.context import CryptContext

SECRET_KEY = "your-secret-key-change-in-production"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 30

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

app = FastAPI(title="CMS API")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

def create_access_token(data: dict):
    to_encode = data.copy()
    expire = datetime.utcnow() + timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    to_encode.update({"exp": expire})
    return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)

def get_current_user(token: str = None, db: Session = Depends(get_db)):
    if not token:
        raise HTTPException(status_code=401, detail="Not authenticated")
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        username: str = payload.get("sub")
        if username is None:
            raise HTTPException(status_code=401, detail="Invalid token")
    except JWTError:
        raise HTTPException(status_code=401, detail="Invalid token")
    
    user = crud.get_user_by_username(db, username)
    if user is None:
        raise HTTPException(status_code=401, detail="User not found")
    return user

# Auth
@app.post("/api/auth/login", response_model=schemas.Token)
def login(user_login: schemas.UserLogin, db: Session = Depends(get_db)):
    user = crud.get_user_by_username(db, user_login.username)
    if not user or not pwd_context.verify(user_login.password, user.password_hash):
        raise HTTPException(status_code=401, detail="Incorrect username or password")
    access_token = create_access_token(data={"sub": user.username})
    return {"access_token": access_token, "token_type": "bearer"}

# Categories
@app.get("/api/categories", response_model=list[schemas.CategoryResponse])
def read_categories(db: Session = Depends(get_db)):
    return crud.get_categories(db)

@app.post("/api/categories", response_model=schemas.CategoryResponse)
def create_category(category: schemas.CategoryCreate, db: Session = Depends(get_db)):
    return crud.create_category(db, category.name)

@app.put("/api/categories/{category_id}", response_model=schemas.CategoryResponse)
def update_category(category_id: int, category: schemas.CategoryCreate, db: Session = Depends(get_db)):
    return crud.update_category(db, category_id, category.name)

@app.delete("/api/categories/{category_id}")
def delete_category(category_id: int, db: Session = Depends(get_db)):
    crud.delete_category(db, category_id)
    return {"message": "Category deleted"}

# Articles
@app.get("/api/articles", response_model=list[schemas.ArticleResponse])
def read_articles(db: Session = Depends(get_db)):
    return crud.get_articles(db)

@app.get("/api/articles/published", response_model=list[schemas.ArticleResponse])
def read_published_articles(db: Session = Depends(get_db)):
    return crud.get_published_articles(db)

@app.get("/api/articles/{article_id}", response_model=schemas.ArticleResponse)
def read_article(article_id: int, db: Session = Depends(get_db)):
    article = crud.get_article(db, article_id)
    if not article:
        raise HTTPException(status_code=404, detail="Article not found")
    return article

@app.post("/api/articles", response_model=schemas.ArticleResponse)
def create_article(article: schemas.ArticleCreate, db: Session = Depends(get_db)):
    return crud.create_article(
        db,
        title=article.title,
        content_md=article.content_md,
        content_html=article.content_md,  # Will be rendered in real implementation
        category_id=article.category_id,
        status=article.status
    )

@app.put("/api/articles/{article_id}", response_model=schemas.ArticleResponse)
def update_article(article_id: int, article: schemas.ArticleUpdate, db: Session = Depends(get_db)):
    update_data = article.model_dump(exclude_unset=True)
    if "content_md" in update_data:
        update_data["content_html"] = update_data["content_md"]
    return crud.update_article(db, article_id, **update_data)

@app.delete("/api/articles/{article_id}")
def delete_article(article_id: int, db: Session = Depends(get_db)):
    crud.delete_article(db, article_id)
    return {"message": "Article deleted"}

# Create default admin user
@app.on_event("startup")
def create_default_admin():
    from database import SessionLocal
    db = SessionLocal()
    if not crud.get_user_by_username(db, "admin"):
        crud.create_user(db, "admin", "admin123")
    db.close()
```

**Step 2: Test backend**

Run: `cd backend && uv run uvicorn app.main:app --reload`
Expected: Server starts on http://localhost:8000

**Step 3: Commit**

```bash
git add backend/app/main.py
git commit -m "feat: implement API routes"
```

---

## Phase 2: Frontend Setup

### Task 6: Initialize React + Vite Project

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/vite.config.ts`
- Create: `frontend/index.html`
- Create: `frontend/src/main.tsx`
- Create: `frontend/src/App.tsx`

**Step 1: Create package.json**

```json
{
  "name": "cms-frontend",
  "private": true,
  "version": "0.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.21.0",
    "marked": "^11.1.0"
  },
  "devDependencies": {
    "@types/react": "^18.2.43",
    "@types/react-dom": "^18.2.17",
    "@vitejs/plugin-react": "^4.2.1",
    "typescript": "^5.2.2",
    "vite": "^5.0.8"
  }
}
```

**Step 2: Create vite.config.ts**

```typescript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8000'
    }
  }
})
```

**Step 3: Create index.html**

```html
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>CMS</title>
  </head>
  <body>
    <div id="root"></div>
    <script type="module" src="/src/main.tsx"></script>
  </body>
</html>
```

**Step 4: Create tsconfig.json**

```json
{
  "compilerOptions": {
    "target": "ES2020",
    "useDefineForClassFields": true,
    "lib": ["ES2020", "DOM", "DOM.Iterable"],
    "module": "ESNext",
    "skipLibCheck": true,
    "moduleResolution": "bundler",
    "allowImportingTsExtensions": true,
    "resolveJsonModule": true,
    "isolatedModules": true,
    "noEmit": true,
    "jsx": "react-jsx",
    "strict": true,
    "noUnusedLocals": true,
    "noUnusedParameters": true,
    "noFallthroughCasesInSwitch": true
  },
  "include": ["src"],
  "references": [{ "path": "./tsconfig.node.json" }]
}
```

**Step 5: Create tsconfig.node.json**

```json
{
  "compilerOptions": {
    "composite": true,
    "skipLibCheck": true,
    "module": "ESNext",
    "moduleResolution": "bundler",
    "allowSyntheticDefaultImports": true
  },
  "include": ["vite.config.ts"]
}
```

**Step 6: Create main.tsx**

```tsx
import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import './index.css'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
```

**Step 7: Create index.css**

```css
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  line-height: 1.6;
  color: #333;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}
```

**Step 8: Create App.tsx**

```tsx
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Home from './pages/Home'
import Article from './pages/Article'
import AdminLogin from './pages/AdminLogin'
import AdminCategories from './pages/AdminCategories'
import AdminArticles from './pages/AdminArticles'
import AdminArticleEdit from './pages/AdminArticleEdit'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/article/:id" element={<Article />} />
        <Route path="/admin" element={<AdminLogin />} />
        <Route path="/admin/categories" element={<AdminCategories />} />
        <Route path="/admin/articles" element={<AdminArticles />} />
        <Route path="/admin/articles/new" element={<AdminArticleEdit />} />
        <Route path="/admin/articles/:id/edit" element={<AdminArticleEdit />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
```

**Step 9: Install dependencies**

Run: `cd frontend && npm install`

**Step 10: Commit**

```bash
git add frontend/
git commit -m "feat: initialize React + Vite frontend"
```

---

### Task 7: Create API Client

**Files:**
- Create: `frontend/src/api/client.ts`

**Step 1: Write client.ts**

```typescript
const API_BASE = '/api'

async function request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
  })
  if (!response.ok) {
    const error = await response.json().catch(() => ({ detail: 'Request failed' }))
    throw new Error(error.detail)
  }
  return response.json()
}

export const api = {
  // Auth
  login: (username: string, password: string) =>
    request<{ access_token: string }>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    }),

  // Categories
  getCategories: () => request<Category[]>('/categories'),
  createCategory: (name: string) =>
    request<Category>('/categories', {
      method: 'POST',
      body: JSON.stringify({ name }),
    }),
  updateCategory: (id: number, name: string) =>
    request<Category>(`/categories/${id}`, {
      method: 'PUT',
      body: JSON.stringify({ name }),
    }),
  deleteCategory: (id: number) =>
    request<void>(`/categories/${id}`, { method: 'DELETE' }),

  // Articles
  getArticles: () => request<Article[]>('/articles'),
  getPublishedArticles: () => request<Article[]>('/articles/published'),
  getArticle: (id: number) => request<Article>(`/articles/${id}`),
  createArticle: (data: ArticleInput) =>
    request<Article>('/articles', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  updateArticle: (id: number, data: ArticleInput) =>
    request<Article>(`/articles/${id}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),
  deleteArticle: (id: number) =>
    request<void>(`/articles/${id}`, { method: 'DELETE' }),
}

export interface Category {
  id: number
  name: string
  created_at: string
}

export interface Article {
  id: number
  title: string
  content_md: string
  content_html: string
  category_id: number
  status: string
  created_at: string
  updated_at: string
}

export interface ArticleInput {
  title: string
  content_md: string
  category_id: number
  status: string
}
```

**Step 2: Commit**

```bash
git add frontend/src/api/client.ts
git commit -m "feat: add API client"
```

---

### Task 8: Create Frontend Pages

**Files:**
- Create: `frontend/src/pages/Home.tsx`
- Create: `frontend/src/pages/Article.tsx`
- Create: `frontend/src/pages/AdminLogin.tsx`
- Create: `frontend/src/pages/AdminCategories.tsx`
- Create: `frontend/src/pages/AdminArticles.tsx`
- Create: `frontend/src/pages/AdminArticleEdit.tsx`

**Step 1: Write Home.tsx**

```tsx
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api, Article } from '../api/client'

export default function Home() {
  const [articles, setArticles] = useState<Article[]>([])

  useEffect(() => {
    api.getPublishedArticles().then(setArticles).catch(console.error)
  }, [])

  return (
    <div className="container">
      <header style={{ padding: '20px 0', borderBottom: '1px solid #eee' }}>
        <h1><Link to="/">CMS</Link></h1>
      </header>
      <main style={{ padding: '20px 0' }}>
        <h2>Articles</h2>
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {articles.map((article) => (
            <li key={article.id} style={{ padding: '15px 0', borderBottom: '1px solid #eee' }}>
              <Link to={`/article/${article.id}`} style={{ fontSize: '18px', fontWeight: 'bold' }}>
                {article.title}
              </Link>
              <p style={{ color: '#666', fontSize: '14px', marginTop: '5px' }}>
                {new Date(article.created_at).toLocaleDateString()}
              </p>
            </li>
          ))}
        </ul>
      </main>
    </div>
  )
}
```

**Step 2: Write Article.tsx**

```tsx
import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { api, Article } from '../api/client'
import { marked } from 'marked'

export default function Article() {
  const { id } = useParams<{ id: string }>()
  const [article, setArticle] = useState<Article | null>(null)

  useEffect(() => {
    if (id) {
      api.getArticle(parseInt(id)).then(setArticle).catch(console.error)
    }
  }, [id])

  if (!article) return <div className="container">Loading...</div>

  return (
    <div className="container">
      <header style={{ padding: '20px 0', borderBottom: '1px solid #eee' }}>
        <Link to="/">← Back</Link>
      </header>
      <main style={{ padding: '20px 0' }}>
        <h1>{article.title}</h1>
        <p style={{ color: '#666', fontSize: '14px' }}>
          {new Date(article.created_at).toLocaleDateString()}
        </p>
        <article 
          style={{ marginTop: '20px' }}
          dangerouslySetInnerHTML={{ __html: article.content_html }}
        />
      </main>
    </div>
  )
}
```

**Step 3: Write AdminLogin.tsx**

```tsx
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api/client'

export default function AdminLogin() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const { access_token } = await api.login(username, password)
      localStorage.setItem('token', access_token)
      navigate('/admin/articles')
    } catch (err) {
      setError('Invalid credentials')
    }
  }

  return (
    <div className="container">
      <main style={{ maxWidth: '400px', margin: '50px auto' }}>
        <h1>Admin Login</h1>
        {error && <p style={{ color: 'red' }}>{error}</p>}
        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '15px' }}>
            <label>Username</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
            />
          </div>
          <div style={{ marginBottom: '15px' }}>
            <label>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
            />
          </div>
          <button type="submit" style={{ padding: '10px 20px' }}>Login</button>
        </form>
      </main>
    </div>
  )
}
```

**Step 4: Write AdminCategories.tsx**

```tsx
import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api, Category } from '../api/client'

export default function AdminCategories() {
  const [categories, setCategories] = useState<Category[]>([])
  const [newName, setNewName] = useState('')
  const navigate = useNavigate()

  const loadCategories = () => {
    api.getCategories().then(setCategories).catch(console.error)
  }

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (!token) {
      navigate('/admin')
      return
    }
    loadCategories()
  }, [navigate])

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await api.createCategory(newName)
      setNewName('')
      loadCategories()
    } catch (err) {
      alert('Failed to create category')
    }
  }

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this category?')) return
    try {
      await api.deleteCategory(id)
      loadCategories()
    } catch (err) {
      alert('Failed to delete category')
    }
  }

  return (
    <div className="container">
      <header style={{ padding: '20px 0', borderBottom: '1px solid #eee' }}>
        <Link to="/admin/articles">← Back to Articles</Link>
      </header>
      <main style={{ padding: '20px 0' }}>
        <h1>Categories</h1>
        <form onSubmit={handleCreate} style={{ margin: '20px 0' }}>
          <input
            type="text"
            value={newName}
            onChange={(e) => setNewName(e.target.value)}
            placeholder="Category name"
            style={{ padding: '8px', marginRight: '10px' }}
          />
          <button type="submit" style={{ padding: '8px 16px' }}>Add</button>
        </form>
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {categories.map((cat) => (
            <li key={cat.id} style={{ padding: '10px', borderBottom: '1px solid #eee', display: 'flex', justifyContent: 'space-between' }}>
              <span>{cat.name}</span>
              <button onClick={() => handleDelete(cat.id)} style={{ color: 'red' }}>Delete</button>
            </li>
          ))}
        </ul>
      </main>
    </div>
  )
}
```

**Step 5: Write AdminArticles.tsx**

```tsx
import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api, Article } from '../api/client'

export default function AdminArticles() {
  const [articles, setArticles] = useState<Article[]>([])
  const navigate = useNavigate()

  const loadArticles = () => {
    api.getArticles().then(setArticles).catch(console.error)
  }

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (!token) {
      navigate('/admin')
      return
    }
    loadArticles()
  }, [navigate])

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this article?')) return
    try {
      await api.deleteArticle(id)
      loadArticles()
    } catch (err) {
      alert('Failed to delete article')
    }
  }

  const toggleStatus = async (article: Article) => {
    const newStatus = article.status === 'published' ? 'draft' : 'published'
    try {
      await api.updateArticle(article.id, { ...article, status: newStatus })
      loadArticles()
    } catch (err) {
      alert('Failed to update status')
    }
  }

  return (
    <div className="container">
      <header style={{ padding: '20px 0', borderBottom: '1px solid #eee', display: 'flex', justifyContent: 'space-between' }}>
        <Link to="/">← Back</Link>
        <nav>
          <Link to="/admin/categories" style={{ marginRight: '20px' }}>Categories</Link>
          <Link to="/admin/articles/new">New Article</Link>
        </nav>
      </header>
      <main style={{ padding: '20px 0' }}>
        <h1>Articles</h1>
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {articles.map((article) => (
            <li key={article.id} style={{ padding: '15px', borderBottom: '1px solid #eee' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <Link to={`/admin/articles/${article.id}/edit`} style={{ fontSize: '18px', fontWeight: 'bold' }}>
                    {article.title}
                  </Link>
                  <p style={{ color: '#666', fontSize: '14px' }}>
                    {article.status} | {new Date(article.created_at).toLocaleDateString()}
                  </p>
                </div>
                <div>
                  <button onClick={() => toggleStatus(article)} style={{ marginRight: '10px' }}>
                    {article.status === 'published' ? 'Unpublish' : 'Publish'}
                  </button>
                  <button onClick={() => handleDelete(article.id)} style={{ color: 'red' }}>Delete</button>
                </div>
              </div>
            </li>
          ))}
        </ul>
      </main>
    </div>
  )
}
```

**Step 6: Write AdminArticleEdit.tsx**

```tsx
import { useEffect, useState } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { api, Article, ArticleInput, Category } from '../api/client'
import { marked } from 'marked'

export default function AdminArticleEdit() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [title, setTitle] = useState('')
  const [contentMd, setContentMd] = useState('')
  const [categoryId, setCategoryId] = useState(0)
  const [status, setStatus] = useState('draft')
  const [categories, setCategories] = useState<Category[]>([])
  const isEdit = !!id

  useEffect(() => {
    const token = localStorage.getItem('token')
    if (!token) {
      navigate('/admin')
      return
    }
    api.getCategories().then(setCategories).catch(console.error)
    
    if (id) {
      api.getArticle(parseInt(id)).then((article) => {
        setTitle(article.title)
        setContentMd(article.content_md)
        setCategoryId(article.category_id)
        setStatus(article.status)
      }).catch(console.error)
    }
  }, [id, navigate])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    const contentHtml = marked(contentMd) as string
    const data: ArticleInput = {
      title,
      content_md: contentMd,
      content_html: contentHtml,
      category_id: categoryId,
      status,
    }
    
    try {
      if (isEdit) {
        await api.updateArticle(parseInt(id!), data)
      } else {
        await api.createArticle(data)
      }
      navigate('/admin/articles')
    } catch (err) {
      alert('Failed to save article')
    }
  }

  return (
    <div className="container">
      <header style={{ padding: '20px 0', borderBottom: '1px solid #eee' }}>
        <Link to="/admin/articles">← Back</Link>
      </header>
      <main style={{ padding: '20px 0' }}>
        <h1>{isEdit ? 'Edit Article' : 'New Article'}</h1>
        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '15px' }}>
            <label>Title</label>
            <input
              type="text"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
              required
            />
          </div>
          <div style={{ marginBottom: '15px' }}>
            <label>Category</label>
            <select
              value={categoryId}
              onChange={(e) => setCategoryId(parseInt(e.target.value))}
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
              required
            >
              <option value={0}>Select category</option>
              {categories.map((cat) => (
                <option key={cat.id} value={cat.id}>{cat.name}</option>
              ))}
            </select>
          </div>
          <div style={{ marginBottom: '15px' }}>
            <label>Status</label>
            <select
              value={status}
              onChange={(e) => setStatus(e.target.value)}
              style={{ width: '100%', padding: '8px', marginTop: '5px' }}
            >
              <option value="draft">Draft</option>
              <option value="published">Published</option>
            </select>
          </div>
          <div style={{ marginBottom: '15px' }}>
            <label>Content (Markdown)</label>
            <textarea
              value={contentMd}
              onChange={(e) => setContentMd(e.target.value)}
              style={{ width: '100%', height: '300px', padding: '8px', marginTop: '5px', fontFamily: 'monospace' }}
              required
            />
          </div>
          <button type="submit" style={{ padding: '10px 20px' }}>Save</button>
        </form>
      </main>
    </div>
  )
}
```

**Step 7: Test frontend**

Run: `cd frontend && npm run dev`
Expected: Dev server on http://localhost:5173

**Step 8: Commit**

```bash
git add frontend/src/pages/
git commit -m "feat: add frontend pages"
```

---

## Phase 3: Integration & MD Rendering

### Task 9: Add MD Rendering in Backend

**Files:**
- Modify: `backend/app/main.py`

**Step 1: Update main.py to use marked**

Add import and update article creation/update to render MD to HTML

**Step 2: Commit**

```bash
git add backend/app/main.py
git commit -m "feat: add markdown rendering in backend"
```

---

### Task 10: Final Integration Test

**Step 1: Start backend**

Run: `cd backend && uv run uvicorn app.main:app --reload`

**Step 2: Start frontend**

Run: `cd frontend && npm run dev`

**Step 3: Test flow**
1. Visit http://localhost:5173/admin
2. Login with admin/admin123
3. Create a category
4. Create an article with MD content
5. Publish it
6. Visit http://localhost:5173/ to see the article

**Step 4: Commit**

```bash
git add .
git commit -m "feat: complete CMS system with MD rendering"
```

---

**Plan complete and saved to `docs/plans/2026-02-27-cms-implementation.md`**

Two execution options:

1. **Subagent-Driven (this session)** - I dispatch fresh subagent per task, review between tasks, fast iteration

2. **Parallel Session (separate)** - Open new session with executing-plans, batch execution with checkpoints

Which approach?