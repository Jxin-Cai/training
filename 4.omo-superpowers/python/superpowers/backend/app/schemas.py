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
