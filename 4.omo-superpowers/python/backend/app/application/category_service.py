from typing import List, Optional
from datetime import datetime

from app.domain.models import Category
from app.domain.repositories import CategoryRepository


class CategoryService:
    """Service for managing categories."""

    def __init__(self, category_repository: CategoryRepository):
        self.category_repository = category_repository

    async def create_category(self, name: str) -> Category:
        """Create a new category."""
        # Check if category with same name exists
        existing = await self.category_repository.get_by_name(name)
        if existing:
            raise ValueError(f"Category with name '{name}' already exists")

        category = Category(name=name)
        return await self.category_repository.create(category)

    async def get_category_by_id(self, category_id: str) -> Optional[Category]:
        """Get a category by ID."""
        return await self.category_repository.get_by_id(category_id)

    async def get_all_categories(self) -> List[Category]:
        """Get all categories."""
        return await self.category_repository.get_all()

    async def update_category(self, category_id: str, name: str) -> Category:
        """Update an existing category."""
        category = await self.category_repository.get_by_id(category_id)
        if not category:
            raise ValueError(f"Category with id {category_id} not found")

        # Check if another category has this name
        existing = await self.category_repository.get_by_name(name)
        if existing and existing.id != category_id:
            raise ValueError(f"Category with name '{name}' already exists")

        category.name = name
        return await self.category_repository.update(category)

    async def delete_category(self, category_id: str) -> bool:
        """Delete a category."""
        return await self.category_repository.delete(category_id)
