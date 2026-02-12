package com.cms.domain.model.category;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 分类聚合根
 * 充血模型：包含业务逻辑方法
 * 
 * @author jxin
 * @date 2026-02-11
 */
@Entity
@Table(name = "categories")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 分类名称 */
    @Column(nullable = false, length = 100)
    private String name;
    
    /** 分类描述 */
    @Column(length = 500)
    private String description;
    
    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @Column(nullable = false)
    private LocalDateTime updateTime;
    
    /**
     * 创建分类（充血模型：工厂方法）
     */
    public static Category create(String name, String description) {
        Category category = new Category();
        category.name = name;
        category.description = description;
        category.createTime = LocalDateTime.now();
        category.updateTime = LocalDateTime.now();
        return category;
    }
    
    /**
     * 更新分类信息（充血模型：业务逻辑封装）
     */
    public void updateInfo(String name, String description) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        this.name = name;
        this.description = description;
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 验证是否可以删除
     * 注：实际检查需要在应用层结合仓储进行
     */
    public boolean canBeDeleted() {
        return true; // 基础验证，实际会在应用层检查是否有关联内容
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
