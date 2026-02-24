# rag_demo/backend/app/domain/entities/document.py
from pydantic import BaseModel
from typing import Optional
from datetime import datetime
import uuid


class Document(BaseModel):
    """文档实体"""
    id: str
    filename: str
    file_path: str
    content: Optional[str] = None
    chunk_count: int = 0
    created_at: datetime

    @classmethod
    def create(cls, filename: str, file_path: str) -> "Document":
        """工厂方法：创建新文档"""
        return cls(
            id=str(uuid.uuid4()),
            filename=filename,
            file_path=file_path,
            created_at=datetime.now(),
        )


class DocumentChunk(BaseModel):
    """文档分片实体"""
    id: str
    document_id: str
    content: str
    chunk_index: int

    @classmethod
    def create(cls, document_id: str, content: str, chunk_index: int) -> "DocumentChunk":
        """工厂方法：创建分片"""
        return cls(
            id=str(uuid.uuid4()),
            document_id=document_id,
            content=content,
            chunk_index=chunk_index,
        )
