package com.cms.domain.content;

import com.cms.domain.category.Category;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "content")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentStatus status = ContentStatus.DRAFT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Content() {}

    public Content(String title, String body, Category category) {
        this.title = title;
        this.body = body;
        this.category = category;
    }

    // Business logic methods
    public void publish() {
        if (this.status == ContentStatus.DRAFT) {
            this.status = ContentStatus.PUBLISHED;
            this.publishedAt = LocalDateTime.now();
        }
    }

    public void unpublish() {
        if (this.status == ContentStatus.PUBLISHED) {
            this.status = ContentStatus.DRAFT;
            this.publishedAt = null;
        }
    }

    public boolean isPublished() {
        return status == ContentStatus.PUBLISHED;
    }

    public void moveToCategory(Category newCategory) {
        if (newCategory == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
        this.category = newCategory;
    }

    public ContentBody getContentBody() {
        return ContentBody.fromHtml(this.body);
    }

    public String getSummary(int maxLength) {
        return getContentBody().getSummary(maxLength);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public ContentStatus getStatus() { return status; }
    public void setStatus(ContentStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
}
