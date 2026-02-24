# rag_demo/backend/app/infrastructure/tools/rag_tool.py
"""
RAG 检索工具 - 作为 Agent 的中间件使用

特点：
- 匹配到内容时：增强上下文，提供参考信息
- 未匹配到内容时：返回空，Agent 会正常回答（不做任何增强）
"""
import logging
from typing import Optional

from langchain_core.tools import tool
from pydantic import BaseModel, Field

from app.infrastructure.vectorstore.chroma_store import get_vector_store

logger = logging.getLogger(__name__)


class RAGInput(BaseModel):
    """RAG 检索工具的输入"""
    query: str = Field(description="用户的问题或查询内容")


class RAGOutput(BaseModel):
    """RAG 检索工具的输出"""
    has_context: bool = Field(description="是否找到相关上下文")
    context: str = Field(description="检索到的上下文内容，如果没有则为空")
    sources: list[str] = Field(default_factory=list, description="来源文档列表")


@tool(response_format="content_and_artifact")
def retrieve_knowledge(query: str) -> tuple[str, RAGOutput]:
    """
    从知识库中检索与用户问题相关的信息。

    当用户的问题可能与已上传的文档相关时，使用此工具来获取参考信息。
    如果没有找到相关内容，会返回空结果，你可以基于自己的知识回答。

    Args:
        query: 用户的问题或需要查询的内容

    Returns:
        检索到的相关内容和来源信息
    """
    logger.info(f"[RAG Tool] 开始检索: {query[:50]}...")

    try:
        vector_store = get_vector_store()
        docs = vector_store.similarity_search(query, k=3)

        if not docs:
            logger.info("[RAG Tool] 未找到相关文档")
            output = RAGOutput(
                has_context=False,
                context="",
                sources=[]
            )
            return "知识库中没有找到相关内容。请基于你自己的知识回答用户问题。", output

        # 找到了相关文档
        logger.info(f"[RAG Tool] 找到 {len(docs)} 个相关文档片段")

        # 构建上下文
        context_parts = []
        sources = []

        for i, doc in enumerate(docs, 1):
            content = doc.page_content
            source = doc.metadata.get("filename", "未知来源")
            context_parts.append(f"【参考文档 {i}】(来源: {source})\n{content}")
            sources.append(source)

        context = "\n\n".join(context_parts)

        output = RAGOutput(
            has_context=True,
            context=context,
            sources=sources
        )

        result_text = f"从知识库中找到以下相关内容：\n\n{context}\n\n请基于以上内容回答用户问题。如果以上内容不足以回答问题，可以补充你自己的知识。"

        logger.info(f"[RAG Tool] 检索完成，返回 {len(sources)} 个来源")
        return result_text, output

    except Exception as e:
        logger.error(f"[RAG Tool] 检索出错: {e}", exc_info=True)
        output = RAGOutput(
            has_context=False,
            context="",
            sources=[]
        )
        return f"知识库检索时发生错误: {str(e)}。请基于你自己的知识回答。", output
