# rag_demo/backend/app/api/routes/document.py
from fastapi import APIRouter, Depends, UploadFile, File, HTTPException
from app.api.dependencies import get_document_service
from app.application.services.document_service import DocumentService
from app.application.dto.document import (
    UploadResponse,
    DocumentListResponse,
    DeleteResponse,
)

router = APIRouter(prefix="/documents", tags=["documents"])


@router.post("/upload", response_model=UploadResponse)
async def upload_document(
    file: UploadFile = File(...),
    service: DocumentService = Depends(get_document_service),
):
    """上传文档"""
    # 检查文件类型
    allowed_extensions = [".pdf", ".txt", ".md"]
    file_ext = "." + file.filename.split(".")[-1].lower()
    if file_ext not in allowed_extensions:
        raise HTTPException(
            status_code=400,
            detail=f"不支持的文件格式，支持: {allowed_extensions}"
        )

    content = await file.read()
    return await service.upload(content, file.filename)


@router.get("", response_model=DocumentListResponse)
async def list_documents(
    service: DocumentService = Depends(get_document_service),
):
    """获取文档列表"""
    return service.list_documents()


@router.delete("/{doc_id}", response_model=DeleteResponse)
async def delete_document(
    doc_id: str,
    service: DocumentService = Depends(get_document_service),
):
    """删除文档"""
    return service.delete_document(doc_id)
