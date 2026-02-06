package com.cms.application.dto;

public record UpdateCategoryRequest(
    String name, 
    String description,
    String parentId,
    Integer sortOrder
) {}
