package com.java.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import lombok.Getter;

/**
 * 分类实体
 * 
 * 表示文章分类，用于组织和筛选文章。
 * 使用充血模型，封装业务逻辑。
 * 
 * @author Jxin
 */
@Getter
public class Category {
    
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    
    /**
     * 创建新分类
     */
    public Category(String name, String description) {
        this.name = Objects.requireNonNull(name, "分类名称不能为空");
        this.description = description != null ? description : "";
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * 从持久化重建分类
     */
    public Category(Long id, String name, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }
    
    /**
     * 设置ID（仅用于持久化）
     */
    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID已存在，不能重复设置");
        }
        this.id = id;
    }
    
    /**
     * 修改分类名称
     */
    public void changeName(String newName) {
        this.name = Objects.requireNonNull(newName, "分类名称不能为空");
    }
    
    /**
     * 修改分类描述
     */
    public void changeDescription(String newDescription) {
        this.description = newDescription != null ? newDescription : "";
    }
    
    /**
     * 更新分类信息
     */
    public void update(String name, String description) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        this.description = description != null ? description : "";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
