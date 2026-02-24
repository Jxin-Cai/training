# rag_demo/backend/app/core/config.py
from pydantic_settings import BaseSettings
from functools import lru_cache


class Settings(BaseSettings):
    """应用配置"""
    # 智谱 LLM 配置
    ZHIPU_API_KEY: str = ""
    ZHIPU_API_HOST: str = "https://open.bigmodel.cn/api/anthropic"
    ZHIPU_MODEL: str = "glm-5"

    # 阿里百炼 Embedding 配置
    DASHSCOPE_API_KEY: str = ""
    DASHSCOPE_EMBEDDING_MODEL: str = "text-embedding-v3"

    CHROMA_PERSIST_DIR: str = "./data/chroma_db"
    DOCUMENTS_DIR: str = "./data/documents"

    # 文本切片配置
    CHUNK_SIZE: int = 500
    CHUNK_OVERLAP: int = 100

    # 检索配置
    RETRIEVAL_TOP_K: int = 3

    # 会话配置
    MAX_HISTORY_TURNS: int = 10

    class Config:
        env_file = ".env"
        env_file_encoding = "utf-8"


@lru_cache()
def get_settings() -> Settings:
    """获取配置单例"""
    return Settings()
