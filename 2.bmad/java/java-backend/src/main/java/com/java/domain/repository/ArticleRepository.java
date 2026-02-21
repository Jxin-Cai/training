package com.java.domain.repository;

import java.util.List;
import java.util.Optional;

import com.java.domain.entity.Article;
import com.java.domain.valueobject.ArticleStatus;

/**
 * 文章仓储接口
 * 
 * 定义文章实体的持久化操作。
 * 位于领域层，由基础设施层实现。
 * 
 * @author Jxin
 */
public interface ArticleRepository {
    
    /**
     * 根据ID查找文章
     */
    Optional<Article> findById(Long id);
    
    /**
     * 查找所有文章
     */
    List<Article> findAll();
    
    /**
     * 根据状态查找文章
     */
    List<Article> findByStatus(ArticleStatus status);
    
    /**
     * 根据分类ID查找文章
     */
    List<Article> findByCategoryId(Long categoryId);
    
    /**
     * 根据作者ID查找文章
     */
    List<Article> findByAuthorId(Long authorId);
    
    /**
     * 根据状态和分类ID查找文章
     */
    List<Article> findByStatusAndCategoryId(ArticleStatus status, Long categoryId);
    
    /**
     * 搜索文章（按标题）
     */
    List<Article> searchByTitle(String keyword);
    
    /**
     * 搜索已发布的文章（按标题）
     */
    List<Article> searchPublishedByTitle(String keyword);
    
    /**
     * 保存文章（新增或更新）
     */
    Article save(Article article);
    
    /**
     * 根据ID删除文章
     */
    void deleteById(Long id);
    
    /**
     * 统计文章数量
     */
    long count();
    
    /**
     * 统计指定状态的文章数量
     */
    long countByStatus(ArticleStatus status);
    
    /**
     * 统计指定分类的文章数量
     */
    long countByCategoryId(Long categoryId);
}
