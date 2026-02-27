from sqlalchemy.orm import Session
from sqlalchemy.exc import IntegrityError
from app.models import Category, Article, User
import bcrypt
from datetime import datetime


def get_password_hash(password: str) -> str:
    return bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8")


def verify_password(plain_password: str, hashed_password: str) -> bool:
    return bcrypt.checkpw(
        plain_password.encode("utf-8"), hashed_password.encode("utf-8")
    )


# Category CRUD
def get_categories(db: Session):
    return db.query(Category).all()


def get_category(db: Session, category_id: int):
    return db.query(Category).filter(Category.id == category_id).first()


def create_category(db: Session, name: str):
    try:
        category = Category(name=name)
        db.add(category)
        db.commit()
        db.refresh(category)
        return category
    except IntegrityError:
        db.rollback()
        raise ValueError(f"Category '{name}' already exists")


def update_category(db: Session, category_id: int, name: str):
    category = get_category(db, category_id)
    if category:
        try:
            category.name = name
            db.commit()
            db.refresh(category)
        except IntegrityError:
            db.rollback()
            raise ValueError(f"Category '{name}' already exists")
    return category


def delete_category(db: Session, category_id: int):
    category = get_category(db, category_id)
    if category:
        db.delete(category)
        db.commit()
    return category


# Article CRUD
def get_articles(db: Session, skip: int = 0, limit: int = 100):
    return (
        db.query(Article)
        .order_by(Article.created_at.desc())
        .offset(skip)
        .limit(limit)
        .all()
    )


def get_published_articles(db: Session, skip: int = 0, limit: int = 100):
    return (
        db.query(Article)
        .filter(Article.status == "published")
        .order_by(Article.created_at.desc())
        .offset(skip)
        .limit(limit)
        .all()
    )


def get_article(db: Session, article_id: int):
    return db.query(Article).filter(Article.id == article_id).first()


def create_article(
    db: Session,
    title: str,
    content_md: str,
    content_html: str,
    category_id: int,
    status: str = "draft",
):
    article = Article(
        title=title,
        content_md=content_md,
        content_html=content_html,
        category_id=category_id,
        status=status,
    )
    db.add(article)
    db.commit()
    db.refresh(article)
    return article


def update_article(db: Session, article_id: int, **kwargs):
    article = get_article(db, article_id)
    if article:
        try:
            for key, value in kwargs.items():
                if value is not None and hasattr(article, key):
                    setattr(article, key, value)
            article.updated_at = datetime.utcnow()
            db.commit()
            db.refresh(article)
        except Exception:
            db.rollback()
            raise
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
    try:
        user = User(username=username, password_hash=get_password_hash(password))
        db.add(user)
        db.commit()
        db.refresh(user)
        return user
    except IntegrityError:
        db.rollback()
        raise ValueError(f"User '{username}' already exists")
