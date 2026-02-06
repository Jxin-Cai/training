from typing import List
from fastapi import APIRouter, Depends, HTTPException

from app.domain.models import Category
from app.application.category_service import CategoryService
from app.presentation.dependencies import get_category_service

router = APIRouter(prefix="/categories", tags=["categories"])


@router.post("/", response_model=Category, status_code=201)
async def create_category(
    name: str, category_service: CategoryService = Depends(get_category_service)
):
    """Create a new category."""
    try:
        return await category_service.create_category(name)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.get("/", response_model=List[Category])
async def get_all_categories(category_service: CategoryService = Depends(get_category_service)):
    """Get all categories."""
    return await category_service.get_all_categories()


@router.get("/{category_id}", response_model=Category)
async def get_category(
    category_id: str, category_service: CategoryService = Depends(get_category_service)
):
    """Get a category by ID."""
    category = await category_service.get_category_by_id(category_id)
    if not category:
        raise HTTPException(status_code=404, detail="Category not found")
    return category


@router.put("/{category_id}", response_model=Category)
async def update_category(
    category_id: str, name: str, category_service: CategoryService = Depends(get_category_service)
):
    """Update a category."""
    try:
        return await category_service.update_category(category_id, name)
    except ValueError as e:
        if "not found" in str(e):
            raise HTTPException(status_code=404, detail="Category not found")
        raise HTTPException(status_code=400, detail=str(e))


@router.delete("/{category_id}", status_code=204)
async def delete_category(
    category_id: str, category_service: CategoryService = Depends(get_category_service)
):
    """Delete a category."""
    success = await category_service.delete_category(category_id)
    if not success:
        raise HTTPException(status_code=404, detail="Category not found")
