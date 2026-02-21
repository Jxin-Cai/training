package com.java.domain.entity;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.java.domain.valueobject.UserRole;

import lombok.Getter;

/**
 * 用户实体
 * 
 * 表示系统中的用户，包含用户的基本信息和角色。
 * 使用充血模型，封装业务逻辑。
 * 实现 UserDetails 以支持 Spring Security 认证。
 * 
 * @author Jxin
 */
@Getter
public class User implements UserDetails {
    
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String username;
    private String password;
    private UserRole role;
    private LocalDateTime createdAt;
    
    /**
     * 创建新用户
     */
    public User(String username, String password, UserRole role) {
        this.username = Objects.requireNonNull(username, "用户名不能为空");
        this.password = Objects.requireNonNull(password, "密码不能为空");
        this.role = Objects.requireNonNull(role, "角色不能为空");
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * 从持久化重建用户
     */
    public User(Long id, String username, String password, UserRole role, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
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
     * 修改角色
     */
    public void changeRole(UserRole newRole) {
        this.role = Objects.requireNonNull(newRole, "新角色不能为空");
    }
    
    /**
     * 修改密码
     */
    public void changePassword(String newPassword) {
        this.password = Objects.requireNonNull(newPassword, "新密码不能为空");
    }
    
    /**
     * 判断是否有后台管理权限
     */
    public boolean canAccessAdmin() {
        return role.canAccessAdmin();
    }
    
    /**
     * 判断是否可以管理用户
     */
    public boolean canManageUsers() {
        return role.canManageUsers();
    }
    
    /**
     * 判断是否为管理员
     */
    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }
    
    /**
     * 判断是否为作者
     */
    public boolean isAuthor() {
        return role == UserRole.AUTHOR;
    }
    
    // ============ UserDetails 接口实现 ============
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    // ============ Object 方法 ============
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", createdAt=" + createdAt +
                '}';
    }
}
