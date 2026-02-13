package com.cms.application;

import com.cms.domain.category.Category;
import com.cms.domain.category.CategoryRepository;
import com.cms.domain.category.CategoryTreeService;
import com.cms.domain.category.CircularReferenceException;
import com.cms.domain.content.ContentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryTreeService categoryTreeService;

    @Autowired
    private ContentRepository contentRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIsNull();
    }

    public List<Category> getChildren(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    public List<Category> getDescendants(Long categoryId) {
        return categoryTreeService.getAllDescendants(categoryId);
    }

    public Category createCategory(String name, String description, Long parentId, Integer displayOrder) {
        Category category = new Category(name);
        category.setDescription(description);
        category.setDisplayOrder(displayOrder != null ? displayOrder : 0);

        if (parentId != null) {
            Category parent = categoryRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent category not found"));
            category.setParent(parent);
        }

        // First save to generate ID
        Category saved = categoryRepository.save(category);

        // Now update the path with the generated ID
        saved.updatePath();
        saved = categoryRepository.save(saved);

        return saved;
    }

    public Category updateCategory(Long id, String name, String description, Long parentId, Integer displayOrder) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (name != null) category.setName(name);
        if (description != null) category.setDescription(description);
        if (displayOrder != null) category.setDisplayOrder(displayOrder);

        if (parentId != null && !parentId.equals(category.getParent() != null ? category.getParent().getId() : null)) {
            if (categoryTreeService.wouldCreateCircularReference(id, parentId)) {
                throw new CircularReferenceException("Cannot create circular category reference");
            }

            Category newParent = parentId != -1 ? categoryRepository.findById(parentId).orElse(null) : null;
            category.setParent(newParent);
            categoryTreeService.updateCategoryPath(category);
        }

        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id, String handleChildren, String handleContent) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        List<Category> children = categoryRepository.findByParentId(id);
        long contentCount = contentRepository.findByCategoryId(id).size();

        if (!children.isEmpty() || contentCount > 0) {
            if ("PREVENT".equals(handleChildren) || "PREVENT".equals(handleContent)) {
                throw new IllegalStateException("Cannot delete category with children or content");
            }

            // Handle children
            if ("DELETE".equals(handleChildren)) {
                children.forEach(child -> deleteCategory(child.getId(), "DELETE", handleContent));
            } else if ("REASSIGN_TO_PARENT".equals(handleChildren)) {
                Category parent = category.getParent();
                children.forEach(child -> {
                    child.setParent(parent);
                    categoryTreeService.updateCategoryPath(child);
                    categoryRepository.save(child);
                });
            }
        }

        categoryRepository.deleteById(id);
    }

    public boolean hasChildren(Long categoryId) {
        return !categoryRepository.findByParentId(categoryId).isEmpty();
    }

    public long getContentCount(Long categoryId) {
        return contentRepository.findByCategoryId(categoryId).size();
    }
}
