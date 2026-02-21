package com.java.domain.valueobject;

/**
 * 用户角色枚举
 * 
 * 定义系统中的用户角色类型：
 * - ADMIN: 管理员，拥有所有权限
 * - AUTHOR: 作者，可以发布和管理文章
 * - READER: 读者，只能阅读内容
 * 
 * @author Jxin
 */
public enum UserRole {
    
    ADMIN("管理员"),
    AUTHOR("作者"),
    READER("读者");
    
    private final String displayName;
    
    UserRole(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 判断是否有后台管理权限
     */
    public boolean canAccessAdmin() {
        return this == ADMIN || this == AUTHOR;
    }
    
    /**
     * 判断是否可以管理用户
     */
    public boolean canManageUsers() {
        return this == ADMIN;
    }
}
