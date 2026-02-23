package com.cms.domain.content;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {
    List<Content> findByCategoryId(Long categoryId);
    List<Content> findByCategoryIdIn(List<Long> categoryIds);
    List<Content> findByStatus(ContentStatus status);
    List<Content> findByCategoryIdAndStatus(Long categoryId, ContentStatus status);
    long countByCategoryIdAndStatus(Long categoryId, ContentStatus status);
}
