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
    published_at: Optional[datetime] = None
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
