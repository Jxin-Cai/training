package com.cms.presentation.dto;

import com.cms.domain.content.Content;

public class ContentSummary {
    private Long id;
    private String title;
    private String summary;
    private Long categoryId;
    private String categoryName;
    private String status;
    private String createdAt;
    private String publishedAt;

    public static ContentSummary from(Content content) {
        ContentSummary summary = new ContentSummary();
        summary.setId(content.getId());
        summary.setTitle(content.getTitle());
        summary.setSummary(content.getSummary(200));
        summary.setCategoryId(content.getCategory().getId());
        summary.setCategoryName(content.getCategory().getName());
        summary.setStatus(content.getStatus().name());
        summary.setCreatedAt(content.getCreatedAt() != null ? content.getCreatedAt().toString() : null);
        summary.setPublishedAt(content.getPublishedAt() != null ? content.getPublishedAt().toString() : null);
        return summary;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getPublishedAt() { return publishedAt; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }
}
