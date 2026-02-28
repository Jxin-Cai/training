import os

from langchain_openrouter import ChatOpenRouter


def get_llm(model: str = "qwen/qwen3.5-flash-02-23") -> ChatOpenRouter:
    api_key = os.getenv("OPENROUTER_API_KEY")
    if not api_key:
        raise SystemExit("请设置 OPENROUTER_API_KEY")

    return ChatOpenRouter(
        model=model,
        api_key=api_key,
        temperature=0.1,
        max_tokens=2048,
    )
