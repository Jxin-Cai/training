package com.cms.application.service;

import com.cms.application.dto.CategoryDto;
import com.cms.application.dto.CreateCategoryRequest;
import com.cms.application.dto.UpdateCategoryRequest;
import com.cms.domain.model.Category;
import com.cms.domain.repository.CategoryRepository;
import org.springframework.stereotype.Service;

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
        Category category = Category.create(request.name(), request.description());
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
    
    public Optional<CategoryDto> update(String id, UpdateCategoryRequest request) {
        return categoryRepository.findById(id).map(category -> {
            if (request.name() != null) {
                category.rename(request.name());
            }
            if (request.description() != null) {
                category.updateDescription(request.description());
            }
            Category updated = categoryRepository.save(category);
            return toDto(updated);
        });
    }
    
    public boolean delete(String id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private CategoryDto toDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
