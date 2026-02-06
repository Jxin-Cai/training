from functools import lru_cache

from app.domain.repositories import CategoryRepository, ContentRepository
from app.infrastructure.repositories import InMemoryCategoryRepository, InMemoryContentRepository
from app.infrastructure.markdown_service import MarkdownService
from app.application.category_service import CategoryService
from app.application.content_service import ContentService


@lru_cache()
def get_category_repository() -> CategoryRepository:
    """Get category repository instance."""
    return InMemoryCategoryRepository()


@lru_cache()
def get_content_repository() -> ContentRepository:
    """Get content repository instance."""
    return InMemoryContentRepository()


@lru_cache()
def get_markdown_service() -> MarkdownService:
    """Get markdown service instance."""
    return MarkdownService()


def get_category_service() -> CategoryService:
    """Get category service instance."""
    return CategoryService(category_repository=get_category_repository())


def get_content_service() -> ContentService:
    """Get content service instance."""
    return ContentService(
        content_repository=get_content_repository(),
        category_repository=get_category_repository(),
        markdown_service=get_markdown_service(),
    )
