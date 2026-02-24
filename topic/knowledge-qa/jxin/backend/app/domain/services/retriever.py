# rag_demo/backend/app/domain/services/retriever.py
from typing import List
from app.core.config import get_settings
from app.infrastructure.vectorstore.chroma_store import get_retriever


class RetrieverService:
    """向量检索领域服务"""

    def retrieve(self, query: str, top_k: int = None) -> List[str]:
        """
        检索相关文档片段

        Args:
            query: 查询文本
            top_k: 返回数量，默认从配置读取

        Returns:
            相关文档片段列表
        """
        settings = get_settings()
        if top_k is None:
            top_k = settings.RETRIEVAL_TOP_K

        retriever = get_retriever(top_k=top_k)
        documents = retriever.invoke(query)
        return [doc.page_content for doc in documents]
