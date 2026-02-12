package com.cms.domain.repository;

import com.cms.domain.model.category.Category;

import java.util.List;
import java.util.Optional;

/**
 * 分类仓储接口
 * DDD模式：接口定义在领域层，实现在基础设施层
 * 
 * @author jxin
 * @date 2026-02-11
 */
public interface CategoryRepository {
    
    /**
     * 保存分类
     */
    Category save(Category category);
    
    /**
     * 根据ID查询分类
     */
    Optional<Category> findById(Long id);
    
    /**
     * 查询所有分类
     */
    List<Category> findAll();
    
    /**
     * 根据ID删除分类
     */
    void deleteById(Long id);
    
    /**
     * 检查分类是否存在
     */
    boolean existsById(Long id);
}
