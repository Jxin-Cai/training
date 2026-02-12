package com.cms.application.service;

import com.cms.application.dto.CategoryDTO;
import com.cms.domain.model.category.Category;
import com.cms.domain.repository.CategoryRepository;
import com.cms.domain.repository.ContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类应用服务
 * 协调领域对象完成业务用例
 * 
 * @author jxin
 * @date 2026-02-11
 */
@Service
@Transactional
public class CategoryApplicationService {
    
    private final CategoryRepository categoryRepository;
    private final ContentRepository contentRepository;
    
    public CategoryApplicationService(CategoryRepository categoryRepository,
                                     ContentRepository contentRepository) {
        this.categoryRepository = categoryRepository;
        this.contentRepository = contentRepository;
    }
    
    /**
     * 创建分类
     */
    public CategoryDTO createCategory(CategoryDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("分类名称不能为空");
        }
        
        Category category = Category.create(dto.getName(), dto.getDescription());
        Category saved = categoryRepository.save(category);
        return CategoryDTO.fromDomain(saved);
    }
    
    /**
     * 更新分类
     */
    public CategoryDTO updateCategory(Long id, CategoryDTO dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在: " + id));
        
        category.updateInfo(dto.getName(), dto.getDescription());
        Category updated = categoryRepository.save(category);
        return CategoryDTO.fromDomain(updated);
    }
    
    /**
     * 删除分类
     */
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("分类不存在: " + id);
        }
        
        // 检查是否有关联内容
        long contentCount = contentRepository.countByCategoryId(id);
        if (contentCount > 0) {
            throw new IllegalStateException("该分类下存在内容，无法删除");
        }
        
        categoryRepository.deleteById(id);
    }
    
    /**
     * 查询单个分类
     */
    @Transactional(readOnly = true)
    public CategoryDTO getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("分类不存在: " + id));
        return CategoryDTO.fromDomain(category);
    }
    
    /**
     * 查询所有分类
     */
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryDTO::fromDomain)
                .collect(Collectors.toList());
    }
}
