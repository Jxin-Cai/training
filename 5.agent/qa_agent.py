#!/usr/bin/env python3
"""QA Agent：LangChain + OpenRouter (Qwen 3.5 Flash)，使用 create_agent。"""

from pathlib import Path

from dotenv import load_dotenv
from langchain.agents import create_agent
from langchain_core.messages import HumanMessage
from models.open_router import get_llm

load_dotenv(Path(__file__).resolve().parent / ".env")
model = get_llm()

agent = create_agent(
    model,
    tools=[],
    system_prompt="你是一个友好、准确的问答助手。请用简洁清晰的中文回答。",
)


def ask(q: str) -> str:
    out = agent.invoke({"messages": [HumanMessage(content=q)]})
    return out["messages"][-1].content


if __name__ == "__main__":
    print("QA Agent (输入 quit 退出)\n")
    while True:
        try:
            s = input("你: ").strip()
        except (EOFError, KeyboardInterrupt):
            break
        if not s or s.lower() in ("quit", "exit", "q"):
            break
        print("助手:", ask(s), "\n")
    print("再见。")
