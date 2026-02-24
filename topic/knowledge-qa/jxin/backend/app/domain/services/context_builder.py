# rag_demo/backend/app/domain/services/context_builder.py
from langchain_core.messages import BaseMessage
from typing import List


class ContextBuilder:
    """上下文构建领域服务"""

    SYSTEM_PROMPT = """你是一个智能问答助手。请基于以下参考信息回答用户问题。
如果参考信息中没有相关内容，请诚实地说你不知道，不要编造答案。

参考信息：
{context}
"""

    def build(
        self,
        retrieved_chunks: List[str],
        history: List[BaseMessage] = None,
    ) -> str:
        """
        构建完整的上下文

        Args:
            retrieved_chunks: 检索到的文档片段
            history: 历史对话

        Returns:
            构建好的上下文字符串
        """
        # 组装检索内容
        context = "\n\n".join([f"【文档片段 {i+1}】\n{chunk}"
                               for i, chunk in enumerate(retrieved_chunks)])

        return self.SYSTEM_PROMPT.format(context=context)

    def build_full_context(
        self,
        query: str,
        retrieved_chunks: List[str],
    ) -> str:
        """
        构建完整的提示词

        Args:
            query: 用户问题
            retrieved_chunks: 检索到的文档片段

        Returns:
            完整的提示词
        """
        system_prompt = self.build(retrieved_chunks)
        return f"{system_prompt}\n\n用户问题：{query}"
