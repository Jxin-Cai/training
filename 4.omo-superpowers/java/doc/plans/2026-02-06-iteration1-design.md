# 迭代1：多级分类与富文本增强 - 设计文档

## 需求概述

1. 分类升级为多级自关联（无限级分类树）
2. 前台按多级分类分层展示内容
3. 后台内容编辑页支持图片上传并嵌入（Base64存储）
4. 前台增加多级分类导航栏，可筛选对应分类下的内容

## 设计决策

| 决策项 | 选择 |
|--------|------|
| 分类层级限制 | 无限制（技术上支持任意深度） |
| 图片存储方式 | Base64 编码存入内容字段 |
| 前台导航样式 | 顶部水平导航 + 子分类下拉 |
| 内容显示方式 | 点击父分类显示所有子孙分类内容 |

---

## 一、领域模型变更

### 1.1 Category 实体

**新增字段：**

```java
public class Category {
    private String id;
    private String name;
    private String description;
    private String parentId;        // 父分类ID（null表示顶级）
    private int sortOrder;          // 同级排序
    private int level;              // 层级深度（0=顶级）
    private String path;            // 物化路径（如 "/id1/id2/id3"）
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**新增行为：**

- `create(name, description, parentId)` - 创建分类
- `moveTo(newParentId)` - 移动分类到新父级
- `updatePath(newPath, newLevel)` - 更新路径信息

**设计说明：**

- `path` 使用物化路径模式，便于查询子孙分类
- `level` 冗余字段，方便前端渲染缩进
- `sortOrder` 用于同级分类排序

---

## 二、Repository 变更

### 2.1 CategoryRepository

**新增方法：**

```java
List<Category> findByParentId(String parentId);
List<Category> findDescendants(String categoryId);
List<Category> findAncestors(String categoryId);
```

### 2.2 ContentRepository

**变更方法：**

```java
// 原方法
List<Content> findByCategoryId(String categoryId);

// 改为支持多个分类ID
List<Content> findByCategoryIds(List<String> categoryIds);
```

---

## 三、Application Service 变更

### 3.1 CategoryService

**改造逻辑：**

1. **创建分类**：自动计算 `path` 和 `level`
   - 如果 `parentId` 为空，`level = 0`，`path = "/" + id`
   - 如果有父级，`level = parent.level + 1`，`path = parent.path + "/" + id`

2. **移动分类**：更新自身及所有子孙的 `path` 和 `level`

3. **删除分类**：验证无子分类才允许删除

4. **查询分类**：返回树形结构

### 3.2 ContentService

**改造逻辑：**

- 按分类查询时，先获取该分类及所有子孙分类ID，再查询内容

### 3.3 ImageUploadService（新增）

```java
@Service
public class ImageUploadService {
    public String convertToBase64(MultipartFile file) {
        // 验证文件类型（仅允许 image/*）
        // 转换为 Base64 Data URL
        // 返回 "data:image/png;base64,..."
    }
}
```

---

## 四、Presentation 变更

### 4.1 CategoryController

**API 变更：**

| 端点 | 方法 | 变更 |
|------|------|------|
| `POST /api/categories` | 创建 | 请求新增 `parentId` 字段 |
| `GET /api/categories` | 列表 | 返回树形结构 |
| `GET /api/categories/flat` | 列表 | 新增：返回扁平列表（含层级信息） |
| `PUT /api/categories/{id}` | 更新 | 支持修改 `parentId`（移动） |
| `DELETE /api/categories/{id}` | 删除 | 验证无子分类 |

**响应结构变更：**

```json
// GET /api/categories 返回树形
[
  {
    "id": "1",
    "name": "技术",
    "parentId": null,
    "level": 0,
    "children": [
      {
        "id": "2",
        "name": "前端",
        "parentId": "1",
        "level": 1,
        "children": []
      }
    ]
  }
]

// GET /api/categories/flat 返回扁平
[
  { "id": "1", "name": "技术", "parentId": null, "level": 0 },
  { "id": "2", "name": "前端", "parentId": "1", "level": 1 }
]
```

### 4.2 ImageController（新增）

| 端点 | 方法 | 说明 |
|------|------|------|
| `POST /api/upload/image` | 上传图片 | 返回 Base64 URL |

**请求：** `multipart/form-data`，字段名 `file`

**响应：**

```json
{
  "url": "data:image/png;base64,iVBORw0KGgo..."
}
```

---

## 五、前端变更

### 5.1 Store 改造 (`stores/category.ts`)

**新增类型：**

```typescript
interface CategoryTreeNode {
  id: string
  name: string
  parentId: string | null
  level: number
  children: CategoryTreeNode[]
}
```

**新增状态：**

- `categoryTree: CategoryTreeNode[]` - 树形分类数据
- `flatCategories: Category[]` - 扁平分类数据（供下拉用）

### 5.2 后台分类管理 (`CategoriesView.vue`)

**改造点：**

- 分类列表改为树形表格（通过 `level` 字段控制缩进）
- 新建/编辑表单新增"父分类"下拉选择
- 删除时后端返回错误提示（有子分类不可删除）

### 5.3 后台内容编辑 (`ContentView.vue`)

**改造点：**

1. 分类选择改为树形下拉（显示层级缩进）

2. 配置 md-editor-v3 图片上传：

```typescript
import { MdEditor } from 'md-editor-v3'

const onUploadImg = async (
  files: File[],
  callback: (urls: string[]) => void
) => {
  const urls = await Promise.all(
    files.map(async (file) => {
      const formData = new FormData()
      formData.append('file', file)
      const res = await api.post('/upload/image', formData)
      return res.data.url
    })
  )
  callback(urls)
}
```

```vue
<MdEditor
  v-model="formData.markdownContent"
  @onUploadImg="onUploadImg"
/>
```

### 5.4 前台布局 (`PublicLayout.vue`)

**新增组件：多级导航栏**

```vue
<template>
  <nav class="category-nav">
    <ul class="nav-list">
      <li v-for="cat in topCategories" :key="cat.id" class="nav-item">
        <router-link :to="`/?category=${cat.id}`">{{ cat.name }}</router-link>
        <ul v-if="cat.children.length" class="dropdown">
          <li v-for="sub in cat.children" :key="sub.id">
            <router-link :to="`/?category=${sub.id}`">{{ sub.name }}</router-link>
            <!-- 支持多级嵌套 -->
          </li>
        </ul>
      </li>
    </ul>
  </nav>
</template>
```

### 5.5 前台首页 (`HomeView.vue`)

**改造点：**

- 读取 URL 参数 `category`
- 调用 `GET /api/contents?categoryId=xxx` 获取内容（后端已处理子孙）
- 显示当前分类面包屑导航

---

## 六、实现任务清单

### 后端任务

| # | 层次 | 任务 | 估时 |
|---|------|------|------|
| 1 | Domain | `Category` 添加 `parentId, level, path, sortOrder` 字段及行为 | 30min |
| 2 | Domain | `CategoryRepository` 添加新方法接口 | 15min |
| 3 | Infrastructure | `InMemoryCategoryRepository` 实现新方法 | 30min |
| 4 | Application | `CategoryService` 改造（创建/移动/删除逻辑） | 45min |
| 5 | Presentation | `CategoryController` 及 DTO 改造 | 30min |
| 6 | Domain | `ContentRepository` 添加 `findByCategoryIds` | 10min |
| 7 | Infrastructure | `InMemoryContentRepository` 实现 | 15min |
| 8 | Application | `ContentService` 改造（含子孙分类查询） | 20min |
| 9 | Application | 新增 `ImageUploadService` | 20min |
| 10 | Presentation | 新增 `ImageController` | 15min |

### 前端任务

| # | 文件 | 任务 | 估时 |
|---|------|------|------|
| 11 | `types/index.ts` | 添加树形分类类型定义 | 10min |
| 12 | `stores/category.ts` | 添加树形数据处理 | 20min |
| 13 | `api/index.ts` | 添加新 API 调用 | 10min |
| 14 | `CategoriesView.vue` | 改造为树形管理界面 | 45min |
| 15 | `ContentView.vue` | 树形分类下拉 + 图片上传 | 30min |
| 16 | `PublicLayout.vue` | 新增多级导航组件 | 45min |
| 17 | `HomeView.vue` | 分类筛选 + 面包屑 | 30min |

---

## 七、测试要点

1. **分类树操作**：创建多级、移动分类、删除有子分类时报错
2. **内容查询**：选择父分类能看到所有子孙内容
3. **图片上传**：上传图片后能正确显示在编辑器和预览中
4. **导航交互**：多级菜单展开收起正常

---

## 八、风险与注意事项

1. **Base64 图片性能**：大图片会增加内容体积，建议前端压缩或限制大小（如 2MB）
2. **分类移动循环检测**：不能将父分类移动到其子孙分类下
3. **内存存储限制**：当前使用内存存储，重启会丢失数据（迭代1暂不处理）
