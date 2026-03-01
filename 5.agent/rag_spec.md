# RAG Agent — 技术规格

## 1. 目录结构

```
5.agent/
├── .env                   # 本地密钥（不提交 git）
├── .gitignore
├── requirements.txt
├── rag_agent.py           # 入口：文档加载、向量化、agent、REPL
├── model.py               # ChatOpenAI 工厂
├── embedding.py           # OpenAIEmbeddings 工厂
├── docs/                  # 存放待检索的 PDF 文件
└── .vectorstore/          # FAISS 本地向量存储缓存（自动生成，不提交 git）
```

---

## 2. 组件说明

### `rag_agent.py`

| 函数 | 职责 |
|------|------|
| `load_and_vectorize_documents()` | 加载 `docs/` 中所有 PDF，分割后用 `embedding.py` 向量化，存入 FAISS；若 `.vectorstore/` 已存在则直接加载 |
| `search_documents(query, k=3)` | LangChain `@tool`，对向量存储执行相似度搜索，返回格式化文本（含来源、页码） |
| `get_document_info()` | LangChain `@tool`，返回向量总数、维度、PDF 文件列表 |
| `create_rag_agent()` | 用 `model.py` 创建 `ChatOpenAI`，注册两个 tool，返回 agent |
| `ask_rag(q)` | 调用 agent 并返回最后一条消息内容 |
| `main()` | 初始化向量存储，启动交互式 REPL |

### `embedding.py`

- **类**：`OpenAIEmbeddings`（来自 `langchain-openai`）
- **工厂函数**：`get_embeddings(model=None) -> OpenAIEmbeddings`
- 设置 `check_embedding_ctx_length=False`（兼容非 OpenAI 的 embedding 提供商，直接传文本而非 token）

### `model.py`

- **类**：`ChatOpenAI`（来自 `langchain-openai`）
- **工厂函数**：`get_llm(model=None) -> ChatOpenAI`
- **参数**：`temperature=0.1`，`max_tokens=2048`

---

## 3. 文档处理流程

```
docs/*.pdf
  └─ PyPDFLoader（逐页加载）
       └─ RecursiveCharacterTextSplitter
            chunk_size=1024, chunk_overlap=128
            separators=["\n\n", "\n", " ", ""]
               └─ OpenAIEmbeddings（向量化）
                    └─ FAISS（存储 / 检索）
                         └─ .vectorstore/（本地持久化）
```

---

## 4. 环境变量

| 变量名 | 必需 | 说明 |
|--------|------|------|
| `OPENAI_API_KEY` | ✅ | OpenAI 兼容 API 密钥（聊天 + embedding 共用） |
| `OPENAI_BASE_URL` | ❌ | 自定义接口地址（代理 / 兼容接口） |
| `OPENAI_MODEL` | ❌ | 聊天模型名称，默认 `qwen/qwen3.5-flash-02-23` |
| `OPENAI_EMBEDDING_MODEL` | ❌ | Embedding 模型名称，默认 `qwen/qwen3-embedding-8b` |

`.env` 模板：

```env
OPENAI_API_KEY=sk-...
OPENAI_BASE_URL=https://your-proxy.example.com/v1
OPENAI_MODEL=qwen/qwen3.5-flash-02-23
OPENAI_EMBEDDING_MODEL=qwen/qwen3-embedding-8b
```

---

## 5. 依赖

```
langchain>=0.3.0
langchain-openai>=0.2.0
langchain-core>=0.3.0
langchain-community>=0.0.0
langchain-text-splitters>=0.0.0
python-dotenv>=1.0.0
pypdf>=4.0.0
faiss-cpu>=1.7.0
```

安装：

```bash
python -m venv venv
source venv/bin/activate
pip install -r requirements.txt
```

---

## 6. 运行

```bash
# 将 PDF 文件放入 docs/ 目录后执行
venv/bin/python rag_agent.py
```

首次运行会自动向量化并缓存至 `.vectorstore/`，后续启动直接加载缓存。若要重新向量化，删除 `.vectorstore/` 目录即可。
