# rag_demo/backend/app/domain/services/text_splitter.py
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_core.documents import Document
from typing import List
from app.core.config import get_settings


class TextSplitterService:
    """文本分段领域服务"""

    def __init__(self):
        settings = get_settings()
        self.splitter = RecursiveCharacterTextSplitter(
            chunk_size=settings.CHUNK_SIZE,
            chunk_overlap=settings.CHUNK_OVERLAP,
            separators=["\n\n", "\n", "。", "！", "？", "；", " ", ""],
        )

    def split(self, text: str) -> List[str]:
        """
        分割文本

        Args:
            text: 原始文本

        Returns:
            分段后的文本列表
        """
        return self.splitter.split_text(text)

    def split_documents(self, documents: List[Document]) -> List[Document]:
        """
        分割文档

        Args:
            documents: 原始文档列表

        Returns:
            分段后的文档列表
        """
        return self.splitter.split_documents(documents)
