package com.java.domain.repository;

import java.util.List;
import java.util.Optional;

import com.java.domain.entity.Category;

/**
 * 分类仓储接口
 * 
 * 定义分类实体的持久化操作。
 * 位于领域层，由基础设施层实现。
 * 
 * @author Jxin
 */
public interface CategoryRepository {
    
    /**
     * 根据ID查找分类
     */
    Optional<Category> findById(Long id);
    
    /**
     * 根据名称查找分类
     */
    Optional<Category> findByName(String name);
    
    /**
     * 查找所有分类
     */
    List<Category> findAll();
    
    /**
     * 保存分类（新增或更新）
     */
    Category save(Category category);
    
    /**
     * 根据ID删除分类
     */
    void deleteById(Long id);
    
    /**
     * 检查分类名称是否存在
     */
    boolean existsByName(String name);
    
    /**
     * 统计分类数量
     */
    long count();
}
