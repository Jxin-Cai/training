package com.cms.presentation.dto;

import com.cms.domain.category.Category;

public class CategoryResponse {
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private String path;
    private Integer displayOrder;
    private boolean hasChildren;
    private long contentCount;

    public static CategoryResponse from(Category category, boolean hasChildren, long contentCount) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());
        response.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        response.setPath(category.getPath());
        response.setDisplayOrder(category.getDisplayOrder());
        response.setHasChildren(hasChildren);
        response.setContentCount(contentCount);
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    public boolean isHasChildren() { return hasChildren; }
    public void setHasChildren(boolean hasChildren) { this.hasChildren = hasChildren; }
    public long getContentCount() { return contentCount; }
    public void setContentCount(long contentCount) { this.contentCount = contentCount; }
}
