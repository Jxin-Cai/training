# Story 1.3: 配置部署脚本

Status: ready-for-dev

## Story

As a **运维人员**,
I want **有一键部署和停止脚本**,
so that **可以快速部署和管理应用**.

## Acceptance Criteria

1. **AC1: 部署脚本（Linux/Mac）**
   - Given 前后端项目已创建
   - When 执行 deploy.sh
   - Then 前端执行 npm install && npm run build
   - And 后端执行 mvn clean package
   - And 服务成功启动

2. **AC2: 部署脚本（Windows）**
   - Given 前后端项目已创建
   - When 执行 deploy.cmd
   - Then 前端执行 npm install && npm run build
   - And 后端执行 mvn clean package
   - And 服务成功启动

3. **AC3: 停止脚本（Linux/Mac）**
   - Given 应用正在运行
   - When 执行 shutdown.sh
   - Then 前端服务优雅关闭
   - And 后端服务优雅关闭（SIGTERM，等待最多30秒）
   - And 如超时则强制终止（SIGKILL）

4. **AC4: 停止脚本（Windows）**
   - Given 应用正在运行
   - When 执行 shutdown.cmd
   - Then 前端服务优雅关闭
   - And 后端服务优雅关闭

5. **AC5: 支持单独部署/停止**
   - Given 部署脚本已创建
   - When 执行 ./deploy.sh frontend 或 ./deploy.sh backend
   - Then 只部署指定服务

   - When 执行 ./shutdown.sh frontend 或 ./shutdown.sh backend
   - Then 只停止指定服务

## Tasks / Subtasks

- [ ] Task 1: 创建Linux/Mac部署脚本 (AC: 1)
  - [ ] 1.1 创建 deploy.sh
  - [ ] 1.2 实现前端部署逻辑
  - [ ] 1.3 实现后端部署逻辑
  - [ ] 1.4 添加单独部署支持
  - [ ] 1.5 设置执行权限

- [ ] Task 2: 创建Windows部署脚本 (AC: 2)
  - [ ] 2.1 创建 deploy.cmd
  - [ ] 2.2 实现前端部署逻辑
  - [ ] 2.3 实现后端部署逻辑
  - [ ] 2.4 添加单独部署支持

- [ ] Task 3: 创建Linux/Mac停止脚本 (AC: 3)
  - [ ] 3.1 创建 shutdown.sh
  - [ ] 3.2 实现进程查找逻辑
  - [ ] 3.3 实现优雅关闭逻辑
  - [ ] 3.4 实现超时强制终止
  - [ ] 3.5 添加单独停止支持

- [ ] Task 4: 创建Windows停止脚本 (AC: 4)
  - [ ] 4.1 创建 shutdown.cmd
  - [ ] 4.2 实现进程查找逻辑
  - [ ] 4.3 实现优雅关闭逻辑
  - [ ] 4.4 添加单独停止支持

## Dev Notes

### 部署要求 (from doc/SPEC.md)

**部署架构：**
- 前后端分离部署
- 前端独立部署为静态资源服务
- 后端独立部署为API服务
- 前后端通过HTTP/HTTPS接口通信

### 部署流程 (from doc/SPEC.md)

**前端部署：**
1. 执行 `npm install` 安装依赖
2. 执行 `npm run build` 构建生产版本
3. 启动前端服务（如使用 serve 或 nginx）

**后端部署：**
1. 执行 `mvn clean package` 构建 jar 包
2. 启动 Spring Boot 应用

### 停止流程 (from doc/SPEC.md)

**停止前端：**
1. 查找前端服务进程（通过端口或进程名）
2. 优雅关闭前端服务（发送 SIGTERM 信号）
3. 清理临时文件和日志

**停止后端：**
1. 查找 Spring Boot 应用进程
2. 优雅关闭应用（发送 SIGTERM 信号，触发 Spring 的 shutdown hooks）
3. 等待应用完全停止（最多等待30秒）
4. 如超时则强制终止（SIGKILL）

### 脚本位置

```
java-backend/
├── deploy.sh
├── deploy.cmd
├── shutdown.sh
└── shutdown.cmd

java-frontend/
├── deploy.sh
├── deploy.cmd
├── shutdown.sh
└── shutdown.cmd
```

### 脚本模板示例

**deploy.sh (Linux/Mac):**
```bash
#!/bin/bash

# 一键部署
deploy_all() {
    echo "Deploying frontend..."
    cd java-frontend && npm install && npm run build && npm run serve &
    
    echo "Deploying backend..."
    cd ../java-backend && mvn clean package && java -jar target/*.jar &
}

# 单独部署
case "$1" in
    frontend)
        cd java-frontend && npm install && npm run build && npm run serve &
        ;;
    backend)
        cd java-backend && mvn clean package && java -jar target/*.jar &
        ;;
    *)
        deploy_all
        ;;
esac
```

**shutdown.sh (Linux/Mac):**
```bash
#!/bin/bash

# 一键停止
shutdown_all() {
    echo "Stopping frontend..."
    pkill -f "npm run serve" || true
    
    echo "Stopping backend..."
    pkill -f "java -jar" || true
}

# 单独停止
case "$1" in
    frontend)
        pkill -f "npm run serve" || true
        ;;
    backend)
        pkill -f "java -jar" || true
        ;;
    *)
        shutdown_all
        ;;
esac
```

### References

- [Source: doc/SPEC.md#部署架构]
- [Source: doc/SPEC.md#部署脚本]
- [Source: doc/SPEC.md#停止脚本]
- [Source: _bmad-output/planning-artifacts/architecture.md#Infrastructure & Deployment]

## Dev Agent Record

### Agent Model Used

(待开发时填写)

### Debug Log References

(待开发时填写)

### Completion Notes List

(待开发时填写)

### File List

(待开发时填写)
