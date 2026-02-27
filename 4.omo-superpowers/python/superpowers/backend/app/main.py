from fastapi import FastAPI, Depends, HTTPException, status
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session
import markdown
from app.database import get_db, SessionLocal
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
    create_article,
    update_article,
    delete_article,
    create_user,
)
from app import schemas
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

    user = get_user_by_username(db, username)
    if user is None:
        raise HTTPException(status_code=401, detail="User not found")
    return user


@app.post("/api/auth/login", response_model=schemas.Token)
def login(user_login: schemas.UserLogin, db: Session = Depends(get_db)):
    user = get_user_by_username(db, user_login.username)
    if not user or not verify_password(user_login.password, user.password_hash):
        raise HTTPException(status_code=401, detail="Incorrect username or password")
    access_token = create_access_token(data={"sub": user.username})
    return {"access_token": access_token, "token_type": "bearer"}


@app.get("/api/categories", response_model=list[schemas.CategoryResponse])
def read_categories(db: Session = Depends(get_db)):
    return get_categories(db)


@app.post("/api/categories", response_model=schemas.CategoryResponse)
def create_category_endpoint(
    category: schemas.CategoryCreate, db: Session = Depends(get_db)
):
    return create_category(db, category.name)


@app.put("/api/categories/{category_id}", response_model=schemas.CategoryResponse)
def update_category_endpoint(
    category_id: int, category: schemas.CategoryCreate, db: Session = Depends(get_db)
):
    return update_category(db, category_id, category.name)


@app.delete("/api/categories/{category_id}")
def delete_category_endpoint(category_id: int, db: Session = Depends(get_db)):
    delete_category(db, category_id)
    return {"message": "Category deleted"}


@app.get("/api/articles", response_model=list[schemas.ArticleResponse])
def read_articles(db: Session = Depends(get_db)):
    return get_articles(db)


@app.get("/api/articles/published", response_model=list[schemas.ArticleResponse])
def read_published_articles_endpoint(db: Session = Depends(get_db)):
    return get_published_articles(db)


@app.get("/api/articles/{article_id}", response_model=schemas.ArticleResponse)
def read_article(article_id: int, db: Session = Depends(get_db)):
    article = get_article(db, article_id)
    if not article:
        raise HTTPException(status_code=404, detail="Article not found")
    return article


@app.post("/api/articles", response_model=schemas.ArticleResponse)
def create_article_endpoint(
    article: schemas.ArticleCreate, db: Session = Depends(get_db)
):
    content_html = markdown.markdown(article.content_md)
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
    article_id: int, article: schemas.ArticleUpdate, db: Session = Depends(get_db)
):
    update_data = article.model_dump(exclude_unset=True)
    if "content_md" in update_data:
        update_data["content_html"] = markdown.markdown(update_data["content_md"])
    return update_article(db, article_id, **update_data)


@app.delete("/api/articles/{article_id}")
def delete_article_endpoint(article_id: int, db: Session = Depends(get_db)):
    delete_article(db, article_id)
    return {"message": "Article deleted"}


@app.on_event("startup")
def create_default_admin():
    db = SessionLocal()
    if not get_user_by_username(db, "admin"):
        create_user(db, "admin", "admin123")
    db.close()
