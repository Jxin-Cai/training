package com.cms.application.dto;

import java.time.LocalDateTime;

public record ContentDto(
    String id,
    String title,
    String markdownContent,
    String htmlContent,
    String categoryId,
    String categoryName,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    LocalDateTime publishedAt
) {}
