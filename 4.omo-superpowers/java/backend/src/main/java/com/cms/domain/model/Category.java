package com.cms.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Category {
    
    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Category() {}
    
    public static Category create(String name, String description) {
        Category category = new Category();
        category.id = UUID.randomUUID().toString();
        category.name = Objects.requireNonNull(name, "Category name cannot be null");
        category.description = description;
        category.createdAt = LocalDateTime.now();
        category.updatedAt = LocalDateTime.now();
        return category;
    }
    
    public static Category reconstitute(String id, String name, String description, 
                                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        Category category = new Category();
        category.id = id;
        category.name = name;
        category.description = description;
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
    
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
