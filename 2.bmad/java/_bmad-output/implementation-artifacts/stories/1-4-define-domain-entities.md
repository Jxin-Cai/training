# Story 1.4: 定义领域实体

Status: ready-for-dev

## Story

As a **开发者**,
I want **定义核心领域实体（User, Article, Category）**,
so that **后续功能有明确的领域模型**.

## Acceptance Criteria

1. **AC1: User实体**
   - Given DDD项目结构已创建
   - When 定义User实体
   - Then User实体包含以下属性：
     - id: Long（唯一标识）
     - username: String（用户名，唯一）
     - password: String（密码，BCrypt加密）
     - role: UserRole（角色枚举：ADMIN, AUTHOR, READER）
     - createdAt: LocalDateTime（创建时间）
   - And 实体位于 domain/entity/User.java
   - And 使用充血模型，包含必要的业务方法

2. **AC2: Article实体**
   - Given User实体已定义
   - When 定义Article实体
   - Then Article实体包含以下属性：
     - id: Long（唯一标识）
     - title: String（标题）
     - content: String（Markdown内容）
     - status: ArticleStatus（状态枚举：DRAFT, PUBLISHED）
     - categoryId: Long（分类ID）
     - authorId: Long（作者ID）
     - createdAt: LocalDateTime（创建时间）
     - updatedAt: LocalDateTime（更新时间）
   - And 实体位于 domain/entity/Article.java
   - And 使用充血模型，包含状态转换方法

3. **AC3: Category实体**
   - Given Article实体已定义
   - When 定义Category实体
   - Then Category实体包含以下属性：
     - id: Long（唯一标识）
     - name: String（分类名称，唯一）
     - description: String（分类描述）
     - createdAt: LocalDateTime（创建时间）
   - And 实体位于 domain/entity/Category.java
   - And 使用充血模型

4. **AC4: 值对象 - ArticleStatus**
   - Given 需要文章状态枚举
   - When 定义ArticleStatus值对象
   - Then 包含枚举值：DRAFT, PUBLISHED
   - And 位于 domain/valueobject/ArticleStatus.java

5. **AC5: 值对象 - UserRole**
   - Given 需要用户角色枚举
   - When 定义UserRole值对象
   - Then 包含枚举值：ADMIN, AUTHOR, READER
   - And 位于 domain/valueobject/UserRole.java

6. **AC6: Repository接口**
   - Given 实体已定义
   - When 定义Repository接口
   - Then 包含以下接口：
     - UserRepository（位于 domain/repository/）
     - ArticleRepository（位于 domain/repository/）
     - CategoryRepository（位于 domain/repository/）
   - And 接口定义基本CRUD方法

## Tasks / Subtasks

- [ ] Task 1: 创建User实体 (AC: 1)
  - [ ] 1.1 创建 domain/entity/User.java
  - [ ] 1.2 定义实体属性
  - [ ] 1.3 添加构造方法
  - [ ] 1.4 添加业务方法（如密码验证）

- [ ] Task 2: 创建Article实体 (AC: 2)
  - [ ] 2.1 创建 domain/entity/Article.java
  - [ ] 2.2 定义实体属性
  - [ ] 2.3 添加构造方法
  - [ ] 2.4 添加状态转换方法（publish, unpublish）

- [ ] Task 3: 创建Category实体 (AC: 3)
  - [ ] 3.1 创建 domain/entity/Category.java
  - [ ] 3.2 定义实体属性
  - [ ] 3.3 添加构造方法

- [ ] Task 4: 创建值对象 (AC: 4, 5)
  - [ ] 4.1 创建 domain/valueobject/ArticleStatus.java
  - [ ] 4.2 创建 domain/valueobject/UserRole.java

- [ ] Task 5: 创建Repository接口 (AC: 6)
  - [ ] 5.1 创建 domain/repository/UserRepository.java
  - [ ] 5.2 创建 domain/repository/ArticleRepository.java
  - [ ] 5.3 创建 domain/repository/CategoryRepository.java
  - [ ] 5.4 定义基本CRUD方法

## Dev Notes

### DDD实体规范 (from doc/SPEC.md)

**充血模型要求：**
- 实体应包含业务逻辑方法
- 避免贫血模型（只有getter/setter）
- 实体应封装自己的业务规则

**实体特征：**
- 具有唯一标识（id）
- 通过id判断相等性
- 可变的属性值

**值对象特征：**
- 不可变
- 通过属性值判断相等性
- 如ArticleStatus、UserRole

### 数据模型 (from Architecture.md)

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│    User     │     │   Article   │     │  Category   │
├─────────────┤     ├─────────────┤     ├─────────────┤
│ id          │     │ id          │     │ id          │
│ username    │     │ title       │     │ name        │
│ password    │     │ content     │     │ description │
│ role        │     │ status      │     │ createdAt   │
│ createdAt   │     │ categoryId  │     └─────────────┘
└─────────────┘     │ authorId    │
                    │ createdAt   │
                    │ updatedAt   │
                    └─────────────┘
```

### Repository接口方法

**基础CRUD方法：**
```java
// UserRepository
User findById(Long id);
User findByUsername(String username);
List<User> findAll();
User save(User user);
void deleteById(Long id);

// ArticleRepository
Article findById(Long id);
List<Article> findAll();
List<Article> findByStatus(ArticleStatus status);
List<Article> findByCategoryId(Long categoryId);
List<Article> findByAuthorId(Long authorId);
Article save(Article article);
void deleteById(Long id);

// CategoryRepository
Category findById(Long id);
List<Category> findAll();
Category findByName(String name);
Category save(Category category);
void deleteById(Long id);
```

### 代码规范 (from doc/SPEC.md)

**SOLID原则：**
- 单一职责：每个实体只负责自己的业务逻辑
- 开闭原则：通过接口扩展，不修改现有代码
- 里氏替换：子类可以替换父类
- 接口隔离：接口最小化
- 依赖倒置：依赖抽象而非具体实现

**Effective Java规范：**
- 使用Builder模式处理多参数构造
- 最小化可变性
- 合理使用Optional

### Project Structure Notes

**文件位置：**
```
src/main/java/com/java/
├── domain/
│   ├── entity/
│   │   ├── User.java
│   │   ├── Article.java
│   │   └── Category.java
│   ├── valueobject/
│   │   ├── ArticleStatus.java
│   │   └── UserRole.java
│   └── repository/
│       ├── UserRepository.java
│       ├── ArticleRepository.java
│       └── CategoryRepository.java
```

### References

- [Source: _bmad-output/planning-artifacts/architecture.md#Data Architecture]
- [Source: _bmad-output/planning-artifacts/epics.md#Story 1.4]
- [Source: doc/SPEC.md#DDD POJO定义规范]
- [Source: doc/SPEC.md#面向对象编程原则]

## Dev Agent Record

### Agent Model Used

(待开发时填写)

### Debug Log References

(待开发时填写)

### Completion Notes List

(待开发时填写)

### File List

(待开发时填写)
