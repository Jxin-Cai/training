# rag_demo/backend/app/infrastructure/vectorstore/chroma_store.py
from langchain_chroma import Chroma
from langchain_core.vectorstores import VectorStoreRetriever
from app.core.config import get_settings
from app.infrastructure.embeddings.zhipu_embeddings import get_embeddings

_vector_store: Chroma | None = None


def get_vector_store() -> Chroma:
    """获取 Chroma 向量存储实例 (单例)"""
    global _vector_store
    if _vector_store is None:
        settings = get_settings()
        embeddings = get_embeddings()
        _vector_store = Chroma(
            persist_directory=settings.CHROMA_PERSIST_DIR,
            embedding_function=embeddings,
        )
    return _vector_store


def get_retriever(top_k: int = 3) -> VectorStoreRetriever:
    """获取检索器"""
    vector_store = get_vector_store()
    return vector_store.as_retriever(
        search_type="similarity",
        search_kwargs={"k": top_k}
    )
