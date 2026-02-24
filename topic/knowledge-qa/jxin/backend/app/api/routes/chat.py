# rag_demo/backend/app/api/routes/chat.py
from fastapi import APIRouter, Depends
from app.api.dependencies import get_chat_service
from app.application.services.chat_service import ChatService
from app.application.dto.chat import (
    ChatRequest,
    ChatResponse,
    ChatHistoryResponse,
)

router = APIRouter(prefix="/chat", tags=["chat"])


@router.post("", response_model=ChatResponse)
async def chat(
    request: ChatRequest,
    service: ChatService = Depends(get_chat_service),
):
    """发送消息并获取 AI 回复"""
    return service.chat(request)


@router.get("/history", response_model=ChatHistoryResponse)
async def get_history(
    session_id: str,
    service: ChatService = Depends(get_chat_service),
):
    """获取对话历史"""
    return service.get_history(session_id)


@router.delete("/history")
async def clear_history(
    session_id: str,
    service: ChatService = Depends(get_chat_service),
):
    """清空对话历史"""
    success = service.clear_history(session_id)
    return {"success": success}
