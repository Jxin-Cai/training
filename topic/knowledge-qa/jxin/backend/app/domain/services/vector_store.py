# rag_demo/backend/app/domain/services/vector_store.py
from langchain_core.documents import Document
from typing import List
from app.infrastructure.vectorstore.chroma_store import get_vector_store


class VectorStoreService:
    """向量存储领域服务"""

    def store(self, documents: List[Document], document_id: str) -> int:
        """
        存储文档到向量数据库

        Args:
            documents: 分段后的文档列表
            document_id: 文档ID

        Returns:
            存储的分段数量
        """
        vector_store = get_vector_store()

        # 为每个文档添加元数据
        for doc in documents:
            if not doc.metadata:
                doc.metadata = {}
            doc.metadata["document_id"] = document_id

        # 添加到向量存储
        vector_store.add_documents(documents)
        return len(documents)

    def delete_by_document_id(self, document_id: str) -> bool:
        """
        删除指定文档的所有分段

        Args:
            document_id: 文档ID

        Returns:
            是否删除成功
        """
        vector_store = get_vector_store()
        # Chroma 删除需要使用 ID 列表,这里先获取再删除
        try:
            # 获取该文档的所有分段 ID
            collection = vector_store._collection
            results = collection.get(
                where={"document_id": document_id},
            )
            if results["ids"]:
                vector_store.delete(ids=results["ids"])
            return True
        except Exception:
            return False
