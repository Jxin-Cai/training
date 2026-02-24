# rag_demo/backend/app/application/services/chat_service.py
"""
对话应用服务 - 基于 LangGraph Agent 模式

特点：
1. 使用 langgraph.prebuilt.create_react_agent 创建智能代理
2. RAG 作为 Tool，按需调用（中间件模式）
3. 内置对话记忆（通过 checkpointer）
4. 详细的日志输出
"""
import logging

from langgraph.prebuilt import create_react_agent
from langgraph.checkpoint.memory import MemorySaver
from langchain_core.messages import HumanMessage, AIMessage

from app.infrastructure.llm.zhipu_llm import get_llm
from app.infrastructure.tools.rag_tool import retrieve_knowledge
from app.application.dto.chat import (
    ChatRequest,
    ChatResponse,
    ChatHistoryResponse,
    MessageDTO,
)

logger = logging.getLogger(__name__)


# Agent 系统提示词
AGENT_PROMPT = """你是一个智能知识问答助手。

## 你的能力
1. 你可以访问知识库检索工具 `retrieve_knowledge`，用于查找用户上传的文档中的相关信息
2. 当用户的问题可能与知识库内容相关时，先使用检索工具查找
3. 如果知识库中没有相关信息，你可以基于自己的知识回答
4. 如果用户只是闲聊，直接回复即可，不需要使用工具

## 工具使用原则
- **谨慎使用工具**：只有当问题确实需要参考知识库时才调用工具
- **不过度依赖**：如果工具返回"没有找到相关内容"，自信地用你的知识回答
- **诚实透明**：如果你不确定或没有相关信息，直接说明

## 对话风格
- 友好、专业、简洁
- 如果使用了知识库信息，可以提及"根据上传的文档..."""


class ChatService:
    """对话应用服务 - LangGraph Agent 模式"""

    def __init__(self):
        self.llm = get_llm()
        self._agents: dict = {}  # session_id -> (agent, checkpointer)
        logger.info("[ChatService] 初始化完成，使用 LangGraph Agent 模式")

    def _get_or_create_agent(self, session_id: str):
        """获取或创建指定会话的 Agent"""
        if session_id in self._agents:
            return self._agents[session_id]

        logger.info(f"[ChatService] 为会话 {session_id} 创建新 Agent")

        # 创建内存检查点（用于对话记忆）
        checkpointer = MemorySaver()

        # 创建工具列表
        tools = [retrieve_knowledge]

        # 创建 Agent
        agent = create_react_agent(
            model=self.llm,
            tools=tools,
            prompt=AGENT_PROMPT,
            checkpointer=checkpointer,
        )

        self._agents[session_id] = (agent, checkpointer)
        return agent, checkpointer

    def chat(self, request: ChatRequest) -> ChatResponse:
        """
        处理对话请求

        Args:
            request: 对话请求

        Returns:
            对话响应
        """
        session_id = request.session_id
        user_message = request.message

        logger.info(f"[ChatService] 收到消息 - Session: {session_id}")
        logger.info(f"[ChatService] 消息内容: {user_message}")

        try:
            # 获取 Agent
            agent, checkpointer = self._get_or_create_agent(session_id)

            # 配置（使用 thread_id 来区分会话）
            config = {"configurable": {"thread_id": session_id}}

            # 执行 Agent
            logger.info("[ChatService] 开始执行 Agent...")

            result = agent.invoke(
                {"messages": [HumanMessage(content=user_message)]},
                config=config,
            )

            # 获取最后一条 AI 消息
            messages = result.get("messages", [])
            ai_reply = "抱歉，我无法处理这个请求。"

            for msg in reversed(messages):
                if isinstance(msg, AIMessage):
                    ai_reply = msg.content
                    break

            logger.info(f"[ChatService] Agent 回复长度: {len(ai_reply)} 字符")

            return ChatResponse(reply=ai_reply)

        except Exception as e:
            logger.error(f"[ChatService] 处理消息时出错: {e}", exc_info=True)
            return ChatResponse(
                reply=f"抱歉，处理您的请求时发生错误: {str(e)}"
            )

    def get_history(self, session_id: str) -> ChatHistoryResponse:
        """获取对话历史"""
        logger.info(f"[ChatService] 获取历史 - Session: {session_id}")

        if session_id not in self._agents:
            return ChatHistoryResponse(messages=[])

        try:
            agent, checkpointer = self._agents[session_id]
            config = {"configurable": {"thread_id": session_id}}

            # 获取状态
            state = agent.get_state(config)
            messages = []

            for msg in state.values.get("messages", []):
                if isinstance(msg, HumanMessage):
                    messages.append(MessageDTO(role="user", content=msg.content))
                elif isinstance(msg, AIMessage):
                    messages.append(MessageDTO(role="assistant", content=msg.content))

            logger.info(f"[ChatService] 返回 {len(messages)} 条历史消息")
            return ChatHistoryResponse(messages=messages)

        except Exception as e:
            logger.error(f"[ChatService] 获取历史出错: {e}")
            return ChatHistoryResponse(messages=[])

    def clear_history(self, session_id: str) -> bool:
        """清空对话历史"""
        logger.info(f"[ChatService] 清空历史 - Session: {session_id}")

        # 清理 Agent 缓存
        if session_id in self._agents:
            del self._agents[session_id]

        return True
