# CMS Frontend - 前端应用

## 项目简介

基于Vue 3的CMS内容管理系统前端应用，包含前台展示和后台管理功能。

## 技术栈

- **框架**: Vue 3
- **路由**: Vue Router 4
- **UI组件库**: Element Plus
- **HTTP客户端**: Axios
- **Markdown渲染**: markdown-it
- **构建工具**: Vue CLI

## 项目结构

```
frontend/
├── public/
│   └── index.html              # HTML模板
├── src/
│   ├── api/                    # API接口
│   │   ├── category.js         # 分类API
│   │   └── content.js          # 内容API
│   ├── components/             # 公共组件
│   │   └── MarkdownEditor.vue  # Markdown编辑器
│   ├── router/                 # 路由配置
│   │   └── index.js
│   ├── utils/                  # 工具函数
│   │   └── request.js          # HTTP请求封装
│   ├── views/                  # 页面组件
│   │   ├── frontend/           # 前台页面
│   │   │   ├── ContentList.vue # 内容列表
│   │   │   └── ContentDetail.vue # 内容详情
│   │   └── admin/              # 后台页面
│   │       ├── CategoryManage.vue # 分类管理
│   │       └── ContentManage.vue  # 内容管理
│   ├── App.vue                 # 根组件
│   └── main.js                 # 入口文件
├── package.json
└── vue.config.js               # Vue CLI配置
```

## 功能特性

### 前台功能
- **内容列表**: 展示所有已发布内容，按发布时间降序排列
- **内容详情**: 展示Markdown渲染后的HTML内容
- **导航**: 可跳转到后台管理

### 后台功能
- **分类管理**: 增删改查分类，支持删除前关联检查
- **内容管理**: 
  - 增删改查内容
  - Markdown编辑器（左右分栏，实时预览）
  - 状态管理（草稿/发布）
  - 状态筛选
- **导航**: 可返回前台，或在分类/内容管理间切换

## 路由设计

### 前台路由
- `/` - 内容列表页
- `/content/:id` - 内容详情页

### 后台路由
- `/admin/categories` - 分类管理页
- `/admin/contents` - 内容管理页

## 本地开发

### 安装依赖

```bash
cd frontend
npm install
```

### 启动开发服务器

```bash
npm run serve
```

访问：http://localhost:3000

### 构建生产版本

```bash
npm run build
```

构建产物位于 `dist/` 目录。

## 部署说明

### 使用http-server部署

```bash
# 全局安装http-server（如未安装）
npm install -g http-server

# 构建项目
npm run build

# 启动服务
http-server dist -p 3000
```

### 使用nginx部署

```nginx
server {
    listen 3000;
    server_name localhost;
    root /path/to/frontend/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 开发注意事项

1. **Markdown渲染**: 前端使用markdown-it实时预览，后端使用commonmark-java渲染HTML
2. **跨域配置**: 开发环境通过Vue CLI的proxy配置，生产环境需要后端CORS支持
3. **状态管理**: 内容状态枚举必须与后端保持一致（DRAFT/PUBLISHED）
4. **API基础路径**: 生产环境需要修改`src/utils/request.js`中的baseURL

## 核心组件说明

### MarkdownEditor
- 左右分栏布局
- 左侧：Markdown编辑区
- 右侧：实时预览区（使用markdown-it渲染）
- 支持v-model双向绑定

### ContentList
- 卡片式展示内容
- 显示标题、分类、发布时间
- 点击卡片跳转到详情页

### ContentDetail
- 展示渲染后的HTML内容
- 包含标题、分类、发布时间等元信息

### CategoryManage
- 表格展示分类列表
- 支持新增、编辑、删除操作
- 删除时检查是否有关联内容

### ContentManage
- 表格展示内容列表
- 支持状态筛选（全部/草稿/已发布）
- 集成Markdown编辑器
- 支持发布/取消发布操作
