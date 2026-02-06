from abc import abstractmethod
from typing import List, Optional, Protocol
from .models import Category, Content


class CategoryRepository(Protocol):
    """Repository protocol for Category entities."""

    async def create(self, category: Category) -> Category:
        """Create a new category."""
        ...

    async def get_by_id(self, category_id: str) -> Optional[Category]:
        """Get a category by ID."""
        ...

    async def get_all(self) -> List[Category]:
        """Get all categories."""
        ...

    async def update(self, category: Category) -> Category:
        """Update an existing category."""
        ...

    async def delete(self, category_id: str) -> bool:
        """Delete a category by ID."""
        ...

    async def get_by_name(self, name: str) -> Optional[Category]:
        """Get a category by name."""
        ...


class ContentRepository(Protocol):
    """Repository protocol for Content entities."""

    async def create(self, content: Content) -> Content:
        """Create new content."""
        ...

    async def get_by_id(self, content_id: str) -> Optional[Content]:
        """Get content by ID."""
        ...

    async def get_all(self) -> List[Content]:
        """Get all content."""
        ...

    async def get_by_category(self, category_id: str) -> List[Content]:
        """Get content by category."""
        ...

    async def get_published(self) -> List[Content]:
        """Get published content sorted by published_at."""
        ...

    async def update(self, content: Content) -> Content:
        """Update existing content."""
        ...

    async def delete(self, content_id: str) -> bool:
        """Delete content by ID."""
        ...
