from typing import List, Optional
from fastapi import APIRouter, Depends, HTTPException, Query
from pydantic import BaseModel

from app.domain.models import Content, ContentStatus
from app.application.content_service import ContentService
from app.presentation.dependencies import get_content_service

router = APIRouter(prefix="/contents", tags=["contents"])


class ContentCreateRequest(BaseModel):
    title: str
    markdown_content: str
    category_id: str
    status: ContentStatus = ContentStatus.DRAFT


class ContentUpdateRequest(BaseModel):
    title: Optional[str] = None
    markdown_content: Optional[str] = None
    category_id: Optional[str] = None


@router.post("/", response_model=Content, status_code=201)
async def create_content(
    request: ContentCreateRequest, content_service: ContentService = Depends(get_content_service)
):
    """Create new content."""
    try:
        return await content_service.create_content(
            title=request.title,
            markdown_content=request.markdown_content,
            category_id=request.category_id,
            status=request.status,
        )
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.get("/", response_model=List[Content])
async def get_all_contents(
    category_id: Optional[str] = Query(None, description="Filter by category"),
    published_only: bool = Query(False, description="Get only published content"),
    content_service: ContentService = Depends(get_content_service),
):
    """Get all content with optional filters."""
    if published_only:
        return await content_service.get_published_content()
    elif category_id:
        return await content_service.get_content_by_category(category_id)
    else:
        return await content_service.get_all_content()


@router.get("/{content_id}", response_model=Content)
async def get_content(
    content_id: str, content_service: ContentService = Depends(get_content_service)
):
    """Get content by ID."""
    content = await content_service.get_content_by_id(content_id)
    if not content:
        raise HTTPException(status_code=404, detail="Content not found")
    return content


@router.put("/{content_id}", response_model=Content)
async def update_content(
    content_id: str,
    request: ContentUpdateRequest,
    content_service: ContentService = Depends(get_content_service),
):
    """Update content."""
    try:
        return await content_service.update_content(
            content_id=content_id,
            title=request.title,
            markdown_content=request.markdown_content,
            category_id=request.category_id,
        )
    except ValueError as e:
        if "not found" in str(e):
            raise HTTPException(status_code=404, detail="Content not found")
        raise HTTPException(status_code=400, detail=str(e))


@router.post("/{content_id}/publish", response_model=Content)
async def publish_content(
    content_id: str, content_service: ContentService = Depends(get_content_service)
):
    """Publish content."""
    try:
        return await content_service.publish_content(content_id)
    except ValueError as e:
        raise HTTPException(status_code=404, detail="Content not found")


@router.post("/{content_id}/unpublish", response_model=Content)
async def unpublish_content(
    content_id: str, content_service: ContentService = Depends(get_content_service)
):
    """Set content back to draft."""
    try:
        return await content_service.unpublish_content(content_id)
    except ValueError as e:
        raise HTTPException(status_code=404, detail="Content not found")


@router.delete("/{content_id}", status_code=204)
async def delete_content(
    content_id: str, content_service: ContentService = Depends(get_content_service)
):
    """Delete content."""
    success = await content_service.delete_content(content_id)
    if not success:
        raise HTTPException(status_code=404, detail="Content not found")
