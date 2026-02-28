import os

from langchain_anthropic import ChatAnthropic


def get_llm(model: str = "claude-3-5-haiku-20241022") -> ChatAnthropic:
    api_key = os.getenv("ANTHROPIC_API_KEY")
    if not api_key:
        raise SystemExit("请设置 ANTHROPIC_API_KEY")

    kwargs: dict = dict(
        model=model,
        api_key=api_key,
        temperature=0.1,
        max_tokens=2048,
    )
    if base_url := os.getenv("ANTHROPIC_BASE_URL"):
        kwargs["anthropic_api_url"] = base_url

    return ChatAnthropic(**kwargs)
