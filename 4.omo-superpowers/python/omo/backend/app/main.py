from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session

from app.database import get_db, SessionLocal, create_tables
from app.crud import (
    get_user_by_username,
    verify_password,
    get_categories,
    create_category,
    update_category,
    delete_category,
    get_articles,
    get_published_articles,
    get_article,
    get_published_article,
    create_article,
    update_article,
    delete_article,
    create_user,
)
from app import schemas
from app.security import get_current_user
from app.services.markdown_render import render_markdown
from jose import JWTError, jwt
from datetime import datetime, timedelta

SECRET_KEY = "your-secret-key-change-in-production"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 30

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


# ============ Public Endpoints (No Auth Required) ============


@app.post("/api/auth/login", response_model=schemas.Token)
def login(user_login: schemas.UserLogin, db: Session = Depends(get_db)):
    user = get_user_by_username(db, user_login.username)
    if not user or not verify_password(user_login.password, user.password_hash):
        raise HTTPException(status_code=401, detail="Incorrect username or password")
    access_token = create_access_token(data={"sub": user.username})
    return {"access_token": access_token, "token_type": "bearer"}


@app.get("/api/categories", response_model=list[schemas.CategoryResponse])
def read_categories(db: Session = Depends(get_db)):
    """Public endpoint: list all categories"""
    return get_categories(db)


@app.get("/api/articles/published", response_model=list[schemas.ArticleResponse])
def read_published_articles_endpoint(db: Session = Depends(get_db)):
    """Public endpoint: list published articles sorted by published_at"""
    return get_published_articles(db)


@app.get("/api/articles/published/{article_id}", response_model=schemas.ArticleResponse)
def read_published_article(article_id: int, db: Session = Depends(get_db)):
    """Public endpoint: get a single published article"""
    article = get_published_article(db, article_id)
    if not article:
        raise HTTPException(
            status_code=404, detail="Article not found or not published"
        )
    return article


# ============ Admin Endpoints (Auth Required) ============


@app.get("/api/articles", response_model=list[schemas.ArticleResponse])
def read_all_articles(
    db: Session = Depends(get_db), current_user=Depends(get_current_user)
):
    """Admin endpoint: list all articles (including drafts)"""
    return get_articles(db)


@app.get("/api/articles/{article_id}", response_model=schemas.ArticleResponse)
def read_article(
    article_id: int,
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user),
):
    """Admin endpoint: get any article by ID"""
    article = get_article(db, article_id)
    if not article:
        raise HTTPException(status_code=404, detail="Article not found")
    return article


@app.post("/api/articles", response_model=schemas.ArticleResponse)
def create_article_endpoint(
    article: schemas.ArticleCreate,
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user),
):
    """Admin endpoint: create a new article"""
    # Always use backend to render markdown (never trust frontend HTML)
    content_html = render_markdown(article.content_md)
    return create_article(
        db,
        title=article.title,
        content_md=article.content_md,
        content_html=content_html,
        category_id=article.category_id,
        status=article.status,
    )


@app.put("/api/articles/{article_id}", response_model=schemas.ArticleResponse)
def update_article_endpoint(
    article_id: int,
    article: schemas.ArticleUpdate,
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user),
):
    """Admin endpoint: update an article"""
    update_data = article.model_dump(exclude_unset=True)

    # Always re-render markdown if content_md is being updated
    # Never trust frontend-provided content_html
    if "content_md" in update_data:
        update_data["content_html"] = render_markdown(update_data["content_md"])

    return update_article(db, article_id, **update_data)


@app.delete("/api/articles/{article_id}")
def delete_article_endpoint(
    article_id: int,
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user),
):
    """Admin endpoint: delete an article"""
    delete_article(db, article_id)
    return {"message": "Article deleted"}


@app.post("/api/categories", response_model=schemas.CategoryResponse)
def create_category_endpoint(
    category: schemas.CategoryCreate,
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user),
):
    """Admin endpoint: create a category"""
    return create_category(db, category.name)


@app.put("/api/categories/{category_id}", response_model=schemas.CategoryResponse)
def update_category_endpoint(
    category_id: int,
    category: schemas.CategoryCreate,
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user),
):
    """Admin endpoint: update a category"""
    return update_category(db, category_id, category.name)


@app.delete("/api/categories/{category_id}")
def delete_category_endpoint(
    category_id: int,
    db: Session = Depends(get_db),
    current_user=Depends(get_current_user),
):
    """Admin endpoint: delete a category"""
    delete_category(db, category_id)
    return {"message": "Category deleted"}


@app.on_event("startup")
def create_default_admin():
    # Create database tables first
    create_tables()
    
    db = SessionLocal()
    if not get_user_by_username(db, "admin"):
        create_user(db, "admin", "admin123")
    db.close()
