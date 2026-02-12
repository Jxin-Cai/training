package com.cms.domain.repository;

import com.cms.domain.model.content.Content;
import com.cms.domain.model.content.ContentStatus;

import java.util.List;
import java.util.Optional;

/**
 * 内容仓储接口
 * DDD模式：接口定义在领域层，实现在基础设施层
 * 
 * @author jxin
 * @date 2026-02-11
 */
public interface ContentRepository {
    
    /**
     * 保存内容
     */
    Content save(Content content);
    
    /**
     * 根据ID查询内容
     */
    Optional<Content> findById(Long id);
    
    /**
     * 查询所有内容
     */
    List<Content> findAll();
    
    /**
     * 根据状态查询内容
     */
    List<Content> findByStatus(ContentStatus status);
    
    /**
     * 查询已发布内容，按发布时间降序排序
     */
    List<Content> findPublishedOrderByPublishTimeDesc();
    
    /**
     * 根据ID删除内容
     */
    void deleteById(Long id);
    
    /**
     * 统计指定分类下的内容数量
     */
    long countByCategoryId(Long categoryId);
    
    /**
     * 检查内容是否存在
     */
    boolean existsById(Long id);
}
