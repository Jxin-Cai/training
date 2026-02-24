# RAG 知识问答系统

基于 LangChain + 智普 AI 的 RAG 知识问答系统演示项目。

## 功能特性

- 文档上传与管理（支持 PDF、TXT、Markdown）
- 自动文档切片与向量化存储
- 基于向量检索的智能问答
- 多轮对话记忆

## 技术栈

- **后端**: FastAPI + LangChain + Chroma + 智普 AI
- **前端**: Vue 3 + Element Plus + TypeScript

## 快速开始

### 1. 配置环境变量

```bash
cd backend
cp .env.example .env
# 编辑 .env 文件，填入你的 ZHIPU_API_KEY
```

### 2. 一键启动

```bash
./start.sh
```

### 3. 访问应用

- 前端: http://localhost:5173
- 后端 API: http://localhost:8000
- API 文档: http://localhost:8000/docs

### 4. 停止服务

```bash
./stop.sh
```

## 项目结构

```
rag_demo/
├── backend/           # 后端 (FastAPI + DDD)
│   ├── app/
│   │   ├── api/              # API 层
│   │   ├── application/      # 应用层
│   │   ├── domain/           # 领域层
│   │   └── infrastructure/   # 基础设施层
│   └── requirements.txt
├── frontend/          # 前端 (Vue 3)
├── data/              # 数据目录
│   ├── documents/     # 上传的文档
│   └── chroma_db/     # 向量数据库
├── start.sh           # 启动脚本
└── stop.sh            # 停止脚本
```

## 核心组件

### 领域层核心能力

- `DocumentReader`: 文档读取
- `TextSplitterService`: 文本分段
- `VectorStoreService`: 向量化存储
- `RetrieverService`: 向量检索
- `ContextBuilder`: 上下文组装

### 配置说明

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| CHUNK_SIZE | 文本分段大小 | 500 |
| CHUNK_OVERLAP | 分段重叠字符数 | 100 |
| RETRIEVAL_TOP_K | 检索返回数量 | 3 |
| MAX_HISTORY_TURNS | 会话历史轮数 | 10 |
