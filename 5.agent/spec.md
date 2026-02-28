# QA Agent — 技术规格 (spec.md)

## 1. 目录结构

```
5.agent/
├── .env                   # 本地密钥（不提交 git）
├── .gitignore             # 忽略 .env、__pycache__、venv/
├── requirements.txt       # Python 依赖清单
├── spec.md                # 本文件
├── qa_agent.py            # 入口：创建 agent、REPL、ask()
├── models/                # 模型工厂包
│   ├── __init__.py
│   ├── open_router.py     # ChatOpenRouter（OpenRouter）
│   ├── openai.py          # ChatOpenAI（OpenAI 兼容）
│   └── anthropic.py       # ChatAnthropic（Anthropic）
└── venv/                  # Python 虚拟环境（不提交 git）
```

---

## 2. 组件说明

### 2.1 `qa_agent.py` — 入口

| 职责 | 说明 |
|------|------|
| 加载 `.env` | `python-dotenv` 从同目录 `.env` 读取环境变量 |
| 初始化模型 | 调用 `models.open_router.get_llm()`（可替换为其他工厂） |
| 创建 agent | `langchain.agents.create_agent(model, tools=[], system_prompt=...)` |
| `ask(q)` | 调用 `agent.invoke({"messages": [HumanMessage(q)]})` 并返回最后一条消息内容 |
| REPL | 交互式问答循环，`quit` / `exit` / `q` 或 `Ctrl+C` 退出 |

### 2.2 `models/open_router.py`

- **类**：`ChatOpenRouter`（来自 `langchain-openrouter`）
- **工厂函数**：`get_llm(model="qwen/qwen3.5-flash-02-23") -> ChatOpenRouter`
- **必需环境变量**：`OPENROUTER_API_KEY`
- **参数**：`temperature=0.1`，`max_tokens=2048`

### 2.3 `models/openai.py`

- **类**：`ChatOpenAI`（来自 `langchain-openai`）
- **工厂函数**：`get_llm(model="gpt-4o-mini") -> ChatOpenAI`
- **必需环境变量**：`OPENAI_API_KEY`
- **可选环境变量**：`OPENAI_BASE_URL`（不设则使用 OpenAI 官方默认地址）
- **参数**：`temperature=0.1`，`max_tokens=2048`

### 2.4 `models/anthropic.py`

- **类**：`ChatAnthropic`（来自 `langchain-anthropic`）
- **工厂函数**：`get_llm(model="claude-3-5-haiku-20241022") -> ChatAnthropic`
- **必需环境变量**：`ANTHROPIC_API_KEY`
- **可选环境变量**：`ANTHROPIC_BASE_URL`（映射至 `anthropic_api_url`，不设则使用官方默认地址）
- **参数**：`temperature=0.1`，`max_tokens=2048`

---

## 3. 环境变量

| 变量名 | 必需 | 所属组件 | 说明 |
|--------|------|----------|------|
| `OPENROUTER_API_KEY` | ✅ | `models/open_router.py` | OpenRouter API 密钥 |
| `OPENAI_API_KEY` | ✅（使用 OpenAI 时） | `models/openai.py` | OpenAI API 密钥 |
| `OPENAI_BASE_URL` | ❌ | `models/openai.py` | 自定义 OpenAI 接口地址（代理 / 兼容接口） |
| `ANTHROPIC_API_KEY` | ✅（使用 Anthropic 时） | `models/anthropic.py` | Anthropic API 密钥 |
| `ANTHROPIC_BASE_URL` | ❌ | `models/anthropic.py` | 自定义 Anthropic 接口地址（代理） |

所有变量从 `.env` 加载，该文件已被 `.gitignore` 排除，**不得提交到版本库**。

`.env` 模板：

```env
# 必填（当前使用 OpenRouter）
OPENROUTER_API_KEY=sk-or-v1-...

# 使用 OpenAI 时填写
# OPENAI_API_KEY=sk-...
# OPENAI_BASE_URL=https://your-proxy.example.com/v1

# 使用 Anthropic 时填写
# ANTHROPIC_API_KEY=sk-ant-...
# ANTHROPIC_BASE_URL=https://your-proxy.example.com
```

---

## 4. 依赖与安装要求

- 不依赖 langgraph，仅使用 langchain

安装命令（使用项目内 venv）：

```bash
python -m venv venv
source venv/bin/activate      # Windows: venv\Scripts\activate
pip install -r requirements.txt
```

---

## 5. 运行

```bash
# 交互式 REPL
venv/bin/python qa_agent.py
```

---

## 6. 切换模型后端

在 `qa_agent.py` 中将导入行替换为对应工厂即可：

```python
# OpenRouter（默认）
from models.open_router import get_llm

# OpenAI
from models.openai import get_llm

# Anthropic
from models.anthropic import get_llm
```

无需修改 agent 创建逻辑，三个工厂均返回兼容 `BaseChatModel` 的实例。
