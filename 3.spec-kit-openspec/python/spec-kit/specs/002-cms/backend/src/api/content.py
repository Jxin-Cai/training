from fastapi import APIRouter, Depends, HTTPException, UploadFile, File, Query
from sqlalchemy.orm import Session
from pydantic import BaseModel
from database import get_db
from models.content import Content
from models.category import Category
from datetime import datetime
from services.markdown_service import render_markdown

router = APIRouter(prefix="/contents", tags=["contents"])


class ContentResponse(BaseModel):
    id: int
    title: str
    category_id: int
    category_name: str | None = None
    markdown_content: str
    html_content: str
    status: str
    created_at: datetime
    published_at: datetime | None = None

    class Config:
        from_attributes = True


class ContentCreate(BaseModel):
    title: str
    category_id: int
    markdown_content: str
    status: str = "draft"


class ContentUpdate(BaseModel):
    title: str | None = None
    category_id: int | None = None
    markdown_content: str | None = None
    status: str | None = None


def content_to_response(
    content: Content, include_markdown: bool = False
) -> ContentResponse:
    return ContentResponse(
        id=content.id,
        title=content.title,
        category_id=content.category_id,
        category_name=content.category.name if content.category else None,
        markdown_content=content.markdown_content if include_markdown else "",
        html_content=content.html_content,
        status=content.status,
        created_at=content.created_at,
        published_at=content.published_at,
    )


@router.get("/admin", response_model=dict)
def list_all_contents(
    page: int = Query(1, ge=1),
    limit: int = Query(10, ge=1, le=100),
    status: str | None = None,
    db: Session = Depends(get_db),
):
    offset = (page - 1) * limit
    query = db.query(Content)
    if status:
        query = query.filter(Content.status == status)
    contents = (
        query.order_by(Content.created_at.desc()).offset(offset).limit(limit).all()
    )
    total = query.count()
    return {
        "contents": [content_to_response(c, include_markdown=True) for c in contents],
        "total": total,
        "page": page,
        "limit": limit,
    }


@router.get("/all", response_model=dict)
def list_contents(
    page: int = Query(1, ge=1),
    limit: int = Query(10, ge=1, le=100),
    db: Session = Depends(get_db),
):
    offset = (page - 1) * limit
    contents = (
        db.query(Content)
        .filter(Content.status == "published")
        .order_by(Content.published_at.desc().nullslast())
        .offset(offset)
        .limit(limit)
        .all()
    )
    total = db.query(Content).filter(Content.status == "published").count()
    return {
        "contents": [content_to_response(c) for c in contents],
        "total": total,
        "page": page,
        "limit": limit,
    }


@router.get("/{content_id}", response_model=ContentResponse)
def get_content(content_id: int, db: Session = Depends(get_db)):
    content = db.query(Content).filter(Content.id == content_id).first()
    if not content or content.status != "published":
        raise HTTPException(status_code=404, detail="内容不存在")
    return content_to_response(content, include_markdown=False)


@router.post("", response_model=ContentResponse, status_code=201)
def create_content(content: ContentCreate, db: Session = Depends(get_db)):
    category = db.query(Category).filter(Category.id == content.category_id).first()
    if not category:
        raise HTTPException(status_code=400, detail="分类不存在")

    html_content = render_markdown(content.markdown_content)

    published_at = datetime.utcnow() if content.status == "published" else None

    db_content = Content(
        title=content.title,
        category_id=content.category_id,
        markdown_content=content.markdown_content,
        html_content=html_content,
        status=content.status,
        published_at=published_at,
    )
    db.add(db_content)
    db.commit()
    db.refresh(db_content)
    return content_to_response(db_content, include_markdown=True)


@router.put("/{content_id}", response_model=ContentResponse)
def update_content(
    content_id: int, content: ContentUpdate, db: Session = Depends(get_db)
):
    db_content = db.query(Content).filter(Content.id == content_id).first()
    if not db_content:
        raise HTTPException(status_code=404, detail="内容不存在")

    if content.category_id is not None:
        category = db.query(Category).filter(Category.id == content.category_id).first()
        if not category:
            raise HTTPException(status_code=400, detail="分类不存在")
        db_content.category_id = content.category_id

    if content.title is not None:
        db_content.title = content.title

    if content.markdown_content is not None:
        db_content.markdown_content = content.markdown_content
        db_content.html_content = render_markdown(content.markdown_content)

    if content.status is not None:
        db_content.status = content.status
        if content.status == "published" and not db_content.published_at:
            db_content.published_at = datetime.utcnow()
        elif content.status == "draft":
            db_content.published_at = None

    db.commit()
    db.refresh(db_content)
    return content_to_response(db_content, include_markdown=True)


@router.delete("/{content_id}", status_code=204)
def delete_content(content_id: int, db: Session = Depends(get_db)):
    content = db.query(Content).filter(Content.id == content_id).first()
    if not content:
        raise HTTPException(status_code=404, detail="内容不存在")
    db.delete(content)
    db.commit()
    return None


@router.post("/upload")
async def upload_file(file: UploadFile = File(...)):
    if not file.filename.endswith(".md"):
        raise HTTPException(status_code=400, detail="仅支持 MD 文件")
    content = await file.read()
    return {"content": content.decode("utf-8")}
