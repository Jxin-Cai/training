# QA Agent — 技术规格

## 1. 目录结构

```
5.agent/
├── .env                   # 本地密钥（不提交 git）
├── .gitignore
├── requirements.txt
├── qa_agent.py            # 入口：创建 agent、REPL、ask()
└── model.py               # ChatOpenAI 工厂
```

---

## 2. 组件说明

### `qa_agent.py`

| 职责 | 说明 |
|------|------|
| 加载 `.env` | `python-dotenv` 从同目录 `.env` 读取环境变量 |
| 初始化模型 | 调用 `model.get_llm()` |
| 创建 agent | `langchain.agents.create_agent(model, tools=[], system_prompt=...)` |
| `ask(q)` | 调用 `agent.invoke({"messages": [HumanMessage(q)]})` 并返回最后一条消息内容 |
| REPL | 交互式问答循环，`quit` / `exit` / `q` 或 `Ctrl+C` 退出 |

### `model.py`

- **类**：`ChatOpenAI`（来自 `langchain-openai`）
- **工厂函数**：`get_llm(model=None) -> ChatOpenAI`
- **参数**：`temperature=0.1`，`max_tokens=2048`

---

## 3. 环境变量

| 变量名 | 必需 | 说明 |
|--------|------|------|
| `OPENAI_API_KEY` | ✅ | OpenAI 兼容 API 密钥 |
| `OPENAI_BASE_URL` | ❌ | 自定义接口地址（代理 / 兼容接口），不设则使用官方地址 |
| `OPENAI_MODEL` | ❌ | 聊天模型名称，默认 `qwen/qwen3.5-flash-02-23` |

`.env` 模板：

```env
OPENAI_API_KEY=sk-...
OPENAI_BASE_URL=https://your-proxy.example.com/v1
OPENAI_MODEL=qwen/qwen3.5-flash-02-23
```

---

## 4. 依赖

```
langchain>=0.3.0
langchain-openai>=0.2.0
langchain-core>=0.3.0
python-dotenv>=1.0.0
```

安装：

```bash
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

---

## 5. 运行

```bash
venv/bin/python qa_agent.py
```
