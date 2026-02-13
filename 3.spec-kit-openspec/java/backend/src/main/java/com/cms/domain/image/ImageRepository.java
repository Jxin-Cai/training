package com.cms.domain.image;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    Image findByFilename(String filename);
    List<Image> findByIdIn(Set<Long> ids);
}
