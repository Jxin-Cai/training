package com.java.domain.repository;

import java.util.List;
import java.util.Optional;

import com.java.domain.entity.User;

/**
 * 用户仓储接口
 * 
 * 定义用户实体的持久化操作。
 * 位于领域层，由基础设施层实现。
 * 
 * @author Jxin
 */
public interface UserRepository {
    
    /**
     * 根据ID查找用户
     */
    Optional<User> findById(Long id);
    
    /**
     * 根据用户名查找用户
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 查找所有用户
     */
    List<User> findAll();
    
    /**
     * 保存用户（新增或更新）
     */
    User save(User user);
    
    /**
     * 根据ID删除用户
     */
    void deleteById(Long id);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 统计用户数量
     */
    long count();
}
