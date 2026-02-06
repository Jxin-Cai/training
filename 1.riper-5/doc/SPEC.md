
## 🏗️ 技术架构

### 前端技术栈

- **框架**：Vue.js
- **构建工具**：npm run build

### 后端技术栈

- **语言**：java
- **框架**：springboot
- **DB** JPA + mysql + h2 + flyway

### 部署架构

- **部署方式**：单进程启动
- **流程**：
    1. 前端代码执行 `npm run build`
    2. 编译产物复制到 `src/main/resources/static`
    3. 后端启动时同时提供前后端服务

---

## 📐 开发规范

### 后端架构规范

#### 1. DDD 分层架构

采用领域驱动设计（DDD），分层如下：

- **Presentation Layer（展示层）**：Controller、DTO
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

#### 2. DDD POJO 定义规范

- 使用 **DDD 战术模式** 定义 POJO：
    - Entity：具有唯一标识的对象
    - Value Object：不可变的值对象
    - Aggregate Root：聚合根
- 遵循 **充血模型**，而非贫血模型

#### 3. 面向对象编程原则

- 遵循 **SOLID 原则**
- 合理使用设计模式
- 避免面向过程式编程

#### 4. 代码质量规范

- **重构**：识别并规避《重构（Refactoring）》中描述的代码坏味道，例如：
    - 重复代码（Duplicated Code）
    - 过长方法（Long Method）
    - 过大类（Large Class）
    - 过长参数列表（Long Parameter List）
    - 发散式变化（Divergent Change）
    - 霰弹式修改（Shotgun Surgery）
- **Effective Java**：遵循《Effective Java》编码建议，例如：
    - 使用构建器（Builder）处理多参数构造
    - 优先使用枚举而非 int 常量
    - 最小化可变性
    - 合理使用 Optional
    - 优先使用标准异常
