package com.java.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.java.domain.entity.Category;
import com.java.domain.repository.ArticleRepository;
import com.java.domain.repository.CategoryRepository;
import com.java.presentation.dto.CategoryDTO;
import com.java.presentation.dto.CategoryRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 分类服务
 * 
 * 处理分类相关的业务逻辑。
 * 
 * @author Jxin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;
    
    /**
     * 获取所有分类
     */
    public List<CategoryDTO> getAllCategories() {
        log.info("获取所有分类");
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 根据ID获取分类
     */
    public CategoryDTO getCategoryById(Long id) {
        log.info("获取分类: {}", id);
        return categoryRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
    }
    
    /**
     * 创建分类
     */
    public CategoryDTO createCategory(CategoryRequest request) {
        log.info("创建分类: {}", request.getName());
        
        // 检查名称是否已存在
        if (categoryRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("分类名称已存在");
        }
        
        Category category = new Category(request.getName(), request.getDescription());
        Category saved = categoryRepository.save(category);
        
        log.info("分类创建成功: {}", saved.getId());
        return convertToDTO(saved);
    }
    
    /**
     * 更新分类
     */
    public CategoryDTO updateCategory(Long id, CategoryRequest request) {
        log.info("更新分类: {}", id);
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在"));
        
        // 检查名称是否与其他分类重复
        categoryRepository.findByName(request.getName())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new IllegalArgumentException("分类名称已存在");
                    }
                });
        
        category.update(request.getName(), request.getDescription());
        Category updated = categoryRepository.save(category);
        
        log.info("分类更新成功: {}", id);
        return convertToDTO(updated);
    }
    
    /**
     * 删除分类
     */
    public void deleteCategory(Long id) {
        log.info("删除分类: {}", id);
        
        // 检查分类是否存在
        if (!categoryRepository.findById(id).isPresent()) {
            throw new IllegalArgumentException("分类不存在");
        }
        
        // 检查分类下是否有文章
        long articleCount = articleRepository.countByCategoryId(id);
        if (articleCount > 0) {
            throw new IllegalArgumentException("该分类下有文章，无法删除");
        }
        
        categoryRepository.deleteById(id);
        log.info("分类删除成功: {}", id);
    }
    
    /**
     * 获取分类下的文章数量
     */
    public long getArticleCount(Long categoryId) {
        return articleRepository.countByCategoryId(categoryId);
    }
    
    /**
     * 转换为DTO
     */
    private CategoryDTO convertToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .build();
    }
}
