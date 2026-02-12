package com.cms.application.dto;

import java.time.LocalDateTime;

public record CategoryDto(
    String id,
    String name,
    String description,
    String parentId,
    int level,
    String path,
    int sortOrder,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
