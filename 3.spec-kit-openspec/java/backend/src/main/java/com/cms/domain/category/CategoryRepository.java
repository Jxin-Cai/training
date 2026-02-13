package com.cms.domain.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentId(Long parentId);
    List<Category> findByParentIsNull();
    boolean existsByNameAndParentId(String name, Long parentId);

    @Query("SELECT c FROM Category c WHERE c.path LIKE CONCAT(:pathPrefix, '%')")
    List<Category> findByPathStartingWith(@Param("pathPrefix") String pathPrefix);
}
