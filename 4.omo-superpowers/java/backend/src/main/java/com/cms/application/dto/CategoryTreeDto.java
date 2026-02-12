package com.cms.application.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CategoryTreeDto(
    String id,
    String name,
    String description,
    String parentId,
    int level,
    int sortOrder,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    List<CategoryTreeDto> children
) {}
