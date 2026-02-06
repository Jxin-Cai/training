package com.cms.domain.repository;

import com.cms.domain.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(String id);
    List<Category> findAll();
    void deleteById(String id);
    boolean existsById(String id);
}
