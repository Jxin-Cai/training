# QA Agent (LangChain + OpenRouter)

基于 LangChain 的问答 Agent，后端使用 OpenRouter 的 **qwen/qwen3.5-flash-02-23** 模型。

## 环境准备

1. Python 3.10+

2. 安装依赖：

```bash
cd 5.agent
pip install -r requirements.txt
```

3. 配置 API Key：在项目目录下创建 `.env` 文件（若尚未创建），写入：

```env
OPENROUTER_API_KEY=你的 OpenRouter API Key
```

**注意**：`.env` 已加入 `.gitignore`，请勿提交到版本库。

## 运行

交互式问答（REPL）：

```bash
python qa_agent.py
```

在代码中调用单次问答：

```python
from qa_agent import ask

answer = ask("什么是机器学习？")
print(answer)
```

## 依赖说明

- `langchain` / `langchain-core`：LangChain 核心
- `langchain-openai`：通过 OpenRouter 的 OpenAI 兼容 API 调用 Qwen
- `python-dotenv`：从 `.env` 加载 `OPENROUTER_API_KEY`

## 模型

- 模型 ID：`qwen/qwen3.5-flash-02-23`
- 通过 OpenRouter 统一 API 调用
