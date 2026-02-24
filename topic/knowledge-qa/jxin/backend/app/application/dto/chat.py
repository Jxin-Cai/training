# rag_demo/backend/app/application/dto/chat.py
from pydantic import BaseModel
from typing import List, Optional


class ChatRequest(BaseModel):
    """对话请求"""
    session_id: str
    message: str


class ChatResponse(BaseModel):
    """对话响应"""
    reply: str


class MessageDTO(BaseModel):
    """消息 DTO"""
    role: str  # "user" or "assistant"
    content: str


class ChatHistoryResponse(BaseModel):
    """对话历史响应"""
    messages: List[MessageDTO]
