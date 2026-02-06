## 🏗️ 技术架构

### 前端技术栈

- **框架**：Vue.js
- **构建工具**：npm run build

### 后端技术栈

- **语言**：Python 3.11+
- **框架**：FastAPI
- **包管理**：uv
- **DB**：直接在内存存储

### 部署架构

- **部署方式**：前后端分离部署
- **架构说明**：
  - 前端独立部署为静态资源服务
  - 后端独立部署为 API 服务
  - 前后端通过 HTTP/HTTPS 接口通信

#### 部署脚本

提供 shell（Linux/Mac）和 cmd（Windows）部署脚本，支持：

1. **一键部署**：同时部署前端和后端
   ```bash
   # Linux/Mac
   ./deploy.sh
   
   # Windows
   deploy.cmd
   ```

2. **单独部署前端**：
   ```bash
   # Linux/Mac
   ./deploy.sh frontend
   
   # Windows
   deploy.cmd frontend
   ```

3. **单独部署后端**：
   ```bash
   # Linux/Mac
   ./deploy.sh backend
   
   # Windows
   deploy.cmd backend
   ```

#### 部署流程

**前端部署**：
1. 执行 `npm install` 安装依赖
2. 执行 `npm run build` 构建生产版本
3. 启动前端服务（如使用 serve 或 nginx）

**后端部署**：
1. 执行 `uv sync` 安装依赖
2. 执行 `uv run uvicorn main:app --host 0.0.0.0 --port 8000` 启动 FastAPI 应用

#### 停止脚本

提供 shell（Linux/Mac）和 cmd（Windows）停止脚本，支持：

1. **一键停止**：同时停止前端和后端
   ```bash
   # Linux/Mac
   ./shutdown.sh
   
   # Windows
   shutdown.cmd
   ```

2. **单独停止前端**：
   ```bash
   # Linux/Mac
   ./shutdown.sh frontend
   
   # Windows
   shutdown.cmd frontend
   ```

3. **单独停止后端**：
   ```bash
   # Linux/Mac
   ./shutdown.sh backend
   
   # Windows
   shutdown.cmd backend
   ```

#### 停止流程

**停止前端**：
1. 查找前端服务进程（通过端口或进程名）
2. 优雅关闭前端服务（发送 SIGTERM 信号）
3. 清理临时文件和日志

**停止后端**：
1. 查找 Uvicorn 进程（通过端口8000或进程名）
2. 优雅关闭应用（发送 SIGTERM 信号，触发 FastAPI 的 shutdown 事件）
3. 等待应用完全停止（最多等待30秒）
4. 如超时则强制终止（SIGKILL）

---

## 📐 开发规范

### 后端架构规范

#### 1. DDD 分层架构

采用领域驱动设计（DDD），分层如下：

- **Presentation Layer（展示层）**：Router、Schema（Pydantic Models）
- **Application Layer（应用层）**：Service、Application Service
- **Domain Layer（领域层）**：
  - Entity（实体）
  - Value Object（值对象，使用 dataclass 或 Pydantic）
  - Domain Service（领域服务）
  - Repository Interface（仓储接口，使用 Protocol）
- **Infrastructure Layer（基础设施层）**：
  - Repository Implementation（仓储实现）
  - 外部服务调用（GLM API）
  - 向量存储实现

#### 2. DDD Model 定义规范

- 使用 **DDD 战术模式** 定义模型：
  - Entity：使用 `dataclass` 或 `Pydantic BaseModel` 定义具有唯一标识的对象
  - Value Object：使用 `@dataclass(frozen=True)` 或 `Pydantic` 定义不可变值对象
  - Aggregate Root：聚合根
- 遵循 **充血模型**，而非贫血模型
- 使用类型注解（Type Hints）增强代码可读性和类型安全

#### 3. 面向对象编程原则

- 遵循 **SOLID 原则**
- 合理使用设计模式
- 避免面向过程式编程
- 利用 Python 的特性：
  - 使用 `Protocol` 定义接口
  - 使用 `ABC`（Abstract Base Class）定义抽象类
  - 使用 `@property` 封装属性访问

#### 4. 代码质量规范

- **重构**：识别并规避《重构（Refactoring）》中描述的代码坏味道，例如：
  - 重复代码（Duplicated Code）
  - 过长方法（Long Method）
  - 过大类（Large Class）
  - 过长参数列表（Long Parameter List）
  - 发散式变化（Divergent Change）
  - 霰弹式修改（Shotgun Surgery）

- **Effective Python**：遵循《Effective Python》编码建议，例如：
  - 使用 `dataclass` 简化数据类定义
  - 优先使用列表推导式和生成器表达式
  - 使用上下文管理器（`with` 语句）管理资源
  - 使用 `Optional` 明确表示可选值
  - 避免可变默认参数
  - 使用 `@dataclass(frozen=True)` 创建不可变对象
  - 合理使用异常处理，定义自定义异常类

- **Python 特定规范**：
  - 遵循 PEP 8 代码风格
  - 使用类型注解（Type Hints）
  - 使用 `mypy` 进行静态类型检查
  - 使用 `ruff` 进行代码检查和格式化
  - 编写 docstring（Google 或 NumPy 风格）
  - 使用 `pytest` 编写单元测试

- **FastAPI 最佳实践**：
  - 使用依赖注入（Dependency Injection）
  - 合理使用 Pydantic 模型进行数据验证
  - 使用异步函数（`async/await`）提升性能
  - 定义清晰的 API 文档（自动生成 OpenAPI）
  - 使用中间件处理跨切面关注点
  - 合理处理异常和错误响应
