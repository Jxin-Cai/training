# rag_demo/backend/app/application/dto/document.py
from pydantic import BaseModel
from datetime import datetime
from typing import List


class UploadResponse(BaseModel):
    """上传响应"""
    doc_id: str
    filename: str
    chunks: int


class DocumentDTO(BaseModel):
    """文档 DTO"""
    id: str
    filename: str
    chunk_count: int
    created_at: datetime


class DocumentListResponse(BaseModel):
    """文档列表响应"""
    documents: List[DocumentDTO]


class DeleteResponse(BaseModel):
    """删除响应"""
    success: bool
    message: str = ""
