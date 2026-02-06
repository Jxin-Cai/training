from datetime import datetime
from typing import Dict, List, Optional
from app.domain.models import Category, Content, ContentStatus
from app.domain.repositories import CategoryRepository, ContentRepository


class InMemoryCategoryRepository:
    """In-memory implementation of CategoryRepository."""

    def __init__(self):
        self._categories: Dict[str, Category] = {}

    async def create(self, category: Category) -> Category:
        self._categories[category.id] = category
        return category

    async def get_by_id(self, category_id: str) -> Optional[Category]:
        return self._categories.get(category_id)

    async def get_all(self) -> List[Category]:
        return list(self._categories.values())

    async def update(self, category: Category) -> Category:
        if category.id not in self._categories:
            raise ValueError(f"Category with id {category.id} not found")

        category.updated_at = datetime.utcnow()
        self._categories[category.id] = category
        return category

    async def delete(self, category_id: str) -> bool:
        if category_id in self._categories:
            del self._categories[category_id]
            return True
        return False

    async def get_by_name(self, name: str) -> Optional[Category]:
        for category in self._categories.values():
            if category.name == name:
                return category
        return None


class InMemoryContentRepository:
    """In-memory implementation of ContentRepository."""

    def __init__(self):
        self._contents: Dict[str, Content] = {}

    async def create(self, content: Content) -> Content:
        self._contents[content.id] = content
        return content

    async def get_by_id(self, content_id: str) -> Optional[Content]:
        return self._contents.get(content_id)

    async def get_all(self) -> List[Content]:
        return list(self._contents.values())

    async def get_by_category(self, category_id: str) -> List[Content]:
        return [
            content for content in self._contents.values() if content.category_id == category_id
        ]

    async def get_published(self) -> List[Content]:
        published_contents = [
            content
            for content in self._contents.values()
            if content.status == ContentStatus.PUBLISHED and content.published_at
        ]
        return sorted(
            published_contents, key=lambda x: x.published_at or datetime.min, reverse=True
        )

    async def update(self, content: Content) -> Content:
        if content.id not in self._contents:
            raise ValueError(f"Content with id {content.id} not found")

        content.updated_at = datetime.utcnow()
        self._contents[content.id] = content
        return content

    async def delete(self, content_id: str) -> bool:
        if content_id in self._contents:
            del self._contents[content_id]
            return True
        return False
