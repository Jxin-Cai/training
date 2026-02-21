# CMS System Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a CMS system with admin panel and public frontend, featuring category management, Markdown content creation with auto-rendering, and content publishing workflow.

**Architecture:** Python FastAPI backend with SQLite database, React + TypeScript frontend with Vite. JWT authentication for admin routes. Public API for content display.

**Tech Stack:** FastAPI, SQLAlchemy, SQLite, React, TypeScript, Vite, marked, highlight.js, JWT

---

## Phase 1: Backend Setup

### Task 1: Initialize Python Project

**Files:**
- Create: `backend/requirements.txt`
- Create: `backend/app/__init__.py`
- Create: `backend/app/main.py`

**Step 1: Create project structure**

```bash
mkdir -p backend/app
```

**Step 2: Create requirements.txt**

```
fastapi==0.109.0
uvicorn==0.27.0
sqlalchemy==2.0.25
pydantic==2.5.3
python-jose[cryptography]==3.3.0
passlib[bcrypt]==1.7.4
python-multipart==0.0.6
marked==12.0.0
pytest==7.4.4
httpx==0.26.0
```

**Step 3: Create main.py**

```python
from fastapi import FastAPI

app = FastAPI(title="CMS API")

@app.get("/")
def root():
    return {"message": "CMS API"}
```

**Step 4: Run and verify**

```bash
cd backend && pip install -r requirements.txt && uvicorn app.main:app --reload
```

Expected: Server starts on port 8000

---

### Task 2: Database Models

**Files:**
- Create: `backend/app/models.py`
- Modify: `backend/app/main.py`

**Step 1: Create models.py**

```python
from sqlalchemy import Column, Integer, String, Text, DateTime, ForeignKey
from sqlalchemy.orm import declarative_base, relationship
from datetime import datetime

Base = declarative_base()

class User(Base):
    __tablename__ = "users"
    id = Column(Integer, primary_key=True, index=True)
    username = Column(String(50), unique=True, nullable=False)
    password_hash = Column(String(255), nullable=False)
    role = Column(String(20), default="editor")
    created_at = Column(DateTime, default=datetime.utcnow)
    contents = relationship("Content", back_populates="author")

class Category(Base):
    __tablename__ = "categories"
    id = Column(Integer, primary_key=True, index=True)
    name = Column(String(100), nullable=False)
    slug = Column(String(100), unique=True, nullable=False)
    sort_order = Column(Integer, default=0)
    created_at = Column(DateTime, default=datetime.utcnow)
    contents = relationship("Content", back_populates="category")

class Content(Base):
    __tablename__ = "contents"
    id = Column(Integer, primary_key=True, index=True)
    title = Column(String(200), nullable=False)
    slug = Column(String(200), unique=True, nullable=False)
    category_id = Column(Integer, ForeignKey("categories.id"))
    markdown_content = Column(Text)
    html_content = Column(Text)
    status = Column(String(20), default="draft")
    author_id = Column(Integer, ForeignKey("users.id"))
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    published_at = Column(DateTime, nullable=True)
    category = relationship("Category", back_populates="contents")
    author = relationship("User", back_populates="contents")
```

**Step 2: Update main.py**

```python
from fastapi import FastAPI
from app.models import Base
from app.database import engine

Base.metadata.create_all(bind=engine)

app = FastAPI(title="CMS API")

@app.get("/")
def root():
    return {"message": "CMS API"}
```

**Step 3: Create database.py**

```python
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

DATABASE_URL = "sqlite:///./cms.db"
engine = create_engine(DATABASE_URL, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
```

---

### Task 3: Authentication System

**Files:**
- Create: `backend/app/auth.py`
- Create: `backend/app/schemas.py`
- Modify: `backend/app/main.py`

**Step 1: Create schemas.py**

```python
from pydantic import BaseModel
from typing import Optional
from datetime import datetime

class UserCreate(BaseModel):
    username: str
    password: str

class UserResponse(BaseModel):
    id: int
    username: str
    role: str
    class Config:
        from_attributes = True

class Token(BaseModel):
    access_token: str
    token_type: str

class CategoryCreate(BaseModel):
    name: str
    slug: str
    sort_order: int = 0

class CategoryResponse(BaseModel):
    id: int
    name: str
    slug: str
    sort_order: int
    class Config:
        from_attributes = True

class ContentCreate(BaseModel):
    title: str
    slug: str
    category_id: int
    markdown_content: str

class ContentUpdate(BaseModel):
    title: Optional[str] = None
    slug: Optional[str] = None
    category_id: Optional[int] = None
    markdown_content: Optional[str] = None

class ContentResponse(BaseModel):
    id: int
    title: str
    slug: str
    category_id: Optional[int]
    markdown_content: Optional[str]
    html_content: Optional[str]
    status: str
    author_id: Optional[int]
    created_at: datetime
    updated_at: datetime
    published_at: Optional[datetime]
    class Config:
        from_attributes = True
```

**Step 2: Create auth.py**

```python
from datetime import datetime, timedelta
from typing import Optional
from jose import JWTError, jwt
from passlib.context import CryptContext
from fastapi import Depends, HTTPException, status
from fastapi.security import OAuth2PasswordBearer
from sqlalchemy.orm import Session
from app.database import get_db
from app.models import User

SECRET_KEY = "cms-secret-key-change-in-production"
ALGORITHM = "HS256"

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/api/auth/login")

def verify_password(plain_password: str, hashed_password: str) -> bool:
    return pwd_context.verify(plain_password, hashed_password)

def get_password_hash(password: str) -> str:
    return pwd_context.hash(password)

def create_access_token(data: dict, expires_delta: Optional[timedelta] = None):
    to_encode = data.copy()
    expire = datetime.utcnow() + (expires_delta or timedelta(hours=24))
    to_encode.update({"exp": expire})
    return jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)

def get_current_user(token: str = Depends(oauth2_scheme), db: Session = Depends(get_db)):
    credentials_exception = HTTPException(status_code=401, detail="Invalid credentials")
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        username: str = payload.get("sub")
        if username is None:
            raise credentials_exception
    except JWTError:
        raise credentials_exception
    user = db.query(User).filter(User.username == username).first()
    if user is None:
        raise credentials_exception
    return user
```

**Step 3: Add auth routes to main.py**

```python
from fastapi import APIRouter, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.orm import Session
from app.database import get_db
from app.models import User
from app.auth import verify_password, get_password_hash, create_access_token, get_current_user
from app.schemas import UserCreate, UserResponse, Token

router = APIRouter()

@router.post("/api/auth/register", response_model=UserResponse)
def register(user: UserCreate, db: Session = Depends(get_db)):
    existing = db.query(User).filter(User.username == user.username).first()
    if existing:
        raise HTTPException(status_code=400, detail="Username already exists")
    hashed = get_password_hash(user.password)
    db_user = User(username=user.username, password_hash=hashed, role="admin")
    db.add(db_user)
    db.commit()
    db.refresh(db_user)
    return db_user

@router.post("/api/auth/login", response_model=Token)
def login(form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)):
    user = db.query(User).filter(User.username == form_data.username).first()
    if not user or not verify_password(form_data.password, user.password_hash):
        raise HTTPException(status_code=401, detail="Invalid credentials")
    token = create_access_token(data={"sub": user.username})
    return {"access_token": token, "token_type": "bearer"}

@router.get("/api/auth/me", response_model=UserResponse)
def me(current_user: User = Depends(get_current_user)):
    return current_user
```

---

### Task 4: Category CRUD API

**Files:**
- Modify: `backend/app/main.py`

**Step 1: Add category routes**

```python
@router.get("/api/admin/categories", response_model=list[CategoryResponse])
def list_categories(db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    return db.query(Category).order_by(Category.sort_order).all()

@router.post("/api/admin/categories", response_model=CategoryResponse)
def create_category(category: CategoryCreate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    db_category = Category(**category.model_dump())
    db.add(db_category)
    db.commit()
    db.refresh(db_category)
    return db_category

@router.put("/api/admin/categories/{category_id}", response_model=CategoryResponse)
def update_category(category_id: int, category: CategoryCreate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    db_category = db.query(Category).filter(Category.id == category_id).first()
    if not db_category:
        raise HTTPException(status_code=404, detail="Category not found")
    db_category.name = category.name
    db_category.slug = category.slug
    db_category.sort_order = category.sort_order
    db.commit()
    db.refresh(db_category)
    return db_category

@router.delete("/api/admin/categories/{category_id}")
def delete_category(category_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    db_category = db.query(Category).filter(Category.id == category_id).first()
    if not db_category:
        raise HTTPException(status_code=404, detail="Category not found")
    db.delete(db_category)
    db.commit()
    return {"message": "Deleted"}
```

**Step 2: Add Category import**

```python
from app.models import User, Category, Content
```

---

### Task 5: Content CRUD API with MD Rendering

**Files:**
- Modify: `backend/app/main.py`

**Step 1: Add markdown rendering utility**

```python
from marked import marked

def render_markdown(md_content: str) -> str:
    return marked(md_content)
```

**Step 2: Add content routes**

```python
@router.get("/api/admin/contents", response_model=list[ContentResponse])
def list_contents(status: Optional[str] = None, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    query = db.query(Content)
    if status:
        query = query.filter(Content.status == status)
    return query.order_by(Content.created_at.desc()).all()

@router.post("/api/admin/contents", response_model=ContentResponse)
def create_content(content: ContentCreate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    html = render_markdown(content.markdown_content)
    db_content = Content(
        **content.model_dump(),
        html_content=html,
        author_id=current_user.id
    )
    db.add(db_content)
    db.commit()
    db.refresh(db_content)
    return db_content

@router.put("/api/admin/contents/{content_id}", response_model=ContentResponse)
def update_content(content_id: int, content: ContentUpdate, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    db_content = db.query(Content).filter(Content.id == content_id).first()
    if not db_content:
        raise HTTPException(status_code=404, detail="Content not found")
    
    if content.title:
        db_content.title = content.title
    if content.slug:
        db_content.slug = content.slug
    if content.category_id:
        db_content.category_id = content.category_id
    if content.markdown_content:
        db_content.markdown_content = content.markdown_content
        db_content.html_content = render_markdown(content.markdown_content)
    
    db.commit()
    db.refresh(db_content)
    return db_content

@router.delete("/api/admin/contents/{content_id}")
def delete_content(content_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    db_content = db.query(Content).filter(Content.id == content_id).first()
    if not db_content:
        raise HTTPException(status_code=404, detail="Content not found")
    db.delete(db_content)
    db.commit()
    return {"message": "Deleted"}

@router.post("/api/admin/contents/{content_id}/publish")
def publish_content(content_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    db_content = db.query(Content).filter(Content.id == content_id).first()
    if not db_content:
        raise HTTPException(status_code=404, detail="Content not found")
    db_content.status = "published"
    db_content.published_at = datetime.utcnow()
    db.commit()
    return {"message": "Published"}

@router.post("/api/admin/contents/{content_id}/unpublish")
def unpublish_content(content_id: int, db: Session = Depends(get_db), current_user: User = Depends(get_current_user)):
    db_content = db.query(Content).filter(Content.id == content_id).first()
    if not db_content:
        raise HTTPException(status_code=404, detail="Content not found")
    db_content.status = "draft"
    db.commit()
    return {"message": "Unpublished"}
```

---

### Task 6: Public API

**Files:**
- Modify: `backend/app/main.py`

**Step 1: Add public routes**

```python
@router.get("/api/contents", response_model=list[ContentResponse])
def list_published_contents(db: Session = Depends(get_db)):
    return db.query(Content).filter(Content.status == "published").order_by(Content.published_at.desc()).all()

@router.get("/api/contents/{slug}", response_model=ContentResponse)
def get_content_by_slug(slug: str, db: Session = Depends(get_db)):
    content = db.query(Content).filter(Content.slug == slug, Content.status == "published").first()
    if not content:
        raise HTTPException(status_code=404, detail="Content not found")
    return content

@router.get("/api/categories", response_model=list[CategoryResponse])
def list_public_categories(db: Session = Depends(get_db)):
    return db.query(Category).order_by(Category.sort_order).all()
```

---

## Phase 2: Frontend Setup

### Task 7: Initialize React Project

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/vite.config.ts`
- Create: `frontend/tsconfig.json`
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
    "react-router-dom": "^6.22.0",
    "react-markdown": "^9.0.1",
    "highlight.js": "^11.9.0"
  },
  "devDependencies": {
    "@types/react": "^18.2.55",
    "@types/react-dom": "^18.2.19",
    "@vitejs/plugin-react": "^4.2.1",
    "typescript": "^5.2.2",
    "vite": "^5.1.0"
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

**Step 3: Create tsconfig.json**

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

**Step 4: Create tsconfig.node.json**

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

**Step 5: Create index.html**

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

**Step 6: Create src/main.tsx**

```typescript
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

**Step 7: Create src/index.css**

```css
* { box-sizing: border-box; margin: 0; padding: 0; }
body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }
.container { max-width: 1200px; margin: 0 auto; padding: 20px; }
.btn { padding: 8px 16px; border: none; border-radius: 4px; cursor: pointer; }
.btn-primary { background: #007bff; color: white; }
.btn-danger { background: #dc3545; color: white; }
input, textarea, select { padding: 8px; border: 1px solid #ddd; border-radius: 4px; width: 100%; }
table { width: 100%; border-collapse: collapse; }
th, td { padding: 12px; border: 1px solid #ddd; text-align: left; }
```

**Step 8: Create src/App.tsx**

```typescript
import { BrowserRouter, Routes, Route } from 'react-router-dom'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<div>Home</div>} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
```

---

### Task 8: API Client

**Files:**
- Create: `frontend/src/api.ts`

**Step 1: Create api.ts**

```typescript
const API_BASE = '/api'

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  const token = localStorage.getItem('token')
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
    ...options?.headers,
  }
  const response = await fetch(`${API_BASE}${path}`, { ...options, headers })
  if (!response.ok) {
    const error = await response.json().catch(() => ({ detail: 'Request failed' }))
    throw new Error(error.detail)
  }
  return response.json()
}

export const api = {
  login: (username: string, password: string) =>
    request<{ access_token: string }>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    }),
  
  register: (username: string, password: string) =>
    request('/auth/register', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    }),
  
  getCurrentUser: () => request('/auth/me'),
  
  // Categories
  getCategories: () => request('/admin/categories'),
  createCategory: (data: { name: string; slug: string; sort_order: number }) =>
    request('/admin/categories', { method: 'POST', body: JSON.stringify(data) }),
  updateCategory: (id: number, data: { name: string; slug: string; sort_order: number }) =>
    request(`/admin/categories/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  deleteCategory: (id: number) =>
    request(`/admin/categories/${id}`, { method: 'DELETE' }),
  
  // Contents
  getContents: (status?: string) => request(`/admin/contents${status ? `?status=${status}` : ''}`),
  getContent: (id: number) => request(`/admin/contents/${id}`),
  createContent: (data: { title: string; slug: string; category_id: number; markdown_content: string }) =>
    request('/admin/contents', { method: 'POST', body: JSON.stringify(data) }),
  updateContent: (id: number, data: Partial<{ title: string; slug: string; category_id: number; markdown_content: string }>) =>
    request(`/admin/contents/${id}`, { method: 'PUT', body: JSON.stringify(data) }),
  deleteContent: (id: number) => request(`/admin/contents/${id}`, { method: 'DELETE' }),
  publishContent: (id: number) => request(`/admin/contents/${id}/publish`, { method: 'POST' }),
  unpublishContent: (id: number) => request(`/admin/contents/${id}/unpublish`, { method: 'POST' }),
  
  // Public
  getPublishedContents: () => request('/contents'),
  getContentBySlug: (slug: string) => request(`/contents/${slug}`),
  getPublicCategories: () => request('/categories'),
}
```

---

### Task 9: Frontend Pages - Public

**Files:**
- Create: `frontend/src/pages/Home.tsx`
- Create: `frontend/src/pages/ContentDetail.tsx`
- Modify: `frontend/src/App.tsx`

**Step 1: Create Home.tsx**

```typescript
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api'

interface Content {
  id: number
  title: string
  slug: string
  status: string
  published_at: string
}

export function Home() {
  const [contents, setContents] = useState<Content[]>([])

  useEffect(() => {
    api.getPublishedContents().then(setContents).catch(console.error)
  }, [])

  return (
    <div className="container">
      <h1>Content List</h1>
      <div style={{ marginTop: 20 }}>
        {contents.map(content => (
          <div key={content.id} style={{ padding: 20, border: '1px solid #ddd', marginBottom: 10 }}>
            <h2><Link to={`/content/${content.slug}`}>{content.title}</Link></h2>
            <p style={{ color: '#666' }}>
              {content.published_at ? new Date(content.published_at).toLocaleDateString() : ''}
            </p>
          </div>
        ))}
      </div>
    </div>
  )
}
```

**Step 2: Create ContentDetail.tsx**

```typescript
import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import ReactMarkdown from 'react-markdown'
import { api } from '../api'

interface Content {
  id: number
  title: string
  html_content: string
  created_at: string
}

export function ContentDetail() {
  const { slug } = useParams<{ slug: string }>()
  const [content, setContent] = useState<Content | null>(null)

  useEffect(() => {
    if (slug) {
      api.getContentBySlug(slug).then(setContent).catch(console.error)
    }
  }, [slug])

  if (!content) return <div className="container">Loading...</div>

  return (
    <div className="container">
      <Link to="/">← Back</Link>
      <h1>{content.title}</h1>
      <p style={{ color: '#666' }}>{new Date(content.created_at).toLocaleDateString()}</p>
      <div 
        style={{ marginTop: 20 }}
        dangerouslySetInnerHTML={{ __html: content.html_content || '' }}
      />
    </div>
  )
}
```

**Step 3: Update App.tsx**

```typescript
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import { Home } from './pages/Home'
import { ContentDetail } from './pages/ContentDetail'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/content/:slug" element={<ContentDetail />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
```

---

### Task 10: Backend Pages - Login

**Files:**
- Create: `frontend/src/pages/Login.tsx`
- Create: `frontend/src/pages/Register.tsx`
- Modify: `frontend/src/App.tsx`

**Step 1: Create Login.tsx**

```typescript
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api'

export function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      const { access_token } = await api.login(username, password)
      localStorage.setItem('token', access_token)
      navigate('/admin')
    } catch (err) {
      alert('Login failed')
    }
  }

  return (
    <div className="container" style={{ maxWidth: 400, marginTop: 100 }}>
      <h1>Login</h1>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: 10 }}>
          <input 
            placeholder="Username" 
            value={username}
            onChange={e => setUsername(e.target.value)}
          />
        </div>
        <div style={{ marginBottom: 10 }}>
          <input 
            type="password" 
            placeholder="Password" 
            value={password}
            onChange={e => setPassword(e.target.value)}
          />
        </div>
        <button className="btn btn-primary" type="submit">Login</button>
      </form>
    </div>
  )
}
```

**Step 2: Create Register.tsx**

```typescript
import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api'

export function Register() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const navigate = useNavigate()

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    try {
      await api.register(username, password)
      const { access_token } = await api.login(username, password)
      localStorage.setItem('token', access_token)
      navigate('/admin')
    } catch (err) {
      alert('Registration failed')
    }
  }

  return (
    <div className="container" style={{ maxWidth: 400, marginTop: 100 }}>
      <h1>Register</h1>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: 10 }}>
          <input 
            placeholder="Username" 
            value={username}
            onChange={e => setUsername(e.target.value)}
          />
        </div>
        <div style={{ marginBottom: 10 }}>
          <input 
            type="password" 
            placeholder="Password" 
            value={password}
            onChange={e => setPassword(e.target.value)}
          />
        </div>
        <button className="btn btn-primary" type="submit">Register</button>
      </form>
    </div>
  )
}
```

**Step 3: Update App.tsx**

```typescript
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Home } from './pages/Home'
import { ContentDetail } from './pages/ContentDetail'
import { Login } from './pages/Login'
import { Register } from './pages/Register'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/content/:slug" element={<ContentDetail />} />
        <Route path="/admin/login" element={<Login />} />
        <Route path="/admin/register" element={<Register />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
```

---

### Task 11: Backend Pages - Categories

**Files:**
- Create: `frontend/src/pages/admin/CategoryList.tsx`
- Create: `frontend/src/pages/admin/CategoryForm.tsx`
- Modify: `frontend/src/App.tsx`

**Step 1: Create CategoryList.tsx**

```typescript
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../../api'

interface Category {
  id: number
  name: string
  slug: string
  sort_order: number
}

export function CategoryList() {
  const [categories, setCategories] = useState<Category[]>([])

  useEffect(() => {
    loadCategories()
  }, [])

  const loadCategories = () => {
    api.getCategories().then(setCategories).catch(console.error)
  }

  const handleDelete = async (id: number) => {
    if (confirm('Delete this category?')) {
      await api.deleteCategory(id)
      loadCategories()
    }
  }

  return (
    <div className="container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1>Categories</h1>
        <Link to="/admin/categories/new" className="btn btn-primary">Add Category</Link>
      </div>
      <table style={{ marginTop: 20 }}>
        <thead>
          <tr>
            <th>Name</th>
            <th>Slug</th>
            <th>Sort Order</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {categories.map(cat => (
            <tr key={cat.id}>
              <td>{cat.name}</td>
              <td>{cat.slug}</td>
              <td>{cat.sort_order}</td>
              <td>
                <Link to={`/admin/categories/${cat.id}/edit`} className="btn" style={{ marginRight: 10 }}>Edit</Link>
                <button className="btn btn-danger" onClick={() => handleDelete(cat.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
```

**Step 2: Create CategoryForm.tsx**

```typescript
import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { api } from '../../api'

export function CategoryForm() {
  const { id } = useParams<{ id: string }>()
  const [name, setName] = useState('')
  const [slug, setSlug] = useState('')
  const [sortOrder, setSortOrder] = useState(0)
  const navigate = useNavigate()

  useEffect(() => {
    if (id && id !== 'new') {
      api.getCategories().then(cats => {
        const cat = cats.find((c: any) => c.id === parseInt(id))
        if (cat) {
          setName(cat.name)
          setSlug(cat.slug)
          setSortOrder(cat.sort_order)
        }
      })
    }
  }, [id])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    const data = { name, slug, sort_order: sortOrder }
    if (id && id !== 'new') {
      await api.updateCategory(parseInt(id), data)
    } else {
      await api.createCategory(data)
    }
    navigate('/admin/categories')
  }

  return (
    <div className="container" style={{ maxWidth: 600 }}>
      <h1>{id && id !== 'new' ? 'Edit' : 'New'} Category</h1>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: 10 }}>
          <label>Name</label>
          <input value={name} onChange={e => setName(e.target.value)} required />
        </div>
        <div style={{ marginBottom: 10 }}>
          <label>Slug</label>
          <input value={slug} onChange={e => setSlug(e.target.value)} required />
        </div>
        <div style={{ marginBottom: 10 }}>
          <label>Sort Order</label>
          <input type="number" value={sortOrder} onChange={e => setSortOrder(parseInt(e.target.value))} />
        </div>
        <button className="btn btn-primary" type="submit">Save</button>
      </form>
    </div>
  )
}
```

**Step 3: Update App.tsx**

```typescript
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Home } from './pages/Home'
import { ContentDetail } from './pages/ContentDetail'
import { Login } from './pages/Login'
import { Register } from './pages/Register'
import { CategoryList } from './pages/admin/CategoryList'
import { CategoryForm } from './pages/admin/CategoryForm'

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const token = localStorage.getItem('token')
  return token ? <>{children}</> : <Navigate to="/admin/login" />
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/content/:slug" element={<ContentDetail />} />
        <Route path="/admin/login" element={<Login />} />
        <Route path="/admin/register" element={<Register />} />
        <Route path="/admin/categories" element={<PrivateRoute><CategoryList /></PrivateRoute>} />
        <Route path="/admin/categories/:id" element={<PrivateRoute><CategoryForm /></PrivateRoute>} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
```

---

### Task 12: Backend Pages - Contents

**Files:**
- Create: `frontend/src/pages/admin/ContentList.tsx`
- Create: `frontend/src/pages/admin/ContentForm.tsx`
- Modify: `frontend/src/App.tsx`

**Step 1: Create ContentList.tsx**

```typescript
import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../../api'

interface Content {
  id: number
  title: string
  slug: string
  status: string
}

export function ContentList() {
  const [contents, setContents] = useState<Content[]>([])

  useEffect(() => {
    loadContents()
  }, [])

  const loadContents = () => {
    api.getContents().then(setContents).catch(console.error)
  }

  const handleDelete = async (id: number) => {
    if (confirm('Delete this content?')) {
      await api.deleteContent(id)
      loadContents()
    }
  }

  const handlePublish = async (id: number) => {
    await api.publishContent(id)
    loadContents()
  }

  const handleUnpublish = async (id: number) => {
    await api.unpublishContent(id)
    loadContents()
  }

  return (
    <div className="container">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1>Contents</h1>
        <Link to="/admin/contents/new" className="btn btn-primary">Add Content</Link>
      </div>
      <table style={{ marginTop: 20 }}>
        <thead>
          <tr>
            <th>Title</th>
            <th>Slug</th>
            <th>Status</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {contents.map(content => (
            <tr key={content.id}>
              <td>{content.title}</td>
              <td>{content.slug}</td>
              <td>
                <span style={{ 
                  padding: '4px 8px', 
                  borderRadius: 4,
                  background: content.status === 'published' ? '#28a745' : '#6c757d',
                  color: 'white'
                }}>
                  {content.status}
                </span>
              </td>
              <td>
                <Link to={`/admin/contents/${content.id}/edit`} className="btn" style={{ marginRight: 10 }}>Edit</Link>
                {content.status === 'draft' ? (
                  <button className="btn btn-primary" onClick={() => handlePublish(content.id)} style={{ marginRight: 10 }}>Publish</button>
                ) : (
                  <button className="btn" onClick={() => handleUnpublish(content.id)} style={{ marginRight: 10 }}>Unpublish</button>
                )}
                <button className="btn btn-danger" onClick={() => handleDelete(content.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
```

**Step 2: Create ContentForm.tsx**

```typescript
import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import ReactMarkdown from 'react-markdown'
import { api } from '../../api'

export function ContentForm() {
  const { id } = useParams<{ id: string }>()
  const [title, setTitle] = useState('')
  const [slug, setSlug] = useState('')
  const [categoryId, setCategoryId] = useState(0)
  const [markdown, setMarkdown] = useState('')
  const [categories, setCategories] = useState<any[]>([])
  const [preview, setPreview] = useState(false)
  const navigate = useNavigate()

  useEffect(() => {
    api.getCategories().then(setCategories).catch(console.error)
  }, [])

  useEffect(() => {
    if (id && id !== 'new') {
      api.getContents().then(contents => {
        const content = contents.find((c: any) => c.id === parseInt(id))
        if (content) {
          setTitle(content.title)
          setSlug(content.slug)
          setCategoryId(content.category_id || 0)
          setMarkdown(content.markdown_content || '')
        }
      })
    }
  }, [id])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    const data = { title, slug, category_id: categoryId, markdown_content: markdown }
    if (id && id !== 'new') {
      await api.updateContent(parseInt(id), data)
    } else {
      await api.createContent(data)
    }
    navigate('/admin/contents')
  }

  return (
    <div className="container" style={{ maxWidth: 1200 }}>
      <h1>{id && id !== 'new' ? 'Edit' : 'New'} Content</h1>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: 10 }}>
          <label>Title</label>
          <input value={title} onChange={e => setTitle(e.target.value)} required />
        </div>
        <div style={{ marginBottom: 10 }}>
          <label>Slug</label>
          <input value={slug} onChange={e => setSlug(e.target.value)} required />
        </div>
        <div style={{ marginBottom: 10 }}>
          <label>Category</label>
          <select value={categoryId} onChange={e => setCategoryId(parseInt(e.target.value))}>
            <option value={0}>Select category</option>
            {categories.map(cat => (
              <option key={cat.id} value={cat.id}>{cat.name}</option>
            ))}
          </select>
        </div>
        <div style={{ marginBottom: 10 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 5 }}>
            <label>Markdown Content</label>
            <button type="button" className="btn" onClick={() => setPreview(!preview)}>
              {preview ? 'Edit' : 'Preview'}
            </button>
          </div>
          {preview ? (
            <div style={{ border: '1px solid #ddd', minHeight: 400, padding: 20 }}>
              <ReactMarkdown>{markdown}</ReactMarkdown>
            </div>
          ) : (
            <textarea 
              value={markdown} 
              onChange={e => setMarkdown(e.target.value)} 
              rows={20}
              style={{ fontFamily: 'monospace' }}
            />
          )}
        </div>
        <button className="btn btn-primary" type="submit">Save</button>
      </form>
    </div>
  )
}
```

**Step 3: Update App.tsx**

```typescript
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Home } from './pages/Home'
import { ContentDetail } from './pages/ContentDetail'
import { Login } from './pages/Login'
import { Register } from './pages/Register'
import { CategoryList } from './pages/admin/CategoryList'
import { CategoryForm } from './pages/admin/CategoryForm'
import { ContentList } from './pages/admin/ContentList'
import { ContentForm } from './pages/admin/ContentForm'

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const token = localStorage.getItem('token')
  return token ? <>{children}</> : <Navigate to="/admin/login" />
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/content/:slug" element={<ContentDetail />} />
        <Route path="/admin/login" element={<Login />} />
        <Route path="/admin/register" element={<Register />} />
        <Route path="/admin" element={<PrivateRoute><ContentList /></PrivateRoute>} />
        <Route path="/admin/contents" element={<PrivateRoute><ContentList /></PrivateRoute>} />
        <Route path="/admin/contents/:id" element={<PrivateRoute><ContentForm /></PrivateRoute>} />
        <Route path="/admin/categories" element={<PrivateRoute><CategoryList /></PrivateRoute>} />
        <Route path="/admin/categories/:id" element={<PrivateRoute><CategoryForm /></PrivateRoute>} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
```

---

### Task 13: Admin Layout

**Files:**
- Create: `frontend/src/pages/admin/Layout.tsx`
- Modify: `frontend/src/App.tsx`

**Step 1: Create Layout.tsx**

```typescript
import { Link, useNavigate } from 'react-router-dom'

export function Layout({ children }: { children: React.ReactNode }) {
  const navigate = useNavigate()

  const handleLogout = () => {
    localStorage.removeItem('token')
    navigate('/admin/login')
  }

  return (
    <div>
      <nav style={{ background: '#333', padding: 15, display: 'flex', gap: 20 }}>
        <Link to="/admin" style={{ color: 'white', textDecoration: 'none' }}>Contents</Link>
        <Link to="/admin/categories" style={{ color: 'white', textDecoration: 'none' }}>Categories</Link>
        <button onClick={handleLogout} style={{ marginLeft: 'auto', background: 'none', border: 'none', color: 'white', cursor: 'pointer' }}>Logout</button>
      </nav>
      {children}
    </div>
  )
}
```

**Step 2: Update App.tsx to wrap admin routes**

```typescript
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { Home } from './pages/Home'
import { ContentDetail } from './pages/ContentDetail'
import { Login } from './pages/Login'
import { Register } from './pages/Register'
import { CategoryList } from './pages/admin/CategoryList'
import { CategoryForm } from './pages/admin/CategoryForm'
import { ContentList } from './pages/admin/ContentList'
import { ContentForm } from './pages/admin/ContentForm'
import { Layout } from './pages/admin/Layout'

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const token = localStorage.getItem('token')
  return token ? <>{children}</> : <Navigate to="/admin/login" />
}

function AdminRoute({ children }: { children: React.ReactNode }) {
  return (
    <PrivateRoute>
      <Layout>{children}</Layout>
    </PrivateRoute>
  )
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/content/:slug" element={<ContentDetail />} />
        <Route path="/admin/login" element={<Login />} />
        <Route path="/admin/register" element={<Register />} />
        <Route path="/admin" element={<AdminRoute><ContentList /></AdminRoute>} />
        <Route path="/admin/contents" element={<AdminRoute><ContentList /></AdminRoute>} />
        <Route path="/admin/contents/:id" element={<AdminRoute><ContentForm /></AdminRoute>} />
        <Route path="/admin/categories" element={<AdminRoute><CategoryList /></AdminRoute>} />
        <Route path="/admin/categories/:id" element={<AdminRoute><CategoryForm /></AdminRoute>} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
```

---

## Phase 3: Integration & Testing

### Task 14: Install Dependencies & Test

**Step 1: Install frontend dependencies**

```bash
cd frontend && npm install
```

**Step 2: Start backend**

```bash
cd backend && pip install -r requirements.txt && uvicorn app.main:app --reload
```

**Step 3: Start frontend**

```bash
cd frontend && npm run dev
```

**Step 4: Verify**

- Frontend: http://localhost:5173
- Backend: http://localhost:8000
- API docs: http://localhost:8000/docs

---

## Summary

Total tasks: 14

The implementation follows:
- TDD approach where applicable
- Clean REST API design
- JWT authentication
- Markdown to HTML auto-rendering
- Category-content association
- Draft/published status workflow