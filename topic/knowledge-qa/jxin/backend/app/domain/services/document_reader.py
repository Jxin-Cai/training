# rag_demo/backend/app/domain/services/document_reader.py
"""文档读取领域服务 - 支持 PDF 转 Markdown"""
from langchain_community.document_loaders import TextLoader
from langchain_core.documents import Document
from pathlib import Path
from typing import List
import logging

logger = logging.getLogger(__name__)


class MarkdownLoader:
    """Markdown 文件加载器 - 直接读取文本"""

    def __init__(self, file_path: str):
        self.file_path = file_path

    def load(self) -> List[Document]:
        with open(self.file_path, "r", encoding="utf-8") as f:
            content = f.read()

        return [
            Document(
                page_content=content,
                metadata={"source": self.file_path},
            )
        ]


class PDFToMarkdownLoader:
    """PDF 转 Markdown 加载器 - 使用 pymupdf"""

    def __init__(self, file_path: str):
        self.file_path = file_path

    def load(self) -> List[Document]:
        """加载 PDF 并转换为 Markdown 格式"""
        try:
            import fitz  # pymupdf
        except ImportError:
            raise ImportError(
                "pymupdf package not found, please install it with: pip install pymupdf"
            )

        documents = []
        doc = fitz.open(self.file_path)

        for page_num, page in enumerate(doc, start=1):
            # 提取文本块，保留格式
            text_blocks = page.get_text("dict", flags=fitz.TEXT_PRESERVE_WHITESPACE)

            markdown_content = self._convert_to_markdown(text_blocks, page_num)

            if markdown_content.strip():
                documents.append(
                    Document(
                        page_content=markdown_content,
                        metadata={
                            "source": self.file_path,
                            "page": page_num,
                            "total_pages": len(doc),
                        },
                    )
                )

        doc.close()
        logger.info(f"PDF 转 Markdown 完成: {self.file_path}, 共 {len(documents)} 页")
        return documents

    def _convert_to_markdown(self, text_blocks: dict, page_num: int) -> str:
        """将 PDF 文本块转换为 Markdown"""
        lines = [f"## 第 {page_num} 页\n"]

        for block in text_blocks.get("blocks", []):
            if block.get("type") != 0:  # 跳过图片等非文本块
                continue

            block_text = ""
            for line in block.get("lines", []):
                line_text = ""
                for span in line.get("spans", []):
                    text = span.get("text", "")
                    size = span.get("size", 12)
                    flags = span.get("flags", 0)

                    # 根据字体大小和样式添加 Markdown 格式
                    if size > 18:  # 大标题
                        text = f"# {text}"
                    elif size > 14:  # 小标题
                        text = f"## {text}"
                    elif flags & 16:  # 粗体
                        text = f"**{text}**"
                    elif flags & 2:  # 斜体
                        text = f"*{text}*"

                    line_text += text

                if line_text.strip():
                    block_text += line_text + "\n"

            if block_text.strip():
                lines.append(block_text)

        return "\n".join(lines)


class DocumentReader:
    """文档读取领域服务"""

    SUPPORTED_EXTENSIONS = {
        ".pdf": PDFToMarkdownLoader,
        ".txt": TextLoader,
        ".md": MarkdownLoader,
    }

    def read(self, file_path: str) -> List[Document]:
        """
        读取文档内容

        Args:
            file_path: 文件路径

        Returns:
            文档内容列表
        """
        path = Path(file_path)
        extension = path.suffix.lower()

        if extension not in self.SUPPORTED_EXTENSIONS:
            raise ValueError(f"不支持的文件格式: {extension}")

        loader_class = self.SUPPORTED_EXTENSIONS[extension]
        loader = loader_class(file_path)
        return loader.load()

    def get_content(self, file_path: str) -> str:
        """
        获取文档纯文本内容

        Args:
            file_path: 文件路径

        Returns:
            文档文本内容
        """
        documents = self.read(file_path)
        return "\n".join([doc.page_content for doc in documents])
