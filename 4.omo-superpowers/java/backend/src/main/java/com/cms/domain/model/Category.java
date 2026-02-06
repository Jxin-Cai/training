package com.cms.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Category {
    
    private String id;
    private String name;
    private String description;
    private String parentId;      // 父分类ID，null表示顶级分类
    private int level;            // 层级深度，0表示顶级
    private String path;          // 物化路径，如 "/id1/id2/id3"
    private int sortOrder;        // 同级排序
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Category() {}
    
    public static Category create(String name, String description) {
        return create(name, description, null);
    }
    
    public static Category create(String name, String description, String parentId) {
        Category category = new Category();
        category.id = UUID.randomUUID().toString();
        category.name = Objects.requireNonNull(name, "Category name cannot be null");
        category.description = description;
        category.parentId = parentId;
        category.level = 0;  // 默认顶级，由Service设置正确值
        category.path = "/" + category.id;  // 默认路径，由Service设置正确值
        category.sortOrder = 0;
        category.createdAt = LocalDateTime.now();
        category.updatedAt = LocalDateTime.now();
        return category;
    }
    
    public static Category reconstitute(String id, String name, String description,
                                         String parentId, int level, String path, int sortOrder,
                                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        Category category = new Category();
        category.id = id;
        category.name = name;
        category.description = description;
        category.parentId = parentId;
        category.level = level;
        category.path = path;
        category.sortOrder = sortOrder;
        category.createdAt = createdAt;
        category.updatedAt = updatedAt;
        return category;
    }
    
    public void rename(String newName) {
        this.name = Objects.requireNonNull(newName, "Category name cannot be null");
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateDescription(String newDescription) {
        this.description = newDescription;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void moveTo(String newParentId, int newLevel, String newPath) {
        this.parentId = newParentId;
        this.level = newLevel;
        this.path = newPath;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updatePathInfo(int newLevel, String newPath) {
        this.level = newLevel;
        this.path = newPath;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isDescendantOf(Category ancestor) {
        return this.path.startsWith(ancestor.getPath() + "/");
    }
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getParentId() { return parentId; }
    public int getLevel() { return level; }
    public String getPath() { return path; }
    public int getSortOrder() { return sortOrder; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
