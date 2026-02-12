package com.cms.infrastructure.repository;

import com.cms.domain.model.content.Content;
import com.cms.domain.model.content.ContentStatus;
import com.cms.domain.repository.ContentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 内容仓储实现
 * 继承JpaRepository并添加自定义查询方法
 * 
 * @author jxin
 * @date 2026-02-11
 */
@Repository
public interface ContentRepositoryImpl extends JpaRepository<Content, Long>, ContentRepository {
    
    /**
     * 根据状态查询内容
     */
    @Override
    List<Content> findByStatus(ContentStatus status);
    
    /**
     * 查询已发布内容，按发布时间降序排序
     */
    @Query("SELECT c FROM Content c WHERE c.status = 'PUBLISHED' ORDER BY c.publishTime DESC")
    @Override
    List<Content> findPublishedOrderByPublishTimeDesc();
    
    /**
     * 统计指定分类下的内容数量
     */
    @Override
    long countByCategoryId(@Param("categoryId") Long categoryId);
}
