package com.cms.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Content {
    
    public enum Status {
        DRAFT, PUBLISHED
    }
    
    private String id;
    private String title;
    private String markdownContent;
    private String htmlContent;
    private String categoryId;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    
    private Content() {}
    
    public static Content create(String title, String markdownContent, String categoryId) {
        Content content = new Content();
        content.id = UUID.randomUUID().toString();
        content.title = Objects.requireNonNull(title, "Content title cannot be null");
        content.markdownContent = markdownContent;
        content.categoryId = Objects.requireNonNull(categoryId, "Category ID cannot be null");
        content.status = Status.DRAFT;
        content.createdAt = LocalDateTime.now();
        content.updatedAt = LocalDateTime.now();
        return content;
    }
    
    public static Content reconstitute(String id, String title, String markdownContent, 
                                        String htmlContent, String categoryId, Status status,
                                        LocalDateTime createdAt, LocalDateTime updatedAt, 
                                        LocalDateTime publishedAt) {
        Content content = new Content();
        content.id = id;
        content.title = title;
        content.markdownContent = markdownContent;
        content.htmlContent = htmlContent;
        content.categoryId = categoryId;
        content.status = status;
        content.createdAt = createdAt;
        content.updatedAt = updatedAt;
        content.publishedAt = publishedAt;
        return content;
    }
    
    public void updateContent(String newTitle, String newMarkdownContent) {
        this.title = Objects.requireNonNull(newTitle, "Content title cannot be null");
        this.markdownContent = newMarkdownContent;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setRenderedHtml(String htmlContent) {
        this.htmlContent = htmlContent;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void publish() {
        this.status = Status.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void unpublish() {
        this.status = Status.DRAFT;
        this.publishedAt = null;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void changeCategory(String newCategoryId) {
        this.categoryId = Objects.requireNonNull(newCategoryId, "Category ID cannot be null");
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean isPublished() {
        return status == Status.PUBLISHED;
    }
    
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMarkdownContent() { return markdownContent; }
    public String getHtmlContent() { return htmlContent; }
    public String getCategoryId() { return categoryId; }
    public Status getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
}
