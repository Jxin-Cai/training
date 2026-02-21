package com.java.domain.valueobject;

/**
 * 文章状态枚举
 * 
 * 定义文章的生命周期状态：
 * - DRAFT: 草稿状态，仅作者可见
 * - PUBLISHED: 已发布状态，所有读者可见
 * 
 * @author Jxin
 */
public enum ArticleStatus {
    
    DRAFT("草稿"),
    PUBLISHED("已发布");
    
    private final String displayName;
    
    ArticleStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 判断是否已发布
     */
    public boolean isPublished() {
        return this == PUBLISHED;
    }
    
    /**
     * 判断是否为草稿
     */
    public boolean isDraft() {
        return this == DRAFT;
    }
}
