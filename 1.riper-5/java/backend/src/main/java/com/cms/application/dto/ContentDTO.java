package com.cms.application.dto;

import com.cms.domain.model.content.Content;

import java.time.LocalDateTime;

/**
 * 内容数据传输对象
 * 
 * @author jxin
 * @date 2026-02-11
 */
public class ContentDTO {
    
    private Long id;
    private String title;
    private String markdownContent;
    private String renderedHtml;
    private Long categoryId;
    private String categoryName;
    private String status;
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    public ContentDTO() {
    }
    
    public ContentDTO(Long id, String title, String markdownContent, String renderedHtml, 
                      Long categoryId, String categoryName, String status, 
                      LocalDateTime publishTime, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.title = title;
        this.markdownContent = markdownContent;
        this.renderedHtml = renderedHtml;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.status = status;
        this.publishTime = publishTime;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
    
    /**
     * 从领域对象转换为DTO
     */
    public static ContentDTO fromDomain(Content content) {
        return new ContentDTO(
                content.getId(),
                content.getTitle(),
                content.getMarkdownContent(),
                content.getRenderedHtml(),
                content.getCategoryId(),
                null,
                content.getStatus().name(),
                content.getPublishTime(),
                content.getCreateTime(),
                content.getUpdateTime()
        );
    }
    
    /**
     * 从领域对象转换为DTO（包含分类名称）
     */
    public static ContentDTO fromDomainWithCategoryName(Content content, String categoryName) {
        ContentDTO dto = fromDomain(content);
        dto.setCategoryName(categoryName);
        return dto;
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMarkdownContent() {
        return markdownContent;
    }
    
    public void setMarkdownContent(String markdownContent) {
        this.markdownContent = markdownContent;
    }
    
    public String getRenderedHtml() {
        return renderedHtml;
    }
    
    public void setRenderedHtml(String renderedHtml) {
        this.renderedHtml = renderedHtml;
    }
    
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getPublishTime() {
        return publishTime;
    }
    
    public void setPublishTime(LocalDateTime publishTime) {
        this.publishTime = publishTime;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
    
    public LocalDateTime getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
