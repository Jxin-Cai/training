# rag_demo/backend/app/infrastructure/llm/zhipu_llm.py
"""
智谱 LLM 服务 - 使用 ChatAnthropic 直接调用

参考: https://docs.langchain.com/oss/python/integrations/chat/anthropic
"""
import os
from langchain_anthropic import ChatAnthropic
from app.core.config import get_settings


def get_llm() -> ChatAnthropic:
    """获取智谱 LLM 实例"""
    settings = get_settings()

    # 设置环境变量供 ChatAnthropic 使用
    os.environ["ANTHROPIC_API_KEY"] = settings.ZHIPU_API_KEY
    os.environ["ANTHROPIC_BASE_URL"] = settings.ZHIPU_API_HOST

    return ChatAnthropic(
        model=settings.ZHIPU_MODEL,
        temperature=0.7,
        max_tokens=2000,
        timeout=30,
    )
