package com.java.domain.entity;

import java.time.LocalDateTime;
import java.util.Objects;

import com.java.domain.valueobject.ArticleStatus;

import lombok.Getter;

/**
 * 文章实体
 * 
 * 表示系统中的文章，包含文章内容和状态。
 * 使用充血模型，封装业务逻辑。
 * 
 * @author Jxin
 */
@Getter
public class Article {
    
    private Long id;
    private String title;
    private String content;
    private ArticleStatus status;
    private Long categoryId;
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 创建新文章（草稿状态）
     */
    public Article(String title, String content, Long categoryId, Long authorId) {
        this.title = Objects.requireNonNull(title, "标题不能为空");
        this.content = Objects.requireNonNull(content, "内容不能为空");
        this.categoryId = Objects.requireNonNull(categoryId, "分类ID不能为空");
        this.authorId = Objects.requireNonNull(authorId, "作者ID不能为空");
        this.status = ArticleStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }
    
    /**
     * 从持久化重建文章
     */
    public Article(Long id, String title, String content, ArticleStatus status, 
                   Long categoryId, Long authorId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.status = status;
        this.categoryId = categoryId;
        this.authorId = authorId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    /**
     * 设置ID（仅用于持久化）
     */
    public void setId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID已存在，不能重复设置");
        }
        this.id = id;
    }
    
    /**
     * 修改标题
     */
    public void changeTitle(String newTitle) {
        this.title = Objects.requireNonNull(newTitle, "标题不能为空");
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 修改内容
     */
    public void changeContent(String newContent) {
        this.content = Objects.requireNonNull(newContent, "内容不能为空");
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 修改分类
     */
    public void changeCategory(Long newCategoryId) {
        this.categoryId = Objects.requireNonNull(newCategoryId, "分类ID不能为空");
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 更新文章内容
     */
    public void update(String title, String content, Long categoryId) {
        if (title != null && !title.trim().isEmpty()) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
        if (categoryId != null) {
            this.categoryId = categoryId;
        }
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 发布文章
     */
    public void publish() {
        if (this.status == ArticleStatus.PUBLISHED) {
            throw new IllegalStateException("文章已发布，无需重复发布");
        }
        this.status = ArticleStatus.PUBLISHED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 取消发布（转为草稿）
     */
    public void unpublish() {
        if (this.status == ArticleStatus.DRAFT) {
            throw new IllegalStateException("文章已是草稿状态");
        }
        this.status = ArticleStatus.DRAFT;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 判断是否已发布
     */
    public boolean isPublished() {
        return status.isPublished();
    }
    
    /**
     * 判断是否为草稿
     */
    public boolean isDraft() {
        return status.isDraft();
    }
    
    /**
     * 判断是否为指定用户的文章
     */
    public boolean isOwnedBy(Long userId) {
        return Objects.equals(this.authorId, userId);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return Objects.equals(id, article.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", categoryId=" + categoryId +
                ", authorId=" + authorId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
