package com.cms.domain.repository;

import com.cms.domain.model.Content;
import java.util.List;
import java.util.Optional;

public interface ContentRepository {
    Content save(Content content);
    Optional<Content> findById(String id);
    List<Content> findAll();
    List<Content> findByCategoryId(String categoryId);
    List<Content> findByCategoryIds(List<String> categoryIds);
    List<Content> findAllPublished();
    List<Content> findAllPublishedOrderByPublishedAtDesc();
    void deleteById(String id);
    boolean existsById(String id);
}
