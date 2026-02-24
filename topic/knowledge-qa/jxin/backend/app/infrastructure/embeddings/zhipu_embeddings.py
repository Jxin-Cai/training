# rag_demo/backend/app/infrastructure/embeddings/zhipu_embeddings.py
"""
Embeddings 服务 - 使用阿里百炼 (DashScope) 向量模型

使用 langchain_community 的 DashScopeEmbeddings
"""
from langchain_community.embeddings import DashScopeEmbeddings
from app.core.config import get_settings


def get_embeddings() -> DashScopeEmbeddings:
    """获取阿里百炼 Embeddings 实例"""
    settings = get_settings()
    return DashScopeEmbeddings(
        model=settings.DASHSCOPE_EMBEDDING_MODEL,
        dashscope_api_key=settings.DASHSCOPE_API_KEY,
    )
