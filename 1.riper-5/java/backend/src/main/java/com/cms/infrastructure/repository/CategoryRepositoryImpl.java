package com.cms.infrastructure.repository;

import com.cms.domain.model.category.Category;
import com.cms.domain.repository.CategoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 分类仓储实现
 * 继承JpaRepository获得基础CRUD能力
 * 
 * @author jxin
 * @date 2026-02-11
 */
@Repository
public interface CategoryRepositoryImpl extends JpaRepository<Category, Long>, CategoryRepository {
    // JpaRepository已经提供了所有需要的方法实现
    // save, findById, findAll, deleteById, existsById
}
