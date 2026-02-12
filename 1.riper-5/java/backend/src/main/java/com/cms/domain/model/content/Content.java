package com.cms.domain.model.content;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 内容聚合根
 * 充血模型：包含业务逻辑方法
 * 
 * @author jxin
 * @date 2026-02-11
 */
@Entity
@Table(name = "contents")
public class Content {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /** 内容标题 */
    @Column(nullable = false, length = 200)
    private String title;
    
    /** Markdown原始内容 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String markdownContent;
    
    /** 渲染后的HTML内容 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String renderedHtml;
    
    /** 关联的分类ID */
    @Column(nullable = false)
    private Long categoryId;
    
    /** 内容状态 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContentStatus status;
    
    /** 发布时间 */
    @Column
    private LocalDateTime publishTime;
    
    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    /** 更新时间 */
    @Column(nullable = false)
    private LocalDateTime updateTime;
    
    /**
     * 创建内容（充血模型：工厂方法）
     */
    public static Content create(String title, String markdownContent, String renderedHtml, Long categoryId) {
        Content content = new Content();
        content.title = title;
        content.markdownContent = markdownContent;
        content.renderedHtml = renderedHtml;
        content.categoryId = categoryId;
        content.status = ContentStatus.DRAFT;
        content.createTime = LocalDateTime.now();
        content.updateTime = LocalDateTime.now();
        return content;
    }
    
    /**
     * 更新内容（充血模型：业务逻辑封装）
     */
    public void updateContent(String title, String markdownContent, String renderedHtml, Long categoryId) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("标题不能为空");
        }
        if (markdownContent == null || markdownContent.trim().isEmpty()) {
            throw new IllegalArgumentException("内容不能为空");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("必须关联分类");
        }
        
        this.title = title;
        this.markdownContent = markdownContent;
        this.renderedHtml = renderedHtml;
        this.categoryId = categoryId;
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 发布内容（充血模型：状态转换逻辑）
     */
    public void publish() {
        if (this.status == ContentStatus.PUBLISHED) {
            throw new IllegalStateException("内容已经是发布状态");
        }
        this.status = ContentStatus.PUBLISHED;
        this.publishTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 取消发布（充血模型：状态转换逻辑）
     */
    public void unpublish() {
        if (this.status == ContentStatus.DRAFT) {
            throw new IllegalStateException("内容已经是草稿状态");
        }
        this.status = ContentStatus.DRAFT;
        this.publishTime = null;
        this.updateTime = LocalDateTime.now();
    }
    
    /**
     * 判断是否已发布
     */
    public boolean isPublished() {
        return this.status == ContentStatus.PUBLISHED;
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
    
    public ContentStatus getStatus() {
        return status;
    }
    
    public void setStatus(ContentStatus status) {
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
