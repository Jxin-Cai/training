package com.java.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章请求DTO
 * 
 * @author Jxin
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleRequest {
    
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    @NotNull(message = "分类ID不能为空")
    private Long categoryId;
}
