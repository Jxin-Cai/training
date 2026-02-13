# CMS - 多级分类与富文本内容管理系统

一个基于 Spring Boot + Vue 3 的内容管理系统，支持多级分类、富文本编辑和图片上传。

## 功能特性

✅ 多级分类浏览 (前台)
✅ 后台分类管理
✅ 富文本编辑器 + 图片上传
✅ 前后台导航

## 技术栈

- **后端**: Spring Boot 3.2 + H2 + Thumbnailator + Tika
- **前端**: Vue 3 + Element Plus + TipTap + Vite

## 快速开始

### 安装依赖
```bash
# 后端
cd backend && mvn install

# 前端
cd frontend && npm install
```

### 启动服务
```bash
# 后端 (端口 8080)
cd backend && mvn spring-boot:run

# 前端 (端口 5173)
cd frontend && npm run dev
```

### 访问应用
- 前台首页: http://localhost:5173
- 管理后台: http://localhost:5173/admin
- H2控制台: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:cmsdb, 用户: sa)

## 部署

```bash
./deploy.sh          # 部署全部
./deploy.sh backend  # 只部署后端
./shutdown.sh        # 停止服务
```

## 详细文档

- `specs/001-multi-level-categories/quickstart.md` - 快速启动指南
- `specs/001-multi-level-categories/data-model.md` - 数据模型
- `specs/001-multi-level-categories/contracts/openapi.yaml` - API规范
