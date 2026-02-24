# rag_demo/backend/app/application/services/document_service.py
import os
import json
from pathlib import Path
from typing import List, Dict
from app.core.config import get_settings
from app.domain.entities.document import Document
from app.domain.services.document_reader import DocumentReader
from app.domain.services.text_splitter import TextSplitterService
from app.domain.services.vector_store import VectorStoreService
from app.application.dto.document import (
    UploadResponse,
    DocumentDTO,
    DocumentListResponse,
    DeleteResponse,
)


class DocumentService:
    """文档应用服务"""

    METADATA_FILE = "documents_metadata.json"

    def __init__(self):
        self.settings = get_settings()
        self.reader = DocumentReader()
        self.splitter = TextSplitterService()
        self.vector_store = VectorStoreService()

    def _get_metadata_path(self) -> Path:
        """获取元数据文件路径"""
        return Path(self.settings.DOCUMENTS_DIR) / self.METADATA_FILE

    def _load_metadata(self) -> Dict[str, dict]:
        """加载元数据"""
        metadata_path = self._get_metadata_path()
        if metadata_path.exists():
            with open(metadata_path, "r", encoding="utf-8") as f:
                return json.load(f)
        return {}

    def _save_metadata(self, metadata: Dict[str, dict]) -> None:
        """保存元数据"""
        metadata_path = self._get_metadata_path()
        metadata_path.parent.mkdir(parents=True, exist_ok=True)
        with open(metadata_path, "w", encoding="utf-8") as f:
            json.dump(metadata, f, ensure_ascii=False, indent=2, default=str)

    async def upload(self, file_content: bytes, filename: str) -> UploadResponse:
        """
        上传并处理文档

        Args:
            file_content: 文件内容
            filename: 文件名

        Returns:
            上传响应
        """
        # 保存原始文件
        file_path = Path(self.settings.DOCUMENTS_DIR) / filename
        file_path.parent.mkdir(parents=True, exist_ok=True)

        # 处理重名文件
        if file_path.exists():
            base = file_path.stem
            ext = file_path.suffix
            counter = 1
            while file_path.exists():
                file_path = file_path.parent / f"{base}_{counter}{ext}"
                counter += 1

        with open(file_path, "wb") as f:
            f.write(file_content)

        # 创建文档实体
        document = Document.create(
            filename=filename,
            file_path=str(file_path),
        )

        # 读取文档
        documents = self.reader.read(str(file_path))

        # 分段
        chunks = self.splitter.split_documents(documents)

        # 向量化存储
        chunk_count = self.vector_store.store(chunks, document.id)
        document.chunk_count = chunk_count

        # 保存元数据
        metadata = self._load_metadata()
        metadata[document.id] = {
            "id": document.id,
            "filename": document.filename,
            "file_path": document.file_path,
            "chunk_count": document.chunk_count,
            "created_at": document.created_at.isoformat(),
        }
        self._save_metadata(metadata)

        return UploadResponse(
            doc_id=document.id,
            filename=document.filename,
            chunks=chunk_count,
        )

    def list_documents(self) -> DocumentListResponse:
        """获取文档列表"""
        metadata = self._load_metadata()
        documents = [
            DocumentDTO(
                id=doc["id"],
                filename=doc["filename"],
                chunk_count=doc["chunk_count"],
                created_at=doc["created_at"],
            )
            for doc in metadata.values()
        ]
        return DocumentListResponse(documents=documents)

    def delete_document(self, doc_id: str) -> DeleteResponse:
        """删除文档"""
        metadata = self._load_metadata()

        if doc_id not in metadata:
            return DeleteResponse(success=False, message="文档不存在")

        doc_info = metadata[doc_id]

        # 删除向量存储中的数据
        self.vector_store.delete_by_document_id(doc_id)

        # 删除原始文件
        file_path = Path(doc_info["file_path"])
        if file_path.exists():
            file_path.unlink()

        # 删除元数据
        del metadata[doc_id]
        self._save_metadata(metadata)

        return DeleteResponse(success=True, message="删除成功")
