package com.cms.application.dto;

public record CreateCategoryRequest(
    String name, 
    String description,
    String parentId
) {}
