package com.cms.application.service;

import com.cms.application.dto.*;
import com.cms.domain.model.Category;
import com.cms.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    public CategoryDto create(CreateCategoryRequest request) {
        Category category = Category.create(
            request.name(), 
            request.description(), 
            request.parentId()
        );
        
        if (request.parentId() != null) {
            Category parent = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent category not found: " + request.parentId()));
            category.updatePathInfo(
                parent.getLevel() + 1,
                parent.getPath() + "/" + category.getId()
            );
        }
        
        Category saved = categoryRepository.save(category);
        return toDto(saved);
    }
    
    public Optional<CategoryDto> findById(String id) {
        return categoryRepository.findById(id).map(this::toDto);
    }
    
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public List<CategoryDto> findFlat() {
        return categoryRepository.findAll().stream()
                .sorted((a, b) -> {
                    int levelCompare = Integer.compare(a.getLevel(), b.getLevel());
                    if (levelCompare != 0) return levelCompare;
                    return Integer.compare(a.getSortOrder(), b.getSortOrder());
                })
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    public List<CategoryTreeDto> findTree() {
        List<Category> roots = categoryRepository.findRootCategories();
        return roots.stream()
                .map(this::buildTree)
                .collect(Collectors.toList());
    }
    
    private CategoryTreeDto buildTree(Category category) {
        List<Category> children = categoryRepository.findByParentId(category.getId());
        List<CategoryTreeDto> childDtos = children.stream()
                .map(this::buildTree)
                .collect(Collectors.toList());
        
        return new CategoryTreeDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getParentId(),
                category.getLevel(),
                category.getSortOrder(),
                category.getCreatedAt(),
                category.getUpdatedAt(),
                childDtos
        );
    }
    
    public Optional<CategoryDto> update(String id, UpdateCategoryRequest request) {
        return categoryRepository.findById(id).map(category -> {
            if (request.name() != null) {
                category.rename(request.name());
            }
            if (request.description() != null) {
                category.updateDescription(request.description());
            }
            if (request.sortOrder() != null) {
                category.setSortOrder(request.sortOrder());
            }
            if (request.parentId() != null && !request.parentId().equals(category.getParentId())) {
                moveCategory(category, request.parentId());
            } else if (request.parentId() == null && category.getParentId() != null) {
                moveCategoryToRoot(category);
            }
            
            Category updated = categoryRepository.save(category);
            return toDto(updated);
        });
    }
    
    private void moveCategory(Category category, String newParentId) {
        Category newParent = categoryRepository.findById(newParentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent category not found: " + newParentId));
        
        if (newParent.getPath().startsWith(category.getPath())) {
            throw new IllegalArgumentException("Cannot move category to its own descendant");
        }
        
        String oldPath = category.getPath();
        int oldLevel = category.getLevel();
        
        category.moveTo(
            newParentId,
            newParent.getLevel() + 1,
            newParent.getPath() + "/" + category.getId()
        );
        
        updateDescendantPaths(category, oldPath, oldLevel);
    }
    
    private void moveCategoryToRoot(Category category) {
        String oldPath = category.getPath();
        int oldLevel = category.getLevel();
        
        category.moveTo(null, 0, "/" + category.getId());
        
        updateDescendantPaths(category, oldPath, oldLevel);
    }
    
    private void updateDescendantPaths(Category movedCategory, String oldPath, int oldLevel) {
        List<Category> descendants = categoryRepository.findDescendants(movedCategory.getId());
        int levelDiff = movedCategory.getLevel() - oldLevel;
        
        for (Category descendant : descendants) {
            String newPath = descendant.getPath().replace(oldPath, movedCategory.getPath());
            descendant.updatePathInfo(descendant.getLevel() + levelDiff, newPath);
            categoryRepository.save(descendant);
        }
    }
    
    public boolean delete(String id) {
        if (!categoryRepository.existsById(id)) {
            return false;
        }
        
        if (categoryRepository.hasChildren(id)) {
            throw new IllegalStateException("Cannot delete category with children");
        }
        
        categoryRepository.deleteById(id);
        return true;
    }
    
    public List<String> getDescendantIds(String categoryId) {
        List<String> ids = new ArrayList<>();
        ids.add(categoryId);
        
        List<Category> descendants = categoryRepository.findDescendants(categoryId);
        for (Category descendant : descendants) {
            ids.add(descendant.getId());
        }
        
        return ids;
    }
    
    private CategoryDto toDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getParentId(),
                category.getLevel(),
                category.getPath(),
                category.getSortOrder(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
