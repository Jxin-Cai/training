from typing import Optional
from datetime import datetime
from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.orm import Session
import markdown

from app.models import Base, User, Category, Content
from app.database import engine, get_db
from app.auth import (
    verify_password,
    get_password_hash,
    create_access_token,
    get_current_user,
)
from app.schemas import (
    UserCreate,
    UserResponse,
    Token,
    CategoryCreate,
    CategoryResponse,
    ContentCreate,
    ContentUpdate,
    ContentResponse,
)

Base.metadata.create_all(bind=engine)

app = FastAPI(title="CMS API")


def render_markdown(md_content: str) -> str:
    return markdown.markdown(md_content)


# Auth routes
@app.post("/api/auth/register", response_model=UserResponse)
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


@app.post("/api/auth/login", response_model=Token)
def login(
    form_data: OAuth2PasswordRequestForm = Depends(), db: Session = Depends(get_db)
):
    user = db.query(User).filter(User.username == form_data.username).first()
    if not user or not verify_password(form_data.password, user.password_hash):
        raise HTTPException(status_code=401, detail="Invalid credentials")
    token = create_access_token(data={"sub": user.username})
    return {"access_token": token, "token_type": "bearer"}


@app.get("/api/auth/me", response_model=UserResponse)
def me(current_user: User = Depends(get_current_user)):
    return current_user


# Admin Category CRUD
@app.get("/api/admin/categories", response_model=list[CategoryResponse])
def list_categories(
    db: Session = Depends(get_db), current_user: User = Depends(get_current_user)
):
    return db.query(Category).order_by(Category.sort_order).all()


@app.post("/api/admin/categories", response_model=CategoryResponse)
def create_category(
    category: CategoryCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    db_category = Category(**category.model_dump())
    db.add(db_category)
    db.commit()
    db.refresh(db_category)
    return db_category


@app.put("/api/admin/categories/{category_id}", response_model=CategoryResponse)
def update_category(
    category_id: int,
    category: CategoryCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    db_category = db.query(Category).filter(Category.id == category_id).first()
    if not db_category:
        raise HTTPException(status_code=404, detail="Category not found")
    db_category.name = category.name
    db_category.slug = category.slug
    db_category.sort_order = category.sort_order
    db.commit()
    db.refresh(db_category)
    return db_category


@app.delete("/api/admin/categories/{category_id}")
def delete_category(
    category_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    db_category = db.query(Category).filter(Category.id == category_id).first()
    if not db_category:
        raise HTTPException(status_code=404, detail="Category not found")
    db.delete(db_category)
    db.commit()
    return {"message": "Deleted"}


# Admin Content CRUD
@app.get("/api/admin/contents", response_model=list[ContentResponse])
def list_contents(
    status: Optional[str] = None,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    query = db.query(Content)
    if status:
        query = query.filter(Content.status == status)
    return query.order_by(Content.created_at.desc()).all()


@app.post("/api/admin/contents", response_model=ContentResponse)
def create_content(
    content: ContentCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    html = render_markdown(content.markdown_content)
    db_content = Content(
        **content.model_dump(), html_content=html, author_id=current_user.id
    )
    db.add(db_content)
    db.commit()
    db.refresh(db_content)
    return db_content


@app.put("/api/admin/contents/{content_id}", response_model=ContentResponse)
def update_content(
    content_id: int,
    content: ContentUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
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


@app.delete("/api/admin/contents/{content_id}")
def delete_content(
    content_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    db_content = db.query(Content).filter(Content.id == content_id).first()
    if not db_content:
        raise HTTPException(status_code=404, detail="Content not found")
    db.delete(db_content)
    db.commit()
    return {"message": "Deleted"}


@app.post("/api/admin/contents/{content_id}/publish")
def publish_content(
    content_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    db_content = db.query(Content).filter(Content.id == content_id).first()
    if not db_content:
        raise HTTPException(status_code=404, detail="Content not found")
    db_content.status = "published"
    db_content.published_at = datetime.utcnow()
    db.commit()
    return {"message": "Published"}


@app.post("/api/admin/contents/{content_id}/unpublish")
def unpublish_content(
    content_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    db_content = db.query(Content).filter(Content.id == content_id).first()
    if not db_content:
        raise HTTPException(status_code=404, detail="Content not found")
    db_content.status = "draft"
    db.commit()
    return {"message": "Unpublished"}


# Public API
@app.get("/api/contents", response_model=list[ContentResponse])
def list_published_contents(db: Session = Depends(get_db)):
    return (
        db.query(Content)
        .filter(Content.status == "published")
        .order_by(Content.published_at.desc())
        .all()
    )


@app.get("/api/contents/{slug}", response_model=ContentResponse)
def get_content_by_slug(slug: str, db: Session = Depends(get_db)):
    content = (
        db.query(Content)
        .filter(Content.slug == slug, Content.status == "published")
        .first()
    )
    if not content:
        raise HTTPException(status_code=404, detail="Content not found")
    return content


@app.get("/api/categories", response_model=list[CategoryResponse])
def list_public_categories(db: Session = Depends(get_db)):
    return db.query(Category).order_by(Category.sort_order).all()


@app.get("/")
def root():
    return {"message": "CMS API"}
