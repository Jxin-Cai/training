## 🏗️ 技术架构

### 前端技术栈

- **框架**：Vue.js
- **构建工具**：npm run build

### 后端技术栈

- **语言**：C++ 17/20
- **框架**：Poco (C++ Portable Components)
- **构建系统**：CMake
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
1. 创建 build 目录：`mkdir -p build && cd build`
2. 执行 CMake 配置：`cmake .. -DCMAKE_BUILD_TYPE=Release`
3. 编译：`cmake --build . --config Release`
4. 启动应用：`./bin/app`

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
1. 查找 C++ 应用进程（通过端口或进程名）
2. 优雅关闭应用（发送 SIGTERM 信号，触发 Poco 的 shutdown 处理）
3. 等待应用完全停止（最多等待30秒）
4. 清理资源（关闭网络连接、释放内存等）
5. 如超时则强制终止（SIGKILL）

---

## 📐 开发规范

### 后端架构规范

#### 1. DDD 分层架构

采用领域驱动设计（DDD），分层如下：

- **Presentation Layer（展示层）**：RequestHandler、DTO（数据传输对象）
- **Application Layer（应用层）**：Service、Application Service
- **Domain Layer（领域层）**：
  - Entity（实体）
  - Value Object（值对象）
  - Domain Service（领域服务）
  - Repository Interface（仓储接口）
- **Infrastructure Layer（基础设施层）**：
  - Repository Implementation（仓储实现）
  - 外部服务调用（GLM API）
  - 向量存储实现

#### 2. DDD 对象定义规范

- 使用 **DDD 战术模式** 定义对象：
  - Entity：使用 `class` 定义具有唯一标识的对象
  - Value Object：使用 `struct` 或不可变类定义值对象
  - Aggregate Root：聚合根
- 遵循 **充血模型**，而非贫血模型
- 使用 C++ 特性增强设计：
  - 使用 `const` 和 `constexpr` 确保不可变性
  - 使用智能指针管理资源
  - 使用移动语义优化性能

#### 3. 面向对象编程原则

- 遵循 **SOLID 原则**
- 合理使用设计模式
- 避免面向过程式编程
- 利用 Modern C++ 特性：
  - 使用虚函数和接口（纯虚函数）
  - 使用模板和泛型编程
  - 使用 RAII（Resource Acquisition Is Initialization）管理资源
  - 优先使用组合而非继承

#### 4. 代码质量规范

- **重构**：识别并规避《重构（Refactoring）》中描述的代码坏味道，例如：
  - 重复代码（Duplicated Code）
  - 过长方法（Long Method）
  - 过大类（Large Class）
  - 过长参数列表（Long Parameter List）
  - 发散式变化（Divergent Change）
  - 霰弹式修改（Shotgun Surgery）

- **Effective C++**：遵循《Effective C++》和《Effective Modern C++》编码建议，例如：
  - 使用智能指针（`std::unique_ptr`、`std::shared_ptr`）管理动态内存
  - 优先使用栈分配而非堆分配
  - 使用 `const` 保证对象不可变性
  - 使用 RAII 管理资源（文件、锁、网络连接等）
  - 优先使用值语义和移动语义
  - 避免裸指针和手动内存管理
  - 使用 `std::optional` 表示可选值
  - 使用 `std::variant` 表示多态类型

- **Modern C++ 规范**：
  - 使用 C++17/20 特性：
    - `std::string_view` 优化字符串处理
    - `if constexpr` 编译期条件判断
    - Structured bindings（结构化绑定）
    - `std::filesystem` 文件系统操作
    - Concepts（C++20）约束模板参数
  - 避免使用 C 风格代码（malloc/free、C 数组等）
  - 使用标准库容器和算法
  - 使用范围 for 循环（Range-based for loop）

- **C++ 特定规范**：
  - 遵循 Google C++ Style Guide 或 C++ Core Guidelines
  - 使用命名空间组织代码
  - 使用 `nullptr` 而非 `NULL` 或 `0`
  - 合理使用异常处理
  - 编写单元测试（使用 Google Test 或 Catch2）
  - 使用静态分析工具（clang-tidy、cppcheck）
  - 使用格式化工具（clang-format）

- **Poco 框架最佳实践**：
  - 使用 Poco 的智能指针（`Poco::AutoPtr`、`Poco::SharedPtr`）
  - 合理使用 Poco 的线程和同步工具
  - 使用 Poco 的日志系统
  - 使用 Poco 的配置管理
  - 合理使用 Poco 的 HTTP 服务器组件
  - 使用 Poco 的 JSON 解析和生成
  - 使用 Poco 的异常处理机制

- **内存和性能管理**：
  - 避免内存泄漏（使用智能指针和 RAII）
  - 避免悬空指针和野指针
  - 注意对象生命周期管理
  - 使用移动语义避免不必要的拷贝
  - 使用 `std::move` 转移所有权
  - 合理使用 `const` 引用传递参数
  - 使用性能分析工具（Valgrind、perf）
