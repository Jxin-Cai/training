package com.cms.domain.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CategoryTreeService {

    @Autowired
    private CategoryRepository categoryRepository;

    public boolean wouldCreateCircularReference(Long categoryId, Long newParentId) {
        if (categoryId == null || newParentId == null) {
            return false;
        }

        Category category = categoryRepository.findById(categoryId).orElse(null);
        Category newParent = categoryRepository.findById(newParentId).orElse(null);

        if (category == null || newParent == null) {
            return false;
        }

        return category.wouldCreateCircularReference(newParent);
    }

    public List<Category> getAllDescendants(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return new ArrayList<>();
        }

        String pathPrefix = category.getPath();
        return categoryRepository.findByPathStartingWith(pathPrefix);
    }

    public List<Category> getAncestors(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if (category == null) {
            return new ArrayList<>();
        }

        List<Category> ancestors = new ArrayList<>();
        Category current = category.getParent();
        while (current != null) {
            ancestors.add(0, current);
            current = current.getParent();
        }

        return ancestors;
    }

    public void updateCategoryPath(Category category) {
        category.updatePath();
        categoryRepository.save(category);

        // Update all children paths
        updateDescendantPaths(category);
    }

    public void updateDescendantPaths(Category parent) {
        List<Category> children = categoryRepository.findByParentId(parent.getId());
        for (Category child : children) {
            child.updatePath();
            categoryRepository.save(child);
            updateDescendantPaths(child);
        }
    }
}
