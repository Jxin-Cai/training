# rag_demo/backend/app/main.py
"""
RAG 知识问答系统 - FastAPI 主入口

特点：
1. 详细的日志配置
2. 全局异常处理
3. CORS 支持
4. API 路由注册
"""
import logging
import sys
from contextlib import asynccontextmanager

from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

from app.api.routes import chat, document


def setup_logging():
    """配置日志系统"""
    # 创建格式化器
    formatter = logging.Formatter(
        fmt="%(asctime)s | %(levelname)-8s | %(name)s | %(message)s",
        datefmt="%Y-%m-%d %H:%M:%S"
    )

    # 配置根日志器
    root_logger = logging.getLogger()
    root_logger.setLevel(logging.DEBUG)  # 演示项目，使用 DEBUG 级别

    # 控制台处理器
    console_handler = logging.StreamHandler(sys.stdout)
    console_handler.setLevel(logging.DEBUG)
    console_handler.setFormatter(formatter)
    root_logger.addHandler(console_handler)

    # 设置第三方库的日志级别（减少噪音）
    logging.getLogger("httpx").setLevel(logging.WARNING)
    logging.getLogger("httpcore").setLevel(logging.WARNING)
    logging.getLogger("chromadb").setLevel(logging.WARNING)
    logging.getLogger("urllib3").setLevel(logging.WARNING)

    return logging.getLogger(__name__)


# 初始化日志
logger = setup_logging()


@asynccontextmanager
async def lifespan(app: FastAPI):
    """应用生命周期管理"""
    logger.info("=" * 60)
    logger.info("🚀 RAG 知识问答系统启动中...")
    logger.info("=" * 60)

    # 可以在这里初始化资源
    yield

    logger.info("🛑 RAG 知识问答系统关闭中...")


app = FastAPI(
    title="RAG Knowledge QA System",
    description="基于 RAG 的知识问答系统 API (Agent 模式)",
    version="1.0.0",
    lifespan=lifespan,
)


# 全局异常处理
@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    """全局异常处理器"""
    logger.error(f"未处理的异常: {exc}", exc_info=True)
    return JSONResponse(
        status_code=500,
        content={
            "detail": str(exc),
            "type": type(exc).__name__,
        }
    )


# CORS 配置
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 生产环境应限制具体域名
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 注册路由
app.include_router(chat.router, prefix="/api")
app.include_router(document.router, prefix="/api")


@app.get("/")
async def root():
    """健康检查"""
    logger.debug("健康检查: /")
    return {"status": "ok", "message": "RAG Demo API is running"}


@app.get("/health")
async def health():
    """健康检查端点"""
    return {"status": "healthy"}
