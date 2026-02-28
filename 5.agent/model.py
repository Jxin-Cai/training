import os

from langchain_openai import ChatOpenAI


def get_llm(model: str = None) -> ChatOpenAI:
    api_key = os.getenv("OPENAI_API_KEY")
    if not api_key:
        raise SystemExit("请设置 OPENAI_API_KEY")

    if model is None:
        model = os.getenv("OPENAI_MODEL", "qwen/qwen3.5-flash-02-23")

    kwargs: dict = dict(
        model=model,
        api_key=api_key,
        temperature=0.1,
        max_tokens=2048,
    )
    if base_url := os.getenv("OPENAI_BASE_URL"):
        kwargs["base_url"] = base_url

    return ChatOpenAI(**kwargs)
