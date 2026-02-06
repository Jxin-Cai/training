package com.cms.application.dto;

import java.time.LocalDateTime;

public record CategoryDto(
    String id,
    String name,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
