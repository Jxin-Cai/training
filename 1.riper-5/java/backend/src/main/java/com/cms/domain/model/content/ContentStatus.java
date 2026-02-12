package com.cms.domain.model.content;

/**
 * 内容状态值对象
 * 
 * @author jxin
 * @date 2026-02-11
 */
public enum ContentStatus {
    
    /** 草稿 */
    DRAFT("草稿"),
    
    /** 已发布 */
    PUBLISHED("已发布");
    
    private final String displayName;
    
    ContentStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
