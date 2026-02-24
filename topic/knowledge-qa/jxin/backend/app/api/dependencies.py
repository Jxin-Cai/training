# rag_demo/backend/app/api/dependencies.py
from functools import lru_cache
from app.application.services.chat_service import ChatService
from app.application.services.document_service import DocumentService


@lru_cache()
def get_chat_service() -> ChatService:
    """获取对话服务实例"""
    return ChatService()


@lru_cache()
def get_document_service() -> DocumentService:
    """获取文档服务实例"""
    return DocumentService()
