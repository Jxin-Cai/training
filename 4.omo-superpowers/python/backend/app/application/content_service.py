from typing import List, Optional
from datetime import datetime

from app.domain.models import Content, ContentStatus
from app.domain.repositories import ContentRepository, CategoryRepository
from app.infrastructure.markdown_service import MarkdownService


class ContentService:
    """Service for managing content."""

    def __init__(
        self,
        content_repository: ContentRepository,
        category_repository: CategoryRepository,
        markdown_service: MarkdownService,
    ):
        self.content_repository = content_repository
        self.category_repository = category_repository
        self.markdown_service = markdown_service

    async def create_content(
        self,
        title: str,
        markdown_content: str,
        category_id: str,
        status: ContentStatus = ContentStatus.DRAFT,
    ) -> Content:
        """Create new content."""
        # Verify category exists
        category = await self.category_repository.get_by_id(category_id)
        if not category:
            raise ValueError(f"Category with id {category_id} not found")

        # Convert markdown to HTML
        html_content = self.markdown_service.convert_with_bleach(markdown_content)

        content = Content(
            title=title,
            markdown_content=markdown_content,
            html_content=html_content,
            status=status,
            category_id=category_id,
            published_at=datetime.utcnow() if status == ContentStatus.PUBLISHED else None,
        )

        return await self.content_repository.create(content)

    async def get_content_by_id(self, content_id: str) -> Optional[Content]:
        """Get content by ID."""
        return await self.content_repository.get_by_id(content_id)

    async def get_all_content(self) -> List[Content]:
        """Get all content."""
        return await self.content_repository.get_all()

    async def get_content_by_category(self, category_id: str) -> List[Content]:
        """Get content by category."""
        # Verify category exists
        category = await self.category_repository.get_by_id(category_id)
        if not category:
            raise ValueError(f"Category with id {category_id} not found")

        return await self.content_repository.get_by_category(category_id)

    async def get_published_content(self) -> List[Content]:
        """Get published content sorted by published_at."""
        return await self.content_repository.get_published()

    async def update_content(
        self,
        content_id: str,
        title: Optional[str] = None,
        markdown_content: Optional[str] = None,
        category_id: Optional[str] = None,
    ) -> Content:
        """Update existing content."""
        content = await self.content_repository.get_by_id(content_id)
        if not content:
            raise ValueError(f"Content with id {content_id} not found")

        if title is not None:
            content.title = title

        if markdown_content is not None:
            content.markdown_content = markdown_content
            content.html_content = self.markdown_service.convert_with_bleach(markdown_content)

        if category_id is not None:
            category = await self.category_repository.get_by_id(category_id)
            if not category:
                raise ValueError(f"Category with id {category_id} not found")
            content.category_id = category_id

        return await self.content_repository.update(content)

    async def publish_content(self, content_id: str) -> Content:
        """Publish content."""
        content = await self.content_repository.get_by_id(content_id)
        if not content:
            raise ValueError(f"Content with id {content_id} not found")

        content.status = ContentStatus.PUBLISHED
        content.published_at = datetime.utcnow()

        return await self.content_repository.update(content)

    async def unpublish_content(self, content_id: str) -> Content:
        """Set content back to draft."""
        content = await self.content_repository.get_by_id(content_id)
        if not content:
            raise ValueError(f"Content with id {content_id} not found")

        content.status = ContentStatus.DRAFT
        content.published_at = None

        return await self.content_repository.update(content)

    async def delete_content(self, content_id: str) -> bool:
        """Delete content."""
        return await self.content_repository.delete(content_id)
