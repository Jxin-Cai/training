# rag_demo/backend/app/infrastructure/memory/chat_memory.py
from langchain_core.chat_history import BaseChatMessageHistory
from langchain_core.messages import BaseMessage, HumanMessage, AIMessage
from collections import deque
from typing import Dict
from app.core.config import get_settings


class InMemoryHistory(BaseChatMessageHistory):
    """内存会话历史"""

    def __init__(self, max_turns: int = 10):
        self._messages: deque = deque(maxlen=max_turns * 2)  # 每轮包含用户和AI两条消息

    @property
    def messages(self) -> list[BaseMessage]:
        return list(self._messages)

    def add_message(self, message: BaseMessage) -> None:
        self._messages.append(message)

    def clear(self) -> None:
        self._messages.clear()


# 会话存储
_session_histories: Dict[str, InMemoryHistory] = {}


def get_session_history(session_id: str) -> InMemoryHistory:
    """获取或创建会话历史"""
    settings = get_settings()
    if session_id not in _session_histories:
        _session_histories[session_id] = InMemoryHistory(
            max_turns=settings.MAX_HISTORY_TURNS
        )
    return _session_histories[session_id]


def clear_session_history(session_id: str) -> None:
    """清空会话历史"""
    if session_id in _session_histories:
        _session_histories[session_id].clear()
