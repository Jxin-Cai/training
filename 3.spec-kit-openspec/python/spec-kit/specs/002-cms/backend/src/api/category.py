from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from pydantic import BaseModel
from database import get_db
from models.category import Category
from datetime import datetime

router = APIRouter(prefix="/categories", tags=["categories"])


class CategoryResponse(BaseModel):
    id: int
    name: str
    created_at: datetime
    updated_at: datetime

    class Config:
        from_attributes = True


class CategoryCreate(BaseModel):
    name: str


class CategoryUpdate(BaseModel):
    name: str


@router.get("", response_model=dict)
def list_categories(db: Session = Depends(get_db)):
    categories = db.query(Category).all()
    return {"categories": [CategoryResponse.model_validate(c) for c in categories]}


@router.post("", response_model=CategoryResponse, status_code=201)
def create_category(category: CategoryCreate, db: Session = Depends(get_db)):
    existing = db.query(Category).filter(Category.name == category.name).first()
    if existing:
        raise HTTPException(status_code=400, detail="分类名称已存在")

    db_category = Category(name=category.name)
    db.add(db_category)
    db.commit()
    db.refresh(db_category)
    return CategoryResponse.model_validate(db_category)


@router.get("/{category_id}", response_model=CategoryResponse)
def get_category(category_id: int, db: Session = Depends(get_db)):
    category = db.query(Category).filter(Category.id == category_id).first()
    if not category:
        raise HTTPException(status_code=404, detail="分类不存在")
    return CategoryResponse.model_validate(category)


@router.put("/{category_id}", response_model=CategoryResponse)
def update_category(
    category_id: int, category: CategoryUpdate, db: Session = Depends(get_db)
):
    db_category = db.query(Category).filter(Category.id == category_id).first()
    if not db_category:
        raise HTTPException(status_code=404, detail="分类不存在")

    existing = (
        db.query(Category)
        .filter(Category.name == category.name, Category.id != category_id)
        .first()
    )
    if existing:
        raise HTTPException(status_code=400, detail="分类名称已存在")

    db_category.name = category.name
    db_category.updated_at = datetime.utcnow()
    db.commit()
    db.refresh(db_category)
    return CategoryResponse.model_validate(db_category)


@router.delete("/{category_id}", status_code=204)
def delete_category(category_id: int, db: Session = Depends(get_db)):
    category = db.query(Category).filter(Category.id == category_id).first()
    if not category:
        raise HTTPException(status_code=404, detail="分类不存在")

    if category.contents:
        raise HTTPException(status_code=400, detail="该分类下存在内容，无法删除")

    db.delete(category)
    db.commit()
    return None
