import os

from langchain_openai import ChatOpenAI


def get_llm(model: str = "gpt-4o-mini") -> ChatOpenAI:
    api_key = os.getenv("OPENAI_API_KEY")
    if not api_key:
        raise SystemExit("请设置 OPENAI_API_KEY")

    kwargs: dict = dict(
        model=model,
        api_key=api_key,
        temperature=0.1,
        max_tokens=2048,
    )
    if base_url := os.getenv("OPENAI_BASE_URL"):
        kwargs["base_url"] = base_url

    return ChatOpenAI(**kwargs)
