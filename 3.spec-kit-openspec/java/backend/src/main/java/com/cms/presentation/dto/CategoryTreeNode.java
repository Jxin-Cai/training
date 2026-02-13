package com.cms.presentation.dto;

import com.cms.domain.category.Category;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryTreeNode {
    private Long id;
    private String name;
    private String description;
    private Long parentId;
    private Integer displayOrder;
    private long contentCount;
    private List<CategoryTreeNode> children = new ArrayList<>();

    public static CategoryTreeNode from(Category category, long contentCount) {
        CategoryTreeNode node = new CategoryTreeNode();
        node.setId(category.getId());
        node.setName(category.getName());
        node.setDescription(category.getDescription());
        node.setParentId(category.getParent() != null ? category.getParent().getId() : null);
        node.setDisplayOrder(category.getDisplayOrder());
        node.setContentCount(contentCount);
        return node;
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
    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }
    public long getContentCount() { return contentCount; }
    public void setContentCount(long contentCount) { this.contentCount = contentCount; }
    public List<CategoryTreeNode> getChildren() { return children; }
    public void setChildren(List<CategoryTreeNode> children) { this.children = children; }
}
