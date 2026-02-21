package com.java.presentation.dto;

import java.time.LocalDateTime;

import com.java.domain.valueobject.ArticleStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章响应DTO
 * 
 * @author Jxin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDTO {
    
    private Long id;
    private String title;
    private String content;
    private ArticleStatus status;
    private Long categoryId;
    private String categoryName;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
